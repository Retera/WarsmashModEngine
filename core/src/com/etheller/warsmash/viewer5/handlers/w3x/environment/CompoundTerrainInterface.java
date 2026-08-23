package com.etheller.warsmash.viewer5.handlers.w3x.environment;

import java.io.IOException;
import java.util.List;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.etheller.warsmash.parsers.w3x.w3e.War3MapW3e;
import com.etheller.warsmash.parsers.w3x.wpm.War3MapWpm;
import com.etheller.warsmash.viewer5.handlers.w3x.DynamicShadowManager;
import com.etheller.warsmash.viewer5.handlers.w3x.SplatModel;
import com.etheller.warsmash.viewer5.handlers.w3x.SplatModel.SplatMover;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CFogMaskSettings;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.vision.CPlayerFogOfWarInterface;

public class CompoundTerrainInterface extends TerrainInterface {
	private final List<? extends TerrainInterface> terrains;
	private final War3MapW3e[] terrainDatas;
	private final War3MapW3e globalBounding;
	private final int columns;
	private final int rows;
	private final int[] mapSize;
	private final Rectangle entireMapRectangle;

	public CompoundTerrainInterface(final List<? extends TerrainInterface> terrains, final War3MapW3e[] terrainDatas,
			final War3MapW3e globalBounding, final War3MapWpm terrainPathing) {
		this.terrains = terrains;
		this.terrainDatas = terrainDatas;
		this.globalBounding = globalBounding;
		this.centerOffset = globalBounding.getCenterOffset();
		final int width = globalBounding.getMapSize()[0];
		final int height = globalBounding.getMapSize()[1];
		this.columns = width;
		this.rows = height;
		this.pathingGrid = new PathingGrid(terrainPathing, this.centerOffset);
		this.uberSplatTable = terrains.get(0).uberSplatTable;
		this.mapSize = globalBounding.getMapSize();
		this.entireMapRectangle = new Rectangle(this.centerOffset[0], this.centerOffset[1],
				(this.mapSize[0] * 128f) - 128, (this.mapSize[1] * 128f) - 128);
	}

	@Override
	public void initShadows() throws IOException {
		for (final TerrainInterface terrain : this.terrains) {
			terrain.initShadows();
		}
	}

	@Override
	public void setFogOfWarData(final CFogMaskSettings fogMaskSettings, final CPlayerFogOfWarInterface fogOfWar) {
		for (final TerrainInterface terrain : this.terrains) {
			terrain.setFogOfWarData(fogMaskSettings, fogOfWar);
		}

	}

	@Override
	public void reloadFogOfWarDataToGPU(final CFogMaskSettings fogMaskSettings) {
		for (final TerrainInterface terrain : this.terrains) {
			terrain.reloadFogOfWarDataToGPU(fogMaskSettings);
		}
	}

	@Override
	public void removeTerrainCellWithoutFlush(final int i, final int j) {
		System.out.println("CompoundTerrainInterface::removeTerrainCellWithoutFlush NYI");
	}

	@Override
	public void flushRemovedTerrainCells() {
		System.out.println("CompoundTerrainInterface::flushRemovedTerrainCells NYI");
	}

	@Override
	public BuildingShadow addShadow(final String shadowString, final float x, final float y) {
		for (final TerrainInterface terrain : this.terrains) {
			if (terrain.inPlayableArea(x, y)) {
				return terrain.addShadow(shadowString, x, y);
			}
		}
		return new BuildingShadow() {
			@Override
			public void remove() {
			}

			@Override
			public void move(final float x, final float y) {
			}
		};
	}

	@Override
	public float getGroundHeight(final float unitX, final float unitY) {
		for (final TerrainInterface terrain : this.terrains) {
			if (terrain.inPlayableArea(unitX, unitY)) {
				return terrain.getGroundHeight(unitX, unitY);
			}
		}
		return NO_TERRAIN_HEIGHT;
	}

	@Override
	public void loadSplats() throws IOException {
		for (final TerrainInterface terrain : this.terrains) {
			terrain.loadSplats();
		}
	}

