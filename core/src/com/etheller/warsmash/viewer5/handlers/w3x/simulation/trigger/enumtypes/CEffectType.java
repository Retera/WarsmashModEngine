package com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes;

import com.etheller.interpreter.ast.util.CHandle;

public enum CEffectType implements CHandle {
	EFFECT,
	TARGET,
	CASTER,
	SPECIAL,
	AREA_EFFECT,
	MISSILE,
	LIGHTNING;

	public static CEffectType[] VALUES = values();

	@Override
	public int getHandleId() {
		return ordinal();
	}
}
