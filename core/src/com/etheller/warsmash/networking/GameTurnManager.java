package com.etheller.warsmash.networking;

public interface GameTurnManager {
	int getLatestCompletedTurn();

	void turnCompleted(int gameTurnTick);

	GameTurnManager PAUSED = new GameTurnManager() {
		@Override
		public int getLatestCompletedTurn() {
			return Integer.MIN_VALUE;
		}

		@Override
		public void turnCompleted(final int gameTurnTick) {
			System.err.println("got turnCompleted(" + gameTurnTick + ") while paused !!");
		}
	};

	GameTurnManager LOCAL = new GameTurnManager() {
		@Override
		public int getLatestCompletedTurn() {
			return Integer.MAX_VALUE;
		}

		@Override
		public void turnCompleted(final int gameTurnTick) {
		}
	};
}
