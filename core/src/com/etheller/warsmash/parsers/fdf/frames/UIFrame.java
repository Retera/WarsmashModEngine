package com.etheller.warsmash.parsers.fdf.frames;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.etheller.warsmash.parsers.fdf.datamodel.AnchorDefinition;
import com.etheller.warsmash.parsers.fdf.datamodel.FramePoint;

public interface UIFrame {
	public void render(SpriteBatch batch);

	public float getFramePointX(FramePoint framePoint);

	public float getFramePointY(FramePoint framePoint);

	void positionBounds(final Viewport viewport);

	void addAnchor(final AnchorDefinition anchorDefinition);

	void setWidth(final float width);

	void setHeight(final float height);
}
