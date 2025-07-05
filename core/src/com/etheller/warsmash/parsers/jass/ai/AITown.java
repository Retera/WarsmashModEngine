package com.etheller.warsmash.parsers.jass.ai;

public interface AITown {
	boolean hasMine();

	boolean hasHall();

	int getMinesOwnedCount();

	int getGoldOwned();
}
