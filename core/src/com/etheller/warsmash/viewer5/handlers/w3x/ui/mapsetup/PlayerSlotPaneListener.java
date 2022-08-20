package com.etheller.warsmash.viewer5.handlers.w3x.ui.mapsetup;

import net.warsmash.uberserver.LobbyPlayerType;

public interface PlayerSlotPaneListener {
	void setPlayerSlot(int index, LobbyPlayerType lobbyPlayerType);

	void setPlayerRace(int index, int raceItemIndex);
}
