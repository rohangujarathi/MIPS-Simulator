package com.umbc.aca.stages;

import com.umbc.aca.Instruction;
import com.umbc.aca.Pipeline;

public class ExecuteMultiplier implements Stage {
	
	public static boolean isBusy = false;
	public static boolean isPipeline = Pipeline.accessInfo.getMultiplier().isPipeline();
	public int counter = Pipeline.accessInfo.getMultiplier().getCycleTime();
	public Instruction instruction;
	
	@Override
	public void init(Instruction inst) {
		// TODO Auto-generated method stub
		if(!isBusy) {
			if((isPipeline && Pipeline.adderList.size()==(counter-1)) || (!isPipeline)) {
				isBusy = true;
			}
			this.instruction = inst;
			this.execute();
			this.counter--;
		}	
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		if(this.counter==0) {
			if(this.next()) {
				Pipeline.multiplierList.remove(0);
				isBusy = false;
			}
		} else {
			this.counter--;
		}
	}

	@Override
	public boolean next() {
		// TODO Auto-generated method stub
		if(!WriteBack.isBusy ) {
			writeOutput();
			Pipeline.writebackStage.init(this.instruction);
			return true;
		} else {
			this.instruction.setSTRUCT(true);
			return false;
		}
	}

	@Override
	public void writeOutput() {
		// TODO Auto-generated method stub
		this.instruction.setEX(Pipeline.cycleCount);
//		Pipeline.inst.add(this.instruction);
	}
	
	public void execute() {
		
	}
	
	
}
