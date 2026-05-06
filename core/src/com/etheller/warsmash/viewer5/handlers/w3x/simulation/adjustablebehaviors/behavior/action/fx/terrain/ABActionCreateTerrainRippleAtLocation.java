package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.action.fx.terrain;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.floats.ABFloatCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.integers.ABIntegerCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.location.ABLocationCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.condition.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.effects.ABTerrainEffect;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.effects.ABTerrainRippleEffect;

public class ABActionCreateTerrainRippleAtLocation implements ABAction {

	private ABLocationCallback location;

	private ABFloatCallback startRadius;
	private ABFloatCallback finalRadius;
	private ABFloatCallback radius;

	private ABFloatCallback depth;

	private ABFloatCallback duration;
	private ABIntegerCallback rippleCount;

	private ABFloatCallback period;

	private ABIntegerCallback spaceWaves;
	private ABIntegerCallback timeWaves;
	private ABBooleanCallback onlyNegative;

	@Override
	public void runAction(final CUnit caster, final ABLocalDataStore localStore, final int castId) {
		final AbilityPointTarget loc = this.location.callback(caster, localStore, castId);

		float startRad = 0;
		float endRad = 0;
		if (radius != null) {
			startRad = radius.callback(caster, localStore, castId);
			endRad = startRad;
		} else {
			startRad = startRadius.callback(caster, localStore, castId);
			endRad = finalRadius.callback(caster, localStore, castId);
		}
		final float theDepth = depth.callback(caster, localStore, castId);
		final float thePeriod = period.callback(caster, localStore, castId);
		final int swaves = spaceWaves.callback(caster, localStore, castId);
		final int twaves = timeWaves.callback(caster, localStore, castId);
		final boolean onlyNeg = onlyNegative.callback(caster, localStore, castId);

		int intervals = 0;
		if (rippleCount != null) {
			intervals = rippleCount.callback(caster, localStore, castId);
		} else {
			final float dur = duration.callback(caster, localStore, castId);
			intervals = ((int) dur) * 2;
		}

		ABTerrainEffect fx = new ABTerrainRippleEffect(localStore.game, loc, startRad, endRad, theDepth, thePeriod,
				intervals, swaves, twaves, onlyNeg);
		localStore.game.registerTimer(fx);
		localStore.put(ABLocalStoreKeys.LASTCREATEDFX, fx);
	}
}
