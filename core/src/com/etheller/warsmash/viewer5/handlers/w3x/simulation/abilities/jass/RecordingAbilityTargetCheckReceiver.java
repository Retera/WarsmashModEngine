package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.jass;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;

public class RecordingAbilityTargetCheckReceiver<TARGET_TYPE> implements AbilityTargetCheckReceiver<TARGET_TYPE> {

	public static final RecordingAbilityTargetCheckReceiver INSTANCE = new RecordingAbilityTargetCheckReceiver();
	private TARGET_TYPE target;
	private boolean targetOk;
	private boolean notActive;
	private boolean wrongId;
	private String commandStringErrorKey;

	public static <TARGET_TYPE> RecordingAbilityTargetCheckReceiver<TARGET_TYPE> getInstance() {
		return INSTANCE;
	}

	public RecordingAbilityTargetCheckReceiver<TARGET_TYPE> reset() {
		this.target = null;
		this.targetOk = false;
		this.notActive = false;
		this.wrongId = false;
		this.commandStringErrorKey = null;
		return this;
	}

	@Override
	public void targetOk(final TARGET_TYPE target) {
		this.target = target;
		this.targetOk = true;

	}

	@Override
	public void notAnActiveAbility() {
		this.notActive = true;
	}

	@Override
	public void orderIdNotAccepted() {
		this.wrongId = true;
	}

	@Override
	public void targetCheckFailed(final String commandStringErrorKey) {
		this.commandStringErrorKey = commandStringErrorKey;

	}

	public boolean isTargetOk() {
		return this.targetOk;
	}

	public TARGET_TYPE getTarget() {
		return this.target;
	}

	public boolean isNotActive() {
		return this.notActive;
	}

	public boolean isWrongId() {
		return this.wrongId;
	}

	public String getCommandStringErrorKey() {
		return this.commandStringErrorKey;
	}

}
