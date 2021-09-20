package com.etheller.warsmash.viewer5.handlers.w3x.simulation.players;

import com.etheller.interpreter.ast.util.CHandle;

public enum CAllianceType implements CHandle {
	PASSIVE,
	HELP_REQUEST,
	HELP_RESPONSE,
	SHARED_XP,
	SHARED_SPELLS,
	SHARED_VISION,
	SHARED_CONTROL,
	SHARED_ADVANCED_CONTROL,
	RESCUABLE,
	SHARED_VISION_FORCED;

	public static CAllianceType[] VALUES = values();

	@Override
	public int getHandleId() {
		return ordinal();
	}
}
