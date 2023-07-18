package com.etheller.warsmash.viewer5.handlers.w3x.ui.command;

import com.etheller.warsmash.util.War3ID;

public interface CommandErrorListener {
	void showInterfaceError(int playerIndex, String message);
	void showCommandErrorWithoutSound(int playerIndex, String message);

	void showUpgradeCompleteAlert(int playerIndex, War3ID queuedRawcode, int level);

}
