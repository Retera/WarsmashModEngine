package com.etheller.interpreter.ast.value;

import com.etheller.interpreter.ast.value.visitor.StaticStructTypeJassTypeVisitor;

public class AnyStructTypeJassType extends PrimitiveJassType {

	public AnyStructTypeJassType(final String name) {
		super(name, null);
	}

	@Override
	public boolean isNullable() {
		return true;
	}

	@Override
	public boolean isAssignableFrom(final JassType value) {
		final StaticStructTypeJassValue staticStructType = value.visit(StaticStructTypeJassTypeVisitor.getInstance());
		if (staticStructType != null) {
			return true;
		}
		return super.isAssignableFrom(value);
	}
}
