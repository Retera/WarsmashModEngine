package com.etheller.interpreter.ast.value;

public class RealJassValue implements JassValue {
	public static final JassValue ZERO = new RealJassValue(0);
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

	public static RealJassValue of(final double value) {
		return new RealJassValue(value);
	}
}
