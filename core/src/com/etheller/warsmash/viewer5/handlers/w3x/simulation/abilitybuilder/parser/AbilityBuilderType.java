package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.parser;

import com.etheller.interpreter.ast.util.CHandle;

public enum AbilityBuilderType implements CHandle {
	NORMAL_AUTOTARGET,
	NORMAL_PAIRING,
	NORMAL_FLEXTARGET_SIMPLE,
	NORMAL_UNITTARGET_SIMPLE,
	NORMAL_POINTTARGET_SIMPLE,
	NORMAL_NOTARGET_SIMPLE,
	NORMAL_FLEXTARGET,
	NORMAL_UNITTARGET,
	NORMAL_POINTTARGET,
	NORMAL_NOTARGET,
	TOGGLE,
	SMART,
	PASSIVE,
	TEMPLATE,
	HIDDEN;

	@Override
	public int getHandleId() {
		return ordinal();
	}

	public static final AbilityBuilderType[] VALUES = values();
}
