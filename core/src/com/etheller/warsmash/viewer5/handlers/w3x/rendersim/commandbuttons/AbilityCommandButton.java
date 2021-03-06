package com.etheller.warsmash.viewer5.handlers.w3x.rendersim.commandbuttons;

import com.badlogic.gdx.graphics.Texture;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.ability.AbilityUI;

public class AbilityCommandButton implements CommandButton {
	private final AbilityUI abilityIconUI;
	private final int orderId;

	public AbilityCommandButton(final AbilityUI abilityIconUI, final int orderId) {
		this.abilityIconUI = abilityIconUI;
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
		return this.abilityIconUI.getOnIconUI().getIcon();
	}

	@Override
	public Texture getDisabledIcon() {
		return this.abilityIconUI.getOnIconUI().getIconDisabled();
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
		return this.abilityIconUI.getOnIconUI().getButtonPositionX();
	}

	@Override
	public int getButtonPositionY() {
		return this.abilityIconUI.getOnIconUI().getButtonPositionY();
	}

	@Override
	public int getOrderId() {
		return this.orderId;
	}
}
