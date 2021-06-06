package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl;

import java.util.EnumSet;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityTypeLevelData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;

public class CAbilityTypeCoupleInstantLevelData extends CAbilityTypeLevelData {
	private final War3ID resultingUnitTypeId;
	private final War3ID partnerUnitTypeId;
	private final boolean moveToPartner;
	private final float castRange;
	private final float area;

	public CAbilityTypeCoupleInstantLevelData(final EnumSet<CTargetType> targetsAllowed,
			final War3ID resultingUnitTypeId, final War3ID partnerUnitTypeId, final boolean moveToPartner,
			final float castRange, final float area) {
		super(targetsAllowed);
		this.resultingUnitTypeId = resultingUnitTypeId;
		this.partnerUnitTypeId = partnerUnitTypeId;
		this.moveToPartner = moveToPartner;
		this.castRange = castRange;
		this.area = area;
	}

	public War3ID getResultingUnitTypeId() {
		return this.resultingUnitTypeId;
	}

	public War3ID getPartnerUnitTypeId() {
		return this.partnerUnitTypeId;
	}

	public boolean isMoveToPartner() {
		return this.moveToPartner;
	}

	public float getCastRange() {
		return this.castRange;
	}

	public float getArea() {
		return this.area;
	}

}
