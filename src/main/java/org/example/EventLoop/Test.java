package org.example.EventLoop;


import io.vertx.core.Future;
import org.example.Utilities.Report;

import static java.lang.Thread.sleep;

public class Test {
    public static void main(String[] args) {
        /*for (int k = 0; k < 10; k++){
            System.out.println("k \n" + (k + 1));
            long start = System.currentTimeMillis();
            String directory = "C:\\Users\\Sofia\\Documents\\Programmazione concorrente e distribuita (Ricci)\\torre10000\\torre10000";
            int longestFiles = 6;
            int numberOfRanges = 5;
            int maxLines = 150;*/
        String directory = args[0];
        int longestFiles = Integer.parseInt(args[1]);
        int numberOfRanges = Integer.parseInt(args[2]);
        int maxLines = Integer.parseInt(args[3]);
        Future<Report> futureReport = new EventLoopSourceAnalyzer()
                .getReport(directory, longestFiles, numberOfRanges, maxLines);
        futureReport.onComplete(report ->  {report.result().getResults();});
        //simulate doing other stuff
        try {
            sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

}
