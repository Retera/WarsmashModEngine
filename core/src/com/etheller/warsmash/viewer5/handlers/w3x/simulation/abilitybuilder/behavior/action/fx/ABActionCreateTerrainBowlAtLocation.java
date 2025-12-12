package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.fx;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.floatcallbacks.ABFloatCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.locationcallbacks.ABLocationCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.effects.ABTerrainBowlEffect;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.effects.ABTerrainEffect;

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
	public void runAction(final CSimulation game, final CUnit caster, final Map<String, Object> localStore,
			final int castId) {
		final AbilityPointTarget loc = this.location.callback(game, caster, localStore, castId);
		final float rad = radius.callback(game, caster, localStore, castId);
		final float theDepth = -1 * depth.callback(game, caster, localStore, castId);
		float startDep = 0;
		if (startDepth != null) {
			startDep = startDepth.callback(game, caster, localStore, castId);
		}
		
		float snkTm = 0;
		float sttTm = 0;
		float fllTm = 0;
		float stpTm = 0;
		if (sinkTime != null) {
			snkTm = sinkTime.callback(game, caster, localStore, castId);
		}
		if (staticTime != null) {
			sttTm = staticTime.callback(game, caster, localStore, castId);
		}
		if (fillTime != null) {
			fllTm = fillTime.callback(game, caster, localStore, castId);
		}
		if (stopDuration != null) {
			stpTm = stopDuration.callback(game, caster, localStore, castId);
		}
		
		ABTerrainEffect fx = new ABTerrainBowlEffect(game, loc, rad, theDepth, snkTm, sttTm, fllTm, stpTm, startDep);
		game.registerTimer(fx);
		localStore.put(ABLocalStoreKeys.LASTCREATEDFX, fx);
	}
}
