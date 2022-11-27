package com.etheller.warsmash.viewer5.handlers.w3x.ui.command;

import com.badlogic.gdx.utils.viewport.Viewport;
import com.etheller.warsmash.parsers.fdf.GameUI;
import com.etheller.warsmash.parsers.fdf.frames.UIFrame;

public interface ClickableFrame extends UIFrame {
	void mouseDown(final GameUI gameUI, final Viewport uiViewport);

	void mouseUp(final GameUI gameUI, final Viewport uiViewport);

	void mouseEnter(final GameUI gameUI, final Viewport uiViewport);

	void mouseExit(final GameUI gameUI, final Viewport uiViewport);

	void onClick(int button);

	// played on click
	String getSoundKey();

	void mouseDragged(GameUI rootFrame, Viewport uiViewport, float x, float y);

	String SOUND_KEY_INTERFACE_CLICK = "InterfaceClick";
	String SOUND_KEY_MENU_BUTTON_CLICK = "MenuButtonClick";
	String SOUND_KEY_GLUE_SCREEN_CLICK = "GlueScreenClick";
	String SOUND_KEY_SUB_GROUP_SELECTION_CHANGE = "SubGroupSelectionChange";
	String SOUND_KEY_NONE = null;
}
