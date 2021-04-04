package com.etheller.warsmash;

public class MathSpeedBenchmark {
	private static final int NUMBER_OF_ITERATIONS = 100000000;

	public static void main(final String[] args) {
		// Let us solve for Ground Distance two ways.

		long sumCosineTime = 0;
		long sumSquareRootTime = 0;
		final float[] thrallXs = new float[NUMBER_OF_ITERATIONS];
		final float[] thrallYs = new float[NUMBER_OF_ITERATIONS];
		final float[] murlocXs = new float[NUMBER_OF_ITERATIONS];
		final float[] murlocYs = new float[NUMBER_OF_ITERATIONS];
		for (int i = 0; i < NUMBER_OF_ITERATIONS; i++) {

			thrallXs[i] = getRandomFloat(-25000.0f, 25000.0f);
			thrallYs[i] = getRandomFloat(-25000.0f, 25000.0f);
			murlocXs[i] = getRandomFloat(-25000.0f, 25000.0f);
			murlocYs[i] = getRandomFloat(-25000.0f, 25000.0f);
		}
		final long clockTime1 = System.currentTimeMillis();
		for (int i = 0; i < NUMBER_OF_ITERATIONS; i++) {
			final float distance2 = groundDistanceSqrt(thrallXs[i], thrallYs[i], murlocXs[i], murlocYs[i]);
		}
		final long clockTime2 = System.currentTimeMillis();
		for (int i = 0; i < NUMBER_OF_ITERATIONS; i++) {
			final float distance1 = groundDistanceCos(thrallXs[i], thrallYs[i], murlocXs[i], murlocYs[i]);
		}
		final long clockTime3 = System.currentTimeMillis();
//		if (Math.abs(distance2 - distance1) > 0.1) {
//			System.out.println(thrallX + "," + thrallY);
//			System.out.println(murlocX + "," + murlocY);
//			System.err.println(distance1 + " != " + distance2);
//			throw new RuntimeException("You have failed to do mathematics.");
//		}
		sumCosineTime = clockTime2 - clockTime1;
		sumSquareRootTime = clockTime3 - clockTime2;
		System.out.println("Square Root: " + sumCosineTime);
		System.out.println("Cosine: " + sumSquareRootTime);
	}

	static float getRandomFloat(final float min, final float max) {
		final float range = max - min;
		return (float) ((Math.random() * range) + min);
	}

	static float groundDistanceSqrt(final float thrallX, final float thrallY, final float murlocX,
			final float murlocY) {
		final float dx = murlocX - thrallX;
		final float dy = murlocY - thrallY;
		return (float) StrictMath.sqrt((dx * dx) + (dy * dy));
	}

	static float groundDistanceCos(final float thrallX, final float thrallY, final float murlocX, final float murlocY) {
		final float dx = murlocX - thrallX;
		final float dy = murlocY - thrallY;
		final double angle = StrictMath.atan2(dy, dx);
		return (float) (dx / StrictMath.cos(angle));
	}
}
