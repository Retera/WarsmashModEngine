package com.etheller.warsmash.parsers.fdf.datamodel;

public class AnchorDefinition {
	private final FramePoint myPoint;
	private final float x;
	private final float y;

	public AnchorDefinition(final FramePoint myPoint, final float x, final float y) {
		this.myPoint = myPoint;
		this.x = x;
		this.y = y;
	}

	public FramePoint getMyPoint() {
		return this.myPoint;
	}

	public float getX() {
		return this.x;
	}

	public float getY() {
		return this.y;
	}

	@Override
	public String toString() {
		return "AnchorDefinition [myPoint=" + this.myPoint + ", x=" + this.x + ", y=" + this.y + "]";
	}
}
