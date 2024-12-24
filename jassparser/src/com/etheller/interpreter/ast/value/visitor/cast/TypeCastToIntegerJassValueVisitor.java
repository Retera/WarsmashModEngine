package com.etheller.interpreter.ast.value.visitor.cast;

import com.etheller.interpreter.ast.util.CHandle;
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
import com.etheller.interpreter.ast.value.visitor.ObjectJassValueVisitor;

public class TypeCastToIntegerJassValueVisitor implements JassValueVisitor<JassValue> {
	public static final TypeCastToIntegerJassValueVisitor INSTANCE = new TypeCastToIntegerJassValueVisitor();

	@Override
	public JassValue accept(final IntegerJassValue value) {
		return value;
	}

	@Override
	public JassValue accept(final RealJassValue value) {
		return IntegerJassValue.of((int) value.getValue());
	}

	@Override
	public JassValue accept(final BooleanJassValue value) {
		return IntegerJassValue.of(value.getValue() ? 1 : 0);
	}

	@Override
	public JassValue accept(final StringJassValue value) {
		return IntegerJassValue.ZERO;// IntegerJassValue.of(value.hashCode());
	}

	@Override
	public JassValue accept(final CodeJassValue value) {
		return IntegerJassValue.of(value.getUserFunctionInstructionPtr());
	}

	@Override
	public JassValue accept(final ArrayJassValue value) {
		return IntegerJassValue.ZERO;
	}

	@Override
	public JassValue accept(final HandleJassValue value) {
		final Object javaValue = value.getJavaValue();
		if (javaValue == null) {
			return IntegerJassValue.ZERO;
		}
		final CHandle javaHandle = (CHandle) javaValue;
		return IntegerJassValue.of(javaHandle.getHandleId());
	}

	@Override
	public JassValue accept(final DummyJassValue value) {
		return IntegerJassValue.ZERO;
	}

	@Override
	public JassValue accept(final StructJassValue value) {
		// NOTE: in the future, maybe structs are convertible to int here
		final CHandle javaHandle = value.visit(ObjectJassValueVisitor.getInstance());
		if (javaHandle == null) {
			return IntegerJassValue.ZERO;
		}
		return IntegerJassValue.of(javaHandle.getHandleId());
	}

	@Override
	public JassValue accept(final StaticStructTypeJassValue value) {
		return IntegerJassValue.ZERO; // TODO get type id
	}

}
