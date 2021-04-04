package com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes;

public enum CFogState {
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
}
