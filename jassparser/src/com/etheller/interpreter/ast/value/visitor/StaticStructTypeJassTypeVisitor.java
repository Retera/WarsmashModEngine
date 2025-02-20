package com.etheller.interpreter.ast.value.visitor;

import com.etheller.interpreter.ast.value.ArrayJassType;
import com.etheller.interpreter.ast.value.HandleJassType;
import com.etheller.interpreter.ast.value.JassTypeVisitor;
import com.etheller.interpreter.ast.value.PrimitiveJassType;
import com.etheller.interpreter.ast.value.StaticStructTypeJassValue;
import com.etheller.interpreter.ast.value.StructJassType;

public class StaticStructTypeJassTypeVisitor implements JassTypeVisitor<StaticStructTypeJassValue> {
	private static final StaticStructTypeJassTypeVisitor INSTANCE = new StaticStructTypeJassTypeVisitor();

	public static StaticStructTypeJassTypeVisitor getInstance() {
		return INSTANCE;
	}

	@Override
	public StaticStructTypeJassValue accept(final PrimitiveJassType primitiveType) {
		return null;
	}

	@Override
	public StaticStructTypeJassValue accept(final ArrayJassType arrayType) {
		return null;
	}

	@Override
	public StaticStructTypeJassValue accept(final HandleJassType type) {
		return null;
	}

	@Override
	public StaticStructTypeJassValue accept(final StructJassType type) {
		return null;
	}

	@Override
	public StaticStructTypeJassValue accept(final StaticStructTypeJassValue staticType) {
		return staticType;
	}

}
