package net.warsmash.uberserver.lobby.state;

import net.warsmash.uberserver.lobby.LobbyRace;
import net.warsmash.uberserver.lobby.LobbySlotType;

public class LobbyPlayerSlot {
	private boolean used;
	private LobbySlotType slotType;
	private LobbyRace race;
	private boolean raceSelectable;
	private int teamIndex;
	private int colorIndex;
	private int handicapIndex;

	public LobbyPlayerSlot(final boolean used, final LobbySlotType slotType, final LobbyRace race,
			final boolean raceSelectable, final int teamIndex, final int colorIndex, final int handicapIndex) {
		this.used = used;
		this.slotType = slotType;
		this.race = race;
		this.raceSelectable = raceSelectable;
		this.teamIndex = teamIndex;
		this.colorIndex = colorIndex;
		this.handicapIndex = handicapIndex;
	}

	public boolean isUsed() {
		return this.used;
	}

	public void setUsed(final boolean used) {
		this.used = used;
	}

	public LobbySlotType getSlotType() {
		return this.slotType;
	}

	public void setSlotType(final LobbySlotType slotType) {
		this.slotType = slotType;
	}

	public LobbyRace getRace() {
		return this.race;
	}

	public void setRace(final LobbyRace race) {
		this.race = race;
	}

	public boolean isRaceSelectable() {
		return this.raceSelectable;
	}

	public void setRaceSelectable(final boolean raceSelectable) {
		this.raceSelectable = raceSelectable;
	}

	public int getTeamIndex() {
		return this.teamIndex;
	}

	public void setTeamIndex(final int teamIndex) {
		this.teamIndex = teamIndex;
	}

	public int getColorIndex() {
		return this.colorIndex;
	}

	public void setColorIndex(final int colorIndex) {
		this.colorIndex = colorIndex;
	}

	public int getHandicapIndex() {
		return this.handicapIndex;
	}

	public void setHandicapIndex(final int handicapIndex) {
		this.handicapIndex = handicapIndex;
	}
}
