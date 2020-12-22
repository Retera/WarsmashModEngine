package com.etheller.warsmash.viewer5.handlers.w3x.simulation;

import java.util.EnumSet;
import java.util.List;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;

public class CAbilityType {
	/* alias: defines which ability editor ability to use */
	private final War3ID alias;
	/* code: defines which CAbility class to use */
	private final War3ID code;

	private final List<CAbilityTypeLevelData> levelData;

	public CAbilityType(final War3ID alias, final War3ID code, final List<CAbilityTypeLevelData> levelData) {
		this.alias = alias;
		this.code = code;
		this.levelData = levelData;
	}

	public War3ID getAlias() {
		return this.alias;
	}

	public War3ID getCode() {
		return this.code;
	}

	public EnumSet<CTargetType> getTargetsAllowed(final int level) {
		return getLevelData(level).getTargetsAllowed();
	}

	private CAbilityTypeLevelData getLevelData(final int level) {
		return this.levelData.get(level);
	}

}
