package com.etheller.interpreter.ast.value.visitor;

import com.etheller.interpreter.ast.value.ArrayJassType;
import com.etheller.interpreter.ast.value.HandleJassType;
import com.etheller.interpreter.ast.value.JassTypeVisitor;
import com.etheller.interpreter.ast.value.PrimitiveJassType;
import com.etheller.interpreter.ast.value.StaticStructTypeJassValue;
import com.etheller.interpreter.ast.value.StructJassType;

public class HandleJassTypeVisitor implements JassTypeVisitor<HandleJassType> {
	private static final HandleJassTypeVisitor INSTANCE = new HandleJassTypeVisitor();

	public static HandleJassTypeVisitor getInstance() {
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
		return type;
	}

	@Override
	public HandleJassType accept(final StructJassType value) {
		return null;
	}

	@Override
	public HandleJassType accept(final StaticStructTypeJassValue staticType) {
		return null;
	}

}
