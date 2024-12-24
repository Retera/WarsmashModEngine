package com.etheller.warsmash.viewer5.handlers.w3x.simulation;

import java.awt.image.BufferedImage;
import java.util.EnumSet;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;

public class CDestructableType {

	private final String name;
	private final float maxLife;
	private final EnumSet<CTargetType> targetedAs;
	private final String armorType;
	private final int buildTime;
	private final float occlusionHeight;
	private final BufferedImage pathingPixelMap;
	private final BufferedImage pathingDeathPixelMap;

	private final int goldRepairCost;
	private final int lumberRepairCost;
	private final int repairTime;

	public CDestructableType(final String name, final float maxLife, final EnumSet<CTargetType> targetedAs,
			final String armorType, final int buildTime, final int goldRepairCost, final int lumberRepairCost,
			final int repairTime, final float occlusionHeight, final BufferedImage pathingPixelMap,
			final BufferedImage pathingDeathPixelMap) {
		this.name = name;
		this.maxLife = maxLife;
		this.targetedAs = targetedAs;
		this.armorType = armorType;
		this.buildTime = buildTime;
		this.occlusionHeight = occlusionHeight;
		this.pathingPixelMap = pathingPixelMap;
		this.pathingDeathPixelMap = pathingDeathPixelMap;
		this.goldRepairCost = goldRepairCost;
		this.lumberRepairCost = lumberRepairCost;
		this.repairTime = repairTime;
	}

	public String getName() {
		return this.name;
	}

	public float getMaxLife() {
		return this.maxLife;
	}

	public EnumSet<CTargetType> getTargetedAs() {
		return this.targetedAs;
	}

	public String getArmorType() {
		return this.armorType;
	}

	public int getBuildTime() {
		return this.buildTime;
	}

	public int getGoldRepairCost() {
		return goldRepairCost;
	}

	public int getLumberRepairCost() {
		return lumberRepairCost;
	}

	public int getRepairTime() {
		return repairTime;
	}

	public float getOcclusionHeight() {
		return occlusionHeight;
	}

	public BufferedImage getPathingPixelMap() {
		return this.pathingPixelMap;
	}

	public BufferedImage getPathingDeathPixelMap() {
		return this.pathingDeathPixelMap;
	}
}
