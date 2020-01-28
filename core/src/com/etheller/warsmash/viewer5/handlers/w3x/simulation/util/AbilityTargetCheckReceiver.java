package com.etheller.warsmash.viewer5.handlers.w3x.simulation.util;

public interface AbilityTargetCheckReceiver<TARGET_TYPE> {
	void targetOk(TARGET_TYPE target);

	void mustTargetTeamType(TeamType correctType);

	void mustTargetType(TargetType correctType);

	void targetOutsideRange(double howMuch);

	void notAnActiveAbility();

	void targetNotVisible();

	void targetTooComplicated();

	void targetNotInPlayableMap();

	public static enum TeamType {
		ALLIED,
		ENEMY,
		PLAYER_UNITS;
	}

	public static enum TargetType {
		UNIT,
		POINT,
		UNIT_OR_POINT,
		NO_TARGET,
	}
}
