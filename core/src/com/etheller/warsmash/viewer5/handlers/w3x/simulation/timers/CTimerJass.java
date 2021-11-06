package com.etheller.warsmash.viewer5.handlers.w3x.simulation.timers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.etheller.interpreter.ast.debug.JassException;
import com.etheller.interpreter.ast.function.JassFunction;
import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.scope.trigger.Trigger;
import com.etheller.interpreter.ast.util.CHandle;
import com.etheller.warsmash.parsers.jass.scope.CommonTriggerExecutionScope;

public class CTimerJass extends CTimer implements CHandle {
	private JassFunction handlerFunc;
	private final GlobalScope jassGlobalScope;
	private final int handleId;
	private final List<Trigger> eventTriggers = new ArrayList<>();

	public CTimerJass(final GlobalScope jassGlobalScope, final int handleId) {
		this.jassGlobalScope = jassGlobalScope;
		this.handleId = handleId;
	}

	public void setHandlerFunc(final JassFunction handlerFunc) {
		this.handlerFunc = handlerFunc;
	}

	@Override
	public void onFire() {
		final CommonTriggerExecutionScope handlerScope = CommonTriggerExecutionScope.expiringTimer(null, this);
		try {
			if (this.handlerFunc != null) {
				this.handlerFunc.call(Collections.emptyList(), this.jassGlobalScope, handlerScope);
			}
		}
		catch (final Exception e) {
			throw new JassException(this.jassGlobalScope, "Exception during jass timer fire", e);
		}
		for (final Trigger trigger : this.eventTriggers) {
			final CommonTriggerExecutionScope executionScope = CommonTriggerExecutionScope.expiringTimer(trigger, this);
			this.jassGlobalScope.queueTrigger(null, null, trigger, executionScope, executionScope);
		}
	}

	public void addEvent(final Trigger trigger) {
		this.eventTriggers.add(trigger);
	}

	public void removeEvent(final Trigger trigger) {
		this.eventTriggers.remove(trigger);
	}

	@Override
	public int getHandleId() {
		return this.handleId;
	}
}
