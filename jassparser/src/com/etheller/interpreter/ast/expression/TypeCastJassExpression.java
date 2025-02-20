package com.etheller.interpreter.ast.expression;

import com.etheller.interpreter.ast.value.JassType;

public class TypeCastJassExpression implements JassExpression {
	private final JassExpression valueExpression;
	private final JassType castToType;

	public TypeCastJassExpression(final JassExpression valueExpression, final JassType castToType) {
		this.valueExpression = valueExpression;
		this.castToType = castToType;
	}

	@Override
	public <T> T accept(final JassExpressionVisitor<T> visitor) {
		return visitor.visit(this);
	}

	public JassExpression getValueExpression() {
		return this.valueExpression;
	}

	public JassType getCastToType() {
		return this.castToType;
	}

}
