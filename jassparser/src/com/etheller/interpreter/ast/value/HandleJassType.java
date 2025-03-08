package com.etheller.interpreter.ast.value;

import com.etheller.interpreter.ast.value.visitor.SuperTypeVisitor;

public class HandleJassType implements JassType {
	private HandleJassType superType;
	private final String name;
	private HandleJassTypeConstructor constructorNative;
	private HandleJassTypeConstructor destructorNative;

	public HandleJassType(final HandleJassType superType, final String name) {
		this.superType = superType;
		this.name = name;
	}

	@Override
	public boolean isAssignableFrom(JassType valueType) {
		while (valueType != null) {
			if (this == valueType) {
				return true;
			}
			valueType = valueType.visit(SuperTypeVisitor.getInstance());
		}
		return false;
	}

	@Override
	public String getName() {
		return this.name;
	}

	public HandleJassType getSuperType() {
		return this.superType;
	}

	public void setSuperType(final HandleJassType superType) {
		this.superType = superType;
	}

	@Override
	public <TYPE> TYPE visit(final JassTypeVisitor<TYPE> visitor) {
		return visitor.accept(this);
	}

	@Override
	public boolean isNullable() {
		return true;
	}

	@Override
	public HandleJassValue getNullValue() {
		return new HandleJassValue(this, null);
	}

	public void setConstructorNative(final HandleJassTypeConstructor constructorNative) {
		this.constructorNative = constructorNative;
	}

	public void setDestructorNative(final HandleJassTypeConstructor constructorNative) {
		this.destructorNative = constructorNative;
	}

	public HandleJassTypeConstructor getConstructorNative() {
		return this.constructorNative;
	}

	public HandleJassTypeConstructor getDestructorNative() {
		return this.destructorNative;
	}

}
