package com.etheller.interpreter.ast.expression;

public class ArrayRefJassExpression implements JassExpression {
	private final JassExpression identifierExpression;
	private final JassExpression indexExpression;

	public ArrayRefJassExpression(final JassExpression identifierExpression, final JassExpression indexExpression) {
		this.identifierExpression = identifierExpression;
		this.indexExpression = indexExpression;
	}

	@Override
	public <T> T accept(final JassExpressionVisitor<T> visitor) {
		return visitor.visit(this);
	}

	public JassExpression getIdentifierExpression() {
		return this.identifierExpression;
	}

	public JassExpression getIndexExpression() {
		return this.indexExpression;
	}

}
