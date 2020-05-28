package com.etheller.interpreter.ast.value;

public class ArrayJassType implements JassType {
	private final PrimitiveJassType primitiveType;

	public ArrayJassType(final PrimitiveJassType primitiveType) {
		this.primitiveType = primitiveType;
	}

	public PrimitiveJassType getPrimitiveType() {
		return primitiveType;
	}

	@Override
	public <TYPE> TYPE visit(final JassTypeVisitor<TYPE> visitor) {
		return visitor.accept(this);
	}
}
