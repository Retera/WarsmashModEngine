package com.etheller.warsmash.viewer5.handlers.w3x.simulation.util;

import com.etheller.warsmash.util.War3ID;

public interface AbilityActivationReceiver {
	void useOk();

	void unknownReasonUseNotOk();

	void notAnActiveAbility();

	void missingRequirement(War3ID type, int level);

	void missingHeroLevelRequirement(int level);

	void noHeroSkillPointsAvailable();

	void disabled();

	void techtreeMaximumReached();

	void techItemAlreadyInProgress();

	void cooldownNotYetReady(float cooldownRemaining, float cooldown);

	void noChargesRemaining();

	void activationCheckFailed(String commandStringErrorKey);
}
