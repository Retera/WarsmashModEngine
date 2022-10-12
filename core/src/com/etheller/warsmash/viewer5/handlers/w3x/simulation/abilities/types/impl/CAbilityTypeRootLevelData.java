package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityTypeLevelData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CDefenseType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;

import java.util.EnumSet;

public class CAbilityTypeRootLevelData extends CAbilityTypeLevelData {
	private int rootedWeaponsAttackBits;
	private int uprootedWeaponsAttackBits;
	private boolean rootedTurning;
	private CDefenseType uprootedDefenseType;
	private float duration;
	private float offDuration;
	

	public CAbilityTypeRootLevelData(EnumSet<CTargetType> targetsAllowed, int rootedWeaponsAttackBits,
			int uprootedWeaponsAttackBits, boolean rootedTurning, CDefenseType uprootedDefenseType, float duration,
			float offDuration) {
		super(targetsAllowed);
		this.rootedWeaponsAttackBits = rootedWeaponsAttackBits;
		this.uprootedWeaponsAttackBits = uprootedWeaponsAttackBits;
		this.rootedTurning = rootedTurning;
		this.uprootedDefenseType = uprootedDefenseType;
		this.duration = duration;
		this.offDuration = offDuration;
	}

	public int getRootedWeaponsAttackBits() {
		return rootedWeaponsAttackBits;
	}

	public int getUprootedWeaponsAttackBits() {
		return uprootedWeaponsAttackBits;
	}

	public boolean isRootedTurning() {
		return rootedTurning;
	}

	public CDefenseType getUprootedDefenseType() {
		return uprootedDefenseType;
	}

	public float getDuration() {
		return duration;
	}

	public float getOffDuration() {
		return offDuration;
	}
	
}
