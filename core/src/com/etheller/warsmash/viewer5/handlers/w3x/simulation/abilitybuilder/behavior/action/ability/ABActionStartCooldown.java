package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.ability;

import java.util.List;
import java.util.Map;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.types.impl.CAbilityTypeAbilityBuilderLevelData;

public class ABActionStartCooldown implements ABAction {

	@SuppressWarnings("unchecked")
	public void runAction(final CSimulation game, final CUnit caster, final Map<String, Object> localStore, final int castId) {
		War3ID alias = (War3ID) localStore.get(ABLocalStoreKeys.ALIAS);
		List<CAbilityTypeAbilityBuilderLevelData>  levelData = (List<CAbilityTypeAbilityBuilderLevelData>) localStore.get(ABLocalStoreKeys.LEVELDATA);
		
		caster.beginCooldown(game, alias, levelData.get(((int) localStore.get(ABLocalStoreKeys.CURRENTLEVEL))-1).getCooldown());
	}
}
