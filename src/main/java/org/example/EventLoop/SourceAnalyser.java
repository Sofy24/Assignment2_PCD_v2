package org.example.EventLoop;


import org.example.Utilities.Report;

public interface SourceAnalyser {

    Report getReport(String d, int longestFiles, int ranges, int maxLines);

    void analyzeSources(String d);
}
