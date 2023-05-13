package org.example.Executors.GUI;

import org.example.Executors.ExecutorsSourceAnalyser;
import org.example.Utilities.GUI.Flag;
import org.example.Utilities.GUI.InputListener;
import org.example.Utilities.CreateRange;
import org.example.Utilities.LongRange;
import org.example.Utilities.Monitor;
import org.example.Executors.SourceAnalyser;
import org.example.Utilities.GUI.View;
import org.example.Utilities.GUI.ViewerAgent;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;


public class ControllerExecutors implements InputListener {

	private final Flag stopFlag;
	private final View view;
	private final SourceAnalyser sourceAnalyser = new ExecutorsSourceAnalyser();
	private boolean alreadyStarted = false;
	private Monitor monitor;
	private ViewerAgent viewerAgent;
	private List<LongRange> ranges = new ArrayList<>();

	public ControllerExecutors(View view){
		this.stopFlag = new Flag();
		this.view = view;
	}
	
	public void started(File dir, int nMaxFilesToRank, int nBands, int maxLoc){
		stopFlag.disable();
		//only at first start
		if (!alreadyStarted) {
			 monitor = new Monitor();
			 alreadyStarted = true;
			 ranges = CreateRange.generateRanges(maxLoc, nBands);
		}

		//started the View Agent
		this.viewerAgent = new ViewerAgent(this.view, this.stopFlag, this.monitor, nMaxFilesToRank, ranges);
		this.viewerAgent.start();

		CompletableFuture<Void> future = this.sourceAnalyser.analyzeSources(
			dir.getAbsolutePath(), nMaxFilesToRank, nBands, maxLoc, monitor, stopFlag);
		//the future returned complete only if interrupted or completed
		future.thenAccept(result -> {
			if (stopFlag.isSet()) {
				//if interrupted
				System.out.println("interrupted");
			}
			else {
				//if completed, reset for a new directory to be submitted, block the viewAgent.
				alreadyStarted = false;
				stopFlag.enable();
				view.done();
				System.out.println("Completed");
			}
		});

	}

	public void stopped() {
		stopFlag.enable();
	}

}
