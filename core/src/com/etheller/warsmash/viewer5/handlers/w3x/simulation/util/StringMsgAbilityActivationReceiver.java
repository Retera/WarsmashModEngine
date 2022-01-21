package com.etheller.warsmash.viewer5.handlers.w3x.simulation.util;

import com.etheller.warsmash.util.War3ID;

public class StringMsgAbilityActivationReceiver implements AbilityActivationReceiver {
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
	public void missingRequirement(final War3ID type, final int level) {
		this.message = "NOTEXTERN: Requires " + type;
	}

	@Override
	public void missingHeroLevelRequirement(final int level) {
		this.message = "NOTEXTERN: Requires Level " + level;
	}

	@Override
	public void noHeroSkillPointsAvailable() {
		this.message = "NOTEXTERN: No hero skill points available.";
	}

	@Override
	public void cooldownNotYetReady(final float cooldownRemaining, final float cooldown) {
		this.message = "NOTEXTERN: Spell is not ready yet.";
	}

	@Override
	public void techtreeMaximumReached() {
		this.message = "NOTEXTERN: Techtree maximum reached.";
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
