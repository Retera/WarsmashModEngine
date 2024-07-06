package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.idcallbacks;

import java.util.List;
import java.util.Map;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.types.impl.CAbilityTypeAbilityBuilderLevelData;

public class ABCallbackGetFirstBuffId extends ABIDCallback {

	@SuppressWarnings("unchecked")
	@Override
	public War3ID callback(final CSimulation game, final CUnit caster, final Map<String, Object> localStore,
			final int castId) {
		final List<War3ID> buffs = ((List<CAbilityTypeAbilityBuilderLevelData>) localStore
				.get(ABLocalStoreKeys.LEVELDATA)).get(((int) localStore.get(ABLocalStoreKeys.CURRENTLEVEL)) - 1)
				.getBuffs();
		if ((buffs != null) && !buffs.isEmpty()) {
			return buffs.get(0);
		}
		return null;
	}

	@Override
	public String generateJassEquivalent(final JassTextGenerator jassTextGenerator) {
		return "GetFirstBuffIdAU(" + jassTextGenerator.getTriggerLocalStore() + ")";
	}

}
