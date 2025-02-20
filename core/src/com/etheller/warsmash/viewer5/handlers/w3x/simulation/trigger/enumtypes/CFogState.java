package com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes;

import com.etheller.interpreter.ast.util.CHandle;

public enum CFogState implements CHandle {
	MASKED((byte) -1),
	FOGGED((byte) 127),
	VISIBLE((byte) 0);
	
	private byte mask;
	
	private CFogState(byte mask) {
		this.mask = mask;
	}

	public static CFogState[] VALUES = values();

	public static CFogState getById(final int id) {
		for (final CFogState type : VALUES) {
			if ((type.getId()) == id) {
				return type;
			}
		}
		return null;
	}
	
	public byte getMask() {
		return this.mask;
	}
	
	public static CFogState getByMask(byte mask) {
		if (mask > 0) {
			return FOGGED;
		} else if (mask < 0) {
			return MASKED;
		} else {
			return VISIBLE;
		}
	}

	public int getId() {
		return 1 << ordinal();
	}

	@Override
	public int getHandleId() {
		return getId();
	}
}
