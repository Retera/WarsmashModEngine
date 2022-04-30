package net.warsmash.uberserver.lobby.state;

import net.warsmash.uberserver.lobby.LobbyConstants;
import net.warsmash.uberserver.lobby.LobbyListener;
import net.warsmash.uberserver.lobby.LobbyRace;
import net.warsmash.uberserver.lobby.LobbySetupListener;
import net.warsmash.uberserver.lobby.LobbySlotType;
import net.warsmash.uberserver.lobby.LobbyType;

public class LobbyStateImplBuilder implements LobbySetupListener {

	private LobbyType lobbyType;
	private boolean fixedPlayerSettings;
	private LobbyPlayerSlot[] playerSlots;
	private String[] forceNames;
	private int nextForceNameIndex;

	@Override
	public void beginLobby(final LobbyType lobbyType, final boolean fixedPlayerSettings, final int forcesCount) {
		this.lobbyType = lobbyType;
		this.fixedPlayerSettings = fixedPlayerSettings;
		this.playerSlots = new LobbyPlayerSlot[LobbyConstants.MAX_PLAYERS];
		this.forceNames = new String[forcesCount];
		this.nextForceNameIndex = 0;
	}

	@Override
	public void addForce(final String name) {
		this.forceNames[this.nextForceNameIndex++] = name;
	}

	@Override
	public void addSlot(final int playerIndex, final LobbySlotType slotType, LobbyRace lobbyRace,
			final int forceIndex) {
		final LobbyPlayerSlot lobbyPlayerSlot = this.playerSlots[playerIndex];
		lobbyPlayerSlot.setUsed(true);
		lobbyPlayerSlot.setColorIndex(playerIndex);
		lobbyPlayerSlot.setHandicapIndex(0); // 100%
		if (this.lobbyType == LobbyType.MELEE) {
			lobbyPlayerSlot.setSlotType(LobbySlotType.USER);
			lobbyPlayerSlot.setRace(LobbyRace.RANDOM);
			lobbyPlayerSlot.setRaceSelectable(true);
			lobbyPlayerSlot.setTeamIndex(playerIndex);
		}
		else {
			boolean raceSelectable = !this.fixedPlayerSettings;
			if (lobbyRace == null) {
				lobbyRace = LobbyRace.RANDOM;
				raceSelectable = true;
			}
			lobbyPlayerSlot.setRace(lobbyRace);
			lobbyPlayerSlot.setSlotType(slotType);
			lobbyPlayerSlot.setRaceSelectable(raceSelectable);
			lobbyPlayerSlot.setTeamIndex(forceIndex);
		}
	}

	@Override
	public LobbyListener endLobby() {
		return new LobbyStateImpl(this.lobbyType, this.fixedPlayerSettings, this.playerSlots, this.forceNames);
	}

}
