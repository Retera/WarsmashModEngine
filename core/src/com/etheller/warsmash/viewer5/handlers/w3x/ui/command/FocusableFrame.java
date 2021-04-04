package com.etheller.warsmash.viewer5.handlers.w3x.ui.command;

import com.etheller.warsmash.parsers.fdf.frames.UIFrame;

public interface FocusableFrame extends UIFrame {
	boolean isFocusable();

	void onFocusGained();

	void onFocusLost();

	boolean keyDown(int keycode);

	boolean keyUp(int keycode);

	boolean keyTyped(char character);
}
