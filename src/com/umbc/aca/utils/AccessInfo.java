package com.umbc.aca.utils;

public class AccessInfo {
	/**
	 * Provides cycle time of all the units and 
	 * whether a particular unit is pipelined or not
	 */
	
	public static int mainMemoryCycles, instructionCacheCycles, dataCacheCycles;
	private static ExecutionUnitInfo adder, multiplier, divider;
	
	public AccessInfo() {
	}

	public int getMainMemoryCycles() {
		return mainMemoryCycles;
	}

	public void setMainMemoryCycles(int mainMemoryCycles) {
		AccessInfo.mainMemoryCycles = mainMemoryCycles;
	}

	public int getInstructionCacheCycles() {
		return instructionCacheCycles;
	}

	public void setInstructionCacheCycles(int instructionCacheCycles) {
		AccessInfo.instructionCacheCycles = instructionCacheCycles;
	}

	public int getDataCacheCycles() {
		return dataCacheCycles;
	}

	public ExecutionUnitInfo getAdder() {
		return adder;
	}

	public void setAdder(ExecutionUnitInfo adder) {
		AccessInfo.adder = adder;
	}

	public ExecutionUnitInfo getMultiplier() {
		return multiplier;
	}

	public void setMultiplier(ExecutionUnitInfo multiplier) {
		AccessInfo.multiplier = multiplier;
	}

	public ExecutionUnitInfo getDivider() {
		return divider;
	}

	public void setDivider(ExecutionUnitInfo divider) {
		AccessInfo.divider = divider;
	}

	public void setDataCacheCycles(int dataCacheCycles) {
		AccessInfo.dataCacheCycles = dataCacheCycles;
	}
}
