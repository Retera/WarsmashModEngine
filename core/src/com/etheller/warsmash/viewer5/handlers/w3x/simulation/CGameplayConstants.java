package com.etheller.warsmash.viewer5.handlers.w3x.simulation;

import com.etheller.warsmash.units.DataTable;
import com.etheller.warsmash.units.Element;

/**
 * Stores some gameplay constants at runtime in a java object (symbol table) to
 * maybe be faster than a map.
 */
public class CGameplayConstants {
	private final float attackHalfAngle;

	public CGameplayConstants(final DataTable parsedDataTable) {
		final Element miscData = parsedDataTable.get("Misc");
		this.attackHalfAngle = (miscData.getFieldFloatValue("AttackHalfAngle")); // TODO use
	}

	public float getAttackHalfAngle() {
		return this.attackHalfAngle;
	}
}
