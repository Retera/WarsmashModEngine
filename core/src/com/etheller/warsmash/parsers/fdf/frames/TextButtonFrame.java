package com.etheller.warsmash.parsers.fdf.frames;

public class TextButtonFrame extends GlueTextButtonFrame {
	private float buttonPushedTextOffsetX;
	private float buttonPushedTextOffsetY;

	public TextButtonFrame(final String name, final UIFrame parent) {
		super(name, parent);
	}

	public void setButtonPushedTextOffsetX(final float buttonPushedTextOffsetX) {
		this.buttonPushedTextOffsetX = buttonPushedTextOffsetX;
	}

	public void setButtonPushedTextOffsetY(final float buttonPushedTextOffsetY) {
		this.buttonPushedTextOffsetY = buttonPushedTextOffsetY;
	}
}
