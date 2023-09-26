package com.etheller.warsmash.networking.uberserver;

import java.util.Objects;

public class AcceptedGameListKey {
	private final String gameId;
	private final int version;

	public AcceptedGameListKey(final String gameId, final int version) {
		this.gameId = gameId;
		this.version = version;
	}

	public String getGameId() {
		return this.gameId;
	}

	public int getVersion() {
		return this.version;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.gameId, this.version);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final AcceptedGameListKey other = (AcceptedGameListKey) obj;
		return Objects.equals(this.gameId, other.gameId) && (this.version == other.version);
	}

	@Override
	public String toString() {
		return "AcceptedGameListKey{" +
				"gameId='" + gameId + '\'' +
				", version=" + version +
				'}';
	}
}
