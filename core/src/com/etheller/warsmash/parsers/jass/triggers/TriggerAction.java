package com.etheller.warsmash.parsers.jass.triggers;

import com.etheller.interpreter.ast.function.JassFunction;
import com.etheller.interpreter.ast.scope.trigger.Trigger;

public class TriggerAction {
	private final Trigger trigger;
	private final JassFunction actionFunc;
	private final int actionIndex;

	public TriggerAction(final Trigger trigger, final JassFunction actionFunc, final int actionIndex) {
		this.trigger = trigger;
		this.actionFunc = actionFunc;
		this.actionIndex = actionIndex;
	}

	public Trigger getTrigger() {
		return this.trigger;
	}

	public JassFunction getActionFunc() {
		return this.actionFunc;
	}

	public int getActionIndex() {
		return this.actionIndex;
	}
}
