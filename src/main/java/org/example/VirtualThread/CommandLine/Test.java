package org.example.VirtualThread.CommandLine;

import org.example.Utilities.Report;
import org.example.VirtualThread.VTSourceAnalyser;

import java.util.concurrent.CompletableFuture;

import static java.lang.Thread.sleep;

public class Test {
    public static void main(String[] args) throws InterruptedException {
/*        String directory = args[0];
        int longestFiles = Integer.parseInt(args[1]);
        int numberOfRanges = Integer.parseInt(args[2]);
        int maxLines = Integer.parseInt(args[3]);*/
        long start = System.currentTimeMillis();
        String directory = "C:\\Users\\Sofia\\Documents\\Programmazione concorrente e distribuita (Ricci)\\Assignment01\\Assignment_01_PCD\\f1";
        int longestFiles = 6;
        int numberOfRanges = 5;
        int maxLines = 150;
        CompletableFuture<Report> futureReport = new VTSourceAnalyser().getReport(directory, longestFiles, numberOfRanges, maxLines);
        //s.getReport("C:\Users\seraf\OneDrive\Desktop\SSS\ASSIGNMENT1\f1", 5, 5, 200);
        //Report::getResults;
        futureReport.thenAccept(s -> {
            s.getResults();
            System.out.println("Tempo finale \n" + (System.currentTimeMillis() - start));
        });

        //simulate doing other stuff
        sleep(50000);


    }

}
