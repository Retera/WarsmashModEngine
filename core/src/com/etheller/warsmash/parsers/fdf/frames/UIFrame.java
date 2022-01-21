package com.etheller.warsmash.parsers.fdf.frames;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.etheller.warsmash.parsers.fdf.GameUI;
import com.etheller.warsmash.parsers.fdf.datamodel.AnchorDefinition;
import com.etheller.warsmash.parsers.fdf.datamodel.FramePoint;

public interface UIFrame {
	public void render(SpriteBatch batch, BitmapFont baseFont, GlyphLayout glyphLayout);

	public float getFramePointX(FramePoint framePoint);

	public float getFramePointY(FramePoint framePoint);

	void setFramePointX(final FramePoint framePoint, final float x);

	void setFramePointY(final FramePoint framePoint, final float y);

	void positionBounds(GameUI gameUI, final Viewport viewport);

	void addAnchor(final AnchorDefinition anchorDefinition);

	void addSetPoint(SetPoint setPointDefinition);

	void setWidth(final float width);

	void setHeight(final float height);

	float getAssignedWidth();

	float getAssignedHeight();

	void setSetAllPoints(boolean setAllPoints);

	void setSetAllPoints(boolean setAllPoints, float inset);

	void setVisible(boolean visible);

	UIFrame getParent();

	void setParent(UIFrame parent);

	boolean isVisible();

	boolean isVisibleOnScreen();

	UIFrame touchDown(float screenX, float screenY, int button);

	UIFrame touchUp(float screenX, float screenY, int button);

	UIFrame getFrameChildUnderMouse(float screenX, float screenY);

	String getName();
}
