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
	public void notAnActiveAbility() {
		this.targetable = false;
	}

	@Override
	public void orderIdNotAccepted() {
		this.targetable = false;
	}

	@Override
	public void targetCheckFailed(String commandStringErrorKey) {
		this.targetable = false;
	}
}
