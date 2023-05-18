package org.example.ReactiveProgramming.CommandLine;

import org.example.ReactiveProgramming.ReactiveProgrammingSourceAnalyser;
import org.example.Utilities.Report;

public class Test {

    public static void main(String[] args) {
        String directory = args[0];
        int longestFiles = Integer.parseInt(args[1]);
        int numberOfRanges = Integer.parseInt(args[2]);
        int maxLines = Integer.parseInt(args[3]);
        var future = new ReactiveProgrammingSourceAnalyser().getReport(
                directory, longestFiles, numberOfRanges, maxLines);
        future.join();
        future.thenAccept(Report::getResults);
    }
}
