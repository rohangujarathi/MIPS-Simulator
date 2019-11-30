package com.umbc.aca.setup;

import java.io.IOException;

import com.umbc.aca.storage.Memory;
import com.umbc.aca.utils.AccessInfo;
import com.umbc.aca.utils.ExecutionUnitInfo;

public class InitializeData {
	
	public AccessInfo initAccessInfo(String[] config) {
		AccessInfo info = new AccessInfo();
		
		for(int i = 0; i<config.length; i++) {
			String data = config[i];
			if(data.toLowerCase().contains("adder")) {
				info.setAdder(splitForALU(data));
			} else if(data.toLowerCase().contains("multiplier")) {
				info.setMultiplier(splitForALU(data));
			} else if(data.toLowerCase().contains("divider")) {
				info.setDivider(splitForALU(data));
			} else if(data.toLowerCase().contains("memory")) {
				info.setMainMemoryCycles(splitForMemory(data));
			} else if(data.toLowerCase().contains("i-cache")) {
				info.setInstructionCacheCycles(splitForMemory(data));
			} else if(data.toLowerCase().contains("d-cache")) {
				info.setDataCacheCycles(splitForMemory(data));
			}
		}
		return info;
	}
	
	public int splitForMemory(String data) {
		return Integer.parseInt(data.split(":")[1].trim());
	}
	
	public ExecutionUnitInfo splitForALU(String data) {
		String[] info = data.split(":")[1].split(",");
		ExecutionUnitInfo exUnitInfo = new ExecutionUnitInfo();
		exUnitInfo.setCycleTime(Integer.parseInt(info[0].trim()));
		if(info[1].trim().toLowerCase().equals("yes")) {
			exUnitInfo.setPipeline(true);
		} else {
			exUnitInfo.setPipeline(false);
		}
		return exUnitInfo;
	}
	
//	public Memory initMemory(ParseFiles p) throws IOException {
//		Memory mem = new Memory();
////		mem.setDataSegment(p.parseData());
////		mem.setInstructionSegment(p.parseInstructions());
//		mem.instructionSegment = p.parseInstructions();
//		mem.dataSegment = p.parseData();
//		return mem;
//	}
	
	public void initMemory(ParseFiles p) throws IOException {
		Memory.instructionSegment = p.parseInstructions();
		Memory.dataSegment = p.parseData();
	}
}
