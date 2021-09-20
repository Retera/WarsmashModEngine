package com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes;

import com.etheller.interpreter.ast.util.CHandle;

public enum CSoundVolumeGroup implements CHandle {
	UNITMOVEMENT,
	UNITSOUNDS,
	COMBAT,
	SPELLS,
	UI,
	MUSIC,
	AMBIENTSOUNDS,
	FIRE;

	public static CSoundVolumeGroup[] VALUES = values();

	@Override
	public int getHandleId() {
		return ordinal();
	}
}
