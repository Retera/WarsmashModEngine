package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl;

import java.util.EnumSet;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityTypeLevelData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;

public class CAbilityTypeBagLevelData extends CAbilityTypeLevelData {

	private final int slotCount;

	public CAbilityTypeBagLevelData(final EnumSet<CTargetType> targetsAllowed, final int slotCount) {
		super(targetsAllowed);
		this.slotCount = slotCount;
	}

	public int getSlotCount() {
		return this.slotCount;
	}

}
