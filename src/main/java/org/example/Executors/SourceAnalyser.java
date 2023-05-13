package org.example.Executors;


import org.example.Utilities.GUI.Flag;
import org.example.Utilities.Monitor;
import org.example.Utilities.Report;

import java.util.concurrent.CompletableFuture;

public interface SourceAnalyser {

    CompletableFuture<Report> getReport(String directory, int longestFiles, int numberOfRanges, int maxLines);

    CompletableFuture<Void> analyzeSources(String directory, int longestFiles, int numberOfRanges, int maxLines,
                                           Monitor monitor, Flag blockFlag);
}
