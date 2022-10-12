package com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat;

public enum CDefenseType implements CodeKeyType {
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
		if (upperCaseTypeString.trim().isEmpty()) {
			System.err.println("bad");
		}
		return valueOf(upperCaseTypeString);
	}
}
