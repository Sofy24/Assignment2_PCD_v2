package org.example;

import org.example.Utilities.ComputedFile;
import org.example.Utilities.LongRange;

import java.util.List;

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
	
	public void update(List<ComputedFile> files, int nMaxFilesToRank, List<LongRange> ranges) {
		System.out.println("updating... "+files.size());
		frame.update(files, nMaxFilesToRank);
		distribFrame.updateDistribution(files, ranges);
	}
	
	public void done() {
		frame.done();
	}

}
	
