package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.action.fx.terrain;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.floats.ABFloatCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.integers.ABIntegerCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.location.ABLocationCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.effects.ABTerrainEffect;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.effects.ABTerrainWaveEffect;

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
	public void runAction(final CUnit caster, final ABLocalDataStore localStore,
			final int castId) {
		final AbilityPointTarget loc = this.startLocation.callback(caster, localStore, castId);
		final AbilityPointTarget tarloc = this.targetLocation.callback(caster, localStore, castId);
		final float rad = radius.callback(caster, localStore, castId);
		final float theDepth = depth.callback(caster, localStore, castId);
		final float dist = distance.callback(caster, localStore, castId);
		final float spd = speed.callback(caster, localStore, castId);
		final int trlTime = trailTime.callback(caster, localStore, castId);
		int cnt = 1;
		if (count != null) {
			cnt = count.callback(caster, localStore, castId);
		}
		
		ABTerrainEffect fx = new ABTerrainWaveEffect(localStore.game, loc, tarloc, rad, theDepth, dist, spd, trlTime, cnt);
		localStore.game.registerTimer(fx);
		localStore.put(ABLocalStoreKeys.LASTCREATEDFX, fx);
	}
}
