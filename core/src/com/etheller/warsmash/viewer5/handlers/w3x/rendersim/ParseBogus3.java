package com.etheller.warsmash.viewer5.handlers.w3x.rendersim;

public class ParseBogus3 {
	public static float endingAccelCutoff(final float maxVelocity, final float endingAccel) {
		final float endingAccelFinishingTime = maxVelocity / endingAccel;
		final float endingDistanceRequired = (maxVelocity * endingAccelFinishingTime)
				- ((endingAccel / 2) * (endingAccelFinishingTime * endingAccelFinishingTime));
		return endingDistanceRequired;
	}

	public static void main(final String[] args) {
		System.out.println(endingAccelCutoff(0.1f, 0.04f));

		for (final OrientationInterpolation oi : OrientationInterpolation.values()) {
			System.out.println(oi + ": " + oi.getStartingAcceleration() + "," + oi.getMaxVelocity() + ","
					+ oi.getEndingNegativeAcceleration() + "->" + oi.getEndingAccelCutoff());
		}
	}
}
