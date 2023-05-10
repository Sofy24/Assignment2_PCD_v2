package org.example;

public class SourceLocMapSnapshot {
	
	private long nSrcProcessed;
	private LocEntry[] entries;
	private int[] bands;
	private int maxLoC;
	
	public SourceLocMapSnapshot(long nSrcProcessed, LocEntry[] entries, int[] bands, int maxLoC) {
		this.nSrcProcessed = nSrcProcessed;
		this.entries = entries;
		this.bands = bands;
		this.maxLoC = maxLoC;

	}

	public long getNumSrcProcessed() {
		return nSrcProcessed;
	}

	public LocEntry[] getRank() {
		return entries;
	}

	public int[] getBands() {
		return bands;
	}
	
	public int getMaxLoc() {
		return maxLoC;
	}


}
