package com.etheller.warsmash.viewer5.handlers.w3x.simulation;

import java.awt.image.BufferedImage;
import java.util.EnumSet;
import java.util.List;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.environment.PathingGrid;
import com.etheller.warsmash.viewer5.handlers.w3x.environment.PathingGrid.MovementType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CDefenseType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttack;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.data.CUnitRace;

/**
 * The quick (symbol table instead of map) lookup for unit type values that we
 * probably cannot change per unit instance.
 */
public class CUnitType {
	private final String name;
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
	private final CUnitRace unitRace;
	private final int goldCost;
	private final int lumberCost;
	private final int buildTime;

	public CUnitType(final String name, final boolean isBldg, final MovementType movementType,
			final float defaultFlyingHeight, final float collisionSize,
			final EnumSet<CUnitClassification> classifications, final List<CUnitAttack> attacks, final String armorType,
			final boolean raise, final boolean decay, final CDefenseType defenseType, final float impactZ,
			final BufferedImage buildingPathingPixelMap, final float deathTime, final EnumSet<CTargetType> targetedAs,
			final float defaultAcquisitionRange, final float minimumAttackRange, final List<War3ID> structuresBuilt,
			final CUnitRace unitRace, final int goldCost, final int lumberCost, final int buildTime) {
		this.name = name;
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
		this.unitRace = unitRace;
		this.goldCost = goldCost;
		this.lumberCost = lumberCost;
		this.buildTime = buildTime;
	}

	public String getName() {
		return this.name;
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

	public CUnitRace getRace() {
		return this.unitRace;
	}

	public int getGoldCost() {
		return this.goldCost;
	}

	public int getLumberCost() {
		return this.lumberCost;
	}

	public int getBuildTime() {
		return this.buildTime;
	}
}
