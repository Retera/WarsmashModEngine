package com.etheller.warsmash.viewer5.handlers.w3x.rendersim.commandbuttons;

import com.badlogic.gdx.graphics.Texture;

public interface CommandButtonListener {
//	String getToolTip();
//
//	String getUberTip();
//
//	int getLumberCost();
//
//	int getGoldCost();
//
//	int getManaCost();
//
//	int getFoodCost();
//
//	Texture getIcon();
//
//	Texture getDisabledIcon();
//
//	boolean isEnabled();
//
//	float getCooldown();
//
//	float getCooldownRemaining();
//
//	boolean isAutoCastCapable();
//
//	boolean isAutoCastActive();
//
//	int getButtonPositionX();
//
//	int getButtonPositionY();
//
//	int getOrderId();
	void commandButton(int buttonPositionX, int buttonPositionY, Texture icon, int abilityHandleId, int orderId,
			int autoCastOrderId, boolean active, boolean autoCastActive, boolean menuButton, String tip, String uberTip,
			char hotkey, int goldCost, int lumberCost, int foodCost, int manaCost, float cooldownRemaining,
			float cooldownMax, int numberOverlay);
}
