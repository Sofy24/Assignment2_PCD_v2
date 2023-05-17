package org.example.EventLoop.sera;

import io.vertx.core.Future;
import org.example.EventLoop.EventLoopSourceAnalyzer;
import org.example.EventLoop.SourceAnalyser;
import org.example.Utilities.CreateRange;
import org.example.Utilities.GUI.Flag;
import org.example.Utilities.GUI.InputListener;
import org.example.Utilities.GUI.View;
import org.example.Utilities.GUI.ViewerAgent;
import org.example.Utilities.LongRange;
import org.example.Utilities.Monitor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ControllerEventLoop implements InputListener {

    private final View view;
    private final Flag stopFlag;
    private boolean alreadyStarted = false;
    private Monitor monitor;
    private List<LongRange> ranges = new ArrayList<>();
    private final SourceAnalyser sourceAnalyser = new EventLoopSourceAnalyzer();
    private ViewerAgent viewerAgent;

    public ControllerEventLoop(View view) {
        this.stopFlag = new Flag();
        this.view = view;
    }

    @Override
    public void started(File dir, int maxFiles, int nBands, int maxLoc) {
        stopFlag.disable();
        if (!alreadyStarted) {
            monitor = new Monitor();
            alreadyStarted = true;
            ranges = CreateRange.generateRanges(maxLoc, nBands);
        }
        this.viewerAgent = new ViewerAgent(this.view, this.stopFlag, this.monitor, maxFiles, ranges);
        this.viewerAgent.start();
        Future<Void> future = this.sourceAnalyser.analyzeSources(
                dir.getAbsolutePath(), stopFlag, monitor, ranges);
        System.out.println("STUFF");
        future.onComplete(f -> {
            if (stopFlag.isSet()) {
                System.out.println("Interrupted");
            } else {
                alreadyStarted = false;
                stopFlag.enable();
                view.done();
                System.out.println("Completed");
            }
        });
    }

    @Override
    public void stopped() {
        stopFlag.enable();
    }
}
