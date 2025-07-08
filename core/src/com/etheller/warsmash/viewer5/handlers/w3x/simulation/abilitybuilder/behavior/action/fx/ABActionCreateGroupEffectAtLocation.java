package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.fx;

import java.util.Map;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.floatcallbacks.ABFloatCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.idcallbacks.ABIDCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.locationcallbacks.ABLocationCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.effects.ABGroupEffect;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CEffectType;

public class ABActionCreateGroupEffectAtLocation implements ABAction {

	private ABLocationCallback location;
	private ABFloatCallback radius;
	private ABIDCallback id;
	private CEffectType effectType;

	@Override
	public void runAction(final CSimulation game, final CUnit caster, final Map<String, Object> localStore,
			final int castId) {
		final AbilityPointTarget loc = this.location.callback(game, caster, localStore, castId);
		float rad = radius.callback(game, caster, localStore, castId);
		War3ID theId = null;
		if (id == null) {
			theId = (War3ID) localStore.get(ABLocalStoreKeys.ALIAS);
		} else {
			theId = id.callback(game, caster, localStore, castId);
		}
		final ABGroupEffect ret = new ABGroupEffect(loc, rad, theId, effectType);
		game.registerTimer(ret);
		localStore.put(ABLocalStoreKeys.LASTCREATEDFX, ret);
	}
}
