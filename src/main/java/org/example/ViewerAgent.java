package org.example;

import org.example.Executors.Monitor;

public class ViewerAgent extends BasicAgent {
	private View view;
	private Flag done;

	private Monitor monitor;
	
	protected ViewerAgent(View view, Flag done, Monitor monitor) {
		super("viewer");
		this.view = view;
		this.done = done;
		this.monitor = monitor;
	}

	public void run() {
		while (!done.isSet()) {
			try {
				view.update(this.monitor.getComputedFileList());
				Thread.sleep(10);
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}
		view.update(this.monitor.getComputedFileList());
	}
}
