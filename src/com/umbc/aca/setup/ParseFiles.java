package com.umbc.aca.setup;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.stream.Stream;

public class ParseFiles {
	/**
	 * Parses all the files, converts them into an array and returns the array
	 */
	Config cfg;
	public ParseFiles() {
		cfg = new Config();
	}
	
	public String[] parseInstructions() throws IOException {
		Stream<String> instructions = readFile(cfg.getProperty("InstructionsFileName"));
		return instructions.toArray(String[]::new);
	}
	
	public String[] parseConfig() throws IOException {
		Stream<String> config = readFile(cfg.getProperty("ConfigFileName"));
		return config.toArray(String[]::new);
	}
	
	public int[] parseData() throws IOException{
		Stream<String> registers = readFile(cfg.getProperty("DataFileName"));
		return registers.
				mapToInt(s -> Integer.parseInt(s, 2))
				.toArray();
	}
	
	public int[] parseRegisters() throws IOException {
		Stream<String> registers = readFile(cfg.getProperty("RegistersFileName"));
		return registers.
				mapToInt(s -> Integer.parseInt(s, 2))
				.toArray();
	}
	
	public Stream<String> readFile(String fileName) throws IOException {
		Stream<String> data = Files.lines(new File(fileName)
				.toPath())
			.map(s -> s.trim())
			.filter(s -> !s.isEmpty());
		return data;
	}
}
