package org.example.Executors;


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
        if (subDirectories != null) {
            for (String subDirectory : subDirectories) {
                DirectorySearchTask task = new DirectorySearchTask(subDirectory, ranges);
                dirForks.add(task);
                task.fork();
            }
        }

        List<RecursiveTask<ComputedFile>> fileForks = new LinkedList<>();
        Set<String> dirFiles = getJavaSourceFiles(directory);
        if ( dirFiles != null) {
            for (String file: dirFiles) {
                ComputeFileTask task = new ComputeFileTask(new FilePath(directory, file), ranges);
                fileForks.add(task);
                task.fork();
            }
        }

        for (RecursiveTask<List<ComputedFile>> task : dirForks) {
            files.addAll(task.join());
        }
        for (RecursiveTask<ComputedFile> task : fileForks) {
            files.add(task.join());
        }
        return files;
    }
}
