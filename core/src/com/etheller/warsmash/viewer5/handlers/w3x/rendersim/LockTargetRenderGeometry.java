package com.etheller.warsmash.viewer5.handlers.w3x.rendersim;

import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.etheller.warsmash.viewer5.GenericNode;

public class LockTargetRenderGeometry implements LockTarget {
	private final GenericNode node;
	private final Vector3 offset;

	public LockTargetRenderGeometry(GenericNode node, Vector3 offset) {
		this.node = node;
		this.offset = offset;
	}

	@Override
	public float getX() {
		return this.node.worldLocation.x + this.offset.x;
	}

	@Override
	public float getY() {
		return this.node.worldLocation.y + this.offset.y;
	}

	@Override
	public float getZ() {
		return this.node.worldLocation.z + this.offset.z;
	}

	@Override
	public void apply(GenericNode turretBone, Quaternion overrideWorldRotation) {
		// NOTE: there's probably a better/simpler way to calculate this
		final float dx = getX() - turretBone.worldLocation.x;
		final float dy = getY() - turretBone.worldLocation.y;
		final float dz = getZ() - turretBone.worldLocation.z;
		final float ang = (float) Math.atan2(dy, dx);
		final double groundDistance = Math.sqrt((dx * dx) + (dy * dy));
		final float angZ = (float) Math.atan2(dz, groundDistance);
		overrideWorldRotation.setFromAxisRad(0, 0, 1, ang);
		final float x = overrideWorldRotation.x;
		final float y = overrideWorldRotation.y;
		final float z = overrideWorldRotation.z;
		final float w = overrideWorldRotation.w;
		overrideWorldRotation.setFromAxisRad(0, 1, 0, angZ);
		overrideWorldRotation.mulLeft(x, y, z, w);
	}

}
