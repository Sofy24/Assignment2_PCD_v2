package org.example.VirtualThread;



import org.example.Utilities.Report;

import java.util.concurrent.CompletableFuture;

public class VTSourceAnalyser implements SourceAnalyser {

    @Override
    public CompletableFuture<Report> getReport(String directory, int longestFiles, int numberOfRanges, int maxLines) {
        return null;
    }

    @Override
    public void analyzeSources(String d) {

    }
}
