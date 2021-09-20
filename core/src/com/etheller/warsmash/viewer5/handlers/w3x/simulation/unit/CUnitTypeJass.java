package com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit;

import com.etheller.interpreter.ast.util.CHandle;

public enum CUnitTypeJass implements CHandle {
	HERO,
	DEAD,
	STRUCTURE,

	FLYING,
	GROUND,

	ATTACKS_FLYING,
	ATTACKS_GROUND,

	MELEE_ATTACKER,
	RANGED_ATTACKER,

	GIANT,
	SUMMONED,
	STUNNED,
	PLAGUED,
	SNARED,

	UNDEAD,
	MECHANICAL,
	PEON,
	SAPPER,
	TOWNHALL,
	ANCIENT,

	TAUREN,
	POISONED,
	POLYMORPHED,
	SLEEPING,
	RESISTANT,
	ETHEREAL,
	MAGIC_IMMUNE;

	public static CUnitTypeJass[] VALUES = values();

	@Override
	public int getHandleId() {
		return ordinal();
	}
}
