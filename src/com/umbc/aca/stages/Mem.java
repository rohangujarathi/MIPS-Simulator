package com.umbc.aca.stages;

import com.umbc.aca.Instruction;
import com.umbc.aca.Pipeline;

public class Mem implements Stage {

	public static boolean isBusy = false;
//	public static int counter = Pipeline.accessInfo.getMainMemoryCycles();
	public static int counter = 0;
	public static Instruction instruction;
	
	@Override
	public void init(Instruction inst) {
		// TODO Auto-generated method stub
		if(!isBusy) {
			isBusy = true;
			Mem.instruction = inst;
			execute();
			counter--;
		}
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		if(counter==0 && isBusy) {
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
		if(!WriteBack.isBusy ) {
			writeOutput();
			Pipeline.writebackStage.init(Mem.instruction);
			return true;
		} else {
			instruction.setSTRUCT(true);
			return false;
		}
	}

	@Override
	public void writeOutput() {
		// TODO Auto-generated method stub
		Mem.instruction.setEX(Pipeline.cycleCount);
	}
	
	public void resetStage() {
		isBusy = false;
		counter = 0;
	}

	public void execute() {
		String ins = Mem.instruction.getName();
		
		if(ins.equals("lw")) {
			int data = Pipeline.dataCache.getData((instruction.getData() - 256) / 4);
			resetDataCacheCycles();
			instruction.setData(data);
		} else if(ins.equals("l.d")) {
			Pipeline.dataCache.getData((instruction.getData() - 256) / 4);
			resetDataCacheCycles();
			Pipeline.dataCache.getData((instruction.getData() + 4 - 256) / 4);
			resetDataCacheCycles();
		} else if(ins.equals("sw")) {
			Pipeline.dataCache.storeData((instruction.getData() - 256) / 4, Pipeline.registers.get(instruction.getOp1()).getValue());
			resetDataCacheCycles();
		} else if(ins.equals("s.d")) {
			Pipeline.dataCache.storeData((instruction.getData() - 256) / 4, -1);
			resetDataCacheCycles();
			Pipeline.dataCache.storeData((instruction.getData() + 4 - 256) / 4, -1);
			resetDataCacheCycles();
		} else {
			Mem.counter = 1;
		}
	}
	
	public void resetDataCacheCycles() {
		if(Pipeline.dataCache.miss) {
			counter = counter +  (2 * Pipeline.dataCache.cycles);
		} else {
			counter = counter + Pipeline.dataCache.cycles;
		}
		Pipeline.dataCache.cycles = 0;
		Pipeline.dataCache.miss = false;
	}
}
