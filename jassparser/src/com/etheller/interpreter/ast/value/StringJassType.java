package com.etheller.interpreter.ast.value;

public class StringJassType extends PrimitiveJassType {

	public StringJassType(final String name) {
		super(name);
	}

	@Override
	public boolean isNullable() {
		return true;
	}

	@Override
	public JassValue getNullValue() {
		return new StringJassValue(null);
	}
}
