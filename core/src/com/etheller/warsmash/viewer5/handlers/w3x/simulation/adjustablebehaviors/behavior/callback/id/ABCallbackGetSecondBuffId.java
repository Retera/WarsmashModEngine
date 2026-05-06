package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.id;

import java.util.List;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.types.impl.ABAbilityBuilderAbilityTypeLevelData;

public class ABCallbackGetSecondBuffId extends ABIDCallback {

	@SuppressWarnings("unchecked")
	@Override
	public War3ID callback(CUnit caster, ABLocalDataStore localStore, final int castId) {
		List<War3ID> buffs = ((List<ABAbilityBuilderAbilityTypeLevelData>) localStore.get(ABLocalStoreKeys.LEVELDATA))
				.get(localStore.getLevelDataInstanceLevel(castId)).getBuffs();

		if (buffs != null && buffs.size() > 1) {
			return buffs.get(1);
		}
		return null;
	}

}
