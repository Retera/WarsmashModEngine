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

public class NegateJassValueVisitor implements JassValueVisitor<JassValue> {
	private static final NegateJassValueVisitor INSTANCE = new NegateJassValueVisitor();

	public static NegateJassValueVisitor getInstance() {
		return INSTANCE;
	}

	@Override
	public JassValue accept(final IntegerJassValue value) {
		return new IntegerJassValue(-value.getValue());
	}

	@Override
	public JassValue accept(final RealJassValue value) {
		return new RealJassValue(-value.getValue());
	}

	@Override
	public JassValue accept(final BooleanJassValue value) {
		throw new IllegalStateException("Unable to apply numeric unary negative sign to boolean");
	}

	@Override
	public JassValue accept(final StringJassValue value) {
		throw new IllegalStateException("Unable to apply numeric unary negative sign to string");
	}

	@Override
	public JassValue accept(final CodeJassValue value) {
		throw new IllegalStateException("Unable to apply numeric unary negative sign to code");
	}

	@Override
	public JassValue accept(final ArrayJassValue value) {
		throw new IllegalStateException("Unable to apply numeric unary negative sign to array");
	}

	@Override
	public JassValue accept(final HandleJassValue value) {
		throw new IllegalStateException("Unable to apply numeric unary negative sign to handle");
	}

	@Override
	public JassValue accept(final DummyJassValue value) {
		return value;
	}

	@Override
	public JassValue accept(final StructJassValue value) {
		final JassValue superValue = value.getSuperValue();
		if (superValue != null) {
			return superValue.visit(this);
		}
		throw new IllegalStateException("Unable to apply numeric unary negative sign to struct");
	}

	@Override
	public JassValue accept(final StaticStructTypeJassValue value) {
		throw new IllegalStateException("Unable to apply numeric unary negative sign to struct type");
	}

}
