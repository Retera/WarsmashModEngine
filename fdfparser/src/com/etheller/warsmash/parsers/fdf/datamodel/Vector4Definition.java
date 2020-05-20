package com.etheller.warsmash.parsers.fdf.datamodel;

public class Vector4Definition {
	private final float x, y, z, w;

	public Vector4Definition(final float x, final float y, final float z, final float w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}

	public float getX() {
		return this.x;
	}

	public float getY() {
		return this.y;
	}

	public float getZ() {
		return this.z;
	}

	public float getW() {
		return this.w;
	}
}
