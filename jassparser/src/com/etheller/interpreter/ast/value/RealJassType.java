package com.etheller.interpreter.ast.value;

public class RealJassType extends PrimitiveJassType {

	public RealJassType(final String name) {
		super(name);
	}

	@Override
	public boolean isAssignableFrom(final JassType value) {
		if (value == JassType.INTEGER) {
			return true;
		}
		return super.isAssignableFrom(value);
	}
}
