package com.etheller.interpreter.ast.expression;

import com.etheller.interpreter.ast.value.JassValue;

public class LiteralJassExpression implements JassExpression {
	private final JassValue value;

	public LiteralJassExpression(final JassValue value) {
		this.value = value;
	}

	@Override
	public <T> T accept(final JassExpressionVisitor<T> visitor) {
		return visitor.visit(this);
	}

	public JassValue getValue() {
		return this.value;
	}

}
