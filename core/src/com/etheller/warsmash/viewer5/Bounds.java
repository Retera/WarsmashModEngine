package com.etheller.warsmash.viewer5;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;

public class Bounds {
	private static final Vector3 tempVec = new Vector3();
	public float x, y, z, r;
	private BoundingBox boundingBox;

	public void fromExtents(final float[] min, final float[] max, final float boundsRadius) {
		final float x = min[0];
		final float y = min[1];
		final float z = min[2];
		final float w = max[0] - x;
		final float d = max[1] - y;
		final float h = max[2] - z;

		this.x = x + (w / 2f);
		this.y = y + (d / 2f);
		this.z = z + (h / 2f);
		this.r = boundsRadius > 0 ? boundsRadius : (float) (Math.max(Math.max(w, d), h) / 2.);
		this.boundingBox = new BoundingBox(new Vector3(min), new Vector3(max));
	}

	public boolean intersectRay(final Ray ray, final Vector3 intersection) {
		if (this.boundingBox == null) {
			return false;
		}
		return Intersector.intersectRayBounds(ray, this.boundingBox, intersection);
	}

	public boolean intersectRayFast(final Ray ray) {
		return Intersector.intersectRayBoundsFast(ray, this.boundingBox);
	}

	public BoundingBox getBoundingBox() {
		return this.boundingBox;
	}

	public float getEstimatedMaxZ() {
		this.boundingBox.getMax(tempVec);
		return tempVec.z;
	}
}
