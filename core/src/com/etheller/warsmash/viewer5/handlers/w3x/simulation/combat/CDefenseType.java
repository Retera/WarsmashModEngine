package com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat;

import com.etheller.interpreter.ast.util.CHandle;

public enum CDefenseType implements CodeKeyType, CHandle {
	SMALL,
	MEDIUM,
	LARGE,
	FORT,
	NORMAL,
	HERO,
	DIVINE,
	NONE;

	public static CDefenseType[] VALUES = values();

	private String codeKey;

	private CDefenseType() {
		this.codeKey = name().charAt(0) + name().substring(1).toLowerCase();
	}

	@Override
	public String getCodeKey() {
		return this.codeKey;
	}

	public static CDefenseType parseDefenseType(final String typeString) {
		final String upperCaseTypeString = typeString.toUpperCase();
		if (upperCaseTypeString.equals("HEAVY")) {
			return LARGE;
		}
		return valueOf(upperCaseTypeString);
	}

	@Override
	public int getHandleId() {
		return ordinal();
	}
}
