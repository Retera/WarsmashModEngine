package com.etheller.interpreter.ast.expression;

public class ArithmeticJassExpression implements JassExpression {

	private final JassExpression leftExpression;
	private final JassExpression rightExpression;
	private final ArithmeticSign arithmeticSign;

	public ArithmeticJassExpression(final JassExpression leftExpression, final JassExpression rightExpression,
			final ArithmeticSign arithmeticSign) {
		this.leftExpression = leftExpression;
		this.rightExpression = rightExpression;
		this.arithmeticSign = arithmeticSign;
	}

	@Override
	public <T> T accept(final JassExpressionVisitor<T> visitor) {
		return visitor.visit(this);
	}

	public JassExpression getLeftExpression() {
		return this.leftExpression;
	}

	public JassExpression getRightExpression() {
		return this.rightExpression;
	}

	public ArithmeticSign getArithmeticSign() {
		return this.arithmeticSign;
	}
}
