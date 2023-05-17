package org.example.EventLoop;


import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import org.example.EventLoop.sera.AnalyzeVerticle;
import org.example.Utilities.*;
import org.example.Utilities.GUI.Flag;
import org.example.Utilities.GUI.View;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

public class EventLoopSourceAnalyzer implements SourceAnalyser {

    private final static int BATCH_SIZE = 5000;

    @Override
    public Future<Report> getReport(String directory, int longestFiles, int numberOfRanges, int maxLines) {
        List<LongRange> ranges = CreateRange.generateRanges(maxLines, numberOfRanges);
        Vertx vertx = Vertx.vertx();
        AtomicInteger totalEvents = new AtomicInteger();
        AtomicInteger processedCount = new AtomicInteger();
        Promise<Report> reportPromise = Promise.promise();
        List<ComputedFile> computedFiles = new ArrayList<>();

        vertx.eventBus().consumer("file", message -> {
            // Handle the received event
            String file = ((String) message.body());
            long len = 0L;
            try {
                len = Files.lines(Paths.get(file), StandardCharsets.UTF_8).count();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            for (LongRange range: ranges) {
                if (range.isInRange(len)) {
                    computedFiles.add(new ComputedFile(new FilePath(file), range.getMin(), len));
                }
            }

            processedCount.getAndIncrement();
            if (processedCount.get() == totalEvents.get()) {
                reportPromise.complete(new Report(computedFiles, ranges, longestFiles));
                vertx.close();
            }
        });

        vertx.eventBus().consumer("dir", message -> {
            // Handle the received event
            String dir = (String)message.body();
            Set<File> dirs = FileSearcher.getSubDirectory(dir);
            if (dirs != null && !dirs.isEmpty()) {
                dirs.forEach(d -> {
                    totalEvents.incrementAndGet();
                    vertx.eventBus().publish("dir", d.getAbsolutePath());
                });
            }

            Set<String> files = FileSearcher.getJavaSourceFiles(dir);
            if (files != null && !files.isEmpty()) {
                files.forEach(file -> {
                    totalEvents.incrementAndGet();
                    vertx.eventBus().publish("file", new FilePath(dir, file).getCompleteFilePath());
                });
            }
            processedCount.incrementAndGet();
            if (processedCount.get() == totalEvents.get()) {//only if there is an empty directory at the end
                reportPromise.complete(new Report(computedFiles, ranges, longestFiles));
                vertx.close();
            }
        });

        totalEvents.incrementAndGet();
        vertx.eventBus().publish("dir", directory);
        return reportPromise.future();
    }

    @Override
    public Future<Void> analyzeSources(String directory, Flag stopFlag, Monitor monitor, List<LongRange> ranges) {
        Vertx vertx = Vertx.vertx();
        Promise<Void> completionPromise = Promise.promise();
        CompletableFuture.supplyAsync(() -> {
            List<FilePath> files;
            if (monitor.getComputedFileList().isEmpty()) {
                files = FileSearcher.getAllFilesWithPaths(directory);
                monitor.addUnprocessedFiles(files);
            } else {
                monitor.removeAllFileComputed();
                files = new ArrayList<>(monitor.getUnprocessedFiles());
            }
            if (files != null) {
                int numBatches = (int) Math.ceil((double) files.size() / BATCH_SIZE);
                AtomicInteger completed = new AtomicInteger(numBatches);
                vertx.eventBus().consumer("completed", (Handler<Message<String>>) message -> {
                    completed.getAndDecrement();
                    if (completed.get() == 0) {
                        completionPromise.complete();
                    }

                });

                for (int i = 0; i < numBatches; i++) {
                    int startIndex = i * BATCH_SIZE;
                    int endIndex = Math.min(startIndex + BATCH_SIZE, files.size());
                    int finalI = i;
                    vertx.deployVerticle(new AnalyzeVerticle("analyze" + i, stopFlag, ranges, monitor),
                            deploymentResult -> {
                                if (deploymentResult.succeeded()) {
                                    vertx.eventBus().publish("compute" + finalI, startIndex + "-" + endIndex);
                                } else {
                                    System.out.println("deployment failed: " + deploymentResult.cause());
                                }
                            });

                }
            }
            return null;
        });
        return completionPromise.future();
    }
}
