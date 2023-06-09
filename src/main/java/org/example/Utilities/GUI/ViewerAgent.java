package org.example.Utilities.GUI;

import org.example.Utilities.LongRange;
import org.example.Utilities.Monitor;

import java.util.List;

public class ViewerAgent extends BasicAgent {
	private final View view;
	private final Flag stopFlag;
	private final Monitor monitor;
	private final int nMaxFilesToRank;
	private final List<LongRange> ranges;
	
	public ViewerAgent(View view, Flag stopFlag, Monitor monitor, int nMaxFilesToRank, List<LongRange> ranges) {
		super("viewer");
		this.view = view;
		this.stopFlag = stopFlag;
		this.monitor = monitor;
		this.nMaxFilesToRank = nMaxFilesToRank;
		this.ranges = ranges;
	}

	public void run() {
		//periodically update the view with the file processed
		while (!stopFlag.isSet()) {
			try {
				view.update(this.monitor.getComputedFileList(), this.nMaxFilesToRank, this.ranges);
				Thread.sleep(30);
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}
		//last update before interruption or completion
		view.update(this.monitor.getComputedFileList(), this.nMaxFilesToRank, this.ranges);
	}
}
