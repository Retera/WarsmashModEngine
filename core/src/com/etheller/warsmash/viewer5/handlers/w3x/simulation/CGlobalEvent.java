package com.etheller.warsmash.viewer5.handlers.w3x.simulation;

import com.etheller.interpreter.ast.scope.TriggerExecutionScope;
import com.etheller.interpreter.ast.scope.trigger.RemovableTriggerEvent;
import com.etheller.interpreter.ast.scope.trigger.Trigger;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.JassGameEventsWar3;

public abstract class CGlobalEvent extends RemovableTriggerEvent {
	public CGlobalEvent(final Trigger t) {
		super(t);
	}

	public abstract JassGameEventsWar3 getEventType();

	public abstract Trigger getTrigger();

	public abstract void fire(CWidget spellAbilityUnit, TriggerExecutionScope unitSpellNoTargetScope);

}
