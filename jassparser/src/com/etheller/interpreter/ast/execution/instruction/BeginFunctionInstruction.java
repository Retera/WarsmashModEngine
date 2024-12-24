package com.etheller.interpreter.ast.execution.instruction;

import com.etheller.interpreter.ast.execution.JassThread;

public class BeginFunctionInstruction implements JassInstruction {
	private final int lineNo;
	private final String sourceFile;
	private final String name;

	public BeginFunctionInstruction(final int lineNo, final String sourceFile, final String name) {
		this.lineNo = lineNo;
		this.sourceFile = sourceFile;
		this.name = name;
	}

	@Override
	public void run(final JassThread thread) {
		// This is a tomb stone instruction giving us function info for later maybe
		if (thread.stackFrame == null) {
			throw new IllegalStateException();
		}
		thread.stackFrame.functionNameMetaData = this;

//		final JassStackFrame jassStackFrame = new JassStackFrame();
//		jassStackFrame.functionNameMetaData = this.functionNameMetaData;
//		jassStackFrame.returnAddress = thread.stackFrame;
//		thread.stackFrame = jassStackFrame;
	}

	public int getLineNo() {
		return this.lineNo;
	}

	public String getSourceFile() {
		return this.sourceFile;
	}

	public String getName() {
		return this.name;
	}

	@Override
	public String toString() {
		return "BeginFunctionInstruction [lineNo=" + this.lineNo + ", sourceFile=" + this.sourceFile + ", name="
				+ this.name + "]";
	}

}
