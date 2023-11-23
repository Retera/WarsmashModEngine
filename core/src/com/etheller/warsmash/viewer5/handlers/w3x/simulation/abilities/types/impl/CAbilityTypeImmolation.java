package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl;

import java.util.List;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.CLevelingAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.nightelf.demonhunter.CAbilityImmolation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityType;

public class CAbilityTypeImmolation extends CAbilityType<CAbilityTypeImmolationLevelData> {

	public CAbilityTypeImmolation(final War3ID alias, final War3ID code,
			final List<CAbilityTypeImmolationLevelData> levelData) {
		super(alias, code, levelData);
	}

	@Override
	public CAbility createAbility(final int handleId) {
		final CAbilityTypeImmolationLevelData levelData = getLevelData(0);
		return new CAbilityImmolation(handleId, getCode(), getAlias(), levelData.getBufferManaRequired(),
				levelData.getDamagePerInterval(), levelData.getManaDrainedPerSecond(), levelData.getAreaOfEffect(),
				levelData.getManaCost(), levelData.getDuration(), levelData.getTargetsAllowed(), levelData.getBuffId());
	}

	@Override
	public void setLevel(final CSimulation game, final CUnit unit, final CLevelingAbility existingAbility, final int level) {

		final CAbilityTypeImmolationLevelData levelData = getLevelData(level - 1);
		final CAbilityImmolation heroAbility = ((CAbilityImmolation) existingAbility);

		heroAbility.setBufferManaRequired(levelData.getBufferManaRequired());
		heroAbility.setDamagePerInterval(levelData.getDamagePerInterval());
		heroAbility.setManaDrainedPerSecond(levelData.getManaDrainedPerSecond());
		heroAbility.setAreaOfEffect(levelData.getAreaOfEffect());
		heroAbility.setManaCost(levelData.getManaCost());
		heroAbility.setDuration(levelData.getDuration());
		heroAbility.setTargetsAllowed(levelData.getTargetsAllowed());
		heroAbility.setBuffId(levelData.getBuffId());

		heroAbility.setLevel(game, unit, level);

	}

}
