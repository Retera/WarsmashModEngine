package com.etheller.warsmash.viewer5.handlers.w3x;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import com.etheller.warsmash.viewer5.SceneLightInstance;
import com.etheller.warsmash.viewer5.SceneLightManager;
import com.etheller.warsmash.viewer5.gl.DataTexture;
import com.etheller.warsmash.viewer5.handlers.mdx.LightInstance;

public class W3xSceneLightManager implements SceneLightManager {
	public final List<LightInstance> lights;
	private FloatBuffer lightDataCopyHeap;
	private final DataTexture unitLightsTexture;
	private final DataTexture terrainLightsTexture;
	private final War3MapViewer viewer;
	private int terrainLightCount;
	private int unitLightCount;

	public W3xSceneLightManager(final War3MapViewer viewer) {
		this.viewer = viewer;
		this.lights = new ArrayList<>();
		this.unitLightsTexture = new DataTexture(viewer.gl, 4, 4, 1);
		this.terrainLightsTexture = new DataTexture(viewer.gl, 4, 4, 1);
		this.lightDataCopyHeap = ByteBuffer.allocateDirect(16 * 1 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
	}

	@Override
	public void add(final SceneLightInstance lightInstance) {
		// TODO redesign to avoid cast
		final LightInstance mdxLight = (LightInstance) lightInstance;
		this.lights.add(mdxLight);
		rebind();
	}

	@Override
	public void remove(final SceneLightInstance lightInstance) {
		// TODO redesign to avoid cast
		final LightInstance mdxLight = (LightInstance) lightInstance;
		this.lights.remove(mdxLight);
		rebind();
	}

	private void rebind() {
		final int numberOfLights = this.lights.size() + 1;
		final int bytesNeeded = numberOfLights * 4 * 16;
		if (bytesNeeded > (this.lightDataCopyHeap.capacity() * 4)) {
			this.lightDataCopyHeap = ByteBuffer.allocateDirect(bytesNeeded).order(ByteOrder.nativeOrder())
					.asFloatBuffer();
		}

		this.unitLightCount = 0;
		this.lightDataCopyHeap.clear();
		int offset = 0;
		if (this.viewer.dncUnit != null) {
			if (!this.viewer.dncUnit.lights.isEmpty()) {
				this.viewer.dncUnit.lights.get(0).bind(0, this.lightDataCopyHeap);
				offset += 16;
				this.unitLightCount++;
			}
		}
		for (final LightInstance light : this.lights) {
			light.bind(offset, this.lightDataCopyHeap);
			offset += 16;
			this.unitLightCount++;
		}
		this.lightDataCopyHeap.flip();
		this.unitLightsTexture.bindAndUpdate(this.lightDataCopyHeap, 16, this.unitLightCount);

		this.terrainLightCount = 0;
		this.lightDataCopyHeap.clear();
		offset = 0;
		if (this.viewer.dncTerrain != null) {
			if (!this.viewer.dncTerrain.lights.isEmpty()) {
				this.viewer.dncTerrain.lights.get(0).bind(0, this.lightDataCopyHeap);
				offset += 16;
				this.terrainLightCount++;
			}
		}
		for (final LightInstance light : this.lights) {
			light.bind(offset, this.lightDataCopyHeap);
			offset += 16;
			this.terrainLightCount++;
		}
		this.lightDataCopyHeap.flip();
		this.terrainLightsTexture.bindAndUpdate(this.lightDataCopyHeap, 16, this.terrainLightCount);
	}

	public DataTexture getUnitLightsTexture() {
		return this.unitLightsTexture;
	}

	public int getUnitLightCount() {
		return this.unitLightCount;
	}

	public DataTexture getTerrainLightsTexture() {
		return this.terrainLightsTexture;
	}

	public int getTerrainLightCount() {
		return this.terrainLightCount;
	}
}
