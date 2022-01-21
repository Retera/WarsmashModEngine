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
	public void mustTargetTeamType(final TeamType correctType) {
		this.target = null;
	}

	@Override
	public void mustTargetType(final TargetType correctType) {
		this.target = null;
	}

	@Override
	public void mustTargetResources() {
		this.target = null;
	}

	@Override
	public void notHolyBoltTarget() {
		this.target = null;
	}

	@Override
	public void alreadyFullHealth() {
		this.target = null;
	}

	@Override
	public void notDeathCoilTarget() {
		this.target = null;
	}

	@Override
	public void targetOutsideRange() {
		this.target = null;
	}

	@Override
	public void notAnActiveAbility() {
		this.target = null;
	}

	@Override
	public void targetNotVisible() {
		this.target = null;
	}

	@Override
	public void targetTooComplicated() {
		this.target = null;
	}

	@Override
	public void targetNotInPlayableMap() {
		this.target = null;
	}

	@Override
	public void orderIdNotAccepted() {
		this.target = null;
	}

	public AbilityPointTarget getTarget() {
		return this.target;
	}

}
