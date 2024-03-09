package com.etheller.warsmash.viewer5.handlers.w3x.simulation.data;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.units.ObjectData;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.environment.PathingGrid;
import com.etheller.warsmash.viewer5.handlers.w3x.environment.PathingGrid.MovementType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CGameplayConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitClassification;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitTypeRequirement;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUpgradeType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.HandleIdAllocator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityAttack;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityMove;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.GetAbilityByRawcodeVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.autocast.CAutocastAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.build.CAbilityHumanBuild;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.build.CAbilityNagaBuild;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.build.CAbilityNightElfBuild;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.build.CAbilityOrcBuild;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.build.CAbilityUndeadBuild;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.CLevelingAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.hero.CAbilityHero;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.hero.CPrimaryAttribute;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.inventory.CAbilityInventory;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.item.shop.CAbilitySellItems;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.queue.CAbilityQueue;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.queue.CAbilityRally;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.queue.CAbilityReviveHero;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.upgrade.CAbilityUpgrade;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CAttackType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CDefenseType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CRegenType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CUpgradeClass;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CWeaponType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttack;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttackInstant;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttackMissile;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttackMissileBounce;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttackMissileLine;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttackMissileSplash;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttackNormal;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.pathing.CBuildingPathingType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.SimulationRenderController;

public class CUnitData {
	private static final String MANA_INITIAL_AMOUNT = "mana0"; // replaced from 'umpi'
	private static final String MANA_MAXIMUM = "manaN"; // replaced from 'umpm'
	private static final String MANA_REGEN = "regenMana"; // replaced from 'umpr'
	private static final String HIT_POINT_MAXIMUM = "HP"; // replaced from 'uhpm'
	private static final String HIT_POINT_REGEN = "regenHP"; // replaced from 'uhpr'
	private static final String HIT_POINT_REGEN_TYPE = "regenType"; // replaced from 'uhrt'
	private static final String MOVEMENT_SPEED_BASE = "spd"; // replaced from 'umvs'
	private static final String PROPULSION_WINDOW = "propWin"; // replaced from 'uprw'
	private static final String TURN_RATE = "turnRate"; // replaced from 'umvr'
	private static final String IS_BLDG = "isbldg"; // replaced from 'ubdg'
	private static final String NAME = "Name"; // replaced from 'unam'
	private static final String PROPER_NAMES = "Propernames"; // replaced from 'upro'
	private static final String PROPER_NAMES_COUNT = "nameCount"; // replaced from 'upru'
	private static final String PROJECTILE_LAUNCH_X = "launchX"; // replaced from 'ulpx'
	private static final String PROJECTILE_LAUNCH_Y = "launchY"; // replaced from 'ulpy'
	private static final String PROJECTILE_LAUNCH_Z = "launchZ"; // replaced from 'ulpz'
	private static final String ATTACKS_ENABLED = "weapsOn"; // replaced from 'uaen'
	private static final String ATTACK1_BACKSWING_POINT = "backSw1"; // replaced from 'ubs1'
	private static final String ATTACK1_DAMAGE_POINT = "dmgpt1"; // replaced from 'udp1'
	private static final String ATTACK1_AREA_OF_EFFECT_FULL_DMG = "Farea1"; // replaced from 'ua1f'
	private static final String ATTACK1_AREA_OF_EFFECT_HALF_DMG = "Harea1"; // replaced from 'ua1h'
	private static final String ATTACK1_AREA_OF_EFFECT_QUARTER_DMG = "Qarea1"; // replaced from 'ua1q'
	private static final String ATTACK1_AREA_OF_EFFECT_TARGETS = "splashTargs1"; // replaced from 'ua1p'
	private static final String ATTACK1_ATTACK_TYPE = "atkType1"; // replaced from 'ua1t'
	private static final String ATTACK1_COOLDOWN = "cool1"; // replaced from 'ua1c'
	private static final String ATTACK1_DMG_BASE = "dmgplus1"; // replaced from 'ua1b'
	private static final String ATTACK1_DAMAGE_FACTOR_HALF = "Hfact1"; // replaced from 'uhd1'
	private static final String ATTACK1_DAMAGE_FACTOR_QUARTER = "Qfact1"; // replaced from 'uqd1'
	private static final String ATTACK1_DAMAGE_LOSS_FACTOR = "damageLoss1"; // replaced from 'udl1'
	private static final String ATTACK1_DMG_DICE = "dice1"; // replaced from 'ua1d'
	private static final String ATTACK1_DMG_SIDES_PER_DIE = "sides1"; // replaced from 'ua1s'
	private static final String ATTACK1_DMG_SPILL_DIST = "spillDist1"; // replaced from 'usd1'
	private static final String ATTACK1_DMG_SPILL_RADIUS = "spillRadius1"; // replaced from 'usr1'
	private static final String ATTACK1_DMG_UPGRADE_AMT = "dmgUp1"; // replaced from 'udu1'
	private static final String ATTACK1_TARGET_COUNT = "targCount1"; // replaced from 'utc1'
	private static final String ATTACK1_PROJECTILE_ARC = "Missilearc"; // replaced from 'uma1'
	private static final String ATTACK1_MISSILE_ART = "Missileart"; // replaced from 'ua1m'
	private static final String ATTACK1_PROJECTILE_HOMING_ENABLED = "MissileHoming"; // replaced from 'umh1'
	private static final String ATTACK1_PROJECTILE_SPEED = "Missilespeed"; // replaced from 'ua1z'
	private static final String ATTACK1_RANGE = "rangeN1"; // replaced from 'ua1r'
	private static final String ATTACK1_RANGE_MOTION_BUFFER = "RngBuff1"; // replaced from 'urb1'
	private static final String ATTACK1_SHOW_UI = "showUI1"; // replaced from 'uwu1'
	private static final String ATTACK1_TARGETS_ALLOWED = "targs1"; // replaced from 'ua1g'
	private static final String ATTACK1_WEAPON_SOUND = "weapType1"; // replaced from 'ucs1'
	private static final String ATTACK1_WEAPON_TYPE = "weapTp1"; // replaced from 'ua1w'

	private static final String ATTACK2_BACKSWING_POINT = "backSw2"; // replaced from 'ubs2'
	private static final String ATTACK2_DAMAGE_POINT = "dmgpt2"; // replaced from 'udp2'
	private static final String ATTACK2_AREA_OF_EFFECT_FULL_DMG = "Farea2"; // replaced from 'ua2f'
	private static final String ATTACK2_AREA_OF_EFFECT_HALF_DMG = "Harea2"; // replaced from 'ua2h'
	private static final String ATTACK2_AREA_OF_EFFECT_QUARTER_DMG = "Qarea2"; // replaced from 'ua2q'
	private static final String ATTACK2_AREA_OF_EFFECT_TARGETS = "splashTargs2"; // replaced from 'ua2p'
	private static final String ATTACK2_ATTACK_TYPE = "atkType2"; // replaced from 'ua2t'
	private static final String ATTACK2_COOLDOWN = "cool2"; // replaced from 'ua2c'
	private static final String ATTACK2_DMG_BASE = "dmgplus2"; // replaced from 'ua2b'
	private static final String ATTACK2_DAMAGE_FACTOR_HALF = "Hfact2"; // replaced from 'uhd2'
	private static final String ATTACK2_DAMAGE_FACTOR_QUARTER = "Qfact2"; // replaced from 'uqd2'
	private static final String ATTACK2_DAMAGE_LOSS_FACTOR = "damageLoss2"; // replaced from 'udl2'
	private static final String ATTACK2_DMG_DICE = "dice2"; // replaced from 'ua2d'
	private static final String ATTACK2_DMG_SIDES_PER_DIE = "sides2"; // replaced from 'ua2s'
	private static final String ATTACK2_DMG_SPILL_DIST = "spillDist2"; // replaced from 'usd2'
	private static final String ATTACK2_DMG_SPILL_RADIUS = "spillRadius2"; // replaced from 'usr2'
	private static final String ATTACK2_DMG_UPGRADE_AMT = "dmgUp2"; // replaced from 'udu2'
	private static final String ATTACK2_TARGET_COUNT = "targCount2"; // replaced from 'utc2'
	private static final String ATTACK2_PROJECTILE_ARC = "Missilearc"; // replaced from 'uma2'
	private static final String ATTACK2_MISSILE_ART = "Missileart"; // replaced from 'ua2m'
	private static final String ATTACK2_PROJECTILE_HOMING_ENABLED = "MissileHoming"; // replaced from 'umh2'
	private static final String ATTACK2_PROJECTILE_SPEED = "Missilespeed"; // replaced from 'ua2z'
	private static final String ATTACK2_RANGE = "rangeN2"; // replaced from 'ua2r'
	private static final String ATTACK2_RANGE_MOTION_BUFFER = "RngBuff2"; // replaced from 'urb2'
	private static final String ATTACK2_SHOW_UI = "showUI2"; // replaced from 'uwu2'
	private static final String ATTACK2_TARGETS_ALLOWED = "targs2"; // replaced from 'ua2g'
	private static final String ATTACK2_WEAPON_SOUND = "weapType2"; // replaced from 'ucs2'
	private static final String ATTACK2_WEAPON_TYPE = "weapTp2"; // replaced from 'ua2w'

