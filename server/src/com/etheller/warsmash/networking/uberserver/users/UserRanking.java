package com.etheller.warsmash.networking.uberserver.users;

public final class UserRanking {
	private int rankedGamesWon;
	private int rankedGamesLost;
	private int rankedGamesPlayed;

	public UserRanking() {
	}

	public int getRankedGamesWon() {
		return this.rankedGamesWon;
	}

	public void setRankedGamesWon(final int rankedGamesWon) {
		this.rankedGamesWon = rankedGamesWon;
	}

	public int getRankedGamesLost() {
		return this.rankedGamesLost;
	}

	public void setRankedGamesLost(final int rankedGamesLost) {
		this.rankedGamesLost = rankedGamesLost;
	}

	public int getRankedGamesPlayed() {
		return this.rankedGamesPlayed;
	}

	public void setRankedGamesPlayed(final int rankedGamesPlayed) {
		this.rankedGamesPlayed = rankedGamesPlayed;
	}
}
