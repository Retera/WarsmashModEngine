package com.etheller.interpreter.ast.execution.instruction;

import com.etheller.interpreter.ast.execution.JassThread;

public class SetReturnAddrInstruction implements JassInstruction {
	private final int returnAddressInstructionPtr;

	public SetReturnAddrInstruction(final int returnAddressInstructionPtr) {
		this.returnAddressInstructionPtr = returnAddressInstructionPtr;
	}

	@Override
	public void run(final JassThread thread) {
		thread.stackFrame.returnAddressInstructionPtr = this.returnAddressInstructionPtr;
	}

}
