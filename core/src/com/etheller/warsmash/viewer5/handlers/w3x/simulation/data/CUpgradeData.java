package com.etheller.warsmash.viewer5.handlers.w3x.simulation.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.etheller.warsmash.units.DataTable;
import com.etheller.warsmash.units.manager.MutableObjectData;
import com.etheller.warsmash.units.manager.MutableObjectData.MutableGameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CGameplayConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitTypeRequirement;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUpgradeType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CUpgradeClass;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.upgrade.CUpgradeEffect;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.upgrade.CUpgradeEffectAttackDamage;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.upgrade.CUpgradeEffectAttackDice;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.upgrade.CUpgradeEffectAttackRange;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.upgrade.CUpgradeEffectAttackSpeed;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.upgrade.CUpgradeEffectDefenseUpgradeBonus;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.upgrade.CUpgradeEffectHitPointRegen;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.upgrade.CUpgradeEffectHitPoints;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.upgrade.CUpgradeEffectHitPointsPcnt;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.upgrade.CUpgradeEffectManaPoints;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.upgrade.CUpgradeEffectManaPointsPcnt;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.upgrade.CUpgradeEffectManaRegen;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.upgrade.CUpgradeEffectMovementSpeed;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.upgrade.CUpgradeEffectMovementSpeedPcnt;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.upgrade.CUpgradeEffectSpellLevel;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.upgrade.CUpgradeEffectTechMaxAllowed;

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

	private static final War3ID[] EFFECT = { War3ID.fromString("gef1"), War3ID.fromString("gef2"),
			War3ID.fromString("gef3"), War3ID.fromString("gef4"), };
	private static final War3ID[] EFFECT_BASE = { War3ID.fromString("gba1"), War3ID.fromString("gba2"),
			War3ID.fromString("gba3"), War3ID.fromString("gba4"), };
	private static final War3ID[] EFFECT_MOD = { War3ID.fromString("gmo1"), War3ID.fromString("gmo2"),
			War3ID.fromString("gmo3"), War3ID.fromString("gmo4"), };
	private static final War3ID[] EFFECT_CODE = { War3ID.fromString("gco1"), War3ID.fromString("gco2"),
			War3ID.fromString("gco3"), War3ID.fromString("gco4"), };

	private final CGameplayConstants gameplayConstants;
	private final MutableObjectData upgradeData;
	private final DataTable standardUpgradeEffectMeta;
	private final Map<War3ID, CUpgradeType> idToType = new HashMap<>();

	public CUpgradeData(final CGameplayConstants gameplayConstants, final MutableObjectData upgradeData,
			final DataTable standardUpgradeEffectMeta) {
		this.gameplayConstants = gameplayConstants;
		this.upgradeData = upgradeData;
		this.standardUpgradeEffectMeta = standardUpgradeEffectMeta;
	}

	public CUpgradeType getType(final War3ID typeId) {
		final MutableGameObject upgradeType = this.upgradeData.get(typeId);
		if (upgradeType == null) {
			return null;
		}
		return getUpgradeTypeInstance(typeId, upgradeType);
	}

	private CUpgradeType getUpgradeTypeInstance(final War3ID typeId, final MutableGameObject upgradeType) {
		CUpgradeType upgradeTypeInstance = this.idToType.get(typeId);
		if (upgradeTypeInstance == null) {
			final List<CUpgradeEffect> upgradeEffects = new ArrayList<>();
			for (int i = 0; i < EFFECT.length; i++) {
				final War3ID effectMetaKey = EFFECT[i];
				final War3ID effectBaseMetaKey = EFFECT_BASE[i];
				final War3ID effectModMetaKey = EFFECT_MOD[i];
				final War3ID effectCodeMetaKey = EFFECT_CODE[i];

				/* This effectId defines what type of benefit the upgrade will provide */
				final String effectIdString = upgradeType.getFieldAsString(effectMetaKey, 0);
				if (effectIdString.length() == 4) {
					final War3ID effectId = War3ID.fromString(effectIdString);
					// NOTE: maybe a string switch is not performant, if it's a problem maybe change
					// it later but the syntax is pretty nice and the calculation is cached and only
					// runs once per upgrade
					switch (effectId.toString()) {
					case "ratd":
						upgradeEffects
								.add(new CUpgradeEffectAttackDice(upgradeType.getFieldAsInteger(effectBaseMetaKey, 0),
										upgradeType.getFieldAsInteger(effectModMetaKey, 0)));
						break;
					case "rlev":
						final String spellIdField = upgradeType.getFieldAsString(effectCodeMetaKey, 0);
						if (spellIdField.length() == 4) {
							upgradeEffects.add(
									new CUpgradeEffectSpellLevel(upgradeType.getFieldAsInteger(effectBaseMetaKey, 0),
											upgradeType.getFieldAsInteger(effectModMetaKey, 0),
											War3ID.fromString(spellIdField)));
						}
						break;
					case "rhpx":
						upgradeEffects
								.add(new CUpgradeEffectHitPoints(upgradeType.getFieldAsInteger(effectBaseMetaKey, 0),
										upgradeType.getFieldAsInteger(effectModMetaKey, 0)));
						break;
					case "rmnx":
						upgradeEffects
								.add(new CUpgradeEffectManaPoints(upgradeType.getFieldAsInteger(effectBaseMetaKey, 0),
										upgradeType.getFieldAsInteger(effectModMetaKey, 0)));
						break;
					case "rmvx":
						upgradeEffects.add(
								new CUpgradeEffectMovementSpeed(upgradeType.getFieldAsInteger(effectBaseMetaKey, 0),
										upgradeType.getFieldAsInteger(effectModMetaKey, 0)));
						break;
					case "rmnr":
						upgradeEffects
								.add(new CUpgradeEffectManaRegen(upgradeType.getFieldAsFloat(effectBaseMetaKey, 0),
										upgradeType.getFieldAsFloat(effectModMetaKey, 0)));
						break;
					case "rhpo":
						upgradeEffects
								.add(new CUpgradeEffectHitPointsPcnt(upgradeType.getFieldAsFloat(effectBaseMetaKey, 0),
										upgradeType.getFieldAsFloat(effectModMetaKey, 0)));
						break;
					case "rman":
						upgradeEffects
								.add(new CUpgradeEffectManaPointsPcnt(upgradeType.getFieldAsFloat(effectBaseMetaKey, 0),
										upgradeType.getFieldAsFloat(effectModMetaKey, 0)));
						break;
					case "rmov":
						upgradeEffects.add(
								new CUpgradeEffectMovementSpeedPcnt(upgradeType.getFieldAsFloat(effectBaseMetaKey, 0),
										upgradeType.getFieldAsFloat(effectModMetaKey, 0)));
						break;
					case "ratx":
						upgradeEffects
								.add(new CUpgradeEffectAttackDamage(upgradeType.getFieldAsInteger(effectBaseMetaKey, 0),
										upgradeType.getFieldAsInteger(effectModMetaKey, 0)));
						break;
					case "ratr":
						upgradeEffects
								.add(new CUpgradeEffectAttackRange(upgradeType.getFieldAsInteger(effectBaseMetaKey, 0),
										upgradeType.getFieldAsInteger(effectModMetaKey, 0)));
						break;
					case "rats":
						upgradeEffects
								.add(new CUpgradeEffectAttackSpeed(upgradeType.getFieldAsFloat(effectBaseMetaKey, 0),
										upgradeType.getFieldAsFloat(effectModMetaKey, 0)));
						break;
					case "rhpr":
						upgradeEffects
								.add(new CUpgradeEffectHitPointRegen(upgradeType.getFieldAsFloat(effectBaseMetaKey, 0),
										upgradeType.getFieldAsFloat(effectModMetaKey, 0)));
						break;
					case "rtma":
						upgradeEffects.add(
								new CUpgradeEffectTechMaxAllowed(upgradeType.getFieldAsInteger(effectBaseMetaKey, 0),
										War3ID.fromString(upgradeType.getFieldAsString(effectCodeMetaKey, 0))));
						break;
					case "rarm":
						upgradeEffects.add(new CUpgradeEffectDefenseUpgradeBonus());
						break;
					default:
						System.err.println("No such UpgradeEffect: " + effectIdString);
						break;
					}
				}
			}

			final boolean appliesToAllUnits = upgradeType.getFieldAsBoolean(APPLIES_TO_ALL_UNITS, 0);

			final String classString = upgradeType.getFieldAsString(CLASS, 0);
			final CUpgradeClass upgradeClass = CUpgradeClass.parseUpgradeClass(classString);

			final int goldBase = upgradeType.getFieldAsInteger(GOLD_BASE, 0);
			final int goldIncrement = upgradeType.getFieldAsInteger(GOLD_INCREMENT, 0);

			final int levelCount = upgradeType.getFieldAsInteger(LEVELS, 0);

			final int lumberBase = upgradeType.getFieldAsInteger(LUMBER_BASE, 0);
			final int lumberIncrement = upgradeType.getFieldAsInteger(LUMBER_INCREMENT, 0);

			final String raceString = upgradeType.getFieldAsString(RACE, 0);
			final CUnitRace unitRace = CUnitRace.parseRace(raceString);

			final int timeBase = (int) Math
					.ceil(upgradeType.getFieldAsInteger(TIME_BASE, 0) * WarsmashConstants.GAME_SPEED_TIME_FACTOR);
			final int timeIncrement = (int) Math
					.ceil(upgradeType.getFieldAsInteger(TIME_INCREMENT, 0) * WarsmashConstants.GAME_SPEED_TIME_FACTOR);

			final boolean transferWithUnitOwnership = upgradeType.getFieldAsBoolean(TRANSFER_WITH_UNIT_OWNERSHIP, 0);

			final List<CUpgradeType.UpgradeLevel> upgradeLevels = new ArrayList<>();
			for (int i = 1; i <= levelCount; i++) {
				final String requirementsTierString = upgradeType.getFieldAsString(REQUIREMENTS, i);
				final String requirementsLevelsString = upgradeType.getFieldAsString(REQUIREMENTS_LEVELS, i);
				final List<CUnitTypeRequirement> tierRequirements = CUnitData.parseRequirements(requirementsTierString,
						requirementsLevelsString);
				final String levelName = upgradeType.getFieldAsString(NAME, i);
				upgradeLevels.add(new CUpgradeType.UpgradeLevel(levelName, tierRequirements));
			}

			upgradeTypeInstance = new CUpgradeType(typeId, upgradeEffects, appliesToAllUnits, upgradeClass, goldBase,
					goldIncrement, levelCount, lumberBase, lumberIncrement, unitRace, timeBase, timeIncrement,
					transferWithUnitOwnership, upgradeLevels);
			this.idToType.put(typeId, upgradeTypeInstance);
		}
		return upgradeTypeInstance;
	}

}
