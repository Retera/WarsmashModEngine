package com.etheller.interpreter.ast.value.visitor;

import com.etheller.interpreter.ast.expression.ArithmeticSign;
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

public class ArithmeticLeftHandNullJassValueVisitor implements JassValueVisitor<JassValue> {
	public static final ArithmeticLeftHandNullJassValueVisitor INSTANCE = new ArithmeticLeftHandNullJassValueVisitor();
	private ArithmeticSign sign;

	public ArithmeticLeftHandNullJassValueVisitor reset(final ArithmeticSign sign) {
		this.sign = sign;
		return this;
	}

	@Override
	public JassValue accept(final BooleanJassValue value) {
		throw new UnsupportedOperationException("Invalid binary operation: null and boolean");
	}

	@Override
	public JassValue accept(final IntegerJassValue value) {
		throw new UnsupportedOperationException("Invalid binary operation: null and integer");
	}

	@Override
	public JassValue accept(final RealJassValue value) {
		throw new UnsupportedOperationException("Invalid binary operation: null and real");
	}

	@Override
	public JassValue accept(final StringJassValue value) {
		return this.sign.apply(null, value.getValue());
	}

	@Override
	public JassValue accept(final CodeJassValue value) {
		throw new UnsupportedOperationException("Invalid binary operation: null and code");
	}

	@Override
	public JassValue accept(final ArrayJassValue value) {
		throw new UnsupportedOperationException("Invalid binary operation: null and array");
	}

	@Override
	public JassValue accept(final HandleJassValue value) {
		final HandleJassType rightHandType = value.getType();
		if (!rightHandType.isNullable()) {
			throw new UnsupportedOperationException("Cannot operate on null for type: " + rightHandType.getName());
		}
		// TODO would be nice not to have to call getNullValue here...
		return this.sign.apply(rightHandType.getNullValue(), value);
	}

	@Override
	public JassValue accept(final DummyJassValue value) {
		return value;
	}

	@Override
	public JassValue accept(final StructJassValue value) {
		throw new UnsupportedOperationException("Cannot perform arithmetic on struct");
	}

	@Override
	public JassValue accept(final StaticStructTypeJassValue value) {
		throw new UnsupportedOperationException("Cannot perform arithmetic on struct type");
	}

}
