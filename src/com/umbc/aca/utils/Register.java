package com.umbc.aca.utils;

public class Register {
	private int value;
	private boolean beingRead;
	private boolean beingWritten;
	
	public int getValue() {
		return value;
	}
	public void setValue(int value) {
		this.value = value;
	}
	public boolean isBeingRead() {
		return beingRead;
	}
	public void setBeingRead(boolean beingRead) {
		this.beingRead = beingRead;
	}
	public boolean isBeingWritten() {
		return beingWritten;
	}
	public void setBeingWritten(boolean beingWritten) {
		this.beingWritten = beingWritten;
	}
	
	
	
}
