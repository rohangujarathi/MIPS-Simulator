package com.umbc.aca.utils;

public class ExecutionUnitInfo {
	/**
	 * Cycle time and pipeline or not info for all the execution units
	 * such as adder, multiply and divide 
	 */
	
	private int cycleTime;
	private boolean pipeline;
	
	public int getCycleTime() {
		return cycleTime;
	}
	public void setCycleTime(int cycleTime) {
		this.cycleTime = cycleTime;
	}
	public boolean isPipeline() {
		return pipeline;
	}
	public void setPipeline(boolean pipeline) {
		this.pipeline = pipeline;
	}
	
}
