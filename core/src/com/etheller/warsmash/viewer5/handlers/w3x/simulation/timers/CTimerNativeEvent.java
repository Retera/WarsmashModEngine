package com.etheller.warsmash.viewer5.handlers.w3x.simulation.timers;

import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.scope.TriggerExecutionScope;
import com.etheller.interpreter.ast.scope.trigger.Trigger;

public class CTimerNativeEvent extends CTimer {
	private final GlobalScope jassGlobalScope;
	private final Trigger trigger;

	public CTimerNativeEvent(final GlobalScope jassGlobalScope, final Trigger trigger) {
		this.jassGlobalScope = jassGlobalScope;
		this.trigger = trigger;
	}

	@Override
	public void onFire() {
		final TriggerExecutionScope triggerScope = new TriggerExecutionScope(this.trigger);
		this.jassGlobalScope.queueTrigger(null, null, this.trigger, triggerScope, triggerScope);
	}

}
