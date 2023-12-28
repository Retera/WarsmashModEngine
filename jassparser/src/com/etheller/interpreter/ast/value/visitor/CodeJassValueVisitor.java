package com.etheller.interpreter.ast.value.visitor;

import com.etheller.interpreter.ast.value.ArrayJassValue;
import com.etheller.interpreter.ast.value.BooleanJassValue;
import com.etheller.interpreter.ast.value.CodeJassValue;
import com.etheller.interpreter.ast.value.DummyJassValue;
import com.etheller.interpreter.ast.value.HandleJassValue;
import com.etheller.interpreter.ast.value.IntegerJassValue;
import com.etheller.interpreter.ast.value.JassValueVisitor;
import com.etheller.interpreter.ast.value.RealJassValue;
import com.etheller.interpreter.ast.value.StringJassValue;

public class CodeJassValueVisitor implements JassValueVisitor<CodeJassValue> {
	private static final CodeJassValueVisitor INSTANCE = new CodeJassValueVisitor();

	public static CodeJassValueVisitor getInstance() {
		return INSTANCE;
	}

	@Override
	public CodeJassValue accept(final IntegerJassValue value) {
		return null;
	}

	@Override
	public CodeJassValue accept(final RealJassValue value) {
		return null;
	}

	@Override
	public CodeJassValue accept(final BooleanJassValue value) {
		return null;
	}

	@Override
	public CodeJassValue accept(final StringJassValue value) {
		return null;
	}

	@Override
	public CodeJassValue accept(final CodeJassValue value) {
		return value;
	}

	@Override
	public CodeJassValue accept(final ArrayJassValue value) {
		return null;
	}

	@Override
	public CodeJassValue accept(final HandleJassValue value) {
		return null;
	}

	@Override
	public CodeJassValue accept(final DummyJassValue value) {
		return null;
	}

}
