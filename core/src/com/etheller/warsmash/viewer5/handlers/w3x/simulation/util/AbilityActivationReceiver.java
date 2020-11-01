package com.etheller.warsmash.viewer5.handlers.w3x.simulation.util;

public interface AbilityActivationReceiver {
	void useOk();

	void notEnoughResources(ResourceType resource);

	void notAnActiveAbility();

	void missingRequirement(String name);

	void casterMovementDisabled();

	void cargoCapacityUnavailable();
}