	private static final String CAST_BACKSWING_POINT = "castbsw"; // replaced from 'ucbs'
	private static final String CAST_POINT = "castpt"; // replaced from 'ucpt'

	private static final String ACQUISITION_RANGE = "acquire"; // replaced from 'uacq'
	private static final String MINIMUM_ATTACK_RANGE = "minRange"; // replaced from 'uamn'

	private static final String PROJECTILE_IMPACT_Z = "impactZ"; // replaced from 'uimz'

	private static final String DEATH_TYPE = "deathType"; // replaced from 'udea'
	private static final String ARMOR_TYPE = "armor"; // replaced from 'uarm'

	private static final String DEFENSE = "def"; // replaced from 'udef'
	private static final String DEFENSE_TYPE = "defType"; // replaced from 'udty'
	private static final String DEFENSE_UPGRADE_BONUS = "defUp"; // replaced from 'udup'
	private static final String MOVE_HEIGHT = "moveHeight"; // replaced from 'umvh'
	private static final String MOVE_TYPE = "movetp"; // replaced from 'umvt'
	private static final String COLLISION_SIZE = "collision"; // replaced from 'ucol'
	private static final String CLASSIFICATION = "type"; // replaced from 'utyp'
	private static final String DEATH_TIME = "death"; // replaced from 'udtm'
	private static final String TARGETED_AS = "targType"; // replaced from 'utar'

	private static final String ABILITIES_DEFAULT_AUTO = "auto"; // replaced from 'uabi'
	private static final String ABILITIES_NORMAL = "abilList"; // replaced from 'uabi'
	private static final String ABILITIES_HERO = "heroAbilList"; // replaced from 'uhab'

	private static final String STRUCTURES_BUILT = "Builds"; // replaced from 'ubui'
	private static final String UNITS_TRAINED = "Trains"; // replaced from 'utra'
	private static final String RESEARCHES_AVAILABLE = "Researches"; // replaced from 'ures'
	private static final String UPGRADES_USED = "upgrades"; // replaced from 'upgr'
	private static final String UPGRADES_TO = "Upgrade"; // replaced from 'uupt'
	private static final String ITEMS_SOLD = "Sellitems"; // replaced from 'usei'
	private static final String ITEMS_MADE = "Makeitems"; // replaced from 'umki'
	private static final String REVIVES_HEROES = "Revive"; // replaced from 'urev'
	private static final String UNIT_RACE = "race"; // replaced from 'urac'

	private static final String REQUIRES = "Requires"; // replaced from 'ureq'
	private static final String REQUIRES_AMOUNT = "Requiresamount"; // replaced from 'urqa'
	private static final String REQUIRES_TIER_COUNT = "Requirescount"; // replaced from 'urqc'
	private static final String[] REQUIRES_TIER_X = { "Requires1", "Requires2", // replaced from 'urq1'
			"Requires3", "Requires4", "Requires5", "Requires6", // replaced from 'urq3'
			"Requires7", "Requires8", "Requires9" }; // replaced from 'urq7'

	private static final String GOLD_COST = "goldcost"; // replaced from 'ugol'
	private static final String LUMBER_COST = "lumbercost"; // replaced from 'ulum'
	private static final String BUILD_TIME = "bldtm"; // replaced from 'ubld'
	private static final String FOOD_USED = "fused"; // replaced from 'ufoo'
	private static final String FOOD_MADE = "fmade"; // replaced from 'ufma'

	private static final String REQUIRE_PLACE = "requirePlace"; // replaced from 'upar'
	private static final String PREVENT_PLACE = "preventPlace"; // replaced from 'upap'

	private static final String UNIT_LEVEL = "level"; // replaced from 'ulev'

	private static final String STR = "STR"; // replaced from 'ustr'
	private static final String STR_PLUS = "STRplus"; // replaced from 'ustp'
	private static final String AGI = "AGI"; // replaced from 'uagi'
	private static final String AGI_PLUS = "AGIplus"; // replaced from 'uagp'
	private static final String INT = "INT"; // replaced from 'uint'
	private static final String INT_PLUS = "INTplus"; // replaced from 'uinp'
	private static final String PRIMARY_ATTRIBUTE = "Primary"; // replaced from 'upra'

	private static final String CAN_FLEE = "canFlee"; // replaced from 'ufle'
	private static final String PRIORITY = "prio"; // replaced from 'upri'

	private static final String POINT_VALUE = "points"; // replaced from 'upoi'

	private static final String CAN_BE_BUILT_ON_THEM = "isBuildOn"; // replaced from 'uibo'
	private static final String CAN_BUILD_ON_ME = "canBuildOn"; // replaced from 'ucbo'

	private static final String SIGHT_RADIUS_DAY = "sight"; // replaced from 'usid'
	private static final String SIGHT_RADIUS_NIGHT = "nsight"; // replaced from 'usin'
	private static final String EXTENDED_LOS = "fatLOS"; // replaced from 'ulos'

	private static final String GOLD_BOUNTY_AWARDED_BASE = "bountyplus"; // replaced from 'ubba'
	private static final String GOLD_BOUNTY_AWARDED_DICE = "bountydice"; // replaced from 'ubdi'
	private static final String GOLD_BOUNTY_AWARDED_SIDES = "bountysides"; // replaced from 'ubsi'

	private static final String LUMBER_BOUNTY_AWARDED_BASE = "lumberbountyplus"; // replaced from 'ulba'
	private static final String LUMBER_BOUNTY_AWARDED_DICE = "lumberbountydice"; // replaced from 'ulbd'
	private static final String LUMBER_BOUNTY_AWARDED_SIDES = "lumberbountysides"; // replaced from 'ulbs'

	private static final String NEUTRAL_BUILDING_SHOW_ICON = "nbmmIcon";

	private final CGameplayConstants gameplayConstants;
	private final ObjectData unitData;
	private final Map<War3ID, CUnitType> unitIdToUnitType = new HashMap<>();
	private final Map<String, War3ID> jassLegacyNameToUnitId = new HashMap<>();
	private final CAbilityData abilityData;
	private final CUpgradeData upgradeData;
	private final SimulationRenderController simulationRenderController;

	public CUnitData(final CGameplayConstants gameplayConstants, final ObjectData unitData,
			final CAbilityData abilityData, final CUpgradeData upgradeData,
			final SimulationRenderController simulationRenderController) {
		this.gameplayConstants = gameplayConstants;
		this.unitData = unitData;
		this.abilityData = abilityData;
		this.upgradeData = upgradeData;
		this.simulationRenderController = simulationRenderController;
	}

	public CUnit create(final CSimulation simulation, final int playerIndex, final War3ID typeId, final float x,
			final float y, final float facing, final BufferedImage buildingPathingPixelMap,
			final HandleIdAllocator handleIdAllocator) {
		final GameObject unitType = this.unitData.get(typeId.asStringValue());
		final int handleId = handleIdAllocator.createId();

		final CUnitType unitTypeInstance = getUnitTypeInstance(typeId, buildingPathingPixelMap, unitType);
		final int life = unitTypeInstance.getMaxLife();
		final float lifeRegen = unitTypeInstance.getLifeRegen();
		final int manaInitial = unitTypeInstance.getManaInitial();
		final int manaMaximum = unitTypeInstance.getManaMaximum();
		final int speed = unitTypeInstance.getSpeed();

		final CUnit unit = new CUnit(handleId, playerIndex, x, y, life, typeId, facing, manaInitial, life, lifeRegen,
				manaMaximum, speed, unitTypeInstance);
		return unit;
	}

