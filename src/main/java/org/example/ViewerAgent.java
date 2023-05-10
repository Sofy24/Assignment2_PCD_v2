package org.example;

public class ViewerAgent extends BasicAgent {

	private SourceLocMap map;
	private View view;
	private Flag done;
	
	protected ViewerAgent(SourceLocMap map, View view, Flag done) {
		super("viewer");
		this.map = map;
		this.view = view;
		this.done = done;
	}

	public void run() {
		while (!done.isSet()) {
			try {
				view.update(); //metti dentro stato corrente delle info
				Thread.sleep(10);
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}
		view.update(); //metti dentro stato corrente delle info
	}
}
