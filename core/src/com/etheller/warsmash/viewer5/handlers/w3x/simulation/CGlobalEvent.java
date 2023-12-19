package com.etheller.warsmash.viewer5.handlers.w3x.simulation;

import com.etheller.interpreter.ast.scope.TriggerExecutionScope;
import com.etheller.interpreter.ast.scope.trigger.RemovableTriggerEvent;
import com.etheller.interpreter.ast.scope.trigger.Trigger;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.JassGameEventsWar3;

public interface CGlobalEvent extends RemovableTriggerEvent {

	JassGameEventsWar3 getEventType();

	Trigger getTrigger();

	void fire(CWidget spellAbilityUnit, TriggerExecutionScope unitSpellNoTargetScope);

}
