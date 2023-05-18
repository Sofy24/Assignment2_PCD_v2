package org.example.ReactiveProgramming.GUI;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import org.example.ReactiveProgramming.ReactiveProgrammingSourceAnalyser;
import org.example.ReactiveProgramming.SourceAnalyser;
import org.example.Utilities.ComputedFile;
import org.example.Utilities.CreateRange;
import org.example.Utilities.GUI.InputListener;
import org.example.Utilities.GUI.View;
import org.example.Utilities.LongRange;
import org.example.Utilities.Monitor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ControllerReactive implements InputListener {

    private final View view;
    private final SourceAnalyser sourceAnalyser = new ReactiveProgrammingSourceAnalyser();
    private boolean alreadyStarted = false;
    private Monitor monitor;
    private List<LongRange> ranges = new ArrayList<>();
    private Disposable disposable = null;


    public ControllerReactive(View view) {
        this.view = view;
    }

    @Override
    public void started(File dir, int maxFiles, int nBands, int maxLoc) {
        if (!alreadyStarted) {
            //when started for the first time
            monitor = new Monitor();
            alreadyStarted = true;
            ranges = CreateRange.generateRanges(maxLoc, nBands);
        }
        CompletableFuture<Flowable<ComputedFile>> future = this.sourceAnalyser.analyzeSources(
                dir.getAbsolutePath(), maxFiles, nBands, maxLoc, monitor);
        //when the flowable is ready, subscribe
        future.thenAccept(flowable -> disposable = flowable.subscribeOn(Schedulers.io()).subscribe(
                file -> {
                    monitor.replaceFileWithComputed(file);
                    view.update(monitor.getComputedFileList(), maxFiles, ranges);
                },
                error -> {},
                () -> {
                    //when flowable completed
                    alreadyStarted = false;
                    view.done();
                    System.out.println("Completed");
                }
        ));
    }

    @Override
    public void stopped() {
        //dispose of the flowable
        if (disposable != null) {
            disposable.dispose();
        }
    }
}
