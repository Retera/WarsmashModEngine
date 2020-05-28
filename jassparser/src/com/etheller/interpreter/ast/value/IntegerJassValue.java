package com.etheller.interpreter.ast.value;

public class IntegerJassValue implements JassValue {
	private final int value;

	public IntegerJassValue(final int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	@Override
	public <TYPE> TYPE visit(final JassValueVisitor<TYPE> visitor) {
		return visitor.accept(this);
	}
}
