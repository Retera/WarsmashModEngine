package com.etheller.warsmash.networking.uberserver;

import com.etheller.warsmash.networking.uberserver.users.User;
import net.warsmash.uberserver.*;

public interface GamingNetworkServerTracker {
    void disconnectedSession(String addressString, User user);

    void handshakeAccepted(String addressString, AcceptedGameListKey acceptedGameListKey);

    void handshakeDenied(String addressString, HandshakeDeniedReason handshakeDeniedReason);

    void accountCreationFailed(String addressString, String username);

    void accountCreatedOk(String addressString, String username);

    void loginOk(String addressString, String username);

    void loginFailed(String addressString, LoginFailureReason loginFailureReason);

    void killedSession(String addressString, User user);

    void joinedChannel(String addressString, User user, String channelName);

    void badSession(String addressString);

    void joinGameFailed(String addressString, User user, String gameName, JoinGameFailureReason joinGameFailureReason);

    void joinedGame(String addressString, User user, String gameName);

    void leftGame(String addressString, User user);

    void updateMapFailedNoGame(String addressString, User user);

    void writeMap(String addressString, User user);

    void mapDoneFailedNoGame(String addressString, User user);

    void uploadMapFailed(String addressString, User user);

    void uploadMapSucceeded(String addressString, User user);

    void requestMapFailedNoGame(String addressString, User user);

    void sentMap(String addressString, User user);

    void subscribedForMap(String addressString, User user);

    void gameCreationFailed(String addressString, User user, GameCreationFailureReason gameCreationFailureReason);

    void createdGame(String addressString, User user, String gameName, String mapName, int totalSlots, LobbyGameSpeed gameSpeed, HostedGameVisibility visibility, long mapChecksum);

    void gameLobbySetPlayerSlotType(String addressString, User user, int slot, LobbyPlayerType lobbyPlayerType);

    void gameLobbySetPlayerSlotTypeFailed(String addressString, User user, int slot, LobbyPlayerType lobbyPlayerType, LobbyActionFailureReason setPlayerSlotFailureReason);

    void gameLobbySetPlayerRace(String addressString, User user, int slot, int raceItemIndex);

    void gameLobbySetPlayerRaceFailed(String addressString, User user, int slot, int raceItemIndex, LobbyActionFailureReason setPlayerSlotFailureReason);

    void gameLobbyStartGame(String addressString, User user, String channelKey);

    void gameLobbyStartGameFailed(String addressString, User user, String channelKey, LobbyActionFailureReason lobbyActionFailureReason);

    void channelChatMessage(String addressString, User user, String channelKey, String text);

    void gameLobbyChatMessage(String addressString, User user, String channelKey, String text);

    void chatMessageFailed(String addressString, User user, String text);

    void channelEmoteMessage(String addressString, User user, String channelKey, String text);

    void gameLobbyEmoteMessage(String addressString, User user, String channelKey, String text);

    void emoteMessageFailed(String addressString, User user, String text);

    void queriedGamesList(String addressString, User user);
}
