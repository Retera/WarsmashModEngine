package com.etheller.interpreter.ast.value;

public class StringJassType extends PrimitiveJassType {

	public StringJassType(final String name) {
		super(name, new StringJassValue(null));
	}

	@Override
	public boolean isNullable() {
		return true;
	}
}
