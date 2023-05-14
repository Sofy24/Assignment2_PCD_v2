package org.example.ReactiveProgramming;


import io.reactivex.rxjava3.core.Flowable;
import org.example.Utilities.ComputedFile;
import org.example.Utilities.Monitor;
import org.example.Utilities.Report;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public interface SourceAnalyser {

    CompletableFuture<Report> getReport(String directory, int longestFiles, int numberOfRanges, int maxLines);

    CompletableFuture<Flowable<ComputedFile>> analyzeSources(String directory, int longestFiles, int numberOfRanges, int maxLines, Monitor monitor);
}
