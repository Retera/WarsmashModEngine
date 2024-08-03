package com.etheller.interpreter.ast.debug;

import com.etheller.interpreter.ast.statement.JassStatement;
import com.etheller.interpreter.ast.statement.JassStatementVisitor;

public class DebuggingJassStatement implements JassStatement {
	private final int lineNo;
	private final JassStatement delegate;

	public DebuggingJassStatement(final int lineNo, final JassStatement delegate) {
		this.lineNo = lineNo;
		this.delegate = delegate;
	}

	@Override
	public <T> T accept(final JassStatementVisitor<T> visitor) {
		return visitor.visit(this);
	}

	public int getLineNo() {
		return this.lineNo;
	}

	public JassStatement getDelegate() {
		return this.delegate;
	}

}
