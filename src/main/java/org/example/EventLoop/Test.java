package org.example.EventLoop;


import io.vertx.core.Future;
import org.example.Utilities.Report;

public class Test {
    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        String directory = "C:\\Users\\Sofia\\Documents\\Programmazione concorrente e distribuita (Ricci)\\Assignment01\\Assignment_01_PCD\\f1";
        int longestFiles = 6;
        int numberOfRanges = 5;
        int maxLines = 150;
        /*String directory = args[0];
        int longestFiles = Integer.parseInt(args[1]);
        int numberOfRanges = Integer.parseInt(args[2]);
        int maxLines = Integer.parseInt(args[3]);*/
        Future<Report> futureReport = new EventLoopSourceAnalyzer()
                .getReport(directory, longestFiles, numberOfRanges, maxLines);
        futureReport.onComplete(report ->  {report.result().getResults(); System.out.println("Tempo finale \n" + (System.currentTimeMillis() - start));});
    }
}
