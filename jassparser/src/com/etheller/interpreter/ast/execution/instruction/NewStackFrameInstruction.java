package com.etheller.interpreter.ast.execution.instruction;

import com.etheller.interpreter.ast.execution.JassStackFrame;
import com.etheller.interpreter.ast.execution.JassThread;

public class NewStackFrameInstruction implements JassInstruction {
	private final int returnAddressInstructionPtr;
	private final int argumentCount;

	public NewStackFrameInstruction(final int returnAddressInstructionPtr, final int argumentCount) {
		this.returnAddressInstructionPtr = returnAddressInstructionPtr;
		this.argumentCount = argumentCount;
	}

	@Override
	public void run(final JassThread thread) {
		final JassStackFrame baseStackFrame = thread.stackFrame;
		final JassStackFrame jassStackFrame = new JassStackFrame();
		jassStackFrame.stackBase = baseStackFrame;
		jassStackFrame.returnAddressInstructionPtr = this.returnAddressInstructionPtr;
		for (int i = 0; i < this.argumentCount; i++) {
			jassStackFrame.push(baseStackFrame.pop());
		}
		thread.stackFrame = jassStackFrame;
	}

}
