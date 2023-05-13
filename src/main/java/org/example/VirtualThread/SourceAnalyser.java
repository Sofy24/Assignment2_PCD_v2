package org.example.VirtualThread;



import org.example.Utilities.Monitor;
import org.example.Flag;
import org.example.Utilities.ComputedFile;
import org.example.Utilities.Report;


import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public interface SourceAnalyser {

    CompletableFuture<Report> getReport(String directory, int longestFiles, int numberOfRanges, int maxLines);

    CompletableFuture<List<Future<ComputedFile>>> analyzeSources(String directory, int longestFiles, int numberOfRanges,
                                                                 int maxLines, Monitor monitor, Flag blockFlag);
}
