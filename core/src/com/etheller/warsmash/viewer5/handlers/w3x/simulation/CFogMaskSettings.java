package com.etheller.warsmash.viewer5.handlers.w3x.simulation;

public interface CFogMaskSettings {
	boolean isFogMaskEnabled();

	boolean isFogEnabled();

	byte getFogStateFromSettings(byte state);
}
