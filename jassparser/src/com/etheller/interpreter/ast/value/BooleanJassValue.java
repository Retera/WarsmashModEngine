package com.etheller.interpreter.ast.value;

public class BooleanJassValue implements JassValue {
	public static final BooleanJassValue TRUE = new BooleanJassValue(true);
	public static final BooleanJassValue FALSE = new BooleanJassValue(false);

	private final boolean value;

	public BooleanJassValue(final boolean value) {
		this.value = value;
	}

	public boolean getValue() {
		return this.value;
	}

	@Override
	public <TYPE> TYPE visit(final JassValueVisitor<TYPE> visitor) {
		return visitor.accept(this);
	}

	@Override
	public String toString() {
		return Boolean.toString(this.value);
	}

	public static BooleanJassValue inverse(final BooleanJassValue value) {
		if (value.value) {
			return FALSE;
		}
		else {
			return TRUE;
		}
	}

	public static BooleanJassValue of(final boolean flag) {
		if (flag) {
			return TRUE;
		}
		else {
			return FALSE;
		}
	}
}
