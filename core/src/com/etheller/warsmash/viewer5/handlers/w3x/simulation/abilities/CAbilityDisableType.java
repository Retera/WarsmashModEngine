package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities;

import com.etheller.interpreter.ast.util.CHandle;

public enum CAbilityDisableType implements CHandle {
	REQUIREMENTS((byte) 1),
	CONSTRUCTION((byte) 2),
	TRANSFORMATION((byte) 4),
	TRIGGER((byte) 8),
	ATTACKDISABLED((byte) 16),
	PLAYER((byte) 32);

	private byte mask;

	CAbilityDisableType(byte i) {
		this.mask = i;
	}

	public byte getMask() {
		return this.mask;
	}

	@Override
	public int getHandleId() {
		return ordinal();
	}

	public static final CAbilityDisableType[] VALUES = values();
}
