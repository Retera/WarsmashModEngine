package com.etheller.warsmash.viewer5.handlers.mdx;

import com.etheller.warsmash.parsers.mdlx.AnimationMap;

public class Light extends GenericObject {

	private final int type;
	private final float[] attenuation;
	private final float[] color;
	private final float intensity;
	private final float[] ambientColor;
	private final float ambientIntensity;

	public Light(final MdxModel model, final com.etheller.warsmash.parsers.mdlx.Light light, final int index) {
		super(model, light, index);

		this.type = light.getType();
		this.attenuation = light.getAttenuation();
		this.color = light.getColor();
		this.intensity = light.getIntensity();
		this.ambientColor = light.getAmbientColor();
		this.ambientIntensity = light.getAmbientIntensity();
	}

	public int getAttenuationStart(final float[] out, final int sequence, final int frame, final int counter) {
		return this.getScalarValue(out, AnimationMap.KLAS.getWar3id(), sequence, frame, counter, this.attenuation[0]);
	}

	public int getAttenuationEnd(final float[] out, final int sequence, final int frame, final int counter) {
		return this.getScalarValue(out, AnimationMap.KLAE.getWar3id(), sequence, frame, counter, this.attenuation[1]);
	}

	public int getIntensity(final float[] out, final int sequence, final int frame, final int counter) {
		return this.getScalarValue(out, AnimationMap.KLAI.getWar3id(), sequence, frame, counter, this.intensity);
	}

	public int getColor(final float[] out, final int sequence, final int frame, final int counter) {
		return this.getVectorValue(out, AnimationMap.KLAC.getWar3id(), sequence, frame, counter, this.color);
	}

	public int getAmbientIntensity(final float[] out, final int sequence, final int frame, final int counter) {
		return this.getScalarValue(out, AnimationMap.KLBI.getWar3id(), sequence, frame, counter, this.ambientIntensity);
	}

	public int getAmbientColor(final float[] out, final int sequence, final int frame, final int counter) {
		return this.getVectorValue(out, AnimationMap.KLBC.getWar3id(), sequence, frame, counter, this.ambientColor);
	}
}
