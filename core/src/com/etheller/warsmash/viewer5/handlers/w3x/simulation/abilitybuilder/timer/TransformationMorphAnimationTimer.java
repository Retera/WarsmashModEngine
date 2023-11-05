package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.timer;

import java.util.EnumSet;

import com.etheller.warsmash.viewer5.handlers.w3x.SequenceUtils;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.PrimaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.SecondaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.timers.CTimer;

public class TransformationMorphAnimationTimer extends CTimer {
	private CUnit unit;
	private boolean addAlternateTagAfter;

	public TransformationMorphAnimationTimer(CSimulation game, CUnit unit, boolean addAlternateTagAfter, float delay) {
		super();
		this.unit = unit;
		this.addAlternateTagAfter = addAlternateTagAfter;
		this.setRepeats(false);
		this.setTimeoutTime(delay);
	}

	public void onFire(CSimulation game) {
		if (addAlternateTagAfter) {
			unit.getUnitAnimationListener().removeSecondaryTagForFutureAnimations(SecondaryTag.ALTERNATE);
			unit.getUnitAnimationListener().playAnimation(false, PrimaryTag.MORPH, SequenceUtils.EMPTY, 1.0f, true);
		} else {
			unit.getUnitAnimationListener().playAnimation(false, PrimaryTag.MORPH, EnumSet.of(SecondaryTag.ALTERNATE),
					1.0f, true);
		}
		this.unit.getUnitAnimationListener().queueAnimation(PrimaryTag.STAND,
				this.addAlternateTagAfter ? EnumSet.of(SecondaryTag.ALTERNATE) : SequenceUtils.EMPTY, true);
		if (this.addAlternateTagAfter) {
			this.unit.getUnitAnimationListener().addSecondaryTagForFutureAnimations(SecondaryTag.ALTERNATE);
		} else {
			this.unit.getUnitAnimationListener().removeSecondaryTagForFutureAnimations(SecondaryTag.ALTERNATE);
		}
	}

}
