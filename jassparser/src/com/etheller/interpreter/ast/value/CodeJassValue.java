package com.etheller.interpreter.ast.value;

import com.etheller.interpreter.ast.function.JassFunction;

public class CodeJassValue implements JassValue {
	private final JassFunction value;
	private final Integer userFunctionInstructionPtr;

	public CodeJassValue(final JassFunction value, final Integer userFunctionInstructionPtr) {
		this.value = value;
		this.userFunctionInstructionPtr = userFunctionInstructionPtr;
	}

	public JassFunction getValue() {
		return this.value;
	}

	public Integer getUserFunctionInstructionPtr() {
		return this.userFunctionInstructionPtr;
	}

	@Override
	public <TYPE> TYPE visit(final JassValueVisitor<TYPE> visitor) {
		return visitor.accept(this);
	}

}
