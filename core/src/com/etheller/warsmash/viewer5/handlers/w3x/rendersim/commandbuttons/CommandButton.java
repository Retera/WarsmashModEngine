package com.etheller.warsmash.viewer5.handlers.w3x.rendersim.commandbuttons;

import com.badlogic.gdx.graphics.Texture;

public interface CommandButton {
	String getToolTip();

	String getUberTip();

	int getLumberCost();

	int getGoldCost();

	int getManaCost();

	int getFoodCost();

	Texture getIcon();

	Texture getDisabledIcon();

	boolean isEnabled();

	float getCooldown();

	float getCooldownRemaining();

	boolean isAutoCastCapable();

	boolean isAutoCastActive();

	int getButtonPositionX();

	int getButtonPositionY();

	int getOrderId();

}
