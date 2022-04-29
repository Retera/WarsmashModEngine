package net.warsmash.uberserver.lobby.state;

public class LobbyUserPlayer {
	private int slotIndex;
	private final String name;

	public LobbyUserPlayer(final int slotIndex, final String name) {
		this.slotIndex = slotIndex;
		this.name = name;
	}

	public int getSlotIndex() {
		return this.slotIndex;
	}

	public void setSlotIndex(final int slotIndex) {
		this.slotIndex = slotIndex;
	}

	public String getName() {
		return this.name;
	}
}
