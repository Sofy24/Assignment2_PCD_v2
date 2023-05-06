package org.example.EventLoop;


import org.example.Utilities.Report;

import java.util.concurrent.Future;

public interface SourceAnalyser {

    Future<Report> getReport(String d, int longestFiles, int ranges, int maxLines);

    void analyzeSources(String d);
}
