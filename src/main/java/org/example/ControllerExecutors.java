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
	
	public ControllerExecutors(View view){
		this.stopFlag = new Flag();
		this.view = view;
	}
	
	public void started(File dir, int nMaxFilesToRank, int nBands, int maxLoc){
		stopFlag.disable();
		Monitor monitor = new Monitor();
		CompletableFuture<?> future = this.sourceAnalyser.analyzeSources(
				dir.getName(), nMaxFilesToRank, nBands, maxLoc, monitor, stopFlag);
		future.join();
		future.thenAccept(result -> System.out.println("Completed"));
	}

	public void stopped() {
		stopFlag.enable();
	}

}
