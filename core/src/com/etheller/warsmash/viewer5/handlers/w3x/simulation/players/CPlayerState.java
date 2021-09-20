package com.etheller.warsmash.viewer5.handlers.w3x.simulation.players;

import com.etheller.interpreter.ast.util.CHandle;

public enum CPlayerState implements CHandle {
	GAME_RESULT,
	// current resource levels
	//
	RESOURCE_GOLD,
	RESOURCE_LUMBER,
	RESOURCE_HERO_TOKENS,
	RESOURCE_FOOD_CAP,
	RESOURCE_FOOD_USED,
	FOOD_CAP_CEILING,

	GIVES_BOUNTY,
	ALLIED_VICTORY,
	PLACED,
	OBSERVER_ON_DEATH,
	OBSERVER,
	UNFOLLOWABLE,

	// taxation rate for each resource
	//
	GOLD_UPKEEP_RATE,
	LUMBER_UPKEEP_RATE,

	// cumulative resources collected by the player during the mission
	//
	GOLD_GATHERED,
	LUMBER_GATHERED,

	UNKNOWN_STATE_17,
	UNKNOWN_STATE_18,
	UNKNOWN_STATE_19,
	UNKNOWN_STATE_20,
	UNKNOWN_STATE_21,
	UNKNOWN_STATE_22,
	UNKNOWN_STATE_23,
	UNKNOWN_STATE_24,

	NO_CREEP_SLEEP;

	public static CPlayerState[] VALUES = values();

	@Override
	public int getHandleId() {
		return ordinal();
	}
}
