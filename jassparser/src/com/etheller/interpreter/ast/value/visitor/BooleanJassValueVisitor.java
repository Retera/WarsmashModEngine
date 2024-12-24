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

public class BooleanJassValueVisitor implements JassValueVisitor<Boolean> {
	private static final BooleanJassValueVisitor INSTANCE = new BooleanJassValueVisitor();

	public static BooleanJassValueVisitor getInstance() {
		return INSTANCE;
	}

	@Override
	public Boolean accept(final IntegerJassValue value) {
		throw new IllegalStateException("Unable to convert " + value + " to boolean");
	}

	@Override
	public Boolean accept(final RealJassValue value) {
		throw new IllegalStateException("Unable to convert " + value + " to boolean");
	}

	@Override
	public Boolean accept(final BooleanJassValue value) {
		return value.getValue();
	}

	@Override
	public Boolean accept(final StringJassValue value) {
		throw new IllegalStateException("Unable to convert " + value + " to boolean");
	}

	@Override
	public Boolean accept(final CodeJassValue value) {
		throw new IllegalStateException("Unable to convert " + value + " to boolean");
	}

	@Override
	public Boolean accept(final ArrayJassValue value) {
		throw new IllegalStateException("Unable to convert " + value + " to boolean");
	}

	@Override
	public Boolean accept(final HandleJassValue value) {
		throw new IllegalStateException("Unable to convert " + value + " to boolean");
	}

	@Override
	public Boolean accept(final DummyJassValue value) {
		throw new IllegalStateException("Unable to convert " + value + " to boolean");
	}

	@Override
	public Boolean accept(final StructJassValue value) {
		final JassValue superValue = value.getSuperValue();
		if (superValue != null) {
			return superValue.visit(this);
		}
		throw new IllegalStateException("Unable to convert " + value + " to boolean");
	}

	@Override
	public Boolean accept(final StaticStructTypeJassValue value) {
		throw new IllegalStateException("Unable to convert static class type " + value.getName() + " to boolean");
	}

}
