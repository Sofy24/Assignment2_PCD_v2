package org.example;

import org.example.Utilities.ComputedFile;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.*;


public class ViewDistributionFrame extends JFrame {
    
    private DistributionPanel panel;
    
    public ViewDistributionFrame(int w, int h){
        setTitle("LoC Distribution");
        setSize(w, h);
        setResizable(false);
        this.setLocation(200, 400);
        panel = new DistributionPanel(w,h);
        getContentPane().add(panel);
        addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent ev){
				System.exit(-1);
			}
			public void windowClosed(WindowEvent ev){
				System.exit(-1);
			}
		});
    }
    
    public void updateDistribution(List<ComputedFile> files, int nMaxFilesToRank){
    	SwingUtilities.invokeLater(() -> panel.updateDistribution(files, nMaxFilesToRank));
    }
        
    public static class DistributionPanel extends JPanel {
        private int w;
        private int h;
        private List<ComputedFile> files;
        private int nMaxFilesToRank;
        
        public DistributionPanel(int w, int h){
            setSize(w,h);
            this.w = w;
            this.h = h - 150;
            this.files = new ArrayList<>();
            this.nMaxFilesToRank = 0;
        }

        public void paint(Graphics g){
    		Graphics2D g2 = (Graphics2D) g;
            if (!files.isEmpty()) {
                List<Integer> bands = files.stream()
                        .sorted(Comparator.comparing(ComputedFile::getLength).reversed())
                        .limit(nMaxFilesToRank)
                        .map(f -> f.getLength().intValue()).toList();
                int max = 0;
                for (Integer band : bands) {
                    if (band > max) {
                        max = band;
                    }
                }

	    		if (max > 0) {
                    int x = 10;
                    double deltay = (((double) h) / max) * 0.9;

                    int deltax = (w - 20) / bands.size();

                    Font font = new Font(null, Font.PLAIN, 8);
                    g2.setFont(font);

                    for (Integer band : bands) {
                        int height = (int) (band * deltay);
                        g2.drawRect(x, h - height, deltax - 10, height);
                        g2.drawString("" + band, x, h - height - 10);
                        x += deltax;
                    }


                    int nLocPerBand = nMaxFilesToRank / (bands.size() - 1);
                    int a = 0;
                    int b = nLocPerBand;

                    x = 12;

                    Font fontRanges = new Font(null, Font.PLAIN, 14);
                    AffineTransform affineTransform = new AffineTransform();
                    affineTransform.rotate(Math.toRadians(90), 0, 0);
                    Font rotatedFont = fontRanges.deriveFont(affineTransform);
                    g2.setFont(rotatedFont);

                    for (int i = 0; i < bands.size() - 1; i++) {
                        g2.drawString(" (" + a + " - " + b + ")", x, h + 10);
                        a = b;
                        b += nLocPerBand;
                        x += deltax;
                    }

                    g2.drawString(" ( > " + a + ")", x, h + 10);


                }
	    		}


    		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
    		          RenderingHints.VALUE_ANTIALIAS_ON);
    		g2.setRenderingHint(RenderingHints.KEY_RENDERING,
    		          RenderingHints.VALUE_RENDER_QUALITY);
    		g2.clearRect(0,0,this.getWidth(),this.getHeight());


        }

        public void updateDistribution(List<ComputedFile> files, int nMaxFilesToRank){
            this.files = files;
            this.nMaxFilesToRank = nMaxFilesToRank;
        	repaint();
        }

    }
}
