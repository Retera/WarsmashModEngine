package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.unit.animation;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.SequenceUtils;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.PrimaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.booleancallbacks.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.floatcallbacks.ABFloatCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.stringcallbacks.ABStringCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.unitcallbacks.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;

public class ABActionPlayAnimation implements ABAction {

	private ABUnitCallback unit;
	private ABStringCallback tag;

	private ABBooleanCallback force;
	private ABFloatCallback speed;
	private ABBooleanCallback allowVariations;

	@Override
	public void runAction(CSimulation game, CUnit caster, Map<String, Object> localStore, final int castId) {
		CUnit targetUnit = unit.callback(game, caster, localStore, castId);
		boolean f = true;
		float s = 1f;
		boolean av = false;
		if (force != null) {
			f = force.callback(game, caster, localStore, castId);
		}
		if (speed != null) {
			s = speed.callback(game, caster, localStore, castId);
		}
		if (allowVariations != null) {
			av = allowVariations.callback(game, caster, localStore, castId);
		}
		
		targetUnit.getUnitAnimationListener().playAnimation(f, PrimaryTag.valueOf(tag.callback(game, caster, localStore, castId)), SequenceUtils.EMPTY, s, av);
	}

}
