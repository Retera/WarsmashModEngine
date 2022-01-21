package com.etheller.warsmash.viewer5.handlers.w3x.simulation.util;

public interface AbilityTargetCheckReceiver<TARGET_TYPE> {
	void targetOk(TARGET_TYPE target);

	void mustTargetTeamType(TeamType correctType);

	void mustTargetType(TargetType correctType);

	void mustTargetResources();

	void targetOutsideRange();

	void notAnActiveAbility();

	void notHolyBoltTarget();

	void alreadyFullHealth();

	void notDeathCoilTarget();

	void targetNotVisible();

	void targetTooComplicated();

	void targetNotInPlayableMap();

	void orderIdNotAccepted();

	public static enum TeamType {
		ALLIED,
		ENEMY,
		PLAYER_UNITS;
	}

	public static enum TargetType {
		UNIT,
		POINT,
		UNIT_OR_POINT,
		NO_TARGET
	}

}
