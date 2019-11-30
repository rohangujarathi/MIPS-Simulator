package com.umbc.aca;

public class Instruction {
	private String name, label, instruction, op1, op2, op3;
	private int offset;
	private int IF, ID, MEM, EX, WB;
	private boolean RAW, WAR, WAW, STRUCT;
	private int data;
	
	public int getData() {
		return data;
	}
	public void setData(int data) {
		this.data = data;
	}
	public String getInstruction() {
		return instruction;
	}
	public void setInstruction(String instruction) {
		this.instruction = instruction;
	}
	
	public int getIF() {
		return IF;
	}
	public void setIF(int iF) {
		IF = iF;
	}
	public int getID() {
		return ID;
	}
	public void setID(int iD) {
		ID = iD;
	}
	public int getMEM() {
		return MEM;
	}
	public void setMEM(int mEM) {
		MEM = mEM;
	}
	public int getEX() {
		return EX;
	}
	public void setEX(int eX) {
		EX = eX;
	}
	public int getWB() {
		return WB;
	}
	public void setWB(int wB) {
		WB = wB;
	}
	public boolean isRAW() {
		return RAW;
	}
	public void setRAW(boolean rAW) {
		RAW = rAW;
	}
	public boolean isWAR() {
		return WAR;
	}
	public void setWAR(boolean wAR) {
		WAR = wAR;
	}
	public boolean isWAW() {
		return WAW;
	}
	public void setWAW(boolean wAW) {
		WAW = wAW;
	}
	public boolean isSTRUCT() {
		return STRUCT;
	}
	public void setSTRUCT(boolean sTRUCT) {
		STRUCT = sTRUCT;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getOp1() {
		return op1;
	}
	public void setOp1(String op1) {
		this.op1 = op1;
	}
	public String getOp2() {
		return op2;
	}
	public void setOp2(String op2) {
		this.op2 = op2;
	}
	public String getOp3() {
		return op3;
	}
	public void setOp3(String op3) {
		this.op3 = op3;
	}
	public int getOffset() {
		return offset;
	}
	public void setOffset(int offset) {
		this.offset = offset;
	}

}
