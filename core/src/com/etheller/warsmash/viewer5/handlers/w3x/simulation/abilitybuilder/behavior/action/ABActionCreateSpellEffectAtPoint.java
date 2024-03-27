package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.floatcallbacks.ABFloatCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.idcallbacks.ABIDCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CEffectType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.SimulationRenderComponent;

public class ABActionCreateSpellEffectAtPoint implements ABAction {

	private ABFloatCallback x;
	private ABFloatCallback y;
	private ABFloatCallback facing;
	private ABIDCallback id;
	private CEffectType effectType;

	public void runAction(final CSimulation game, final CUnit caster, final Map<String, Object> localStore,
			final int castId) {
		float dir = 0;
		if (facing != null) {
			dir = facing.callback(game, caster, localStore, castId);
		}
		SimulationRenderComponent ret = game.spawnSpellEffectOnPoint(x.callback(game, caster, localStore, castId),
				y.callback(game, caster, localStore, castId), dir, this.id.callback(game, caster, localStore, castId),
				effectType, 0);
		localStore.put(ABLocalStoreKeys.LASTCREATEDFX, ret);
	}
}
