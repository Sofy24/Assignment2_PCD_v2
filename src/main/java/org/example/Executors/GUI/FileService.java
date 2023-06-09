package org.example.Executors.GUI;

import org.example.Utilities.GUI.Flag;
import org.example.Utilities.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class FileService extends Thread{

    private final String directory;

    private final List<LongRange> ranges;

    private final Monitor monitor;

    private final Flag blockFlag;

    private final ExecutorService executorService;

    public FileService(String directory, List<LongRange> ranges, Monitor monitor, Flag blockFlag) {
        this.directory = directory;
        this.ranges = ranges;
        this.monitor = monitor;
        this.blockFlag = blockFlag;
        executorService = Executors.newCachedThreadPool();

    }

    public Void compute() {
        List<FilePath> files;
        List<Future<?>> futureTask = new ArrayList<>();
        //get all the file or if it is a restart get only the unprocessed files.
        if (monitor.getUnprocessedFiles().isEmpty()) {
            files = FileSearcher.getAllFilesWithPaths(directory);
            monitor.addUnprocessedFiles(files);
        }
        else {
            files = new ArrayList<>(monitor.getUnprocessedFiles());
        }
        //for each file submit a task
        if (files != null) {
            files.forEach(file -> futureTask.add(
                    executorService.submit(new ComputeAndStoreFileTask(file, monitor, blockFlag, ranges))));
        }
        //for every submitted task wait for it to end (completion or interruption)
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
