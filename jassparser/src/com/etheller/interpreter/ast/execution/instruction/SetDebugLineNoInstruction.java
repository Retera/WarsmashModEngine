package com.etheller.interpreter.ast.execution.instruction;

import com.etheller.interpreter.ast.execution.JassThread;

public class SetDebugLineNoInstruction implements JassInstruction {
	private final int lineNo;

	public SetDebugLineNoInstruction(final int lineNo) {
		this.lineNo = lineNo;
	}

	@Override
	public void run(final JassThread thread) {
		thread.stackFrame.debugLineNo = this.lineNo;
	}

}
