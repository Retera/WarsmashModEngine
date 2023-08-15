package com.etheller.warsmash.viewer5.handlers.w3x.simulation.util;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;

public class PointAbilityTargetCheckReceiver implements AbilityTargetCheckReceiver<AbilityPointTarget> {
	public static final PointAbilityTargetCheckReceiver INSTANCE = new PointAbilityTargetCheckReceiver();

	private AbilityPointTarget target;

	public PointAbilityTargetCheckReceiver reset() {
		this.target = null;
		return this;
	}

	@Override
	public void targetOk(final AbilityPointTarget target) {
		this.target = target;
	}

	@Override
	public void notAnActiveAbility() {
		this.target = null;
	}

	@Override
	public void orderIdNotAccepted() {
		this.target = null;
	}

	@Override
	public void targetCheckFailed(String commandStringErrorKey) {
		this.target = null;
	}

	public AbilityPointTarget getTarget() {
		return this.target;
	}

}
