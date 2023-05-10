package org.example;

import org.example.Utilities.ComputedFile;

import java.util.List;

/**
 * Class representing the view part of the application.
 * 
 * @author aricci
 *
 */
public class View {

	private ViewFrame frame;
	private ViewDistributionFrame distribFrame;
	
	public View(){
		frame = new ViewFrame();
		distribFrame = new ViewDistributionFrame(800,600);
	}
	
	public void addListener(InputListener l){
		frame.addListener(l);
	}

	public void display() {
        javax.swing.SwingUtilities.invokeLater(() -> {
        	frame.setVisible(true);
        	distribFrame.setVisible(true);
        });
    }
	
	public void update(List<ComputedFile> files) { //rescritto
		frame.update(files);
		//distribFrame.updateDistribution(snapshot);
	}
	
	public void done() {
		frame.done();
	}

}
	
