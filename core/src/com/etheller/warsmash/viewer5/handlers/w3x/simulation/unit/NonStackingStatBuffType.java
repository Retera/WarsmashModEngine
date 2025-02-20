package com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit;

import com.etheller.interpreter.ast.util.CHandle;

public enum NonStackingStatBuffType implements CHandle {
	MELEEATK,
	MELEEATKPCT,
	RNGDATK,
	RNGDATKPCT,
	ATKSPD,
	DEF,
	DEFPCT,
	HPGEN,
	HPGENPCT,
	MAXHPGENPCT,
	MPGEN,
	MPGENPCT,
	MAXMPGENPCT,
	MVSPD,
	MVSPDPCT,
	HPSTEAL,
	THORNS,
	THORNSPCT,

	MAXHP,
	MAXHPPCT,
	MAXMP,
	MAXMPPCT,

	ALLATK, // These are for parsing
	ALLATKPCT;

	@Override
	public int getHandleId() {
		return ordinal();
	}

	public static final NonStackingStatBuffType[] VALUES = values();
}
