package org.example.EventLoop;


import io.vertx.core.Future;
import org.example.Utilities.Report;

public interface SourceAnalyser {

    Future<Report> getReport(String d, int longestFiles, int ranges, int maxLines);

    void analyzeSources(String d);
}
