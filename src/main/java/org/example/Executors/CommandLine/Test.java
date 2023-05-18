package org.example.Executors.CommandLine;

import org.example.Executors.ExecutorsSourceAnalyser;
import org.example.Utilities.Report;

import java.util.concurrent.CompletableFuture;

public class Test {

    public static void main(String[] args) {
        String directory = args[0];
        int longestFiles = Integer.parseInt(args[1]);
        int numberOfRanges = Integer.parseInt(args[2]);
        int maxLines = Integer.parseInt(args[3]);
        CompletableFuture<Report> futureReport = new ExecutorsSourceAnalyser()
                .getReport(directory, longestFiles, numberOfRanges, maxLines);
        futureReport.join();
        futureReport.thenAccept(Report::getResults);
    }
}
