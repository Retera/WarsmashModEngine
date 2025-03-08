package com.etheller.warsmash.viewer5.handlers.w3x.simulation.config;

import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.scope.trigger.RemovableTriggerEvent;
import com.etheller.interpreter.ast.scope.trigger.Trigger;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayerEvent;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.JassGameEventsWar3;

public class War3MapConfigPlayer extends CBasePlayer {

	public War3MapConfigPlayer(final CBasePlayer other) {
		super(other);
	}

	public War3MapConfigPlayer(final int id) {
		super(id);
	}

	@Override
	public RemovableTriggerEvent addEvent(final GlobalScope globalScope, final Trigger whichTrigger,
			final JassGameEventsWar3 eventType) {
		return RemovableTriggerEvent.doNothing(whichTrigger);
	}

	@Override
	public void removeEvent(final CPlayerEvent playerEvent) {
	}
}
