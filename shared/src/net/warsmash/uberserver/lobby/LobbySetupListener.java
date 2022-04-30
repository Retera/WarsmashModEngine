package net.warsmash.uberserver.lobby;

public interface LobbySetupListener {
	void beginLobby(LobbyType lobbyType, boolean fixedPlayerSettings, int forcesCount);

	void addForce(String name);

	// NOTE: below, pass null for lobbyRace if you want it to be specially
	// selectable
	void addSlot(int playerIndex, LobbySlotType slotType, LobbyRace lobbyRace, int forceIndex);

	LobbyListener endLobby();

}
