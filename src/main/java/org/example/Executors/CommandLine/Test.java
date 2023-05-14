package org.example.Executors.CommandLine;

import org.example.Executors.ExecutorsSourceAnalyser;
import org.example.Utilities.Report;

import java.util.concurrent.CompletableFuture;

import static java.lang.Thread.sleep;

public class Test {

    public static void main(String[] args) throws InterruptedException {
        long start = System.currentTimeMillis();
        //System.out.println("Tempo iniziale \n" + System.currentTimeMillis());
        //String directory = args[0];
        //int longestFiles = Integer.parseInt(args[1]);
        //int numberOfRanges = Integer.parseInt(args[2]);
        //int maxLines = Integer.parseInt(args[3]);
        String directory = "C:\\Users\\Sofia\\Documents\\Programmazione concorrente e distribuita (Ricci)\\Assignment01\\Assignment_01_PCD\\f1";
        int longestFiles = 6;
        int numberOfRanges = 5;
        int maxLines = 150;
        CompletableFuture<Report> futureReport = new ExecutorsSourceAnalyser()
                .getReport(directory, longestFiles, numberOfRanges, maxLines);
        //            Report::getResults;
        futureReport.thenAccept(s -> {
            s.getResults();
            System.out.println("Tempo finale \n" + (System.currentTimeMillis() - start));
            });
        //simulate doing other code
        sleep(5000);

    }
}
