package com.etheller.interpreter.ast.debug;

public class JassStackElement {
	private final String sourceFile;
	private final String functionName;
	private int lineNumber;

	public JassStackElement(final String sourceFile, final String functionName, final int lineNumber) {
		this.sourceFile = sourceFile;
		this.functionName = functionName;
		this.lineNumber = lineNumber;
	}

	public JassStackElement(final JassStackElement other) {
		this(other.sourceFile, other.functionName, other.lineNumber);
	}

	public String getSourceFile() {
		return this.sourceFile;
	}

	public String getFunctionName() {
		return this.functionName;
	}

	public int getLineNumber() {
		return this.lineNumber;
	}

	public void setLineNumber(final int lineNumber) {
		this.lineNumber = lineNumber;
	}

	@Override
	public String toString() {
		return "\tat " + this.functionName + "(" + this.sourceFile + ":" + +this.lineNumber + ")";
	}
}
