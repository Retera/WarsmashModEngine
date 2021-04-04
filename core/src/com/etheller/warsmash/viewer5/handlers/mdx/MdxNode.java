package com.etheller.warsmash.viewer5.handlers.mdx;

import com.badlogic.gdx.math.Quaternion;
import com.etheller.warsmash.viewer5.Scene;
import com.etheller.warsmash.viewer5.SkeletalNode;

public class MdxNode extends SkeletalNode {
	private static final Quaternion HALF_PI_X = new Quaternion().setFromAxisRad(1, 0, 0, (float) (-Math.PI / 2));
	private static final Quaternion HALF_PI_Y = new Quaternion().setFromAxisRad(0, 1, 0, (float) (-Math.PI / 2));

	@Override
	protected void convertBasis(final Quaternion computedRotation) {
		computedRotation.mul(HALF_PI_Y);
		computedRotation.mul(HALF_PI_X);
	}

	@Override
	protected void update(final float dt, final Scene scene) {

	}

}
