package com.etheller.warsmash.parsers.fdf.datamodel;

public class AnchorDefinition {
	private final FramePoint myPoint;
	private final FramePoint relativePoint;
	private final float x;
	private final float y;

	public AnchorDefinition(final FramePoint myPoint, final float x, final float y) {
		this(myPoint, myPoint, x, y);
	}

	public AnchorDefinition(final FramePoint myPoint, final FramePoint relativePoint, final float x, final float y) {
		this.myPoint = myPoint;
		this.relativePoint = relativePoint;
		this.x = x;
		this.y = y;
	}

	public FramePoint getMyPoint() {
		return this.myPoint;
	}

	public FramePoint getRelativePoint() {
		return this.relativePoint;
	}

	public float getX() {
		return this.x;
	}

	public float getY() {
		return this.y;
	}

	@Override
	public String toString() {
		return "AnchorDefinition [myPoint=" + this.myPoint + ", relativePoint=" + this.relativePoint + ", x=" + this.x
				+ ", y=" + this.y + "]";
	}
}
