package com.etheller.interpreter.ast.expression;

public class NotJassExpression implements JassExpression {
	private final JassExpression expression;

	public NotJassExpression(final JassExpression expression) {
		this.expression = expression;
	}

	@Override
	public <T> T accept(final JassExpressionVisitor<T> visitor) {
		return visitor.visit(this);
	}

	public JassExpression getExpression() {
		return this.expression;
	}
}
