package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.fx;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.booleancallbacks.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.floatcallbacks.ABFloatCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.integercallbacks.ABIntegerCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.locationcallbacks.ABLocationCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.effects.ABTerrainEffect;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.effects.ABTerrainRippleEffect;

public class ABActionCreateTerrainRippleAtLocation implements ABAction {

	private ABLocationCallback location;
	private ABFloatCallback startRadius;
	private ABFloatCallback finalRadius;
	private ABFloatCallback depth;
	private ABFloatCallback duration;
	private ABFloatCallback period;
	private ABIntegerCallback spaceWaves;
	private ABIntegerCallback timeWaves;
	private ABBooleanCallback onlyNegative;

	@Override
	public void runAction(final CSimulation game, final CUnit caster, final Map<String, Object> localStore,
			final int castId) {
		final AbilityPointTarget loc = this.location.callback(game, caster, localStore, castId);
		final float startRad = startRadius.callback(game, caster, localStore, castId);
		final float endRad = finalRadius.callback(game, caster, localStore, castId);
		final float theDepth = depth.callback(game, caster, localStore, castId);
		final float thePeriod = period.callback(game, caster, localStore, castId);
		final int swaves = spaceWaves.callback(game, caster, localStore, castId);
		final int twaves = timeWaves.callback(game, caster, localStore, castId);
		final boolean onlyNeg = onlyNegative.callback(game, caster, localStore, castId);
		
		final float dur = duration.callback(game, caster, localStore, castId);
		final int intervals = (int)dur;
		
		ABTerrainEffect fx = new ABTerrainRippleEffect(game, loc, startRad, endRad, theDepth, thePeriod, 2 * intervals, swaves, twaves, onlyNeg);
		game.registerTimer(fx);
		localStore.put(ABLocalStoreKeys.LASTCREATEDFX, fx);
	}
}
