package com.umbc.aca;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import com.umbc.aca.setup.InitializeData;
import com.umbc.aca.setup.ParseFiles;
import com.umbc.aca.stages.Decode;
import com.umbc.aca.stages.ExecuteAdder;
import com.umbc.aca.stages.ExecuteDivider;
import com.umbc.aca.stages.ExecuteIU;
import com.umbc.aca.stages.ExecuteMultiplier;
import com.umbc.aca.stages.Fetch;
import com.umbc.aca.stages.Mem;
import com.umbc.aca.stages.WriteBack;
import com.umbc.aca.storage.DataCache;
import com.umbc.aca.storage.InstructionCache;
import com.umbc.aca.storage.Memory;
import com.umbc.aca.utils.AccessInfo;
import com.umbc.aca.utils.Register;

public class Pipeline {
	
	public static Memory memory;
	public static AccessInfo accessInfo;
	public static Map<String, Register> registers = new HashMap<String, Register>();
	public static int cycleCount = 0;
	public static int programCounter = 0;
	public static List<Instruction> inst = new ArrayList<Instruction>();
	public static Map<String, Integer> labels = new HashMap<>();
	public static Fetch fetch;
	public static Decode decode;
	public static ExecuteIU executeIU;
	public static List<ExecuteAdder> adderList;
	public static List<ExecuteMultiplier> multiplierList;
	public static List<ExecuteDivider> dividerList;
	public static Mem memStage;
	public static WriteBack writebackStage;
	public static InstructionCache instructionCache;
	public static int iCacheAccess = 0;
	public static int iCacheHits = 0;
	public static DataCache dataCache;
	public static int dCacheAccess = 0;
	public static int dCacheHits = 0;
	
	public Pipeline() {
		try {
			InitializeData initialize = new InitializeData();
			ParseFiles p = new ParseFiles();
//			Pipeline.memory = initialize.initMemory(p);
			initialize.initMemory(p);
			Memory.instructionSegment = removeLabels(Memory.instructionSegment);
			Pipeline.accessInfo = initialize.initAccessInfo(p.parseConfig());
			int[] tempRegList = p.parseRegisters();
			for(int i = 0; i < tempRegList.length; i++) {
				Register r = new Register();
				r.setBeingRead(false);
				r.setBeingRead(false);
				r.setValue(tempRegList[i]);
				String regName = "r" + i;
				Pipeline.registers.put(regName, r);
			}
			
			for(int j = 0; j < 32; j++ ) {
				Register r = new Register();
				r.setBeingRead(false);
				r.setBeingRead(false);
				r.setValue(0);
				String regName = "f" + j;
				Pipeline.registers.put(regName, r);
			}
			
			instructionCache = new InstructionCache();
			dataCache = new DataCache();
			
			fetch = new Fetch();
			decode = new Decode();
			executeIU = new ExecuteIU();
			adderList = new CopyOnWriteArrayList<>();
			multiplierList = new CopyOnWriteArrayList<>();
			dividerList = new CopyOnWriteArrayList<>();
			memStage = new Mem();
			writebackStage = new WriteBack();
		} catch(Exception e) {
			e.printStackTrace();
			System.out.println(e);
		}
	}
	
	public void updateCycles() {
		writebackStage.update();
		for(Iterator<ExecuteDivider> iterator = dividerList.iterator(); iterator.hasNext();) {
			iterator.next().update();
		}
		for(Iterator<ExecuteMultiplier> iterator = multiplierList.iterator(); iterator.hasNext();) {
			iterator.next().update();
		}
		for(Iterator<ExecuteAdder> iterator = adderList.iterator(); iterator.hasNext();) {
			iterator.next().update();
		}
		memStage.update();
		executeIU.update();
		decode.update();
		fetch.update();
		cycleCount++;
	}
	
	public void completeOnGoingOperations() {
		while(dividerList.size()>0 || multiplierList.size()>0 || adderList.size()>0 
				|| ExecuteIU.isBusy || Mem.isBusy || WriteBack.isBusy || Fetch.isBusy) {
			updateCycles();
		}
	}
	
