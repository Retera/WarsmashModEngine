package com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit;

import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.scope.TriggerExecutionScope;
import com.etheller.interpreter.ast.scope.trigger.RemovableTriggerEvent;
import com.etheller.interpreter.ast.scope.trigger.Trigger;
import com.etheller.interpreter.ast.scope.trigger.TriggerBooleanExpression;
import com.etheller.warsmash.parsers.jass.scope.CommonTriggerExecutionScope;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CDestructable;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CItem;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.JassGameEventsWar3;

public class CWidgetEvent extends RemovableTriggerEvent {
	private final GlobalScope globalScope;
	private final CWidget widget;
	private final Trigger trigger;
	private final JassGameEventsWar3 eventType;
	private final TriggerBooleanExpression filter;

	public CWidgetEvent(final GlobalScope globalScope, final CWidget widget, final Trigger trigger,
			final JassGameEventsWar3 eventType, final TriggerBooleanExpression filter) {
		super(trigger);
		this.globalScope = globalScope;
		this.widget = widget;
		this.trigger = trigger;
		this.eventType = eventType;
		this.filter = filter;
	}

	public Trigger getTrigger() {
		return this.trigger;
	}

	public JassGameEventsWar3 getEventType() {
		return this.eventType;
	}

	@Override
	public void remove() {
		this.widget.removeEvent(this);
	}

	public void fire(final CWidget triggerWidget, final TriggerExecutionScope scope) {
		this.globalScope.queueTrigger(this.filter, triggerWidget.visit(ScopeBuilder.INSTANCE.reset(scope)),
				this.trigger, scope, scope);
	}

	private static final class ScopeBuilder implements AbilityTargetVisitor<CommonTriggerExecutionScope> {
		public static final ScopeBuilder INSTANCE = new ScopeBuilder();
		private TriggerExecutionScope parentScope;

		public ScopeBuilder reset(final TriggerExecutionScope parentScope) {
			this.parentScope = parentScope;
			return this;
		}

		@Override
		public CommonTriggerExecutionScope accept(final AbilityPointTarget target) {
			throw new IllegalStateException("what?");
		}

		@Override
		public CommonTriggerExecutionScope accept(final CUnit target) {
			return CommonTriggerExecutionScope.filterScope(this.parentScope, target);
		}

		@Override
		public CommonTriggerExecutionScope accept(final CDestructable target) {
			return CommonTriggerExecutionScope.filterScope(this.parentScope, target);
		}

		@Override
		public CommonTriggerExecutionScope accept(final CItem target) {
			return CommonTriggerExecutionScope.filterScope(this.parentScope, target);
		}

	}
}
