package com.etheller.interpreter.ast.value.visitor;

import com.etheller.interpreter.ast.function.JassFunction;
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

public class JassFunctionJassValueVisitor implements JassValueVisitor<JassFunction> {
	private static final JassFunctionJassValueVisitor INSTANCE = new JassFunctionJassValueVisitor();

	public static JassFunctionJassValueVisitor getInstance() {
		return INSTANCE;
	}

	@Override
	public JassFunction accept(final IntegerJassValue value) {
		return null;
	}

	@Override
	public JassFunction accept(final RealJassValue value) {
		return null;
	}

	@Override
	public JassFunction accept(final BooleanJassValue value) {
		return null;
	}

	@Override
	public JassFunction accept(final StringJassValue value) {
		return null;
	}

	@Override
	public JassFunction accept(final CodeJassValue value) {
		return value.getValue();
	}

	@Override
	public JassFunction accept(final ArrayJassValue value) {
		return null;
	}

	@Override
	public JassFunction accept(final HandleJassValue value) {
		return null;
	}

	@Override
	public JassFunction accept(final DummyJassValue value) {
		return null;
	}

	@Override
	public JassFunction accept(final StructJassValue value) {
		final JassValue superValue = value.getSuperValue();
		if (superValue != null) {
			return superValue.visit(this);
		}
		return null;
	}

	@Override
	public JassFunction accept(final StaticStructTypeJassValue value) {
		return null;
	}

}
