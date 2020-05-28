package com.etheller.interpreter.ast.value;

public class PrimitiveJassType implements JassType {
	private final String name;

	public PrimitiveJassType(final String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	@Override
	public <TYPE> TYPE visit(final JassTypeVisitor<TYPE> visitor) {
		return visitor.accept(this);
	}

}
