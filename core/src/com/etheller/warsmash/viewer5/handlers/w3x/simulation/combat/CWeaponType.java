package com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat;

import com.etheller.interpreter.ast.util.CHandle;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CDamageType;

public enum CWeaponType implements CHandle {
	NONE(CDamageType.UNKNOWN, false),
	NORMAL(CDamageType.NORMAL, false),
	INSTANT(CDamageType.NORMAL, true),
	ARTILLERY(CDamageType.NORMAL, true),
	ALINE(CDamageType.NORMAL, true),
	MISSILE(CDamageType.NORMAL, true),
	MSPLASH(CDamageType.NORMAL, true),
	MBOUNCE(CDamageType.NORMAL, true),
	MLINE(CDamageType.NORMAL, true);

	private CDamageType damageType;
	private boolean ranged;

	CWeaponType(final CDamageType damageType, final boolean ranged) {
		this.damageType = damageType;
	}

	public CDamageType getDamageType() {
		return this.damageType;
	}

	public boolean isRanged() {
		return this.ranged;
	}

	public void setRanged(final boolean ranged) {
		this.ranged = ranged;
	}

	public static CWeaponType parseWeaponType(final String weaponTypeString) {
		return valueOf(weaponTypeString.toUpperCase());
	}

	public boolean isAttackGroundSupported() {
		return (this == CWeaponType.ARTILLERY) || (this == CWeaponType.ALINE);
	}

	@Override
	public int getHandleId() {
		return ordinal();
	}
}
