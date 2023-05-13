package org.example;

import org.example.Executors.ExecutorsSourceAnalyser;
import org.example.Executors.Monitor;
import org.example.Executors.SourceAnalyser;

import java.io.File;
import java.util.Map;
import java.util.concurrent.CompletableFuture;


public class ControllerExecutors implements InputListener {

	private Flag stopFlag;
	private View view;
	private SourceAnalyser sourceAnalyser = new ExecutorsSourceAnalyser();
	private boolean alreadyStarted = false;

	private Monitor monitor;

	private ViewerAgent viewerAgent;
	
	public ControllerExecutors(View view){
		this.stopFlag = new Flag();
		this.view = view;
	}
	
	public void started(File dir, int nMaxFilesToRank, int nBands, int maxLoc){
		System.out.println("Restart");
		stopFlag.disable();
		if (!alreadyStarted) {
			 monitor = new Monitor();
			 alreadyStarted = true;
		}
		this.viewerAgent = new ViewerAgent(this.view, this.stopFlag, this.monitor, nMaxFilesToRank);
		this.viewerAgent.start();
		CompletableFuture<Void> future = this.sourceAnalyser.analyzeSources(
			dir.getAbsolutePath(), nMaxFilesToRank, nBands, maxLoc, monitor, stopFlag);
		future.thenAccept(result -> {
			if (stopFlag.isSet()) {
				System.out.println("interrupted");
			}
			else {
				alreadyStarted = false;
				stopFlag.enable();
				view.done();
				System.out.println("true Completed");
			}
		});

	}

	public void stopped() {
		stopFlag.enable();
	}

}
