package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.floatcallbacks;

import java.util.List;
import java.util.Map;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
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
	public Float callback(final CSimulation game, final CUnit caster, final Map<String, Object> localStore,
			final int castId) {
		final List<CAbilityTypeAbilityBuilderLevelData> levelData = (List<CAbilityTypeAbilityBuilderLevelData>) localStore
				.get(ABLocalStoreKeys.LEVELDATA);
		if (this.target != null) {
			final CUnit tar = this.target.callback(game, caster, localStore, castId);
			if (tar.isHero() || tar.isUnitType(CUnitTypeJass.RESISTANT)) {
				return levelData.get(((int) localStore.get(ABLocalStoreKeys.CURRENTLEVEL)) - 1).getDurationHero();
			}
		}
		return levelData.get(((int) localStore.get(ABLocalStoreKeys.CURRENTLEVEL)) - 1).getDurationNormal();
	}

	@Override
	public String generateJassEquivalent(final JassTextGenerator jassTextGenerator) {
		if (this.target != null) {
			return "GetAbilityDurationForTargetAU(" + jassTextGenerator.getTriggerLocalStore() + ", "
					+ this.target.generateJassEquivalent(jassTextGenerator) + ")";
		}
		return "GetAbilityDurationAU(" + jassTextGenerator.getTriggerLocalStore() + ")";

	}

}
