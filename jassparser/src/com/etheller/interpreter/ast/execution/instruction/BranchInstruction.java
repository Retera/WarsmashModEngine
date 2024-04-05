package com.etheller.interpreter.ast.execution.instruction;

import com.etheller.interpreter.ast.execution.JassThread;

public class BranchInstruction implements JassInstruction {
	private final int newInstructionPointer;

	public BranchInstruction(final int newInstructionPointer) {
		this.newInstructionPointer = newInstructionPointer;
	}

	@Override
	public void run(final JassThread thread) {
		thread.instructionPtr = this.newInstructionPointer;
	}
}