	public void applyPlayerUpgradesToUnit(final CSimulation simulation, final int playerIndex,
			final CUnitType unitTypeInstance, final CUnit unit) {
		final CPlayer player = simulation.getPlayer(playerIndex);
		for (final War3ID upgradeId : unitTypeInstance.getUpgradesUsed()) {
			final int techtreeUnlocked = player.getTechtreeUnlocked(upgradeId);
			if (techtreeUnlocked > 0) {
				final CUpgradeType upgradeType = this.upgradeData.getType(upgradeId);
				if (upgradeType != null) {
					upgradeType.apply(simulation, unit, techtreeUnlocked);
				}
			}
		}
	}

	public void unapplyPlayerUpgradesToUnit(final CSimulation simulation, final int playerIndex,
			final CUnitType unitTypeInstance, final CUnit unit) {
		final CPlayer player = simulation.getPlayer(playerIndex);
		for (final War3ID upgradeId : unitTypeInstance.getUpgradesUsed()) {
			final int techtreeUnlocked = player.getTechtreeUnlocked(upgradeId);
			if (techtreeUnlocked > 0) {
				final CUpgradeType upgradeType = this.upgradeData.getType(upgradeId);
				if (upgradeType != null) {
					upgradeType.unapply(simulation, unit, techtreeUnlocked);
				}
			}
		}
	}

	public void addDefaultAbilitiesToUnit(final CSimulation simulation, final HandleIdAllocator handleIdAllocator,
			final CUnitType unitTypeInstance, final boolean resetMana, final int manaInitial, final int speed,
			final CUnit unit) {
		if (speed > 0) {
			unit.add(simulation, new CAbilityMove(handleIdAllocator.createId()));
		}
		final List<CUnitAttack> unitSpecificAttacks = new ArrayList<>();
		for (final CUnitAttack attack : unitTypeInstance.getAttacks()) {
			unitSpecificAttacks.add(attack.copy());
		}
		unit.setUnitSpecificAttacks(unitSpecificAttacks);
		unit.setUnitSpecificCurrentAttacks(
				getEnabledAttacks(unitSpecificAttacks, unitTypeInstance.getAttacksEnabled()));
		if (!unit.getCurrentAttacks().isEmpty()) {
			unit.add(simulation, new CAbilityAttack(handleIdAllocator.createId()));
		}
		final List<War3ID> structuresBuilt = unitTypeInstance.getStructuresBuilt();
		if (!structuresBuilt.isEmpty()) {
			switch (unitTypeInstance.getRace()) {
			case ORC:
				unit.add(simulation, new CAbilityOrcBuild(handleIdAllocator.createId(), structuresBuilt));
				break;
			case HUMAN:
				unit.add(simulation, new CAbilityHumanBuild(handleIdAllocator.createId(), structuresBuilt));
				break;
			case UNDEAD:
				unit.add(simulation, new CAbilityUndeadBuild(handleIdAllocator.createId(), structuresBuilt));
				break;
			case NIGHTELF:
				unit.add(simulation, new CAbilityNightElfBuild(handleIdAllocator.createId(), structuresBuilt));
				break;
			case NAGA:
				unit.add(simulation, new CAbilityNagaBuild(handleIdAllocator.createId(), structuresBuilt));
				break;
			case CREEPS:
			case CRITTERS:
			case DEMON:
			case OTHER:
				unit.add(simulation, new CAbilityOrcBuild(handleIdAllocator.createId(), structuresBuilt));
				break;
			}
		}
		final List<War3ID> unitsTrained = unitTypeInstance.getUnitsTrained();
		final List<War3ID> researchesAvailable = unitTypeInstance.getResearchesAvailable();
		final List<War3ID> upgradesTo = unitTypeInstance.getUpgradesTo();
		final List<War3ID> itemsSold = unitTypeInstance.getItemsSold();
		final List<War3ID> itemsMade = unitTypeInstance.getItemsMade();
		if (!unitsTrained.isEmpty() || !researchesAvailable.isEmpty()) {
			unit.add(simulation, new CAbilityQueue(handleIdAllocator.createId(), unitsTrained, researchesAvailable));
		}
		if (!upgradesTo.isEmpty()) {
			unit.add(simulation, new CAbilityUpgrade(handleIdAllocator.createId(), upgradesTo));
		}
		if (!itemsSold.isEmpty()) {
			unit.add(simulation, new CAbilitySellItems(handleIdAllocator.createId(), itemsSold));
		}
		if (!itemsMade.isEmpty()) {
			unit.add(simulation, new CAbilitySellItems(handleIdAllocator.createId(), itemsMade));
		}
		if (unitTypeInstance.isRevivesHeroes()) {
			unit.add(simulation, new CAbilityReviveHero(handleIdAllocator.createId()));
		}
		if (!unitsTrained.isEmpty() || unitTypeInstance.isRevivesHeroes()) {
			unit.add(simulation, new CAbilityRally(handleIdAllocator.createId()));
		}
		if (unitTypeInstance.isHero()) {
			final List<War3ID> heroAbilityList = unitTypeInstance.getHeroAbilityList();
			unit.add(simulation, new CAbilityHero(handleIdAllocator.createId(), heroAbilityList));
			// reset initial mana after the value is adjusted for hero data
			unit.setMana(manaInitial);
		}
		for (final War3ID ability : unitTypeInstance.getAbilityList()) {
			final CLevelingAbility existingAbility = unit
					.getAbility(GetAbilityByRawcodeVisitor.getInstance().reset(ability));
			if ((existingAbility == null) || !existingAbility.isPermanent()) {
				final CAbility createAbility = this.abilityData.createAbility(ability, handleIdAllocator.createId());
				if (createAbility != null) {
					unit.add(simulation, createAbility);
				}
				if (ability.equals(unitTypeInstance.getDefaultAutocastAbility())
						&& (createAbility instanceof CAutocastAbility)) {
					((CAutocastAbility) createAbility).setAutoCastOn(unit, true);
				}
			}
			else {
				if (ability.equals(unitTypeInstance.getDefaultAutocastAbility())
						&& (existingAbility instanceof CAutocastAbility)) {
					((CAutocastAbility) existingAbility).setAutoCastOn(unit, true);
				}
			}
		}

		if (unitTypeInstance.isHero() && simulation.isMapReignOfChaos()
				&& (unit.getFirstAbilityOfType(CAbilityInventory.class) == null)) {
			unit.add(simulation,
					simulation.getAbilityData().createAbility(War3ID.fromString("AInv"), handleIdAllocator.createId()));
		}
	}

