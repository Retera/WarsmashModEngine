package com.etheller.interpreter.ast.value.visitor;

import com.etheller.interpreter.ast.value.ArrayJassValue;
import com.etheller.interpreter.ast.value.BooleanJassValue;
import com.etheller.interpreter.ast.value.CodeJassValue;
import com.etheller.interpreter.ast.value.DummyJassValue;
import com.etheller.interpreter.ast.value.HandleJassValue;
import com.etheller.interpreter.ast.value.IntegerJassValue;
import com.etheller.interpreter.ast.value.JassValueVisitor;
import com.etheller.interpreter.ast.value.RealJassValue;
import com.etheller.interpreter.ast.value.StaticStructTypeJassValue;
import com.etheller.interpreter.ast.value.StringJassValue;
import com.etheller.interpreter.ast.value.StructJassValue;

public class StaticStructTypeJassValueVisitor implements JassValueVisitor<StaticStructTypeJassValue> {
	private static final StaticStructTypeJassValueVisitor INSTANCE = new StaticStructTypeJassValueVisitor();

	public static StaticStructTypeJassValueVisitor getInstance() {
		return INSTANCE;
	}

	@Override
	public StaticStructTypeJassValue accept(final IntegerJassValue value) {
		return null;
	}

	@Override
	public StaticStructTypeJassValue accept(final RealJassValue value) {
		return null;
	}

	@Override
	public StaticStructTypeJassValue accept(final BooleanJassValue value) {
		return null;
	}

	@Override
	public StaticStructTypeJassValue accept(final StringJassValue value) {
		return null;
	}

	@Override
	public StaticStructTypeJassValue accept(final CodeJassValue value) {
		return null;
	}

	@Override
	public StaticStructTypeJassValue accept(final ArrayJassValue value) {
		return null;
	}

	@Override
	public StaticStructTypeJassValue accept(final HandleJassValue value) {
		return null;
	}

	@Override
	public StaticStructTypeJassValue accept(final DummyJassValue value) {
		return null;
	}

	@Override
	public StaticStructTypeJassValue accept(final StructJassValue value) {
		return null;
	}

	@Override
	public StaticStructTypeJassValue accept(final StaticStructTypeJassValue value) {
		return value;
	}

}
