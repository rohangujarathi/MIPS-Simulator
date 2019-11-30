package com.umbc.aca;

public class Hazards {

	/**
	 * Checks whether there is a hazard or not
	 * @return
	 */
	public static boolean isRawHazard(String register) {
		if(Pipeline.registers.get(register).isBeingWritten()) {
			return true;
		} else {
			return false;
		}
	}
	
	public static boolean isWawHazard() {
		
		return true;
	}
	
}
