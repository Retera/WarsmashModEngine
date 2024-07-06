package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.ability;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.parsers.jass.JassTextGeneratorType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABSingleAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.types.impl.CAbilityTypeAbilityBuilderLevelData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;

public class ABActionRemoveTargetAllowed implements ABSingleAction {

	private CTargetType targetType;

	@Override
	@SuppressWarnings("unchecked")
	public void runAction(final CSimulation game, final CUnit caster, final Map<String, Object> localStore,
			final int castId) {
		final List<CAbilityTypeAbilityBuilderLevelData> levelData = (List<CAbilityTypeAbilityBuilderLevelData>) localStore
				.get(ABLocalStoreKeys.LEVELDATA);
		final EnumSet<CTargetType> targetsAllowed = levelData
				.get(((int) localStore.get(ABLocalStoreKeys.CURRENTLEVEL)) - 1).getTargetsAllowed();
		targetsAllowed.remove(this.targetType);
	}

	@Override
	public String generateJassEquivalent(final JassTextGenerator jassTextGenerator) {
		return "AbilityTypeLevelDataRemoveTargetAllowed("
				+ jassTextGenerator.getUserDataExpr("AB_LOCAL_STORE_KEY_LEVELDATA",
						JassTextGeneratorType.AbilityTypeLevelDataHandle)
				+ ", "
				+ jassTextGenerator.getUserDataExpr("AB_LOCAL_STORE_KEY_CURRENTLEVEL", JassTextGeneratorType.Integer)
				+ " - 1, TARGET_TYPE_" + this.targetType.name() + ")";
	}
}
