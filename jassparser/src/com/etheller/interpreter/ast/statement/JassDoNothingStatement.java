package com.etheller.interpreter.ast.statement;

public class JassDoNothingStatement implements JassStatement {

	@Override
	public <T> T accept(final JassStatementVisitor<T> visitor) {
		return visitor.visit(this);
	}

}
