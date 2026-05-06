package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.action.ability;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.parsers.jass.JassTextGeneratorType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.ability.ABAbilityBuilderActiveAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABSingleAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;

public class ABActionDeactivateToggledAbility implements ABSingleAction {

	@Override
	public void runAction(final CUnit caster, final ABLocalDataStore localStore, final int castId) {
		ABAbilityBuilderActiveAbility ability = (ABAbilityBuilderActiveAbility) localStore.originAbility;
		ability.deactivate(localStore.game, caster);
	}

	@Override
	public String generateJassEquivalent(final JassTextGenerator jassTextGenerator) {
		return "AbilityDeactivate(" + jassTextGenerator.getCaster() + ", " + jassTextGenerator
				.getUserDataExpr("AB_LOCAL_STORE_KEY_TOGGLEDABILITY", JassTextGeneratorType.AbilityHandle) + ")";
	}
}
