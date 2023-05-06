package org.example.VirtualThread;

import org.example.Utilities.ComputedFile;
import org.example.Utilities.CreateRange;
import org.example.Utilities.LongRange;
import org.example.Utilities.Report;

import java.util.List;
import java.util.concurrent.*;

public class VTSourceAnalyser implements SourceAnalyser {

    @Override
    public CompletableFuture<Report> getReport(String directory, int longestFiles, int numberOfRanges, int maxLines) {
        List<LongRange> ranges = CreateRange.generateRanges(maxLines, numberOfRanges);
        return CompletableFuture.supplyAsync(() ->
                new Report(new FileService(directory, ranges).compute(), ranges, longestFiles));
    }

    @Override
    public void analyzeSources(String d) {

    }
}
