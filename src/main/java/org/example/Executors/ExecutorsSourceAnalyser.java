package org.example.Executors;


import io.vertx.core.Future;
import org.example.Utilities.CreateRange;
import org.example.Utilities.LongRange;
import org.example.Utilities.Report;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;

public class ExecutorsSourceAnalyser implements SourceAnalyser{
    @Override
    public CompletableFuture<Report> getReport(String directory, int longestFiles, int numberOfRanges, int maxLines) {
        List<LongRange> ranges = CreateRange.generateRanges(maxLines, numberOfRanges);
        try {
            return CompletableFuture.supplyAsync(() ->
                    new Report(new ForkJoinPool().invoke(new DirectorySearchTask(directory, ranges)), ranges, longestFiles));

        } catch (Exception e){
            return null;
        }
    }

    @Override
    public void analyzeSources(String directory, int longestFiles, int numberOfRanges, int maxLines) {

    }
}
