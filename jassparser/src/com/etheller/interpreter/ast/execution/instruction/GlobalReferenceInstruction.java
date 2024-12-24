package com.etheller.interpreter.ast.execution.instruction;

import com.etheller.interpreter.ast.execution.JassThread;

public class GlobalReferenceInstruction implements JassInstruction {
	private final int globalId;

	public GlobalReferenceInstruction(final int globalId) {
		this.globalId = globalId;
	}

	@Override
	public void run(final JassThread thread) {
		thread.stackFrame.push(thread.globalScope.getGlobalById(this.globalId));
	}

}
