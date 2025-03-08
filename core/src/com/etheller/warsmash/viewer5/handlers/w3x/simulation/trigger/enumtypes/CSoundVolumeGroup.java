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
	FIRE,
	// some reforged related thing below:
	CINEMATIC_GENERAL,
	CINEMATIC_AMBIENT,
	CINEMATIC_MUSIC,
	CINEMATIC_DIALOGUE,
	CINEMATIC_SOUND_EFFECTS_1,
	CINEMATIC_SOUND_EFFECTS_2,
	CINEMATIC_SOUND_EFFECTS_3;

	public static CSoundVolumeGroup[] VALUES = values();

	@Override
	public int getHandleId() {
		return ordinal();
	}
}
