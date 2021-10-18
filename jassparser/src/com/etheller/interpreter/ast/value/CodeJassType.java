package com.etheller.interpreter.ast.value;

public class CodeJassType extends PrimitiveJassType {

	public CodeJassType(final String name) {
		super(name);
	}

	@Override
	public boolean isNullable() {
		return true;
	}

	@Override
	public JassValue getNullValue() {
		return new CodeJassValue(null);
	}
}
