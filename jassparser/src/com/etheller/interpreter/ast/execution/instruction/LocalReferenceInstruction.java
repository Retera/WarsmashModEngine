package com.etheller.interpreter.ast.execution.instruction;

import com.etheller.interpreter.ast.execution.JassThread;

public class LocalReferenceInstruction implements JassInstruction {
	private final int localId;

	public LocalReferenceInstruction(final int localId) {
		this.localId = localId;
	}

	@Override
	public void run(final JassThread thread) {
		thread.stackFrame.push(thread.stackFrame.contents.get(this.localId));
	}

}
