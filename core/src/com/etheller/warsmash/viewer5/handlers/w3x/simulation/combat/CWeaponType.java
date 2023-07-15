package com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CDamageType;

public enum CWeaponType {
	NORMAL(CDamageType.NORMAL),
	INSTANT(CDamageType.UNIVERSAL), //TODO Can we check if this is the right type?
	ARTILLERY(CDamageType.UNIVERSAL),
	ALINE(CDamageType.UNIVERSAL),
	MISSILE(CDamageType.UNIVERSAL),
	MSPLASH(CDamageType.UNIVERSAL),
	MBOUNCE(CDamageType.UNIVERSAL),
	MLINE(CDamageType.UNIVERSAL);

	private CDamageType damageType;

	CWeaponType(CDamageType damageType) {
		this.damageType = damageType;
	}

	public CDamageType getDamageType() {
		return damageType;
	}

	public static CWeaponType parseWeaponType(final String weaponTypeString) {
		return valueOf(weaponTypeString.toUpperCase());
	}

	public boolean isAttackGroundSupported() {
		return (this == CWeaponType.ARTILLERY) || (this == CWeaponType.ALINE);
	}
}
