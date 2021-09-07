package com.etheller.interpreter.ast.value;

public class RealJassValue implements JassValue {
	private final double value;

	public RealJassValue(final double value) {
		this.value = value;
	}

	public double getValue() {
		return this.value;
	}

	@Override
	public <TYPE> TYPE visit(final JassValueVisitor<TYPE> visitor) {
		return visitor.accept(this);
	}

	@Override
	public String toString() {
		return Double.toString(this.value);
	}
}
