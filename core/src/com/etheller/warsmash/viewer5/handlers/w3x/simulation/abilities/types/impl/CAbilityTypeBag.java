package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl;

import java.util.List;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.CLevelingAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.inventory.CAbilityBag;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityType;

public class CAbilityTypeBag extends CAbilityType<CAbilityTypeBagLevelData> {

	public CAbilityTypeBag(final War3ID alias, final War3ID code, final List<CAbilityTypeBagLevelData> levelData) {
		super(alias, code, levelData);
	}

	@Override
	public CAbility createAbility(final int handleId) {
		final CAbilityTypeBagLevelData levelData = getLevelData(0);
		return new CAbilityBag(handleId, getCode(), getAlias(), levelData.getSlotCount());
	}

	@Override
	public void setLevel(final CSimulation game, final CUnit unit, final CLevelingAbility existingAbility,
			final int level) {
		existingAbility.setLevel(game, unit, level);
	}

}
