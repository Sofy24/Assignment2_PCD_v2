package org.example.Executors;


import org.example.Flag;
import org.example.Utilities.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;

public class ExecutorsSourceAnalyser implements SourceAnalyser{
    @Override
    public CompletableFuture<Report> getReport(String directory, int longestFiles, int numberOfRanges, int maxLines) {
        List<LongRange> ranges = CreateRange.generateRanges(maxLines, numberOfRanges);
        try {
            return CompletableFuture.supplyAsync(() ->
                    new Report(new ForkJoinPool().invoke(new DirectorySearchTask(directory, ranges)), ranges, longestFiles));

        } catch (Exception e){
            return null;
        }
    }

    @Override
    public CompletableFuture<?> analyzeSources(String directory, int longestFiles, int numberOfRanges, int maxLines,
                                               Monitor monitor, Flag blockFlag) {
        List<LongRange> ranges = CreateRange.generateRanges(maxLines, numberOfRanges);
        return CompletableFuture.supplyAsync(() ->  new FileService(directory, ranges, longestFiles, monitor, blockFlag).compute());
        /*
        analizeSoucres(dir, monitor, ranges, ranking, flag) {
    asynctask -> {
        if (monitor.getunprocessedfiles() == null) {
            files = getallFiles(dir)
            monitor.addUncomputedFiles(files)
        }
        else {
            files = monitor.getunprocessedfiles()
        }
        blocksOFFiles = files diviso in blocchi uguali
        for each block {
            executor.submit(new processblocktask(block, monitor, ranges, flag))
        }
    }
}

         */
    }
}
