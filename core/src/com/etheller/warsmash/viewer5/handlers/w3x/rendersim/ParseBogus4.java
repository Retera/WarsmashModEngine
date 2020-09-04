package com.etheller.warsmash.viewer5.handlers.w3x.rendersim;

import java.util.Arrays;

public class ParseBogus4 {

	public static void main(final String[] args) {

		final OrientationInterpolation[] interpolations = OrientationInterpolation.values();
		final float[] velocities = new float[interpolations.length];
		final float[] signs = new float[interpolations.length];
		Arrays.fill(signs, -1);
		signs[1] = signs[6] = 1;
		final float[] angles = new float[interpolations.length];
		Arrays.fill(angles, (float) ((3 * Math.PI) / 2));
		final boolean[] spun = new boolean[interpolations.length];

		for (int j = 0; j < 140; j++) {
			final StringBuilder sb = new StringBuilder();
			for (int i = 0; i < interpolations.length; i++) {
				float simulationFacing = 90;
				if (simulationFacing < 0) {
					simulationFacing += 360;
				}
				float renderFacing = (float) Math.toDegrees(angles[i]);
				if (renderFacing < 0) {
					renderFacing += 360;
				}
				float facingDelta = simulationFacing - renderFacing;
				if (facingDelta < -180) {
					facingDelta = 360 + facingDelta;
				}
				if (facingDelta > 180) {
					facingDelta = -360 + facingDelta;
				}
				final float absoluteFacingDelta = Math.abs(facingDelta);

				final float absoluteFacingDeltaRadians = (float) Math.toRadians(absoluteFacingDelta);
				float acceleration;
				final boolean endPhase = (absoluteFacingDeltaRadians <= interpolations[i].getEndingAccelCutoff())
						&& ((velocities[i] * signs[i]) > 0);
				if (endPhase) {
					acceleration = -interpolations[i].getEndingNegativeAcceleration() * signs[i];
					if (((velocities[i] + acceleration) * signs[i]) <= 0.001) {
						velocities[i] = absoluteFacingDeltaRadians * signs[i] * 0.25f;
					}
					else {
						velocities[i] = velocities[i] + acceleration;
					}
				}
				else {
					acceleration = interpolations[i].getStartingAcceleration() * signs[i];
					velocities[i] = velocities[i] + acceleration;
				}
				if ((velocities[i] * signs[i]) > interpolations[i].getMaxVelocity()) {
					velocities[i] = interpolations[i].getMaxVelocity() * signs[i];
				}

				float angleToAdd = (float) (/* Math.signum(facingDelta) **/ Math
						.toDegrees(velocities[i]) /* * deltaTime */);
				if (absoluteFacingDelta < Math.abs(angleToAdd)) {
					angleToAdd = facingDelta;
				}
				double newDegreesAngle = (((Math.toDegrees(angles[i]) + angleToAdd) % 360) + 360) % 360;
				if (newDegreesAngle > 280) {
					spun[i] = true;
				}
				else if ((newDegreesAngle < 100) && spun[i]) {
					newDegreesAngle += 360;
				}
				angles[i] = (float) Math.toRadians(newDegreesAngle);
				if (sb.length() > 0) {
					sb.append(',');
				}
				sb.append(angles[i]);
			}
			System.out.println(sb.toString());
		}
	}
}
