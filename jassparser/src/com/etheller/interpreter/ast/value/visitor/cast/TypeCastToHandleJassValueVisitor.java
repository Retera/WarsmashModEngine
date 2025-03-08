package com.etheller.interpreter.ast.value.visitor.cast;

import com.etheller.interpreter.ast.value.ArrayJassValue;
import com.etheller.interpreter.ast.value.BooleanJassValue;
import com.etheller.interpreter.ast.value.CodeJassValue;
import com.etheller.interpreter.ast.value.DummyJassValue;
import com.etheller.interpreter.ast.value.HandleJassType;
import com.etheller.interpreter.ast.value.HandleJassValue;
import com.etheller.interpreter.ast.value.IntegerJassValue;
import com.etheller.interpreter.ast.value.JassValue;
import com.etheller.interpreter.ast.value.JassValueVisitor;
import com.etheller.interpreter.ast.value.RealJassValue;
import com.etheller.interpreter.ast.value.StaticStructTypeJassValue;
import com.etheller.interpreter.ast.value.StringJassValue;
import com.etheller.interpreter.ast.value.StructJassValue;

public class TypeCastToHandleJassValueVisitor implements JassValueVisitor<JassValue> {
	private final HandleJassType destinationType;

	public TypeCastToHandleJassValueVisitor(final HandleJassType destinationType) {
		this.destinationType = destinationType;
	}

	@Override
	public JassValue accept(final IntegerJassValue value) {
		// NOTE: in the future, we might wish to return
		// "universalHandleMap.get(value.getValue())" here
		// or something, to allow patch 1.22 and lower emulation
		// to typecast integers back to handles
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
		final HandleJassType valueType = value.getType();
		if ((valueType == this.destinationType) || this.destinationType.isAssignableFrom(valueType)) {
			return value;
		}
		if (valueType.isAssignableFrom(this.destinationType)) {
			return new HandleJassValue(this.destinationType, value.getJavaValue());
		}
		return this.destinationType.getNullValue();
//		throw new ClassCastException(
//				"Cannot cast " + valueType.getName() + " to unrelated type " + this.destinationType.getName());
	}

	@Override
	public JassValue accept(final DummyJassValue value) {
		return this.destinationType.getNullValue();
	}

	@Override
	public JassValue accept(final StructJassValue value) {
		final JassValue superValue = value.getSuperValue();
		if (superValue != null) {
			return superValue.visit(this);
		}
		return this.destinationType.getNullValue();
	}

	@Override
	public JassValue accept(final StaticStructTypeJassValue value) {
		return this.destinationType.getNullValue();
	}

}