	public void addMissingDefaultAbilitiesToUnit(final CSimulation simulation,
			final HandleIdAllocator handleIdAllocator, final CUnitType unitTypeInstance, final boolean resetMana,
			final int manaInitial, final int speed, final CUnit unit) {
		final CAbilityMove preMove = unit.getFirstAbilityOfType(CAbilityMove.class);
		if ((speed > 0) && (preMove == null)) {
			unit.add(simulation, new CAbilityMove(handleIdAllocator.createId()));
		}
		if ((speed <= 0) && (preMove != null)) {
			unit.remove(simulation, preMove);
		}
		final List<CUnitAttack> unitSpecificAttacks = new ArrayList<>();
		for (final CUnitAttack attack : unitTypeInstance.getAttacks()) {
			unitSpecificAttacks.add(attack.copy());
		}
		unit.setUnitSpecificAttacks(unitSpecificAttacks);
		unit.setUnitSpecificCurrentAttacks(
				getEnabledAttacks(unitSpecificAttacks, unitTypeInstance.getAttacksEnabled()));
		if (!unit.getCurrentAttacks().isEmpty()) {
			unit.add(simulation, new CAbilityAttack(handleIdAllocator.createId()));
		}
		final List<War3ID> structuresBuilt = unitTypeInstance.getStructuresBuilt();
		if (!structuresBuilt.isEmpty()) {
			switch (unitTypeInstance.getRace()) {
			case ORC:
				unit.add(simulation, new CAbilityOrcBuild(handleIdAllocator.createId(), structuresBuilt));
				break;
			case HUMAN:
				unit.add(simulation, new CAbilityHumanBuild(handleIdAllocator.createId(), structuresBuilt));
				break;
			case UNDEAD:
				unit.add(simulation, new CAbilityUndeadBuild(handleIdAllocator.createId(), structuresBuilt));
				break;
			case NIGHTELF:
				unit.add(simulation, new CAbilityNightElfBuild(handleIdAllocator.createId(), structuresBuilt));
				break;
			case NAGA:
				unit.add(simulation, new CAbilityNagaBuild(handleIdAllocator.createId(), structuresBuilt));
				break;
			case CREEPS:
			case CRITTERS:
			case DEMON:
			case OTHER:
				unit.add(simulation, new CAbilityOrcBuild(handleIdAllocator.createId(), structuresBuilt));
				break;
			}
		}
		final List<War3ID> unitsTrained = unitTypeInstance.getUnitsTrained();
		final List<War3ID> researchesAvailable = unitTypeInstance.getResearchesAvailable();
		final List<War3ID> upgradesTo = unitTypeInstance.getUpgradesTo();
		final List<War3ID> itemsSold = unitTypeInstance.getItemsSold();
		final List<War3ID> itemsMade = unitTypeInstance.getItemsMade();
		if (!unitsTrained.isEmpty() || !researchesAvailable.isEmpty()) {
			unit.add(simulation, new CAbilityQueue(handleIdAllocator.createId(), unitsTrained, researchesAvailable));
		}
		if (!upgradesTo.isEmpty()) {
			unit.add(simulation, new CAbilityUpgrade(handleIdAllocator.createId(), upgradesTo));
		}
		if (!itemsSold.isEmpty()) {
			unit.add(simulation, new CAbilitySellItems(handleIdAllocator.createId(), itemsSold));
		}
		if (!itemsMade.isEmpty()) {
			unit.add(simulation, new CAbilitySellItems(handleIdAllocator.createId(), itemsMade));
		}
		if (unitTypeInstance.isRevivesHeroes()) {
			unit.add(simulation, new CAbilityReviveHero(handleIdAllocator.createId()));
		}
		if (!unitsTrained.isEmpty() || unitTypeInstance.isRevivesHeroes()) {
			unit.add(simulation, new CAbilityRally(handleIdAllocator.createId()));
		}
		if (unitTypeInstance.isHero()) {
			final List<War3ID> heroAbilityList = unitTypeInstance.getHeroAbilityList();
			if (unit.getFirstAbilityOfType(CAbilityHero.class) != null) {
				final CAbilityHero abil = unit.getFirstAbilityOfType(CAbilityHero.class);
				abil.setSkillsAvailable(heroAbilityList);
				abil.recalculateAllStats(simulation, unit);
			}
			else {
				unit.add(simulation, new CAbilityHero(handleIdAllocator.createId(), heroAbilityList));
				// reset initial mana after the value is adjusted for hero data
				unit.setMana(manaInitial);
			}
		}
		for (final War3ID ability : unitTypeInstance.getAbilityList()) {
			final CLevelingAbility existingAbility = unit
					.getAbility(GetAbilityByRawcodeVisitor.getInstance().reset(ability));
			if ((existingAbility == null)) {
				final CAbility createAbility = this.abilityData.createAbility(ability, handleIdAllocator.createId());
				if (createAbility != null) {
					unit.add(simulation, createAbility);
				}
				if (ability.equals(unitTypeInstance.getDefaultAutocastAbility())
						&& (createAbility instanceof CAutocastAbility)) {
					((CAutocastAbility) createAbility).setAutoCastOn(unit, true);
				}
			}
			else {
				if (ability.equals(unitTypeInstance.getDefaultAutocastAbility())
						&& (existingAbility instanceof CAutocastAbility)) {
					((CAutocastAbility) existingAbility).setAutoCastOn(unit, true);
				}
			}
		}

		if (unitTypeInstance.isHero() && simulation.isMapReignOfChaos()
				&& (unit.getFirstAbilityOfType(CAbilityInventory.class) == null)) {
			unit.add(simulation,
					simulation.getAbilityData().createAbility(War3ID.fromString("AInv"), handleIdAllocator.createId()));
		}
	}

