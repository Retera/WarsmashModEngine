package com.etheller.interpreter.ast.value.visitor;

import com.etheller.interpreter.ast.value.ArrayJassType;
import com.etheller.interpreter.ast.value.HandleJassType;
import com.etheller.interpreter.ast.value.JassTypeVisitor;
import com.etheller.interpreter.ast.value.PrimitiveJassType;
import com.etheller.interpreter.ast.value.StaticStructTypeJassValue;
import com.etheller.interpreter.ast.value.StructJassType;

public class ArrayTypeVisitor implements JassTypeVisitor<ArrayJassType> {
	private static final ArrayTypeVisitor INSTANCE = new ArrayTypeVisitor();

	public static ArrayTypeVisitor getInstance() {
		return INSTANCE;
	}

	@Override
	public ArrayJassType accept(final PrimitiveJassType primitiveType) {
		return null;
	}

	@Override
	public ArrayJassType accept(final ArrayJassType arrayType) {
		return arrayType;
	}

	@Override
	public ArrayJassType accept(final HandleJassType type) {
		return null;
	}

	@Override
	public ArrayJassType accept(final StructJassType value) {
		return null;
	}

	@Override
	public ArrayJassType accept(final StaticStructTypeJassValue staticType) {
		return null;
	}

}
