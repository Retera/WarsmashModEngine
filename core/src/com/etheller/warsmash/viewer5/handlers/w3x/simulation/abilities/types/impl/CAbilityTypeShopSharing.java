package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl;

import java.util.List;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.CLevelingAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.item.shop.CAbilityNeutralBuilding;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityType;

public class CAbilityTypeShopSharing extends CAbilityType<CAbilityTypeNeutralBuildingLevelData> {

	public CAbilityTypeShopSharing(final War3ID alias, final War3ID code,
			final List<CAbilityTypeNeutralBuildingLevelData> levelData) {
		super(alias, code, levelData);
	}

	@Override
	public CAbility createAbility(final int handleId) {
		final CAbilityTypeNeutralBuildingLevelData levelData = getLevelData(0);
		return new CAbilityNeutralBuilding(handleId, getCode(), getAlias(), levelData.getActivationRadius(),
				levelData.getInteractionType(), levelData.isShowSelectUnitButton(), levelData.isShowUnitIndicator(),
				true);
	}

	@Override
	public void setLevel(final CSimulation game, final CUnit unit, final CLevelingAbility existingAbility, final int level) {
		final CAbilityTypeNeutralBuildingLevelData levelData = getLevelData(level - 1);
		final CAbilityNeutralBuilding heroAbility = ((CAbilityNeutralBuilding) existingAbility);

		heroAbility.setActivationRadius(levelData.getActivationRadius());
		heroAbility.setInteractionType(levelData.getInteractionType());
		heroAbility.setShowSelectUnitButton(levelData.isShowSelectUnitButton());
		heroAbility.setShowUnitIndicator(levelData.isShowUnitIndicator());

		heroAbility.setLevel(game, unit, level);
	}

}
