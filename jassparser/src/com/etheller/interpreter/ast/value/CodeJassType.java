package com.etheller.interpreter.ast.value;

public class CodeJassType extends PrimitiveJassType {

	public CodeJassType(final String name) {
		super(name, new CodeJassValue(null, -1));
	}

	@Override
	public boolean isNullable() {
		return true;
	}
}
