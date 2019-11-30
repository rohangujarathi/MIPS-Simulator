package com.umbc.aca.storage;

import com.umbc.aca.Pipeline;
import com.umbc.aca.stages.Fetch;
import com.umbc.aca.utils.AccessInfo;

public class InstructionCache {
	public int[] validBit = {0,0,0,0};
	public int[] tag = new int[4];
//	data = [blocknumber][offset]
	public String[][] data = new String[4][4];
	public final int blockSize = 4;
	public final int noOfCacheBlocks = 4;
	
	public String getInstruction(String memoryLocation) {
		int offset = Integer.parseInt(memoryLocation.substring(6), 2);
		int tag = Integer.parseInt(memoryLocation.substring(0,  4), 2);
		int blockAddress = getBlockAddress(memoryLocation);
		int index = getCacheBlockAddress(blockAddress);
		Pipeline.iCacheAccess++;
		if(validBit[index] == 1 && this.tag[index] == tag) {
			//HIT case
			Pipeline.iCacheHits++;
			Fetch.counter = AccessInfo.instructionCacheCycles;
			return this.data[index][offset];
		} else {
			//MISS case
			Fetch.counter = 2 * (AccessInfo.mainMemoryCycles + AccessInfo.instructionCacheCycles);
			validBit[index] = 1;
			this.tag[index] = tag;
			//Fetching from memory
			int firstFetchIndex = Integer.parseInt(memoryLocation, 2) - offset;
			for(int i = 0; i<blockSize; i++) {
				if(!(firstFetchIndex >= Memory.instructionSegment.length)) {
					this.data[index][i] = Memory.instructionSegment[firstFetchIndex];
					firstFetchIndex++;
				}
			}
			return this.data[index][offset];
		}	
	}
	
	public int getBlockAddress(String memoryLocation) {
		return Integer.parseInt(memoryLocation, 2) / blockSize;
	}
	
	public int getCacheBlockAddress(int blockAddress) {
		return blockAddress % noOfCacheBlocks;
	}
}
