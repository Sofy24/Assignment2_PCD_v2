package org.example.Utilities.GUI;

public class Flag {

	private boolean flag;
	
	public Flag() {
		flag = false;
	}
	
	public void disable() {
		flag = false;
	}
	
	public void enable() {
		flag = true;
	}
	
	public boolean isSet() {
		return flag;
	}
}
