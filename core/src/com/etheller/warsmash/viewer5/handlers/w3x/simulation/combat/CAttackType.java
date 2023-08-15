package com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat;

import com.etheller.interpreter.ast.util.CHandle;

public enum CAttackType implements CodeKeyType, CHandle {
	UNKNOWN,
	NORMAL,
	PIERCE,
	SIEGE,
	SPELLS,
	CHAOS,
	MAGIC,
	HERO;

	public static CAttackType[] VALUES = values();

	private String codeKey;
	private String damageKey;

	private CAttackType() {
		final String name = name();
		this.codeKey = name.charAt(0) + name.substring(1).toLowerCase();
		this.damageKey = this.codeKey;
	}

	@Override
	public String getCodeKey() {
		return this.codeKey;
	}

	public String getDamageKey() {
		return this.damageKey;
	}

	public static CAttackType parseAttackType(final String attackTypeString) {
		final String upperCaseAttackType = attackTypeString.toUpperCase();
		if ("SEIGE".equals(upperCaseAttackType)) {
			return SIEGE;
		}
		return valueOf(upperCaseAttackType);
	}

	@Override
	public int getHandleId() {
		return ordinal();
	}
}
