package com.etheller.warsmash.viewer5.handlers.w3x.rendersim.ability;

import com.badlogic.gdx.graphics.Texture;
import com.etheller.warsmash.util.War3ID;

public class OrderButtonUI {
	private Texture icon;
	private Texture iconDisabled;
	private int buttonPositionX;
	private int buttonPositionY;
	private String tip;
	private String uberTip;
	private char hotkey;

	private War3ID previewBuildUnitId;
	private float mouseTargetRadius;

	public OrderButtonUI() {
	}

	public Texture getIcon() {
		return this.icon;
	}

	public void setIcon(final Texture icon) {
		this.icon = icon;
	}

	public Texture getIconDisabled() {
		return this.iconDisabled;
	}

	public void setIconDisabled(final Texture iconDisabled) {
		this.iconDisabled = iconDisabled;
	}

	public int getButtonPositionX() {
		return this.buttonPositionX;
	}

	public void setButtonPositionX(final int buttonPositionX) {
		this.buttonPositionX = buttonPositionX;
	}

	public int getButtonPositionY() {
		return this.buttonPositionY;
	}

	public void setButtonPositionY(final int buttonPositionY) {
		this.buttonPositionY = buttonPositionY;
	}

	public String getTip() {
		return this.tip;
	}

	public void setTip(final String tip) {
		this.tip = tip;
	}

	public String getUberTip() {
		return this.uberTip;
	}

	public void setUberTip(final String uberTip) {
		this.uberTip = uberTip;
	}

	public char getHotkey() {
		return this.hotkey;
	}

	public void setHotkey(final char hotkey) {
		this.hotkey = hotkey;
	}

	public War3ID getPreviewBuildUnitId() {
		return this.previewBuildUnitId;
	}

	public void setPreviewBuildUnitId(final War3ID previewBuildUnitId) {
		this.previewBuildUnitId = previewBuildUnitId;
	}

	public float getMouseTargetRadius() {
		return this.mouseTargetRadius;
	}

	public void setMouseTargetRadius(final float mouseTargetRadius) {
		this.mouseTargetRadius = mouseTargetRadius;
	}

	public void setFromIconUI(final IconUI theIconUi) {
		this.icon = theIconUi.getIcon();
		this.iconDisabled = theIconUi.getIconDisabled();
		this.buttonPositionX = theIconUi.getButtonPositionX();
		this.buttonPositionY = theIconUi.getButtonPositionY();
		this.tip = theIconUi.getToolTip();
		this.uberTip = theIconUi.getUberTip();
		this.hotkey = theIconUi.getHotkey();
	}
}
