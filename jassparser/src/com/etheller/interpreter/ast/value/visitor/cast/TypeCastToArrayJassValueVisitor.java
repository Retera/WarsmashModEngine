package com.etheller.interpreter.ast.value.visitor.cast;

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

public class TypeCastToArrayJassValueVisitor implements JassValueVisitor<JassValue> {
	public static final TypeCastToArrayJassValueVisitor INSTANCE = new TypeCastToArrayJassValueVisitor();

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
		// NOTE: maybe later allow us to type cast everything in the array together,
		// although that would be very slow
		return value;
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
		return null;
	}

	@Override
	public JassValue accept(final StaticStructTypeJassValue value) {
		return null;
	}

}
