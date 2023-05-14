package org.example.ReactiveProgramming;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import org.example.Executors.CommandLine.DirectorySearchTask;
import org.example.Utilities.*;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.stream.Stream;

public class ReactiveProgrammingSourceAnalyser implements SourceAnalyser{

    private List<String> getAllSubDirectory(String directory, List<String> allDirs) {
        allDirs.add(directory);
        Set<File> subDirs = FileSearcher.getSubDirectory(directory);
        if (subDirs != null) {
            subDirs.stream()
                    .map(File::getAbsolutePath)
                    .forEach(dir -> getAllSubDirectory(dir, allDirs));
            return allDirs;
        }
        return null;
    }

    @Override
    public CompletableFuture<Report> getReport(String directory, int longestFiles, int numberOfRanges, int maxLines) {
        List<LongRange> ranges = CreateRange.generateRanges(maxLines, numberOfRanges);
        CompletableFuture<Report> future = new CompletableFuture<>();

        Flowable<ComputedFile> computedFileFlowable = Flowable.fromCallable(() ->
                        Objects.requireNonNull(getAllSubDirectory(directory, new ArrayList<>())).stream()
                                .map(dir -> {
                                    Set<String> files = FileSearcher.getJavaSourceFiles(dir);
                                    if (files != null) {
                                        return files.stream()
                                                .map(file -> dir + System.getProperty("file.separator") + file)
                                                .toList();
                                    }
                                    return null;
                                }).filter(Objects::nonNull)
                                .flatMap(List::stream)
                                .toList())
                .flatMapIterable(files -> files)
                .map(file -> {
                    Long fileLen = Files.lines(Paths.get(file), StandardCharsets.UTF_8).count();
                    for (LongRange range: ranges) {
                        if (range.isInRange(fileLen))
                            return new ComputedFile(new FilePath(file),
                                    range.getMin(),
                                    fileLen);
                    }
                    return null;
                }).subscribeOn(Schedulers.single());

        List<ComputedFile> computedFiles = new ArrayList<>();

        computedFileFlowable.subscribe(
                computedFiles::add,
                future::completeExceptionally,
                () -> future.complete(new Report(computedFiles, ranges, longestFiles))
        );
        return future;
    }

    @Override
    public CompletableFuture<Flowable<ComputedFile>> analyzeSources(String directory, int longestFiles, int numberOfRanges, int maxLines, Monitor monitor) {
        try {
            return CompletableFuture.supplyAsync(() -> {
                List<FilePath> files;
                List<LongRange> ranges = CreateRange.generateRanges(maxLines, numberOfRanges);
                if (monitor.getUnprocessedFiles().isEmpty()) {
                    files = FileSearcher.getAllFilesWithPaths(directory);
                    monitor.addUnprocessedFiles(files);
                }
                else {
                    files = new ArrayList<>(monitor.getUnprocessedFiles());
                }
                if (files != null) {
                    return Flowable.fromIterable(files)
                            .map(file -> computeFile(file, ranges));
                            /*.scan(new ArrayList<>(), (list, value) -> {
                                if (value != null) {
                                    list.add(value);
                                }
                                return list;
                            });*/
                }
                return null;
            });

        } catch (Exception e){
            return null;
        }
    }

    private ComputedFile computeFile(FilePath filePath, List<LongRange> ranges) {
        long fileLen;
        try (Stream<String> lines = Files.lines(Paths.get(filePath.getCompleteFilePath()), StandardCharsets.UTF_8)) {
            fileLen = lines.count();
        } catch (Exception e){
            return null;
        }
        for (LongRange range: ranges) {
            if (range.isInRange(fileLen)) {
                return new ComputedFile(filePath, range.getMin(), fileLen);
            }
        }
        return null;
    }


}
