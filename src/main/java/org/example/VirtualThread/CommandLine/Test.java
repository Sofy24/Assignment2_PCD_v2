package org.example.VirtualThread.CommandLine;

import org.example.Utilities.Report;
import org.example.VirtualThread.VTSourceAnalyser;

import java.util.concurrent.CompletableFuture;

import static java.lang.Thread.sleep;

public class Test {
    public static void main(String[] args) throws InterruptedException {
        String directory = args[0];
        int longestFiles = Integer.parseInt(args[1]);
        int numberOfRanges = Integer.parseInt(args[2]);
        int maxLines = Integer.parseInt(args[3]);

        CompletableFuture<Report> futureReport = new VTSourceAnalyser().getReport(
                directory, longestFiles, numberOfRanges, maxLines);
        futureReport.join();
        futureReport.thenAccept(Report::getResults);
    }

}
