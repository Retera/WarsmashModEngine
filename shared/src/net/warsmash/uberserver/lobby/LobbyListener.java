package net.warsmash.uberserver.lobby;

public interface LobbyListener {
	void addUserPlayer(int slotIndex, String playerName);

	void moveUserPlayer(String playerName, int oldSlotIndex, int newSlotIndex);

	void setUserSlotSetting(int slotIndex, UserSlotSetting userSlotSetting);

	void setRace(int slotIndex, LobbyRace race);

	void setTeam(int slotIndex, int teamIndex);

	void setColor(int slotIndex, int colorIndex);

	void setHandicap(int slotIndex, int handicapIndex);
}
