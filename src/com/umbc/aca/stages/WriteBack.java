package com.umbc.aca.stages;

import com.umbc.aca.Instruction;
import com.umbc.aca.Pipeline;
import com.umbc.aca.utils.Register;

public class WriteBack implements Stage {
	
	public static boolean isBusy = false;
	public static int counter = 1;
	public static Instruction instruction;

	@Override
	public void init(Instruction inst) {
		// TODO Auto-generated method stub
		if(!isBusy) {
			isBusy = true;
			WriteBack.instruction = inst;
			execute();
			counter--;
		}
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		if(counter==0) {
			writeOutput();
			resetStage();
			updateRegisters();
		} else if (isBusy) {
			counter--;
		}
	}

	@Override
	public boolean next() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void writeOutput() {
		// TODO Auto-generated method stub
		WriteBack.instruction.setWB(Pipeline.cycleCount);
		Pipeline.inst.add(WriteBack.instruction);
	}
	
	public void resetStage() {
		isBusy = false;
		counter = 1;
	}
	
	public void execute() {
		String ins = instruction.getName();
		if(ins.equals("lw") || ins.equals("dadd") || ins.equals("daddi")
				|| ins.equals("dsub") || ins.equals("dsubi") 
				|| ins.equals("and") || ins.equals("andi") 
				|| ins.equals("or") || ins.equals("ori")) {
			
//			Register r = Pipeline.registers.get(instruction.getOp1());
//			System.out.println("Before update: " + instruction.getOp1() + " " + r.getValue());
//			r.setValue(instruction.getData());
//			Pipeline.registers.put(instruction.getOp1(), r);
//			System.out.println("After update: " + instruction.getOp1() + " " + Pipeline.registers.get(instruction.getOp1()).getValue());
			
			Pipeline.registers.get(instruction.getOp1()).setValue(instruction.getData());
		}
	}
	
	
	public void updateRegisters() {
		String[] op = {instruction.getOp1(), instruction.getOp2(), instruction.getOp3()};
		for(int i = 0; i<op.length; i++) {
			if(Pipeline.registers.containsKey(op[i])) {
				Register r = Pipeline.registers.get(op[i]);
				r.setBeingRead(false);
				r.setBeingWritten(false);
				Pipeline.registers.put(op[i], r);
			}
		}
	}

}
