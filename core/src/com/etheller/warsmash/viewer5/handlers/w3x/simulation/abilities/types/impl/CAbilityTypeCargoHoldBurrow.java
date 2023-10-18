package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl;

import java.util.List;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.cargohold.CAbilityCargoHold;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.cargohold.CAbilityCargoHoldBurrow;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.CLevelingAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityType;

public class CAbilityTypeCargoHoldBurrow extends CAbilityType<CAbilityTypeCargoHoldBurrowLevelData> {

	public CAbilityTypeCargoHoldBurrow(final War3ID alias, final War3ID code,
			final List<CAbilityTypeCargoHoldBurrowLevelData> levelData) {
		super(alias, code, levelData);
	}

	@Override
	public CAbility createAbility(final int handleId) {
		final CAbilityTypeCargoHoldBurrowLevelData levelData = getLevelData(0);
		return new CAbilityCargoHoldBurrow(handleId, getCode(), getAlias(), levelData.getCargoCapcity(), levelData.getDuration(),
				levelData.getCastRange(), levelData.getTargetsAllowed());
	}

	@Override
	public void setLevel(final CSimulation game, final CUnit unit, final CLevelingAbility existingAbility, final int level) {
		final CAbilityTypeCargoHoldBurrowLevelData levelData = getLevelData(level - 1);
		final CAbilityCargoHold heroAbility = ((CAbilityCargoHold) existingAbility);

		heroAbility.setDuration(levelData.getDuration());
		heroAbility.setCargoCapacity(levelData.getCargoCapcity());
		heroAbility.setTargetsAllowed(levelData.getTargetsAllowed());

		heroAbility.setLevel(game, unit, level);
	}

}
