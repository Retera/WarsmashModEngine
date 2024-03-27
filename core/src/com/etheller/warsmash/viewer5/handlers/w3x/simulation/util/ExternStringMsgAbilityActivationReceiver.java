package com.etheller.warsmash.viewer5.handlers.w3x.simulation.util;

import com.etheller.warsmash.util.War3ID;

public class ExternStringMsgAbilityActivationReceiver implements AbilityActivationReceiver {
	public static final ExternStringMsgAbilityActivationReceiver INSTANCE = new ExternStringMsgAbilityActivationReceiver();
	private String externStringKey;
	private boolean useOk = false;

	public ExternStringMsgAbilityActivationReceiver reset() {
		this.externStringKey = null;
		this.useOk = false;
		return this;
	}

	public String getExternStringKey() {
		return externStringKey;
	}

	public boolean isUseOk() {
		return this.useOk;
	}

	@Override
	public void useOk() {
		this.useOk = true;
	}

	@Override
	public void unknownReasonUseNotOk() {
		this.externStringKey = "Replaceme";
	}

	@Override
	public void notAnActiveAbility() {
		this.externStringKey = ""; // no error message
	}

	@Override
	public void missingRequirement(final War3ID type, final int level) {
		this.externStringKey = ""; // no error message
	}

	@Override
	public void missingHeroLevelRequirement(final int level) {
		this.externStringKey = ""; // no error message
	}

	@Override
	public void noHeroSkillPointsAvailable() {
		this.externStringKey = ""; // no error message
	}

	@Override
	public void cooldownNotYetReady(final float cooldownRemaining, final float cooldown) {
		this.externStringKey = CommandStringErrorKeys.SPELL_IS_NOT_READY_YET;
	}

	@Override
	public void techtreeMaximumReached() {
		this.externStringKey = "";
	}

	@Override
	public void techItemAlreadyInProgress() {
		this.externStringKey = "";
	}

	@Override
	public void disabled() {
		this.externStringKey = "";
	}

	@Override
	public void noChargesRemaining() {
		this.externStringKey = "";
	}

	@Override
	public void activationCheckFailed(String commandStringErrorKey) {
		this.externStringKey = commandStringErrorKey;
	}
}
