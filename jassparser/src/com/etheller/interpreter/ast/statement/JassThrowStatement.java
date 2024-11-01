package com.etheller.interpreter.ast.statement;

public class JassThrowStatement implements JassStatement {

	private final int lineNo;
	private final String sourceFile;
	private final String exceptionMessage;

	public JassThrowStatement(final int lineNo, final String sourceFile, final String exceptionMessage) {
		this.lineNo = lineNo;
		this.sourceFile = sourceFile;
		this.exceptionMessage = exceptionMessage;
	}

	@Override
	public <T> T accept(final JassStatementVisitor<T> visitor) {
		return visitor.visit(this);
	}

	public int getLineNo() {
		return this.lineNo;
	}

	public String getSourceFile() {
		return this.sourceFile;
	}

	public String getExceptionMessage() {
		return this.exceptionMessage;
	}

}
