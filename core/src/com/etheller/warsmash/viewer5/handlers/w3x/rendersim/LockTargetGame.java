package com.etheller.warsmash.viewer5.handlers.w3x.rendersim;

import com.badlogic.gdx.math.Quaternion;
import com.etheller.warsmash.viewer5.GenericNode;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;

public class LockTargetGame implements LockTarget {
	private final AbilityTarget target;

	public LockTargetGame(AbilityTarget target) {
		this.target = target;
	}

	@Override
	public float getX() {
		return this.target.getX();
	}

	@Override
	public float getY() {
		return this.target.getY();
	}

	@Override
	public float getZ() {
		return 0;
	}

	@Override
	public void apply(GenericNode turretBone, Quaternion overrideWorldRotation) {
		final float ang = (float) Math.atan2(this.target.getY() - turretBone.worldLocation.y,
				this.target.getX() - turretBone.worldLocation.x);
		overrideWorldRotation.setFromAxisRad(0, 0, 1, ang);
	}
}
