package com.etheller.warsmash.viewer5.handlers.w3x.simulation.players;

import com.etheller.interpreter.ast.util.CHandle;

public enum CMapFlag implements CHandle {
	MAP_FOG_HIDE_TERRAIN,
	MAP_FOG_MAP_EXPLORED,
	MAP_FOG_ALWAYS_VISIBLE,

	MAP_USE_HANDICAPS,
	MAP_OBSERVERS,
	MAP_OBSERVERS_ON_DEATH,

	MAP_FIXED_COLORS,

	MAP_LOCK_RESOURCE_TRADING,
	MAP_RESOURCE_TRADING_ALLIES_ONLY,

	MAP_LOCK_ALLIANCE_CHANGES,
	MAP_ALLIANCE_CHANGES_HIDDEN,

	MAP_CHEATS,
	MAP_CHEATS_HIDDEN,

	MAP_LOCK_SPEED,
	MAP_LOCK_RANDOM_SEED,
	MAP_SHARED_ADVANCED_CONTROL,
	MAP_RANDOM_HERO,
	MAP_RANDOM_RACES,
	MAP_RELOADED;

	public static CMapFlag[] VALUES = values();

	public static CMapFlag getById(final int id) {
		for (final CMapFlag type : VALUES) {
			if ((type.getId()) == id) {
				return type;
			}
		}
		return null;
	}

	public int getId() {
		return 1 << ordinal();
	}

	@Override
	public int getHandleId() {
		return getId();
	}
}
