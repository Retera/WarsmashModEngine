package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core;

public class ABConstants {
	public static int NO_CAST_ID = 0;
	public static int STARTING_CAST_ID = 0;
	public static int STARTING_TRIGGER_ID = 0;
	public static int STARTING_TRIGGER_PRIORITY_ID = 1;
	
	public static int incrementCastId(int castId) {
		if (castId + 1 == NO_CAST_ID) {
			return castId+2;
		}
		return castId+1;
	}
	
	public static int incrementTriggerId(int castId) {
		return incrementCastId(castId);
	}
}
