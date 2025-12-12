package com.etheller.interpreter.ast.expression;

import com.etheller.interpreter.ast.value.ArrayJassType;

public class JassNewArrayExpression implements JassExpression {
	private final ArrayJassType type;
	private final int arrayLength;

	public JassNewArrayExpression(final ArrayJassType type, final int arrayLength) {
		this.type = type;
		this.arrayLength = arrayLength;
	}

	@Override
	public <T> T accept(final JassExpressionVisitor<T> visitor) {
		return visitor.visit(this);
	}

	public ArrayJassType getType() {
		return this.type;
	}

	public int getArrayLength() {
		return this.arrayLength;
	}
}
