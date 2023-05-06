package org.example.VirtualThread;


import org.example.Utilities.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class FileService extends Thread{

    ExecutorService executorService;
    String directory;
    List<LongRange> ranges;

    public FileService(String directory, List<LongRange> ranges) {
        executorService = Executors.newVirtualThreadPerTaskExecutor();
        this.directory = directory;
        this.ranges = ranges;
    }

    public List<ComputedFile> compute() {
        List<FilePath> files = FileSearcher.getAllFilesWithPaths(directory);
        List<Future<ComputedFile>> futureComputedFiles = new ArrayList<>();
        List<ComputedFile> computedFiles = new ArrayList<>();
        if (files != null) {
            for (FilePath file : files) {
                futureComputedFiles.add(executorService.submit(new ComputeFileTask(file, ranges)));
            }
        }
        for (Future<ComputedFile> futureFile : futureComputedFiles ) {
            try {
                computedFiles.add(futureFile.get());
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        return computedFiles;
    }
}
