package com.etheller.warsmash.viewer5.handlers.w3x.ui.menu;

public interface BattleNetUIActionListener {
	void cancelLoginPrompt();

	void recoverPassword(String text);

	void logon(String accountName, String password);

	void quitBattleNet();

	void openCustomGameMenu();

	void enterDefaultChat();

	void createAccount(String username, String password, String repeatPassword);

	void submitChatText(String text);

	void showChannelChooserPanel();

	void returnToChat();

	void requestJoinChannel(String text);

	void requestJoinGame(String text);

	void showError(String errorKey);

	void showCreateGameMenu();
}
