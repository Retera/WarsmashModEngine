package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.floatcallbacks;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.CBuff;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.buffcallbacks.ABBuffCallback;

public class ABCallbackGetBuffDurationElapsed extends ABFloatCallback {

	private ABBuffCallback buff;

	@Override
	public Float callback(final CSimulation game, final CUnit caster, final Map<String, Object> localStore,
			final int castId) {
		final CBuff theBuff = buff.callback(game, caster, localStore, castId);
		return theBuff.getDurationMax() - theBuff.getDurationRemaining(game, caster);
	}

}
