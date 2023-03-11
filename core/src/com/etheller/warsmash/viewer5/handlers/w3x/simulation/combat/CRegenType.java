package com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat;

public enum CRegenType {
	NONE, ALWAYS, BLIGHT, DAY, NIGHT;

	public static CRegenType parseRegenType(final String typeString) {
		try {
			return valueOf(typeString.toUpperCase());
		}
		catch (final Exception exc) {
			exc.printStackTrace();
			return NONE;
		}
	}
}
