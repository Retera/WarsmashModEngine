package com.etheller.warsmash.viewer5.handlers.w3x.rendersim.ability;

import com.badlogic.gdx.graphics.Texture;

public class IconUI {
	private final Texture icon;
	private final Texture iconDisabled;
	private final int buttonPositionX;
	private final int buttonPositionY;

	public IconUI(final Texture icon, final Texture iconDisabled, final int buttonPositionX,
			final int buttonPositionY) {
		this.icon = icon;
		this.iconDisabled = iconDisabled;
		this.buttonPositionX = buttonPositionX;
		this.buttonPositionY = buttonPositionY;
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
}
