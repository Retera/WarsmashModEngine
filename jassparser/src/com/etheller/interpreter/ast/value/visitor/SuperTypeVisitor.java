package com.etheller.interpreter.ast.value.visitor;

import com.etheller.interpreter.ast.value.ArrayJassType;
import com.etheller.interpreter.ast.value.HandleJassType;
import com.etheller.interpreter.ast.value.JassTypeVisitor;
import com.etheller.interpreter.ast.value.PrimitiveJassType;

public class SuperTypeVisitor implements JassTypeVisitor<HandleJassType> {
	private static final SuperTypeVisitor INSTANCE = new SuperTypeVisitor();

	public static SuperTypeVisitor getInstance() {
		return INSTANCE;
	}

	@Override
	public HandleJassType accept(final PrimitiveJassType primitiveType) {
		return null;
	}

	@Override
	public HandleJassType accept(final ArrayJassType arrayType) {
		return null;
	}

	@Override
	public HandleJassType accept(final HandleJassType type) {
		return type.getSuperType();
	}

}
