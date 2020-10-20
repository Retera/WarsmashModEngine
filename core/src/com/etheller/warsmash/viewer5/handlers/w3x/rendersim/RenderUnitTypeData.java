package com.etheller.warsmash.viewer5.handlers.w3x.rendersim;

public class RenderUnitTypeData {
	private final float maxPitch;
	private final float maxRoll;
	private final float sampleRadius;

	public RenderUnitTypeData(final float maxPitch, final float maxRoll, final float sampleRadius) {
		this.maxPitch = maxPitch;
		this.maxRoll = maxRoll;
		this.sampleRadius = sampleRadius;
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
}
