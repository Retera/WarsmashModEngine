package com.etheller.warsmash.parsers.fdf.datamodel;

public class Vector4Definition {
	private float x, y, z, w;

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

	public void setX(final float x) {
		this.x = x;
	}

	public void setY(final float y) {
		this.y = y;
	}

	public void setZ(final float z) {
		this.z = z;
	}

	public void setW(final float w) {
		this.w = w;
	}

	public void set(final float x2, final float y2, final float z2, final float w2) {
		this.x = x2;
		this.y = y2;
		this.z = z2;
		this.w = w2;

	}
}
