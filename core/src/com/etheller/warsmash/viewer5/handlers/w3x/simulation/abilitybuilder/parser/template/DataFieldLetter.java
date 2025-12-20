package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.parser.template;

import com.etheller.interpreter.ast.util.CHandle;
import com.etheller.warsmash.util.WarsmashConstants;

public enum DataFieldLetter implements CHandle {
	A(0),
	B(1),
	C(2),
	D(3),
	E(4),
	F(5),
	G(6),
	H(7),
	I(8),
	J(9);

	private final int index;

	DataFieldLetter(final int index) {
		this.index = index;
	}

	public int getIndex() {
		return this.index;
	}

	@Override
	public int getHandleId() {
		return ordinal();
	}

	@Override
	public String toString() {
		if (WarsmashConstants.PARSE_ABILITY_DATA_NUMERIC) {
			return Integer.toString(ordinal() + 1);
		}
		return super.toString();
	}

	public static final DataFieldLetter[] VALUES = values();
}
