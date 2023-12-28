package com.etheller.interpreter.ast.execution.instruction;

import com.etheller.interpreter.ast.execution.JassStackFrame;
import com.etheller.interpreter.ast.execution.JassThread;

public class NewStackFrameInstruction implements JassInstruction {

	@Override
	public void run(final JassThread thread) {
		final JassStackFrame jassStackFrame = new JassStackFrame();
		jassStackFrame.stackBase = thread.stackFrame;
		thread.stackFrame = jassStackFrame;
	}

}
