package com.etheller.warsmash.viewer5.handlers.w3x.rendersim.ability;

import com.badlogic.gdx.graphics.Texture;

public class IconUI {
	private final Texture icon;
	private final Texture iconDisabled;
	private final int buttonPositionX;
	private final int buttonPositionY;
	private final String toolTip;
	private final String uberTip;
	private final char hotkey;

	public IconUI(final Texture icon, final Texture iconDisabled, final int buttonPositionX, final int buttonPositionY,
			final String toolTip, final String uberTip, final char hotkey) {
		this.icon = icon;
		this.iconDisabled = iconDisabled;
		this.buttonPositionX = buttonPositionX;
		this.buttonPositionY = buttonPositionY;
		this.toolTip = toolTip;
		this.uberTip = uberTip;
		this.hotkey = hotkey;
	}

	public Texture getIcon() {
		return this.icon;
	}

	public Texture getIconDisabled() {
		return this.iconDisabled;
	}

	public int getButtonPositionX() {
		return this.buttonPositionX;
	}

	public int getButtonPositionY() {
		return this.buttonPositionY;
	}

	public String getToolTip() {
		return this.toolTip;
	}

	public String getUberTip() {
		return this.uberTip;
	}

	public char getHotkey() {
		return this.hotkey;
	}
}
