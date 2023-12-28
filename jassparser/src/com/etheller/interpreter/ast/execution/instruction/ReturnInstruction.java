package com.etheller.interpreter.ast.execution.instruction;

import com.etheller.interpreter.ast.execution.JassStackFrame;
import com.etheller.interpreter.ast.execution.JassThread;
import com.etheller.interpreter.ast.value.JassValue;

public class ReturnInstruction implements JassInstruction {
	public static final ReturnInstruction INSTANCE = new ReturnInstruction();

	@Override
	public void run(final JassThread thread) {
		final JassStackFrame finishingStackFrame = thread.stackFrame;
		final JassValue valueToReturn = finishingStackFrame.pop();
		thread.stackFrame = finishingStackFrame.stackBase;
		thread.instructionPtr = finishingStackFrame.returnAddressInstructionPtr;
		if (thread.stackFrame != null) {
			thread.stackFrame.push(valueToReturn);
		}
	}

}
