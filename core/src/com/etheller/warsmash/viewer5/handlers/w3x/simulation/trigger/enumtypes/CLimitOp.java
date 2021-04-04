package com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes;

public enum CLimitOp {
	LESS_THAN,
	LESS_THAN_OR_EQUAL,
	EQUAL,
	GREATER_THAN_OR_EQUAL,
	GREATER_THAN,
	NOT_EQUAL;

	public static CLimitOp[] VALUES = values();
}
