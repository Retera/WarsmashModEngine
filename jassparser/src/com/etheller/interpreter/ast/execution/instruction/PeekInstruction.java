package com.etheller.interpreter.ast.execution.instruction;

import com.etheller.interpreter.ast.execution.JassThread;

public class PeekInstruction implements JassInstruction {
	public static final PeekInstruction INSTANCE = new PeekInstruction();

	@Override
	public void run(final JassThread thread) {
		thread.stackFrame.push(thread.stackFrame.peek());
	}

}
