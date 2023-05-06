package org.example.Executors;


import org.example.Utilities.LongRange;
import org.example.Utilities.Report;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;

public class ExecutorsSourceAnalyser implements SourceAnalyser{
    @Override
    public CompletableFuture<Report> getReport(String directory, int longestFiles, int numberOfRanges, int maxLines) {
        List<LongRange> ranges = new ArrayList<>();
        final int rangeSize = maxLines / (numberOfRanges - 1);
        for (long i = 0; i < numberOfRanges; i++) {
            ranges.add(new LongRange(
                    rangeSize * i, i != (numberOfRanges - 1) ? rangeSize * (i + 1) - 1 : Long.MAX_VALUE));
        }
        try {
            return CompletableFuture.supplyAsync(() ->
                    new Report(new ForkJoinPool().invoke(new DirectorySearchTask(directory, ranges)), ranges, longestFiles));

        } catch (Exception e){
            return null;
        }
    }

    @Override
    public void analyzeSources(String d) {
    }
}
