package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.action.ability;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.id.ABIDCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABSingleAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;

public class ABActionCreateAbilityFromId implements ABSingleAction {

	private ABIDCallback id;

	@Override
	public void runAction(final CUnit caster, final ABLocalDataStore localStore,
			final int castId) {
		War3ID theId = this.id.callback(caster, localStore, castId);
		CAbilityType<?> type = localStore.game.getAbilityData().getAbilityType(theId);
		if (type != null) {
			final CAbility ability = type.createAbility(localStore.game.getHandleIdAllocator().createId());
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
