package com.etheller.warsmash.viewer5.handlers.w3x.simulation.timers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.etheller.interpreter.ast.function.JassFunction;
import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.scope.trigger.Trigger;
import com.etheller.warsmash.parsers.jass.scope.CommonTriggerExecutionScope;

public class CTimerJass extends CTimer {
	private JassFunction handlerFunc;
	private final GlobalScope jassGlobalScope;
	private final List<Trigger> eventTriggers = new ArrayList<>();

	public CTimerJass(final GlobalScope jassGlobalScope) {
		this.jassGlobalScope = jassGlobalScope;
	}

	public void setHandlerFunc(final JassFunction handlerFunc) {
		this.handlerFunc = handlerFunc;
	}

	@Override
	public void onFire() {
		final CommonTriggerExecutionScope handlerScope = CommonTriggerExecutionScope.expiringTimer(null, this);
		this.handlerFunc.call(Collections.emptyList(), this.jassGlobalScope, handlerScope);
		for (final Trigger trigger : this.eventTriggers) {
			final CommonTriggerExecutionScope executionScope = CommonTriggerExecutionScope.expiringTimer(trigger, this);
			if (trigger.evaluate(this.jassGlobalScope, executionScope)) {
				trigger.execute(this.jassGlobalScope, executionScope);
			}
		}
	}

	public void addEvent(final Trigger trigger) {
		this.eventTriggers.add(trigger);
	}

	public void removeEvent(final Trigger trigger) {
		this.eventTriggers.remove(trigger);
	}
}
