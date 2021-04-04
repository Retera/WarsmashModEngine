package com.etheller.interpreter.ast.value.visitor;

import com.etheller.interpreter.ast.value.ArrayJassValue;
import com.etheller.interpreter.ast.value.BooleanJassValue;
import com.etheller.interpreter.ast.value.CodeJassValue;
import com.etheller.interpreter.ast.value.HandleJassValue;
import com.etheller.interpreter.ast.value.IntegerJassValue;
import com.etheller.interpreter.ast.value.JassValueVisitor;
import com.etheller.interpreter.ast.value.RealJassValue;
import com.etheller.interpreter.ast.value.StringJassValue;

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

}
