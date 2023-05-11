package org.example.Executors;

import org.example.Flag;
import org.example.Utilities.ComputedFile;
import org.example.Utilities.FilePath;
import org.example.Utilities.FileSearcher;
import org.example.Utilities.LongRange;
import org.example.ViewerAgent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class FileService extends Thread{

    private String directory;

    private List<LongRange> ranges;

    private int longestFiles;

    private Monitor monitor;

    private Flag blockFlag;

    private ExecutorService executorService;

    public FileService(String directory, List<LongRange> ranges, int longestFiles, Monitor monitor, Flag blockFlag) {
        this.directory = directory;
        this.ranges = ranges;
        this.longestFiles = longestFiles;
        this.monitor = monitor;
        this.blockFlag = blockFlag;
        executorService = Executors.newCachedThreadPool();

    }

    public Void compute() {
        List<FilePath> files;
        List<Future<?>> futureTask = new ArrayList<>();
        if (monitor.getUnprocessedFiles().isEmpty()) {
            files = FileSearcher.getAllFilesWithPaths(directory);
            monitor.addUnprocessedFiles(files);
        }
        else {
            files = monitor.getUnprocessedFiles();
        }
        if (files != null) {
            files.forEach(file -> futureTask.add(
                    executorService.submit(new ComputeAndStoreFileTask(file, monitor, blockFlag, ranges))));
        }
        for (Future<?> future : futureTask ) {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }



}
