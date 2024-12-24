package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.cargohold;

import java.util.EnumSet;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.SecondaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;

public class CAbilityCargoHoldEntangledMine extends CAbilityCargoHold {

	public CAbilityCargoHoldEntangledMine(final int handleId, final War3ID code, final War3ID alias,
			final int cargoCapacity, final float duration, final float castRange,
			final EnumSet<CTargetType> targetsAllowed) {
		super(handleId, code, alias, cargoCapacity, duration, castRange, targetsAllowed);
	}

	@Override
	public void addUnit(final CUnit cargoHoldUnit, final CUnit target) {
		final SecondaryTag tagBefore = SecondaryTag.fromCount(getCargoCount());
		super.addUnit(cargoHoldUnit, target);
		final SecondaryTag tagAfter = SecondaryTag.fromCount(getCargoCount());
		updateTags(cargoHoldUnit, tagBefore, tagAfter);
	}

	@Override
	public CUnit removeUnitAtIndex(final CUnit cargoHoldUnit, final int index) {
		final SecondaryTag tagBefore = SecondaryTag.fromCount(getCargoCount());
		final CUnit removedUnit = super.removeUnitAtIndex(cargoHoldUnit, index);
		final SecondaryTag tagAfter = SecondaryTag.fromCount(getCargoCount());
		updateTags(cargoHoldUnit, tagBefore, tagAfter);
		return removedUnit;
	}

	public void updateTags(final CUnit cargoHoldUnit, final SecondaryTag tagBefore, final SecondaryTag tagAfter) {
		boolean tagsChanged = false;
		if (tagBefore != null) {
			if (cargoHoldUnit.getUnitAnimationListener().removeSecondaryTag(tagBefore)) {
				tagsChanged = true;
			}
		}
		if (tagAfter != null) {
			if (cargoHoldUnit.getUnitAnimationListener().addSecondaryTag(tagAfter)) {
				tagsChanged = true;
			}
		}
		if (tagsChanged) {
			cargoHoldUnit.getUnitAnimationListener().forceResetCurrentAnimation();
		}
	}
}
