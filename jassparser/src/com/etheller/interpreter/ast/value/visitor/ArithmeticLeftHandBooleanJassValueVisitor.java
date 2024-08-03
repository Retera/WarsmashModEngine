package com.etheller.interpreter.ast.value.visitor;

import com.etheller.interpreter.ast.expression.ArithmeticSign;
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

public class ArithmeticLeftHandBooleanJassValueVisitor implements JassValueVisitor<JassValue> {
	public static final ArithmeticLeftHandBooleanJassValueVisitor INSTANCE = new ArithmeticLeftHandBooleanJassValueVisitor();

	private BooleanJassValue leftHand;
	private ArithmeticSign sign;

	public ArithmeticLeftHandBooleanJassValueVisitor reset(final BooleanJassValue leftHand, final ArithmeticSign sign) {
		this.leftHand = leftHand;
		this.sign = sign;
		return this;
	}

	@Override
	public JassValue accept(final BooleanJassValue value) {
		return this.sign.apply(this.leftHand, value);
	}

	@Override
	public JassValue accept(final IntegerJassValue value) {
		throw new UnsupportedOperationException("Cannot perform boolean arithmetic on integer");
	}

	@Override
	public JassValue accept(final RealJassValue value) {
		throw new UnsupportedOperationException("Cannot perform boolean arithmetic on real");
	}

	@Override
	public JassValue accept(final StringJassValue value) {
		throw new UnsupportedOperationException("Cannot perform arithmetic on string");
		// uncomment the below if you decide you build a mod where I2S is no longer
		// necessary, probably:
//		return new StringJassValue(this.leftHand.toString() + value.getValue());
	}

	@Override
	public JassValue accept(final CodeJassValue value) {
		throw new UnsupportedOperationException("Cannot perform arithmetic on code");
	}

	@Override
	public JassValue accept(final ArrayJassValue value) {
		throw new UnsupportedOperationException("Cannot perform arithmetic on array");
	}

	@Override
	public JassValue accept(final HandleJassValue value) {
		throw new UnsupportedOperationException("Cannot perform arithmetic on handle");
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
