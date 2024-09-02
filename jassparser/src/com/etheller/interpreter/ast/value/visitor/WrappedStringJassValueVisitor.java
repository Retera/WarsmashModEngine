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

public class WrappedStringJassValueVisitor implements JassValueVisitor<StringJassValue> {
	private static final WrappedStringJassValueVisitor INSTANCE = new WrappedStringJassValueVisitor();

	public static WrappedStringJassValueVisitor getInstance() {
		return INSTANCE;
	}

	@Override
	public StringJassValue accept(final IntegerJassValue value) {
		return null;
	}

	@Override
	public StringJassValue accept(final RealJassValue value) {
		return null;
	}

	@Override
	public StringJassValue accept(final BooleanJassValue value) {
		return null;
	}

	@Override
	public StringJassValue accept(final StringJassValue value) {
		return value;
	}

	@Override
	public StringJassValue accept(final CodeJassValue value) {
		return null;
	}

	@Override
	public StringJassValue accept(final ArrayJassValue value) {
		return null;
	}

	@Override
	public StringJassValue accept(final HandleJassValue value) {
		return null;
	}

	@Override
	public StringJassValue accept(final DummyJassValue value) {
		return null;
	}

	@Override
	public StringJassValue accept(final StructJassValue value) {
		final JassValue superValue = value.getSuperValue();
		if (superValue != null) {
			return superValue.visit(this);
		}
		return null;
	}

	@Override
	public StringJassValue accept(final StaticStructTypeJassValue value) {
		return null;
	}

}
