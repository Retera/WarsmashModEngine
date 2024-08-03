package com.etheller.interpreter.ast.value.visitor;

import com.etheller.interpreter.ast.value.ArrayJassType;
import com.etheller.interpreter.ast.value.HandleJassType;
import com.etheller.interpreter.ast.value.JassType;
import com.etheller.interpreter.ast.value.JassTypeVisitor;
import com.etheller.interpreter.ast.value.PrimitiveJassType;
import com.etheller.interpreter.ast.value.StaticStructTypeJassValue;
import com.etheller.interpreter.ast.value.StructJassType;

public class ArrayPrimitiveTypeVisitor implements JassTypeVisitor<JassType> {
	private static final ArrayPrimitiveTypeVisitor INSTANCE = new ArrayPrimitiveTypeVisitor();

	public static ArrayPrimitiveTypeVisitor getInstance() {
		return INSTANCE;
	}

	@Override
	public JassType accept(final PrimitiveJassType primitiveType) {
		return null;
	}

	@Override
	public JassType accept(final ArrayJassType arrayType) {
		return arrayType.getPrimitiveType();
	}

	@Override
	public JassType accept(final HandleJassType type) {
		return null;
	}

	@Override
	public JassType accept(final StructJassType value) {
		final JassType superType = value.getSuperType();
		if (superType != null) {
			return superType.visit(this);
		}
		return null;
	}

	@Override
	public JassType accept(final StaticStructTypeJassValue staticType) {
		return null;
	}
}