	private CUnitType getUnitTypeInstance(final War3ID typeId, final BufferedImage buildingPathingPixelMap,
			final GameObject unitType) {
		CUnitType unitTypeInstance = this.unitIdToUnitType.get(typeId);
		if (unitTypeInstance == null) {
			final String legacyName = getLegacyName(unitType);
			final int life = unitType.getFieldAsInteger(HIT_POINT_MAXIMUM, 0);
			final float lifeRegen = unitType.getFieldAsFloat(HIT_POINT_REGEN, 0);
			final CRegenType lifeRegenType = CRegenType
					.parseRegenType(unitType.getFieldAsString(HIT_POINT_REGEN_TYPE, 0));
			final int manaInitial = unitType.getFieldAsInteger(MANA_INITIAL_AMOUNT, 0);
			final int manaMaximum = unitType.getFieldAsInteger(MANA_MAXIMUM, 0);
			final float manaRegen = unitType.getFieldAsFloat(MANA_REGEN, 0);
			final int speed = unitType.getFieldAsInteger(MOVEMENT_SPEED_BASE, 0);
			final int defense = unitType.getFieldAsInteger(DEFENSE, 0);
			final String defaultAutocastAbility = unitType.getFieldAsString(ABILITIES_DEFAULT_AUTO, 0);
			final List<String> abilityListString = unitType.getFieldAsList(ABILITIES_NORMAL);
			final List<String> heroAbilityListString = unitType.getFieldAsList(ABILITIES_HERO);
			final int unitLevel = unitType.getFieldAsInteger(UNIT_LEVEL, 0);
			final int priority = unitType.getFieldAsInteger(PRIORITY, 0);
			final int defenseUpgradeBonus = unitType.getFieldAsInteger(DEFENSE_UPGRADE_BONUS, 0);

			final float moveHeight = unitType.getFieldAsFloat(MOVE_HEIGHT, 0);
			final String movetp = unitType.getFieldAsString(MOVE_TYPE, 0);
			final float collisionSize = unitType.getFieldAsFloat(COLLISION_SIZE, 0);
			final float propWindow = unitType.getFieldAsFloat(PROPULSION_WINDOW, 0);
			final float turnRate = unitType.getFieldAsFloat(TURN_RATE, 0);

			final boolean canFlee = unitType.getFieldAsBoolean(CAN_FLEE, 0);

			final boolean canBeBuiltOnThem = unitType.getFieldAsBoolean(CAN_BE_BUILT_ON_THEM, 0);
			final boolean canBuildOnMe = unitType.getFieldAsBoolean(CAN_BUILD_ON_ME, 0);

			final float strPlus = unitType.getFieldAsFloat(STR_PLUS, 0);
			final float agiPlus = unitType.getFieldAsFloat(AGI_PLUS, 0);
			final float intPlus = unitType.getFieldAsFloat(INT_PLUS, 0);

			final int strength = unitType.getFieldAsInteger(STR, 0);
			final int agility = unitType.getFieldAsInteger(AGI, 0);
			final int intelligence = unitType.getFieldAsInteger(INT, 0);
			final CPrimaryAttribute primaryAttribute = CPrimaryAttribute
					.parsePrimaryAttribute(unitType.getFieldAsString(PRIMARY_ATTRIBUTE, 0));

			final String properNames = unitType.getFieldAsString(PROPER_NAMES, 0);
			final int properNamesCount = unitType.getFieldAsInteger(PROPER_NAMES_COUNT, 0);

			final boolean isBldg = unitType.getFieldAsBoolean(IS_BLDG, 0);
			PathingGrid.MovementType movementType = PathingGrid.getMovementType(movetp);
			if (movementType == null) {
				movementType = MovementType.DISABLED;
			}
			final String unitName = unitType.getFieldAsString(NAME, 0);
			final float acquisitionRange = unitType.getFieldAsFloat(ACQUISITION_RANGE, 0);
			// note: uamn expected type int below, not exactly sure why that decision was
			// made but I'll support it
			final float minimumAttackRange = unitType.getFieldAsInteger(MINIMUM_ATTACK_RANGE, 0);
			final EnumSet<CTargetType> targetedAs = CTargetType
					.parseTargetTypeSet(unitType.getFieldAsList(TARGETED_AS));
			final List<String> classificationStringList = unitType.getFieldAsList(CLASSIFICATION);
			final EnumSet<CUnitClassification> classifications = EnumSet.noneOf(CUnitClassification.class);
			if (!classificationStringList.isEmpty()) {
				for (final String unitEditorKey : classificationStringList) {
					final CUnitClassification unitClassification = CUnitClassification
							.parseUnitClassification(unitEditorKey);
					if (unitClassification != null) {
						classifications.add(unitClassification);
					}
				}
			}
			final List<CUnitAttack> attacks = new ArrayList<>();
			final int attacksEnabled = unitType.getFieldAsInteger(ATTACKS_ENABLED, 0);
			try {
				// attack one
				final float animationBackswingPoint = unitType.getFieldAsFloat(ATTACK1_BACKSWING_POINT, 0);
				final float animationDamagePoint = unitType.getFieldAsFloat(ATTACK1_DAMAGE_POINT, 0);
				final int areaOfEffectFullDamage = unitType.getFieldAsInteger(ATTACK1_AREA_OF_EFFECT_FULL_DMG, 0);
				final int areaOfEffectMediumDamage = unitType.getFieldAsInteger(ATTACK1_AREA_OF_EFFECT_HALF_DMG, 0);
				final int areaOfEffectSmallDamage = unitType.getFieldAsInteger(ATTACK1_AREA_OF_EFFECT_QUARTER_DMG, 0);
				final EnumSet<CTargetType> areaOfEffectTargets = CTargetType
						.parseTargetTypeSet(unitType.getFieldAsList(ATTACK1_AREA_OF_EFFECT_TARGETS));
				final CAttackType attackType = CAttackType
						.parseAttackType(unitType.getFieldAsString(ATTACK1_ATTACK_TYPE, 0));
				final float cooldownTime = unitType.getFieldAsFloat(ATTACK1_COOLDOWN, 0);
				final int damageBase = unitType.getFieldAsInteger(ATTACK1_DMG_BASE, 0);
				final float damageFactorMedium = unitType.getFieldAsFloat(ATTACK1_DAMAGE_FACTOR_HALF, 0);
				final float damageFactorSmall = unitType.getFieldAsFloat(ATTACK1_DAMAGE_FACTOR_QUARTER, 0);
				final float damageLossFactor = unitType.getFieldAsFloat(ATTACK1_DAMAGE_LOSS_FACTOR, 0);
				final int damageDice = unitType.getFieldAsInteger(ATTACK1_DMG_DICE, 0);
				final int damageSidesPerDie = unitType.getFieldAsInteger(ATTACK1_DMG_SIDES_PER_DIE, 0);
				final float damageSpillDistance = unitType.getFieldAsFloat(ATTACK1_DMG_SPILL_DIST, 0);
				final float damageSpillRadius = unitType.getFieldAsFloat(ATTACK1_DMG_SPILL_RADIUS, 0);
				final int damageUpgradeAmount = unitType.getFieldAsInteger(ATTACK1_DMG_UPGRADE_AMT, 0);
				final int maximumNumberOfTargets = unitType.getFieldAsInteger(ATTACK1_TARGET_COUNT, 0);
				final float projectileArc = unitType.getFieldAsFloat(ATTACK1_PROJECTILE_ARC, 0);
				final String projectileArt = unitType.getFieldAsString(ATTACK1_MISSILE_ART, 0);
				final boolean projectileHomingEnabled = unitType.getFieldAsBoolean(ATTACK1_PROJECTILE_HOMING_ENABLED,
						0);
				final int projectileSpeed = unitType.getFieldAsInteger(ATTACK1_PROJECTILE_SPEED, 0);
				final int range = unitType.getFieldAsInteger(ATTACK1_RANGE, 0);
				final float rangeMotionBuffer = unitType.getFieldAsFloat(ATTACK1_RANGE_MOTION_BUFFER, 0);
				final boolean showUI = unitType.getFieldAsBoolean(ATTACK1_SHOW_UI, 0);
				final EnumSet<CTargetType> targetsAllowed = CTargetType
						.parseTargetTypeSet(unitType.getFieldAsList(ATTACK1_TARGETS_ALLOWED));
				final String weaponSound = unitType.getFieldAsString(ATTACK1_WEAPON_SOUND, 0);
				final String weapon_type_temp = unitType.getFieldAsString(ATTACK1_WEAPON_TYPE, 0);
				CWeaponType weaponType = CWeaponType.NONE;
				if (!"_".equals(weapon_type_temp)) {
					weaponType = CWeaponType.parseWeaponType(weapon_type_temp);
				}
				attacks.add(createAttack(animationBackswingPoint, animationDamagePoint, areaOfEffectFullDamage,
						areaOfEffectMediumDamage, areaOfEffectSmallDamage, areaOfEffectTargets, attackType,
						cooldownTime, damageBase, damageFactorMedium, damageFactorSmall, damageLossFactor, damageDice,
						damageSidesPerDie, damageSpillDistance, damageSpillRadius, damageUpgradeAmount,
						maximumNumberOfTargets, projectileArc, projectileArt, projectileHomingEnabled, projectileSpeed,
						range, rangeMotionBuffer, showUI, targetsAllowed, weaponSound, weaponType));
			}
			catch (final Exception exc) {
				System.err.println("Attack 1 failed to parse with: " + exc.getClass() + ":" + exc.getMessage());
			}
			try {
				// attack two
				final float animationBackswingPoint = unitType.getFieldAsFloat(ATTACK2_BACKSWING_POINT, 0);
				final float animationDamagePoint = unitType.getFieldAsFloat(ATTACK2_DAMAGE_POINT, 0);
				final int areaOfEffectFullDamage = unitType.getFieldAsInteger(ATTACK2_AREA_OF_EFFECT_FULL_DMG, 0);
				final int areaOfEffectMediumDamage = unitType.getFieldAsInteger(ATTACK2_AREA_OF_EFFECT_HALF_DMG, 0);
				final int areaOfEffectSmallDamage = unitType.getFieldAsInteger(ATTACK2_AREA_OF_EFFECT_QUARTER_DMG, 0);
				final EnumSet<CTargetType> areaOfEffectTargets = CTargetType
						.parseTargetTypeSet(unitType.getFieldAsList(ATTACK2_AREA_OF_EFFECT_TARGETS));
				final CAttackType attackType = CAttackType
						.parseAttackType(unitType.getFieldAsString(ATTACK2_ATTACK_TYPE, 0));
				final float cooldownTime = unitType.getFieldAsFloat(ATTACK2_COOLDOWN, 0);
				final int damageBase = unitType.getFieldAsInteger(ATTACK2_DMG_BASE, 0);
				final float damageFactorMedium = unitType.getFieldAsFloat(ATTACK2_DAMAGE_FACTOR_HALF, 0);
				final float damageFactorSmall = unitType.getFieldAsFloat(ATTACK2_DAMAGE_FACTOR_QUARTER, 0);
				final float damageLossFactor = unitType.getFieldAsFloat(ATTACK2_DAMAGE_LOSS_FACTOR, 0);
				final int damageDice = unitType.getFieldAsInteger(ATTACK2_DMG_DICE, 0);
				final int damageSidesPerDie = unitType.getFieldAsInteger(ATTACK2_DMG_SIDES_PER_DIE, 0);
				final float damageSpillDistance = unitType.getFieldAsFloat(ATTACK2_DMG_SPILL_DIST, 0);
				final float damageSpillRadius = unitType.getFieldAsFloat(ATTACK2_DMG_SPILL_RADIUS, 0);
				final int damageUpgradeAmount = unitType.getFieldAsInteger(ATTACK2_DMG_UPGRADE_AMT, 0);
				final int maximumNumberOfTargets = unitType.getFieldAsInteger(ATTACK2_TARGET_COUNT, 0);
				float projectileArc = unitType.getFieldAsFloat(ATTACK2_PROJECTILE_ARC, 0);
				String projectileArt = unitType.getFieldAsString(ATTACK2_MISSILE_ART, 0);
				int projectileSpeed = unitType.getFieldAsInteger(ATTACK2_PROJECTILE_SPEED, 0);
				if ("_".equals(projectileArt) || projectileArt.isEmpty()) {
					projectileArt = unitType.getFieldAsString(ATTACK1_MISSILE_ART, 0);
					projectileSpeed = unitType.getFieldAsInteger(ATTACK1_PROJECTILE_SPEED, 0);
					projectileArc = unitType.getFieldAsFloat(ATTACK1_PROJECTILE_ARC, 0);
				}
				final boolean projectileHomingEnabled = unitType.getFieldAsBoolean(ATTACK2_PROJECTILE_HOMING_ENABLED,
						0);
				final int range = unitType.getFieldAsInteger(ATTACK2_RANGE, 0);
				final float rangeMotionBuffer = unitType.getFieldAsFloat(ATTACK2_RANGE_MOTION_BUFFER, 0);
				boolean showUI = unitType.getFieldAsBoolean(ATTACK2_SHOW_UI, 0);
				final EnumSet<CTargetType> targetsAllowed = CTargetType
						.parseTargetTypeSet(unitType.getFieldAsList(ATTACK2_TARGETS_ALLOWED));
				final String weaponSound = unitType.getFieldAsString(ATTACK2_WEAPON_SOUND, 0);
				final String weapon_type_temp = unitType.getFieldAsString(ATTACK2_WEAPON_TYPE, 0);
				CWeaponType weaponType = CWeaponType.NONE;
				if (!"_".equals(weapon_type_temp)) {
					weaponType = CWeaponType.parseWeaponType(weapon_type_temp);
				}
				if (!attacks.isEmpty()) {
					final CUnitAttack otherAttack = attacks.get(0);
					if ((otherAttack.getAttackType() == attackType) && (targetsAllowed.size() == 1)
							&& (targetsAllowed.contains(CTargetType.TREE)
									|| (targetsAllowed.contains(CTargetType.STRUCTURE)
											&& (otherAttack.getDamageBase() == damageBase)
											&& (otherAttack.getDamageSidesPerDie() == damageSidesPerDie)
											&& (otherAttack.getDamageDice() == damageDice)))) {
						showUI = false;
					}
				}
				attacks.add(createAttack(animationBackswingPoint, animationDamagePoint, areaOfEffectFullDamage,
						areaOfEffectMediumDamage, areaOfEffectSmallDamage, areaOfEffectTargets, attackType,
						cooldownTime, damageBase, damageFactorMedium, damageFactorSmall, damageLossFactor, damageDice,
						damageSidesPerDie, damageSpillDistance, damageSpillRadius, damageUpgradeAmount,
						maximumNumberOfTargets, projectileArc, projectileArt, projectileHomingEnabled, projectileSpeed,
						range, rangeMotionBuffer, showUI, targetsAllowed, weaponSound, weaponType));
			}
			catch (final Exception exc) {
				System.err.println("Attack 2 failed to parse with: " + exc.getClass() + ":" + exc.getMessage());
			}
			final List<CUnitAttack> enabledAttacks = getEnabledAttacks(attacks, attacksEnabled);
			final int deathType = unitType.getFieldAsInteger(DEATH_TYPE, 0);
			final boolean raise = (deathType & 0x1) != 0;
			final boolean decay = (deathType & 0x2) != 0;
			final String armorType = unitType.getFieldAsString(ARMOR_TYPE, 0);
			final float impactZ = unitType.getFieldAsFloat(PROJECTILE_IMPACT_Z, 0);
			final CDefenseType defenseType = CDefenseType.parseDefenseType(unitType.getFieldAsString(DEFENSE_TYPE, 0));
			final float deathTime = unitType.getFieldAsFloat(DEATH_TIME, 0);
			final int goldCost = unitType.getFieldAsInteger(GOLD_COST, 0);
			final int lumberCost = unitType.getFieldAsInteger(LUMBER_COST, 0);
			final int buildTime = (int) Math
					.ceil(unitType.getFieldAsInteger(BUILD_TIME, 0) * WarsmashConstants.GAME_SPEED_TIME_FACTOR);
			final int foodUsed = unitType.getFieldAsInteger(FOOD_USED, 0);
			final int foodMade = unitType.getFieldAsInteger(FOOD_MADE, 0);

			final float castBackswingPoint = unitType.getFieldAsFloat(CAST_BACKSWING_POINT, 0);
			final float castPoint = unitType.getFieldAsFloat(CAST_POINT, 0);

			final int pointValue = unitType.getFieldAsInteger(POINT_VALUE, 0);

			final int sightRadiusDay = unitType.getFieldAsInteger(SIGHT_RADIUS_DAY, 0);
			final int sightRadiusNight = unitType.getFieldAsInteger(SIGHT_RADIUS_NIGHT, 0);
			final boolean extendedLineOfSight = unitType.getFieldAsBoolean(EXTENDED_LOS, 0);

			final int goldBountyAwardedBase = unitType.getFieldAsInteger(GOLD_BOUNTY_AWARDED_BASE, 0);
			final int goldBountyAwardedDice = unitType.getFieldAsInteger(GOLD_BOUNTY_AWARDED_DICE, 0);
			final int goldBountyAwardedSides = unitType.getFieldAsInteger(GOLD_BOUNTY_AWARDED_SIDES, 0);

			final int lumberBountyAwardedBase = unitType.getFieldAsInteger(LUMBER_BOUNTY_AWARDED_BASE, 0);
			final int lumberBountyAwardedDice = unitType.getFieldAsInteger(LUMBER_BOUNTY_AWARDED_DICE, 0);
			final int lumberBountyAwardedSides = unitType.getFieldAsInteger(LUMBER_BOUNTY_AWARDED_SIDES, 0);

			final boolean revivesHeroes = unitType.getFieldAsBoolean(REVIVES_HEROES, 0);

			final List<War3ID> unitsTrained = parseIDList(unitType.getFieldAsList(UNITS_TRAINED));

			final List<War3ID> upgradesTo = parseIDList(unitType.getFieldAsList(UPGRADES_TO));

			final List<War3ID> researchesAvailable = parseIDList(unitType.getFieldAsList(RESEARCHES_AVAILABLE));

			final List<War3ID> upgradesUsed = parseIDList(unitType.getFieldAsList(UPGRADES_USED));
			final EnumMap<CUpgradeClass, War3ID> upgradeClassToType = new EnumMap<>(CUpgradeClass.class);
			for (final War3ID upgradeUsed : upgradesUsed) {
				final CUpgradeType upgradeType = this.upgradeData.getType(upgradeUsed);
				if (upgradeType != null) {
					final CUpgradeClass upgradeClass = upgradeType.getUpgradeClass();
					if (upgradeClass != null) {
						upgradeClassToType.put(upgradeClass, upgradeUsed);
					}
				}
			}

			final List<War3ID> structuresBuilt = parseIDList(unitType.getFieldAsList(STRUCTURES_BUILT));

			final List<War3ID> itemsSold = parseIDList(unitType.getFieldAsList(ITEMS_SOLD));
			final List<War3ID> itemsMade = parseIDList(unitType.getFieldAsList(ITEMS_MADE));

			final War3ID defaultAutocastAbilityId;
			if ((defaultAutocastAbility != null) && !defaultAutocastAbility.isEmpty()
					&& !defaultAutocastAbility.equals("_")) {
				defaultAutocastAbilityId = War3ID.fromString(defaultAutocastAbility);
			}
			else {
				defaultAutocastAbilityId = null;
			}
			final List<War3ID> heroAbilityList = parseIDList(heroAbilityListString);
			final List<War3ID> abilityList = parseIDList(abilityListString);

			final List<String> requirementsString = unitType.getFieldAsList(REQUIRES);
			final List<String> requirementsLevelsString = unitType.getFieldAsList(REQUIRES_AMOUNT);
			final List<CUnitTypeRequirement> requirements = parseRequirements(requirementsString,
					requirementsLevelsString);
			final int requirementsTiersCount = unitType.getFieldAsInteger(REQUIRES_TIER_COUNT, 0);
			final List<List<CUnitTypeRequirement>> requirementTiers = new ArrayList<>();
			for (int i = 1; i <= requirementsTiersCount; i++) {
				final List<String> requirementsTierString = unitType.getFieldAsList(REQUIRES_TIER_X[i - 1]);
				final List<CUnitTypeRequirement> tierRequirements = parseRequirements(requirementsTierString,
						Collections.emptyList());
				requirementTiers.add(tierRequirements);
			}

			final EnumSet<CBuildingPathingType> preventedPathingTypes = CBuildingPathingType
					.parsePathingTypeListSet(unitType.getFieldAsString(PREVENT_PLACE, 0));
			final EnumSet<CBuildingPathingType> requiredPathingTypes = CBuildingPathingType
					.parsePathingTypeListSet(unitType.getFieldAsString(REQUIRE_PLACE, 0));

			final String raceString = unitType.getFieldAsString(UNIT_RACE, 0);
			final CUnitRace unitRace = CUnitRace.parseRace(raceString);

			final boolean hero = Character.isUpperCase(typeId.charAt(0));

			final List<String> heroProperNames = Arrays.asList(properNames.split(","));

			final boolean neutralBuildingShowMinimapIcon = unitType.getFieldAsBoolean(NEUTRAL_BUILDING_SHOW_ICON, 0);

			unitTypeInstance = new CUnitType(unitName, legacyName, typeId, life, lifeRegen, manaRegen, lifeRegenType,
					manaInitial, manaMaximum, speed, defense, defaultAutocastAbilityId, abilityList, isBldg,
					movementType, moveHeight, collisionSize, classifications, attacks, attacksEnabled, armorType, raise,
					decay, defenseType, impactZ, buildingPathingPixelMap, deathTime, targetedAs, acquisitionRange,
					minimumAttackRange, structuresBuilt, unitsTrained, researchesAvailable, upgradesUsed,
					upgradeClassToType, upgradesTo, itemsSold, itemsMade, unitRace, goldCost, lumberCost, foodUsed,
					foodMade, buildTime, preventedPathingTypes, requiredPathingTypes, propWindow, turnRate,
					requirements, requirementTiers, unitLevel, hero, strength, strPlus, agility, agiPlus, intelligence,
					intPlus, primaryAttribute, heroAbilityList, heroProperNames, properNamesCount, canFlee, priority,
					revivesHeroes, pointValue, castBackswingPoint, castPoint, canBeBuiltOnThem, canBuildOnMe,
					defenseUpgradeBonus, sightRadiusDay, sightRadiusNight, extendedLineOfSight, goldBountyAwardedBase,
					goldBountyAwardedDice, goldBountyAwardedSides, lumberBountyAwardedBase, lumberBountyAwardedDice,
					lumberBountyAwardedSides, neutralBuildingShowMinimapIcon);
			this.unitIdToUnitType.put(typeId, unitTypeInstance);
			this.jassLegacyNameToUnitId.put(legacyName, typeId);
		}
		return unitTypeInstance;
	}

