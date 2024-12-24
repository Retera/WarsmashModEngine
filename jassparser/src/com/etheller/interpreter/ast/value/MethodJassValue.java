package com.etheller.interpreter.ast.value;

import java.util.Objects;

import com.etheller.interpreter.ast.execution.JassStackFrame;

public class MethodJassValue extends CodeJassValue {

	private final JassValue thisStruct;

	public MethodJassValue(final JassValue thisStruct, final Integer userFunctionInstructionPtr) {
		super(userFunctionInstructionPtr);
		this.thisStruct = thisStruct;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = (prime * result) + Objects.hash(this.thisStruct);
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final MethodJassValue other = (MethodJassValue) obj;
		return Objects.equals(this.thisStruct, other.thisStruct);
	}

	@Override
	public void initStack(final JassStackFrame stackFrame) {
		super.initStack(stackFrame);
		stackFrame.push(this.thisStruct);
	}
}
