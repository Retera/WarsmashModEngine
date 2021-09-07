package com.etheller.interpreter.ast.value;

public class PrimitiveJassType implements JassType {
	private final String name;

	public PrimitiveJassType(final String name) {
		this.name = name;
	}

	@Override
	public boolean isAssignableFrom(final JassType value) {
		return value == this;
	}

	@Override
	public boolean isNullable() {
		return false;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public <TYPE> TYPE visit(final JassTypeVisitor<TYPE> visitor) {
		return visitor.accept(this);
	}

	@Override
	public JassValue getNullValue() {
		return null;
	}

}
