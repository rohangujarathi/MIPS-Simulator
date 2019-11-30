package com.umbc.aca;

import java.util.ArrayList;

public class Output {
	private ArrayList<String> instructions;
	private ArrayList<Integer> IF, ID, EX, WB;
	private ArrayList<Boolean> RAW, WAR, WAW, STRUCT;
	
	public ArrayList<String> getInstructions() {
		return instructions;
	}
	public void setInstructions(ArrayList<String> instructions) {
		this.instructions = instructions;
	}
	public ArrayList<Integer> getIF() {
		return IF;
	}
	public void setIF(ArrayList<Integer> iF) {
		IF = iF;
	}
	public ArrayList<Integer> getID() {
		return ID;
	}
	public void setID(ArrayList<Integer> iD) {
		ID = iD;
	}
	public ArrayList<Integer> getEX() {
		return EX;
	}
	public void setEX(ArrayList<Integer> eX) {
		EX = eX;
	}
	public ArrayList<Integer> getWB() {
		return WB;
	}
	public void setWB(ArrayList<Integer> wB) {
		WB = wB;
	}
	public ArrayList<Boolean> getRAW() {
		return RAW;
	}
	public void setRAW(ArrayList<Boolean> rAW) {
		RAW = rAW;
	}
	public ArrayList<Boolean> getWAR() {
		return WAR;
	}
	public void setWAR(ArrayList<Boolean> wAR) {
		WAR = wAR;
	}
	public ArrayList<Boolean> getWAW() {
		return WAW;
	}
	public void setWAW(ArrayList<Boolean> wAW) {
		WAW = wAW;
	}
	public ArrayList<Boolean> getSTRUCT() {
		return STRUCT;
	}
	public void setSTRUCT(ArrayList<Boolean> sTRUCT) {
		STRUCT = sTRUCT;
	}
	
	
}
