package com.etheller.interpreter.ast.value.visitor;

import com.etheller.interpreter.ast.value.ArrayJassValue;
import com.etheller.interpreter.ast.value.BooleanJassValue;
import com.etheller.interpreter.ast.value.CodeJassValue;
import com.etheller.interpreter.ast.value.DummyJassValue;
import com.etheller.interpreter.ast.value.HandleJassValue;
import com.etheller.interpreter.ast.value.IntegerJassValue;
import com.etheller.interpreter.ast.value.JassType;
import com.etheller.interpreter.ast.value.JassValueVisitor;
import com.etheller.interpreter.ast.value.RealJassValue;
import com.etheller.interpreter.ast.value.StaticStructTypeJassValue;
import com.etheller.interpreter.ast.value.StringJassValue;
import com.etheller.interpreter.ast.value.StructJassValue;

public class JassTypeGettingValueVisitor implements JassValueVisitor<JassType> {
	public static final JassTypeGettingValueVisitor INSTANCE = new JassTypeGettingValueVisitor();

	public static JassTypeGettingValueVisitor getInstance() {
		return INSTANCE;
	}

	@Override
	public JassType accept(final IntegerJassValue value) {
		return JassType.INTEGER;
	}

	@Override
	public JassType accept(final RealJassValue value) {
		return JassType.REAL;
	}

	@Override
	public JassType accept(final BooleanJassValue value) {
		return JassType.BOOLEAN;
	}

	@Override
	public JassType accept(final StringJassValue value) {
		return JassType.STRING;
	}

	@Override
	public JassType accept(final CodeJassValue value) {
		return JassType.CODE;
	}

	@Override
	public JassType accept(final ArrayJassValue value) {
		return value.getType();
	}

	@Override
	public JassType accept(final HandleJassValue value) {
		return value.getType();
	}

	@Override
	public JassType accept(final DummyJassValue value) {
		return JassType.DUMMY;
	}

	@Override
	public JassType accept(final StructJassValue value) {
		return value.getType();
	}

	@Override
	public JassType accept(final StaticStructTypeJassValue value) {
		return value;
	}

}
