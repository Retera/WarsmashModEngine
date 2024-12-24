package com.etheller.interpreter.ast.execution.instruction;

import com.etheller.interpreter.ast.execution.JassThread;

public class DoNothingInstruction implements JassInstruction {
	public static final DoNothingInstruction INSTANCE = new DoNothingInstruction();

	// this was created to map directly to `DoNothingStatement` in the existing
	// code, but off the top of my head I'm forgetting why that even exists.
	// Feel free to remove this class if you later deem it to be unnecessary

	@Override
	public void run(final JassThread thread) {
	}

}