	@Override
	public SplatModel getSplatModel(final String string) {
		return null;
	}

	@Override
	public int get128CellX(final float x) {
		final float userCellSpaceX = (x - this.centerOffset[0]) / 128.0f;
		final int cellX = (int) userCellSpaceX;
		return cellX;
	}

	@Override
	public float get128WorldCoordinateFromCellX(final int cellX) {
		return (cellX * 128.0f) + this.centerOffset[0];
	}

	@Override
	public int get128CellY(final float y) {
		final float userCellSpaceY = (y - this.centerOffset[1]) / 128.0f;
		final int cellY = (int) userCellSpaceY;
		return cellY;
	}

	@Override
	public float get128WorldCoordinateFromCellY(final int cellY) {
		return (cellY * 128.0f) + this.centerOffset[1];
	}

	@Override
	public RenderCorner getCorner(final float x, final float y) {
		for (final TerrainInterface terrain : this.terrains) {
			if (terrain.inPlayableArea(x, y)) {
				return terrain.getCorner(x, y);
			}
		}
		return null;
	}

	@Override
	public void updateGroundTextures(final Rectangle blightRectangleCellUnits) {
		for (final TerrainInterface terrain : this.terrains) {
			terrain.updateGroundTextures(blightRectangleCellUnits);
		}
	}

	@Override
	public Rectangle getEntireMap() {
		return this.entireMapRectangle;
	}

	@Override
	public void createWaves() {
		for (final TerrainInterface terrain : this.terrains) {
			terrain.createWaves();
		}
	}

	@Override
	public void update(final float deltaTime) {
		for (final TerrainInterface terrain : this.terrains) {
			terrain.update(deltaTime);
		}
	}

	@Override
	public void renderGround(final DynamicShadowManager dynamicShadowManager) {
		for (final TerrainInterface terrain : this.terrains) {
			terrain.renderGround(dynamicShadowManager);
		}
	}

	@Override
	public void renderCliffs() {
		for (final TerrainInterface terrain : this.terrains) {
			terrain.renderCliffs();
		}
	}

	@Override
	public void renderUberSplats(final boolean onTopLayer) {
		for (final TerrainInterface terrain : this.terrains) {
			terrain.renderUberSplats(onTopLayer);
		}
	}

	@Override
	public void renderWater() {
		for (final TerrainInterface terrain : this.terrains) {
			terrain.renderWater();
		}
	}

	@Override
	public SplatMover addUnitShadowSplat(final String texture, final float x, final float y, final float x2,
			final float y2, final float zDepthUpward, final float opacity, final boolean aboveWater) {
		for (final TerrainInterface terrain : this.terrains) {
			if (terrain.inPlayableArea(x, y)) {
				terrain.addUnitShadowSplat(texture, x, y, x2, y2, zDepthUpward, opacity, aboveWater);
			}
		}
		return null;
	}

	@Override
	public SplatMover addUberSplat(final String path, final float x, final float y, final float z, final float scale,
			final boolean unshaded, final boolean noDepthTest, final boolean highPriority, final boolean aboveWater) {
		for (final TerrainInterface terrain : this.terrains) {
			if (terrain.inPlayableArea(x, y)) {
				terrain.addUberSplat(path, x, y, z, scale, unshaded, noDepthTest, highPriority, aboveWater);
			}
		}
		return null;
	}

	@Override
	public void removeSplatBatchModel(final String key) {
		for (final TerrainInterface terrain : this.terrains) {
			terrain.removeSplatBatchModel(key);
		}
	}

	@Override
	public void addSplatBatchModel(final String string, final SplatModel model) {
		for (final TerrainInterface terrain : this.terrains) {
			terrain.addSplatBatchModel(string, model);
		}
	}

	@Override
	public float getWaterHeight(final float x, final float y) {
		for (final TerrainInterface terrain : this.terrains) {
			if (terrain.inPlayableArea(x, y)) {
				return terrain.getWaterHeight(x, y);
			}
		}
		return NO_TERRAIN_HEIGHT;
	}

