package com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat;

public enum CWeaponType {
	NORMAL,
	INSTANT,
	ARTILLERY,
	ALINE,
	MISSILE,
	MSPLASH,
	MBOUNCE,
	MLINE;

	public static CWeaponType parseWeaponType(final String weaponTypeString) {
		return valueOf(weaponTypeString.toUpperCase());
	}

	public boolean isAttackGroundSupported() {
		return (this == CWeaponType.ARTILLERY) || (this == CWeaponType.ALINE);
	}
}
