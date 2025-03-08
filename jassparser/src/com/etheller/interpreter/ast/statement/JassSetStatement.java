package com.etheller.interpreter.ast.statement;

import com.etheller.interpreter.ast.expression.JassExpression;

public class JassSetStatement implements JassStatement {
	private final String identifier;
	private final JassExpression expression;

	public JassSetStatement(final String identifier, final JassExpression expression) {
		this.identifier = identifier;
		this.expression = expression;
	}

	@Override
	public <T> T accept(final JassStatementVisitor<T> visitor) {
		return visitor.visit(this);
	}

	public String getIdentifier() {
		return this.identifier;
	}

	public JassExpression getExpression() {
		return this.expression;
	}
}
