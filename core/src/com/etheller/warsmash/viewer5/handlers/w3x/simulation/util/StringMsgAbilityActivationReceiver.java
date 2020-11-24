package com.etheller.warsmash.viewer5.handlers.w3x.simulation.util;

public class StringMsgAbilityActivationReceiver implements AbilityActivationReceiver {
	private static final StringMsgAbilityActivationReceiver INSTANCE = new StringMsgAbilityActivationReceiver();

	public static StringMsgAbilityActivationReceiver getInstance() {
		return INSTANCE;
	}

	private String message;
	private boolean useOk = false;

	public StringMsgAbilityActivationReceiver reset() {
		this.message = null;
		this.useOk = false;
		return this;
	}

	public String getMessage() {
		return this.message;
	}

	public boolean isUseOk() {
		return this.useOk;
	}

	@Override
	public void useOk() {
		this.useOk = true;
	}

	@Override
	public void notEnoughResources(final ResourceType resource) {
		this.message = "NOTEXTERN: Requires more " + resource.name().toLowerCase() + ".";
	}

	@Override
	public void notAnActiveAbility() {
		this.message = "NOTEXTERN: Not an active ability.";
	}

	@Override
	public void missingRequirement(final String name) {
		this.message = "NOTEXTERN: Requires " + name;
	}

	@Override
	public void cargoCapacityUnavailable() {
		this.message = "NOTEXTERN: Cargo capacity unavailable.";
	}

	@Override
	public void casterMovementDisabled() {
		this.message = "NOTEXTERN: Caster movement disabled.";
	}

	@Override
	public void disabled() {
		this.message = "NOTEXTERN: Ability is disabled.";
	}

}
