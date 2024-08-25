package com.etheller.interpreter.ast.value;

public class StaticStructTypeJassValue implements JassValue, JassType {
	private final StructJassType staticType;

	public StaticStructTypeJassValue(final StructJassType staticType) {
		this.staticType = staticType;
	}

	@Override
	public <TYPE> TYPE visit(final JassValueVisitor<TYPE> visitor) {
		return visitor.accept(this);
	}

	public StructJassType getStaticType() {
		return this.staticType;
	}

	@Override
	public <TYPE> TYPE visit(final JassTypeVisitor<TYPE> visitor) {
		return visitor.accept(this);
	}

	@Override
	public String getName() {
		return this.staticType.getName() + ".class";
	}

	@Override
	public boolean isAssignableFrom(final JassType value) {
		return (value == this) || (value == JassType.ANY_STRUCT_TYPE);
	}

	@Override
	public boolean isNullable() {
		return false;
	}

	@Override
	public JassValue getNullValue() {
		return this;
	}
}
