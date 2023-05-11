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
	
	public ControllerExecutors(View view){
		this.stopFlag = new Flag();
		this.view = view;
	}
	
	public void started(File dir, int nMaxFilesToRank, int nBands, int maxLoc){
		stopFlag.disable();
		if (!alreadyStarted) {
			 monitor = new Monitor();
			 alreadyStarted = true;
		}
		CompletableFuture<?> future = this.sourceAnalyser.analyzeSources(
				dir.getName(), nMaxFilesToRank, nBands, maxLoc, monitor, stopFlag);
		future.join();
		future.thenAccept(result -> {
			if (stopFlag.isSet()) {
				System.out.println("interrupted");
			}
			else {
				alreadyStarted = false;
				System.out.println("Completed");
			}
		});
	}

	public void stopped() {
		stopFlag.enable();
	}

}
