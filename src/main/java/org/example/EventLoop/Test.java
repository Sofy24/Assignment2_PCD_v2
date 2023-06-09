package org.example.EventLoop;


import io.vertx.core.Future;
import org.example.Utilities.Report;

import static java.lang.Thread.sleep;

public class Test {
    public static void main(String[] args) {
        String directory = args[0];
        int longestFiles = Integer.parseInt(args[1]);
        int numberOfRanges = Integer.parseInt(args[2]);
        int maxLines = Integer.parseInt(args[3]);
        Future<Report> futureReport = new EventLoopSourceAnalyzer()
                .getReport(directory, longestFiles, numberOfRanges, maxLines);
        futureReport.onComplete(report -> report.result().getResults());
        //simulate doing other code
        try {
            sleep(50000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

}
