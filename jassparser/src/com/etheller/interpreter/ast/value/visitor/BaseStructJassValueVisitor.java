package com.etheller.interpreter.ast.value.visitor;

import com.etheller.interpreter.ast.util.CExtensibleHandle;
import com.etheller.interpreter.ast.value.ArrayJassValue;
import com.etheller.interpreter.ast.value.BaseStructJassValue;
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

public class BaseStructJassValueVisitor implements JassValueVisitor<BaseStructJassValue> {
	private static final BaseStructJassValueVisitor INSTANCE = new BaseStructJassValueVisitor();

	public static BaseStructJassValueVisitor getInstance() {
		return INSTANCE;
	}

	@Override
	public BaseStructJassValue accept(final IntegerJassValue value) {
		return null;
	}

	@Override
	public BaseStructJassValue accept(final RealJassValue value) {
		return null;
	}

	@Override
	public BaseStructJassValue accept(final BooleanJassValue value) {
		return null;
	}

	@Override
	public BaseStructJassValue accept(final StringJassValue value) {
		return null;
	}

	@Override
	public BaseStructJassValue accept(final CodeJassValue value) {
		return null;
	}

	@Override
	public BaseStructJassValue accept(final ArrayJassValue value) {
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
	public BaseStructJassValue accept(final StaticStructTypeJassValue value) {
		return value;
	}
}
