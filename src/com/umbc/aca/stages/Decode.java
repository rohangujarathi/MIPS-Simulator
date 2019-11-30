package com.umbc.aca.stages;

import com.umbc.aca.Instruction;
import com.umbc.aca.Pipeline;
import com.umbc.aca.storage.Memory;
import com.umbc.aca.utils.Register;

public class Decode implements Stage {
	public static boolean isBusy = false;
	public static int counter = 1;
	public static boolean temp = false;
	public static Instruction instruction;
	
	@Override
	public void init(Instruction inst) {
		if(!isBusy) {
			isBusy = true;
			Decode.instruction = inst;
			decodeInstructions(Decode.instruction.getInstruction());
			counter--;
		}
	}
	
	@Override
	public void update() {

		if(isBusy && Pipeline.programCounter!=-1) {
			if(instruction.getName().toLowerCase().equals("hlt")
					&& Memory.instructionSegment[Pipeline.programCounter-1].toLowerCase().equals("hlt")) {
				Pipeline.inst.add(instruction);
				writeOutput();
				Pipeline.programCounter = -1;
			} else if(counter==0) {
				if(checkHazards()) {
					return;
				} else if(checkBranchCondition()) {
					writeOutput();
					Pipeline.inst.add(instruction);
					resetStage();
					return;
				} else if(next()) {
					updateRegisterValues();
					resetStage();
				}
			} else {
				counter--;
			}
		}
	}
	
	@Override
	public boolean next() {
		
		String unit = getExecutionUnit(Decode.instruction.getName());
		if(unit.equals("iu")) {
			return nextToIU();
		} else if(unit.equals("add")) {
			return nextToAdder();
		} else if(unit.equals("multiply")) {
			return nextToMultiply();
		} else if(unit.equals("divide")) {
			return nextToDivide();
		}
		
		return true;
	}
	
	public boolean nextToIU() {
		if(!ExecuteIU.isBusy ) {
			writeOutput();
			Pipeline.executeIU.init(Decode.instruction);
			return true;
		} else {
//			instruction.setSTRUCT(true);
			return false;
		}
	}
	
	public boolean nextToAdder() {
		if(!ExecuteAdder.isBusy) {
			writeOutput();
			ExecuteAdder adder = new ExecuteAdder();
			adder.init(Decode.instruction);
			Pipeline.adderList.add(adder);
			return true;
		} else {
			instruction.setSTRUCT(true);
			return false;
		}
	}
	
	public boolean nextToMultiply() {
		if(!ExecuteMultiplier.isBusy) {
			writeOutput();
			ExecuteMultiplier multiplier = new ExecuteMultiplier();
			multiplier.init(Decode.instruction);
			Pipeline.multiplierList.add(multiplier);
			return true;
		} else {
			instruction.setSTRUCT(true);
			return false;
		}
	}
	
	public boolean nextToDivide() {
		if(!ExecuteDivider.isBusy) {
			writeOutput();
			ExecuteDivider divider = new ExecuteDivider();
			divider.init(Decode.instruction);
			Pipeline.dividerList.add(divider);
			return true;
		} else {
			instruction.setSTRUCT(true);
			return false;
		}
	}
	
	@Override
	public void writeOutput() {
		Decode.instruction.setID(Pipeline.cycleCount);
	}
	
	public boolean checkBranchCondition() {
		if(Decode.instruction.getName().toLowerCase().equals("hlt")) {
			return true;
		} else if(Decode.instruction.getName().toLowerCase().equals("bne")) {
			if(!checkIfBranchTrue(Decode.instruction.getOp1(), Decode.instruction.getOp2())) {
				Pipeline.programCounter = Pipeline.labels.get(Decode.instruction.getLabel());
				Fetch.branchTrue = true;
			}
			return true;
		} else if(Decode.instruction.getName().toLowerCase().equals("beq")) {
			if(checkIfBranchTrue(Decode.instruction.getOp1(), Decode.instruction.getOp2())) {
				Pipeline.programCounter = Pipeline.labels.get(Decode.instruction.getLabel());
				Fetch.branchTrue = true;
			}
			resetStage();
			return true;
		} else if(Decode.instruction.getName().toLowerCase().equals("j")) {
			Pipeline.programCounter = Pipeline.labels.get(Decode.instruction.getLabel());
			Fetch.branchTrue = true;
//			resetStage();
			return true;
		} else {
			return false;
		}
	}
	
	public boolean checkIfBranchTrue(String val1, String val2) {
		if(Pipeline.registers.get(val1.toLowerCase()).getValue() == (Pipeline.registers.get(val2.toLowerCase()).getValue())) {
			return true;
		} else {
			return false;
		}
	}
	
