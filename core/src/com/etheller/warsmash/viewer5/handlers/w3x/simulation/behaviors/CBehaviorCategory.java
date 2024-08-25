package com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors;

import com.etheller.interpreter.ast.util.CHandle;

public enum CBehaviorCategory implements CHandle {
	IDLE,
	MOVEMENT,
	ATTACK,
	SPELL;

	@Override
	public int getHandleId() {
		return ordinal();
	}

	public static final CBehaviorCategory[] VALUES = values();
}
