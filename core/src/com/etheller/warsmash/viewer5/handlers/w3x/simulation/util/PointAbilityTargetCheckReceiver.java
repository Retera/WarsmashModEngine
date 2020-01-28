package com.etheller.warsmash.viewer5.handlers.w3x.simulation.util;

import com.badlogic.gdx.math.Vector2;

public class PointAbilityTargetCheckReceiver implements AbilityTargetCheckReceiver<Vector2> {
	public static final PointAbilityTargetCheckReceiver INSTANCE = new PointAbilityTargetCheckReceiver();

	private Vector2 target;

	@Override
	public void targetOk(final Vector2 target) {
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
	public void targetOutsideRange(final double howMuch) {
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

	public Vector2 getTarget() {
		return this.target;
	}

}
