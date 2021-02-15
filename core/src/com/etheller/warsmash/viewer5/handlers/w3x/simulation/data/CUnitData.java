package com.etheller.warsmash.viewer5.handlers.w3x.simulation.data;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.etheller.warsmash.units.manager.MutableObjectData;
import com.etheller.warsmash.units.manager.MutableObjectData.MutableGameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.environment.PathingGrid;
import com.etheller.warsmash.viewer5.handlers.w3x.environment.PathingGrid.RemovablePathingMapInstance;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitClassification;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitTypeRequirement;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.HandleIdAllocator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityAttack;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityMove;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.build.CAbilityHumanBuild;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.build.CAbilityNagaBuild;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.build.CAbilityNeutralBuild;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.build.CAbilityNightElfBuild;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.build.CAbilityOrcBuild;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.build.CAbilityUndeadBuild;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.queue.CAbilityQueue;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.queue.CAbilityRally;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CAttackType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CDefenseType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CWeaponType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttack;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttackInstant;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttackMissile;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttackMissileBounce;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttackMissileLine;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttackMissileSplash;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttackNormal;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.pathing.CBuildingPathingType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.SimulationRenderController;

public class CUnitData {
	private static final War3ID MANA_INITIAL_AMOUNT = War3ID.fromString("umpi");
	private static final War3ID MANA_MAXIMUM = War3ID.fromString("umpm");
	private static final War3ID HIT_POINT_MAXIMUM = War3ID.fromString("uhpm");
	private static final War3ID MOVEMENT_SPEED_BASE = War3ID.fromString("umvs");
	private static final War3ID PROPULSION_WINDOW = War3ID.fromString("uprw");
	private static final War3ID TURN_RATE = War3ID.fromString("umvr");
	private static final War3ID IS_BLDG = War3ID.fromString("ubdg");
	private static final War3ID NAME = War3ID.fromString("unam");
	private static final War3ID PROJECTILE_LAUNCH_X = War3ID.fromString("ulpx");
	private static final War3ID PROJECTILE_LAUNCH_Y = War3ID.fromString("ulpy");
	private static final War3ID PROJECTILE_LAUNCH_Z = War3ID.fromString("ulpz");
	private static final War3ID ATTACKS_ENABLED = War3ID.fromString("uaen");
	private static final War3ID ATTACK1_BACKSWING_POINT = War3ID.fromString("ubs1");
	private static final War3ID ATTACK1_DAMAGE_POINT = War3ID.fromString("udp1");
	private static final War3ID ATTACK1_AREA_OF_EFFECT_FULL_DMG = War3ID.fromString("ua1f");
	private static final War3ID ATTACK1_AREA_OF_EFFECT_HALF_DMG = War3ID.fromString("ua1h");
	private static final War3ID ATTACK1_AREA_OF_EFFECT_QUARTER_DMG = War3ID.fromString("ua1q");
	private static final War3ID ATTACK1_AREA_OF_EFFECT_TARGETS = War3ID.fromString("ua1p");
	private static final War3ID ATTACK1_ATTACK_TYPE = War3ID.fromString("ua1t");
	private static final War3ID ATTACK1_COOLDOWN = War3ID.fromString("ua1c");
	private static final War3ID ATTACK1_DMG_BASE = War3ID.fromString("ua1b");
	private static final War3ID ATTACK1_DAMAGE_FACTOR_HALF = War3ID.fromString("uhd1");
	private static final War3ID ATTACK1_DAMAGE_FACTOR_QUARTER = War3ID.fromString("uqd1");
	private static final War3ID ATTACK1_DAMAGE_LOSS_FACTOR = War3ID.fromString("udl1");
	private static final War3ID ATTACK1_DMG_DICE = War3ID.fromString("ua1d");
	private static final War3ID ATTACK1_DMG_SIDES_PER_DIE = War3ID.fromString("ua1s");
	private static final War3ID ATTACK1_DMG_SPILL_DIST = War3ID.fromString("usd1");
	private static final War3ID ATTACK1_DMG_SPILL_RADIUS = War3ID.fromString("usr1");
	private static final War3ID ATTACK1_DMG_UPGRADE_AMT = War3ID.fromString("udu1");
	private static final War3ID ATTACK1_TARGET_COUNT = War3ID.fromString("utc1");
	private static final War3ID ATTACK1_PROJECTILE_ARC = War3ID.fromString("uma1");
	private static final War3ID ATTACK1_MISSILE_ART = War3ID.fromString("ua1m");
	private static final War3ID ATTACK1_PROJECTILE_HOMING_ENABLED = War3ID.fromString("umh1");
	private static final War3ID ATTACK1_PROJECTILE_SPEED = War3ID.fromString("ua1z");
	private static final War3ID ATTACK1_RANGE = War3ID.fromString("ua1r");
	private static final War3ID ATTACK1_RANGE_MOTION_BUFFER = War3ID.fromString("urb1");
	private static final War3ID ATTACK1_SHOW_UI = War3ID.fromString("uwu1");
	private static final War3ID ATTACK1_TARGETS_ALLOWED = War3ID.fromString("ua1g");
	private static final War3ID ATTACK1_WEAPON_SOUND = War3ID.fromString("ucs1");
	private static final War3ID ATTACK1_WEAPON_TYPE = War3ID.fromString("ua1w");

