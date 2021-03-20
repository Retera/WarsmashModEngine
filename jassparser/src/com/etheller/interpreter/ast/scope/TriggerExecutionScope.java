package com.etheller.interpreter.ast.scope;

import com.etheller.interpreter.ast.scope.trigger.Trigger;

public class TriggerExecutionScope {
	public static final TriggerExecutionScope EMPTY = new TriggerExecutionScope(null);

	private final Trigger triggeringTrigger;

	public TriggerExecutionScope(final Trigger triggeringTrigger) {
		this.triggeringTrigger = triggeringTrigger;
	}

	public Trigger getTriggeringTrigger() {
		return this.triggeringTrigger;
	}

}
