package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl;

import java.util.List;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.CLevelingAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.item.CAbilityItemManaRegain;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityTypeLevelData;

public class CAbilityTypeItemManaRegain extends CAbilityType<CAbilityTypeItemManaRegainLevelData> {

	public CAbilityTypeItemManaRegain(final War3ID alias, final War3ID code,
			final List<CAbilityTypeItemManaRegainLevelData> levelData) {
		super(alias, code, levelData);
	}

	@Override
	public CAbility createAbility(final int handleId) {
		final CAbilityTypeItemManaRegainLevelData levelData = getLevelData(0);
		return new CAbilityItemManaRegain(handleId, getCode(), getAlias(), levelData.getManaToRegain(), levelData.getCooldown());
	}

	@Override
	public void setLevel(final CSimulation game, CUnit unit, final CLevelingAbility existingAbility, final int level) {

		final CAbilityTypeLevelData levelData = getLevelData(level - 1);
		final CLevelingAbility heroAbility = (existingAbility);

		// TODO ignores fields

		heroAbility.setLevel(game, unit, level);

	}
}
