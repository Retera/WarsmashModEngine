package com.etheller.interpreter.ast.execution.instruction;

import com.etheller.interpreter.ast.execution.JassThread;

public class PopInstruction implements JassInstruction {
	public static final PopInstruction INSTANCE = new PopInstruction();

	@Override
	public void run(final JassThread thread) {
		thread.stackFrame.pop();
	}

}
