package com.etheller.interpreter.ast.value;

public class StringJassValue implements JassValue {
	private final String value;

	public StringJassValue(final String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	@Override
	public <TYPE> TYPE visit(final JassValueVisitor<TYPE> visitor) {
		return visitor.accept(this);
	}
}
