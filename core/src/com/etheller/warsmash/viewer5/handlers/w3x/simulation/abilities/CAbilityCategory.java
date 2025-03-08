package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities;

import com.etheller.interpreter.ast.util.CHandle;

public enum CAbilityCategory implements CHandle {
	ATTACK,
	MOVEMENT,
	CORE,
	PASSIVE,
	SPELL,
	ITEM,
	BUFF;

	@Override
	public int getHandleId() {
		return ordinal();
	}

	public static final CAbilityCategory[] VALUES = values();
}
