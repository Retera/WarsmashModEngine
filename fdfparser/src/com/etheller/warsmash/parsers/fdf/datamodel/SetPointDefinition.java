package com.etheller.warsmash.parsers.fdf.datamodel;

public class SetPointDefinition {
	private final FramePoint myPoint;
	private final String other;
	private final FramePoint otherPoint;
	private final float x;
	private final float y;

	public SetPointDefinition(final FramePoint myPoint, final String other, final FramePoint otherPoint, final float x,
			final float y) {
		this.myPoint = myPoint;
		this.other = other;
		this.otherPoint = otherPoint;
		this.x = x;
		this.y = y;
	}

	public FramePoint getMyPoint() {
		return this.myPoint;
	}

	public String getOther() {
		return this.other;
	}

	public FramePoint getOtherPoint() {
		return this.otherPoint;
	}

	public float getX() {
		return this.x;
	}

	public float getY() {
		return this.y;
	}
}
