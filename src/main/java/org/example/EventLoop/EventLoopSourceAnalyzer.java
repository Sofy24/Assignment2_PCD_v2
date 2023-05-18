package org.example.EventLoop;


import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import org.example.EventLoop.GUI.AnalyzeVerticle;
import org.example.Utilities.*;
import org.example.Utilities.GUI.Flag;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

import static org.example.Utilities.ComputeFile.computeFile;

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

        //file processing
        vertx.eventBus().consumer("file", message -> {
            // Handle the received event
            String file = ((String) message.body());
            ComputedFile computedFile = computeFile(file, ranges);
            if (computedFile != null) {
                computedFiles.add(computedFile);
            }
            processedCount.getAndIncrement();
            //if all message published are executed complete the promise
            if (processedCount.get() == totalEvents.get()) {
                reportPromise.complete(new Report(computedFiles, ranges, longestFiles));
                vertx.close();
            }
        });

        vertx.eventBus().consumer("dir", message -> {
            // find all subdirectory and file in a directory
            String dir = (String)message.body();
            Set<File> dirs = FileSearcher.getSubDirectory(dir);
            if (dirs != null && !dirs.isEmpty()) {
                dirs.forEach(d -> {
                    totalEvents.incrementAndGet();
                    //send a message for every subdirectory
                    vertx.eventBus().publish("dir", d.getAbsolutePath());
                });
            }

            Set<String> files = FileSearcher.getJavaSourceFiles(dir);
            if (files != null && !files.isEmpty()) {
                files.forEach(file -> {
                    totalEvents.incrementAndGet();
                    //send a message for every file
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
        //start the message exchange
        vertx.eventBus().publish("dir", directory);
        return reportPromise.future();
    }

    @Override
    public Future<Void> analyzeSources(String directory, Flag stopFlag, Monitor monitor, List<LongRange> ranges) {
        Vertx vertx = Vertx.vertx();
        Promise<Void> completionPromise = Promise.promise();
        CompletableFuture.supplyAsync(() -> {
            List<FilePath> files;
            //take the unprocessed files
            if (monitor.getComputedFileList().isEmpty()) {
                //first start
                files = FileSearcher.getAllFilesWithPaths(directory);
                monitor.addUnprocessedFiles(files);
            } else {
                //restart
                monitor.removeAllFileComputed();
                files = new ArrayList<>(monitor.getUnprocessedFiles());
            }
            if (files != null) {
                //divide the files in batches
                int numBatches = (int) Math.ceil((double) files.size() / BATCH_SIZE);
                AtomicInteger completed = new AtomicInteger(numBatches);
                //create a consumer for Verticle completion
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
                    //create a Verticle for every batch
                    vertx.deployVerticle(new AnalyzeVerticle("analyze" + i, stopFlag, ranges, monitor),
                            deploymentResult -> {
                                //if Verticle deployed send to it the range of file to process
                                if (deploymentResult.succeeded()) {
                                    //istead of sending the list of file to process, send the range
                                    vertx.eventBus()
                                            .publish("compute" + finalI, startIndex + "-" + endIndex);
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
