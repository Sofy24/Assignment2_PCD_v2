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



    public static void main(String[] args) throws InterruptedException {
        String directory = "C:\\Users\\seraf\\OneDrive\\Desktop\\SSS\\ASSIGNMENT1\\file50";
        List<LongRange> ranges = CreateRange.generateRanges(200, 5);
        Flowable<ComputedFile> computedFileFlowable = Flowable.fromCallable(() ->
                        Objects.requireNonNull(getAllSubDirectory(directory, new ArrayList<>())).stream()
                                .map(dir -> {
                                    System.out.println("DIR:"+dir);
                                    Set<String> files = FileSearcher.getJavaSourceFiles(dir);
                                    if (files != null) {
                                        return files.stream()
                                                .map(file -> dir + System.getProperty("file.separator") + file)
                                                .toList();
                                    }
                                    return null;
                                }).filter(Objects::nonNull)
                                .flatMap(List::stream)
                                .peek(file -> System.out.println(file))
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

        computedFileFlowable.subscribe(
                computedFile -> System.out.println(computedFile.getFilePath()),
                error -> System.out.println("error"),
                () -> System.out.println("ALL FINISHED")
        );
        sleep(1000);

        System.out.println(computedFileFlowable.blockingFirst());


    }
}
