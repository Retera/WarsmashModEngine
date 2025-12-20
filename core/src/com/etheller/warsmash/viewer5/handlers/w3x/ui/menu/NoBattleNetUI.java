package com.etheller.warsmash.viewer5.handlers.w3x.ui.menu;

import java.io.File;

import com.badlogic.gdx.utils.IntIntMap;
import com.etheller.warsmash.parsers.fdf.frames.SpriteFrame;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.config.War3MapConfig;

import net.warsmash.uberserver.ChannelServerMessageType;
import net.warsmash.uberserver.LobbyPlayerType;

public class NoBattleNetUI implements BattleNetUIInterface {
	private final SpriteFrame doorsDummy;

	public NoBattleNetUI(final SpriteFrame doorsDummy) {
		this.doorsDummy = doorsDummy;
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub

	}

	@Override
	public void loginAccepted(final long sessionToken, final String welcomeMessage) {
		// TODO Auto-generated method stub

	}

	@Override
	public SpriteFrame getDoors() {
		return this.doorsDummy;
	}

	@Override
	public void joinedChannel(final String channelName) {
		// TODO Auto-generated method stub

	}

	@Override
	public void hideCurrentScreen() {
		// TODO Auto-generated method stub

	}

	@Override
	public void channelMessage(final String userName, final String message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void channelEmote(final String userName, final String message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void channelServerMessage(final String userName, final ChannelServerMessageType messageType) {
		// TODO Auto-generated method stub

	}

	@Override
	public void accountCreatedOk() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setJoinGamePreviewMapToHostedMap() {
		// TODO Auto-generated method stub

	}

	@Override
	public String getLastHostedGamePath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getGamingNetworkSessionToken() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setJoinGamePreviewMap(final File mapLookupFile) {
		// TODO Auto-generated method stub

	}

	@Override
	public void clearJoinGamePreviewMap(final String mapNameOnly) {
		// TODO Auto-generated method stub

	}

	@Override
	public void joinedGame(final String gameName, final boolean host) {
		// TODO Auto-generated method stub

	}

	@Override
	public void beginGamesList() {
		// TODO Auto-generated method stub

	}

	@Override
	public void gamesListItem(final String gameName, final int openSlots, final int totalSlots) {
		// TODO Auto-generated method stub

	}

	@Override
	public void endGamesList() {
		// TODO Auto-generated method stub

	}

	@Override
	public void gameLobbySlotSetPlayerType(final int slot, final LobbyPlayerType playerType) {
		// TODO Auto-generated method stub

	}

	@Override
	public void gameLobbySlotSetPlayer(final int slot, final String userName) {
		// TODO Auto-generated method stub

	}

	@Override
	public void gameLobbySlotSetPlayerRace(final int slot, final int raceItemIndex) {
		// TODO Auto-generated method stub

	}

	@Override
	public IntIntMap getGameChatroomServerSlotToMapSlot() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IntIntMap getGameChatroomMapSlotToServerSlot() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public War3MapConfig getGameChatroomMapConfig() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getCurrentChannel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setVisible(final boolean b) {
		// TODO Auto-generated method stub

	}

	@Override
	public void showLoginPrompt(final String gatewayString) {
		// TODO Auto-generated method stub

	}

	@Override
	public void showWelcomeScreen() {
		// TODO Auto-generated method stub

	}

	@Override
	public void showCustomGameMenu() {
		// TODO Auto-generated method stub

	}

	@Override
	public void showCustomGameCreateMenu() {
		// TODO Auto-generated method stub

	}

	@Override
	public void showChannelMenu() {
		// TODO Auto-generated method stub

	}

	@Override
	public void showChatChannel() {
		// TODO Auto-generated method stub

	}

	@Override
	public void showCustomGameLobby() {
		// TODO Auto-generated method stub

	}

}
