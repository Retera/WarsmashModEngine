package com.etheller.warsmash.viewer5.handlers.w3x.environment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.badlogic.gdx.math.Rectangle;
import com.etheller.warsmash.units.DataTable;
import com.etheller.warsmash.viewer5.handlers.w3x.DynamicShadowManager;
import com.etheller.warsmash.viewer5.handlers.w3x.SplatModel;
import com.etheller.warsmash.viewer5.handlers.w3x.SplatModel.SplatMover;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CFogMaskSettings;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.vision.CPlayerFogOfWar;

public abstract class TerrainInterface {
	public PathingGrid pathingGrid;
	public DataTable uberSplatTable;
	public float[] centerOffset;
	public final Map<String, Splat> splats = new HashMap<>();
	public SoftwareGroundMesh softwareGroundMesh;
	public SoftwareWaterAndGroundMesh softwareWaterAndGroundMesh;

	public abstract void initShadows() throws IOException;

	public abstract void setFogOfWarData(final CFogMaskSettings fogMaskSettings, CPlayerFogOfWar fogOfWar);

	public abstract void reloadFogOfWarDataToGPU(final CFogMaskSettings fogMaskSettings);

	public abstract void removeTerrainCellWithoutFlush(int i, int j);

	public abstract void flushRemovedTerrainCells();

	public abstract BuildingShadow addShadow(String shadowString, float x, float y);

	public abstract float getGroundHeight(float unitX, float unitY);

	public abstract void loadSplats() throws IOException;

	public abstract SplatModel getSplatModel(String string);

	public abstract int get128CellX(float whichLocationX);

	public abstract int get128CellY(float whichLocationY);

	public abstract float get128WorldCoordinateFromCellX(int cellX);

	public abstract float get128WorldCoordinateFromCellY(int cellY);

	public abstract RenderCorner getCorner(float x, float y);

	public abstract void updateGroundTextures(Rectangle blightRectangleCellUnits);

	public abstract Rectangle getEntireMap();

	public abstract void createWaves();

	public abstract void update(float deltaTime);

	public abstract void renderGround(DynamicShadowManager dynamicShadowManager);

	public abstract void renderCliffs();

	public abstract void renderUberSplats(boolean onTopLayer);

	public abstract void renderWater();

	public abstract SplatMover addUnitShadowSplat(final String texture, final float x, final float y, final float x2,
			final float y2, final float zDepthUpward, final float opacity, final boolean aboveWater);

	public abstract SplatMover addUberSplat(final String path, final float x, final float y, final float z,
			final float scale, final boolean unshaded, final boolean noDepthTest, final boolean highPriority,
			final boolean aboveWater);

	public abstract void removeSplatBatchModel(String key);

	public abstract void addSplatBatchModel(String string, SplatModel model);

	public abstract float getWaterHeight(float x, float y);

	public abstract Rectangle getPlayableMapArea();

	public abstract float[] getDefaultCameraBounds();

	public abstract void setWaterBaseColor(final float red, final float green, final float blue, final float alpha);

	public abstract boolean inPlayableArea(float x, float y);

	public static final class SoftwareGroundMesh {
		public final float[] vertices;
		public final int[] indices;

