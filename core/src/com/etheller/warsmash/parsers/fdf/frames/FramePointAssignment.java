package com.etheller.warsmash.parsers.fdf.frames;

import com.badlogic.gdx.utils.viewport.Viewport;
import com.etheller.warsmash.parsers.fdf.GameUI;

public interface FramePointAssignment {
	float getX(GameUI gameUI, Viewport uiViewport);

	float getY(GameUI gameUI, Viewport uiViewport);
}
