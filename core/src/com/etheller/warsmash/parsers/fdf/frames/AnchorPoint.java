package com.etheller.warsmash.parsers.fdf.frames;

import com.badlogic.gdx.utils.viewport.Viewport;
import com.etheller.warsmash.parsers.fdf.GameUI;
import com.etheller.warsmash.parsers.fdf.datamodel.FramePoint;

public class AnchorPoint implements FramePointAssignment {
	private final FramePoint framePoint;
	private final float x;
	private final float y;

	public AnchorPoint(final FramePoint framePoint, final float x, final float y) {
		this.framePoint = framePoint;
		this.x = x;
		this.y = y;
	}

	public float getX() {
		return this.x;
	}

	public float getY() {
		return this.y;
	}

	@Override
	public float getX(final GameUI gameUI, final Viewport uiViewport) {
		return gameUI.getFramePointX(this.framePoint) + this.x;
	}

	@Override
	public float getY(final GameUI gameUI, final Viewport uiViewport) {
		return gameUI.getFramePointY(this.framePoint) + this.y;
	}

}
