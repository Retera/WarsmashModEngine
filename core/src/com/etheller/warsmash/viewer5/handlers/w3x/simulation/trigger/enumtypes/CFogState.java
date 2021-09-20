package com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes;

import com.etheller.interpreter.ast.util.CHandle;

public enum CFogState implements CHandle {
	MASKED,
	FOGGED,
	VISIBLE;

	public static CFogState[] VALUES = values();

	public static CFogState getById(final int id) {
		for (final CFogState type : VALUES) {
			if ((type.getId()) == id) {
				return type;
			}
		}
		return null;
	}

	public int getId() {
		return 1 << ordinal();
	}

	@Override
	public int getHandleId() {
		return getId();
	}
}
