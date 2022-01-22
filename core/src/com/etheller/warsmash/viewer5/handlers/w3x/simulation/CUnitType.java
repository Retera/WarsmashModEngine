package com.etheller.warsmash.viewer5.handlers.w3x.simulation;

import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.environment.PathingGrid;
import com.etheller.warsmash.viewer5.handlers.w3x.environment.PathingGrid.MovementType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.hero.CPrimaryAttribute;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CDefenseType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CRegenType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttack;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.data.CUnitRace;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.pathing.CBuildingPathingType;

/**
 * The quick (symbol table instead of map) lookup for unit type values that we
 * probably cannot change per unit instance.
 */
public class CUnitType {
	private final String name;
	private final String legacyName;
	private final War3ID typeId;
	private final int maxLife;
	private final float lifeRegen;
	private final float manaRegen;
	private final CRegenType lifeRegenType;
	private final int manaInitial;
	private final int manaMaximum;
	private final int speed;
	private final int defense;
	private final String abilityList;
	private final boolean building;
	private final PathingGrid.MovementType movementType;
	private final float defaultFlyingHeight;
	private final float collisionSize;
	private final EnumSet<CUnitClassification> classifications;
	private final List<CUnitAttack> attacks;
	private final String armorType; // used for audio
	private final boolean raise;
	private final boolean decay;
	private final CDefenseType defenseType;
	private final float impactZ;
	private final float deathTime;

	// TODO: this should probably not be stored as game state, i.e., is it really
	// game data? can we store it in a cleaner way?
	private final BufferedImage buildingPathingPixelMap;
	private final EnumSet<CTargetType> targetedAs;
	private final float defaultAcquisitionRange;
	private final float minimumAttackRange;
	private final List<War3ID> structuresBuilt;
	private final List<War3ID> unitsTrained;
	private final List<War3ID> researchesAvailable;
	private final List<War3ID> upgradesTo;
	private final CUnitRace unitRace;
	private final int goldCost;
	private final int lumberCost;
	private final int foodUsed;
	private final int foodMade;
	private final int buildTime;
	private final EnumSet<CBuildingPathingType> preventedPathingTypes;
	private final EnumSet<CBuildingPathingType> requiredPathingTypes;
	private final float propWindow;
	private final float turnRate;
	private final List<CUnitTypeRequirement> requirements;
	private final int level;
	private final boolean hero;
	private final int startingStrength;
	private final float strengthPerLevel;
	private final int startingAgility;
	private final float agilityPerLevel;
	private final int startingIntelligence;
	private final float intelligencePerLevel;
	private final CPrimaryAttribute primaryAttribute;
	private final List<War3ID> heroAbilityList;
	private final List<String> heroProperNames;
	private final int properNamesCount;
	private final boolean canFlee;
	private final int priority;
	private final boolean revivesHeroes;
	private final int pointValue;
	private final List<List<CUnitTypeRequirement>> requirementTiers;
	private final float castBackswingPoint;
	private final float castPoint;
	private final boolean canBeBuiltOnThem;
	private final boolean canBuildOnMe;

