package com.etheller.warsmash.viewer5;

import com.etheller.warsmash.viewer5.gl.DataTexture;
import com.etheller.warsmash.viewer5.handlers.w3x.W3xSceneLightManager;

public class AutoUpdateSceneLightManager implements W3xSceneLightManager {
	private final W3xSceneLightManager delegate;

	public AutoUpdateSceneLightManager(final W3xSceneLightManager delegate) {
		this.delegate = delegate;
	}

	@Override
	public void add(final SceneLightInstance lightInstance) {
		this.delegate.add(lightInstance);
		this.delegate.update();
	}

	@Override
	public void remove(final SceneLightInstance lightInstance) {
		this.delegate.remove(lightInstance);
		this.delegate.update();
	}

	@Override
	public void update() {
		this.delegate.update();
	}

	@Override
	public DataTexture getUnitLightsTexture() {
		return this.delegate.getUnitLightsTexture();
	}

	@Override
	public int getUnitLightCount() {
		return this.delegate.getUnitLightCount();
	}

	@Override
	public DataTexture getTerrainLightsTexture() {
		return this.delegate.getTerrainLightsTexture();
	}

	@Override
	public int getTerrainLightCount() {
		return this.delegate.getTerrainLightCount();
	}

	@Override
	public void dispose() {
		this.delegate.dispose();
	}

}
