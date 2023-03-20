package com.etheller.warsmash.viewer5.handlers.w3x.rendersim;

import java.util.List;

public class RenderUnitTypeData {
	private final float maxPitch;
	private final float maxRoll;
	private final float sampleRadius;
	private final boolean allowCustomTeamColor;
	private final int teamColor;
	private final float animationRunSpeed;
	private final float scalingValue;
	private final float animationWalkSpeed;
	private final String buildingShadow;
	private final String uberSplat;
	private final float uberSplatScaleValue;
	private final List<RenderUnitReplaceableTex> replaceableTextures;

	public RenderUnitTypeData(final float maxPitch, final float maxRoll, final float sampleRadius,
			final boolean allowCustomTeamColor, final int teamColor, final float animationRunSpeed,
			final float animationWalkSpeed, final float scalingValue, final String buildingShadow,
			final String uberSplat, final float uberSplatScaleValue,
			final List<RenderUnitReplaceableTex> replaceableTextures) {
		this.maxPitch = maxPitch;
		this.maxRoll = maxRoll;
		this.sampleRadius = sampleRadius;
		this.allowCustomTeamColor = allowCustomTeamColor;
		this.teamColor = teamColor;
		this.animationRunSpeed = animationRunSpeed;
		this.animationWalkSpeed = animationWalkSpeed;
		this.scalingValue = scalingValue;
		this.buildingShadow = buildingShadow;
		this.uberSplat = uberSplat;
		this.uberSplatScaleValue = uberSplatScaleValue;
		this.replaceableTextures = replaceableTextures;
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

	public String getBuildingShadow() {
		return this.buildingShadow;
	}

	public String getUberSplat() {
		return this.uberSplat;
	}

	public float getUberSplatScaleValue() {
		return this.uberSplatScaleValue;
	}

	public List<RenderUnitReplaceableTex> getReplaceableTextures() {
		return this.replaceableTextures;
	}
}
