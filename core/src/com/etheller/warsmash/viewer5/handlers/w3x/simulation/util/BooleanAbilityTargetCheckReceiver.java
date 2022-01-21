package com.etheller.warsmash.viewer5.handlers.w3x.simulation.util;

public final class BooleanAbilityTargetCheckReceiver<TARGET_TYPE> implements AbilityTargetCheckReceiver<TARGET_TYPE> {
	private static final BooleanAbilityTargetCheckReceiver<?> INSTANCE = new BooleanAbilityTargetCheckReceiver<>();

	public static <T> BooleanAbilityTargetCheckReceiver<T> getInstance() {
		return (BooleanAbilityTargetCheckReceiver<T>) INSTANCE;
	}

	private boolean targetable = false;

	public boolean isTargetable() {
		return this.targetable;
	}

	public BooleanAbilityTargetCheckReceiver<TARGET_TYPE> reset() {
		this.targetable = false;
		return this;
	}

	@Override
	public void targetOk(final TARGET_TYPE target) {
		this.targetable = true;
	}

	@Override
	public void mustTargetTeamType(final TeamType correctType) {
		this.targetable = false;
	}

	@Override
	public void mustTargetType(final TargetType correctType) {
		this.targetable = false;
	}

	@Override
	public void mustTargetResources() {
		this.targetable = false;
	}

	@Override
	public void targetOutsideRange() {
		this.targetable = false;
	}

	@Override
	public void notAnActiveAbility() {
		this.targetable = false;
	}

	@Override
	public void notHolyBoltTarget() {
		this.targetable = false;
	}

	@Override
	public void alreadyFullHealth() {
		this.targetable = false;
	}

	@Override
	public void notDeathCoilTarget() {
		this.targetable = false;
	}

	@Override
	public void targetNotVisible() {
		this.targetable = false;
	}

	@Override
	public void targetTooComplicated() {
		this.targetable = false;
	}

	@Override
	public void targetNotInPlayableMap() {
		this.targetable = false;
	}

	@Override
	public void orderIdNotAccepted() {
		this.targetable = false;
	}

}
