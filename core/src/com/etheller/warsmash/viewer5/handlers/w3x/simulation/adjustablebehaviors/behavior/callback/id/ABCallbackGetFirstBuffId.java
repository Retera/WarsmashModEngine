package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.id;

import java.util.List;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.types.impl.ABAbilityBuilderAbilityTypeLevelData;

public class ABCallbackGetFirstBuffId extends ABIDCallback {

	private ABIDCallback defaultId;

	@SuppressWarnings("unchecked")
	@Override
	public War3ID callback(final CUnit caster, final ABLocalDataStore localStore, final int castId) {
		final List<War3ID> buffs = ((List<ABAbilityBuilderAbilityTypeLevelData>) localStore
				.get(ABLocalStoreKeys.LEVELDATA)).get(localStore.getLevelDataInstanceLevel(castId)).getBuffs();
		if ((buffs != null) && !buffs.isEmpty()) {
			return buffs.get(0);
		}
		if (defaultId != null) {
			return defaultId.callback(caster, localStore, castId);
		}
		return null;
	}

	@Override
	public String generateJassEquivalent(final JassTextGenerator jassTextGenerator) {
		return "GetFirstBuffIdAU(" + jassTextGenerator.getTriggerLocalStore() + ")";
	}

}
