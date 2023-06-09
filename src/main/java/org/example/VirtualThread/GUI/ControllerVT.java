package org.example.VirtualThread.GUI;


import org.example.Utilities.CreateRange;
import org.example.Utilities.LongRange;
import org.example.Utilities.Monitor;
import org.example.Utilities.GUI.Flag;
import org.example.Utilities.GUI.InputListener;
import org.example.Utilities.ComputedFile;
import org.example.Utilities.GUI.View;
import org.example.Utilities.GUI.ViewerAgent;
import org.example.VirtualThread.SourceAnalyser;
import org.example.VirtualThread.VTSourceAnalyser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;


public class ControllerVT implements InputListener {

	private final Flag stopFlag;
	private final View view;
	private final SourceAnalyser sourceAnalyser = new VTSourceAnalyser();
	private boolean alreadyStarted = false;
	private Monitor monitor;
	private ViewerAgent viewerAgent;
	private List<LongRange> ranges = new ArrayList<>();
	
	public ControllerVT(View view){
		this.stopFlag = new Flag();
		this.view = view;
	}
	
	public void started(File dir, int nMaxFilesToRank, int nBands, int maxLoc){
		stopFlag.disable();
		if (!alreadyStarted) {
			//when first started
			 monitor = new Monitor();
			 alreadyStarted = true;
			 ranges = CreateRange.generateRanges(maxLoc, nBands);
		}
		//GUI update
		this.viewerAgent = new ViewerAgent(this.view, this.stopFlag, this.monitor, nMaxFilesToRank, ranges);
		CompletableFuture<List<Future<ComputedFile>>> future = this.sourceAnalyser.analyzeSources(
				dir.getAbsolutePath(), nMaxFilesToRank, nBands, maxLoc, monitor, stopFlag);
		future.thenAccept(result -> {
			this.viewerAgent.start();
			//every time a task complete
			result.forEach(f -> {
				try {
					monitor.replaceFileWithComputed(f.get());
				} catch (InterruptedException | ExecutionException ignored) {}
			});
			//when all completed
			if (stopFlag.isSet()) {
				System.out.println("interrupted");
			}
			else {
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
