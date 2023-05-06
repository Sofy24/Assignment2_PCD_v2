package org.example.Utilities;

import jdk.incubator.concurrent.StructuredTaskScope;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileSearcher {

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

    private static Set<File> getSubDirectory(String dir) {
        try {
            return Stream.of(Objects.requireNonNull(new File(dir).listFiles()))
                    .filter(File::isDirectory)
                    .collect(Collectors.toSet());
        } catch (NullPointerException e) {
            return null;
        }
    }

    public static List<FilePath> getAllFilesWithPaths(String dir) {

        List<FilePath> files = new ArrayList<>();
        try {
            Objects.requireNonNull(getJavaSourceFiles(dir)).forEach(file -> files.add(new FilePath(dir, file)));
            Objects.requireNonNull(getSubDirectory(dir)).forEach(directory ->
                    files.addAll(Objects.requireNonNull(getAllFilesWithPaths(directory.getAbsolutePath()))));
            return files;
        } catch (NullPointerException e){
            return null;
        }
    }

    public static List<FilePath> getAllFilesWithPathsVT(String dir) {
        List<FilePath> files = new ArrayList<>();
        try {
            Objects.requireNonNull(getJavaSourceFiles(dir)).forEach(file -> files.add(new FilePath(dir, file)));
            List<Thread> list = new ArrayList<>();
            Set<File> subDir = getSubDirectory(dir);
            Objects.requireNonNull(subDir).forEach(directory -> {
                    try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
                        Future<List<FilePath>> dirFiles = scope.fork(() -> getAllFilesWithPathsVT(directory.getAbsolutePath()));
                        scope.join(); // Join both forks
                        scope.throwIfFailed(); // ... and propagate errors
                        // Here, both forks have succeeded, so compose their results
                        files.addAll(dirFiles.resultNow());
                    } catch (ExecutionException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
            });
            return files;
        } catch (NullPointerException e){
            return null;
        }
    }

}
