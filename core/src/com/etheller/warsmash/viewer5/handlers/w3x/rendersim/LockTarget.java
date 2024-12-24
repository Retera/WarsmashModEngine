package com.etheller.warsmash.viewer5.handlers.w3x.rendersim;

import com.badlogic.gdx.math.Quaternion;
import com.etheller.warsmash.viewer5.GenericNode;

public interface LockTarget {
	float getX();

	float getY();

	float getZ();

	void apply(GenericNode sourceNode, Quaternion overrideWorldRotation);
}
