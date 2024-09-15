package com.etheller.interpreter.ast.scope.trigger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.etheller.interpreter.ast.debug.JassException;
import com.etheller.interpreter.ast.execution.JassThread;
import com.etheller.interpreter.ast.function.JassFunction;
import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.scope.TriggerExecutionScope;
import com.etheller.interpreter.ast.util.CHandle;
import com.etheller.interpreter.ast.value.CodeJassValue;
import com.etheller.interpreter.ast.value.JassValue;

public class Trigger implements CHandle {
	private static int STUPID_STATIC_TRIGGER_COUNT_DELETE_THIS_LATER = 452354453;
	private final int handleId = STUPID_STATIC_TRIGGER_COUNT_DELETE_THIS_LATER++;
	private final List<TriggerBooleanExpression> conditions = new ArrayList<>();
	private final List<JassFunction> actions = new ArrayList<>();
	private final List<RemovableTriggerEvent> events = new ArrayList<>();
	private int evalCount;
	private int execCount;
	private boolean enabled = true;
	// used for eval
	private transient final TriggerExecutionScope triggerExecutionScope = new TriggerExecutionScope(this);
	private boolean waitOnSleeps = true;

	public void addEvent(final RemovableTriggerEvent event) {
		this.events.add(event);
	}

	public int addAction(final JassFunction function) {
		final int index = this.actions.size();
		this.actions.add(function);
		return index;
	}

	public int addAction(final CodeJassValue function) {
		final int index = this.actions.size();
		this.actions.add(new JassThreadActionFunc(function));
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
				throw new JassException(globalScope, "Exception during Trigger action execute", e);
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
		for (final RemovableTriggerEvent event : this.events) {
			event.remove();
		}
		this.events.clear();
	}

	public void reset() {
//		this.actions.clear();
//		this.conditions.clear();
//		this.enabled = true;
//		this.waitOnSleeps = true;
		this.evalCount = 0;
		this.execCount = 0;
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

	private final class JassThreadActionFunc implements JassFunction {
		private final CodeJassValue codeJassValue;

		public JassThreadActionFunc(final CodeJassValue codeJassValue) {
			this.codeJassValue = codeJassValue;
		}

		@Override
		public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
				final TriggerExecutionScope triggerScope) {
			final JassThread triggerThread = globalScope.createThread(this.codeJassValue, triggerScope);
			globalScope.queueThread(triggerThread);
			return null;
		}

	}

	public TriggerExecutionScope getTriggerExecutionScope() {
		return this.triggerExecutionScope;
	}

	public void removeEvent(final RemovableTriggerEvent evt) {
		if (evt != null) {
			evt.remove();
			this.events.remove(evt);
		}
	}
}
