package com.etheller.warsmash.viewer5.handlers.w3x.ui;

public enum MenuCursorState {
	NORMAL("Normal"),
	SCROLL_LEFT("Scroll Left"),
	SCROLL_RIGHT("Scroll Right"),
	SCROLL_DOWN("Scroll Down"),
	SCROLL_UP("Scroll Up"),
	SCROLL_DOWN_LEFT("Scroll Down Left"),
	SCROLL_DOWN_RIGHT("Scroll Down Right"),
	SCROLL_UP_LEFT("Scroll Up Left"),
	SCROLL_UP_RIGHT("Scroll Up Right"),
	SELECT("Select"),
	TARGET_CURSOR(null), // handled specially
	HOLD_ITEM("HoldItem");
	private String animationName;

	private MenuCursorState(final String animationName) {
		this.animationName = animationName;
	}

	public String getAnimationName() {
		return this.animationName;
	}
}
