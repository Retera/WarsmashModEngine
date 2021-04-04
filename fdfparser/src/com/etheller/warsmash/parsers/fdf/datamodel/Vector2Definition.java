package com.etheller.warsmash.parsers.fdf.datamodel;

public class Vector2Definition {
	private float x;
	private float y;

	public Vector2Definition(final float x, final float y) {
		this.x = x;
		this.y = y;
	}

	public float getX() {
		return this.x;
	}

	public float getY() {
		return this.y;
	}

	public void setX(final float x) {
		this.x = x;
	}

	public void setY(final float y) {
		this.y = y;
	}

}
