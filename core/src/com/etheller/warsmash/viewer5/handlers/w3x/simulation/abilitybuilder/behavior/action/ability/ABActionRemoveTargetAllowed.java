package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.ability;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.types.impl.CAbilityTypeAbilityBuilderLevelData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;

public class ABActionRemoveTargetAllowed implements ABAction {

	private CTargetType targetType;
	
	@SuppressWarnings("unchecked")
	public void runAction(final CSimulation game, final CUnit caster, final Map<String, Object> localStore, final int castId) {
		List<CAbilityTypeAbilityBuilderLevelData> levelData = (List<CAbilityTypeAbilityBuilderLevelData>) localStore
				.get(ABLocalStoreKeys.LEVELDATA);
		EnumSet<CTargetType> targetsAllowed = levelData.get(((int) localStore.get(ABLocalStoreKeys.CURRENTLEVEL))-1)
				.getTargetsAllowed();
		targetsAllowed.remove(targetType);
	}
}
