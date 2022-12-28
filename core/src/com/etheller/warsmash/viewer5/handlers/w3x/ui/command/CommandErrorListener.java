package com.etheller.warsmash.viewer5.handlers.w3x.ui.command;

import com.etheller.warsmash.util.War3ID;

public interface CommandErrorListener {
	void showCommandError(int playerIndex, String message);

	void showCantPlaceError(int playerIndex);

	void showCantTransportError(int playerIndex);

	void showNoFoodError(int playerIndex);

	void showNoManaError(int playerIndex);

	void showInventoryFullError(int playerIndex);

	void showUnableToFindCoupleTargetError(int playerIndex);

	void showBlightRingFullError(int playerIndex);

	void showUpgradeCompleteAlert(int playerIndex, War3ID queuedRawcode, int level);

}
