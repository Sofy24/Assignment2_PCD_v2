package org.example.Utilities.GUI;

import org.example.Utilities.ComputedFile;
import org.example.Utilities.LongRange;

import java.util.List;

public class View {

	private final ViewFrame frame;
	private final ViewDistributionFrame distribFrame;
	
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
		frame.update(files, nMaxFilesToRank);
		distribFrame.updateDistribution(files, ranges);
	}
	
	public void done() {
		frame.done();
	}

}
	
