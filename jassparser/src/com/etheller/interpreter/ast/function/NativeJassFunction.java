package com.etheller.interpreter.ast.function;

import java.util.List;

import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.scope.LocalScope;
import com.etheller.interpreter.ast.scope.TriggerExecutionScope;
import com.etheller.interpreter.ast.value.JassType;
import com.etheller.interpreter.ast.value.JassValue;

public class NativeJassFunction extends AbstractJassFunction {
	private final String name;
	private final JassFunction implementation;

	public NativeJassFunction(final List<JassParameter> parameters, final JassType returnType, final String name,
			final JassFunction impl) {
		super(parameters, returnType);
		this.name = name;
		this.implementation = impl;
	}

	@Override
	protected JassValue innerCall(final List<JassValue> arguments, final GlobalScope globalScope,
			final TriggerExecutionScope triggerScope, final LocalScope localScope) {
		return this.implementation.call(arguments, globalScope, triggerScope);
	}
}
