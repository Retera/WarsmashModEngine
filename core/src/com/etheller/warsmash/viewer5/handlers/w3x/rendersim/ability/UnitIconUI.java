package com.etheller.warsmash.viewer5.handlers.w3x.rendersim.ability;

import com.badlogic.gdx.graphics.Texture;

public class UnitIconUI extends IconUI {
	private final String reviveTip;
	private final String awakenTip;

	public UnitIconUI(final Texture icon, final Texture iconDisabled, final int buttonPositionX,
			final int buttonPositionY, final String toolTip, final String uberTip, final char hotkey,
			final String reviveTip, final String awakenTip) {
		super(icon, iconDisabled, buttonPositionX, buttonPositionY, toolTip, uberTip, hotkey);
		this.reviveTip = reviveTip;
		this.awakenTip = awakenTip;
	}

	public String getReviveTip() {
		return this.reviveTip;
	}

	public String getAwakenTip() {
		return this.awakenTip;
	}
}
