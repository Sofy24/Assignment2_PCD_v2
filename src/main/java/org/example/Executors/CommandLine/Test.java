package org.example.Executors.CommandLine;

import org.example.Executors.ExecutorsSourceAnalyser;
import org.example.Utilities.Report;

import java.util.concurrent.CompletableFuture;

import static java.lang.Thread.sleep;

public class Test {

    public static void main(String[] args) throws InterruptedException {
        String directory = args[0];
        int longestFiles = Integer.parseInt(args[1]);
        int numberOfRanges = Integer.parseInt(args[2]);
        int maxLines = Integer.parseInt(args[3]);
        CompletableFuture<Report> futureReport = new ExecutorsSourceAnalyser()
                .getReport(directory, longestFiles, numberOfRanges, maxLines);
        futureReport.thenAccept(Report::getResults);
        //simulate doing other code
        sleep(5000);
    }
}
