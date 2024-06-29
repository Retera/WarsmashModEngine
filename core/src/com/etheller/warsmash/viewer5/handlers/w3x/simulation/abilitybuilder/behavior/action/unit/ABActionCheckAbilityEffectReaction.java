package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.unit;

import java.util.List;
import java.util.Map;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.parsers.jass.JassTextGeneratorType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.unitcallbacks.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;

public class ABActionCheckAbilityEffectReaction implements ABAction {

	private ABUnitCallback target;
	private List<ABAction> onHitActions;
	private List<ABAction> onBlockActions;

	@Override
	public void runAction(final CSimulation game, final CUnit caster, final Map<String, Object> localStore,
			final int castId) {
		CUnit tarU = caster;
		if (this.target != null) {
			tarU = this.target.callback(game, caster, localStore, castId);
		}
		if (tarU.checkForAbilityEffectReaction(game, caster, (CAbility) localStore.get(ABLocalStoreKeys.ABILITY))) {
			if (this.onHitActions != null) {
				for (final ABAction periodicAction : this.onHitActions) {
					periodicAction.runAction(game, caster, localStore, castId);
				}
			}
		}
		else {
			if (this.onBlockActions != null) {
				for (final ABAction periodicAction : this.onBlockActions) {
					periodicAction.runAction(game, caster, localStore, castId);
				}
			}
		}
	}

	@Override
	public String generateJassEquivalent(final JassTextGenerator jassTextGenerator) {
		final String onBlockFunc = jassTextGenerator.createAnonymousFunction(this.onBlockActions,
				"CheckAbilityEffectReactionAU_OnBlock");
		final String onHitFunc = jassTextGenerator.createAnonymousFunction(this.onHitActions,
				"CheckAbilityEffectReactionAU_OnHit");
		String tarU;
		if (this.target != null) {
			tarU = jassTextGenerator.getCaster();
		}
		else {
			tarU = jassTextGenerator.getCaster();
		}
		return "CheckAbilityEffectReactionAU(" + tarU + ", "
				+ jassTextGenerator.getUserDataExpr("AB_LOCAL_STORE_KEYS_ABILITY", JassTextGeneratorType.Ability) + ", "
				+ jassTextGenerator.functionPointerByName(onHitFunc) + ", "
				+ jassTextGenerator.functionPointerByName(onBlockFunc) + ")";
	}
}
