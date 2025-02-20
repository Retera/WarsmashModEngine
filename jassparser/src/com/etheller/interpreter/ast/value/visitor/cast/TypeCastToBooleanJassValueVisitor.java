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

public class TypeCastToBooleanJassValueVisitor implements JassValueVisitor<JassValue> {
	public static final TypeCastToBooleanJassValueVisitor INSTANCE = new TypeCastToBooleanJassValueVisitor();

	@Override
	public JassValue accept(final IntegerJassValue value) {
		return BooleanJassValue.of(value.getValue() != 0);
	}

	@Override
	public JassValue accept(final RealJassValue value) {
		return BooleanJassValue.TRUE;
	}

	@Override
	public JassValue accept(final BooleanJassValue value) {
		return value;
	}

	@Override
	public JassValue accept(final StringJassValue value) {
		return BooleanJassValue.of(value.getValue() != null);
	}

	@Override
	public JassValue accept(final CodeJassValue value) {
		return BooleanJassValue.of(value.getUserFunctionInstructionPtr() != null);
	}

	@Override
	public JassValue accept(final ArrayJassValue value) {
		return BooleanJassValue.TRUE;
	}

	@Override
	public JassValue accept(final HandleJassValue value) {
		return BooleanJassValue.of(value.getJavaValue() != null);
	}

	@Override
	public JassValue accept(final DummyJassValue value) {
		return BooleanJassValue.FALSE;
	}

	@Override
	public JassValue accept(final StructJassValue value) {
		return BooleanJassValue.TRUE;
	}

	@Override
	public JassValue accept(final StaticStructTypeJassValue value) {
		return BooleanJassValue.TRUE;
	}

}
