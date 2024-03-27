package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl;

import java.util.List;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.CLevelingAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.human.bloodmage.phoenix.CAbilityPhoenixFire;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityType;

public class CAbilityTypePhoenixFire extends CAbilityType<CAbilityTypePhoenixFireLevelData> {

	public CAbilityTypePhoenixFire(final War3ID alias, final War3ID code,
			final List<CAbilityTypePhoenixFireLevelData> levelData) {
		super(alias, code, levelData);
	}

	@Override
	public CAbility createAbility(final int handleId) {
		final CAbilityTypePhoenixFireLevelData levelData = getLevelData(0);
		return new CAbilityPhoenixFire(handleId, getCode(), getAlias(), levelData.getInitialDamage(),
				levelData.getDamagePerSecond(), levelData.getAreaOfEffect(), levelData.getCooldown(),
				levelData.getDuration(), levelData.getTargetsAllowed());
	}

	@Override
	public void setLevel(final CSimulation game, final CUnit unit, final CLevelingAbility existingAbility, final int level) {
		final CAbilityTypePhoenixFireLevelData levelData = getLevelData(level - 1);
		final CAbilityPhoenixFire heroAbility = ((CAbilityPhoenixFire) existingAbility);

		heroAbility.setInitialDamage(levelData.getInitialDamage());
		heroAbility.setDamagePerSecond(levelData.getDamagePerSecond());
		heroAbility.setAreaOfEffect(levelData.getAreaOfEffect());
		heroAbility.setCooldown(levelData.getCooldown());
		heroAbility.setDuration(levelData.getDuration());
		heroAbility.setTargetsAllowed(levelData.getTargetsAllowed());

		heroAbility.setLevel(game, unit, level);
	}
}