	private static final War3ID ATTACK2_BACKSWING_POINT = War3ID.fromString("ubs2");
	private static final War3ID ATTACK2_DAMAGE_POINT = War3ID.fromString("udp2");
	private static final War3ID ATTACK2_AREA_OF_EFFECT_FULL_DMG = War3ID.fromString("ua2f");
	private static final War3ID ATTACK2_AREA_OF_EFFECT_HALF_DMG = War3ID.fromString("ua2h");
	private static final War3ID ATTACK2_AREA_OF_EFFECT_QUARTER_DMG = War3ID.fromString("ua2q");
	private static final War3ID ATTACK2_AREA_OF_EFFECT_TARGETS = War3ID.fromString("ua2p");
	private static final War3ID ATTACK2_ATTACK_TYPE = War3ID.fromString("ua2t");
	private static final War3ID ATTACK2_COOLDOWN = War3ID.fromString("ua2c");
	private static final War3ID ATTACK2_DMG_BASE = War3ID.fromString("ua2b");
	private static final War3ID ATTACK2_DAMAGE_FACTOR_HALF = War3ID.fromString("uhd2");
	private static final War3ID ATTACK2_DAMAGE_FACTOR_QUARTER = War3ID.fromString("uqd2");
	private static final War3ID ATTACK2_DAMAGE_LOSS_FACTOR = War3ID.fromString("udl2");
	private static final War3ID ATTACK2_DMG_DICE = War3ID.fromString("ua2d");
	private static final War3ID ATTACK2_DMG_SIDES_PER_DIE = War3ID.fromString("ua2s");
	private static final War3ID ATTACK2_DMG_SPILL_DIST = War3ID.fromString("usd2");
	private static final War3ID ATTACK2_DMG_SPILL_RADIUS = War3ID.fromString("usr2");
	private static final War3ID ATTACK2_DMG_UPGRADE_AMT = War3ID.fromString("udu2");
	private static final War3ID ATTACK2_TARGET_COUNT = War3ID.fromString("utc2");
	private static final War3ID ATTACK2_PROJECTILE_ARC = War3ID.fromString("uma2");
	private static final War3ID ATTACK2_MISSILE_ART = War3ID.fromString("ua2m");
	private static final War3ID ATTACK2_PROJECTILE_HOMING_ENABLED = War3ID.fromString("umh2");
	private static final War3ID ATTACK2_PROJECTILE_SPEED = War3ID.fromString("ua2z");
	private static final War3ID ATTACK2_RANGE = War3ID.fromString("ua2r");
	private static final War3ID ATTACK2_RANGE_MOTION_BUFFER = War3ID.fromString("urb2");
	private static final War3ID ATTACK2_SHOW_UI = War3ID.fromString("uwu2");
	private static final War3ID ATTACK2_TARGETS_ALLOWED = War3ID.fromString("ua2g");
	private static final War3ID ATTACK2_WEAPON_SOUND = War3ID.fromString("ucs2");
	private static final War3ID ATTACK2_WEAPON_TYPE = War3ID.fromString("ua2w");

