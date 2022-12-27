package com.etheller.warsmash.viewer5.handlers.w3x.simulation.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.etheller.warsmash.units.manager.MutableObjectData;
import com.etheller.warsmash.units.manager.MutableObjectData.MutableGameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CGameplayConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitTypeRequirement;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUpgradeType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CUpgradeClass;

public class CUpgradeData {
	private static final War3ID APPLIES_TO_ALL_UNITS = War3ID.fromString("glob");
	private static final War3ID CLASS = War3ID.fromString("gcls");
	private static final War3ID GOLD_BASE = War3ID.fromString("gglb");
	private static final War3ID GOLD_INCREMENT = War3ID.fromString("gglm");
	private static final War3ID LEVELS = War3ID.fromString("glvl");
	private static final War3ID LUMBER_BASE = War3ID.fromString("glmb");
	private static final War3ID LUMBER_INCREMENT = War3ID.fromString("glmm");
	private static final War3ID RACE = War3ID.fromString("grac");
	private static final War3ID TIME_BASE = War3ID.fromString("gtib");
	private static final War3ID TIME_INCREMENT = War3ID.fromString("gtim");
	private static final War3ID TRANSFER_WITH_UNIT_OWNERSHIP = War3ID.fromString("ginh");
	private static final War3ID REQUIREMENTS = War3ID.fromString("greq");
	private static final War3ID REQUIREMENTS_LEVELS = War3ID.fromString("grqc");
	private static final War3ID NAME = War3ID.fromString("gnam");

	private final CGameplayConstants gameplayConstants;
	private final MutableObjectData upgradeData;
	private final Map<War3ID, CUpgradeType> idToType = new HashMap<>();

	public CUpgradeData(final CGameplayConstants gameplayConstants, final MutableObjectData upgradeData) {
		this.gameplayConstants = gameplayConstants;
		this.upgradeData = upgradeData;
	}

	public CUpgradeType getType(final War3ID typeId) {
		final MutableGameObject upgradeType = this.upgradeData.get(typeId);
		return getUpgradeTypeInstance(typeId, upgradeType);
	}

	private CUpgradeType getUpgradeTypeInstance(final War3ID typeId, final MutableGameObject upgradeType) {
		CUpgradeType upgradeTypeInstance = this.idToType.get(typeId);
		if (upgradeTypeInstance == null) {
			boolean appliesToAllUnits = upgradeType.getFieldAsBoolean(APPLIES_TO_ALL_UNITS, 0);

			final String classString = upgradeType.getFieldAsString(CLASS, 0);
			final CUpgradeClass upgradeClass = CUpgradeClass.parseUpgradeClass(classString);

			int goldBase = upgradeType.getFieldAsInteger(GOLD_BASE, 0);
			int goldIncrement = upgradeType.getFieldAsInteger(GOLD_INCREMENT, 0);

			int levelCount = upgradeType.getFieldAsInteger(LEVELS, 0);

			int lumberBase = upgradeType.getFieldAsInteger(LUMBER_BASE, 0);
			int lumberIncrement = upgradeType.getFieldAsInteger(LUMBER_INCREMENT, 0);

			final String raceString = upgradeType.getFieldAsString(RACE, 0);
			final CUnitRace unitRace = CUnitRace.parseRace(raceString);

			int timeBase = (int) Math
					.ceil(upgradeType.getFieldAsInteger(TIME_BASE, 0) * WarsmashConstants.GAME_SPEED_TIME_FACTOR);
			int timeIncrement = (int) Math
					.ceil(upgradeType.getFieldAsInteger(TIME_INCREMENT, 0) * WarsmashConstants.GAME_SPEED_TIME_FACTOR);

			boolean transferWithUnitOwnership = upgradeType.getFieldAsBoolean(TRANSFER_WITH_UNIT_OWNERSHIP, 0);

			final List<CUpgradeType.UpgradeLevel> upgradeLevels = new ArrayList<>();
			for (int i = 1; i <= levelCount; i++) {
				final String requirementsTierString = upgradeType.getFieldAsString(REQUIREMENTS, i);
				final String requirementsLevelsString = upgradeType.getFieldAsString(REQUIREMENTS_LEVELS, i);
				final List<CUnitTypeRequirement> tierRequirements = CUnitData.parseRequirements(requirementsTierString,
						requirementsLevelsString);
				final String levelName = upgradeType.getFieldAsString(NAME, i);
				upgradeLevels.add(new CUpgradeType.UpgradeLevel(levelName, tierRequirements));
			}

			upgradeTypeInstance = new CUpgradeType(typeId, appliesToAllUnits, upgradeClass, goldBase, goldIncrement,
					levelCount, lumberBase, lumberIncrement, unitRace, timeBase, timeIncrement, transferWithUnitOwnership,
					upgradeLevels);
			this.idToType.put(typeId, upgradeTypeInstance);
		}
		return upgradeTypeInstance;
	}

}
