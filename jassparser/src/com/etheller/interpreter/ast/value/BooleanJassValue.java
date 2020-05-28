package com.etheller.interpreter.ast.value;

public class BooleanJassValue implements JassValue {
	private final boolean value;

	public BooleanJassValue(final boolean value) {
		this.value = value;
	}

	public boolean getValue() {
		return value;
	}

	@Override
	public <TYPE> TYPE visit(final JassValueVisitor<TYPE> visitor) {
		return visitor.accept(this);
	}
}
