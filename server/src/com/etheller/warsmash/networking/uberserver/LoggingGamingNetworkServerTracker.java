package com.etheller.warsmash.networking.uberserver;

import com.etheller.warsmash.networking.uberserver.users.User;
import net.warsmash.uberserver.*;

import java.io.PrintStream;
import java.util.Date;

public class LoggingGamingNetworkServerTracker implements GamingNetworkServerTracker {
    private final PrintStream stream;

    public LoggingGamingNetworkServerTracker(PrintStream stream) {
        this.stream = stream;
    }

    private String getTimestamp() {
        return new Date(System.currentTimeMillis()).toString();
    }

    @Override
    public void disconnectedSession(String addressString, User user) {
        stream.println(getTimestamp() + " " + addressString + " " + user.getUsername() + " - disconnectedSession()");
    }

    @Override
    public void handshakeAccepted(String addressString, AcceptedGameListKey acceptedGameListKey) {
        stream.println(getTimestamp() + " " + addressString + " - handshakeAccepted(" + acceptedGameListKey + ")");
    }

    @Override
    public void handshakeDenied(String addressString, HandshakeDeniedReason handshakeDeniedReason) {
        stream.println(getTimestamp() + " " + addressString + " - handshakeDenied(" + handshakeDeniedReason + ")");
    }

    @Override
    public void accountCreationFailed(String addressString, String username) {
        stream.println(getTimestamp() + " " + addressString + " - accountCreationFailed(" + username + ")");
    }

    @Override
    public void accountCreatedOk(String addressString, String username) {
        stream.println(getTimestamp() + " " + addressString + " " + username + " - accountCreatedOk()");
    }

    @Override
    public void loginOk(String addressString, String username) {
        stream.println(getTimestamp() + " " + addressString + " " + username + " - loginOk()");
    }

    @Override
    public void loginFailed(String addressString, LoginFailureReason loginFailureReason) {
        stream.println(getTimestamp() + " " + addressString + " - loginFailed(" + loginFailureReason + ")");
    }

    @Override
    public void killedSession(String addressString, User user) {
        stream.println(getTimestamp() + " " + addressString + " " + user.getUsername() + " - killedSession()");
    }

    @Override
    public void joinedChannel(String addressString, User user, String channelName) {
        stream.println(getTimestamp() + " " + addressString + " " + user.getUsername() + " - joinedChannel(" + channelName + ")");
    }

    @Override
    public void badSession(String addressString) {
        stream.println(getTimestamp() + " " + addressString + " - badSession()");
    }

    @Override
    public void joinGameFailed(String addressString, User user, String gameName, JoinGameFailureReason joinGameFailureReason) {
        stream.println(getTimestamp() + " " + addressString + " " + user.getUsername() + " - joinGameFailed(" + gameName + ", " + joinGameFailureReason + ")");
    }

    @Override
    public void joinedGame(String addressString, User user, String gameName) {
        stream.println(getTimestamp() + " " + addressString + " " + user.getUsername() + " - joinedGame(" + gameName + ")");
    }

    @Override
    public void leftGame(String addressString, User user) {
        stream.println(getTimestamp() + " " + addressString + " " + user.getUsername() + " - leftGame()");
    }

    @Override
    public void updateMapFailedNoGame(String addressString, User user) {
        stream.println(getTimestamp() + " " + addressString + " " + user.getUsername() + " - updateMapFailedNoGame()");
    }

    @Override
    public void writeMap(String addressString, User user) {
        stream.println(getTimestamp() + " " + addressString + " " + user.getUsername() + " - writeMap()");
    }

    @Override
    public void mapDoneFailedNoGame(String addressString, User user) {
        stream.println(getTimestamp() + " " + addressString + " " + user.getUsername() + " - mapDoneFailedNoGame()");
    }

    @Override
    public void uploadMapFailed(String addressString, User user) {
        stream.println(getTimestamp() + " " + addressString + " " + user.getUsername() + " - uploadMapFailed()");
    }

    @Override
    public void uploadMapSucceeded(String addressString, User user) {
        stream.println(getTimestamp() + " " + addressString + " " + user.getUsername() + " - uploadMapSucceeded()");
    }

    @Override
    public void requestMapFailedNoGame(String addressString, User user) {
        stream.println(getTimestamp() + " " + addressString + " " + user.getUsername() + " - requestMapFailedNoGame()");
    }

    @Override
    public void sentMap(String addressString, User user) {
        stream.println(getTimestamp() + " " + addressString + " " + user.getUsername() + " - sentMap()");
    }

