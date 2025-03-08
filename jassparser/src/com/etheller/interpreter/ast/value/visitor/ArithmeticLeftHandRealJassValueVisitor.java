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

public class ArithmeticLeftHandRealJassValueVisitor implements JassValueVisitor<JassValue> {
	public static final ArithmeticLeftHandRealJassValueVisitor INSTANCE = new ArithmeticLeftHandRealJassValueVisitor();
	private RealJassValue leftHand;
	private ArithmeticSign sign;

	public ArithmeticLeftHandRealJassValueVisitor reset(final RealJassValue leftHand, final ArithmeticSign sign) {
		this.leftHand = leftHand;
		this.sign = sign;
		return this;
	}

	@Override
	public JassValue accept(final BooleanJassValue value) {
		throw new UnsupportedOperationException("Cannot perform integer arithmetic on boolean");
	}

	@Override
	public JassValue accept(final IntegerJassValue value) {
		return this.sign.apply(this.leftHand, value);
	}

	@Override
	public JassValue accept(final RealJassValue value) {
		return this.sign.apply(this.leftHand, value);
	}

	@Override
	public JassValue accept(final StringJassValue value) {
		return this.sign.apply(this.leftHand.toString(), value.getValue());
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
