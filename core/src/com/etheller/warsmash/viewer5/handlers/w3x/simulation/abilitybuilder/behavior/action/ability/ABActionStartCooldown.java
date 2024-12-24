package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.ability;

import java.util.Map;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.parsers.jass.JassTextGeneratorType;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.ability.AbilityBuilderAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.ability.GetABAbilityByRawcodeVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.floatcallbacks.ABFloatCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.idcallbacks.ABIDCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.unitcallbacks.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABSingleAction;

public class ABActionStartCooldown implements ABSingleAction {

	private ABIDCallback alias;
	private ABUnitCallback unit;
	private ABFloatCallback cooldown;

	@Override
	public void runAction(final CSimulation game, final CUnit caster, final Map<String, Object> localStore,
			final int castId) {
		CUnit theUnit = caster;
		if (this.unit != null) {
			theUnit = this.unit.callback(game, caster, localStore, castId);
		}
		if (this.alias != null) {
			final War3ID aliasId = this.alias.callback(game, caster, localStore, castId);
			if (this.cooldown != null) {
				theUnit.beginCooldown(game, aliasId, this.cooldown.callback(game, caster, localStore, castId));
			}
			else {
				final AbilityBuilderAbility abil = theUnit
						.getAbility(GetABAbilityByRawcodeVisitor.getInstance().reset(aliasId));
				if (abil != null) {
					abil.startCooldown(game, theUnit);
				}
			}
		}
		else {
			if (this.cooldown != null) {
				final War3ID aliasId = (War3ID) localStore.get(ABLocalStoreKeys.ALIAS);
				theUnit.beginCooldown(game, aliasId, this.cooldown.callback(game, caster, localStore, castId));
			}
			else {
				final AbilityBuilderAbility abil = (AbilityBuilderAbility) localStore.get(ABLocalStoreKeys.ABILITY);
				abil.startCooldown(game, theUnit);
			}
		}
	}

	@Override
	public String generateJassEquivalent(final JassTextGenerator jassTextGenerator) {
		String unitExpression;
		if (this.unit != null) {
			unitExpression = this.unit.generateJassEquivalent(jassTextGenerator);
		}
		else {
			unitExpression = jassTextGenerator.getCaster();
		}
		if (this.alias != null) {
			final String aliasExpression = this.alias.generateJassEquivalent(jassTextGenerator);
			if (this.cooldown != null) {
				return "StartUnitAbilityCooldown(" + unitExpression + ", " + aliasExpression + ", "
						+ this.cooldown.generateJassEquivalent(jassTextGenerator) + ")";
			}
			else {
				return "StartUnitAbilityDefaultCooldown(" + unitExpression + ", " + aliasExpression + ")";
			}
		}
		else {
			if (this.cooldown != null) {
				return "StartUnitAbilityCooldown(" + unitExpression + ", "
						+ jassTextGenerator.getUserDataExpr("AB_LOCAL_STORE_KEY_ALIAS", JassTextGeneratorType.Integer)
						+ ", " + this.cooldown.generateJassEquivalent(jassTextGenerator) + ")";
			}
			else {
				return "StartAbilityDefaultCooldown(" + unitExpression + ", "
						+ jassTextGenerator.getUserDataExpr("AB_LOCAL_STORE_KEY_ABILITY", JassTextGeneratorType.AbilityHandle)
						+ ")";
			}
		}
	}
}