    @Override
    public void subscribedForMap(String addressString, User user) {
        stream.println(getTimestamp() + " " + addressString + " " + user.getUsername() + " - subscribedForMap()");
    }

    @Override
    public void gameCreationFailed(String addressString, User user, GameCreationFailureReason gameCreationFailureReason) {
        stream.println(getTimestamp() + " " + addressString + " " + user.getUsername() + " - gameCreationFailed(" + gameCreationFailureReason + ")");
    }

    @Override
    public void createdGame(String addressString, User user, String gameName, String mapName, int totalSlots, LobbyGameSpeed gameSpeed, HostedGameVisibility visibility, long mapChecksum) {
        stream.println(getTimestamp() + " " + addressString + " " + user.getUsername() + " - createdGame(" + gameName + ", " + mapName + ", " + totalSlots + ", " + gameSpeed + ", " + visibility + ", " + mapChecksum + ")");
    }

    @Override
    public void gameLobbySetPlayerSlotType(String addressString, User user, int slot, LobbyPlayerType lobbyPlayerType) {
        stream.println(getTimestamp() + " " + addressString + " " + user.getUsername() + " - gameLobbySetPlayerSlotType(" + slot + ", " + lobbyPlayerType + ")");
    }

    @Override
    public void gameLobbySetPlayerSlotTypeFailed(String addressString, User user, int slot, LobbyPlayerType lobbyPlayerType, LobbyActionFailureReason setPlayerSlotFailureReason) {
        stream.println(getTimestamp() + " " + addressString + " " + user.getUsername() + " - gameLobbySetPlayerSlotTypeFailed(" + slot + ", " + lobbyPlayerType + ", " + setPlayerSlotFailureReason + ")");
    }

    @Override
    public void gameLobbySetPlayerRace(String addressString, User user, int slot, int raceItemIndex) {
        stream.println(getTimestamp() + " " + addressString + " " + user.getUsername() + " - gameLobbySetPlayerRace(" + slot + ", " + raceItemIndex + ")");
    }

    @Override
    public void gameLobbySetPlayerRaceFailed(String addressString, User user, int slot, int raceItemIndex, LobbyActionFailureReason setPlayerSlotFailureReason) {
        stream.println(getTimestamp() + " " + addressString + " " + user.getUsername() + " - gameLobbySetPlayerRaceFailed(" + slot + ", " + raceItemIndex + ", " + setPlayerSlotFailureReason + ")");
    }

    @Override
    public void gameLobbyStartGame(String addressString, User user, String channelKey) {
        stream.println(getTimestamp() + " " + addressString + " " + user.getUsername() + " - gameLobbyStartGame(" + channelKey + ")");
    }

    @Override
    public void gameLobbyStartGameFailed(String addressString, User user, String channelKey, LobbyActionFailureReason lobbyActionFailureReason) {
        stream.println(getTimestamp() + " " + addressString + " " + user.getUsername() + " - gameLobbyStartGameFailed(" + channelKey + ", " + lobbyActionFailureReason + ")");
    }

    @Override
    public void channelChatMessage(String addressString, User user, String channelKey, String text) {
        stream.println(getTimestamp() + " " + addressString + " " + user.getUsername() + " - channelChatMessage(" + channelKey + ", \"" + text + "\")");
    }

    @Override
    public void gameLobbyChatMessage(String addressString, User user, String channelKey, String text) {
        stream.println(getTimestamp() + " " + addressString + " " + user.getUsername() + " - gameLobbyChatMessage(" + channelKey + ", \"" + text + "\")");
    }

    @Override
    public void chatMessageFailed(String addressString, User user, String text) {
        stream.println(getTimestamp() + " " + addressString + " " + user.getUsername() + " - chatMessageFailed(" + text + ")");
    }

    @Override
    public void channelEmoteMessage(String addressString, User user, String channelKey, String text) {
        stream.println(getTimestamp() + " " + addressString + " " + user.getUsername() + " - channelEmoteMessage(" + channelKey + ", \"" + text + "\")");
    }

    @Override
    public void gameLobbyEmoteMessage(String addressString, User user, String channelKey, String text) {
        stream.println(getTimestamp() + " " + addressString + " " + user.getUsername() + " - gameLobbyEmoteMessage(" + channelKey + ", \"" + text + "\")");
    }

    @Override
    public void emoteMessageFailed(String addressString, User user, String text) {
        stream.println(getTimestamp() + " " + addressString + " " + user.getUsername() + " - emoteMessageFailed(" + text + ")");
    }

    @Override
    public void queriedGamesList(String addressString, User user) {
        stream.println(getTimestamp() + " " + addressString + " " + user.getUsername() + " - queriedGamesList()");
    }
}
