package com.etheller.warsmash.viewer5.handlers.w3x.rendersim.commandbuttons;

import com.badlogic.gdx.graphics.Texture;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.ability.IconUI;

public class BasicCommandButton implements CommandButton {
	private final IconUI iconUI;
	private final int orderId;

	public BasicCommandButton(final IconUI iconUI, final int orderId) {
		this.iconUI = iconUI;
		this.orderId = orderId;
	}

	@Override
	public String getToolTip() {
		return null;
	}

	@Override
	public String getUberTip() {
		return null;
	}

	@Override
	public int getLumberCost() {
		return 0;
	}

	@Override
	public int getGoldCost() {
		return 0;
	}

	@Override
	public int getManaCost() {
		return 0;
	}

	@Override
	public int getFoodCost() {
		return 0;
	}

	@Override
	public Texture getIcon() {
		return this.iconUI.getIcon();
	}

	@Override
	public Texture getDisabledIcon() {
		return this.iconUI.getIconDisabled();
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public float getCooldown() {
		return 0;
	}

	@Override
	public float getCooldownRemaining() {
		return 0;
	}

	@Override
	public boolean isAutoCastCapable() {
		return false;
	}

	@Override
	public boolean isAutoCastActive() {
		return false;
	}

	@Override
	public int getButtonPositionX() {
		return this.iconUI.getButtonPositionX();
	}

	@Override
	public int getButtonPositionY() {
		return this.iconUI.getButtonPositionY();
	}

	@Override
	public int getOrderId() {
		return this.orderId;
	}

}
