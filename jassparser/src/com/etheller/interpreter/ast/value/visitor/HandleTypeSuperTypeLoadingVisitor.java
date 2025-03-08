package com.etheller.interpreter.ast.value.visitor;

import com.etheller.interpreter.ast.value.ArrayJassType;
import com.etheller.interpreter.ast.value.HandleJassType;
import com.etheller.interpreter.ast.value.JassTypeVisitor;
import com.etheller.interpreter.ast.value.PrimitiveJassType;
import com.etheller.interpreter.ast.value.StaticStructTypeJassValue;
import com.etheller.interpreter.ast.value.StructJassType;

public class HandleTypeSuperTypeLoadingVisitor implements JassTypeVisitor<Void> {
	private HandleJassType superType;

	public HandleTypeSuperTypeLoadingVisitor reset(final HandleJassType superType) {
		this.superType = superType;
		return this;
	}

	@Override
	public Void accept(final PrimitiveJassType primitiveType) {
		return null;
	}

	@Override
	public Void accept(final ArrayJassType arrayType) {
		return null;
	}

	@Override
	public Void accept(final HandleJassType type) {
		type.setSuperType(this.superType);
		return null;
	}

	@Override
	public Void accept(final StructJassType type) {
		return null;
	}

	@Override
	public Void accept(final StaticStructTypeJassValue staticType) {
		return null;
	}

}
