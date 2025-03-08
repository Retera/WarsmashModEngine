package com.etheller.interpreter.ast.execution.instruction;

import com.etheller.interpreter.ast.execution.JassThread;

public class GlobalAssignmentInstruction implements JassInstruction {
	private final int globalId;

	public GlobalAssignmentInstruction(final int globalId) {
		this.globalId = globalId;
	}

	@Override
	public void run(final JassThread thread) {
		thread.globalScope.getAssignableGlobalById(this.globalId).setValue(thread.stackFrame.pop());
	}

}
