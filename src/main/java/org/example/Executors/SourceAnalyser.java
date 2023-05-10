package org.example.Executors;


import io.vertx.core.Future;
import org.example.Utilities.Report;

import java.util.concurrent.CompletableFuture;

public interface SourceAnalyser {

    CompletableFuture<Report> getReport(String directory, int longestFiles, int numberOfRanges, int maxLines);

    void analyzeSources(String directory, int longestFiles, int numberOfRanges, int maxLines);
}
