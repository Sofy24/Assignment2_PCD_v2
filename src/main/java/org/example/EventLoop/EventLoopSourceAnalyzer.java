package org.example.EventLoop;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import org.example.Utilities.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

public class EventLoopSourceAnalyzer implements SourceAnalyser {
    @Override
    public CompletableFuture<Report> getReport(String directory, int longestFiles, int numberOfRanges, int maxLines) {
        List<LongRange> ranges = CreateRange.generateRanges(maxLines, numberOfRanges);
        return CompletableFuture.supplyAsync(() ->
                new Report(computeReport(directory, ranges).onComplete(t -> System.out.println("RUMAO")).result(), ranges, longestFiles));
    }

    @Override
    public void analyzeSources(String d) {

    }

    private Future<List<ComputedFile>> computeReport(String directory, List<LongRange> ranges) {
        Vertx vertx = Vertx.vertx();
        System.out.println("START");
        AtomicInteger totalEvents = new AtomicInteger(0);
        AtomicInteger processedCount = new AtomicInteger(0);
        Promise<List<ComputedFile>> completionPromise = Promise.promise();
        List<ComputedFile> computedFiles = new ArrayList<>();
        //String directory = "C:\\Users\\seraf\\OneDrive\\Desktop\\SSS\\ASSIGNMENT1\\f1";

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
                    computedFiles.add(new ComputedFile(new FilePath("a","b"), range.getMin(), len));
                }
            }

            processedCount.incrementAndGet();
            if (processedCount.getAcquire() == totalEvents.getAcquire()) {
                System.out.println("FINISHED");
                completionPromise.complete(computedFiles);
                vertx.close();
            }

        });

        vertx.eventBus().consumer("dir", message -> {
            // Handle the received event
            String dir = (String)message.body();
            System.out.println("DIR: "+ dir);

            Set<File> dirs = FileSearcher.getSubDirectory(dir);
            if (dirs != null) {
                dirs.forEach(d -> {
                    totalEvents.incrementAndGet();
                    vertx.eventBus().publish("dir", d.getAbsolutePath());
                });
            }

            Set<String> files = FileSearcher.getJavaSourceFiles(dir);
            System.out.println("FILES: " + files);
            if (files != null) {
                files.forEach(file -> {
                    totalEvents.incrementAndGet();
                    vertx.eventBus().publish("file", new FilePath(dir, file).getCompleteFilePath());
                });
            }
            processedCount.incrementAndGet();
        });

        totalEvents.incrementAndGet();
        vertx.eventBus().publish("dir", directory);
        return completionPromise.future();
    }


}
