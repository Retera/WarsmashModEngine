package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.condition;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.ability.AbilityBuilderAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.ability.AbilityBuilderActiveAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.booleancallbacks.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.unitcallbacks.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABCondition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.types.impl.CAbilityTypeAbilityBuilderLevelData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.BooleanAbilityTargetCheckReceiver;

public class ABConditionIsUnitValidTarget extends ABCondition {

	private ABUnitCallback caster;
	private ABUnitCallback target;
	private ABBooleanCallback targetedEffect;

	@SuppressWarnings("unchecked")
	@Override
	public Boolean callback(CSimulation game, CUnit casterUnit, Map<String, Object> localStore, final int castId) {
		CUnit theCaster = casterUnit;

		EnumSet<CTargetType> targetsAllowed = null;
		AbilityBuilderAbility ability = (AbilityBuilderAbility) localStore.get(ABLocalStoreKeys.ABILITY);
		if (ability != null && ability instanceof AbilityBuilderActiveAbility) {
			targetsAllowed = ((AbilityBuilderActiveAbility)ability).getTargetsAllowed();
		} else {
			List<CAbilityTypeAbilityBuilderLevelData> levelData = (List<CAbilityTypeAbilityBuilderLevelData>) localStore
					.get(ABLocalStoreKeys.LEVELDATA);
			targetsAllowed = levelData.get(((int) localStore.get(ABLocalStoreKeys.CURRENTLEVEL))-1)
					.getTargetsAllowed();
		}
		
		if (targetsAllowed.isEmpty()) {
			return true;
		}
		final CUnit theUnit = this.target.callback(game, casterUnit, localStore, castId);
		if (theUnit == null) {
			return false;
		}
		boolean te = false;
		if (this.caster != null) {
			theCaster = this.caster.callback(game, casterUnit, localStore, castId);
		}
		if (this.targetedEffect != null) {
			te = this.targetedEffect.callback(game, theCaster, localStore, castId);
		}

		return theUnit.canBeTargetedBy(game, theCaster, te, targetsAllowed,
				BooleanAbilityTargetCheckReceiver.<CWidget>getInstance().reset());
	}

	@Override
	public String generateJassEquivalent(JassTextGenerator jassTextGenerator) {
		String casterExpr;
		if (this.caster == null) {
			casterExpr = jassTextGenerator.getCaster();
		}
		else {
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
