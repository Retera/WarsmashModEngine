package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.fx;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.floatcallbacks.ABFloatCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.locationcallbacks.ABLocationCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;

public class ABActionModifyTerrainVertex implements ABAction {

	private ABLocationCallback location;
	private ABFloatCallback dz;

	@Override
	public void runAction(final CSimulation game, final CUnit caster, final Map<String, Object> localStore,
			final int castId) {
		final AbilityPointTarget loc = this.location.callback(game, caster, localStore, castId);
		game.adjustTerrain(loc.x, loc.y, dz.callback(game, caster, localStore, castId));
	}
}
