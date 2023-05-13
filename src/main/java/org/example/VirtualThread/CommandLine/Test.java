package org.example.VirtualThread.CommandLine;

import org.example.Utilities.Report;
import org.example.VirtualThread.VTSourceAnalyser;

import java.util.concurrent.CompletableFuture;

import static java.lang.Thread.sleep;

public class Test {

    private final static int N_WORKERS = 12;
    public static void main(String[] args) throws InterruptedException {
        String directory = args[0];
        int longestFiles = Integer.parseInt(args[1]);
        int numberOfRanges = Integer.parseInt(args[2]);
        int maxLines = Integer.parseInt(args[3]);
        CompletableFuture<Report> futureReport = new VTSourceAnalyser().getReport(directory, longestFiles, numberOfRanges, maxLines);
        //s.getReport("C:\Users\seraf\OneDrive\Desktop\SSS\ASSIGNMENT1\f1", 5, 5, 200);
        futureReport.thenAccept(Report::getResults);
        System.out.println("BANANA");
        //simulate doing other stuff
        sleep(50000);


        /*try {
            String dir = args[0];//"C:\\Users\\seraf\\OneDrive\\Desktop\\file50"; //D
            int ranges = Integer.parseInt(args[1]); //NI
            int maxLines = Integer.parseInt(args[2]); //MAXL
            int numberOfLongestFiles = Integer.parseInt(args[3]); //N
            new VTSourceAnalyser().getReport(dir, numberOfLongestFiles, ranges, maxLines);
            List<FilePath> files = FileSearcher.getAllFilesWithPaths(dir);
            if (files != null) {
                Monitor monitor = new Monitor(ranges, maxLines, numberOfLongestFiles);
                Master master = new Master(monitor, N_WORKERS, files, true);
                master.start();
            } else {
                System.out.println("Wrong directory");
            }
        } catch (NumberFormatException e){
            System.out.println("Wrong parameters");
        }*/





        /*
        var t0 = System.currentTimeMillis();
        FileSearcher.getAllFilesWithPaths("C:\\Users\\seraf\\OneDrive\\Desktop\\SSS\\ASSIGNMENT1\\f1").size();
        var t1 = System.currentTimeMillis();
        System.out.println("Time elapsed: " + (t1 - t0));
        t0 = System.currentTimeMillis();
        FileSearcher.getAllFilesWithPathsVT("C:\\Users\\seraf\\OneDrive\\Desktop\\SSS\\ASSIGNMENT1\\f1").size();
        t1 = System.currentTimeMillis();
        System.out.println("Time elapsed: " + (t1 - t0));/*

        /*

        var t0 = System.currentTimeMillis();
        var list = new ArrayList<Thread>();
        for (var i = 0; i < 1_000; i++) {
            Thread t = Thread.ofVirtual().unstarted(() -> {
                try {
                    Thread.sleep(Duration.ofSeconds(1));
                } catch (Exception ignored) {
                }
            });
            t.start();
            list.add(t);
        }

        list.forEach(t -> {
            try {
                t.join();
            } catch (Exception ex) {};
        });
        var t1 = System.currentTimeMillis();
        System.out.println("Time elapsed: " + (t1 - t0));*/

    }

}
