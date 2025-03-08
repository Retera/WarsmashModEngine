package com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit;

import com.etheller.interpreter.ast.util.CHandle;

public enum NonStackingStatBuffType implements CHandle {
	MELEEATK(false),
	MELEEATKPCT(false),
	RNGDATK(false),
	RNGDATKPCT(false),
	ATKSPD(false),
	DEF(false),
	DEFPCT(false),
	HPGEN(false),
	HPGENPCT(false),
	MAXHPGENPCT(false),
	MPGEN(false),
	MPGENPCT(false),
	MAXMPGENPCT(false),
	MVSPD(false),
	MVSPDPCT(false),
	HPSTEAL(false),
	THORNS(false),
	THORNSPCT(false),

	MAXHP(false),
	MAXHPPCT(false),
	MAXMP(false),
	MAXMPPCT(false),
	
	STR(true),
	STRPCT(true),
	AGI(true),
	AGIPCT(true),
	INT(true),
	INTPCT(true),

	ALLATK(false), // These are for parsing
	ALLATKPCT(false);
	
	private boolean heroStat;

	@Override
	public int getHandleId() {
		return ordinal();
	}

	public static final NonStackingStatBuffType[] VALUES = values();
	
	private NonStackingStatBuffType(final boolean hero) {
		this.heroStat = hero;
	}
	
	public boolean isHeroStat() {
		return this.heroStat;
	}
}
