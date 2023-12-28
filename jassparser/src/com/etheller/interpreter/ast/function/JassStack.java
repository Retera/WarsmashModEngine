package com.etheller.interpreter.ast.function;

import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.scope.LocalScope;
import com.etheller.interpreter.ast.scope.TriggerExecutionScope;

public abstract class JassStack {
	public GlobalScope globalScope;
	public LocalScope localScope;
	public TriggerExecutionScope triggerScope;

	public JassStack(final GlobalScope globalScope, final LocalScope localScope,
			final TriggerExecutionScope triggerScope) {
		this.globalScope = globalScope;
		this.localScope = localScope;
		this.triggerScope = triggerScope;
	}

	public abstract void update();
}
