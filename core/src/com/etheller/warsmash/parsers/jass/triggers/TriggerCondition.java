package com.etheller.warsmash.parsers.jass.triggers;

import com.etheller.interpreter.ast.scope.trigger.Trigger;
import com.etheller.interpreter.ast.scope.trigger.TriggerBooleanExpression;

public class TriggerCondition {
	private final TriggerBooleanExpression boolexpr;
	private final Trigger trigger;
	private final int conditionIndex;

	public TriggerCondition(final TriggerBooleanExpression boolexpr, final Trigger trigger, final int index) {
		this.boolexpr = boolexpr;
		this.trigger = trigger;
		this.conditionIndex = index;
	}

	public TriggerBooleanExpression getBoolexpr() {
		return this.boolexpr;
	}

	public Trigger getTrigger() {
		return this.trigger;
	}

	public int getConditionIndex() {
		return this.conditionIndex;
	}
}