	public static List<CUnitAttack> getEnabledAttacks(final List<CUnitAttack> attacks, final int attacksEnabled) {
		final List<CUnitAttack> enabledAttacks = new ArrayList<>();
		if ((attacksEnabled & 0x1) != 0) {
			if (attacks.size() > 0) {
				enabledAttacks.add(attacks.get(0));
			}
		}
		if ((attacksEnabled & 0x2) != 0) {
			if (attacks.size() > 1) {
				enabledAttacks.add(attacks.get(1));
			}
		}
		return enabledAttacks;
	}

	public static List<War3ID> parseIDList(final List<String> structuresBuiltString) {
		final List<War3ID> structuresBuilt = new ArrayList<>();
		for (final String structuresBuiltStringItem : structuresBuiltString) {
			if (structuresBuiltStringItem.length() == 4) {
				structuresBuilt.add(War3ID.fromString(structuresBuiltStringItem));
			}
		}
		return structuresBuilt;
	}

	public static Set<War3ID> parseIDSet(final List<String> structuresBuiltString) {
		final Set<War3ID> structuresBuilt = new HashSet<>();
		for (final String structuresBuiltStringItem : structuresBuiltString) {
			if (structuresBuiltStringItem.length() == 4) {
				structuresBuilt.add(War3ID.fromString(structuresBuiltStringItem));
			}
		}
		return structuresBuilt;
	}

