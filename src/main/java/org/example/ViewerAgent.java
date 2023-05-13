package org.example;

import org.example.Utilities.Monitor;

public class ViewerAgent extends BasicAgent {
	private View view;
	private Flag stopFlag;
	private Monitor monitor;
	private int nMaxFilesToRank;
	
	public ViewerAgent(View view, Flag stopFlag, Monitor monitor, int nMaxFilesToRank) {
		super("viewer");
		this.view = view;
		this.stopFlag = stopFlag;
		this.monitor = monitor;
		this.nMaxFilesToRank = nMaxFilesToRank;
	}

	public void run() {
		//periodically update the view with the file processed
		while (!stopFlag.isSet()) {
			try {
				view.update(this.monitor.getComputedFileList(), this.nMaxFilesToRank);
				Thread.sleep(30);
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}
		//last update before interruption or completion
		view.update(this.monitor.getComputedFileList(), this.nMaxFilesToRank);
	}
}
