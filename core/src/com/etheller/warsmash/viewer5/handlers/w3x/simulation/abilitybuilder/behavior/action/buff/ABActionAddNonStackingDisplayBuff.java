package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.buff;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.CBuff;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.buffcallbacks.ABBuffCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.stringcallbacks.ABStringCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.unitcallbacks.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;

public class ABActionAddNonStackingDisplayBuff implements ABAction {

	private ABUnitCallback target;
	private ABStringCallback key;
	private ABBuffCallback buff;

	public void runAction(final CSimulation game, final CUnit caster, final Map<String, Object> localStore, final int castId) {
		CBuff ability = buff.callback(game, caster, localStore, castId);
		System.err.println(game.getGameTurnTick() + " Trying to add " + ability.getHandleId());
		target.callback(game, caster, localStore, castId).addNonStackingDisplayBuff(game,
				key.callback(game, caster, localStore, castId), ability);
		System.err.println(game.getGameTurnTick() + " Tried to add " + ability.getHandleId());
		localStore.put(ABLocalStoreKeys.LASTADDEDBUFF, ability);
	}
}
