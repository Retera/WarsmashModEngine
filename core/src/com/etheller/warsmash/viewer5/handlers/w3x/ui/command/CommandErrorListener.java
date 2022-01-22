package com.etheller.warsmash.viewer5.handlers.w3x.ui.command;

public interface CommandErrorListener {
	void showCommandError(int playerIndex, String message);

	void showCantPlaceError(int playerIndex);

	void showNoFoodError(int playerIndex);

	void showNoManaError(int playerIndex);

	void showInventoryFullError(int playerIndex);

	void showUnableToFindCoupleTargetError(int playerIndex);

	void showBlightRingFullError(int playerIndex);

}
