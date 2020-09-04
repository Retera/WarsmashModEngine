package com.etheller.warsmash.viewer5.handlers.w3x.rendersim;

/**
 * We observe this table during gameplay but I haven't found it anywhere in the
 * data yet. So, I'm making my own.
 */
public enum OrientationInterpolation {
	OI0(0.07f, 0.2f, 999f),
	OI1(0.03f, 0.1f, 0.04f),
	OI2(0.015f, 1.0f, 999),
	OI3(0.005f, 0.1f, 0.0043f),
	OI4(0.04f, 0.15f, 0.01f),
	OI5(0.05f, 0.18f, 0.015f),
	OI6(0.1f, 0.3f, 0.1f),
	OI7(0.003f, 0.08f, 0.0027f),
	OI8(0.001f, 0.05f, 0.001f);

	public static OrientationInterpolation[] VALUES = values();

	private float startingAcceleration;
	private float maxVelocity;
	private float endingNegativeAcceleration;
	private float endingAccelCutoff;
	private float startingAccelCutoff;

	private OrientationInterpolation(final float startingAcceleration, final float maxVelocity,
			final float endingNegativeAcceleration) {
		this.startingAcceleration = startingAcceleration;
		this.maxVelocity = maxVelocity;
		this.endingNegativeAcceleration = endingNegativeAcceleration;
		this.endingAccelCutoff = endingAccelCutoff(maxVelocity, endingNegativeAcceleration);
		this.startingAccelCutoff = endingAccelCutoff(maxVelocity, startingAcceleration);
	}

	public float getStartingAcceleration() {
		return this.startingAcceleration;
	}

	public float getMaxVelocity() {
		return this.maxVelocity;
	}

	public float getEndingNegativeAcceleration() {
		return this.endingNegativeAcceleration;
	}

	public float getEndingAccelCutoff() {
		return this.endingAccelCutoff;
	}

	public float getStartingAccelCutoff() {
		return this.startingAccelCutoff;
	}

	private static float endingAccelCutoff(final float maxVelocity, final float endingAccel) {
		final float endingAccelFinishingTime = maxVelocity / endingAccel;
		final float endingDistanceRequired = (maxVelocity * endingAccelFinishingTime)
				- ((endingAccel / 2) * (endingAccelFinishingTime * endingAccelFinishingTime));
		return endingDistanceRequired;
	}
}
