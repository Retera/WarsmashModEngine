package com.etheller.warsmash.parsers.mdlx;

public enum InterpolationType {
	DONT_INTERP, LINEAR, BEZIER, HERMITE;

	public static final InterpolationType[] VALUES = values();

	public boolean tangential() {
		return ordinal() > 1;
	}
}
