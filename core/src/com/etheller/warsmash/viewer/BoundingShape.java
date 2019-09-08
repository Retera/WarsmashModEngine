package com.etheller.warsmash.viewer;

import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

public class BoundingShape extends SceneNode {
	private final float[] min = new float[] { -1, -1, -1 };
	private final float[] max = new float[] { 1, 1, 1 };
	private float radius = (float) Math.sqrt(2);

	public void fromBounds(final float[] min, final float[] max) {
		System.arraycopy(min, 0, this.min, 0, this.min.length);
		System.arraycopy(max, 0, this.max, 0, this.max.length);

		final float dX = max[0] - min[0];
		final float dY = max[1] - min[1];
		final float dZ = max[2] - min[2];

		this.radius = (float) Math.sqrt((dX * dX) + (dY * dY) + (dZ * dZ)) / 2;
	}

	public void fromRadius(final float radius) {
		final float s = (float) (radius * Math.cos(radius));
		this.min[0] = this.min[1] = this.min[2] = s;
		this.max[0] = this.max[1] = this.max[2] = s;
		this.radius = radius;
	}

	public void fromVertices(final float[] vertices) {
		final float[] min = new float[] { 1E9f, 1E9f, 1E9f };
		final float[] max = new float[] { -1E9f, -1E9f, -1E9f };

		for (int i = 0, l = vertices.length; i < l; i += 3) {
			final float x = vertices[i];
			final float y = vertices[i + 1];
			final float z = vertices[i + 2];

			if (x > max[0]) {
				max[0] = x;
			}
			if (x < min[0]) {
				min[0] = x;
			}
			if (y > max[1]) {
				max[1] = y;
			}
			if (y < min[1]) {
				min[1] = y;
			}
			if (z > max[2]) {
				max[2] = z;
			}
			if (z < min[2]) {
				min[2] = z;
			}
		}

		fromBounds(min, max);
	}

	public Vector3 getPositiveVertex(final Vector3 out, final Vector3 normal) {
		if (normal.x >= 0) {
			out.x = this.max[0];
		}
		else {
			out.x = this.min[0];
		}
		if (normal.y >= 0) {
			out.y = this.max[1];
		}
		else {
			out.y = this.min[1];
		}
		if (normal.z >= 0) {
			out.z = this.max[2];
		}
		else {
			out.z = this.min[2];
		}

		return out;
	}

	public Vector3 getNegativeVertex(final Vector3 out, final Vector3 normal) {
		if (normal.x >= 0) {
			out.x = this.min[0];
		}
		else {
			out.x = this.max[0];
		}
		if (normal.y >= 0) {
			out.y = this.min[1];
		}
		else {
			out.y = this.max[1];
		}
		if (normal.z >= 0) {
			out.z = this.min[2];
		}
		else {
			out.z = this.max[2];
		}

		return out;
	}

	@Override
	protected void updateObject(final Scene scene) {
	}

	@Override
	protected void convertBasis(final Quaternion computedRotation) {
		// TODO ???
	}

}
