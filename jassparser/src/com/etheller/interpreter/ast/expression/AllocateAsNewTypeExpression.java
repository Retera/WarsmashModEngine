package com.etheller.interpreter.ast.expression;

import com.etheller.interpreter.ast.value.StructJassType;

public class AllocateAsNewTypeExpression implements JassExpression {
	private final JassExpression originalValue;
	private final StructJassType type;

	public AllocateAsNewTypeExpression(final JassExpression originalValue, final StructJassType type) {
		this.originalValue = originalValue;
		this.type = type;
	}

	@Override
	public <T> T accept(final JassExpressionVisitor<T> visitor) {
		return visitor.visit(this);
	}

	public JassExpression getOriginalValue() {
		return this.originalValue;
	}

	public StructJassType getType() {
		return this.type;
	}

}
