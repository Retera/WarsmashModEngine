package com.etheller.interpreter.ast.value;

public class DummyJassValue implements JassValue {
	public static final DummyJassValue PAUSE_FOR_SLEEP = new DummyJassValue();

	@Override
	public <TYPE> TYPE visit(final JassValueVisitor<TYPE> visitor) {
		return visitor.accept(this);
	}

}
