package com.etheller.interpreter.ast.value;

import com.etheller.interpreter.ast.execution.JassStackFrame;
import com.etheller.interpreter.ast.function.JassFunction;

public class MethodJassValue extends CodeJassValue {

	private final JassValue thisStruct;

	public MethodJassValue(final JassValue thisStruct, final JassFunction value,
			final Integer userFunctionInstructionPtr) {
		super(value, userFunctionInstructionPtr);
		this.thisStruct = thisStruct;
	}

	@Override
	public void initStack(final JassStackFrame stackFrame) {
		super.initStack(stackFrame);
		stackFrame.push(this.thisStruct);
	}
}
