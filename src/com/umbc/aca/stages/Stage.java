package com.umbc.aca.stages;

import com.umbc.aca.Instruction;

public interface Stage {
	
	public void init(Instruction inst);
	public void update();
	public boolean next();
	public void writeOutput();
}
