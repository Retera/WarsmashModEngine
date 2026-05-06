package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.action.ability;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.parsers.jass.JassTextGeneratorType;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.ability.ABAbilityBuilderAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.floats.ABFloatCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.id.ABIDCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.unit.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABSingleAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.visitor.ABGetABAbilityByRawcodeVisitor;

public class ABActionStartCooldown implements ABSingleAction {

	private ABIDCallback alias;
	private ABUnitCallback unit;
	private ABFloatCallback cooldown;

	@Override
	public void runAction(final CUnit caster, final ABLocalDataStore localStore, final int castId) {
		CUnit theUnit = caster;
		if (this.unit != null) {
			theUnit = this.unit.callback(caster, localStore, castId);
		}
		if (this.alias != null) {
			final War3ID aliasId = this.alias.callback(caster, localStore, castId);
			if (this.cooldown != null) {
				theUnit.beginCooldown(localStore.game, aliasId, this.cooldown.callback(caster, localStore, castId));
			} else {
				final ABAbilityBuilderAbility abil = theUnit
						.getAbility(ABGetABAbilityByRawcodeVisitor.getInstance().reset(aliasId));
				if (abil != null) {
					abil.startCooldown(localStore.game, theUnit);
				}
			}
		} else {
			if (this.cooldown != null) {
				final War3ID aliasId = localStore.get(ABLocalStoreKeys.ALIAS, War3ID.class);
				theUnit.beginCooldown(localStore.game, aliasId, this.cooldown.callback(caster, localStore, castId));
			} else {
				final ABAbilityBuilderAbility abil = localStore.originAbility;
				abil.startCooldown(localStore.game, theUnit);
			}
		}
	}

	@Override
	public String generateJassEquivalent(final JassTextGenerator jassTextGenerator) {
		String unitExpression;
		if (this.unit != null) {
			unitExpression = this.unit.generateJassEquivalent(jassTextGenerator);
		} else {
			unitExpression = jassTextGenerator.getCaster();
		}
		if (this.alias != null) {
			final String aliasExpression = this.alias.generateJassEquivalent(jassTextGenerator);
			if (this.cooldown != null) {
				return "StartUnitAbilityCooldown(" + unitExpression + ", " + aliasExpression + ", "
						+ this.cooldown.generateJassEquivalent(jassTextGenerator) + ")";
			} else {
				return "StartUnitAbilityDefaultCooldown(" + unitExpression + ", " + aliasExpression + ")";
			}
		} else {
			if (this.cooldown != null) {
				return "StartUnitAbilityCooldown(" + unitExpression + ", "
						+ jassTextGenerator.getUserDataExpr("AB_LOCAL_STORE_KEY_ALIAS", JassTextGeneratorType.Integer)
						+ ", " + this.cooldown.generateJassEquivalent(jassTextGenerator) + ")";
			} else {
				return "StartAbilityDefaultCooldown(" + unitExpression + ", " + jassTextGenerator
						.getUserDataExpr("AB_LOCAL_STORE_KEY_ABILITY", JassTextGeneratorType.AbilityHandle) + ")";
			}
		}
	}
}
