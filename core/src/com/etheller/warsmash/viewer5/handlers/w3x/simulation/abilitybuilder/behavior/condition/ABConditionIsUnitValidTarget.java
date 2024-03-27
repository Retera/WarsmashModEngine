package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.condition;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.booleancallbacks.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.unitcallbacks.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABCondition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.types.impl.CAbilityTypeAbilityBuilderLevelData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.BooleanAbilityTargetCheckReceiver;

public class ABConditionIsUnitValidTarget implements ABCondition {

	private ABUnitCallback caster;
	private ABUnitCallback target;
	private ABBooleanCallback targetedEffect;

	@SuppressWarnings("unchecked")
	@Override
	public boolean evaluate(CSimulation game, CUnit casterUnit, Map<String, Object> localStore, final int castId) {
		CUnit theCaster = casterUnit;

		List<CAbilityTypeAbilityBuilderLevelData> levelData = (List<CAbilityTypeAbilityBuilderLevelData>) localStore
				.get(ABLocalStoreKeys.LEVELDATA);
		EnumSet<CTargetType> targetsAllowed = levelData.get(((int) localStore.get(ABLocalStoreKeys.CURRENTLEVEL))-1)
				.getTargetsAllowed();
		if (targetsAllowed.isEmpty()) {
			return true;
		}
		CUnit theUnit = target.callback(game, casterUnit, localStore, castId);
		boolean te = false;
		if (caster != null) {
			theCaster = caster.callback(game, casterUnit, localStore, castId);
		}
		if (targetedEffect != null) {
			te = targetedEffect.callback(game, theCaster, localStore, castId);
		}
//		for (CTargetType tar : targetsAllowed) {
//			System.err.println("Matches " + tar + "? " + theUnit.canBeTargetedBy(game, theCaster, EnumSet.of(tar)));
//		}

		return theUnit.canBeTargetedBy(game, theCaster, te, targetsAllowed, BooleanAbilityTargetCheckReceiver.<CWidget>getInstance().reset());
	}

}