	public CUnitType(final String name, final String legacyName, final War3ID typeId, final int maxLife,
			final float lifeRegen, final float manaRegen, final CRegenType lifeRegenType, final int manaInitial,
			final int manaMaximum, final int speed, final int defense, final String abilityList, final boolean isBldg,
			final MovementType movementType, final float defaultFlyingHeight, final float collisionSize,
			final EnumSet<CUnitClassification> classifications, final List<CUnitAttack> attacks, final String armorType,
			final boolean raise, final boolean decay, final CDefenseType defenseType, final float impactZ,
			final BufferedImage buildingPathingPixelMap, final float deathTime, final EnumSet<CTargetType> targetedAs,
			final float defaultAcquisitionRange, final float minimumAttackRange, final List<War3ID> structuresBuilt,
			final List<War3ID> unitsTrained, final List<War3ID> researchesAvailable, final List<War3ID> upgradesTo,
			final CUnitRace unitRace, final int goldCost, final int lumberCost, final int foodUsed, final int foodMade,
			final int buildTime, final EnumSet<CBuildingPathingType> preventedPathingTypes,
			final EnumSet<CBuildingPathingType> requiredPathingTypes, final float propWindow, final float turnRate,
			final List<CUnitTypeRequirement> requirements, final List<List<CUnitTypeRequirement>> requirementTiers,
			final int level, final boolean hero, final int strength, final float strengthPerLevel, final int agility,
			final float agilityPerLevel, final int intelligence, final float intelligencePerLevel,
			final CPrimaryAttribute primaryAttribute, final List<War3ID> heroAbilityList,
			final List<String> heroProperNames, final int properNamesCount, final boolean canFlee, final int priority,
			final boolean revivesHeroes, final int pointValue, final float castBackswingPoint, final float castPoint,
			final boolean canBeBuiltOnThem, final boolean canBuildOnMe) {
		this.name = name;
		this.legacyName = legacyName;
		this.typeId = typeId;
		this.maxLife = maxLife;
		this.lifeRegen = lifeRegen;
		this.manaRegen = manaRegen;
		this.lifeRegenType = lifeRegenType;
		this.manaInitial = manaInitial;
		this.manaMaximum = manaMaximum;
		this.speed = speed;
		this.defense = defense;
		this.abilityList = abilityList;
		this.building = isBldg;
		this.movementType = movementType;
		this.defaultFlyingHeight = defaultFlyingHeight;
		this.collisionSize = collisionSize;
		this.classifications = classifications;
		this.attacks = attacks;
		this.armorType = armorType;
		this.raise = raise;
		this.decay = decay;
		this.defenseType = defenseType;
		this.impactZ = impactZ;
		this.buildingPathingPixelMap = buildingPathingPixelMap;
		this.deathTime = deathTime;
		this.targetedAs = targetedAs;
		this.defaultAcquisitionRange = defaultAcquisitionRange;
		this.minimumAttackRange = minimumAttackRange;
		this.structuresBuilt = structuresBuilt;
		this.unitsTrained = unitsTrained;
		this.researchesAvailable = researchesAvailable;
		this.upgradesTo = upgradesTo;
		this.unitRace = unitRace;
		this.goldCost = goldCost;
		this.lumberCost = lumberCost;
		this.foodUsed = foodUsed;
		this.foodMade = foodMade;
		this.buildTime = buildTime;
		this.preventedPathingTypes = preventedPathingTypes;
		this.requiredPathingTypes = requiredPathingTypes;
		this.propWindow = propWindow;
		this.turnRate = turnRate;
		this.requirements = requirements;
		this.requirementTiers = requirementTiers;
		this.level = level;
		this.hero = hero;
		this.startingStrength = strength;
		this.strengthPerLevel = strengthPerLevel;
		this.startingAgility = agility;
		this.agilityPerLevel = agilityPerLevel;
		this.startingIntelligence = intelligence;
		this.intelligencePerLevel = intelligencePerLevel;
		this.primaryAttribute = primaryAttribute;
		this.heroAbilityList = heroAbilityList;
		this.heroProperNames = heroProperNames;
		this.properNamesCount = properNamesCount;
		this.canFlee = canFlee;
		this.priority = priority;
		this.revivesHeroes = revivesHeroes;
		this.pointValue = pointValue;
		this.castBackswingPoint = castBackswingPoint;
		this.castPoint = castPoint;
		this.canBeBuiltOnThem = canBeBuiltOnThem;
		this.canBuildOnMe = canBuildOnMe;
	}

	public String getName() {
		return this.name;
	}

	public String getLegacyName() {
		return this.legacyName;
	}

	public War3ID getTypeId() {
		return this.typeId;
	}

	public int getMaxLife() {
		return this.maxLife;
	}

	public float getLifeRegen() {
		return this.lifeRegen;
	}

	public float getManaRegen() {
		return this.manaRegen;
	}

	public CRegenType getLifeRegenType() {
		return this.lifeRegenType;
	}

	public int getManaInitial() {
		return this.manaInitial;
	}

	public int getManaMaximum() {
		return this.manaMaximum;
	}

	public int getSpeed() {
		return this.speed;
	}

	public int getDefense() {
		return this.defense;
	}

	public String getAbilityList() {
		return this.abilityList;
	}

	public float getDefaultFlyingHeight() {
		return this.defaultFlyingHeight;
	}

