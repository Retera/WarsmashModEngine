package com.etheller.warsmash.viewer5.handlers.mdx;

import com.etheller.warsmash.parsers.mdlx.AnimationMap;

public class Light extends GenericObject {

	private final Type type;
	private final float[] attenuation;
	private final float[] color;
	private final float intensity;
	private final float[] ambientColor;
	private final float ambientIntensity;

	public Light(final MdxModel model, final com.etheller.warsmash.parsers.mdlx.Light light, final int index) {
		super(model, light, index);

		switch (light.getType()) {
		case 0:
			this.type = Type.OMNIDIRECTIONAL;
			break;
		case 1:
			this.type = Type.DIRECTIONAL;
			break;
		case 2:
			this.type = Type.AMBIENT;
			break;
		default:
			this.type = Type.DIRECTIONAL;
			break;
		}
		this.attenuation = light.getAttenuation();
		this.color = light.getColor();
		this.intensity = light.getIntensity();
		this.ambientColor = light.getAmbientColor();
		this.ambientIntensity = light.getAmbientIntensity();
	}

	public Type getType() {
		return this.type;
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

	@Override
	public int getVisibility(final float[] out, final int sequence, final int frame, final int counter) {
		return this.getScalarValue(out, AnimationMap.KLAV.getWar3id(), sequence, frame, counter, 1);
	}

	public static enum Type {
		// Omnidirectional light used for in-game sun
		OMNIDIRECTIONAL,
		// Directional light used for torches in the game world, and similar objects
		// that "glow"
		DIRECTIONAL,
		// Directional ambient light used for torches in the game world, and similar
		// objects that "glow"
		AMBIENT;
	}
}
