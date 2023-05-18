package org.example.ReactiveProgramming;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import org.example.Utilities.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static org.example.Utilities.ComputeFile.computeFile;

public class ReactiveProgrammingSourceAnalyser implements SourceAnalyser{

    //returns all the subdir in a directory
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

        //flowable that starting form a directory outputs all the files inside (also the files in subdir)
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
                .map(file -> computeFile(file, ranges)).subscribeOn(Schedulers.single());

        List<ComputedFile> computedFiles = new ArrayList<>();
        //subscribe the flowable
        computedFileFlowable.subscribe(
                computedFiles::add,
                future::completeExceptionally,
                //when flowable complete
                () -> future.complete(new Report(computedFiles, ranges, longestFiles))
        );
        return future;
    }

    @Override
    public CompletableFuture<Flowable<ComputedFile>> analyzeSources(
            String directory, int longestFiles, int numberOfRanges, int maxLines, Monitor monitor) {
        try {
            return CompletableFuture.supplyAsync(() -> {
                List<FilePath> files;
                List<LongRange> ranges = CreateRange.generateRanges(maxLines, numberOfRanges);
                //file to process
                if (monitor.getUnprocessedFiles().isEmpty()) {
                    files = FileSearcher.getAllFilesWithPaths(directory);
                    monitor.addUnprocessedFiles(files);
                }
                else {
                    files = new ArrayList<>(monitor.getUnprocessedFiles());
                }
                if (files != null) {
                    //return the flowable that produces computedFiles
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

}
