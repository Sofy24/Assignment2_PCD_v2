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
import java.util.concurrent.atomic.AtomicInteger;

public class EventLoopSourceAnalyzer implements SourceAnalyser {
    @Override
    public Future<Report> getReport(String directory, int longestFiles, int numberOfRanges, int maxLines) {
        List<LongRange> ranges = CreateRange.generateRanges(maxLines, numberOfRanges);
        Vertx vertx = Vertx.vertx();
        AtomicInteger totalEvents = new AtomicInteger(0);
        AtomicInteger processedCount = new AtomicInteger(0);
        Promise<Report> reportPromise = Promise.promise();
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
                    computedFiles.add(new ComputedFile(new FilePath(file), range.getMin(), len));
                }
            }

            processedCount.incrementAndGet();
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
        });

        totalEvents.incrementAndGet();
        vertx.eventBus().publish("dir", directory);
        return reportPromise.future();
    }

    @Override
    public void analyzeSources(String d) {

    }
}
