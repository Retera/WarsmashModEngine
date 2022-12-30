package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl;

import java.util.EnumSet;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityTypeLevelData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;

public class CAbilityTypeNeutralBuildingLevelData extends CAbilityTypeLevelData {
	private final float activationRadius;
	private final int interactionType;
	private final boolean showSelectUnitButton;
	private final boolean showUnitIndicator;

	public CAbilityTypeNeutralBuildingLevelData(final EnumSet<CTargetType> targetsAllowed, final float activationRadius,
			final int interactionType, final boolean showSelectUnitButton, final boolean showUnitIndicator) {
		super(targetsAllowed);
		this.activationRadius = activationRadius;
		this.interactionType = interactionType;
		this.showSelectUnitButton = showSelectUnitButton;
		this.showUnitIndicator = showUnitIndicator;
	}

	public float getActivationRadius() {
		return activationRadius;
	}

	public int getInteractionType() {
		return interactionType;
	}

	public boolean isShowSelectUnitButton() {
		return showSelectUnitButton;
	}

	public boolean isShowUnitIndicator() {
		return showUnitIndicator;
	}

}
