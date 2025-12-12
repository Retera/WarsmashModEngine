package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.ability;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.unitcallbacks.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.widget.ABWidgetCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.JassGameEventsWar3;

public class ABActionSendStartCastingEvents implements ABAction {

	private ABUnitCallback caster;
	private ABWidgetCallback target;
	@Override
	public void runAction(final CSimulation game, final CUnit basecaster, final Map<String, Object> localStore,
			final int castId) {
		CUnit theUnit = basecaster;
		CWidget theTarget = null;
		if (caster != null) {
			theUnit = caster.callback(game, basecaster, localStore, castId);
		}
		if (target != null) {
			theTarget = target.callback(game, basecaster, localStore, castId);
		}
		CAbility theAbility = (CAbility) localStore.get(ABLocalStoreKeys.ABILITY);
		theUnit.fireSpellEvents(game, JassGameEventsWar3.EVENT_UNIT_SPELL_CHANNEL, theAbility, theTarget);
		theUnit.fireSpellEvents(game, JassGameEventsWar3.EVENT_UNIT_SPELL_CAST, theAbility, theTarget);
		theUnit.fireSpellEvents(game, JassGameEventsWar3.EVENT_UNIT_SPELL_EFFECT, theAbility, theTarget);
	}
}
