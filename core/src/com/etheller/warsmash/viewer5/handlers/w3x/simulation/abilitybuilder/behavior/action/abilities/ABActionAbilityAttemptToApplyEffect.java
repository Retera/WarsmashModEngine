package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.abilities;

import java.util.Map;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.ability.AbilityBuilderActiveAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.abilitycallbacks.ABAbilityCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.unitcallbacks.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABSingleAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.BooleanAbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.BooleanAbilityTargetCheckReceiver;

public class ABActionAbilityAttemptToApplyEffect implements ABSingleAction {

	private ABAbilityCallback ability;
	private ABUnitCallback source;
	private ABUnitCallback target;

	@Override
	public void runAction(final CSimulation game, final CUnit caster, final Map<String, Object> localStore,
			final int castId) {
		CUnit theCaster = caster;
		if (source != null) {
			theCaster = source.callback(game, theCaster, localStore, castId);
		}
		CUnit theTarget = target.callback(game, theCaster, localStore, castId);
		final CAbility theAbility = this.ability.callback(game, caster, localStore, castId);
		if (theAbility instanceof AbilityBuilderActiveAbility) {
			AbilityBuilderActiveAbility active = ((AbilityBuilderActiveAbility)theAbility);
			int orderId = active.getBaseOrderId();
			final BooleanAbilityActivationReceiver activationReceiver = BooleanAbilityActivationReceiver.INSTANCE;
			active.checkCanUse(game, theCaster, orderId, false,  activationReceiver);
			if (activationReceiver.isOk()) {
				final BooleanAbilityTargetCheckReceiver<CWidget> booleanTargetReceiver = BooleanAbilityTargetCheckReceiver
						.<CWidget>getInstance().reset();
				active.checkCanTarget(game, theCaster, orderId, false, theTarget, booleanTargetReceiver);
				if (booleanTargetReceiver.isTargetable()) {
					if (theCaster.chargeMana(active.getChargedManaCost())) {
						active.internalBegin(game, theCaster, orderId, false, theTarget);
						active.startCooldown(game, theCaster);
						active.runEndCastingActions(game, theCaster, orderId);
						active.cleanupInputs();
					}
				}
			}
			
		}
	}

	@Override
	public String generateJassEquivalent(final JassTextGenerator jassTextGenerator) {
		return "JASSTODO";
	}
}
