package org.example;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;

public class SourceLocMap {

	private int[] bands;
	private LinkedList<LocEntry> maxLocSources;
	private int maxSourcesToTrack;
	private int maxLinesOfCode;
	private long nSrcProcessed;
	
	public SourceLocMap(int maxSourcesToTrack, int nBands, int maxLinesOfCode) {
		maxLocSources = new LinkedList<>();
		this.maxSourcesToTrack = maxSourcesToTrack;
		this.maxLinesOfCode = maxLinesOfCode;
		bands = new int[nBands];
		for (int i = 0; i < bands.length; i++) {
			bands[i] = 0;
		}
		nSrcProcessed = 0;
	}
	
	public synchronized void clear() {
		maxLocSources.clear();
		for (int i = 0; i < bands.length; i++) {
			bands[i] = 0;
		}
		nSrcProcessed = 0;
	}
	
	public synchronized void add(File src, int nLoc) {
		nSrcProcessed++;
		Iterator<LocEntry> it = maxLocSources.iterator();
		int pos = 0;
		boolean toBeInserted = false;
		while (it.hasNext() && pos < maxSourcesToTrack) {
			LocEntry el = it.next();
			if (nLoc > el.getNLoc()) {
				toBeInserted = true;
				break;
			} 	
			pos++;
		}

		if (pos < maxSourcesToTrack || toBeInserted) {
			maxLocSources.add(pos, new LocEntry(src.getName(), src.getAbsolutePath(), nLoc));
			if (maxLocSources.size() > maxSourcesToTrack) {	
				maxLocSources.removeLast();
			}
		}

		if (nLoc > maxLinesOfCode) {
			bands[bands.length - 1]++;
		} else {
			int nLocPerBand = maxLinesOfCode/(bands.length - 1);
			int bandIndex = nLoc / nLocPerBand;
			bands[bandIndex]++;
		}
	}
	
	public synchronized void dumpSrcsWithMoreNLocs() {
		maxLocSources.forEach(el -> {
			System.out.println((el.getSrcFullPath() + " - "  + el.getNLoc()));
		}); 
	}

	public synchronized SourceLocMapSnapshot getSnaptshot() {
		LocEntry[] list = new LocEntry[maxLocSources.size()];
		maxLocSources.toArray(list);
		return new SourceLocMapSnapshot(nSrcProcessed, list, bands.clone(),maxLinesOfCode);
	}
		
	public synchronized void dumpDistribution() {
		int nLocPerBand = maxLinesOfCode/(bands.length - 1);
		int a = 0;
		int b = nLocPerBand;
		
		for (int i = 0; i < bands.length - 1; i++) {
			System.out.println("band " + (i+1) + " (" + a + " - " + b + "): " + bands[i]);
			a = b + 1;
			b += nLocPerBand;
		}
		System.out.println("band " + bands.length + " ( >= " + a + "): " + bands[bands.length - 1]);
	}
	
}
