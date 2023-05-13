package org.example.VirtualThread.GUI;


import org.example.Executors.Monitor;
import org.example.Flag;
import org.example.InputListener;
import org.example.Utilities.ComputedFile;
import org.example.View;
import org.example.ViewerAgent;
import org.example.VirtualThread.SourceAnalyser;
import org.example.VirtualThread.VTSourceAnalyser;

import java.io.File;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;


public class ControllerExecutors implements InputListener {

	private Flag stopFlag;
	private View view;
	private SourceAnalyser sourceAnalyser = new VTSourceAnalyser();
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
		CompletableFuture<List<Future<ComputedFile>>> future = this.sourceAnalyser.analyzeSources(
				dir.getAbsolutePath(), nMaxFilesToRank, nBands, maxLoc, monitor, stopFlag);
		future.thenAccept(result -> {
			this.viewerAgent.start();
			result.forEach(f -> {
				try {
					monitor.addComputedFile(f.get());
				} catch (InterruptedException | ExecutionException ignored) {}
			});
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
