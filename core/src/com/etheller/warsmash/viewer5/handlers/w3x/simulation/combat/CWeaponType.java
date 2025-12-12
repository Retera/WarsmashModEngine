package com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat;

import com.etheller.interpreter.ast.util.CHandle;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CDamageType;

public enum CWeaponType implements CHandle {
	NONE(CDamageType.UNKNOWN, false, false),
	NORMAL(CDamageType.NORMAL, false, false),
	INSTANT(CDamageType.NORMAL, true, false),
	ARTILLERY(CDamageType.NORMAL, true, true),
	ALINE(CDamageType.NORMAL, true, true),
	MISSILE(CDamageType.NORMAL, true, true),
	MSPLASH(CDamageType.NORMAL, true, true),
	MBOUNCE(CDamageType.NORMAL, true, true),
	MLINE(CDamageType.NORMAL, true, true);

	private CDamageType damageType;
	private boolean ranged;
	private boolean projectile;

	CWeaponType(final CDamageType damageType, final boolean ranged, final boolean projectile) {
		this.damageType = damageType;
		this.ranged = ranged;
		this.projectile = projectile;
	}

	public CDamageType getDamageType() {
		return this.damageType;
	}

	public boolean isRanged() {
		return this.ranged;
	}

	public boolean isProjectile() {
		return this.projectile;
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
