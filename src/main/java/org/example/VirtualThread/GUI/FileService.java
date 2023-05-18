package org.example.VirtualThread.GUI;


import org.example.Utilities.Monitor;
import org.example.Utilities.GUI.Flag;
import org.example.Utilities.ComputedFile;
import org.example.Utilities.FilePath;
import org.example.Utilities.FileSearcher;
import org.example.Utilities.LongRange;

import java.util.ArrayList;
import java.util.List;
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
        executorService = Executors.newVirtualThreadPerTaskExecutor();
    }

    public List<Future<ComputedFile>> compute() {
        List<FilePath> files;
        List<Future<ComputedFile>> futureTasks = new ArrayList<>();
        //handle the file to compute
        if (monitor.getUnprocessedFiles().isEmpty()) {
            files = FileSearcher.getAllFilesWithPaths(directory);
            monitor.addUnprocessedFiles(files);
        }
        else {
            files = new ArrayList<>(monitor.getUnprocessedFiles());
        }
        //for each file create a task to compute the file
        if (files != null) {
            for (FilePath file : files) {
                futureTasks.add(executorService.submit(new ComputeFileTask(file, blockFlag, ranges)));
            }
        }
        return futureTasks;
    }
}
