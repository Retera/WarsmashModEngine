package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.condition.targeting;

import java.util.EnumSet;
import java.util.List;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.ability.ABAbilityBuilderAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.ability.ABAbilityBuilderActiveAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.unit.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.condition.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.types.impl.ABAbilityBuilderAbilityTypeLevelData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.BooleanAbilityTargetCheckReceiver;

public class ABConditionIsUnitValidTarget extends ABBooleanCallback {

	private ABUnitCallback caster;
	private ABUnitCallback target;
	private ABBooleanCallback targetedEffect;

	@SuppressWarnings("unchecked")
	@Override
	public Boolean callback(CUnit casterUnit, ABLocalDataStore localStore, final int castId) {
		CUnit theCaster = casterUnit;

		EnumSet<CTargetType> targetsAllowed = null;
		ABAbilityBuilderAbility ability = localStore.originAbility;
		if (ability != null && ability instanceof ABAbilityBuilderActiveAbility) {
			targetsAllowed = ((ABAbilityBuilderActiveAbility) ability).getTargetsAllowed();
		} else {
			List<ABAbilityBuilderAbilityTypeLevelData> levelData = (List<ABAbilityBuilderAbilityTypeLevelData>) localStore
					.get(ABLocalStoreKeys.LEVELDATA);
			targetsAllowed = levelData.get(localStore.getLevelDataInstanceLevel(castId)).getTargetsAllowed();
		}

		if (targetsAllowed.isEmpty()) {
			return true;
		}
		final CUnit theUnit = this.target.callback(casterUnit, localStore, castId);
		if (theUnit == null) {
			return false;
		}
		boolean te = false;
		if (this.caster != null) {
			theCaster = this.caster.callback(casterUnit, localStore, castId);
		}
		if (this.targetedEffect != null) {
			te = this.targetedEffect.callback(theCaster, localStore, castId);
		}

		return theUnit.canBeTargetedBy(localStore.game, theCaster, te, targetsAllowed,
				BooleanAbilityTargetCheckReceiver.<CWidget>getInstance().reset());
	}

	@Override
	public String generateJassEquivalent(JassTextGenerator jassTextGenerator) {
		String casterExpr;
		if (this.caster == null) {
			casterExpr = jassTextGenerator.getCaster();
		} else {
			casterExpr = this.caster.generateJassEquivalent(jassTextGenerator);
		}
		String targetedEffectExpr = "false";
		if (this.targetedEffect != null) {
			targetedEffectExpr = this.targetedEffect.generateJassEquivalent(jassTextGenerator);
		}
		return "IsUnitValidTargetAU(" + this.target.generateJassEquivalent(jassTextGenerator) + ", " + casterExpr + ", "
				+ targetedEffectExpr + ", " + jassTextGenerator.getTriggerLocalStore() + ")";
	}

}
