package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.condition;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.unitcallbacks.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.widget.ABWidgetCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABCondition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.types.impl.CAbilityTypeAbilityBuilderLevelData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;

public class ABConditionIsValidTarget implements ABCondition {

	private ABUnitCallback caster;
	private ABWidgetCallback target;

	@SuppressWarnings("unchecked")
	@Override
	public boolean evaluate(CSimulation game, CUnit casterUnit, Map<String, Object> localStore, final int castId) {
		CUnit theCaster = casterUnit;

		final List<CAbilityTypeAbilityBuilderLevelData> levelData = (List<CAbilityTypeAbilityBuilderLevelData>) localStore
				.get(ABLocalStoreKeys.LEVELDATA);
		final EnumSet<CTargetType> targetsAllowed = levelData
				.get(((int) localStore.get(ABLocalStoreKeys.CURRENTLEVEL)) - 1).getTargetsAllowed();

		if (targetsAllowed.isEmpty()) {
			return true;
		}
		if (this.caster != null) {
			theCaster = this.caster.callback(game, casterUnit, localStore, castId);
		}

//		CWidget theTarget = target.callback(game, theCaster, localStore, castId);
//		for (CTargetType tar : targetsAllowed) {
//			System.err.println("Matches " + tar + "? " + theTarget.canBeTargetedBy(game, theCaster, EnumSet.of(tar)));
//		}

		return this.target.callback(game, casterUnit, localStore, castId).canBeTargetedBy(game, theCaster,
				targetsAllowed);
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
		return "IsValidTargetAU(" + this.target.generateJassEquivalent(jassTextGenerator) + ", " + casterExpr + ", "
				+ jassTextGenerator.getTriggerLocalStore() + ")";
	}

}
