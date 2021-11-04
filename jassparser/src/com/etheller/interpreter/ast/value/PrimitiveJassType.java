package com.etheller.interpreter.ast.value;

public class PrimitiveJassType implements JassType {
	private final String name;
	private final JassValue nullValue;

	public PrimitiveJassType(final String name, final JassValue nullValue) {
		this.name = name;
		this.nullValue = nullValue;
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
		return this.nullValue;
	}

}
