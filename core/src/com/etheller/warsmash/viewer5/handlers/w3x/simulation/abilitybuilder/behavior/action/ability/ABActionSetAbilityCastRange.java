package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.ability;

import java.util.Map;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.parsers.jass.JassTextGeneratorType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.ability.AbilityBuilderActiveAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.floatcallbacks.ABFloatCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABSingleAction;

public class ABActionSetAbilityCastRange implements ABSingleAction {

	private ABFloatCallback range;

	@Override
	public void runAction(final CSimulation game, final CUnit caster, final Map<String, Object> localStore,
			final int castId) {
		final AbilityBuilderActiveAbility ability = (AbilityBuilderActiveAbility) localStore
				.get(ABLocalStoreKeys.TOGGLEDABILITY);
		ability.setCastRange(this.range.callback(game, caster, localStore, castId));
	}

	@Override
	public String generateJassEquivalent(final JassTextGenerator jassTextGenerator) {
		return "SetAbilityCastRange(" + jassTextGenerator.getCaster() + ", "
				+ jassTextGenerator.getUserDataExpr("AB_LOCAL_STORE_KEY_TOGGLEDABILITY", JassTextGeneratorType.AbilityHandle)
				+ ", " + this.range.generateJassEquivalent(jassTextGenerator) + ")";
	}
}
