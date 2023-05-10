package org.example;

public class ViewerAgent extends BasicAgent {
	private View view;
	private Flag done;

	private CurrentState currentState;
	
	protected ViewerAgent(View view, Flag done) {
		super("viewer");
		this.view = view;
		this.done = done;
		this.currentState = CurrentState.getInstance();
	}

	public void run() {
		while (!done.isSet()) {
			try {
				view.update(CurrentState.getFiles()); //metti dentro stato corrente delle info
				Thread.sleep(10);
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}
		view.update(CurrentState.getFiles()); //metti dentro stato corrente delle info
	}
}
