package org.example;

import org.example.Utilities.Monitor;

public class ViewerAgent extends BasicAgent {
	private View view;
	private Flag done;

	private Monitor monitor;

	private int nMaxFilesToRank;
	
	public ViewerAgent(View view, Flag done, Monitor monitor, int nMaxFilesToRank) {
		super("viewer");
		this.view = view;
		this.done = done;
		this.monitor = monitor;
		this.nMaxFilesToRank = nMaxFilesToRank;
	}

	public void run() {
		while (!done.isSet()) {
			try {
				view.update(this.monitor.getComputedFileList(), this.nMaxFilesToRank);
				Thread.sleep(30);
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}
		view.update(this.monitor.getComputedFileList(), this.nMaxFilesToRank);
	}
}