	public static List<CUnitTypeRequirement> parseRequirements(final List<String> requirementsString,
			final List<String> requirementsLevelsString) {
		final List<CUnitTypeRequirement> requirements = new ArrayList<>();
		for (int i = 0; i < requirementsString.size(); i++) {
			final String item = requirementsString.get(i);
			if (!item.isEmpty() && (item.length() == 4)) {
				int level;
				if (i < requirementsLevelsString.size()) {
					if (requirementsLevelsString.get(i).isEmpty()) {
						level = 1;
					}
					else {
						try {
							level = Integer.parseInt(requirementsLevelsString.get(i));
						}
						catch (final NumberFormatException exc) {
							level = 1;
						}
					}
				}
				else if (requirementsLevelsString.size() > 0) {
					final String requirementLevel = requirementsLevelsString.get(requirementsLevelsString.size() - 1);
					if (requirementLevel.isEmpty()) {
						level = 1;
					}
					else {
						try {
							level = Integer.parseInt(requirementLevel);
						}
						catch (final NumberFormatException exc) {
							level = 1;
						}
					}
				}
				else {
					level = 1;
				}
				requirements.add(new CUnitTypeRequirement(War3ID.fromString(item), level));
			}
		}
		return requirements;
	}

	private String getLegacyName(final GameObject unitType) {
		return unitType.getLegacyName();
	}

	private static int[] populateHeroStatTable(final int maxHeroLevel, final float statPerLevel) {
		final int[] table = new int[maxHeroLevel];
		float sumBonusAtLevel = 0f;
		for (int i = 0; i < table.length; i++) {
			final float newSumBonusAtLevel = sumBonusAtLevel + statPerLevel;
			if (i == 0) {
				table[i] = (int) newSumBonusAtLevel;
			}
			else {
				table[i] = (int) newSumBonusAtLevel - table[i - 1];
			}
			sumBonusAtLevel = newSumBonusAtLevel;
		}
		return table;
	}

