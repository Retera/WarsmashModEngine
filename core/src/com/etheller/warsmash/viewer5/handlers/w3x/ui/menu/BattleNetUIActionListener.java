package com.etheller.warsmash.viewer5.handlers.w3x.ui.menu;

public interface BattleNetUIActionListener {
	void cancelLoginPrompt();

	void recoverPassword(String text);

	void logon(String accountName, String password);

	void quitBattleNet();

	void openCustomGameMenu();

	void enterDefaultChat();
}
