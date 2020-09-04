package com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat;

public enum CAttackType implements CodeKeyType {
	UNKNOWN,
	NORMAL,
	PIERCE,
	SIEGE,
	SPELLS,
	CHAOS,
	MAGIC,
	HERO;

	private String codeKey;

	private CAttackType() {
		String name = name();
		if (name.equals("SPELLS")) {
			name = "MAGIC";
		}
		this.codeKey = name.charAt(0) + name.substring(1).toLowerCase();
	}

	@Override
	public String getCodeKey() {
		return this.codeKey;
	}

	public static CAttackType parseAttackType(final String attackTypeString) {
		return valueOf(attackTypeString.toUpperCase());
	}
}
