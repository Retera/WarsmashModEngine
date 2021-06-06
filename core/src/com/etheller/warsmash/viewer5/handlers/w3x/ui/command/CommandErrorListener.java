package com.etheller.warsmash.viewer5.handlers.w3x.ui.command;

public interface CommandErrorListener {
	void showCommandError(String message);

	void showCantPlaceError();

	void showNoFoodError();

	void showInventoryFullError();

	void showUnableToFindCoupleTargetError();
}
