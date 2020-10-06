package com.etheller.warsmash.viewer5.handlers.w3x;

import com.etheller.warsmash.viewer5.gl.DataTexture;

public interface W3xSceneLightManager {
	public DataTexture getUnitLightsTexture();

	public int getUnitLightCount();

	public DataTexture getTerrainLightsTexture();

	public int getTerrainLightCount();
}
