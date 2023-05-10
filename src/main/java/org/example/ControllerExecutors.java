package org.example;

import org.example.Executors.ExecutorsSourceAnalyser;
import org.example.Executors.SourceAnalyser;

import java.io.File;



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
		this.sourceAnalyser.analyzeSources(dir.getName(), nMaxFilesToRank, nBands, maxLoc);
	}

	public void stopped() {
		stopFlag.enable();
	}

}
