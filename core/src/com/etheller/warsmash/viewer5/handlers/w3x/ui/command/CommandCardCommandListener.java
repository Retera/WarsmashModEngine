package com.etheller.warsmash.viewer5.handlers.w3x.ui.command;

public interface CommandCardCommandListener {
	void onClick(int abilityHandleId, int orderId, boolean rightClick);

	void openMenu(int orderId);
}
