package com.etheller.warsmash.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

public class WarsmashUtils {
	private static final Vector2 windowSize = new Vector2(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

	public static void toggleFullScreen() {
		if (!Gdx.graphics.isFullscreen()) {
			windowSize.set(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
			Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
		}
		else {
			Gdx.graphics.setWindowedMode((int) windowSize.x, (int) windowSize.y);
		}
	}
}
