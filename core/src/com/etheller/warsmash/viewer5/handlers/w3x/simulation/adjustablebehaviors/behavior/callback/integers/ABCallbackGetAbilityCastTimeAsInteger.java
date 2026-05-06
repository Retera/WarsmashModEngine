package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.integers;

import java.util.List;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.ability.ABAbilityBuilderAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.types.impl.ABAbilityBuilderAbilityTypeLevelData;

public class ABCallbackGetAbilityCastTimeAsInteger extends ABIntegerCallback {

	@SuppressWarnings("unchecked")
	@Override
	public Integer callback(CUnit caster, ABLocalDataStore localStore, final int castId) {
		ABAbilityBuilderAbility ability = localStore.originAbility;
		if (ability != null) {
			return (int) ability.getCastTime();
		} else {
			List<ABAbilityBuilderAbilityTypeLevelData> levelData = (List<ABAbilityBuilderAbilityTypeLevelData>) localStore
					.get(ABLocalStoreKeys.LEVELDATA);
			return (int) levelData.get(localStore.getLevelDataInstanceLevel(castId)).getCastTime();
		}
	}

}
