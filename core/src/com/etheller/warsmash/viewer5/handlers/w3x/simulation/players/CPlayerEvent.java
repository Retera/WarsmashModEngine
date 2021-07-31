package com.etheller.warsmash.viewer5.handlers.w3x.simulation.players;

import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.scope.TriggerExecutionScope;
import com.etheller.interpreter.ast.scope.trigger.RemovableTriggerEvent;
import com.etheller.interpreter.ast.scope.trigger.Trigger;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.JassGameEventsWar3;

public class CPlayerEvent implements RemovableTriggerEvent {
	private final GlobalScope globalScope;
	private final CPlayerJass player;
	private final Trigger trigger;
	private final JassGameEventsWar3 eventType;

	public CPlayerEvent(final GlobalScope globalScope, final CPlayerJass player, final Trigger trigger,
			final JassGameEventsWar3 eventType) {
		this.globalScope = globalScope;
		this.player = player;
		this.trigger = trigger;
		this.eventType = eventType;
	}

	public Trigger getTrigger() {
		return this.trigger;
	}

	public JassGameEventsWar3 getEventType() {
		return this.eventType;
	}

	@Override
	public void remove() {
		this.player.removeEvent(this);
	}

	public void fire(final CUnit hero, final TriggerExecutionScope scope) {
		this.trigger.evaluate(this.globalScope, scope);
	}
}
