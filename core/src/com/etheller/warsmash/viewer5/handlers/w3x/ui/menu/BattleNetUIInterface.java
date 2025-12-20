package com.etheller.warsmash.viewer5.handlers.w3x.ui.menu;

import java.io.File;

import com.badlogic.gdx.utils.IntIntMap;
import com.etheller.warsmash.parsers.fdf.frames.SpriteFrame;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.config.War3MapConfig;

import net.warsmash.uberserver.ChannelServerMessageType;
import net.warsmash.uberserver.LobbyPlayerType;

public interface BattleNetUIInterface {

	void hide();

	void loginAccepted(long sessionToken, String welcomeMessage);

	SpriteFrame getDoors();

	void joinedChannel(String channelName);

	void hideCurrentScreen();

	void channelMessage(String userName, String message);

	void channelEmote(String userName, String message);

	void channelServerMessage(String userName, ChannelServerMessageType messageType);

	void accountCreatedOk();

	void setJoinGamePreviewMapToHostedMap();

	String getLastHostedGamePath();

	long getGamingNetworkSessionToken();

	void setJoinGamePreviewMap(File mapLookupFile);

	void clearJoinGamePreviewMap(String mapNameOnly);

	void joinedGame(String gameName, boolean host);

	void beginGamesList();

	void gamesListItem(String gameName, int openSlots, int totalSlots);

	void endGamesList();

	void gameLobbySlotSetPlayerType(int slot, LobbyPlayerType playerType);

	void gameLobbySlotSetPlayer(int slot, String userName);

	void gameLobbySlotSetPlayerRace(int slot, int raceItemIndex);

	IntIntMap getGameChatroomServerSlotToMapSlot();

	IntIntMap getGameChatroomMapSlotToServerSlot();

	War3MapConfig getGameChatroomMapConfig();

	Object getCurrentChannel();

	void setVisible(boolean b);

	void showLoginPrompt(String gatewayString);

	void showWelcomeScreen();

	void showCustomGameMenu();

	void showCustomGameCreateMenu();

	void showChannelMenu();

	void showChatChannel();

	void showCustomGameLobby();

}
