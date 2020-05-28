package com.etheller.interpreter.ast.value;

import com.etheller.interpreter.ast.function.JassFunction;

public class CodeJassValue implements JassValue {
	private final JassFunction value;

	public CodeJassValue(final JassFunction value) {
		this.value = value;
	}

	public JassFunction getValue() {
		return value;
	}

	@Override
	public <TYPE> TYPE visit(final JassValueVisitor<TYPE> visitor) {
		return visitor.accept(this);
	}

}
