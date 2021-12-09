package com.etheller.interpreter.ast.scope.variableevent;

import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.scope.TriggerExecutionScope;
import com.etheller.interpreter.ast.scope.trigger.Trigger;

public class VariableEvent {
	private final Trigger trigger;
	private final CLimitOp limitOp;
	private final double doubleValue;

	public VariableEvent(final Trigger trigger, final CLimitOp limitOp, final double doubleValue) {
		this.trigger = trigger;
		this.limitOp = limitOp;
		this.doubleValue = doubleValue;
	}

	public Trigger getTrigger() {
		return this.trigger;
	}

	public CLimitOp getLimitOp() {
		return this.limitOp;
	}

	public double getDoubleValue() {
		return this.doubleValue;
	}

	public boolean isMatching(final double realValue) {
		switch (this.limitOp) {
		case EQUAL:
			return this.doubleValue == realValue; // TODO probably bad, probably needs epsilon comparison, but what's
													// our default epsilon for this case?
		case GREATER_THAN:
			return realValue > this.doubleValue;
		case GREATER_THAN_OR_EQUAL:
			return realValue >= this.doubleValue;
		case LESS_THAN:
			return realValue < this.doubleValue;
		case LESS_THAN_OR_EQUAL:
			return realValue <= this.doubleValue;
		case NOT_EQUAL:
			return realValue != this.doubleValue;
		}
		throw new IllegalStateException();
	}

	public void fire(final GlobalScope globalScope) {
		final TriggerExecutionScope triggerScope = new TriggerExecutionScope(this.trigger);
		globalScope.queueTrigger(null, null, this.trigger, triggerScope, triggerScope);
	}
}
