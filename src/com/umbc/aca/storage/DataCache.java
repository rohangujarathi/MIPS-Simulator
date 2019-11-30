package com.umbc.aca.storage;

import com.umbc.aca.Pipeline;
import com.umbc.aca.utils.AccessInfo;

public class DataCache {
	
//	public int[][] validBit = {{0,0}, {0,0}};			//	validbit = [set][block]
//	public int[][] tag = new int[2][2];  				//	tag = [set][block]
	public int[][] lruFlag = {{0,0}, {0,0}};  			//	LRUFlag = [set][block]
	public int[][][] data = new int[2][2][4];   		//	data = [set][block][offset]
//	public int[][][] locationTags = new int[2][2][4];	//	locationTags = [set][block][offset]
	public int[][][] locationTags = {{{-1,-1, -1, -1}, {-1, -1, -1,-1}}, {{-1, -1, -1,-1}, {-1, -1, -1,-1}}};
	public boolean[][] dirtyBit = new boolean[2][2];			//  dirtybit = [set][block]
	public final int wordPerBlock = 4;
	public final int numberOfSets = 4;
	public boolean miss = false;
	public int cycles = 0;

	public int getData(int memoryLocation) {

		String stringLocation = convertToBinary(memoryLocation);
		int integerLocation = Integer.parseInt(stringLocation, 2);
		int setNumber = getSetNumber(stringLocation);
		
		Pipeline.dCacheAccess++;
		
		for(int block = 0; block<locationTags[setNumber].length; block++) {
			for(int off = 0; off<locationTags[setNumber][block].length; off++) {
				if(locationTags[setNumber][block][off] == integerLocation) {
					// Congrats....Its a hit
					return handleHit(setNumber, block, off);
				}
			}
		}
		
		int offset = Integer.parseInt(stringLocation.substring(stringLocation.length() - 2), 2); // last 2 digits
		return handleMiss(setNumber, integerLocation, offset);
	}
	
	public void storeData(int memoryLocation, int regValue) {
		
		Pipeline.dCacheAccess++;
//		String stringLocation = Integer.toBinaryString(memoryLocation);
		String stringLocation = convertToBinary(memoryLocation);
		int setNumber = getSetNumber(stringLocation);
		
		// HIT CASE
		for(int set = 0; set<locationTags.length; set++) {
			for(int block = 0; block<locationTags[set].length; block++) {
				for(int index = 0; index<locationTags[set][block].length; index++) {
					if(locationTags[set][block][index] == memoryLocation) {
						Pipeline.dCacheHits++;
						this.cycles = AccessInfo.dataCacheCycles;
						data[set][block][index] = regValue;
						dirtyBit[set][block] = true;
						lruFlag[set][block] = getmax(lruFlag[set]) + 1;
						return;
					}
				}
			}
		}
		
		if(regValue != -1) {
			Memory.dataSegment[memoryLocation] = regValue;
		}
		
		// To handle store double case while miss
//		if(regValue == -1) {
//			miss = false;
//			this.cycles = AccessInfo.mainMemoryCycles + AccessInfo.dataCacheCycles;
//			return;
//		}
		
		
		//MISS CASE
//		Memory.dataSegment[memoryLocation] = regValue;
		int offset = Integer.parseInt(stringLocation.substring(stringLocation.length() - 2), 2); // last 2 digits
		handleMiss(setNumber, memoryLocation, offset);
	}
	
	public int getSetNumber(String memoryLocation) {
		int blockAddress = Integer.parseInt(memoryLocation, 2) / wordPerBlock;
		int setNumber = blockAddress % numberOfSets;
		return setNumber;
	}
	
	public int getmax(int[] flags) {
		int max = 0;
		for(int i = 0; i<flags.length; i++) {
			if(flags[i] > max) {
				max = flags[i];
			}
		}
		return max;
	}
	
	public int getMin(int[] flags) {
		int min = 0;
		int minlocation = 0;
		for(int i = 0; i<flags.length; i++) {
			if(flags[i] < min) {
				min = flags[i];
				minlocation = i;
			}
		}
		return minlocation;
	}
	
	public int handleHit(int setNumber, int block, int offset) {
		Pipeline.dCacheHits++;
		this.cycles = AccessInfo.dataCacheCycles;
		int max = getmax(lruFlag[setNumber]);
		lruFlag[setNumber][block] = max + 1;
		return data[setNumber][block][offset];
	}
	
	public int handleMiss(int setNumber, int integerLocation, int offset) {
		this.cycles = AccessInfo.mainMemoryCycles + AccessInfo.dataCacheCycles;
		this.miss = true;
		int block = getMin(lruFlag[setNumber]);
		int max = getmax(lruFlag[setNumber]);
		int fetchIndex = integerLocation - offset;
		lruFlag[setNumber][block] = max + 1;
		boolean dirty = dirtyBit[setNumber][block];
		for(int i = 0; i<wordPerBlock; i++) {
			if(dirty) {
				int storeIndex = this.locationTags[setNumber][block][i];
				Memory.dataSegment[storeIndex] = this.data[setNumber][block][i];
			}
			if(!(fetchIndex >= Memory.dataSegment.length)) {
				this.data[setNumber][block][i] = Memory.dataSegment[fetchIndex];
				this.locationTags[setNumber][block][i] = fetchIndex;
				fetchIndex++;
			}
		}
		if(dirty) {
			dirtyBit[setNumber][block] = false;
		}
		return data[setNumber][block][offset];
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
	
	public void printCacheData() {
		for(int i = 0; i<data.length; i++) {
			for(int j = 0; j<data[i].length; j++) {
				for(int k = 0; k<data[i][j].length; k++) {
					System.out.print(data[i][j][k] + " ");
				}
			}
		}
	}
}
