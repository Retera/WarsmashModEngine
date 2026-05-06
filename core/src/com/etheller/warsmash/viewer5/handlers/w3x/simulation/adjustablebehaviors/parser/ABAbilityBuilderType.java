package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.parser;

import com.etheller.interpreter.ast.util.CHandle;

public enum ABAbilityBuilderType implements CHandle {
	NORMAL_AUTOTARGET,
	NORMAL_PAIRING,
	NORMAL_FLEXTARGET,
	NORMAL_UNITTARGET,
	NORMAL_POINTTARGET,
	NORMAL_NOTARGET,
	PASSIVE,
	TEMPLATE,
	INHERIT,
	HIDDEN;

	@Override
	public int getHandleId() {
		return ordinal();
	}

	public static final ABAbilityBuilderType[] VALUES = values();
}
