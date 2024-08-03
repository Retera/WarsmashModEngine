package com.etheller.interpreter.ast.expression;

public class ArrayRefJassExpression implements JassExpression {
	private final String identifier;
	private final JassExpression indexExpression;

	public ArrayRefJassExpression(final String identifier, final JassExpression indexExpression) {
		this.identifier = identifier;
		this.indexExpression = indexExpression;
	}

	@Override
	public <T> T accept(final JassExpressionVisitor<T> visitor) {
		return visitor.visit(this);
	}

	public String getIdentifier() {
		return this.identifier;
	}

	public JassExpression getIndexExpression() {
		return this.indexExpression;
	}

}
