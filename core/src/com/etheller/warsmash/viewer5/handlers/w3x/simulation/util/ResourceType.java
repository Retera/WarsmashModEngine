package com.etheller.warsmash.viewer5.handlers.w3x.simulation.util;

import com.etheller.interpreter.ast.util.CHandle;

public enum ResourceType implements CHandle {
	GOLD, LUMBER, FOOD, MANA;

	public static final ResourceType[] VALUES = values();

	@Override
	public int getHandleId() {
		return ordinal();
	}
}
