package com.umbc.aca.stages;

import com.umbc.aca.Instruction;
import com.umbc.aca.Pipeline;

public class ExecuteIU implements Stage {
	
	public static boolean isBusy = false;
	public static int counter = 1;
	public static Instruction instruction;
	
	
	@Override
	public void init(Instruction inst) {
		// TODO Auto-generated method stub
		if(!isBusy) {
			isBusy = true;
			ExecuteIU.instruction = inst;
			execute();
			counter--;
		}
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		if(counter==0) {
			if(next()) {
				resetStage();
			}
		} else if (isBusy) {
			counter--;
		}
	}

	@Override
	public boolean next() {
		// TODO Auto-generated method stub
		if(!Mem.isBusy ) {
			writeOutput();
			Pipeline.memStage.init(ExecuteIU.instruction);
			return true;
		} else {
			instruction.setSTRUCT(true);
			return false;
		}
	}

	@Override
	public void writeOutput() {
		// TODO Auto-generated method stub
//		ExecuteIU.instruction.setEX(Pipeline.cycleCount);
	}
	
	public void resetStage() {
		isBusy = false;
		counter = 1;
	}
	
	public void execute() {
		
		int data = 0;
		String ins = instruction.getName().toLowerCase();
		if (ins.equals("lw") || ins.equals("sw") 
				|| ins.equals("l.d") || ins.equals("s.d")) {
			data = Pipeline.registers.get(instruction.getOp2()).getValue() 
					+ instruction.getOffset();	
		} else if(ins.equals("dadd")) {
			data = Pipeline.registers.get(instruction.getOp2()).getValue()
					+ Pipeline.registers.get(instruction.getOp3()).getValue();
		} else if(ins.equals("dsub")) {
			data = Pipeline.registers.get(instruction.getOp2()).getValue()
					- Pipeline.registers.get(instruction.getOp3()).getValue();
		} else if(ins.equals("daddi")) {
			data = Pipeline.registers.get(instruction.getOp2()).getValue() 
					+ instruction.getOffset();
		} else if(ins.equals("dsubi")) {
			data = Pipeline.registers.get(instruction.getOp2()).getValue() 
					- instruction.getOffset();
		} else if(ins.equals("and")) {
			data = Pipeline.registers.get(instruction.getOp2()).getValue()
					& Pipeline.registers.get(instruction.getOp3()).getValue();
		} else if(ins.equals("andi")) {
			data = Pipeline.registers.get(instruction.getOp2()).getValue() 
					& instruction.getOffset();
		} else if(ins.equals("or")) {
			data = Pipeline.registers.get(instruction.getOp2()).getValue()
					| Pipeline.registers.get(instruction.getOp3()).getValue();
		} else if(ins.equals("ori")) {
			data = Pipeline.registers.get(instruction.getOp2()).getValue() 
					| instruction.getOffset();
		}
		
		instruction.setData(data);
	}
}
