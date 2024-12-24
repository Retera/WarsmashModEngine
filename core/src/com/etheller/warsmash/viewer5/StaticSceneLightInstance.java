package com.etheller.warsmash.viewer5;

import java.nio.FloatBuffer;

import com.badlogic.gdx.math.Vector3;
import com.etheller.warsmash.viewer5.handlers.mdx.Light.Type;

public class StaticSceneLightInstance implements SceneLightInstance {
	private final Type type;
	private final float[] attenuation;
	private final float[] color;
	private final float intensity;
	private final float[] ambientColor;
	private final float ambientIntensity;
	private final Vector3 worldLocation;

	public StaticSceneLightInstance(final Type type, final float[] attenuation, final float[] color,
			final float intensity, final float[] ambientColor, final float ambientIntensity,
			final Vector3 worldLocation) {
		this.type = type;
		this.attenuation = attenuation;
		this.color = color;
		this.intensity = intensity;
		this.ambientColor = ambientColor;
		this.ambientIntensity = ambientIntensity;
		this.worldLocation = worldLocation;
	}

	public static StaticSceneLightInstance createDefault(final Vector3 lightDirection) {
		final Vector3 usedDirection = new Vector3(lightDirection.y, -lightDirection.x, -lightDirection.z);
		return new StaticSceneLightInstance(Type.DIRECTIONAL, new float[] { 1, 2 }, new float[] { 1, 1, 1 }, 1,
				new float[] { 1, 1, 1 }, 0.3f, usedDirection);
	}

	@Override
	public void bind(final int offset, final FloatBuffer floatBuffer) {
		final float attenuationStart = this.attenuation[0];
		final float attenuationEnd = this.attenuation[1];
		final float intensity = this.intensity;
		final float colorRed = this.color[0];
		final float colorGreen = this.color[1];
		final float colorBlue = this.color[2];
		final float ambientIntensity = this.ambientIntensity;
		final float ambientColorRed = this.ambientColor[0];
		final float ambientColorGreen = this.ambientColor[1];
		final float ambientColorBlue = this.ambientColor[2];
		floatBuffer.put(offset, this.worldLocation.x);
		floatBuffer.put(offset + 1, this.worldLocation.y);
		floatBuffer.put(offset + 2, this.worldLocation.z);
		// I use some padding to make the memory structure of the light be a 4x4 float
		// grid, when somebody who actually has experience with this stuff comes along
		// to change this to something smart, maybe they'll remove the padding if it's
		// not necessary. I'm basing how I implement this on how Ghostwolf did
		// BoneTexture
		floatBuffer.put(offset + 3, this.worldLocation.z);
		floatBuffer.put(offset + 4, this.type.ordinal());
		floatBuffer.put(offset + 5, attenuationStart);
		floatBuffer.put(offset + 6, attenuationEnd);
		floatBuffer.put(offset + 7, 0);
		floatBuffer.put(offset + 8, colorRed);
		floatBuffer.put(offset + 9, colorGreen);
		floatBuffer.put(offset + 10, colorBlue);
		floatBuffer.put(offset + 11, intensity);
		floatBuffer.put(offset + 12, ambientColorRed);
		floatBuffer.put(offset + 13, ambientColorGreen);
		floatBuffer.put(offset + 14, ambientColorBlue);
		floatBuffer.put(offset + 15, ambientIntensity);
	}
}
