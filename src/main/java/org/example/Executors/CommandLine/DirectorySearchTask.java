package org.example.Executors.CommandLine;


import org.example.Utilities.ComputedFile;
import org.example.Utilities.FilePath;
import org.example.Utilities.LongRange;

import java.io.File;
import java.util.*;
import java.util.concurrent.RecursiveTask;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class DirectorySearchTask extends RecursiveTask<List<ComputedFile>> {

    private final String directory;
    private final List<LongRange> ranges;

    //search all the sub directory in a directory
    public DirectorySearchTask(String directory, List<LongRange> ranges) {
        super();
        this.directory = directory;
        this.ranges = ranges;
    }

    private Set<String> getSubDirectory() {
        try {
            return Stream.of(Objects.requireNonNull(new File(directory).listFiles()))
                    .filter(File::isDirectory)
                    .map(File::getAbsolutePath)
                    .collect(Collectors.toSet());
        } catch (NullPointerException e) {
            return null;
        }
    }

    private static Set<String> getJavaSourceFiles(String dir) {
        try {
            return Stream.of(Objects.requireNonNull(new File(dir).listFiles()))
                    .filter(file -> !file.isDirectory())
                    .map(File::getName)
                    .filter(name -> name.endsWith(".java"))
                    .collect(Collectors.toSet());
        } catch (NullPointerException e) {
            return null;
        }
    }


    @Override
    protected List<ComputedFile> compute() {
        List<ComputedFile> files = new ArrayList<>();
        List<RecursiveTask<List<ComputedFile>>> dirForks = new LinkedList<>();
        Set<String> subDirectories = getSubDirectory();
        //create a new directory search task for each new directory found in the current directory
        if (subDirectories != null) {
            for (String subDirectory : subDirectories) {
                DirectorySearchTask task = new DirectorySearchTask(subDirectory, ranges);
                dirForks.add(task);
                task.fork();
            }
        }
        //create a new task for each file found
        List<RecursiveTask<ComputedFile>> fileForks = new LinkedList<>();
        Set<String> dirFiles = getJavaSourceFiles(directory);
        if ( dirFiles != null) {
            for (String file: dirFiles) {
                ComputeFileTask task = new ComputeFileTask(new FilePath(directory, file), ranges);
                fileForks.add(task);
                task.fork();
            }
        }
        //combine the result of the tasks
        for (RecursiveTask<List<ComputedFile>> task : dirForks) {
            files.addAll(task.join());
        }
        for (RecursiveTask<ComputedFile> task : fileForks) {
            files.add(task.join());
        }
        return files;
    }
}
