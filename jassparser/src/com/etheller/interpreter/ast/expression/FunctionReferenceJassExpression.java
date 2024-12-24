package com.etheller.interpreter.ast.expression;

public class FunctionReferenceJassExpression implements JassExpression {
	private final String identifier;

	public FunctionReferenceJassExpression(final String identifier) {
		this.identifier = identifier;
	}

	@Override
	public <T> T accept(final JassExpressionVisitor<T> visitor) {
		return visitor.visit(this);
	}

	public String getIdentifier() {
		return this.identifier;
	}
}
