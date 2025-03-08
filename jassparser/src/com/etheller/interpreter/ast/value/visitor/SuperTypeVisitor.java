package com.etheller.interpreter.ast.value.visitor;

import com.etheller.interpreter.ast.value.ArrayJassType;
import com.etheller.interpreter.ast.value.HandleJassType;
import com.etheller.interpreter.ast.value.JassType;
import com.etheller.interpreter.ast.value.JassTypeVisitor;
import com.etheller.interpreter.ast.value.PrimitiveJassType;
import com.etheller.interpreter.ast.value.StaticStructTypeJassValue;
import com.etheller.interpreter.ast.value.StructJassType;

public class SuperTypeVisitor implements JassTypeVisitor<JassType> {
	private static final SuperTypeVisitor INSTANCE = new SuperTypeVisitor();

	public static SuperTypeVisitor getInstance() {
		return INSTANCE;
	}

	@Override
	public JassType accept(final PrimitiveJassType primitiveType) {
		return null;
	}

	@Override
	public JassType accept(final ArrayJassType arrayType) {
		return null;
	}

	@Override
	public JassType accept(final HandleJassType type) {
		return type.getSuperType();
	}

	@Override
	public JassType accept(final StructJassType type) {
		return type.getSuperType();
	}

	@Override
	public JassType accept(final StaticStructTypeJassValue staticType) {
		return null;
	}

}
