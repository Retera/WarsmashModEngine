package com.etheller.interpreter.ast.value.visitor;

import com.etheller.interpreter.ast.util.CExtensibleHandle;
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

public class StructJassValueVisitor implements JassValueVisitor<StructJassValue> {
	private static final StructJassValueVisitor INSTANCE = new StructJassValueVisitor();

	public static StructJassValueVisitor getInstance() {
		return INSTANCE;
	}

	@Override
	public StructJassValue accept(final IntegerJassValue value) {
		return null;
	}

	@Override
	public StructJassValue accept(final RealJassValue value) {
		return null;
	}

	@Override
	public StructJassValue accept(final BooleanJassValue value) {
		return null;
	}

	@Override
	public StructJassValue accept(final StringJassValue value) {
		return null;
	}

	@Override
	public StructJassValue accept(final CodeJassValue value) {
		return null;
	}

	@Override
	public StructJassValue accept(final ArrayJassValue value) {
		return null;
	}

	@Override
	public StructJassValue accept(final HandleJassValue value) {
		final Object javaValue = value.getJavaValue();
		if (javaValue instanceof CExtensibleHandle) {
			return ((CExtensibleHandle) javaValue).getStructValue();
		}
		return null;
	}

	@Override
	public StructJassValue accept(final DummyJassValue value) {
		return null;
	}

	@Override
	public StructJassValue accept(final StructJassValue value) {
		return value;
	}

	@Override
	public StructJassValue accept(final StaticStructTypeJassValue value) {
		return null;
	}
}
