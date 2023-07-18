package com.etheller.warsmash.viewer5.handlers.w3x.simulation.util;

public interface AbilityTargetCheckReceiver<TARGET_TYPE> {
	void targetOk(TARGET_TYPE target);


	void notAnActiveAbility();

	void orderIdNotAccepted();

	void targetCheckFailed(String commandStringErrorKey);

	public static enum TeamType {
		ALLIED, ENEMY, PLAYER_UNITS, CONTROL, NEUTRAL;
	}

	public static enum TargetType {
		UNIT, POINT, UNIT_OR_POINT, NO_TARGET
	}

}
