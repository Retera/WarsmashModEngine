package com.etheller.warsmash.viewer5.handlers.w3x.simulation;

import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.scope.TriggerExecutionScope;
import com.etheller.interpreter.ast.scope.trigger.Trigger;
import com.etheller.interpreter.ast.scope.trigger.TriggerBooleanExpression;
import com.etheller.warsmash.parsers.jass.scope.CommonTriggerExecutionScope;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.JassGameEventsWar3;

public class CGlobalWidgetEvent extends CGlobalEvent {
	private final CSimulation game;

	private final GlobalScope globalScope;
	private final Trigger trigger;
	private final JassGameEventsWar3 eventType;
	private final TriggerBooleanExpression filter;

	public CGlobalWidgetEvent(final CSimulation game, final GlobalScope globalScope, final Trigger trigger,
			final JassGameEventsWar3 eventType, final TriggerBooleanExpression filter) {
		super(trigger);
		this.game = game;
		this.globalScope = globalScope;
		this.trigger = trigger;
		this.eventType = eventType;
		this.filter = filter;
	}

	@Override
	public Trigger getTrigger() {
		return this.trigger;
	}

	@Override
	public JassGameEventsWar3 getEventType() {
		return this.eventType;
	}

	@Override
	public void remove() {
		this.game.removeGlobalEvent(this);
	}

	@Override
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
