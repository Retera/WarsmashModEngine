package com.etheller.warsmash.parsers.fdf.frames;

import com.etheller.warsmash.parsers.fdf.datamodel.FramePoint;

public class SetPoint {
	private final FramePoint myPoint;
	private final UIFrame other;
	private final FramePoint otherPoint;
	private final float x;
	private final float y;

	public SetPoint(final FramePoint myPoint, final UIFrame other, final FramePoint otherPoint, final float x,
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

	public UIFrame getOther() {
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
