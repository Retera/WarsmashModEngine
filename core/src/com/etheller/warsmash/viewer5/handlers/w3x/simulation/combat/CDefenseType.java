package com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat;

public enum CDefenseType implements CodeKeyType {
	NONE,
	NORMAL,
	SMALL,
	MEDIUM,
	LARGE,
	FORT,
	HERO,
	DIVINE;

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
		return valueOf(typeString.toUpperCase());
	}
}
