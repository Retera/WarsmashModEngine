package com.etheller.warsmash.viewer5.handlers.w3x.ui.menu;

import com.etheller.warsmash.parsers.w3x.War3Map;

import net.warsmash.uberserver.HostedGameVisibility;
import net.warsmash.uberserver.LobbyGameSpeed;
import net.warsmash.uberserver.LobbyPlayerType;

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

	void createGame(String string, String customCreatePanelCurrentSelectedMapPath, int mapPlayerSlots,
			LobbyGameSpeed gameSpeed, HostedGameVisibility hostedGameVisibility, long mapChecksum, War3Map map);

	void showError(String errorKey);

	void showCreateGameMenu();

	void leaveCustomGame();

	void gameLobbySetPlayerSlot(int serverSlot, LobbyPlayerType lobbyPlayerType);

	void gameLobbySetPlayerRace(int serverSlot, int raceItemIndex);

	void startGame();
}
