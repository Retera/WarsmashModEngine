package com.etheller.warsmash.viewer5.handlers.w3x.simulation.timers;

import java.util.Collections;

import com.etheller.interpreter.ast.function.JassFunction;
import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.scope.TriggerExecutionScope;

public class CTimerJass extends CTimer {
	private JassFunction handlerFunc;
	private final GlobalScope jassGlobalScope;

	public CTimerJass(final GlobalScope jassGlobalScope) {
		this.jassGlobalScope = jassGlobalScope;
	}

	public void setHandlerFunc(final JassFunction handlerFunc) {
		this.handlerFunc = handlerFunc;
	}

	@Override
	public void onFire() {
		this.handlerFunc.call(Collections.emptyList(), this.jassGlobalScope, TriggerExecutionScope.EMPTY);
	}
}
