package com.etheller.warsmash.networking.uberserver.users;

public final class UserStats {
	private int gamesPlayed;
	private int gamesWon;
	private int gamesLost;

	public UserStats() {
		this.gamesPlayed = 0;
		this.gamesWon = 0;
		this.gamesLost = 0;
	}

	public int getGamesPlayed() {
		return this.gamesPlayed;
	}

	public void setGamesPlayed(final int gamesPlayed) {
		this.gamesPlayed = gamesPlayed;
	}

	public int getGamesWon() {
		return this.gamesWon;
	}

	public void setGamesWon(final int gamesWon) {
		this.gamesWon = gamesWon;
	}

	public int getGamesLost() {
		return this.gamesLost;
	}

	public void setGamesLost(final int gamesLost) {
		this.gamesLost = gamesLost;
	}

}
