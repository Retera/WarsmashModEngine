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

public class TypeCastToRealJassValueVisitor implements JassValueVisitor<JassValue> {
	public static final TypeCastToRealJassValueVisitor INSTANCE = new TypeCastToRealJassValueVisitor();

	@Override
	public JassValue accept(final IntegerJassValue value) {
		return RealJassValue.of(value.getValue());
	}

	@Override
	public JassValue accept(final RealJassValue value) {
		return value;
	}

	@Override
	public JassValue accept(final BooleanJassValue value) {
		return RealJassValue.of(value.getValue() ? 1.0 : 0.0);
	}

	@Override
	public JassValue accept(final StringJassValue value) {
		return RealJassValue.ZERO;
	}

	@Override
	public JassValue accept(final CodeJassValue value) {
		return RealJassValue.ZERO;
	}

	@Override
	public JassValue accept(final ArrayJassValue value) {
		return RealJassValue.ZERO;
	}

	@Override
	public JassValue accept(final HandleJassValue value) {
		return RealJassValue.ZERO;
	}

	@Override
	public JassValue accept(final DummyJassValue value) {
		return RealJassValue.ZERO;
	}

	@Override
	public JassValue accept(final StructJassValue value) {
		return RealJassValue.ZERO;
	}

	@Override
	public JassValue accept(final StaticStructTypeJassValue value) {
		return RealJassValue.ZERO;
	}

}
