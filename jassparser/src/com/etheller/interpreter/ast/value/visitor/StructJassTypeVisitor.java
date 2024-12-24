package com.etheller.interpreter.ast.value.visitor;

import com.etheller.interpreter.ast.value.ArrayJassType;
import com.etheller.interpreter.ast.value.HandleJassType;
import com.etheller.interpreter.ast.value.JassTypeVisitor;
import com.etheller.interpreter.ast.value.PrimitiveJassType;
import com.etheller.interpreter.ast.value.StaticStructTypeJassValue;
import com.etheller.interpreter.ast.value.StructJassType;

public class StructJassTypeVisitor implements JassTypeVisitor<StructJassType> {
	private static final StructJassTypeVisitor INSTANCE = new StructJassTypeVisitor();

	public static StructJassTypeVisitor getInstance() {
		return INSTANCE;
	}

	@Override
	public StructJassType accept(final PrimitiveJassType primitiveType) {
		return null;
	}

	@Override
	public StructJassType accept(final ArrayJassType arrayType) {
		return null;
	}

	@Override
	public StructJassType accept(final HandleJassType type) {
		return null;
	}

	@Override
	public StructJassType accept(final StructJassType type) {
		return type;
	}

	@Override
	public StructJassType accept(final StaticStructTypeJassValue staticType) {
		return null;
	}

}
