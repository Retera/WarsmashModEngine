package com.etheller.warsmash.viewer5.handlers.w3x.rendersim;

public class RenderUnitTypeData {
	private final float maxPitch;
	private final float maxRoll;
	private final float sampleRadius;
	private final boolean allowCustomTeamColor;
	private final int teamColor;
	private final float animationRunSpeed;
	private final float scalingValue;
	private final float animationWalkSpeed;

	public RenderUnitTypeData(final float maxPitch, final float maxRoll, final float sampleRadius,
			final boolean allowCustomTeamColor, final int teamColor, final float animationRunSpeed,
			final float animationWalkSpeed, final float scalingValue) {
		this.maxPitch = maxPitch;
		this.maxRoll = maxRoll;
		this.sampleRadius = sampleRadius;
		this.allowCustomTeamColor = allowCustomTeamColor;
		this.teamColor = teamColor;
		this.animationRunSpeed = animationRunSpeed;
		this.animationWalkSpeed = animationWalkSpeed;
		this.scalingValue = scalingValue;
	}

	public float getMaxPitch() {
		return this.maxPitch;
	}

	public float getMaxRoll() {
		return this.maxRoll;
	}

	public float getElevationSampleRadius() {
		return this.sampleRadius;
	}

	public boolean isAllowCustomTeamColor() {
		return this.allowCustomTeamColor;
	}

	public int getTeamColor() {
		return this.teamColor;
	}

	public float getAnimationRunSpeed() {
		return this.animationRunSpeed;
	}

	public float getAnimationWalkSpeed() {
		return this.animationWalkSpeed;
	}

	public float getScalingValue() {
		return this.scalingValue;
	}
}
