package org.example.ReactiveProgramming;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import org.example.Utilities.*;

import java.io.File;
import java.lang.foreign.PaddingLayout;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static java.lang.Thread.sleep;

public class Test2 {

    public static List<String> getAllSubDirectory(String directory, List<String> allDirs) {
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

    public static CompletableFuture<Report> generateReport(String directory, int longestFiles, int numberOfRanges, int maxLines) {
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
                () ->  {
                    future.complete(new Report(computedFiles, ranges, longestFiles));
                }
        );
        return future;
    }

    public static void main(String[] args) throws InterruptedException {
        String directory = "C:\\Users\\seraf\\OneDrive\\Desktop\\SSS\\ASSIGNMENT1\\file50";
        var future = generateReport(directory, 5, 5, 150);

        future.thenAccept(Report::getResults);
        //simulate doing other stuff
        sleep(5000);


    }
}
