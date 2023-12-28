package com.etheller.interpreter.ast.execution.instruction;

import com.etheller.interpreter.ast.execution.JassThread;

public class BeginFunctionInstruction implements JassInstruction {
	private final String functionNameMetaData;

	public BeginFunctionInstruction(final int lineNo, final String sourceFile, final String name) {
		this.functionNameMetaData = name + ":(" + sourceFile + ": " + lineNo + ")";
	}

	@Override
	public void run(final JassThread thread) {
		// This is a tomb stone instruction giving us function info for later maybe
		thread.stackFrame.functionNameMetaData = this.functionNameMetaData;

//		final JassStackFrame jassStackFrame = new JassStackFrame();
//		jassStackFrame.functionNameMetaData = this.functionNameMetaData;
//		jassStackFrame.returnAddress = thread.stackFrame;
//		thread.stackFrame = jassStackFrame;
	}

}