		protected SoftwareGroundMesh(final float[] groundHeights, final float[] groundCornerHeights,
				final float[] centerOffset, final int columns, final int rows) {
			this.vertices = new float[(columns - 1) * (rows - 1) * Shapes.INSTANCE.quadVertices.length * 3];
			this.indices = new int[(columns - 1) * (rows - 1) * Shapes.INSTANCE.quadIndices.length * 3];
			for (int y = 0; y < (rows - 1); y++) {
				for (int x = 0; x < (columns - 1); x++) {
					final int instanceId = (y * (columns - 1)) + x;
					for (int vertexId = 0; vertexId < Shapes.INSTANCE.quadVertices.length; vertexId++) {
						final float vPositionX = Shapes.INSTANCE.quadVertices[vertexId][0];
						final float vPositionY = Shapes.INSTANCE.quadVertices[vertexId][1];
						final int groundCornerHeightIndex = (int) (((vPositionY + y) * columns) + (vPositionX + x));
						final float height = groundCornerHeights[groundCornerHeightIndex];
						this.vertices[(instanceId * 4 * 3) + (vertexId * 3)] = ((vPositionX + x) * 128f)
								+ centerOffset[0];
						this.vertices[(instanceId * 4 * 3) + (vertexId * 3) + 1] = ((vPositionY + y) * 128f)
								+ centerOffset[1];
						this.vertices[(instanceId * 4 * 3) + (vertexId * 3) + 2] = height * 128f;
					}
					for (int triangle = 0; triangle < Shapes.INSTANCE.quadIndices.length; triangle++) {
						for (int vertexId = 0; vertexId < Shapes.INSTANCE.quadIndices[triangle].length; vertexId++) {
							final int vertexIndex = Shapes.INSTANCE.quadIndices[triangle][vertexId];
							final int indexValue = vertexIndex + (instanceId * 4);
							if ((indexValue * 3) >= this.vertices.length) {
								throw new IllegalStateException();
							}
							this.indices[(instanceId * 2 * 3) + (triangle * 3) + vertexId] = indexValue;
						}
					}
				}
			}
		}
	}

	public static final class SoftwareWaterAndGroundMesh {
		public final float[] vertices;
		public final int[] indices;

		protected SoftwareWaterAndGroundMesh(final float waterHeightOffset, final float[] groundCornerHeights,
				final float[] waterHeights, final byte[] waterExistsData, final float[] centerOffset, final int columns,
				final int rows) {
			this.vertices = new float[(columns - 1) * (rows - 1) * Shapes.INSTANCE.quadVertices.length * 3];
			this.indices = new int[(columns - 1) * (rows - 1) * Shapes.INSTANCE.quadIndices.length * 3];
			for (int y = 0; y < (rows - 1); y++) {
				for (int x = 0; x < (columns - 1); x++) {
					final int instanceId = (y * (columns - 1)) + x;
					for (int vertexId = 0; vertexId < Shapes.INSTANCE.quadVertices.length; vertexId++) {
						final float vPositionX = Shapes.INSTANCE.quadVertices[vertexId][0];
						final float vPositionY = Shapes.INSTANCE.quadVertices[vertexId][1];
						final int groundCornerHeightIndex = (int) (((vPositionY + y) * columns) + (vPositionX + x));
						final float height;
						if (waterExistsData[groundCornerHeightIndex] != 0) {
							height = Math.max(groundCornerHeights[groundCornerHeightIndex],
									waterHeights[groundCornerHeightIndex] + waterHeightOffset);
						}
						else {
							height = groundCornerHeights[groundCornerHeightIndex];
						}
						this.vertices[(instanceId * 4 * 3) + (vertexId * 3)] = ((vPositionX + x) * 128f)
								+ centerOffset[0];
						this.vertices[(instanceId * 4 * 3) + (vertexId * 3) + 1] = ((vPositionY + y) * 128f)
								+ centerOffset[1];
						this.vertices[(instanceId * 4 * 3) + (vertexId * 3) + 2] = height * 128f;
					}
					for (int triangle = 0; triangle < Shapes.INSTANCE.quadIndices.length; triangle++) {
						for (int vertexId = 0; vertexId < Shapes.INSTANCE.quadIndices[triangle].length; vertexId++) {
							final int vertexIndex = Shapes.INSTANCE.quadIndices[triangle][vertexId];
							final int indexValue = vertexIndex + (instanceId * 4);
							if ((indexValue * 3) >= this.vertices.length) {
								throw new IllegalStateException();
							}
							this.indices[(instanceId * 2 * 3) + (triangle * 3) + vertexId] = indexValue;
						}
					}
				}
			}
		}
	}

	public static final class Splat {
		public List<float[]> locations = new ArrayList<>();
		public List<Consumer<SplatMover>> unitMapping = new ArrayList<>();
		public float opacity = 1;
		public boolean aboveWater = false;
	}
}
