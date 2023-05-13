package org.example.ReactiveProgramming.CommandLine;

import org.example.ReactiveProgramming.ReactiveProgrammingSourceAnalyser;
import org.example.Utilities.*;

import static java.lang.Thread.sleep;

public class Test {

    public static void main(String[] args) throws InterruptedException {
        String directory = args[0];
        int longestFiles = Integer.parseInt(args[1]);
        int numberOfRanges = Integer.parseInt(args[2]);
        int maxLines = Integer.parseInt(args[3]);
        var future = new ReactiveProgrammingSourceAnalyser().getReport(
                directory, longestFiles, numberOfRanges, maxLines);

        future.thenAccept(Report::getResults);
        //simulate doing other code
        sleep(50000);


    }
}
