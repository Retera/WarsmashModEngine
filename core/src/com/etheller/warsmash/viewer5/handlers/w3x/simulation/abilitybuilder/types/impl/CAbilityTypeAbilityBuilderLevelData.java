package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.types.impl;

import java.util.EnumSet;
import java.util.List;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitTypeRequirement;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityTypeLevelData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;

public class CAbilityTypeAbilityBuilderLevelData extends CAbilityTypeLevelData {
	private final float area;
	private final float castRange;
	private final float castTime;
	private final float cooldown;
	private final float durationHero;
	private final float durationNormal;
	private final List<War3ID> buffs;
	private final List<War3ID> effects;
	private final int manaCost;
	private final List<String> data;
	private final War3ID unitId;
	private List<CUnitTypeRequirement> requirements;

	public CAbilityTypeAbilityBuilderLevelData(EnumSet<CTargetType> targetsAllowed, float area, float castRange,
			float castTime, float cooldown, float durationHero, float durationNormal, List<War3ID> buffs,
			List<War3ID> effects, int manaCost, List<String> data, War3ID unitId, List<CUnitTypeRequirement> requirements) {
		super(targetsAllowed);
		this.area = area;
		this.castRange = castRange;
		this.castTime = castTime;
		this.cooldown = cooldown;
		this.durationHero = durationHero;
		this.durationNormal = durationNormal;
		this.buffs = buffs;
		this.effects = effects;
		this.manaCost = manaCost;
		this.data = data;
		this.unitId = unitId;
		this.requirements = requirements;
	}

	public float getArea() {
		return area;
	}

	public float getCastRange() {
		return castRange;
	}

	public float getCastTime() {
		return castTime;
	}

	public float getCooldown() {
		return cooldown;
	}

	public float getDurationHero() {
		return durationHero;
	}

	public float getDurationNormal() {
		return durationNormal;
	}

	public List<War3ID> getBuffs() {
		return buffs;
	}

	public List<War3ID> getEffects() {
		return effects;
	}

	public int getManaCost() {
		return manaCost;
	}

	public List<String> getData() {
		return data;
	}

	public War3ID getUnitId() {
		return unitId;
	}

	/**
	 * @return the requirements
	 */
	public List<CUnitTypeRequirement> getRequirements() {
		return requirements;
	}

	/**
	 * @param requirements the requirements to set
	 */
	public void setRequirements(List<CUnitTypeRequirement> requirements) {
		this.requirements = requirements;
	}

}
