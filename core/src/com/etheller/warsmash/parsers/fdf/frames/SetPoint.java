package com.etheller.warsmash.parsers.fdf.frames;

import com.badlogic.gdx.utils.viewport.Viewport;
import com.etheller.warsmash.parsers.fdf.GameUI;
import com.etheller.warsmash.parsers.fdf.datamodel.FramePoint;

public class SetPoint implements FramePointAssignment {
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

	@Override
	public float getX(final GameUI gameUI, final Viewport uiViewport) {
		if (this.other == null) {
			throw new NullPointerException();
		}
		return this.other.getFramePointX(this.otherPoint) + this.x;
	}

	@Override
	public float getY(final GameUI gameUI, final Viewport uiViewport) {
		return this.other.getFramePointY(this.otherPoint) + this.y;
	}

	@Override
	public String toString() {
		return "SetPoint [myPoint=" + this.myPoint + ", other=" + this.other + ", otherPoint=" + this.otherPoint
				+ ", x=" + this.x + ", y=" + this.y + "]";
	}
}
