package com.etheller.interpreter.ast.expression;

public class MethodReferenceJassExpression implements JassExpression {
	private final JassExpression structExpression;
	private final String identifier;

	public MethodReferenceJassExpression(final JassExpression structExpression, final String identifier) {
		this.structExpression = structExpression;
		this.identifier = identifier;
	}

	@Override
	public <T> T accept(final JassExpressionVisitor<T> visitor) {
		return visitor.visit(this);
	}

	public JassExpression getStructExpression() {
		return this.structExpression;
	}

	public String getIdentifier() {
		return this.identifier;
	}
}
