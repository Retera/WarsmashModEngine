package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.abilities;

import java.util.Map;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.ability.AbilityBuilderAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.abilitycallbacks.ABAbilityCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.booleancallbacks.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABSingleAction;

public class ABActionAbilitySetShowIcon implements ABSingleAction {

	private ABAbilityCallback ability;
	private ABBooleanCallback show;

	@Override
	public void runAction(final CSimulation game, final CUnit caster, final Map<String, Object> localStore,
			final int castId) {
		if (this.ability != null) {
			final CAbility abil = ability.callback(game, caster, localStore, castId);
			if (abil != null) {
				abil.setIconShowing(show.callback(game, caster, localStore, castId));
			}
		}
		else {
			final AbilityBuilderAbility abil = (AbilityBuilderAbility) localStore.get(ABLocalStoreKeys.ABILITY);
			abil.setIconShowing(show.callback(game, caster, localStore, castId));
		}
	}

	@Override
	public String generateJassEquivalent(final JassTextGenerator jassTextGenerator) {
		return "JASSTODO";
	}
}
