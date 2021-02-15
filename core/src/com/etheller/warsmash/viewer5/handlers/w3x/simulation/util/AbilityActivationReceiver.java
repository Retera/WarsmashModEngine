package com.etheller.warsmash.viewer5.handlers.w3x.simulation.util;

import com.etheller.warsmash.util.War3ID;

public interface AbilityActivationReceiver {
	void useOk();

	void notEnoughResources(ResourceType resource);

	void notAnActiveAbility();

	void missingRequirement(War3ID type, int level);

	void casterMovementDisabled();

	void cargoCapacityUnavailable();

	void disabled();
}
