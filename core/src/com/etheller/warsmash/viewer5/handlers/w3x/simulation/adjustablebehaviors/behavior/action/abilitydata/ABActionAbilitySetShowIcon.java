package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.action.abilitydata;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.ability.ABAbilityCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.condition.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABSingleAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;

public class ABActionAbilitySetShowIcon implements ABSingleAction {

	private ABAbilityCallback ability;
	private ABBooleanCallback show;

	@Override
	public void runAction(final CUnit caster, final ABLocalDataStore localStore, final int castId) {
		if (this.ability != null) {
			final CAbility abil = ability.callback(caster, localStore, castId);
			if (abil != null) {
				abil.setIconShowing(show.callback(caster, localStore, castId));
			}
		} else {
			localStore.originAbility.setIconShowing(show.callback(caster, localStore, castId));
		}
	}

	@Override
	public String generateJassEquivalent(final JassTextGenerator jassTextGenerator) {
		return "JASSTODO";
	}
}
