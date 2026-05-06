package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.floats;

import java.util.List;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.unit.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.types.impl.ABAbilityBuilderAbilityTypeLevelData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit.CUnitTypeJass;

public class ABCallbackGetAbilityDuration extends ABFloatCallback {

	private ABUnitCallback target;

	@SuppressWarnings("unchecked")
	@Override
	public Float callback(final CUnit caster, final ABLocalDataStore localStore, final int castId) {
		final List<ABAbilityBuilderAbilityTypeLevelData> levelData = (List<ABAbilityBuilderAbilityTypeLevelData>) localStore
				.get(ABLocalStoreKeys.LEVELDATA);
		if (this.target != null) {
			final CUnit tar = this.target.callback(caster, localStore, castId);
			if (tar.isHero() || tar.isUnitType(CUnitTypeJass.RESISTANT)) {
				return levelData.get(localStore.getLevelDataInstanceLevel(castId)).getDurationHero();
			}
		}
		return levelData.get(localStore.getLevelDataInstanceLevel(castId)).getDurationNormal();
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
