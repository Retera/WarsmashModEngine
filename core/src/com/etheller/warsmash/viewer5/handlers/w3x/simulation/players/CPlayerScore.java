package com.etheller.warsmash.viewer5.handlers.w3x.simulation.players;

import com.etheller.interpreter.ast.util.CHandle;

public enum CPlayerScore implements CHandle {
	UNITS_TRAINED,
	UNITS_KILLED,
	STRUCT_BUILT,
	STRUCT_RAZED,
	TECH_PERCENT,
	FOOD_MAXPROD,
	FOOD_MAXUSED,
	HEROES_KILLED,
	ITEMS_GAINED,
	MERCS_HIRED,
	GOLD_MINED_TOTAL,
	GOLD_MINED_UPKEEP,
	GOLD_LOST_UPKEEP,
	GOLD_LOST_TAX,
	GOLD_GIVEN,
	GOLD_RECEIVED,
	LUMBER_TOTAL,
	LUMBER_LOST_UPKEEP,
	LUMBER_LOST_TAX,
	LUMBER_GIVEN,
	LUMBER_RECEIVED,
	UNIT_TOTAL,
	HERO_TOTAL,
	RESOURCE_TOTAL,
	TOTAL;

	public static CPlayerScore[] VALUES = values();

	@Override
	public int getHandleId() {
		return ordinal();
	}
}
