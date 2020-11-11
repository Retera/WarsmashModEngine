package com.etheller.warsmash.viewer5.handlers.w3x.ui.command;

public interface CommandCardCommandListener {
	void startUsingAbility(int abilityHandleId, int orderId, boolean rightClick);

	void openMenu(int orderId);
}
