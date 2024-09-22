package com.etheller.interpreter.ast.value.visitor.cast;

import com.etheller.interpreter.ast.util.CExtensibleHandle;
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
import com.etheller.interpreter.ast.value.StructJassType;
import com.etheller.interpreter.ast.value.StructJassValue;

public class TypeCastToStructJassValueVisitor implements JassValueVisitor<JassValue> {
	private final StructJassType destinationType;

	public TypeCastToStructJassValueVisitor(final StructJassType destinationType) {
		this.destinationType = destinationType;
	}

	@Override
	public JassValue accept(final IntegerJassValue value) {
		// NOTE: in the future, maybe structs are convertible to int here
		return this.destinationType.getNullValue();
	}

	@Override
	public JassValue accept(final RealJassValue value) {
		return this.destinationType.getNullValue();
	}

	@Override
	public JassValue accept(final BooleanJassValue value) {
		return this.destinationType.getNullValue();
	}

	@Override
	public JassValue accept(final StringJassValue value) {
		return this.destinationType.getNullValue();
	}

	@Override
	public JassValue accept(final CodeJassValue value) {
		return this.destinationType.getNullValue();
	}

	@Override
	public JassValue accept(final ArrayJassValue value) {
		return this.destinationType.getNullValue();
	}

	@Override
	public JassValue accept(final HandleJassValue value) {
		final Object javaValue = value.getJavaValue();
		if (javaValue instanceof CExtensibleHandle) {
			final CExtensibleHandle structHandle = (CExtensibleHandle) javaValue;
			final StructJassValue structValue = structHandle.getStructValue();
			return structValue.visit(this);
		}
		return this.destinationType.getNullValue();
//		throw new ClassCastException(
//				"Cannot cast " + value.getType().getName() + " to unrelated type " + this.destinationType.getName());
	}

	@Override
	public JassValue accept(final DummyJassValue value) {
		return this.destinationType.getNullValue();
	}

	@Override
	public JassValue accept(final StructJassValue value) {
		final StructJassType valueType = value.getType();
		if ((valueType == this.destinationType) || this.destinationType.isAssignableFrom(valueType)) {
			return value;
		}
		return this.destinationType.getNullValue();
//		throw new ClassCastException(
//				"Cannot cast " + valueType.getName() + " to unrelated type " + this.destinationType.getName());
	}

	@Override
	public JassValue accept(final StaticStructTypeJassValue value) {
		return this.destinationType.getNullValue();
	}

}
