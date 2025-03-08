package com.etheller.interpreter.ast.expression;

public class NegateJassExpression implements JassExpression {
	private final JassExpression expression;

	public NegateJassExpression(final JassExpression expression) {
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
