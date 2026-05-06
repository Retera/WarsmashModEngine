package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.id;

import java.util.List;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.types.impl.ABAbilityBuilderAbilityTypeLevelData;

public class ABCallbackGetFirstEffectId extends ABIDCallback {

	@SuppressWarnings("unchecked")
	@Override
	public War3ID callback(CUnit caster, ABLocalDataStore localStore, final int castId) {
		List<War3ID> effects = ((List<ABAbilityBuilderAbilityTypeLevelData>) localStore.get(ABLocalStoreKeys.LEVELDATA))
				.get(localStore.getLevelDataInstanceLevel(castId)).getEffects();
		if (effects != null && !effects.isEmpty()) {
			return effects.get(0);
		}
		return null;
	}

}