	private CUnitAttack createAttack(final float animationBackswingPoint, final float animationDamagePoint,
			final int areaOfEffectFullDamage, final int areaOfEffectMediumDamage, final int areaOfEffectSmallDamage,
			final EnumSet<CTargetType> areaOfEffectTargets, final CAttackType attackType, final float cooldownTime,
			final int damageBase, final float damageFactorMedium, final float damageFactorSmall,
			final float damageLossFactor, final int damageDice, final int damageSidesPerDie,
			final float damageSpillDistance, final float damageSpillRadius, final int damageUpgradeAmount,
			final int maximumNumberOfTargets, final float projectileArc, final String projectileArt,
			final boolean projectileHomingEnabled, final int projectileSpeed, final int range,
			final float rangeMotionBuffer, final boolean showUI, final EnumSet<CTargetType> targetsAllowed,
			final String weaponSound, final CWeaponType weaponType) {
		final CUnitAttack attack;
		switch (weaponType) {
		case MISSILE:
			attack = new CUnitAttackMissile(animationBackswingPoint, animationDamagePoint, attackType, cooldownTime,
					damageBase, damageDice, damageSidesPerDie, damageUpgradeAmount, range, rangeMotionBuffer, showUI,
					targetsAllowed, weaponSound, weaponType, projectileArc, projectileArt, projectileHomingEnabled,
					projectileSpeed);
			break;
		case MBOUNCE:
			attack = new CUnitAttackMissileBounce(animationBackswingPoint, animationDamagePoint, attackType,
					cooldownTime, damageBase, damageDice, damageSidesPerDie, damageUpgradeAmount, range,
					rangeMotionBuffer, showUI, targetsAllowed, weaponSound, weaponType, projectileArc, projectileArt,
					projectileHomingEnabled, projectileSpeed, damageLossFactor, maximumNumberOfTargets,
					areaOfEffectFullDamage, areaOfEffectTargets);
			break;
		case MSPLASH:
		case ARTILLERY:
			attack = new CUnitAttackMissileSplash(animationBackswingPoint, animationDamagePoint, attackType,
					cooldownTime, damageBase, damageDice, damageSidesPerDie, damageUpgradeAmount, range,
					rangeMotionBuffer, showUI, targetsAllowed, weaponSound, weaponType, projectileArc, projectileArt,
					projectileHomingEnabled, projectileSpeed, areaOfEffectFullDamage, areaOfEffectMediumDamage,
					areaOfEffectSmallDamage, areaOfEffectTargets, damageFactorMedium, damageFactorSmall);
			break;
		case MLINE:
		case ALINE:
			attack = new CUnitAttackMissileLine(animationBackswingPoint, animationDamagePoint, attackType, cooldownTime,
					damageBase, damageDice, damageSidesPerDie, damageUpgradeAmount, range, rangeMotionBuffer, showUI,
					targetsAllowed, weaponSound, weaponType, projectileArc, projectileArt, projectileHomingEnabled,
					projectileSpeed, damageSpillDistance, damageSpillRadius);
			break;
		case INSTANT:
			attack = new CUnitAttackInstant(animationBackswingPoint, animationDamagePoint, attackType, cooldownTime,
					damageBase, damageDice, damageSidesPerDie, damageUpgradeAmount, range, rangeMotionBuffer, showUI,
					targetsAllowed, weaponSound, weaponType, projectileArt);
			break;
		default:
		case NORMAL:
			attack = new CUnitAttackNormal(animationBackswingPoint, animationDamagePoint, attackType, cooldownTime,
					damageBase, damageDice, damageSidesPerDie, damageUpgradeAmount, range, rangeMotionBuffer, showUI,
					targetsAllowed, weaponSound, weaponType);
			break;
		}
		return attack;
	}

	public float getPropulsionWindow(final War3ID unitTypeId) {
		return this.unitData.get(unitTypeId.asStringValue()).getFieldAsFloat(PROPULSION_WINDOW, 0);
	}

	public float getTurnRate(final War3ID unitTypeId) {
		return this.unitData.get(unitTypeId.asStringValue()).getFieldAsFloat(TURN_RATE, 0);
	}

	public boolean isBuilding(final War3ID unitTypeId) {
		return this.unitData.get(unitTypeId.asStringValue()).getFieldAsBoolean(IS_BLDG, 0);
	}

	public String getName(final War3ID unitTypeId) {
		return this.unitData.get(unitTypeId.asStringValue()).getName();
	}

	public int getA1MinDamage(final War3ID unitTypeId) {
		return this.unitData.get(unitTypeId.asStringValue()).getFieldAsInteger(ATTACK1_DMG_BASE, 0)
				+ this.unitData.get(unitTypeId.asStringValue()).getFieldAsInteger(ATTACK1_DMG_DICE, 0);
	}

	public int getA1MaxDamage(final War3ID unitTypeId) {
		return this.unitData.get(unitTypeId.asStringValue()).getFieldAsInteger(ATTACK1_DMG_BASE, 0)
				+ (this.unitData.get(unitTypeId.asStringValue()).getFieldAsInteger(ATTACK1_DMG_DICE, 0) * this.unitData
						.get(unitTypeId.asStringValue()).getFieldAsInteger(ATTACK1_DMG_SIDES_PER_DIE, 0));
	}

	public int getA2MinDamage(final War3ID unitTypeId) {
		return this.unitData.get(unitTypeId.asStringValue()).getFieldAsInteger(ATTACK2_DMG_BASE, 0)
				+ this.unitData.get(unitTypeId.asStringValue()).getFieldAsInteger(ATTACK2_DMG_DICE, 0);
	}

	public int getA2MaxDamage(final War3ID unitTypeId) {
		return this.unitData.get(unitTypeId.asStringValue()).getFieldAsInteger(ATTACK2_DMG_BASE, 0)
				+ (this.unitData.get(unitTypeId.asStringValue()).getFieldAsInteger(ATTACK2_DMG_DICE, 0) * this.unitData
						.get(unitTypeId.asStringValue()).getFieldAsInteger(ATTACK2_DMG_SIDES_PER_DIE, 0));
	}

	public int getDefense(final War3ID unitTypeId) {
		return this.unitData.get(unitTypeId.asStringValue()).getFieldAsInteger(DEFENSE, 0);
	}

	public int getA1ProjectileSpeed(final War3ID unitTypeId) {
		return this.unitData.get(unitTypeId.asStringValue()).getFieldAsInteger(ATTACK1_PROJECTILE_SPEED, 0);
	}

	public float getA1ProjectileArc(final War3ID unitTypeId) {
		return this.unitData.get(unitTypeId.asStringValue()).getFieldAsFloat(ATTACK1_PROJECTILE_ARC, 0);
	}

	public int getA2ProjectileSpeed(final War3ID unitTypeId) {
		return this.unitData.get(unitTypeId.asStringValue()).getFieldAsInteger(ATTACK2_PROJECTILE_SPEED, 0);
	}

	public float getA2ProjectileArc(final War3ID unitTypeId) {
		return this.unitData.get(unitTypeId.asStringValue()).getFieldAsFloat(ATTACK2_PROJECTILE_ARC, 0);
	}

	public String getA1MissileArt(final War3ID unitTypeId) {
		return this.unitData.get(unitTypeId.asStringValue()).getFieldAsString(ATTACK1_MISSILE_ART, 0);
	}

	public String getA2MissileArt(final War3ID unitTypeId) {
		return this.unitData.get(unitTypeId.asStringValue()).getFieldAsString(ATTACK2_MISSILE_ART, 0);
	}

	public float getA1Cooldown(final War3ID unitTypeId) {
		return this.unitData.get(unitTypeId.asStringValue()).getFieldAsFloat(ATTACK1_COOLDOWN, 0);
	}

	public float getA2Cooldown(final War3ID unitTypeId) {
		return this.unitData.get(unitTypeId.asStringValue()).getFieldAsFloat(ATTACK2_COOLDOWN, 0);
	}

	public float getProjectileLaunchX(final War3ID unitTypeId) {
		return this.unitData.get(unitTypeId.asStringValue()).getFieldAsFloat(PROJECTILE_LAUNCH_X, 0);
	}

	public float getProjectileLaunchY(final War3ID unitTypeId) {
		return this.unitData.get(unitTypeId.asStringValue()).getFieldAsFloat(PROJECTILE_LAUNCH_Y, 0);
	}

	public float getProjectileLaunchZ(final War3ID unitTypeId) {
		return this.unitData.get(unitTypeId.asStringValue()).getFieldAsFloat(PROJECTILE_LAUNCH_Z, 0);
	}

	public CUnitType getUnitType(final War3ID rawcode) {
		final CUnitType unitTypeInstance = this.unitIdToUnitType.get(rawcode);
		if (unitTypeInstance != null) {
			return unitTypeInstance;
		}
		final GameObject unitType = this.unitData.get(rawcode.asStringValue());
		if (unitType == null) {
			return null;
		}
		final BufferedImage buildingPathingPixelMap = this.simulationRenderController
				.getBuildingPathingPixelMap(rawcode);
		return getUnitTypeInstance(rawcode, buildingPathingPixelMap, unitType);
	}

	public CUnitType getUnitTypeByJassLegacyName(final String jassLegacyName) {
		final War3ID typeId = this.jassLegacyNameToUnitId.get(jassLegacyName);
		if (typeId == null) {
			// VERY inefficient, but this is a crazy system anyway, they should not be using
			// this!
			System.err.println(
					"We are doing a highly inefficient lookup for a non-cached unit type based on its legacy string ID that I am pretty sure is not used by modding community: "
							+ jassLegacyName);
			for (final String key : this.unitData.keySet()) {
				final GameObject gameObject = this.unitData.get(key);
				if (jassLegacyName.equals(getLegacyName(gameObject).toLowerCase())) {
					return getUnitType(War3ID.fromString(gameObject.getId()));
				}
			}
		}
		return getUnitType(typeId);
	}
}