	@Override
	public Rectangle getPlayableMapArea() {
		Rectangle map = new Rectangle();
		for (final TerrainInterface terrain : this.terrains) {
			final Rectangle entireSubMap = terrain.getPlayableMapArea();
			map = map.merge(entireSubMap);
		}
		return map;
	}

	@Override
	public float[] getDefaultCameraBounds() {
		return this.terrains.get(0).getDefaultCameraBounds();
	}

	@Override
	public void setWaterBaseColor(final float red, final float green, final float blue, final float alpha) {
		for (final TerrainInterface terrain : this.terrains) {
			terrain.setWaterBaseColor(red, green, blue, alpha);
		}
	}

	@Override
	public boolean inPlayableArea(final float x, final float y) {
		for (final TerrainInterface terrain : this.terrains) {
			if (terrain.inPlayableArea(x, y)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean inActivePlayableArea(final float x, final float y) {
		for (final TerrainInterface terrain : this.terrains) {
			if (terrain.inPlayableArea(x, y)) {
				return terrain.inActivePlayableArea(x, y);
			}
		}
		return getEntireMap().contains(x, y);
	}

	@Override
	public boolean intersectRayTerrain(final Ray gdxRayHeap, final Vector3 out, final boolean intersectWithWater) {
		for (final TerrainInterface terrain : this.terrains) {
			if (terrain.intersectRayTerrain(gdxRayHeap, out, intersectWithWater)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public WdtLiquidType getLiquidType(final float x, final float y) {
		for (final TerrainInterface terrain : this.terrains) {
			if (terrain.inActivePlayableArea(x, y)) {
				return terrain.getLiquidType(x, y);
			}
		}
		return WdtLiquidType.OCEAN;
	}

	@Override
	public int[] getTerrainModBufferSize(final float x, final float y, final float width, final float height) {
		for (final TerrainInterface terrain : this.terrains) {
			if (terrain.inActivePlayableArea(x, y)) {
				return terrain.getTerrainModBufferSize(x, y, width, height);
			}
		}
		return new int[] { Math.min(this.rows - 1, Math.max(0, get128CellX(x))),
				Math.min(this.columns - 1, Math.max(0, get128CellY(y))),
				Math.min(this.rows - 1, Math.max(0, get128CellX(x + width) + 1)),
				Math.min(this.columns - 1, Math.max(0, get128CellY(y + height) + 1)) };
	}

	@Override
	public int[] getTerrainModBufferSize(final float centerX, final float centerY, final float radius) {
		return getTerrainModBufferSize(centerX - radius, centerY - radius, radius * 2, radius * 2);
	}

	@Override
	public void updateGroundBuffer(final int[] rect, final float[] modBuffer) {
		for (final TerrainInterface terrain : this.terrains) {
			terrain.updateGroundBuffer(rect, modBuffer);
		}
	}

	@Override
	public void updateGroundBuffer(final float x, final float y, final float i) {
		for (final TerrainInterface terrain : this.terrains) {
			if (terrain.inActivePlayableArea(x, y)) {
				terrain.updateGroundBuffer(x, y, i);
			}
		}
		for (final TerrainInterface terrain : this.terrains) {
			terrain.updateGroundBuffer(x, y, i);
		}
	}

	@Override
	public float getTerrainSpaceX(final float x) {
		return (x - this.centerOffset[0]) / 128.0f;
	}

	@Override
	public float getTerrainSpaceY(final float y) {
		return (y - this.centerOffset[1]) / 128.0f;
	}

	@Override
	public void setWdtHole(final float worldX, final float worldY, final boolean hole) {
		for (final TerrainInterface terrain : this.terrains) {
			if (terrain.inActivePlayableArea(worldX, worldY)) {
				terrain.setWdtHole(worldX, worldY, hole);
			}
		}
	}

	public boolean isInside(final float worldX, final float worldY, final int terrainIdx) {
		if ((terrainIdx < 0) || (terrainIdx >= this.terrainDatas.length)) {
			return false;
		}
		return this.terrains.get(terrainIdx).inPlayableArea(worldX, worldY);
	}

}
