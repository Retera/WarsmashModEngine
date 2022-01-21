package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types;

import java.util.EnumSet;
import java.util.List;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.CLevelingAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;

public abstract class CAbilityType<TYPE_LEVEL_DATA extends CAbilityTypeLevelData> {
	/* alias: defines which ability editor ability to use */
	private final War3ID alias;
	/* code: defines which CAbility class to use */
	private final War3ID code;

	private final List<TYPE_LEVEL_DATA> levelData;

	public CAbilityType(final War3ID alias, final War3ID code, final List<TYPE_LEVEL_DATA> levelData) {
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

	protected final TYPE_LEVEL_DATA getLevelData(final int level) {
		return this.levelData.get(level);
	}

	public abstract CAbility createAbility(int handleId);

	public abstract void setLevel(CSimulation game, CLevelingAbility existingAbility, int level);

}
