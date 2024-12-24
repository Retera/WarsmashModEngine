package com.etheller.warsmash.viewer5.handlers.w3x.simulation.util;

import com.etheller.warsmash.util.War3ID;

public class BooleanAbilityActivationReceiver implements AbilityActivationReceiver {
	public static final BooleanAbilityActivationReceiver INSTANCE = new BooleanAbilityActivationReceiver();
	private boolean ok;

	@Override
	public void useOk() {
		this.ok = true;
	}

	@Override
	public void unknownReasonUseNotOk() {
		this.ok = false;
	}

	@Override
	public void notAnActiveAbility() {
		this.ok = false;
	}

	@Override
	public void missingRequirement(final War3ID type, final int level) {
		this.ok = false;
	}

	@Override
	public void missingHeroLevelRequirement(final int level) {
		this.ok = false;
	}

	@Override
	public void noHeroSkillPointsAvailable() {
		this.ok = false;
	}

	@Override
	public void techtreeMaximumReached() {
		this.ok = false;
	}

	@Override
	public void techItemAlreadyInProgress() {
		this.ok = false;
	}

	@Override
	public void disabled() {
		this.ok = false;
	}

	@Override
	public void cooldownNotYetReady(final float cooldownRemaining, final float cooldownMax) {
		this.ok = false;
	}

	@Override
	public void noChargesRemaining() {
		this.ok = false;
	}

	@Override
	public void activationCheckFailed(String commandStringErrorKey) {
		this.ok = false;
	}

	public boolean isOk() {
		return this.ok;
	}

}
