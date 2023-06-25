package com.etheller.warsmash.viewer5.handlers.w3x.ui.command;

import com.etheller.warsmash.util.War3ID;

public class SettableCommandErrorListener implements CommandErrorListener {
	private CommandErrorListener delegate;

	@Override
	public void showInterfaceError(final int playerIndex, final String message) {
		this.delegate.showInterfaceError(playerIndex, message);
	}

	@Override
	public void showCommandErrorWithoutSound(int playerIndex, String message) {
		this.delegate.showCommandErrorWithoutSound(playerIndex, message);
	}

	public void setDelegate(final CommandErrorListener delegate) {
		this.delegate = delegate;
	}

	@Override
	public void showUpgradeCompleteAlert(int playerIndex, War3ID queuedRawcode, int level) {
		this.delegate.showUpgradeCompleteAlert(playerIndex, queuedRawcode, level);
	}
}
