package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.parser.template;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit.NonStackingStatBuffType;

public enum StatBuffType {
	ATK,
	ATKSPD,
	DEF,
	HPGEN,
	MAXHPGEN,
	MPGEN,
	MAXMPGEN,
	MVSPD,
	HPSTEAL,
	THORNS,
	MAXHP,
	MAXMP;
	
	
	public NonStackingStatBuffType toNonStackingStatBuffType(boolean percentage) {
		switch(this) {
		case DEF:
			if (percentage) {
				return NonStackingStatBuffType.DEFPCT;
			} else {
				return NonStackingStatBuffType.DEF;
			}
		case HPGEN:
			if (percentage) {
				return NonStackingStatBuffType.HPGENPCT;
			} else {
				return NonStackingStatBuffType.HPGEN;
			}
		case MAXHPGEN:
			if (percentage) {
				return NonStackingStatBuffType.MAXHPGENPCT;
			} else {
				return NonStackingStatBuffType.HPGEN;
			}
		case MAXMPGEN:
			if (percentage) {
				return NonStackingStatBuffType.MAXMPGENPCT;
			} else {
				return NonStackingStatBuffType.MPGEN;
			}
		case MPGEN:
			if (percentage) {
				return NonStackingStatBuffType.MPGENPCT;
			} else {
				return NonStackingStatBuffType.MPGEN;
			}
		case MAXHP:
			if (percentage) {
				return NonStackingStatBuffType.MAXHPPCT;
			} else {
				return NonStackingStatBuffType.MAXHP;
			}
		case MAXMP:
			if (percentage) {
				return NonStackingStatBuffType.MAXMPPCT;
			} else {
				return NonStackingStatBuffType.MAXMP;
			}
		case MVSPD:
			if (percentage) {
				return NonStackingStatBuffType.MVSPDPCT;
			} else {
				return NonStackingStatBuffType.MVSPD;
			}
		case THORNS:
			if (percentage) {
				return NonStackingStatBuffType.THORNSPCT;
			} else {
				return NonStackingStatBuffType.THORNS;
			}
		case ATKSPD:
			return NonStackingStatBuffType.ATKSPD;
		case HPSTEAL:
			return NonStackingStatBuffType.HPSTEAL;
		case ATK:
		default:
			System.err.println("ERROR: Tried to convert Attack buff type with wrong function");
			return null;
		}
	}
	
	public NonStackingStatBuffType toAtkNonStackingStatBuffType(boolean percentage, boolean melee, boolean range) {
		switch(this) {
		case ATK:
			if (percentage) {
				if (melee && range) {
					return NonStackingStatBuffType.ALLATKPCT;
				} else if (melee) {
					return NonStackingStatBuffType.MELEEATKPCT;
				} else if (range) {
					return NonStackingStatBuffType.RNGDATKPCT;
				}
			} else {
				if (melee && range) {
					return NonStackingStatBuffType.ALLATK;
				} else if (melee) {
					return NonStackingStatBuffType.MELEEATK;
				} else if (range) {
					return NonStackingStatBuffType.RNGDATK;
				}
			}
			System.err.println("ERROR: Tried to convert Attack buff with both melee and range set to false");
			return null;
		default:
			System.err.println("ERROR: Tried to convert non-Attack buff type with wrong function");
			return null;
		}
	}
}