	public static void main(String[] args) {
		 
		Pipeline pipeline = new Pipeline();

		while(Pipeline.programCounter != -1) {
			pipeline.updateCycles();
		}
		pipeline.completeOnGoingOperations();
		
		inst.sort(Comparator.comparing(Instruction::getIF));
		
		System.out.println("Instruction \t\t IF \t ID \t EX \t WB \t RAW \t WAR \t WAW \t Struct \t");
		for(int i = 0; i<Pipeline.inst.size(); i++) {
			Instruction in = Pipeline.inst.get(i);
			System.out.format("%-25s%-8s%-8s%-8s%-9s%-8s%-9s%-8s%s\n", in.getInstruction().trim().toUpperCase(),
					formatInteger(in.getIF()), formatInteger(in.getID()),
					formatInteger(in.getEX()), formatInteger(in.getWB()),
					formatBoolean(in.isRAW()), formatBoolean(in.isWAR()),
					formatBoolean(in.isWAW()), formatBoolean(in.isSTRUCT()));
		}
		
		System.out.println("\n");
		System.out.println("Total number of access requests for instruction cache: " + Pipeline.iCacheAccess);
		System.out.println("Number of instruction cache hits: " + Pipeline.iCacheHits);
		System.out.println("Total number of access requests for data cache: " + Pipeline.dCacheAccess);
		System.out.println("Number of data cache hits: " + Pipeline.dCacheHits);
		
		try {
			pipeline.writeResult();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
	
	public void writeResult() throws IOException {
		List<String> lines = new ArrayList<>();
		lines.add("Instruction \t\t IF \t ID \t EX \t WB \t RAW \t WAR \t WAW \t Struct \t");
		for(int i = 0; i<Pipeline.inst.size(); i++) {
			Instruction in = Pipeline.inst.get(i);
			if(in.getName().equals("hlt") && in.getID()==0) {
				lines.add(String.format("%-25s", in.getInstruction().trim().toUpperCase()) 
						+ String.format("%-8s", formatInteger(in.getIF())) 
						+ String.format("%-8s", formatInteger(in.getID())) 
						+ String.format("%-8s", formatInteger(in.getEX())) 
						+ String.format("%-9s", formatInteger(in.getWB()))
						+ String.format("%-8s", "") 
						+ String.format("%-9s", "") 
						+ String.format("%-8s", "") 
						+ String.format("%-8s", ""));
			} else {
				lines.add(String.format("%-25s", in.getInstruction().trim().toUpperCase()) 
						+ String.format("%-8s", formatInteger(in.getIF())) 
						+ String.format("%-8s", formatInteger(in.getID())) 
						+ String.format("%-8s", formatInteger(in.getEX())) 
						+ String.format("%-9s", formatInteger(in.getWB()))
						+ String.format("%-8s", formatBoolean(in.isRAW())) 
						+ String.format("%-9s", formatBoolean(in.isWAR())) 
						+ String.format("%-8s", formatBoolean(in.isWAW())) 
						+ String.format("%-8s", formatBoolean(in.isSTRUCT())));
			}
		}
		lines.add("\n");
		lines.add("Total number of access requests for instruction cache: " + Pipeline.iCacheAccess);
		lines.add("Number of instruction cache hits: " + Pipeline.iCacheHits);
		lines.add("Total number of access requests for data cache: " + Pipeline.dCacheAccess);
		lines.add("Number of data cache hits: " + Pipeline.dCacheHits);
		Path file = Paths.get("Result.txt");
		Files.write(file, lines, StandardCharsets.UTF_8);
	}
	
	
	public static String formatBoolean(boolean value) {
		if(value) {
			return "Y";
		} else {
			return "N";	
		}
	}
	
	public static String formatInteger(int value) {
		if(value == 0) {
			return " ";
		} else {
			return Integer.toString(value);
		}
	}
	
	public static void printRegisters(Map<String, Register> registers) {
		for (Map.Entry<String,Register> entry : registers.entrySet()) {  
            System.out.println("Key = " + entry.getKey() + 
                             ", Value = " + entry.getValue().getValue()); 
		}
	}
	
	public String[] removeLabels(String[] instructions) {
		String[] newInstructions = new String[instructions.length];
		for(int i = 0; i<instructions.length; i++) {
			String[] s = instructions[i].split(":");
			if(s.length>1) {
				labels.put(s[0].trim().toLowerCase(), i);
				newInstructions[i] = s[1];
			} else {
				newInstructions[i] = s[0];
			}
		}
		return newInstructions;
	}
}
