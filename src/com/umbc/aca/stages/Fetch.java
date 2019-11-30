package com.umbc.aca.stages;

import com.umbc.aca.Instruction;
import com.umbc.aca.Pipeline;
import com.umbc.aca.utils.AccessInfo;

public class Fetch implements Stage {
	public static int counter = AccessInfo.mainMemoryCycles;
	public static boolean isBusy = false;
	public static Instruction instruction = new Instruction();
	public static boolean branchTrue = false;
	
	public void init() {
		if (!isBusy && Pipeline.programCounter!=-1) {
			isBusy = true;
			fetchInstruction();
			counter--;
		}
	}
	
	public void fetchInstruction() {
		String binaryLocation = convertToBinary(Pipeline.programCounter);
		instruction.setInstruction(Pipeline.instructionCache.getInstruction(binaryLocation).toLowerCase());
	}
	
	public void update() {
		
		if(counter == 0 && isBusy) {
			if(Pipeline.programCounter==-1) {
				writeOutput();
				instruction.setName(instruction.getInstruction().trim());	
				Pipeline.inst.add(instruction);
				isBusy = false;
			}
			if(branchTrue) {
				branchTrue = false;
				writeOutput();
				instruction.setName(instruction.getInstruction().trim());
				Pipeline.inst.add(instruction);
				isBusy = false;
				instruction = new Instruction();
				counter = AccessInfo.mainMemoryCycles;
				init();
			}
			else if(next()) {
				isBusy = false;
				instruction = new Instruction();
				counter = AccessInfo.mainMemoryCycles;
				Pipeline.programCounter++;
				init();
			}
			return;
		}
		
		if (!isBusy) {
			init();
		} else {
			counter--;
		}
	}
	
	public boolean next() {
		
		if(Decode.isBusy) {
			return false;
		} else {
			writeOutput();
			Pipeline.decode.init(instruction);
			return true;
		}
	}
	
	public void writeOutput() {
		instruction.setIF(Pipeline.cycleCount);
	}

	@Override
	public void init(Instruction inst) {
		// TODO Auto-generated method stub
		
	}
	
	public String convertToBinary(int n) {
		String s = Integer.toBinaryString(n);
		String binaryValue = s;
		if(s.length()<8) {
			int offset = 8 - s.length();
			for (int j = 0; j<offset; j++) {
				binaryValue = "0" + binaryValue;
			}
		}
		return binaryValue;
	}
}
