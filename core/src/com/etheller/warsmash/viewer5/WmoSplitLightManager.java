package com.etheller.warsmash.viewer5;

import com.etheller.warsmash.viewer5.gl.DataTexture;
import com.etheller.warsmash.viewer5.handlers.w3x.W3xSceneLightManager;

/**
 * Light-manager facade for WMO-generated surfaces, backed by TWO underlying managers:
 * <ul>
 * <li><b>selfManager</b> lights the WMO surface geometry itself. It does NOT contain the surface's own
 * baked MOLT lights (the surface is already pre-lit by its MOCV vertex colors, so feeding its own MOLT
 * lights back in would double-light it). It DOES receive external dynamic lights (a doodad's torch, a
 * unit's spell glow) so those still illuminate the surface.</li>
 * <li><b>servedManager</b> is handed to the doodads spawned for this surface and to units standing on it.
 * It contains the surface's MOLT lights PLUS the same external dynamic lights, so those entities are lit
 * by the WMO's interior lamps and by each other's dynamic lights.</li>
 * </ul>
 * Two facades are built over the same pair of underlying managers:
 * <ul>
 * <li>the <b>surface</b> facade ({@code textureSource=self, addToSelf=false}): the surface renders from
 * selfManager, and its own MOLT lights (registered through this facade) go to servedManager only.</li>
 * <li>the <b>served</b> facade ({@code textureSource=served, addToSelf=true}): entities render from
 * servedManager, and their dynamic lights (registered through this facade) go to BOTH managers.</li>
 * </ul>
 * {@link #update()} always ticks both underlying managers; only the surface facade owns and disposes them.
 */
public class WmoSplitLightManager implements W3xSceneLightManager {
	private final W3xSceneLightManager textureSource;
	private final W3xSceneLightManager selfManager;
	private final W3xSceneLightManager servedManager;
	private final boolean addToSelf;
	private final boolean ownsUnderlying;

	public WmoSplitLightManager(final W3xSceneLightManager textureSource, final W3xSceneLightManager selfManager,
			final W3xSceneLightManager servedManager, final boolean addToSelf, final boolean ownsUnderlying) {
		this.textureSource = textureSource;
		this.selfManager = selfManager;
		this.servedManager = servedManager;
		this.addToSelf = addToSelf;
		this.ownsUnderlying = ownsUnderlying;
	}

	@Override
	public void add(final SceneLightInstance lightInstance) {
		this.servedManager.add(lightInstance);
		if (this.addToSelf) {
			this.selfManager.add(lightInstance);
		}
	}

	@Override
	public void remove(final SceneLightInstance lightInstance) {
		this.servedManager.remove(lightInstance);
		if (this.addToSelf) {
			this.selfManager.remove(lightInstance);
		}
	}

	@Override
	public void update() {
		this.selfManager.update();
		this.servedManager.update();
	}

	@Override
	public DataTexture getUnitLightsTexture() {
		return this.textureSource.getUnitLightsTexture();
	}

	@Override
	public int getUnitLightCount() {
		return this.textureSource.getUnitLightCount();
	}

	@Override
	public DataTexture getTerrainLightsTexture() {
		return this.textureSource.getTerrainLightsTexture();
	}

	@Override
	public int getTerrainLightCount() {
		return this.textureSource.getTerrainLightCount();
	}

	@Override
	public void dispose() {
		if (this.ownsUnderlying) {
			this.selfManager.dispose();
			this.servedManager.dispose();
		}
	}
}
