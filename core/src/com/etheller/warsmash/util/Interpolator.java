package com.etheller.warsmash.util;

public class Interpolator {
	public static void interpolateScalar(final float[] out, final float[] a, final float[] b, final float[] c,
			final float[] d, final float t, final int type) {
		switch (type) {
		case 0: {
			out[0] = a[0];
			break;
		}
		case 1: {
			out[0] = RenderMathUtils.lerp(a[0], d[0], t);
			break;
		}
		case 2: {
			out[0] = RenderMathUtils.hermite(a[0], b[0], c[0], d[0], t);
			break;
		}
		case 3: {
			out[0] = RenderMathUtils.bezier(a[0], b[0], c[0], d[0], t);
			break;
		}
		}
	}

	public static void interpolateVector(final float[] out, final float[] a, final float[] b, final float[] c,
			final float[] d, final float t, final int type) {
		switch (type) {
		case 0: {
			System.arraycopy(a, 0, out, 0, a.length);
			break;
		}
		case 1: {
			out[0] = RenderMathUtils.lerp(a[0], d[0], t);
			out[1] = RenderMathUtils.lerp(a[1], d[1], t);
			out[2] = RenderMathUtils.lerp(a[2], d[2], t);
			break;
		}
		case 2: {
			out[0] = RenderMathUtils.hermite(a[0], b[0], c[0], d[0], t);
			out[1] = RenderMathUtils.hermite(a[1], b[1], c[1], d[1], t);
			out[2] = RenderMathUtils.hermite(a[2], b[2], c[2], d[2], t);
			break;
		}
		case 3: {
			out[0] = RenderMathUtils.bezier(a[0], b[0], c[0], d[0], t);
			out[1] = RenderMathUtils.bezier(a[1], b[1], c[1], d[1], t);
			out[2] = RenderMathUtils.bezier(a[2], b[2], c[2], d[2], t);
			break;
		}
		}
	}

	public static void interpolateQuaternion(final float[] out, final float[] a, final float[] b, final float[] c,
			final float[] d, final float t, final int type) {
		switch (type) {
		case 0: {
			System.arraycopy(a, 0, out, 0, a.length);
			break;
		}
		case 1: {
			RenderMathUtils.slerp(out, a, d, t);
			break;
		}
		case 2:
		case 3: {
			RenderMathUtils.sqlerp(out, a, b, c, d, t);
			break;
		}
		}
	}
}