	public PathingGrid.MovementType getMovementType() {
		return this.movementType;
	}

	public float getCollisionSize() {
		return this.collisionSize;
	}

	public boolean isBuilding() {
		return this.building;
	}

	public EnumSet<CUnitClassification> getClassifications() {
		return this.classifications;
	}

	public List<CUnitAttack> getAttacks() {
		return this.attacks;
	}

	public boolean isRaise() {
		return this.raise;
	}

	public boolean isDecay() {
		return this.decay;
	}

	public String getArmorType() {
		return this.armorType;
	}

	public CDefenseType getDefenseType() {
		return this.defenseType;
	}

	public float getImpactZ() {
		return this.impactZ;
	}

	public BufferedImage getBuildingPathingPixelMap() {
		return this.buildingPathingPixelMap;
	}

	public float getDeathTime() {
		return this.deathTime;
	}

	public EnumSet<CTargetType> getTargetedAs() {
		return this.targetedAs;
	}

	public float getDefaultAcquisitionRange() {
		return this.defaultAcquisitionRange;
	}

	public float getMinimumAttackRange() {
		return this.minimumAttackRange;
	}

	public List<War3ID> getStructuresBuilt() {
		return this.structuresBuilt;
	}

	public List<War3ID> getUnitsTrained() {
		return this.unitsTrained;
	}

	public List<War3ID> getResearchesAvailable() {
		return this.researchesAvailable;
	}

	public List<War3ID> getUpgradesTo() {
		return this.upgradesTo;
	}

	public CUnitRace getRace() {
		return this.unitRace;
	}

	public int getGoldCost() {
		return this.goldCost;
	}

	public int getLumberCost() {
		return this.lumberCost;
	}

	public int getFoodUsed() {
		return this.foodUsed;
	}

	public int getFoodMade() {
		return this.foodMade;
	}

	public int getBuildTime() {
		return this.buildTime;
	}

	public EnumSet<CBuildingPathingType> getPreventedPathingTypes() {
		return this.preventedPathingTypes;
	}

	public EnumSet<CBuildingPathingType> getRequiredPathingTypes() {
		return this.requiredPathingTypes;
	}

	public float getPropWindow() {
		return this.propWindow;
	}

	public float getTurnRate() {
		return this.turnRate;
	}

	public List<CUnitTypeRequirement> getRequirements() {
		return this.requirements;
	}

	public List<CUnitTypeRequirement> getRequirementsTier(final int tier) {
		final int index = tier - 1;
		if ((index >= 0) && (index < this.requirementTiers.size())) {
			return this.requirementTiers.get(index);
		}
		else {
			return Collections.emptyList();
		}
	}

	public int getLevel() {
		return this.level;
	}

	public boolean isHero() {
		return this.hero;
	}

	public int getStartingStrength() {
		return this.startingStrength;
	}

	public float getStrengthPerLevel() {
		return this.strengthPerLevel;
	}

	public int getStartingAgility() {
		return this.startingAgility;
	}

	public float getAgilityPerLevel() {
		return this.agilityPerLevel;
	}

	public int getStartingIntelligence() {
		return this.startingIntelligence;
	}

	public float getIntelligencePerLevel() {
		return this.intelligencePerLevel;
	}

	public CPrimaryAttribute getPrimaryAttribute() {
		return this.primaryAttribute;
	}

	public List<War3ID> getHeroAbilityList() {
		return this.heroAbilityList;
	}

	public List<String> getHeroProperNames() {
		return this.heroProperNames;
	}

	public int getProperNamesCount() {
		return this.properNamesCount;
	}

	public boolean isCanFlee() {
		return this.canFlee;
	}

	public int getPriority() {
		return this.priority;
	}

	public boolean isRevivesHeroes() {
		return this.revivesHeroes;
	}

	public int getPointValue() {
		return this.pointValue;
	}

	public float getCastBackswingPoint() {
		return this.castBackswingPoint;
	}

	public float getCastPoint() {
		return this.castPoint;
	}

	// "isCan" - these method names are computer generated, cut me some slack
	public boolean isCanBeBuiltOnThem() {
		return this.canBeBuiltOnThem;
	}

	public boolean isCanBuildOnMe() {
		return this.canBuildOnMe;
	}
}
