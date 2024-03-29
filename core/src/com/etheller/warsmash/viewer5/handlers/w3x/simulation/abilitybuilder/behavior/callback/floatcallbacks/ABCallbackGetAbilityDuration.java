package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.floatcallbacks;

import java.util.List;
import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.unitcallbacks.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.types.impl.CAbilityTypeAbilityBuilderLevelData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit.CUnitTypeJass;

public class ABCallbackGetAbilityDuration extends ABFloatCallback {

	private ABUnitCallback target;
	
	@SuppressWarnings("unchecked")
	@Override
	public Float callback(CSimulation game, CUnit caster, Map<String, Object> localStore, final int castId) {
		List<CAbilityTypeAbilityBuilderLevelData>  levelData = (List<CAbilityTypeAbilityBuilderLevelData>) localStore.get(ABLocalStoreKeys.LEVELDATA);
		if (target != null) {
			CUnit tar = target.callback(game, caster, localStore, castId);
			if (tar.isHero() || tar.isUnitType(CUnitTypeJass.RESISTANT)) {
				return levelData.get(((int) localStore.get(ABLocalStoreKeys.CURRENTLEVEL))-1).getDurationHero();
			}
		}
		return levelData.get(((int) localStore.get(ABLocalStoreKeys.CURRENTLEVEL))-1).getDurationNormal();
	}

}
