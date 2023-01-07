package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl;

import java.util.EnumSet;
import java.util.Set;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityTypeLevelData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;

public class CAbilityTypeLoadLevelData extends CAbilityTypeLevelData {
	private final float castRange;
	private final Set<War3ID> allowedUnitTypes;

	public CAbilityTypeLoadLevelData(final EnumSet<CTargetType> targetsAllowed, final float castRange,
			final Set<War3ID> allowedUnitTypes) {
		super(targetsAllowed);
		this.castRange = castRange;
		this.allowedUnitTypes = allowedUnitTypes;
	}

	public float getCastRange() {
		return this.castRange;
	}

	public Set<War3ID> getAllowedUnitTypes() {
		return this.allowedUnitTypes;
	}
}