	private static final War3ID ACQUISITION_RANGE = War3ID.fromString("uacq");
	private static final War3ID MINIMUM_ATTACK_RANGE = War3ID.fromString("uamn");

	private static final War3ID PROJECTILE_IMPACT_Z = War3ID.fromString("uimz");

	private static final War3ID DEATH_TYPE = War3ID.fromString("udea");
	private static final War3ID ARMOR_TYPE = War3ID.fromString("uarm");

	private static final War3ID DEFENSE = War3ID.fromString("udef");
	private static final War3ID DEFENSE_TYPE = War3ID.fromString("udty");
	private static final War3ID MOVE_HEIGHT = War3ID.fromString("umvh");
	private static final War3ID MOVE_TYPE = War3ID.fromString("umvt");
	private static final War3ID COLLISION_SIZE = War3ID.fromString("ucol");
	private static final War3ID CLASSIFICATION = War3ID.fromString("utyp");
	private static final War3ID DEATH_TIME = War3ID.fromString("udtm");
	private static final War3ID TARGETED_AS = War3ID.fromString("utar");

	private static final War3ID ABILITIES_NORMAL = War3ID.fromString("uabi");

	private static final War3ID STRUCTURES_BUILT = War3ID.fromString("ubui");
	private static final War3ID UNITS_TRAINED = War3ID.fromString("utra");
	private static final War3ID RESEARCHES_AVAILABLE = War3ID.fromString("ures");
	private static final War3ID UNIT_RACE = War3ID.fromString("urac");

	private static final War3ID REQUIRES = War3ID.fromString("ureq");
	private static final War3ID REQUIRES_AMOUNT = War3ID.fromString("urqc");

	private static final War3ID GOLD_COST = War3ID.fromString("ugol");
	private static final War3ID LUMBER_COST = War3ID.fromString("ulum");
	private static final War3ID BUILD_TIME = War3ID.fromString("ubld");
	private static final War3ID FOOD_USED = War3ID.fromString("ufoo");
	private static final War3ID FOOD_MADE = War3ID.fromString("ufma");

	private static final War3ID REQUIRE_PLACE = War3ID.fromString("upar");
	private static final War3ID PREVENT_PLACE = War3ID.fromString("upap");

	private final MutableObjectData unitData;
	private final Map<War3ID, CUnitType> unitIdToUnitType = new HashMap<>();
	private final CAbilityData abilityData;
	private final SimulationRenderController simulationRenderController;

	public CUnitData(final MutableObjectData unitData, final CAbilityData abilityData,
			final SimulationRenderController simulationRenderController) {
		this.unitData = unitData;
		this.abilityData = abilityData;
		this.simulationRenderController = simulationRenderController;
	}

