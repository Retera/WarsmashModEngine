package com.etheller.interpreter.ast.value;

import java.util.List;

import com.etheller.interpreter.ast.struct.JassStructMemberType;

public class StaticStructTypeJassValue extends BaseStructJassValue
		implements JassValue, JassType, StructJassTypeInterface {
	private final StructJassType staticType;
	private final List<JassStructMemberType> staticMemberTypes;

	public StaticStructTypeJassValue(final StructJassType staticType,
			final List<JassStructMemberType> staticMemberTypes) {
		super(staticMemberTypes);
		this.staticType = staticType;
		this.staticMemberTypes = staticMemberTypes;
	}

	@Override
	public JassStructMemberType getMemberByName(final String name) {
		final JassStructMemberType member = tryGetMemberByName(name);
		if (member == null) {
			throw new IllegalArgumentException("Type '" + this.staticType.getName() + "' has no member '" + name + "'");
		}
		return member;
	}

	@Override
	public JassStructMemberType tryGetMemberByName(final String name) {
		for (final JassStructMemberType staticMemberType : this.staticMemberTypes) {
			if (staticMemberType.getId().equals(name)) {
				return staticMemberType;
			}
		}
		return null;
	}

	@Override
	public int getMemberIndexInefficientlyByName(final String name) {
		for (int index = 0; index < this.staticMemberTypes.size(); index++) {
			if (this.staticMemberTypes.get(index).getId().equals(name)) {
				return index;
			}
		}
		throw new IllegalArgumentException(
				"Type '" + this.staticType.getName() + "' has no static member '" + name + "'");
	}

	@Override
	public int tryGetMemberIndexInefficientlyByName(final String name) {
		for (int index = 0; index < this.staticMemberTypes.size(); index++) {
			if (this.staticMemberTypes.get(index).getId().equals(name)) {
				return index;
			}
		}
		return -1;
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
