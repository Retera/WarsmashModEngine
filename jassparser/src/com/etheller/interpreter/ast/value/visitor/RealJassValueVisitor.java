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

public class RealJassValueVisitor implements JassValueVisitor<Double> {
	private static final RealJassValueVisitor INSTANCE = new RealJassValueVisitor();

	public static RealJassValueVisitor getInstance() {
		return INSTANCE;
	}

	@Override
	public Double accept(final IntegerJassValue value) {
		return Double.valueOf(value.getValue());
	}

	@Override
	public Double accept(final RealJassValue value) {
		return value.getValue();
	}

	@Override
	public Double accept(final BooleanJassValue value) {
		return 0.0;
	}

	@Override
	public Double accept(final StringJassValue value) {
		return 0.0;
	}

	@Override
	public Double accept(final CodeJassValue value) {
		return 0.0;
	}

	@Override
	public Double accept(final ArrayJassValue value) {
		return 0.0;
	}

	@Override
	public Double accept(final HandleJassValue value) {
		return 0.0;
	}

	@Override
	public Double accept(final DummyJassValue value) {
		return 0.0;
	}

	@Override
	public Double accept(final StructJassValue value) {
		final JassValue superValue = value.getSuperValue();
		if (superValue != null) {
			return superValue.visit(this);
		}
		return 0.0;
	}

	@Override
	public Double accept(final StaticStructTypeJassValue value) {
		return 0.0;
	}

}
