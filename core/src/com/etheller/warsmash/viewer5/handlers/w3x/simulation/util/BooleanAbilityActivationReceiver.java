package com.etheller.warsmash.viewer5.handlers.w3x.simulation.util;

public class BooleanAbilityActivationReceiver implements AbilityActivationReceiver {
	public static final BooleanAbilityActivationReceiver INSTANCE = new BooleanAbilityActivationReceiver();
	private boolean ok;

	@Override
	public void useOk() {
		this.ok = true;
	}

	@Override
	public void notEnoughResources(final ResourceType resource) {
		this.ok = false;
	}

	@Override
	public void notAnActiveAbility() {
		this.ok = false;
	}

	@Override
	public void missingRequirement(final String name) {
		this.ok = false;
	}

	@Override
	public void casterMovementDisabled() {
		this.ok = false;
	}

	@Override
	public void cargoCapacityUnavailable() {
		this.ok = false;
	}

	public boolean isOk() {
		return this.ok;
	}

}
