package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl;

import java.util.List;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.CLevelingAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.human.mountainking.CAbilityThunderBolt;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityType;

public class CAbilityTypeThunderBolt extends CAbilityType<CAbilityTypeThunderBoltLevelData> {

	private float projectileSpeed;
	private boolean projectileHomingEnabled;

	public CAbilityTypeThunderBolt(final War3ID alias, final War3ID code,
			final List<CAbilityTypeThunderBoltLevelData> levelData, float projectileSpeed,
			boolean projectileHomingEnabled) {
		super(alias, code, levelData);
		this.projectileSpeed = projectileSpeed;
		this.projectileHomingEnabled = projectileHomingEnabled;
	}

	@Override
	public CAbility createAbility(final int handleId) {
		final CAbilityTypeThunderBoltLevelData levelData = getLevelData(0);
		return new CAbilityThunderBolt(handleId, getAlias(), levelData.getManaCost(), levelData.getDamage(),
				levelData.getCastRange(), levelData.getCooldown(), levelData.getDuration(), levelData.getHeroDuration(),
				projectileSpeed, projectileHomingEnabled, levelData.getBuffId(), levelData.getTargetsAllowed());
	}

	@Override
	public void setLevel(final CSimulation game, final CLevelingAbility existingAbility, final int level) {

		final CAbilityTypeThunderBoltLevelData levelData = getLevelData(level - 1);
		final CAbilityThunderBolt heroAbility = (CAbilityThunderBolt) existingAbility;

		heroAbility.setManaCost(levelData.getManaCost());
		heroAbility.setDamage(levelData.getDamage());
		heroAbility.setCastRange(levelData.getCastRange());
		heroAbility.setCooldown(levelData.getCooldown());
		heroAbility.setDuration(levelData.getDuration());
		heroAbility.setHeroDuration(levelData.getHeroDuration());
		heroAbility.setBuffId(levelData.getBuffId());
		heroAbility.setTargetsAllowed(levelData.getTargetsAllowed());

		heroAbility.setLevel(level);

	}

}