	public CUnit create(final CSimulation simulation, final int playerIndex, final War3ID typeId, final float x,
			final float y, final float facing, final BufferedImage buildingPathingPixelMap,
			final HandleIdAllocator handleIdAllocator, final RemovablePathingMapInstance pathingInstance) {
		final MutableGameObject unitType = this.unitData.get(typeId);
		final int handleId = handleIdAllocator.createId();

		final CUnitType unitTypeInstance = getUnitTypeInstance(typeId, buildingPathingPixelMap, unitType);
		final int life = unitTypeInstance.getLife();
		final int manaInitial = unitTypeInstance.getManaInitial();
		final int manaMaximum = unitTypeInstance.getManaMaximum();
		final int speed = unitTypeInstance.getSpeed();
		final int defense = unitTypeInstance.getDefense();

		final CUnit unit = new CUnit(handleId, playerIndex, x, y, life, typeId, facing, manaInitial, life, manaMaximum,
				speed, defense, unitTypeInstance, pathingInstance);
		if (speed > 0) {
			unit.add(simulation, new CAbilityMove(handleIdAllocator.createId()));
		}
		if (!unitTypeInstance.getAttacks().isEmpty()) {
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
				unit.add(simulation, new CAbilityNeutralBuild(handleIdAllocator.createId(), structuresBuilt));
				break;
			}
		}
		final List<War3ID> unitsTrained = unitTypeInstance.getUnitsTrained();
		final List<War3ID> researchesAvailable = unitTypeInstance.getResearchesAvailable();
		if (!unitsTrained.isEmpty() || !researchesAvailable.isEmpty()) {
			unit.add(simulation, new CAbilityQueue(handleIdAllocator.createId(), unitsTrained, researchesAvailable));
		}
		if (!unitsTrained.isEmpty()) {
			unit.add(simulation, new CAbilityRally(handleIdAllocator.createId()));
		}
		for (final String ability : unitTypeInstance.getAbilityList().split(",")) {
			if ((ability.length() > 0) && !"_".equals(ability)) {
				final CAbility createAbility = this.abilityData.createAbility(ability, handleIdAllocator.createId());
				if (createAbility != null) {
					unit.add(simulation, createAbility);
				}
			}
		}
		return unit;
	}

	private CUnitType getUnitTypeInstance(final War3ID typeId, final BufferedImage buildingPathingPixelMap,
			final MutableGameObject unitType) {
		CUnitType unitTypeInstance = this.unitIdToUnitType.get(typeId);
		if (unitTypeInstance == null) {
			final int life = unitType.getFieldAsInteger(HIT_POINT_MAXIMUM, 0);
			final int manaInitial = unitType.getFieldAsInteger(MANA_INITIAL_AMOUNT, 0);
			final int manaMaximum = unitType.getFieldAsInteger(MANA_MAXIMUM, 0);
			final int speed = unitType.getFieldAsInteger(MOVEMENT_SPEED_BASE, 0);
			final int defense = unitType.getFieldAsInteger(DEFENSE, 0);
			final String abilityList = unitType.getFieldAsString(ABILITIES_NORMAL, 0);

			final float moveHeight = unitType.getFieldAsFloat(MOVE_HEIGHT, 0);
			final String movetp = unitType.getFieldAsString(MOVE_TYPE, 0);
			final float collisionSize = unitType.getFieldAsFloat(COLLISION_SIZE, 0);
			final float propWindow = unitType.getFieldAsFloat(PROPULSION_WINDOW, 0);
			final float turnRate = unitType.getFieldAsFloat(TURN_RATE, 0);

			final boolean isBldg = unitType.getFieldAsBoolean(IS_BLDG, 0);
			final PathingGrid.MovementType movementType = PathingGrid.getMovementType(movetp);
			final String unitName = unitType.getFieldAsString(NAME, 0);
			final float acquisitionRange = unitType.getFieldAsFloat(ACQUISITION_RANGE, 0);
			final float minimumAttackRange = unitType.getFieldAsFloat(MINIMUM_ATTACK_RANGE, 0);
			final EnumSet<CTargetType> targetedAs = CTargetType
					.parseTargetTypeSet(unitType.getFieldAsString(TARGETED_AS, 0));
			final String classificationString = unitType.getFieldAsString(CLASSIFICATION, 0);
			final EnumSet<CUnitClassification> classifications = EnumSet.noneOf(CUnitClassification.class);
			if (classificationString != null) {
				final String[] classificationValues = classificationString.split(",");
				for (final String unitEditorKey : classificationValues) {
					final CUnitClassification unitClassification = CUnitClassification
							.parseUnitClassification(unitEditorKey);
					if (unitClassification != null) {
						classifications.add(unitClassification);
					}
				}
			}
			final List<CUnitAttack> attacks = new ArrayList<>();
			final int attacksEnabled = unitType.getFieldAsInteger(ATTACKS_ENABLED, 0);
			if ((attacksEnabled & 0x1) != 0) {
				try {
					// attack one
					final float animationBackswingPoint = unitType.getFieldAsFloat(ATTACK1_BACKSWING_POINT, 0);
					final float animationDamagePoint = unitType.getFieldAsFloat(ATTACK1_DAMAGE_POINT, 0);
					final int areaOfEffectFullDamage = unitType.getFieldAsInteger(ATTACK1_AREA_OF_EFFECT_FULL_DMG, 0);
					final int areaOfEffectMediumDamage = unitType.getFieldAsInteger(ATTACK1_AREA_OF_EFFECT_HALF_DMG, 0);
					final int areaOfEffectSmallDamage = unitType.getFieldAsInteger(ATTACK1_AREA_OF_EFFECT_QUARTER_DMG,
							0);
					final EnumSet<CTargetType> areaOfEffectTargets = CTargetType
							.parseTargetTypeSet(unitType.getFieldAsString(ATTACK1_AREA_OF_EFFECT_TARGETS, 0));
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
					final boolean projectileHomingEnabled = unitType
							.getFieldAsBoolean(ATTACK1_PROJECTILE_HOMING_ENABLED, 0);
					final int projectileSpeed = unitType.getFieldAsInteger(ATTACK1_PROJECTILE_SPEED, 0);
					final int range = unitType.getFieldAsInteger(ATTACK1_RANGE, 0);
					final float rangeMotionBuffer = unitType.getFieldAsFloat(ATTACK1_RANGE_MOTION_BUFFER, 0);
					final boolean showUI = unitType.getFieldAsBoolean(ATTACK1_SHOW_UI, 0);
					final EnumSet<CTargetType> targetsAllowed = CTargetType
							.parseTargetTypeSet(unitType.getFieldAsString(ATTACK1_TARGETS_ALLOWED, 0));
					final String weaponSound = unitType.getFieldAsString(ATTACK1_WEAPON_SOUND, 0);
					final CWeaponType weaponType = CWeaponType
							.parseWeaponType(unitType.getFieldAsString(ATTACK1_WEAPON_TYPE, 0));
					attacks.add(createAttack(animationBackswingPoint, animationDamagePoint, areaOfEffectFullDamage,
							areaOfEffectMediumDamage, areaOfEffectSmallDamage, areaOfEffectTargets, attackType,
							cooldownTime, damageBase, damageFactorMedium, damageFactorSmall, damageLossFactor,
							damageDice, damageSidesPerDie, damageSpillDistance, damageSpillRadius, damageUpgradeAmount,
							maximumNumberOfTargets, projectileArc, projectileArt, projectileHomingEnabled,
							projectileSpeed, range, rangeMotionBuffer, showUI, targetsAllowed, weaponSound,
							weaponType));
				}
				catch (final Exception exc) {
					System.err.println("Attack 1 failed to parse with: " + exc.getClass() + ":" + exc.getMessage());
				}
			}
			if ((attacksEnabled & 0x2) != 0) {
				try {
					// attack two
					final float animationBackswingPoint = unitType.getFieldAsFloat(ATTACK2_BACKSWING_POINT, 0);
					final float animationDamagePoint = unitType.getFieldAsFloat(ATTACK2_DAMAGE_POINT, 0);
					final int areaOfEffectFullDamage = unitType.getFieldAsInteger(ATTACK2_AREA_OF_EFFECT_FULL_DMG, 0);
					final int areaOfEffectMediumDamage = unitType.getFieldAsInteger(ATTACK2_AREA_OF_EFFECT_HALF_DMG, 0);
					final int areaOfEffectSmallDamage = unitType.getFieldAsInteger(ATTACK2_AREA_OF_EFFECT_QUARTER_DMG,
							0);
					final EnumSet<CTargetType> areaOfEffectTargets = CTargetType
							.parseTargetTypeSet(unitType.getFieldAsString(ATTACK2_AREA_OF_EFFECT_TARGETS, 0));
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
					final boolean projectileHomingEnabled = unitType
							.getFieldAsBoolean(ATTACK2_PROJECTILE_HOMING_ENABLED, 0);
					final int range = unitType.getFieldAsInteger(ATTACK2_RANGE, 0);
					final float rangeMotionBuffer = unitType.getFieldAsFloat(ATTACK2_RANGE_MOTION_BUFFER, 0);
					boolean showUI = unitType.getFieldAsBoolean(ATTACK2_SHOW_UI, 0);
					final EnumSet<CTargetType> targetsAllowed = CTargetType
							.parseTargetTypeSet(unitType.getFieldAsString(ATTACK2_TARGETS_ALLOWED, 0));
					final String weaponSound = unitType.getFieldAsString(ATTACK2_WEAPON_SOUND, 0);
					final CWeaponType weaponType = CWeaponType
							.parseWeaponType(unitType.getFieldAsString(ATTACK2_WEAPON_TYPE, 0));
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
							cooldownTime, damageBase, damageFactorMedium, damageFactorSmall, damageLossFactor,
							damageDice, damageSidesPerDie, damageSpillDistance, damageSpillRadius, damageUpgradeAmount,
							maximumNumberOfTargets, projectileArc, projectileArt, projectileHomingEnabled,
							projectileSpeed, range, rangeMotionBuffer, showUI, targetsAllowed, weaponSound,
							weaponType));
				}
				catch (final Exception exc) {
					System.err.println("Attack 2 failed to parse with: " + exc.getClass() + ":" + exc.getMessage());
				}
			}
			final int deathType = unitType.getFieldAsInteger(DEATH_TYPE, 0);
			final boolean raise = (deathType & 0x1) != 0;
			final boolean decay = (deathType & 0x2) != 0;
			final String armorType = unitType.getFieldAsString(ARMOR_TYPE, 0);
			final float impactZ = unitType.getFieldAsFloat(PROJECTILE_IMPACT_Z, 0);
			final CDefenseType defenseType = CDefenseType.parseDefenseType(unitType.getFieldAsString(DEFENSE_TYPE, 0));
			final float deathTime = unitType.getFieldAsFloat(DEATH_TIME, 0);
			final int goldCost = unitType.getFieldAsInteger(GOLD_COST, 0);
			final int lumberCost = unitType.getFieldAsInteger(LUMBER_COST, 0);
			final int buildTime = unitType.getFieldAsInteger(BUILD_TIME, 0);
			final int foodUsed = unitType.getFieldAsInteger(FOOD_USED, 0);
			final int foodMade = unitType.getFieldAsInteger(FOOD_MADE, 0);

			final String unitsTrainedString = unitType.getFieldAsString(UNITS_TRAINED, 0);
			final String[] unitsTrainedStringItems = unitsTrainedString.trim().split(",");
			final List<War3ID> unitsTrained = new ArrayList<>();
			for (final String unitsTrainedStringItem : unitsTrainedStringItems) {
				if (unitsTrainedStringItem.length() == 4) {
					unitsTrained.add(War3ID.fromString(unitsTrainedStringItem));
				}
			}

			final String researchesAvailableString = unitType.getFieldAsString(RESEARCHES_AVAILABLE, 0);
			final String[] researchesAvailableStringItems = researchesAvailableString.trim().split(",");
			final List<War3ID> researchesAvailable = new ArrayList<>();
			for (final String researchesAvailableStringItem : researchesAvailableStringItems) {
				if (researchesAvailableStringItem.length() == 4) {
					researchesAvailable.add(War3ID.fromString(researchesAvailableStringItem));
				}
			}

			final String structuresBuiltString = unitType.getFieldAsString(STRUCTURES_BUILT, 0);
			final String[] structuresBuiltStringItems = structuresBuiltString.split(",");
			final List<War3ID> structuresBuilt = new ArrayList<>();
			for (final String structuresBuiltStringItem : structuresBuiltStringItems) {
				if (structuresBuiltStringItem.length() == 4) {
					structuresBuilt.add(War3ID.fromString(structuresBuiltStringItem));
				}
			}

			final String requirementsString = unitType.getFieldAsString(REQUIRES, 0);
			final String requirementsLevelsString = unitType.getFieldAsString(REQUIRES_AMOUNT, 0);
			final String[] requirementsStringItems = requirementsString.split(",");
			final String[] requirementsLevelsStringItems = requirementsLevelsString.split(",");
			final List<CUnitTypeRequirement> requirements = new ArrayList<>();
			for (int i = 0; i < requirementsStringItems.length; i++) {
				final String item = requirementsStringItems[i];
				if (!item.isEmpty()) {
					int level;
					if (i < requirementsLevelsStringItems.length) {
						if (requirementsLevelsStringItems[i].isEmpty()) {
							level = 1;
						}
						else {
							level = Integer.parseInt(requirementsLevelsStringItems[i]);
						}
					}
					else if (requirementsLevelsStringItems.length > 0) {
						final String requirementLevel = requirementsLevelsStringItems[requirementsLevelsStringItems.length
								- 1];
						if (requirementLevel.isEmpty()) {
							level = 1;
						}
						else {
							level = Integer.parseInt(requirementLevel);
						}
					}
					else {
						level = 1;
					}
					requirements.add(new CUnitTypeRequirement(War3ID.fromString(item), level));
				}
			}

			final EnumSet<CBuildingPathingType> preventedPathingTypes = CBuildingPathingType
					.parsePathingTypeListSet(unitType.getFieldAsString(PREVENT_PLACE, 0));
			final EnumSet<CBuildingPathingType> requiredPathingTypes = CBuildingPathingType
					.parsePathingTypeListSet(unitType.getFieldAsString(REQUIRE_PLACE, 0));

			final String raceString = unitType.getFieldAsString(UNIT_RACE, 0);
			final CUnitRace unitRace = CUnitRace.parseRace(raceString);

			unitTypeInstance = new CUnitType(unitName, life, manaInitial, manaMaximum, speed, defense, abilityList,
					isBldg, movementType, moveHeight, collisionSize, classifications, attacks, armorType, raise, decay,
					defenseType, impactZ, buildingPathingPixelMap, deathTime, targetedAs, acquisitionRange,
					minimumAttackRange, structuresBuilt, unitsTrained, researchesAvailable, unitRace, goldCost,
					lumberCost, foodUsed, foodMade, buildTime, preventedPathingTypes, requiredPathingTypes, propWindow,
					turnRate, requirements);
			this.unitIdToUnitType.put(typeId, unitTypeInstance);
		}
		return unitTypeInstance;
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
		return this.unitData.get(unitTypeId).getFieldAsFloat(PROPULSION_WINDOW, 0);
	}

	public float getTurnRate(final War3ID unitTypeId) {
		return this.unitData.get(unitTypeId).getFieldAsFloat(TURN_RATE, 0);
	}

	public boolean isBuilding(final War3ID unitTypeId) {
		return this.unitData.get(unitTypeId).getFieldAsBoolean(IS_BLDG, 0);
	}

	public String getName(final War3ID unitTypeId) {
		return this.unitData.get(unitTypeId).getName();
	}

	public int getA1MinDamage(final War3ID unitTypeId) {
		return this.unitData.get(unitTypeId).getFieldAsInteger(ATTACK1_DMG_BASE, 0)
				+ this.unitData.get(unitTypeId).getFieldAsInteger(ATTACK1_DMG_DICE, 0);
	}

	public int getA1MaxDamage(final War3ID unitTypeId) {
		return this.unitData.get(unitTypeId).getFieldAsInteger(ATTACK1_DMG_BASE, 0)
				+ (this.unitData.get(unitTypeId).getFieldAsInteger(ATTACK1_DMG_DICE, 0)
						* this.unitData.get(unitTypeId).getFieldAsInteger(ATTACK1_DMG_SIDES_PER_DIE, 0));
	}

	public int getA2MinDamage(final War3ID unitTypeId) {
		return this.unitData.get(unitTypeId).getFieldAsInteger(ATTACK2_DMG_BASE, 0)
				+ this.unitData.get(unitTypeId).getFieldAsInteger(ATTACK2_DMG_DICE, 0);
	}

	public int getA2MaxDamage(final War3ID unitTypeId) {
		return this.unitData.get(unitTypeId).getFieldAsInteger(ATTACK2_DMG_BASE, 0)
				+ (this.unitData.get(unitTypeId).getFieldAsInteger(ATTACK2_DMG_DICE, 0)
						* this.unitData.get(unitTypeId).getFieldAsInteger(ATTACK2_DMG_SIDES_PER_DIE, 0));
	}

	public int getDefense(final War3ID unitTypeId) {
		return this.unitData.get(unitTypeId).getFieldAsInteger(DEFENSE, 0);
	}

	public int getA1ProjectileSpeed(final War3ID unitTypeId) {
		return this.unitData.get(unitTypeId).getFieldAsInteger(ATTACK1_PROJECTILE_SPEED, 0);
	}

	public float getA1ProjectileArc(final War3ID unitTypeId) {
		return this.unitData.get(unitTypeId).getFieldAsFloat(ATTACK1_PROJECTILE_ARC, 0);
	}

	public int getA2ProjectileSpeed(final War3ID unitTypeId) {
		return this.unitData.get(unitTypeId).getFieldAsInteger(ATTACK2_PROJECTILE_SPEED, 0);
	}

	public float getA2ProjectileArc(final War3ID unitTypeId) {
		return this.unitData.get(unitTypeId).getFieldAsFloat(ATTACK2_PROJECTILE_ARC, 0);
	}

	public String getA1MissileArt(final War3ID unitTypeId) {
		return this.unitData.get(unitTypeId).getFieldAsString(ATTACK1_MISSILE_ART, 0);
	}

	public String getA2MissileArt(final War3ID unitTypeId) {
		return this.unitData.get(unitTypeId).getFieldAsString(ATTACK2_MISSILE_ART, 0);
	}

	public float getA1Cooldown(final War3ID unitTypeId) {
		return this.unitData.get(unitTypeId).getFieldAsFloat(ATTACK1_COOLDOWN, 0);
	}

	public float getA2Cooldown(final War3ID unitTypeId) {
		return this.unitData.get(unitTypeId).getFieldAsFloat(ATTACK2_COOLDOWN, 0);
	}

	public float getProjectileLaunchX(final War3ID unitTypeId) {
		return this.unitData.get(unitTypeId).getFieldAsFloat(PROJECTILE_LAUNCH_X, 0);
	}

	public float getProjectileLaunchY(final War3ID unitTypeId) {
		return this.unitData.get(unitTypeId).getFieldAsFloat(PROJECTILE_LAUNCH_Y, 0);
	}

	public float getProjectileLaunchZ(final War3ID unitTypeId) {
		return this.unitData.get(unitTypeId).getFieldAsFloat(PROJECTILE_LAUNCH_Z, 0);
	}

	public CUnitType getUnitType(final War3ID rawcode) {
		final CUnitType unitTypeInstance = this.unitIdToUnitType.get(rawcode);
		if (unitTypeInstance != null) {
			return unitTypeInstance;
		}
		final MutableGameObject unitType = this.unitData.get(rawcode);
		if (unitType == null) {
			return null;
		}
		final BufferedImage buildingPathingPixelMap = this.simulationRenderController
				.getBuildingPathingPixelMap(rawcode);
		return getUnitTypeInstance(rawcode, buildingPathingPixelMap, unitType);
	}
}
