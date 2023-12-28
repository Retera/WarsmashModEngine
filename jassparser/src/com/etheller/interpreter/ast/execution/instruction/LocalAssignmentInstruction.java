package com.etheller.interpreter.ast.execution.instruction;

import com.etheller.interpreter.ast.execution.JassThread;

public class LocalAssignmentInstruction implements JassInstruction {
	private final int localId;

	public LocalAssignmentInstruction(final int localId) {
		this.localId = localId;
	}

	@Override
	public void run(final JassThread thread) {
		thread.stackFrame.contents.set(this.localId, thread.stackFrame.pop());
	}

}
