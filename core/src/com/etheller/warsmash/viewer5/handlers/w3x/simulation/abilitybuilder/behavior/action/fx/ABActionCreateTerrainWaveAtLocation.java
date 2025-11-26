package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.fx;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.floatcallbacks.ABFloatCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.integercallbacks.ABIntegerCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.locationcallbacks.ABLocationCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.effects.ABTerrainEffect;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.effects.ABTerrainWaveEffect;

public class ABActionCreateTerrainWaveAtLocation implements ABAction {

	private ABLocationCallback startLocation;
	private ABLocationCallback targetLocation;
	private ABFloatCallback radius;
	private ABFloatCallback depth;
	private ABFloatCallback distance;
	private ABFloatCallback speed;
	private ABIntegerCallback trailTime;
	private ABIntegerCallback count;

	@Override
	public void runAction(final CSimulation game, final CUnit caster, final Map<String, Object> localStore,
			final int castId) {
		final AbilityPointTarget loc = this.startLocation.callback(game, caster, localStore, castId);
		final AbilityPointTarget tarloc = this.targetLocation.callback(game, caster, localStore, castId);
		final float rad = radius.callback(game, caster, localStore, castId);
		final float theDepth = depth.callback(game, caster, localStore, castId);
		final float dist = distance.callback(game, caster, localStore, castId);
		final float spd = speed.callback(game, caster, localStore, castId);
		final int trlTime = trailTime.callback(game, caster, localStore, castId);
		int cnt = 1;
		if (count != null) {
			cnt = count.callback(game, caster, localStore, castId);
		}
		
		ABTerrainEffect fx = new ABTerrainWaveEffect(game, loc, tarloc, rad, theDepth, dist, spd, trlTime, cnt);
		game.registerTimer(fx);
		localStore.put(ABLocalStoreKeys.LASTCREATEDFX, fx);
	}
}
