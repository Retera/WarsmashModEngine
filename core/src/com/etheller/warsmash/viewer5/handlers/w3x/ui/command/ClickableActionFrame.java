package com.etheller.warsmash.viewer5.handlers.w3x.ui.command;

import com.badlogic.gdx.utils.viewport.Viewport;
import com.etheller.warsmash.parsers.fdf.GameUI;
import com.etheller.warsmash.parsers.fdf.frames.UIFrame;

public interface ClickableActionFrame extends UIFrame {
	void mouseDown(final GameUI gameUI, final Viewport uiViewport);

	void mouseUp(final GameUI gameUI, final Viewport uiViewport);

	void onClick(int button);
}
