package com.etheller.interpreter.ast.scope.trigger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.etheller.interpreter.ast.function.JassFunction;
import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.scope.TriggerExecutionScope;

public class Trigger {
	private final List<TriggerBooleanExpression> conditions = new ArrayList<>();
	private final List<JassFunction> actions = new ArrayList<>();
	private int evalCount;
	private int execCount;
	private boolean enabled = true;
	// used for eval
	private transient final TriggerExecutionScope triggerExecutionScope = new TriggerExecutionScope(this);

	public int addAction(final JassFunction function) {
		final int index = this.actions.size();
		this.actions.add(function);
		return index;
	}

	public int addCondition(final TriggerBooleanExpression boolexpr) {
		final int index = this.conditions.size();
		this.conditions.add(boolexpr);
		return index;
	}

	public void removeCondition(final TriggerBooleanExpression boolexpr) {
		this.conditions.remove(boolexpr);
	}

	public void removeConditionAtIndex(final int conditionIndex) {
		this.conditions.remove(conditionIndex);
	}

	public int getEvalCount() {
		return this.evalCount;
	}

	public int getExecCount() {
		return this.execCount;
	}

	public boolean evaluate(final GlobalScope globalScope, final TriggerExecutionScope triggerScope) {
		for (final TriggerBooleanExpression condition : this.conditions) {
			if (!condition.evaluate(globalScope, triggerScope)) {
				return false;
			}
		}
		return true;
	}

	public void execute(final GlobalScope globalScope) {
		for (final JassFunction action : this.actions) {
			action.call(Collections.emptyList(), globalScope, this.triggerExecutionScope);
		}
	}

	public boolean isEnabled() {
		return this.enabled;
	}

	public void setEnabled(final boolean enabled) {
		this.enabled = enabled;
	}

	public void destroy() {

	}
}
