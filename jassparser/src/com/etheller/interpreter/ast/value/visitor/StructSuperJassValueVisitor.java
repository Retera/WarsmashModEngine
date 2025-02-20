package com.etheller.interpreter.ast.value.visitor;

import com.etheller.interpreter.ast.value.ArrayJassValue;
import com.etheller.interpreter.ast.value.BooleanJassValue;
import com.etheller.interpreter.ast.value.CodeJassValue;
import com.etheller.interpreter.ast.value.DummyJassValue;
import com.etheller.interpreter.ast.value.HandleJassValue;
import com.etheller.interpreter.ast.value.IntegerJassValue;
import com.etheller.interpreter.ast.value.JassValue;
import com.etheller.interpreter.ast.value.JassValueVisitor;
import com.etheller.interpreter.ast.value.RealJassValue;
import com.etheller.interpreter.ast.value.StaticStructTypeJassValue;
import com.etheller.interpreter.ast.value.StringJassValue;
import com.etheller.interpreter.ast.value.StructJassValue;

public class StructSuperJassValueVisitor implements JassValueVisitor<JassValue> {
	private static final StructSuperJassValueVisitor INSTANCE = new StructSuperJassValueVisitor();

	public static <V> StructSuperJassValueVisitor getInstance() {
		return INSTANCE;
	}

	@Override
	public JassValue accept(final IntegerJassValue value) {
		return null;
	}

	@Override
	public JassValue accept(final RealJassValue value) {
		return null;
	}

	@Override
	public JassValue accept(final BooleanJassValue value) {
		return null;
	}

	@Override
	public JassValue accept(final StringJassValue value) {
		return null;
	}

	@Override
	public JassValue accept(final CodeJassValue value) {
		return null;
	}

	@Override
	public JassValue accept(final ArrayJassValue value) {
		return null;
	}

	@Override
	public JassValue accept(final HandleJassValue value) {
		return null;
	}

	@Override
	public JassValue accept(final DummyJassValue value) {
		return null;
	}

	@Override
	public JassValue accept(final StructJassValue value) {
		final JassValue superValue = value.getSuperValue();
		if (superValue != null) {
			return superValue.visit(this);
		}
		return superValue;
	}

	@Override
	public JassValue accept(final StaticStructTypeJassValue value) {
		return null;
	}
}
