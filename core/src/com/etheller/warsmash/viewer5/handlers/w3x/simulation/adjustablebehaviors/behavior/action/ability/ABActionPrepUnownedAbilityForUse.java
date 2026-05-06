package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.action.ability;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.ability.ABAbilityCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.unit.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABSingleAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;

public class ABActionPrepUnownedAbilityForUse implements ABSingleAction {

	private ABUnitCallback unit;
	private ABAbilityCallback ability;

	@Override
	public void runAction(final CUnit caster, final ABLocalDataStore localStore, final int castId) {
		final CAbility theAbility = this.ability.callback(caster, localStore, castId);
		CUnit theUnit = caster;
		if (unit != null) {
			theUnit = unit.callback(caster, localStore, castId);
		}
		if (theAbility != null) {
			theAbility.onAddDisabled(localStore.game, theUnit);
			theAbility.onAdd(localStore.game, theUnit);
		}
	}

	@Override
	public String generateJassEquivalent(JassTextGenerator jassTextGenerator) {
		return "AddUnitAbilityAU(" + jassTextGenerator.getTriggerLocalStore() + ", "
				+ this.unit.generateJassEquivalent(jassTextGenerator) + ", "
				+ this.ability.generateJassEquivalent(jassTextGenerator) + ")";
	}
}
