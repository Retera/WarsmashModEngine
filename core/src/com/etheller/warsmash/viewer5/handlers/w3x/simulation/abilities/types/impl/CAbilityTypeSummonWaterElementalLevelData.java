package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl;

import java.util.EnumSet;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;

public class CAbilityTypeSummonWaterElementalLevelData extends CAbilitySpellBaseTypeLevelData {
	private War3ID summonUnitId;
	private int summonUnitCount;
	private War3ID buffId;
	private float duration;
	private float areaOfEffect;

	public CAbilityTypeSummonWaterElementalLevelData(EnumSet<CTargetType> targetsAllowed, int manaCost, float castRange,
			float cooldown, float castingTime, War3ID summonUnitId, int summonUnitCount, War3ID buffId, float duration,
			float areaOfEffect) {
		super(targetsAllowed, manaCost, castRange, cooldown, castingTime);
		this.summonUnitId = summonUnitId;
		this.summonUnitCount = summonUnitCount;
		this.buffId = buffId;
		this.duration = duration;
		this.areaOfEffect = areaOfEffect;
	}

	public War3ID getSummonUnitId() {
		return summonUnitId;
	}

	public void setSummonUnitId(War3ID summonUnitId) {
		this.summonUnitId = summonUnitId;
	}

	public int getSummonUnitCount() {
		return summonUnitCount;
	}

	public void setSummonUnitCount(int summonUnitCount) {
		this.summonUnitCount = summonUnitCount;
	}

	public War3ID getBuffId() {
		return buffId;
	}

	public void setBuffId(War3ID buffId) {
		this.buffId = buffId;
	}

	public float getDuration() {
		return duration;
	}

	public void setDuration(float duration) {
		this.duration = duration;
	}

	public float getAreaOfEffect() {
		return areaOfEffect;
	}

	public void setAreaOfEffect(float areaOfEffect) {
		this.areaOfEffect = areaOfEffect;
	}
}
