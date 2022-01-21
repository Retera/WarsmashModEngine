package com.etheller.warsmash.viewer5.handlers.w3x.ui.command;

import com.badlogic.gdx.utils.viewport.Viewport;
import com.etheller.warsmash.parsers.fdf.GameUI;

public interface ClickableActionFrame extends ClickableFrame {
	@Override
	void mouseDown(final GameUI gameUI, final Viewport uiViewport);

	@Override
	void mouseUp(final GameUI gameUI, final Viewport uiViewport);

	@Override
	void onClick(int button);

	String getToolTip();

	String getUberTip();

	int getToolTipGoldCost();

	int getToolTipLumberCost();

	int getToolTipFoodCost();

	int getToolTipManaCost();
}
