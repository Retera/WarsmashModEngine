package com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat;

public enum CUpgradeClass {
	ARMOR,
	ARTILLERY,
	MELEE,
	RANGED,
	CASTER;

	public static CUpgradeClass parseUpgradeClass(final String upgradeClassString) {
		return valueOf(upgradeClassString.toUpperCase());
	}
}
