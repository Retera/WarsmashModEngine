package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action;

import java.util.Map;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.idcallbacks.ABIDCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABSingleAction;

public class ABActionCreateAbilityFromId implements ABSingleAction {

	private ABIDCallback id;

	@Override
	public void runAction(final CSimulation game, final CUnit caster, final Map<String, Object> localStore,
			final int castId) {
		War3ID theId = this.id.callback(game, caster, localStore, castId);
		CAbilityType<?> type = game.getAbilityData().getAbilityType(theId);
		if (type != null) {
			final CAbility ability = type.createAbility(game.getHandleIdAllocator().createId());
			localStore.put(ABLocalStoreKeys.LASTCREATEDABILITY, ability);
		} else {
			System.err.println("Failed to find an ability type definition for ability " + theId.asStringValue());
		}
	}

	@Override
	public String generateJassEquivalent(JassTextGenerator jassTextGenerator) {
		return "CreateAbilityFromIdAU(" + jassTextGenerator.getTriggerLocalStore() + ", "
				+ this.id.generateJassEquivalent(jassTextGenerator) + ")";
	}
}
