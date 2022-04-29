package com.etheller.interpreter.ast.scope.trigger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.etheller.interpreter.ast.debug.JassException;
import com.etheller.interpreter.ast.function.JassFunction;
import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.scope.TriggerExecutionScope;
import com.etheller.interpreter.ast.util.CHandle;

public class Trigger implements CHandle {
	private static int STUPID_STATIC_TRIGGER_COUNT_DELETE_THIS_LATER = 452354453;
	private final int handleId = STUPID_STATIC_TRIGGER_COUNT_DELETE_THIS_LATER++;
	private final List<TriggerBooleanExpression> conditions = new ArrayList<>();
	private final List<JassFunction> actions = new ArrayList<>();
	private int evalCount;
	private int execCount;
	private boolean enabled = true;
	// used for eval
	private transient final TriggerExecutionScope triggerExecutionScope = new TriggerExecutionScope(this);
	private boolean waitOnSleeps = true;

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

	public void clearConditions() {
		this.conditions.clear();
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

	public void execute(final GlobalScope globalScope, final TriggerExecutionScope triggerScope) {
		if (!this.enabled) {
			return;
		}
		for (final JassFunction action : this.actions) {
			try {
				action.call(Collections.emptyList(), globalScope, triggerScope);
			}
			catch (final Exception e) {
				if ((e.getMessage() != null) && e.getMessage().startsWith("Needs to sleep")) {
					// TODO not good design
					e.printStackTrace();
				}
				else {
					throw new JassException(globalScope, "Exception during Trigger action execute", e);
				}
			}
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

	public void reset() {
		this.actions.clear();
		this.conditions.clear();
		this.evalCount = 0;
		this.execCount = 0;
		this.enabled = true;
		this.waitOnSleeps = true;
	}

	public void setWaitOnSleeps(final boolean waitOnSleeps) {
		this.waitOnSleeps = waitOnSleeps;
	}

	public boolean isWaitOnSleeps() {
		return this.waitOnSleeps;
	}

	@Override
	public int getHandleId() {
		return this.handleId;
	}

}
