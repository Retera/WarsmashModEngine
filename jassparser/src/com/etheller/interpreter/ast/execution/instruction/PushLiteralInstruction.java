package com.etheller.interpreter.ast.execution.instruction;

import com.etheller.interpreter.ast.execution.JassThread;
import com.etheller.interpreter.ast.value.JassValue;

public class PushLiteralInstruction implements JassInstruction {
	private final JassValue value;

	public PushLiteralInstruction(final JassValue value) {
		this.value = value;
	}

	@Override
	public void run(final JassThread thread) {
		thread.stackFrame.push(this.value);
	}

}
