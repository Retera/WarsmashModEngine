package com.etheller.interpreter.ast.expression;

import com.etheller.interpreter.ast.value.StructJassType;

public class JassNewExpression implements JassExpression {
	private final StructJassType type;

	public JassNewExpression(final StructJassType type) {
		this.type = type;
	}

	@Override
	public <T> T accept(final JassExpressionVisitor<T> visitor) {
		return visitor.visit(this);
	}

	public StructJassType getType() {
		return this.type;
	}
}
