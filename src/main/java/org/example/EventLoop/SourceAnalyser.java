package org.example.EventLoop;


import io.vertx.core.Future;
import org.example.Utilities.GUI.Flag;
import org.example.Utilities.GUI.View;
import org.example.Utilities.LongRange;
import org.example.Utilities.Monitor;
import org.example.Utilities.Report;

import java.util.List;

public interface SourceAnalyser {

    Future<Report> getReport(String d, int longestFiles, int ranges, int maxLines);

    Future<Void> analyzeSources(String directory, Flag stopFlag, Monitor monitor, List<LongRange> ranges);
}
