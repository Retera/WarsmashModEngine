package com.etheller.interpreter.ast.value;

public class StructAssignabilityTypeVisitor implements JassTypeVisitor<Boolean> {
	public static final StructAssignabilityTypeVisitor INSTANCE = new StructAssignabilityTypeVisitor();
	private StructJassType interestedType;

	public StructAssignabilityTypeVisitor reset(final StructJassType interestedType) {
		this.interestedType = interestedType;
		return this;
	}

	@Override
	public Boolean accept(final PrimitiveJassType primitiveType) {
		return false;
	}

	@Override
	public Boolean accept(final ArrayJassType arrayType) {
		return false;
	}

	@Override
	public Boolean accept(final HandleJassType type) {
		return false;
	}

	@Override
	public Boolean accept(final StructJassType type) {
		return (type == this.interestedType) || this.interestedType.isAssignableFrom(type.getSuperType());
	}

	@Override
	public Boolean accept(final StaticStructTypeJassValue staticType) {
		return false;
	}

}