	public void resetStage() {
		isBusy = false;
		counter = 1;
	}
	
	public void decodeInstructions(String instruction) {
		String[] data;
		if(instruction.contains(":")) {
			data = instruction.split(":");
			Decode.instruction.setLabel(data[0].trim());
//			Pipeline.labels.put(data[0].trim(), Pipeline.programCounter);
			setInstDetails(data[1]);
		} else {
			setInstDetails(instruction);
		}
	}
	
	public void setInstDetails(String data) {
		String[] s;
		if(data.trim().contains(", ") ) {
			s = data.trim().replaceAll(", "," ").split(" ");
		} else {
			s = data.trim().replaceAll(","," ").split(" ");
		}
//		String[] s = data.trim().replaceAll(", "," ").split(" ");
		if(s.length == 1) {
			Decode.instruction.setName(s[0]);
		} else if(s.length == 2) {
			Decode.instruction.setName(s[0].trim());
			Decode.instruction.setLabel(s[1].trim());
		} else if(s.length == 3) {
			Decode.instruction.setName(s[0].trim());
			Decode.instruction.setOp1(s[1].trim());
			String[] temp = s[2].replace("(", " ").replace(')', ' ').split(" ");
			Decode.instruction.setOffset(Integer.parseInt(temp[0]));
			Decode.instruction.setOp2(temp[1].trim());
		} else {
			Decode.instruction.setName(s[0].trim());
			Decode.instruction.setOp1(s[1].trim());
			Decode.instruction.setOp2(s[2].trim());
			if(s[0].toLowerCase().equals("bne") || s[0].toLowerCase().equals("beq")) {
				Decode.instruction.setLabel(s[3].trim());
			} else if(s[0].toLowerCase().contains("daddi") || s[0].toLowerCase().contains("dsubi")
					|| s[0].toLowerCase().contains("andi") || s[0].toLowerCase().contains("ori")) {
				Decode.instruction.setOffset(Integer.parseInt(s[3].trim()));
			} else {
				Decode.instruction.setOp3(s[3].trim());
			}
		}
	}
	
	public String getExecutionUnit(String instructionName) {
		if(instructionName.toLowerCase().equals("add.d")
				|| instructionName.toLowerCase().equals("sub.d")) {
			return "add";
		} else if(instructionName.toLowerCase().equals("mul.d")) {
			return "multiply";
		} else if(instructionName.toLowerCase().equals("div.d")) {
			return "divide";
		} else {
			return "iu";
		}
	}
	
	public boolean checkHazards() {
		String op1 = instruction.getOp1();
		String op2 = instruction.getOp2();
		String op3 = instruction.getOp3();
		
		if(op1 != null && Pipeline.registers.get(op1).isBeingWritten()) {
			String inst = instruction.getName();
			if(inst.equals("bne") || inst.equals("beq")
					|| inst.equals("sw") || inst.equals("s.d")) {
				instruction.setRAW(true);
			} else {
				instruction.setWAW(true);
			}
			return true;
		}
		
		if(op2 != null && Pipeline.registers.get(op2).isBeingWritten()) {
			instruction.setRAW(true);
			return true;
		}
		
		if(Pipeline.registers.containsKey(op3) && Pipeline.registers.get(op3).isBeingWritten()) {
			instruction.setRAW(true);
			return true;
		}
		
		return false;
	}
	
	public void updateRegisterValues() {
		String op1 = instruction.getOp1();
		String op2 = instruction.getOp2();
		String op3 = instruction.getOp3();
		
//		WAW Hazard
		if(op1 != null && !Pipeline.registers.get(op1).isBeingWritten()) {
			Register r = Pipeline.registers.get(op1);
			if(instruction.getName().equals("bne") 
					|| instruction.getName().equals("beq") 
					|| instruction.getName().equals("sw")
					|| instruction.getName().equals("s.d")) {
				r.setBeingRead(true);
			} else {
				r.setBeingWritten(true);
			}
			Pipeline.registers.put(op1, r);
		}
		
//		RAW Hazard
		if(op2 != null && !Pipeline.registers.get(op2).isBeingRead()) {
			Register r = Pipeline.registers.get(op2);
			r.setBeingRead(true);
			Pipeline.registers.put(op2, r);
		}

//		RAW Hazard
		if(op3 != null && Pipeline.registers.containsKey(op3) && !Pipeline.registers.get(op3).isBeingRead()) {
			Register r = Pipeline.registers.get(op3);
			r.setBeingRead(true);
			Pipeline.registers.put(op3, r);
		}
	}
}
