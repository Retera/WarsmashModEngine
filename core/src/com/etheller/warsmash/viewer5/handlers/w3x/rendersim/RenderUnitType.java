package com.etheller.warsmash.viewer5.handlers.w3x.rendersim;

import java.awt.image.BufferedImage;
import java.util.EnumSet;

import com.badlogic.gdx.math.Vector3;
import com.etheller.warsmash.units.manager.MutableObjectData.WorldEditorDataType;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxModel;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.SecondaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.UnitSoundset;

public class RenderUnitType implements RenderWidgetType {
	private final MdxModel model;
	private final MdxModel portraitModel;
	private final MdxModel specialArtModel;
	private final BufferedImage buildingPathingPixelMap;
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
	private final EnumSet<SecondaryTag> requiredAnimationNamesForAttachments;
	private final EnumSet<SecondaryTag> requiredAnimationNames;
	private final RenderShadowType renderShadowType;
	private final UnitSoundset soundset;
	private final Vector3 tintingColor;
	private final float selectScale;
	private final float selectHeight;
	private final int orientationInterpolation;
	private final float blendTime;

	public RenderUnitType(MdxModel model, MdxModel portraitModel, MdxModel specialArtModel,
			BufferedImage buildingPathingPixelMap, final float maxPitch, final float maxRoll, final float sampleRadius,
			final boolean allowCustomTeamColor, final int teamColor, final float animationRunSpeed,
			final float animationWalkSpeed, final float scalingValue, final String buildingShadow,
			final String uberSplat, final float uberSplatScaleValue,
			final EnumSet<SecondaryTag> requiredAnimationNamesForAttachments,
			EnumSet<SecondaryTag> requiredAnimationNames, RenderShadowType renderShadowType, UnitSoundset soundset,
			Vector3 tintingColor, float selectScale, float selectHeight, int orientationInterpolation,
			float blendTime) {
		this.model = model;
		this.portraitModel = portraitModel;
		this.specialArtModel = specialArtModel;
		this.buildingPathingPixelMap = buildingPathingPixelMap;
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
		this.requiredAnimationNamesForAttachments = requiredAnimationNamesForAttachments;
		this.requiredAnimationNames = requiredAnimationNames;
		this.renderShadowType = renderShadowType;
		this.soundset = soundset;
		this.tintingColor = tintingColor;
		this.selectScale = selectScale;
		this.selectHeight = selectHeight;
		this.orientationInterpolation = orientationInterpolation;
		this.blendTime = blendTime;
	}

	public MdxModel getModel() {
		return this.model;
	}

	public MdxModel getPortraitModel() {
		return this.portraitModel;
	}

	public MdxModel getSpecialArtModel() {
		return this.specialArtModel;
	}

	public BufferedImage getBuildingPathingPixelMap() {
		return this.buildingPathingPixelMap;
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

	public EnumSet<SecondaryTag> getRequiredAnimationNamesForAttachments() {
		return this.requiredAnimationNamesForAttachments;
	}

	public EnumSet<SecondaryTag> getRequiredAnimationNames() {
		return this.requiredAnimationNames;
	}

	public RenderShadowType getRenderShadowType() {
		return this.renderShadowType;
	}

	public UnitSoundset getSoundset() {
		return this.soundset;
	}

	public Vector3 getTintingColor() {
		return this.tintingColor;
	}

	public float getSelectScale() {
		return this.selectScale;
	}

	public float getSelectHeight() {
		return this.selectHeight;
	}

	public int getOrientationInterpolation() {
		return this.orientationInterpolation;
	}

	public float getBlendTime() {
		return this.blendTime;
	}

	@Override
	public WorldEditorDataType getType() {
		return WorldEditorDataType.UNITS;
	}
}
