package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.action.fx.terrain;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.floats.ABFloatCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.location.ABLocationCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.effects.ABTerrainBowlEffect;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.effects.ABTerrainEffect;

public class ABActionCreateTerrainBowlAtLocation implements ABAction {

	private ABLocationCallback location;
	private ABFloatCallback radius;
	private ABFloatCallback depth;
	private ABFloatCallback sinkTime;
	private ABFloatCallback staticTime;
	private ABFloatCallback fillTime;

	private ABFloatCallback stopDuration;
	private ABFloatCallback startDepth;

	@Override
	public void runAction(final CUnit caster, final ABLocalDataStore localStore, final int castId) {
		final AbilityPointTarget loc = this.location.callback(caster, localStore, castId);
		final float rad = radius.callback(caster, localStore, castId);
		final float theDepth = -1 * depth.callback(caster, localStore, castId);
		float startDep = 0;
		if (startDepth != null) {
			startDep = startDepth.callback(caster, localStore, castId);
		}

		float snkTm = 0;
		float sttTm = 0;
		float fllTm = 0;
		float stpTm = 0;
		if (sinkTime != null) {
			snkTm = sinkTime.callback(caster, localStore, castId);
		}
		if (staticTime != null) {
			sttTm = staticTime.callback(caster, localStore, castId);
		}
		if (fillTime != null) {
			fllTm = fillTime.callback(caster, localStore, castId);
		}
		if (stopDuration != null) {
			stpTm = stopDuration.callback(caster, localStore, castId);
		}

		ABTerrainEffect fx = new ABTerrainBowlEffect(localStore.game, loc, rad, theDepth, snkTm, sttTm, fllTm, stpTm,
				startDep);
		localStore.game.registerTimer(fx);
		localStore.put(ABLocalStoreKeys.LASTCREATEDFX, fx);
	}
}
