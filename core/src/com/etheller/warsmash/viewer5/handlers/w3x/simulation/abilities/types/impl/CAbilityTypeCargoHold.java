package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl;

import java.util.List;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.cargohold.CAbilityCargoHold;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.CLevelingAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityType;

public class CAbilityTypeCargoHold extends CAbilityType<CAbilityTypeCargoHoldLevelData> {

	public CAbilityTypeCargoHold(final War3ID alias, final War3ID code,
			final List<CAbilityTypeCargoHoldLevelData> levelData) {
		super(alias, code, levelData);
	}

	@Override
	public CAbility createAbility(final int handleId) {
		final CAbilityTypeCargoHoldLevelData levelData = getLevelData(0);
		return new CAbilityCargoHold(handleId, getAlias(), levelData.getCargoCapcity(), levelData.getDuration(),
				levelData.getTargetsAllowed());
	}

	@Override
	public void setLevel(final CSimulation game, final CLevelingAbility existingAbility, final int level) {
		final CAbilityTypeCargoHoldLevelData levelData = getLevelData(level - 1);
		final CAbilityCargoHold heroAbility = ((CAbilityCargoHold) existingAbility);

		heroAbility.setDuration(levelData.getDuration());
		heroAbility.setCargoCapacity(levelData.getCargoCapcity());
		heroAbility.setTargetsAllowed(levelData.getTargetsAllowed());

		heroAbility.setLevel(level);
	}

}
