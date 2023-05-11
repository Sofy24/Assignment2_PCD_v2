package org.example.Executors;


import org.example.Flag;
import org.example.Utilities.Report;

import java.util.concurrent.CompletableFuture;

public interface SourceAnalyser {

    CompletableFuture<Report> getReport(String directory, int longestFiles, int numberOfRanges, int maxLines);

    CompletableFuture<?> analyzeSources(String directory, int longestFiles, int numberOfRanges, int maxLines,
                                        Monitor monitor, Flag blockFlag);
}
