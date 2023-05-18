package org.example.VirtualThread;

import org.example.Utilities.Monitor;
import org.example.Utilities.GUI.Flag;
import org.example.Utilities.ComputedFile;
import org.example.Utilities.CreateRange;
import org.example.Utilities.LongRange;
import org.example.Utilities.Report;
import org.example.VirtualThread.GUI.FileService;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public class VTSourceAnalyser implements SourceAnalyser {

    @Override
    public CompletableFuture<Report> getReport(String directory, int longestFiles, int numberOfRanges, int maxLines) {
        List<LongRange> ranges = CreateRange.generateRanges(maxLines, numberOfRanges);
        return CompletableFuture.supplyAsync(() ->
                new Report(new org.example.VirtualThread.CommandLine.FileService(
                        directory, ranges).compute(), ranges, longestFiles));
    }

    @Override
    public CompletableFuture<List<Future<ComputedFile>>> analyzeSources(
            String directory, int longestFiles, int numberOfRanges, int maxLines, Monitor monitor, Flag blockFlag) {
        List<LongRange> ranges = CreateRange.generateRanges(maxLines, numberOfRanges);
        return CompletableFuture.supplyAsync(() ->  new FileService(
                directory, ranges, monitor, blockFlag).compute());

    }
}
