package net.warsmash.uberserver.lobby.state;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.warsmash.uberserver.lobby.LobbyListener;
import net.warsmash.uberserver.lobby.LobbyRace;
import net.warsmash.uberserver.lobby.LobbySlotType;
import net.warsmash.uberserver.lobby.LobbyStateView;
import net.warsmash.uberserver.lobby.LobbyType;
import net.warsmash.uberserver.lobby.UserSlotSetting;

public class LobbyStateImpl implements LobbyListener, LobbyStateView {
	private final LobbyType lobbyType;
	private final boolean fixedPlayerSettings;
	private final LobbyPlayerSlot[] playerSlots;
	private final String[] forceNames;
	private final UserSlotSetting[] userSlotSettings;
	private final List<LobbyUserPlayer> userPlayers;

	public LobbyStateImpl(final LobbyType lobbyType, final boolean fixedPlayerSettings,
			final LobbyPlayerSlot[] playerSlots, final String[] forceNames) {
		this.lobbyType = lobbyType;
		this.fixedPlayerSettings = fixedPlayerSettings;
		this.playerSlots = playerSlots;
		this.forceNames = forceNames;
		this.userSlotSettings = new UserSlotSetting[playerSlots.length];
		this.userPlayers = new ArrayList<>();
		Arrays.fill(this.userSlotSettings, UserSlotSetting.OPEN);
	}

	@Override
	public void addUserPlayer(final int slotIndex, final String playerName) {
		for (final LobbyUserPlayer lobbyUserPlayer : this.userPlayers) {
			if (lobbyUserPlayer.getSlotIndex() == slotIndex) {
				throw new LobbyActionException("addUserPlayer(" + slotIndex + "," + playerName
						+ ") would conflict with " + lobbyUserPlayer.getName() + " who is already in that slot!");
			}
		}
		this.userPlayers.add(new LobbyUserPlayer(slotIndex, playerName));
		this.userSlotSettings[slotIndex] = null;
	}

	@Override
	public void moveUserPlayer(final String playerName, final int oldSlotIndex, final int newSlotIndex) {
		if (this.lobbyType == LobbyType.MELEE) {
			throw new LobbyActionException("Cannot move user player in " + this.lobbyType);
		}
		for (final LobbyUserPlayer lobbyUserPlayer : this.userPlayers) {
			if (lobbyUserPlayer.getName().equals(playerName) && (lobbyUserPlayer.getSlotIndex() == oldSlotIndex)) {
				this.userSlotSettings[oldSlotIndex] = UserSlotSetting.OPEN;
				lobbyUserPlayer.setSlotIndex(newSlotIndex);
				this.userSlotSettings[newSlotIndex] = null;
			}
		}
	}

	@Override
	public void setUserSlotSetting(final int slotIndex, final UserSlotSetting userSlotSetting) {
		if (this.playerSlots[slotIndex].getSlotType() != LobbySlotType.USER) {
			throw new LobbyActionException("Cannot change slot setting for player " + slotIndex);
		}
		this.userSlotSettings[slotIndex] = userSlotSetting;
	}

	@Override
	public void setRace(final int slotIndex, final LobbyRace race) {
		if (this.playerSlots[slotIndex].isRaceSelectable()) {
			this.playerSlots[slotIndex].setRace(race);
		}
		else {
			throw new LobbyActionException("Cannot set race for player " + slotIndex);
		}
	}

	@Override
	public void setTeam(final int slotIndex, final int teamIndex) {
		if (this.lobbyType != LobbyType.MELEE) {
			throw new LobbyActionException("Cannot set team in " + this.lobbyType);
		}
	}

	@Override
	public void setColor(final int slotIndex, final int colorIndex) {
		if (this.fixedPlayerSettings) {
			throw new LobbyActionException("Cannot set player color in a fixed player settings lobby");
		}
		this.playerSlots[slotIndex].setColorIndex(colorIndex);
	}

	@Override
	public void setHandicap(final int slotIndex, final int handicapIndex) {
		this.playerSlots[slotIndex].setHandicapIndex(handicapIndex);
	}

	@Override
	public void getSnapshot(final LobbyListener lobbyListener) {
		for (int i = 0; i < this.playerSlots.length; i++) {
			final LobbyPlayerSlot lobbyPlayerSlot = this.playerSlots[i];
		}
	}

}
