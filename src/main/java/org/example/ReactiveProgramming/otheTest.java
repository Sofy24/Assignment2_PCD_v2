package org.example.ReactiveProgramming;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import org.example.Utilities.*;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

public class otheTest {

    static private void log(String msg) {
        System.out.println("[ " + Thread.currentThread().getName() + " ] " + msg);
    }

    static private ComputedFile computeFile(FilePath filePath, List<LongRange> ranges) {
        long fileLen = 0L;
        try (Stream<String> lines = Files.lines(Paths.get(filePath.getCompleteFilePath()), StandardCharsets.UTF_8)) {
            fileLen = lines.count();
        } catch (Exception e){
            System.out.println("ROMPIPALLE");
            return null;
        }
        for (LongRange range: ranges) {
            if (range.isInRange(fileLen)) {
                return new ComputedFile(filePath, range.getMin(), fileLen);
            }
        }
        return null;
    }

    static private Flowable<ArrayList<ComputedFile>> generateFLowable(List<FilePath> files,
                                                                      List<LongRange> ranges) {
        return Flowable.fromIterable(files)
                .map(file -> computeFile(file, ranges))
                .scan(new ArrayList<ComputedFile>(), (list, value) -> {
                    if (value != null) {
                        list.add(value);
                    }
                    return list;
                });
    }



    public static void main(String[] args) throws InterruptedException {
        String directory = "C:\\Users\\seraf\\OneDrive\\Desktop\\SSS\\ASSIGNMENT1\\file50";
        List<FilePath> files = FileSearcher.getAllFilesWithPaths(directory);
        List<LongRange> ranges = CreateRange.generateRanges(200, 5);
        @NonNull Flowable<ArrayList<ComputedFile>> flow;
        if (files != null) {
            flow = generateFLowable(new ArrayList<>(files), ranges);
            Disposable sub = flow.subscribeOn(Schedulers.single()).subscribe(
                    l -> {
                        System.out.println(l.size());
                        if (l.size() > 0) {
                            System.out.println(l.size() + " "+l.toString());
                            files.remove(l.get(l.size()-1).getFilePath());
                        }},
                    error -> {},
                    () -> System.out.println("COmpleted")
            );
            Thread.sleep(20);
            sub.dispose();
            //System.out.println("\n\n interrput \n\n");
            flow = generateFLowable(new ArrayList<>(files), ranges);
            Thread.sleep(3000);
            flow.subscribe(l -> {
                if (l.size() > 0) {
                    System.out.println("N" + l.size());
                    System.out.println("N" + l.get(l.size() - 1).getFilePath());
                }
            });
        }






    }
}
