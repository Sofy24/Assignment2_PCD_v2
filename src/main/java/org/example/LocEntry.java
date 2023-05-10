package org.example;

public class LocEntry {
	
	private String srcFullPath;
	private String srcFileName;
	
	private int nLoc;
	
	LocEntry(String srcFileName, String srcFullPath, int nLoc){
		this.srcFullPath = srcFullPath;
		this.srcFileName = srcFileName;
		this.nLoc = nLoc;
	}
	
	public String getSrcFileName() {
		return srcFileName;
	}
	
	public String getSrcFullPath() {
		return srcFullPath;
	}
	
	public int getNLoc() {
		return nLoc;
	}
}
