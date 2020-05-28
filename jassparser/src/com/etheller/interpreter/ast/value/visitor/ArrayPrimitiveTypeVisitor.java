package com.etheller.interpreter.ast.value.visitor;

import com.etheller.interpreter.ast.value.ArrayJassType;
import com.etheller.interpreter.ast.value.JassTypeVisitor;
import com.etheller.interpreter.ast.value.PrimitiveJassType;

public class ArrayPrimitiveTypeVisitor implements JassTypeVisitor<PrimitiveJassType> {
	private static final ArrayPrimitiveTypeVisitor INSTANCE = new ArrayPrimitiveTypeVisitor();

	public static ArrayPrimitiveTypeVisitor getInstance() {
		return INSTANCE;
	}

	@Override
	public PrimitiveJassType accept(final PrimitiveJassType primitiveType) {
		return null;
	}

	@Override
	public PrimitiveJassType accept(final ArrayJassType arrayType) {
		return arrayType.getPrimitiveType();
	}

}
