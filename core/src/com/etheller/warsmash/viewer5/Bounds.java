package com.etheller.warsmash.viewer5;

public class Bounds {
	public float x, y, z, r;

	public void fromExtents(final float[] min, final float[] max) {
		final float x = min[0];
		final float y = min[1];
		final float z = min[2];
		final float w = max[0] - x;
		final float d = max[1] - y;
		final float h = max[2] - z;

		this.x = x + (w / 2f);
		this.y = y + (d / 2f);
		this.z = z + (h / 2f);
		this.r = (float) (Math.max(Math.max(w, d), h) / 2.);
	}
}
