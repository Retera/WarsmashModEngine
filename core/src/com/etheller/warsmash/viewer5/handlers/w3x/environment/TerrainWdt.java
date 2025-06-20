package com.etheller.warsmash.viewer5.handlers.w3x.environment;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.etheller.warsmash.datasources.DataSource;
import com.etheller.warsmash.parsers.w3x.w3e.War3MapW3e;
import com.etheller.warsmash.parsers.w3x.w3i.War3MapW3i;
import com.etheller.warsmash.parsers.w3x.wpm.War3MapWpm;
import com.etheller.warsmash.parsers.wdt.Chunk;
import com.etheller.warsmash.parsers.wdt.Chunk.Vector3b;
import com.etheller.warsmash.parsers.wdt.ChunkInfo;
import com.etheller.warsmash.parsers.wdt.DoodadDefinition;
import com.etheller.warsmash.parsers.wdt.MapChunkLayer;
import com.etheller.warsmash.parsers.wdt.MapChunkLiquidLayer;
import com.etheller.warsmash.parsers.wdt.MapChunkLiquidLayer.SOVert;
import com.etheller.warsmash.parsers.wdt.MapChunkLiquidLayer.SWVert;
import com.etheller.warsmash.parsers.wdt.WdtMap;
import com.etheller.warsmash.parsers.wdt.WdtMap.TileHeader;
import com.etheller.warsmash.units.DataTable;
import com.etheller.warsmash.units.Element;
import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.util.ImageUtils;
import com.etheller.warsmash.util.ImageUtils.AnyExtensionImage;
import com.etheller.warsmash.util.RenderMathUtils;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.util.WorldEditStrings;
import com.etheller.warsmash.viewer5.Camera;
import com.etheller.warsmash.viewer5.Model;
import com.etheller.warsmash.viewer5.ModelInstance;
import com.etheller.warsmash.viewer5.ModelViewer;
import com.etheller.warsmash.viewer5.PathSolver;
import com.etheller.warsmash.viewer5.RawOpenGLTextureResource;
import com.etheller.warsmash.viewer5.RenderBatch;
import com.etheller.warsmash.viewer5.Scene;
import com.etheller.warsmash.viewer5.Texture;
import com.etheller.warsmash.viewer5.TextureMapper;
import com.etheller.warsmash.viewer5.gl.DataTexture;
import com.etheller.warsmash.viewer5.gl.Extensions;
import com.etheller.warsmash.viewer5.gl.WebGL;
import com.etheller.warsmash.viewer5.handlers.ModelHandler;
import com.etheller.warsmash.viewer5.handlers.w3x.DynamicShadowManager;
import com.etheller.warsmash.viewer5.handlers.w3x.SplatModel;
import com.etheller.warsmash.viewer5.handlers.w3x.SplatModel.SplatMover;
import com.etheller.warsmash.viewer5.handlers.w3x.W3xSceneLightManager;
import com.etheller.warsmash.viewer5.handlers.w3x.W3xShaders;
import com.etheller.warsmash.viewer5.handlers.w3x.War3MapViewer;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.RenderDoodad;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CFogMaskSettings;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.vision.CPlayerFogOfWar;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.vision.CPlayerFogOfWarInterface;

public class TerrainWdt extends TerrainInterface {
	private static final int LOAD_RADIUS = 1;
	public static final float CELL_SIZE = 128f;
	private static final String[] colorTags = { "R", "G", "B", "A" };
	private static final float[] sizeHeap = new float[2];
	private static final Vector3 normalHeap1 = new Vector3();
	private static final Vector3 normalHeap2 = new Vector3();
	private static final float[] fourComponentHeap = new float[4];
	private static final Matrix4 tempMatrix = new Matrix4();
	public static boolean WIREFRAME_TERRAIN = false;
	// In WC3 they didn't finish developing the height 3 ramps
	// There are a couple of models for some of them but generally they are just bad
	// voodoo. Enabling this setting should be coupled with creating
	// new artwork for advanced ramp use cases that don't exist in WC3.
	private static final boolean DISALLOW_HEIGHT_3_RAMPS = true;

	public ShaderProgram groundShader;
	public ShaderProgram waterShader;
	public ShaderProgram cliffShader;
	public float waterIndex;
	public float waterIncreasePerFrame;
	public float waterHeightOffset;

	//
	public List<UnloadedTexture> cliffTextures = new ArrayList<>();
	public int columns;
	public int rows;
	public float[] maxDeepColor = new float[4];
	public float[] minDeepColor = new float[4];
	public float[] maxShallowColor = new float[4];
	public float[] minShallowColor = new float[4];
	public float[] maxDeepColorApplied = new float[4];
	public float[] minDeepColorApplied = new float[4];
	public float[] maxShallowColorApplied = new float[4];
	public float[] minShallowColorApplied = new float[4];

	private final DataTable terrainTable;
	private final DataTable cliffTable;
	private final DataTable waterTable;
	private final int waterTextureCount;
	private int cliffTexturesSize;
	private final List<CliffMesh> cliffMeshes = new ArrayList<>();
	private final Map<String, Integer> pathToCliff = new HashMap<>();
	private final List<Integer> cliffToGroundTexture = new ArrayList<>();
	private final List<IVec3> cliffs = new ArrayList<>();
	private final DataSource dataSource;

	private final Camera camera;
	private final War3MapViewer viewer;
	private final WebGL webGL;
	private final ShaderProgram uberSplatShader;

	private final Map<String, SplatModel> uberSplatModels;
	private final List<SplatModel> uberSplatModelsList;
	private int fogOfWarMap;
	public final Map<String, List<float[]>> shadows = new HashMap<>();
	public final Map<String, Texture> shadowTextures = new HashMap<>();
	private final int[] mapBounds;
	private final float[] shaderMapBounds;
	private final int[] mapSize;
	private CPlayerFogOfWar fogOfWarData;
	private ByteBuffer visualFogData;
	private final Rectangle shaderMapBoundsRectangle;
	private final Rectangle entireMapRectangle;
	private final float[] defaultCameraBounds;
	private final GroundTexture blightTexture;

	public final Map<TileHeader, List<War3ID>> tileHeaderToDoodadIds = new HashMap<>();
	private int lastCameraCellX;
	private int lastCameraCellY;

	private final Map<String, GroundTextureWdt> pathToTexture = new HashMap<>();
	private final Map<AlphaMapData, AlphaMapDataValue> alphaDataToId = new HashMap<>();
	private final WorldGrid worldGrid;
	private final List<Tile> activeTiles = new ArrayList<>();
	private final Tile[][] tiles;
	private final int waterTextureArray;

	private final ArrayDeque<DynamicTask> tasks = new ArrayDeque<>();

	public TerrainWdt(final WdtMap map, final War3MapW3e w3eFile, final War3MapWpm terrainPathing,
			final War3MapW3i w3iFile, final WebGL webGL, final DataSource dataSource,
			final WorldEditStrings worldEditStrings, final War3MapViewer viewer, final DataTable worldEditData)
			throws IOException {
		this.webGL = webGL;
		this.viewer = viewer;
		this.camera = viewer.worldScene.camera;
		this.dataSource = dataSource;
		final String texturesExt = ".blp";
		final int width = w3eFile.getMapSize()[0];
		final int height = w3eFile.getMapSize()[1];
		this.columns = width;
		this.rows = height;

		System.out.println("reading SLKs for terrain");
		this.terrainTable = new DataTable(worldEditStrings);
		try (InputStream terrainSlkStream = dataSource.getResourceAsStream("TerrainArt\\Terrain.slk")) {
			this.terrainTable.readSLK(terrainSlkStream);
		}
		this.cliffTable = new DataTable(worldEditStrings);
		try (InputStream cliffSlkStream = dataSource.getResourceAsStream("TerrainArt\\CliffTypes.slk")) {
			this.cliffTable.readSLK(cliffSlkStream);
		}
		this.waterTable = new DataTable(worldEditStrings);
		try (InputStream waterSlkStream = dataSource.getResourceAsStream("TerrainArt\\Water.slk")) {
			this.waterTable.readSLK(waterSlkStream);
		}
		this.uberSplatTable = new DataTable(worldEditStrings);
		try (InputStream uberSlkStream = dataSource.getResourceAsStream("Splats\\UberSplatData.slk")) {
			this.uberSplatTable.readSLK(uberSlkStream);
		}

		System.out.println("reading water info");
		final char tileset = w3eFile.getTileset();
		final Element waterInfo = this.waterTable.get(tileset + "Sha");
		if (waterInfo != null) {
			this.waterHeightOffset = waterInfo.getFieldFloatValue("height");
			this.waterTextureCount = waterInfo.getFieldValue("numTex");
			this.waterIncreasePerFrame = waterInfo.getFieldValue("texRate");
		}
		else {
			this.waterHeightOffset = 0;
			this.waterTextureCount = 0;
			this.waterIncreasePerFrame = 0;
		}

		loadWaterColor(this.minShallowColor, "Smin", waterInfo);
		loadWaterColor(this.maxShallowColor, "Smax", waterInfo);
		loadWaterColor(this.minDeepColor, "Dmin", waterInfo);
		loadWaterColor(this.maxDeepColor, "Dmax", waterInfo);
		for (int i = 0; i < 3; i++) {
			if (this.minDeepColor[i] > this.maxDeepColor[i]) {
				this.maxDeepColor[i] = this.minDeepColor[i];
			}
		}
		setWaterBaseColor(1.0f, 1.0f, 1.0f, 1.0f);

		// Cliff Meshes

		final Element tilesets = worldEditData.get("TileSets");

		this.blightTexture = new GroundTexture(
				tilesets.getField(Character.toString(tileset)).split(",")[1] + texturesExt, null, dataSource, Gdx.gl30);

		// Cliff Textures

		System.out.println("updating heights");

		final GL30 gl = Gdx.gl30;

		// Water textures
		this.waterTextureArray = gl.glGenTexture();
		gl.glBindTexture(GL30.GL_TEXTURE_2D_ARRAY, this.waterTextureArray);

		if (waterInfo != null) {
			final String fileName = waterInfo.getField("texFile");
			final List<BufferedImage> waterTextures = new ArrayList<>();
			boolean anyWaterTextureNeedsSRGB = false;
			int waterImageDimension = 128;
			for (int i = 0; i < this.waterTextureCount; i++) {
				final AnyExtensionImage imageInfo = ImageUtils.getAnyExtensionImageFixRGB(dataSource,
						fileName + (i < 10 ? "0" : "") + Integer.toString(i) + texturesExt, "water texture");
				final BufferedImage image = imageInfo.getImageData();
				if ((image.getWidth() != 128) || (image.getHeight() != 128)) {
					System.err.println(
							"Odd water texture size detected of " + image.getWidth() + " x " + image.getHeight());
					waterImageDimension = image.getWidth();
				}
				anyWaterTextureNeedsSRGB |= imageInfo.isNeedsSRGBFix();
				waterTextures.add(image);
			}
			gl.glTexImage3D(GL30.GL_TEXTURE_2D_ARRAY, 0,
					anyWaterTextureNeedsSRGB ? GL30.GL_SRGB8_ALPHA8 : GL30.GL_RGBA8, waterImageDimension,
					waterImageDimension, this.waterTextureCount, 0, GL30.GL_RGBA, GL30.GL_UNSIGNED_BYTE, null);
			gl.glTexParameteri(GL30.GL_TEXTURE_2D_ARRAY, GL30.GL_TEXTURE_WRAP_S, GL30.GL_CLAMP_TO_EDGE);
			gl.glTexParameteri(GL30.GL_TEXTURE_2D_ARRAY, GL30.GL_TEXTURE_WRAP_T, GL30.GL_CLAMP_TO_EDGE);
			gl.glTexParameteri(GL30.GL_TEXTURE_2D_ARRAY, GL30.GL_TEXTURE_BASE_LEVEL, 0);

			for (int i = 0; i < waterTextures.size(); i++) {
				final BufferedImage image = waterTextures.get(i);
				gl.glTexSubImage3D(GL30.GL_TEXTURE_2D_ARRAY, 0, 0, 0, i, image.getWidth(), image.getHeight(), 1,
						GL30.GL_RGBA, GL30.GL_UNSIGNED_BYTE, ImageUtils.getTextureBuffer(image));
			}
		}

		gl.glGenerateMipmap(GL30.GL_TEXTURE_2D_ARRAY);

		System.out.println("creating shaders");
		this.groundShader = webGL.createShaderProgram(TerrainShaders.TerrainWdt.vert(), TerrainShaders.TerrainWdt.frag);
		this.cliffShader = webGL.createShaderProgram(TerrainShaders.Cliffs.vert(), TerrainShaders.Cliffs.frag);
		this.waterShader = webGL.createShaderProgram(TerrainShaders.WaterWdt.vert(), TerrainShaders.WaterWdt.frag);

		this.uberSplatShader = webGL.createShaderProgram(W3xShaders.UberSplat.vert(), W3xShaders.UberSplat.frag);

		// TODO collision bodies (?)

		this.centerOffset = w3eFile.getCenterOffset();
		this.uberSplatModels = new LinkedHashMap<>();
		this.uberSplatModelsList = new ArrayList<>();
		this.defaultCameraBounds = w3iFile.getCameraBounds();
		this.mapBounds = w3iFile.getCameraBoundsComplements();
		this.shaderMapBounds = new float[] { (this.mapBounds[0] * 128.0f) + this.centerOffset[0],
				(this.mapBounds[2] * 128.0f) + this.centerOffset[1],
				((this.columns - this.mapBounds[1] - 1) * 128.0f) + this.centerOffset[0],
				((this.rows - this.mapBounds[3] - 1) * 128.0f) + this.centerOffset[1] };
		this.shaderMapBoundsRectangle = new Rectangle(this.shaderMapBounds[0], this.shaderMapBounds[1],
				this.shaderMapBounds[2] - this.shaderMapBounds[0], this.shaderMapBounds[3] - this.shaderMapBounds[1]);
		this.mapSize = w3eFile.getMapSize();
		this.entireMapRectangle = new Rectangle(this.centerOffset[0], this.centerOffset[1],
				(this.mapSize[0] * 128f) - 128, (this.mapSize[1] * 128f) - 128);
		System.out.println("creating software ground mesh");
		// TODO
//		this.softwareGroundMesh = new SoftwareGroundMesh(this.groundCornerHeights, this.centerOffset, width, height);
//		this.softwareWaterAndGroundMesh = new SoftwareWaterAndGroundMesh(this.waterHeightOffset,
//				this.groundCornerHeights, this.waterHeights, this.waterExistsData, this.centerOffset, width, height);

//		gl.glBindBuffer(GL30.GL_ELEMENT_ARRAY_BUFFER, this.testElementBuffer);
//		gl.glBufferData(GL30.GL_ELEMENT_ARRAY_BUFFER, this.softwareGroundMesh.indices.length,
//				RenderMathUtils.wrap(this.softwareGroundMesh.indices), GL30.GL_STATIC_DRAW);

		System.out.println("creating wave builder");
		System.out.println("creating pathing grid");
		this.pathingGrid = new PathingGrid(terrainPathing, this.centerOffset);

		System.out.println("Spawning tiles");
		this.tiles = new Tile[64][64];

		System.out.println("Beginning to load chunk models");
		final int totalTileHeaderCount = map.tileHeaders.size();
		int currentTileHeaderIndex = 0;
		this.worldGrid = new WorldGrid(this.centerOffset,
				WdtChunkModelInstance.tilesize * WdtChunkModelInstance.wowToWc3Factor);
		for (final TileHeader tileHeader : map.tileHeaders) {

			final WdtChunkModel wdtChunkModel = new WdtChunkModel(null, viewer, "", PathSolver.DEFAULT, "", tileHeader);
			wdtChunkModel.load(null, null);
			final Tile tile = new Tile(tileHeader, wdtChunkModel, this.worldGrid.getCornerX(wdtChunkModel.blockX),
					this.worldGrid.getCornerY(wdtChunkModel.blockY));
			this.tiles[wdtChunkModel.blockX][wdtChunkModel.blockY] = tile;
			System.out.println("Created chunk models for " + currentTileHeaderIndex + " / " + totalTileHeaderCount);
			currentTileHeaderIndex++;
		}
		System.out.println("Ending loading chunk models");

	}

	@Override
	public void intersectRayTerrain(final Ray gdxRayHeap, final Vector3 out, final boolean intersectWithWater) {
//		out.set(gdxRayHeap.origin);
//		out.add(gdxRayHeap.direction);
		for (final Tile tile : this.activeTiles) {
			if (tile.activeTile != null) {
				if (tile.activeTile.intersectRayTerrain(gdxRayHeap, out, intersectWithWater)) {
					return;
				}
			}
		}
	}

	@Override
	public void createWaves() {
	}

	@Override
	public void updateGroundTextures(final Rectangle area) {
	}

	@Override
	public void removeTerrainCellWithoutFlush(final int i, final int j) {
	}

	@Override
	public void flushRemovedTerrainCells() {
	}

	private static void loadWaterColor(final float[] out, final String prefix, final Element waterInfo) {
		for (int i = 0; i < colorTags.length; i++) {
			final String colorTag = colorTags[i];
			out[i] = waterInfo == null ? 0.0f : waterInfo.getFieldFloatValue(prefix + "_" + colorTag) / 255f;
		}
	}

	public short getVariation(final int groundTexture, final int variation) {
		return 0; // TODO
	}

	@Override
	public void update(final float deltaTime) {
		this.waterIndex += this.waterIncreasePerFrame * deltaTime;

		if (this.waterIndex >= this.waterTextureCount) {
			this.waterIndex = 0;
		}

		final Vector3 cameraLocation = this.camera.location;
		final int cellX = this.worldGrid.getCellX(cameraLocation.x);
		final int cellY = this.worldGrid.getCellY(cameraLocation.y);
		this.lastCameraCellX = cellX;
		this.lastCameraCellY = cellY;
		for (int k = this.activeTiles.size() - 1; k >= 0; k--) {
			final Tile tile = this.activeTiles.get(k);
			final int dx = Math.abs(tile.terrainModel.blockX - cellX);
			final int dy = Math.abs(tile.terrainModel.blockY - cellY);
			if ((dx > LOAD_RADIUS) || (dy > LOAD_RADIUS)) {
				tile.deactivate();
				this.activeTiles.remove(k);
			}
		}
		for (int i = -LOAD_RADIUS; i <= LOAD_RADIUS; i++) {
			for (int j = -LOAD_RADIUS; j <= LOAD_RADIUS; j++) {
				final int x = cellX + i;
				final int y = cellY + j;

				if ((x >= 0) && (x < this.tiles.length)) {
					if ((y >= 0) && (y < this.tiles[x].length)) {
						final Tile tile = this.tiles[x][y];
						if (tile != null) {
							if (tile.activate()) {
								this.activeTiles.add(this.tiles[x][y]);
							}
						}
					}
				}
			}
		}

		if (!this.tasks.isEmpty()) {
			final DynamicTask nextTask = this.tasks.peek();
			if (nextTask.run()) {
				this.tasks.poll();
			}
			if (this.tasks.isEmpty()) {
				System.out.println("finished all tasks for now!!");
			}
		}
	}

	@Override
	public void renderGround(final DynamicShadowManager dynamicShadowManager) {
	}

	@Override
	public void renderUberSplats(final boolean onTopLayer) {
		final Tile tile = getCurrentTile();
		if ((tile == null) || (tile.activeTile == null)) {
			return;
		}
		final GL30 gl = Gdx.gl30;
		final WebGL webGL = this.webGL;
		final ShaderProgram shader = this.uberSplatShader;

		gl.glDepthMask(false);
		gl.glEnable(GL30.GL_BLEND);
		gl.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);
		gl.glBlendEquation(GL30.GL_FUNC_ADD);

		webGL.useShaderProgram(this.uberSplatShader);

		shader.setUniformMatrix("u_mvp", this.camera.viewProjectionMatrix);
		shader.setUniformi("u_heightMap", 0);
		sizeHeap[0] = this.columns - 1;
		sizeHeap[1] = this.rows - 1;
		shader.setUniform2fv("u_size", sizeHeap, 0, 2);
		sizeHeap[0] = 1 / (float) this.columns;
		sizeHeap[1] = 1 / (float) this.rows;
		shader.setUniform2fv("u_pixel", sizeHeap, 0, 2);
		shader.setUniform2fv("u_centerOffset", tile.myCornerXY, 0, 2);
		shader.setUniformi("u_texture", 1);
		shader.setUniformi("u_shadowMap", 2);
//		shader.setUniformi("u_waterHeightsMap", 3);
		shader.setUniformi("u_fogOfWarMap", 4);
		shader.setUniformf("u_waterHeightOffset", 0);// this.waterHeightOffset);

		gl.glActiveTexture(GL30.GL_TEXTURE0);
		gl.glBindTexture(GL30.GL_TEXTURE_2D, tile.activeTile.groundCornerHeightLinear);

		gl.glActiveTexture(GL30.GL_TEXTURE2);
		gl.glBindTexture(GL30.GL_TEXTURE_2D, tile.activeTile.shadowMap);

//		gl.glActiveTexture(GL30.GL_TEXTURE3);
//		gl.glBindTexture(GL30.GL_TEXTURE_2D, this.waterHeight);

		gl.glActiveTexture(GL30.GL_TEXTURE4);
		gl.glBindTexture(GL30.GL_TEXTURE_2D, this.fogOfWarMap);

		final W3xSceneLightManager lightManager = (W3xSceneLightManager) this.viewer.worldScene.getLightManager();
		final DataTexture terrainLightsTexture = lightManager.getTerrainLightsTexture();

		terrainLightsTexture.bind(21);
		gl.glUniform1i(shader.getUniformLocation("u_lightTexture"), 21);
		gl.glUniform1f(shader.getUniformLocation("u_lightCount"), lightManager.getTerrainLightCount());
		gl.glUniform1f(shader.getUniformLocation("u_lightTextureHeight"), terrainLightsTexture.getHeight());
		shader.setUniformf("u_fogColor", this.viewer.worldScene.fogSettings.color);
		shader.setUniformf("u_fogParams", this.viewer.worldScene.fogSettings.style.ordinal(),
				this.viewer.worldScene.fogSettings.start, this.viewer.worldScene.fogSettings.end,
				this.viewer.worldScene.fogSettings.density);

		// Render the cliffs
		for (final SplatModel splat : this.uberSplatModelsList) {
			if (splat.isHighPriority() == onTopLayer) {
				splat.render(gl, shader);
			}
		}
	}

	private Tile getCurrentTile() {
		final Tile tile = this.tiles[this.lastCameraCellX][this.lastCameraCellY];
		return tile;
	}

	@Override
	public void renderWater() {
		for (final Tile tile : this.activeTiles) {
			if (tile.activeTile != null) {
				tile.activeTile.renderWater();
			}
		}
	}

	@Override
	public void renderCliffs() {
	}

	@Override
	public BuildingShadow addShadow(final String file, final float shadowX, final float shadowY) {
		if (!this.shadows.containsKey(file)) {
			final String path = "ReplaceableTextures\\Shadows\\" + file + ".blp";
			this.shadows.put(file, new ArrayList<>());
			this.shadowTextures.put(file, (Texture) this.viewer.load(path, PathSolver.DEFAULT, null));
		}
		final List<float[]> shadowList = this.shadows.get(file);
		final float[] shadowPositionArray = new float[] { shadowX, shadowY };
		shadowList.add(shadowPositionArray);
		final Tile tile = getCurrentTile();
		if ((tile != null) && (tile.activeTile != null)) {
			final Texture texture = this.shadowTextures.get(file);

			final int columns = (tile.activeTile.width - 1) * 4;
			final int rows = (tile.activeTile.height - 1) * 4;
			if (tile.activeTile.blitShadowData(columns, rows, shadowX, shadowY, texture)) {
				final GL30 gl = Gdx.gl30;
				gl.glBindTexture(GL30.GL_TEXTURE_2D, tile.activeTile.shadowMap);
				gl.glTexImage2D(GL30.GL_TEXTURE_2D, 0, GL30.GL_R8, columns, rows, 0, GL30.GL_RED, GL30.GL_UNSIGNED_BYTE,
						RenderMathUtils.wrap(tile.activeTile.shadowData));
			}
		}
		return new BuildingShadow() {
			@Override
			public void remove() {
				shadowList.remove(shadowPositionArray);
				final Tile tile = getCurrentTile();
				if ((tile != null) && (tile.activeTile != null)) {
					tile.activeTile.reloadShadowDataToGPU();
				}
			}

			@Override
			public void move(final float x, final float y) {
				shadowPositionArray[0] = x;
				shadowPositionArray[1] = y;
				final Tile tile = getCurrentTile();
				if ((tile != null) && (tile.activeTile != null)) {
					tile.activeTile.reloadShadowDataToGPU();
				}
			}
		};
	}

	@Override
	public void initShadows() throws IOException {
	}

//	public Vector3 groundNormal(final Vector3 out, int x, int y) {
//		final float[] centerOffset = this.centerOffset;
//		final int[] mapSize = this.mapSize;
//
//		x = (int) ((x - centerOffset[0]) / 128);
//		y = (int) ((y - centerOffset[1]) / 128);
//
//		final int cellX = x;
//		final int cellY = y;
//
//		// See if this coordinate is in the map
//
//		if ((cellX >= 0) && (cellX < (mapSize[0] - 1)) && (cellY >= 0) && (cellY < (mapSize[1] - 1))) {
//			// See http://gamedev.stackexchange.com/a/24574
//			final Corner[][] corners = this.corners;
//			final float bottomLeft = corners[cellY][cellX].getGroundHeight();
//			final float bottomRight = corners[cellY][cellX + 1].getGroundHeight();
//			final float topLeft = corners[cellY + 1][cellX].getGroundHeight();
//			final float topRight = corners[cellY + 1][cellX + 1].getGroundHeight();
//			final int sqX = x - cellX;
//			final int sqY = y - cellY;
//
//			if ((sqX + sqY) < 1) {
//				normalHeap1.set(1, 0, bottomRight - bottomLeft);
//				normalHeap2.set(0, 1, topLeft - bottomLeft);
//			}
//			else {
//				normalHeap1.set(-1, 0, topRight - topLeft);
//				normalHeap2.set(0, 1, topRight - bottomRight);
//			}
//
//			out.set(normalHeap1.crs(normalHeap2)).nor();
//		}
//		else {
//			out.set(0, 0, 1);
//		}
//
//		return out;
//	}

	private static final class UnloadedTexture {
		private final int width;
		private final int height;
		private final Buffer data;
		private final String cliffModelDir;
		private final String rampModelDir;

		public UnloadedTexture(final int width, final int height, final Buffer data, final String cliffModelDir,
				final String rampModelDir) {
			this.width = width;
			this.height = height;
			this.data = data;
			this.cliffModelDir = cliffModelDir;
			this.rampModelDir = rampModelDir;
		}

	}

	@Override
	public float getGroundHeight(final float x, final float y) {
		final float userCellSpaceXWc3 = (x - this.centerOffset[0]) / 128.0f;
		final float userCellSpaceYWc3 = (y - this.centerOffset[1]) / 128.0f;
		final int cellXWc3 = (int) userCellSpaceXWc3;
		final int cellYWc3 = (int) userCellSpaceYWc3;

		if ((cellXWc3 >= 0) && (cellXWc3 < (this.mapSize[0] - 1)) && (cellYWc3 >= 0)
				&& (cellYWc3 < (this.mapSize[1] - 1))) {
			final int worldGridCellX = this.worldGrid.getCellX(x);
			final int worldGridCellY = this.worldGrid.getCellY(y);
			if ((worldGridCellX >= 0) && (worldGridCellX < this.tiles.length)) {
				final Tile[] column = this.tiles[worldGridCellX];
				if ((worldGridCellY >= 0) && (worldGridCellY < column.length)) {
					final Tile tile = column[worldGridCellY];
					if (tile != null) {
						final float cornerX = this.worldGrid.getCornerX(worldGridCellX);
						final float cornerY = this.worldGrid.getCornerY(worldGridCellY);
						final float xWithinBlock = x - cornerX;
						final float yWithinBlock = y - cornerY;

						final float chunkSize = 128.0f * 8;
						final int chunkX = (int) (xWithinBlock / chunkSize);
						final int chunkY = (int) (yWithinBlock / chunkSize);
						final Chunk chunk = tile.chunks[chunkX][chunkY];
						if (chunk != null) {
							final float userCellSpaceX = (xWithinBlock - (chunkX * chunkSize)) / 128.0f;
							final float userCellSpaceY = (yWithinBlock - (chunkY * chunkSize)) / 128.0f;
							final int cellX = (int) userCellSpaceX;
							final int cellY = (int) userCellSpaceY;

							final float[][] heightMap = chunk.getHeightMap();
							final float bottomLeft = heightMap[8 - cellY][cellX]
									* WdtChunkModelInstance.wowToHiveWEFactor;
							final float bottomRight = heightMap[8 - cellY][cellX + 1]
									* WdtChunkModelInstance.wowToHiveWEFactor;
							final float topLeft = heightMap[8 - cellY - 1][cellX]
									* WdtChunkModelInstance.wowToHiveWEFactor;
							final float topRight = heightMap[8 - cellY - 1][cellX + 1]
									* WdtChunkModelInstance.wowToHiveWEFactor;
							final float center = heightMap[16 - cellY][cellX] * WdtChunkModelInstance.wowToHiveWEFactor;

							final float sqX = userCellSpaceXWc3 - cellXWc3;
							final float sqY = userCellSpaceYWc3 - cellYWc3;
							final float sqXinv = 1 - sqX;
							final float sqYinv = 1 - sqY;
							float height;

							if ((sqX + sqY) < 1) {
								if ((sqX + sqYinv) >= 1) {
									// bottom
									height = bottomLeft + ((bottomRight - bottomLeft) * sqX)
											+ (((center * 2) - bottomRight - bottomLeft) * sqY);
								}
								else {
									// left
									height = bottomLeft + (((center * 2) - topLeft - bottomLeft) * sqX)
											+ ((topLeft - bottomLeft) * sqY);
								}
							}
							else {
								if ((sqX + sqYinv) >= 1) {
									// right
									height = topRight + ((bottomRight - topRight) * sqYinv)
											+ (((center * 2) - bottomRight - topRight) * sqXinv);
								}
								else {
									// top
									height = topRight + (((center * 2) - topLeft - topRight) * sqYinv)
											+ ((topLeft - topRight) * sqXinv);
								}
							}

							return height * 128.0f;
						}
					}
				}
			}
		}

		return 0;
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
		return null;
	}

	@Override
	public float getWaterHeight(final float x, final float y) {
		/*
		 * final float userCellSpaceX = (x - this.centerOffset[0]) / 128.0f; final float
		 * userCellSpaceY = (y - this.centerOffset[1]) / 128.0f; final int cellX = (int)
		 * userCellSpaceX; final int cellY = (int) userCellSpaceY;
		 *
		 * if ((cellX >= 0) && (cellX < (this.mapSize[0] - 1)) && (cellY >= 0) && (cellY
		 * < (this.mapSize[1] - 1))) { final float bottomLeft = this.waterHeights[(cellY
		 * * this.columns) + cellX]; final float bottomRight = this.waterHeights[(cellY
		 * * this.columns) + cellX + 1]; final float topLeft = this.waterHeights[((cellY
		 * + 1) * this.columns) + cellX]; final float topRight =
		 * this.waterHeights[((cellY + 1) * this.columns) + cellX + 1]; final float sqX
		 * = userCellSpaceX - cellX; final float sqY = userCellSpaceY - cellY; float
		 * height;
		 *
		 * if ((sqX + sqY) < 1) { height = bottomLeft + ((bottomRight - bottomLeft) *
		 * sqX) + ((topLeft - bottomLeft) * sqY); } else { height = topRight +
		 * ((bottomRight - topRight) * (1 - sqY)) + ((topLeft - topRight) * (1 - sqX));
		 * }
		 *
		 * return (height + this.waterHeightOffset) * 128.0f; }
		 */

		return this.waterHeightOffset * 128.0f;
	}

	@Override
	public void loadSplats() throws IOException {
		for (final Map.Entry<String, Splat> entry : this.splats.entrySet()) {
			final String path = entry.getKey();
			final Splat splat = entry.getValue();

			final SplatModel splatModel = new SplatModel(Gdx.gl30,
					(Texture) this.viewer.load(path, PathSolver.DEFAULT, null), splat.locations, this.centerOffset,
					splat.unitMapping.isEmpty() ? null : splat.unitMapping, false, false, false, false);
			splatModel.color[3] = splat.opacity;
			addSplatBatchModel(path, splatModel);
		}
	}

	@Override
	public void removeSplatBatchModel(final String path) {
		this.uberSplatModelsList.remove(this.uberSplatModels.remove(path));
	}

	@Override
	public void addSplatBatchModel(final String path, final SplatModel model) {
		this.uberSplatModels.put(path, model);
		this.uberSplatModelsList.add(model);
		Collections.sort(this.uberSplatModelsList);
	}

	@Override
	public SplatModel getSplatModel(final String pathKey) {
		return this.uberSplatModels.get(pathKey);
	}

	@Override
	public SplatMover addUberSplat(final String path, final float x, final float y, final float z, final float scale,
			final boolean unshaded, final boolean noDepthTest, final boolean highPriority, final boolean aboveWater) {
		SplatModel splatModel = this.uberSplatModels.get(path);
		if (splatModel == null) {
			splatModel = new SplatModel(Gdx.gl30, (Texture) this.viewer.load(path, PathSolver.DEFAULT, null),
					new ArrayList<>(), this.centerOffset, new ArrayList<>(), unshaded, noDepthTest, highPriority,
					aboveWater);
			addSplatBatchModel(path, splatModel);
		}
		return splatModel.add(x - scale, y - scale, x + scale, y + scale, z, this.centerOffset);
	}

	@Override
	public SplatMover addUnitShadowSplat(final String texture, final float x, final float y, final float x2,
			final float y2, final float zDepthUpward, final float opacity, final boolean aboveWater) {
		SplatModel splatModel = this.uberSplatModels.get(texture);
		if (splatModel == null) {
			splatModel = new SplatModel(Gdx.gl30, (Texture) this.viewer.load(texture, PathSolver.DEFAULT, null),
					new ArrayList<>(), this.centerOffset, new ArrayList<>(), false, false, false, aboveWater);
			splatModel.color[3] = opacity;
			addSplatBatchModel(texture, splatModel);
		}
		return splatModel.add(x, y, x2, y2, zDepthUpward, this.centerOffset);
	}

	@Override
	public boolean inPlayableArea(float x, float y) {
		x = (x - this.centerOffset[0]) / 128.0f;
		y = (y - this.centerOffset[1]) / 128.0f;
		if (x < this.mapBounds[0]) {
			return false;
		}
		if (x >= (this.mapSize[0] - this.mapBounds[1] - 1)) {
			return false;
		}
		if (y < this.mapBounds[2]) {
			return false;
		}
		if (y >= (this.mapSize[1] - this.mapBounds[3] - 1)) {
			return false;
		} // TODO why do we use floor if we can use int cast?
		return true;
	}

	@Override
	public Rectangle getPlayableMapArea() {
		return this.shaderMapBoundsRectangle;
	}

	@Override
	public Rectangle getEntireMap() {
		return this.entireMapRectangle;
	}

	private static char getRampLetter(final int layerHeightOffset, final boolean isRamp) {
		if (isRamp) {
			switch (layerHeightOffset) {
			case 0:
				return 'L';
			case 1:
				return 'H';
			case 2:
				return 'X';
			default:
				throw new IllegalArgumentException("Invalid ramp");
			}
		}
		else {
			return (char) ('A' + layerHeightOffset);
		}
	}

	@Override
	public float[] getDefaultCameraBounds() {
		return this.defaultCameraBounds;
	}

	@Override
	public void setFogOfWarData(final CFogMaskSettings fogMaskSettings, final CPlayerFogOfWarInterface fogOfWarData) {
	}

	@Override
	public void reloadFogOfWarDataToGPU(final CFogMaskSettings fogMaskSettings) {
	}

	public int getFogOfWarMap() {
		return this.fogOfWarMap;
	}

	@Override
	public void setWaterBaseColor(final float red, final float green, final float blue, final float alpha) {
		final float[] rgba = { red, green, blue, alpha };
		for (int i = 0; i < 4; i++) {
			this.maxDeepColorApplied[i] = this.maxDeepColor[i] * rgba[i];
			this.minDeepColorApplied[i] = this.minDeepColor[i] * rgba[i];
			this.maxShallowColorApplied[i] = this.maxShallowColor[i] * rgba[i];
			this.minShallowColorApplied[i] = this.minShallowColor[i] * rgba[i];
		}
	}

	private class WdtChunkModelInstance extends ModelInstance {
		public static final int CHUNK_GRID_SIZE = 9;
		public static final int CHUNK_INTERIOR_GRID_SIZE = CHUNK_GRID_SIZE - 1;
		public static final float tilesize = 533.3333f;
		public static final float wowToHiveWEFactor = 1.0f / ((tilesize / 16) / 8);
		public static final float wowToWc3Factor = 128.0f * wowToHiveWEFactor;

		private final ChunkInfo chunkInfo;
		private final Chunk chunk;

		private float[] groundCornerHeights;
		private float[] groundCornerHeightsWdtInterior;
		private float[] groundCornerNormals;
		private float[] groundCornerNormalsInterior;
		private short[] groundTextureList;

		private int groundCornerHeight;
		private int groundCornerHeightWdtInterior;
		private int groundCornerNormal;
		private int groundCornerNormalWdtInterior;
		private int groundTextureData = -1;
		private int[] alphaMapHandleIds;
		private AlphaMapDataValue[] alphaMapValues;
		private final int[] tileOffset = new int[2];

		public WdtChunkModelInstance(final Model model, final ChunkInfo chunkInfo, final Chunk chunk) {
			super(model);
			this.chunkInfo = chunkInfo;
			this.chunk = chunk;
		}

		@Override
		public boolean isVisible(final Camera camera) {
			return true; // super.isVisible(camera);
		}

		@Override
		public void updateAnimations(final float dt) {

		}

		@Override
		public void clearEmittedObjects() {

		}

		@Override
		protected void updateLights(final Scene scene2) {

		}

		@Override
		public void renderOpaque(final Matrix4 mvp) {
			// Render tiles

			final WdtChunkModel model = (WdtChunkModel) this.model;
			TerrainWdt.this.webGL.useShaderProgram(TerrainWdt.this.groundShader);

			final GL30 gl = Gdx.gl30;
			gl.glEnable(GL20.GL_CULL_FACE);
			gl.glDisable(GL30.GL_BLEND);
			gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
			gl.glEnable(GL20.GL_DEPTH_TEST);
			gl.glDepthMask(true);

			gl.glUniformMatrix4fv(TerrainWdt.this.groundShader.getUniformLocation("MVP"), 1, false, mvp.val, 0);
			gl.glUniform1i(TerrainWdt.this.groundShader.getUniformLocation("show_pathing_map"),
					TerrainWdt.this.viewer.renderPathing);
			gl.glUniform1i(TerrainWdt.this.groundShader.getUniformLocation("show_lighting"),
					TerrainWdt.this.viewer.renderLighting);
			gl.glUniform1i(TerrainWdt.this.groundShader.getUniformLocation("height_cliff_texture"), 1);
			gl.glUniform1i(TerrainWdt.this.groundShader.getUniformLocation("height_cliff_texture_wdt"), 23);
			gl.glUniform1i(TerrainWdt.this.groundShader.getUniformLocation("normal_texture"), 10);
			gl.glUniform1i(TerrainWdt.this.groundShader.getUniformLocation("normal_texture_interior"), 11);
			gl.glUniform1i(TerrainWdt.this.groundShader.getUniformLocation("terrain_alpha_list"), 2);
//			gl.glUniform1i(TerrainWdt.this.groundShader.getUniformLocation("shadowMap"), 20);
			gl.glUniform1f(TerrainWdt.this.groundShader.getUniformLocation("centerOffsetX"),
					TerrainWdt.this.centerOffset[0]);
			gl.glUniform1f(TerrainWdt.this.groundShader.getUniformLocation("centerOffsetY"),
					TerrainWdt.this.centerOffset[1]);
			gl.glUniform1i(TerrainWdt.this.groundShader.getUniformLocation("tileOffsetX"), this.tileOffset[0]);
			gl.glUniform1i(TerrainWdt.this.groundShader.getUniformLocation("tileOffsetY"), this.tileOffset[1]);
			gl.glUniform2f(TerrainWdt.this.groundShader.getUniformLocation("size_world"), TerrainWdt.this.columns,
					TerrainWdt.this.rows);

			final W3xSceneLightManager lightManager = (W3xSceneLightManager) TerrainWdt.this.viewer.worldScene
					.getLightManager();
			final DataTexture unitLightsTexture = lightManager.getTerrainLightsTexture();

			unitLightsTexture.bind(21);
			gl.glUniform1i(TerrainWdt.this.groundShader.getUniformLocation("lightTexture"), 21);
			gl.glUniform1f(TerrainWdt.this.groundShader.getUniformLocation("lightCount"),
					lightManager.getTerrainLightCount());
			gl.glUniform1f(TerrainWdt.this.groundShader.getUniformLocation("lightTextureHeight"),
					unitLightsTexture.getHeight());
			TerrainWdt.this.groundShader.setUniformf("u_fogColor", TerrainWdt.this.viewer.worldScene.fogSettings.color);
			TerrainWdt.this.groundShader.setUniformf("u_fogParams",
					TerrainWdt.this.viewer.worldScene.fogSettings.style.ordinal(),
					TerrainWdt.this.viewer.worldScene.fogSettings.start,
					TerrainWdt.this.viewer.worldScene.fogSettings.end,
					TerrainWdt.this.viewer.worldScene.fogSettings.density);

			gl.glUniform1i(TerrainWdt.this.groundShader.getUniformLocation("cliff_textures"), 0);

			gl.glActiveTexture(GL30.GL_TEXTURE1);
			gl.glBindTexture(GL30.GL_TEXTURE_2D, this.groundCornerHeight);

			gl.glUniform1i(TerrainWdt.this.groundShader.getUniformLocation("pathing_map_static"), 2);
			gl.glActiveTexture(GL30.GL_TEXTURE2);
			gl.glBindTexture(GL30.GL_TEXTURE_2D, this.groundTextureData);

			gl.glUniform1i(TerrainWdt.this.groundShader.getUniformLocation("sample0"), 3);
			gl.glUniform1i(TerrainWdt.this.groundShader.getUniformLocation("sample1"), 4);
			gl.glUniform1i(TerrainWdt.this.groundShader.getUniformLocation("sample2"), 5);
			gl.glUniform1i(TerrainWdt.this.groundShader.getUniformLocation("sample3"), 6);
			gl.glUniform1i(TerrainWdt.this.groundShader.getUniformLocation("alpha1"), 7);
			gl.glUniform1i(TerrainWdt.this.groundShader.getUniformLocation("alpha2"), 8);
			gl.glUniform1i(TerrainWdt.this.groundShader.getUniformLocation("alpha3"), 9);
			gl.glUniform1i(TerrainWdt.this.groundShader.getUniformLocation("shadowMap"), 20);
			gl.glUniform1i(TerrainWdt.this.groundShader.getUniformLocation("fogOfWarMap"), 22);
			int i = 0;
			for (final MapChunkLayer layer : this.chunk.getMapChunkLayers()) {
				final GroundTexture groundTexture = model.groundTextures.get((int) layer.getTextureId());
				gl.glActiveTexture(GL30.GL_TEXTURE3 + i);
				gl.glBindTexture(GL30.GL_TEXTURE_2D, groundTexture.id);
				i++;
			}
			i = 0;
			for (final int handleId : this.alphaMapHandleIds) {
				gl.glActiveTexture(GL30.GL_TEXTURE7 + i);
				gl.glBindTexture(GL30.GL_TEXTURE_2D, handleId);
				i++;
			}
			gl.glUniform1i(TerrainWdt.this.groundShader.getUniformLocation("layer_count"),
					this.chunk.getMapChunkLayers().size());
			gl.glActiveTexture(GL30.GL_TEXTURE10);
			gl.glBindTexture(GL30.GL_TEXTURE_2D, this.groundCornerNormal);

			gl.glActiveTexture(GL30.GL_TEXTURE11);
			gl.glBindTexture(GL30.GL_TEXTURE_2D, this.groundCornerNormalWdtInterior);

//			gl.glActiveTexture(GL30.GL_TEXTURE20, /*pathingMap.getTextureStatic()*/);
//			gl.glActiveTexture(GL30.GL_TEXTURE21, /*pathingMap.getTextureDynamic()*/);

//			gl.glActiveTexture(GL30.GL_TEXTURE20);
//			gl.glBindTexture(GL30.GL_TEXTURE_2D, TerrainWdt.this.shadowMap);

//			gl.glActiveTexture(GL30.GL_TEXTURE22);
//			gl.glBindTexture(GL30.GL_TEXTURE_2D, TerrainWdt.this.fogOfWarMap);

			gl.glActiveTexture(GL30.GL_TEXTURE23);
			gl.glBindTexture(GL30.GL_TEXTURE_2D, this.groundCornerHeightWdtInterior);

//			gl.glEnableVertexAttribArray(0);
			gl.glBindBuffer(GL30.GL_ARRAY_BUFFER, ShapesWdt.INSTANCE.vertexBuffer);
			gl.glVertexAttribPointer(TerrainWdt.this.groundShader.getAttributeLocation("vPosition"), 2, GL30.GL_FLOAT,
					false, 0, 0);

			gl.glBindBuffer(GL30.GL_ELEMENT_ARRAY_BUFFER, ShapesWdt.INSTANCE.indexBuffer);
			if (WIREFRAME_TERRAIN) {
				Extensions.wireframeExtension.glPolygonMode(GL20.GL_FRONT_AND_BACK, Extensions.GL_LINE);
			}
			gl.glDrawElementsInstanced(GL30.GL_TRIANGLES, ShapesWdt.INSTANCE.quadIndices.length * 3,
					GL30.GL_UNSIGNED_INT, 0, CHUNK_INTERIOR_GRID_SIZE * CHUNK_INTERIOR_GRID_SIZE);
			if (WIREFRAME_TERRAIN) {
				Extensions.wireframeExtension.glPolygonMode(GL20.GL_FRONT_AND_BACK, Extensions.GL_FILL);
			}

//			gl.glDisableVertexAttribArray(0);

			gl.glEnable(GL30.GL_BLEND);

		}

		@Override
		public void renderTranslucent() {
		}

		@Override
		public void load() {
			final WdtChunkModel model = (WdtChunkModel) this.model;
			final TileHeader tileHeader = model.tileHeader;

			// internet says: floor((32 - (axis / 533.33333)))
			final long indexX = this.chunk.getIndexX();
			final long indexY = this.chunk.getIndexY();

			final long war3ChunkIndexX = (model.blockX * 16) + indexX;
			final long war3ChunkIndexY = ((model.blockY * 16) + 15) - indexY;

			final float[][] heightMap = this.chunk.getHeightMap();
			final Vector3b[][] normals = this.chunk.getNormals();

			this.groundCornerHeights = new float[CHUNK_GRID_SIZE * CHUNK_GRID_SIZE];
			this.groundCornerHeightsWdtInterior = new float[CHUNK_INTERIOR_GRID_SIZE * CHUNK_INTERIOR_GRID_SIZE];
			this.groundCornerNormals = new float[CHUNK_GRID_SIZE * CHUNK_GRID_SIZE * 3];
			this.groundCornerNormalsInterior = new float[CHUNK_INTERIOR_GRID_SIZE * CHUNK_INTERIOR_GRID_SIZE * 3];

			final long war3BaseIndexX = (war3ChunkIndexX * 8);
			final long war3BaseIndexY = (war3ChunkIndexY * 8);
			this.tileOffset[0] = (int) war3BaseIndexX;
			this.tileOffset[1] = (int) war3BaseIndexY;
			double heightAvg = 0;
			for (int i = 0; i < CHUNK_GRID_SIZE; i++) {
				for (int j = 0; j < CHUNK_GRID_SIZE; j++) {
					final float height = heightMap[i][j];
					final Vector3b normal = normals[i][j];

					final int war3IndexX = (9 - i - 1);
					final int war3IndexY = j;
					final float convertedHeight = height * wowToHiveWEFactor;
					this.groundCornerHeights[(war3IndexX * CHUNK_GRID_SIZE) + war3IndexY] = convertedHeight;
					for (int k = 0; k < normal.components.length; k++) {
						this.groundCornerNormals[(((war3IndexX * CHUNK_GRID_SIZE) + war3IndexY) * 3)
								+ k] = (normal.components[k]) / 128f;
					}
					heightAvg += convertedHeight;
				}
			}
			setLocation((war3BaseIndexX * 128f) + TerrainWdt.this.centerOffset[0],
					(war3BaseIndexY * 128f) + TerrainWdt.this.centerOffset[1],
					(float) (heightAvg / (CHUNK_GRID_SIZE * CHUNK_GRID_SIZE)));
			for (int i = 0; i < CHUNK_INTERIOR_GRID_SIZE; i++) {
				for (int j = 0; j < CHUNK_INTERIOR_GRID_SIZE; j++) {
					final float height = heightMap[i + 9][j];
					final Vector3b normal = normals[i + 9][j];

					final int war3IndexX = (8 - i - 1);
					final int war3IndexY = j;
					this.groundCornerHeightsWdtInterior[(war3IndexX * CHUNK_INTERIOR_GRID_SIZE) + war3IndexY] = height
							* wowToHiveWEFactor;

					for (int k = 0; k < normal.components.length; k++) {
						this.groundCornerNormalsInterior[(((war3IndexX * CHUNK_INTERIOR_GRID_SIZE) + war3IndexY) * 3)
								+ k] = (normal.components[k]) / 128f;
					}
				}
			}
			final GL30 gl = Gdx.gl30;
			this.alphaMapHandleIds = new int[this.chunk.getMapChunkLayers().size() - 1];
			this.alphaMapValues = new AlphaMapDataValue[this.chunk.getMapChunkLayers().size() - 1];
			for (int layerId = 1; layerId < this.chunk.getMapChunkLayers().size(); layerId++) {
				final MapChunkLayer mapChunkLayer = this.chunk.getMapChunkLayers().get(layerId);
				int offset = (int) mapChunkLayer.getOffsAlpha();
				final short[] alphaMaps = this.chunk.getAlphaMaps();
				final AlphaMapData alphaMapData = new AlphaMapData(offset, alphaMaps);
				AlphaMapDataValue alphaMapDataValue = TerrainWdt.this.alphaDataToId.get(alphaMapData);
				final int idx = layerId - 1;
				if (alphaMapDataValue == null) {
					alphaMapDataValue = new AlphaMapDataValue(alphaMapData);
					alphaMapDataValue.glTextureHandle = gl.glGenTexture();

					final ByteBuffer wrapper = ByteBuffer.allocateDirect(4096 * 4).order(ByteOrder.nativeOrder());
					while (wrapper.hasRemaining()) {
						final short data = alphaMaps[offset];
						final byte nibbleA = (byte) (data & 0xF);
						final byte nibbleB = (byte) ((data & 0xF0) >> 4);
						wrapper.putFloat(nibbleA / 15f);
						wrapper.putFloat(nibbleB / 15f);
						offset++;
					}
					for (int x = 0; x < 64; x++) {
						// the internet said to copy the last row into the one next to it if some map
						// header
						// was set, and we don't even have that header on alpha. So, here's assuming
						// we have to always do that:
//						alpha_map[x][63] == alpha_map[x][62]
//						alpha_map[63][x] == alpha_map[62][x]
//						alpha_map[63][63] == alpha_map[62][62]
						wrapper.putFloat(4 * ((x * 64) + 63), wrapper.getFloat(4 * ((x * 64) + 62)));
						wrapper.putFloat(4 * ((63 * 64) + x), wrapper.getFloat(4 * ((62 * 64) + x)));
					}
					wrapper.putFloat(4 * ((63 * 64) + 63), wrapper.getFloat(4 * ((62 * 64) + 62)));

					gl.glBindTexture(GL30.GL_TEXTURE_2D, alphaMapDataValue.glTextureHandle);
					wrapper.clear();
					gl.glTexImage2D(GL30.GL_TEXTURE_2D, 0, GL30.GL_R32F, 64, 64, 0, GL30.GL_RED, GL30.GL_FLOAT,
							wrapper);
					gl.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MAG_FILTER, GL30.GL_LINEAR);
					gl.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MIN_FILTER, GL30.GL_LINEAR);
					gl.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_WRAP_S, GL30.GL_CLAMP_TO_EDGE);
					gl.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_WRAP_T, GL30.GL_CLAMP_TO_EDGE);

					TerrainWdt.this.alphaDataToId.put(alphaMapData, alphaMapDataValue);
				}
				this.alphaMapHandleIds[idx] = alphaMapDataValue.glTextureHandle;
				this.alphaMapValues[idx] = alphaMapDataValue;
				alphaMapDataValue.referenceCount++;

			}
			this.groundTextureList = new short[CHUNK_INTERIOR_GRID_SIZE * CHUNK_INTERIOR_GRID_SIZE * 4];

			// Ground
			// Ground
			this.groundTextureData = gl.glGenTexture();
			gl.glBindTexture(GL30.GL_TEXTURE_2D, this.groundTextureData);
			gl.glTexImage2D(GL30.GL_TEXTURE_2D, 0, GL30.GL_RGBA16UI, CHUNK_INTERIOR_GRID_SIZE, CHUNK_INTERIOR_GRID_SIZE,
					0, GL30.GL_RGBA_INTEGER, GL30.GL_UNSIGNED_SHORT, RenderMathUtils.wrapShort(this.groundTextureList));
			gl.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MAG_FILTER, GL30.GL_NEAREST);
			gl.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MIN_FILTER, GL30.GL_NEAREST);

			this.groundCornerHeight = gl.glGenTexture();
			gl.glBindTexture(GL30.GL_TEXTURE_2D, this.groundCornerHeight);
			gl.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MAG_FILTER, GL30.GL_NEAREST);
			gl.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MIN_FILTER, GL30.GL_NEAREST);

			gl.glTexImage2D(GL30.GL_TEXTURE_2D, 0, GL30.GL_R16F, CHUNK_GRID_SIZE, CHUNK_GRID_SIZE, 0, GL30.GL_RED,
					GL30.GL_FLOAT, RenderMathUtils.wrap(this.groundCornerHeights));
			gl.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_WRAP_S, GL30.GL_CLAMP_TO_EDGE);
			gl.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_WRAP_T, GL30.GL_CLAMP_TO_EDGE);

			this.groundCornerHeightWdtInterior = gl.glGenTexture();
			gl.glBindTexture(GL30.GL_TEXTURE_2D, this.groundCornerHeightWdtInterior);
			gl.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MAG_FILTER, GL30.GL_NEAREST);
			gl.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MIN_FILTER, GL30.GL_NEAREST);

			gl.glTexImage2D(GL30.GL_TEXTURE_2D, 0, GL30.GL_R16F, CHUNK_INTERIOR_GRID_SIZE, CHUNK_INTERIOR_GRID_SIZE, 0,
					GL30.GL_RED, GL30.GL_FLOAT, RenderMathUtils.wrap(this.groundCornerHeightsWdtInterior));
			gl.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_WRAP_S, GL30.GL_CLAMP_TO_EDGE);
			gl.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_WRAP_T, GL30.GL_CLAMP_TO_EDGE);

			this.groundCornerNormal = gl.glGenTexture();
			gl.glBindTexture(GL30.GL_TEXTURE_2D, this.groundCornerNormal);
			gl.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MAG_FILTER, GL30.GL_NEAREST);
			gl.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MIN_FILTER, GL30.GL_NEAREST);

			gl.glTexImage2D(GL30.GL_TEXTURE_2D, 0, GL30.GL_RGB, CHUNK_GRID_SIZE, CHUNK_GRID_SIZE, 0, GL30.GL_RGB,
					GL30.GL_FLOAT, RenderMathUtils.wrap(this.groundCornerNormals));
			gl.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_WRAP_S, GL30.GL_CLAMP_TO_EDGE);
			gl.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_WRAP_T, GL30.GL_CLAMP_TO_EDGE);

			this.groundCornerNormalWdtInterior = gl.glGenTexture();
			gl.glBindTexture(GL30.GL_TEXTURE_2D, this.groundCornerNormalWdtInterior);
			gl.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MAG_FILTER, GL30.GL_NEAREST);
			gl.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MIN_FILTER, GL30.GL_NEAREST);

			gl.glTexImage2D(GL30.GL_TEXTURE_2D, 0, GL30.GL_RGB, CHUNK_INTERIOR_GRID_SIZE, CHUNK_INTERIOR_GRID_SIZE, 0,
					GL30.GL_RGB, GL30.GL_FLOAT, RenderMathUtils.wrap(this.groundCornerNormalsInterior));
			gl.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_WRAP_S, GL30.GL_CLAMP_TO_EDGE);
			gl.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_WRAP_T, GL30.GL_CLAMP_TO_EDGE);

		}

		@Override
		public void unload() {
			for (final AlphaMapDataValue alphaMapDataValue : this.alphaMapValues) {
				alphaMapDataValue.referenceCount--;
				if (alphaMapDataValue.referenceCount <= 0) {
					Gdx.gl30.glDeleteTexture(alphaMapDataValue.glTextureHandle);
					TerrainWdt.this.alphaDataToId.remove(alphaMapDataValue.key);
				}
			}
			Gdx.gl30.glDeleteTexture(this.groundTextureData);
			Gdx.gl30.glDeleteTexture(this.groundCornerHeight);
			Gdx.gl30.glDeleteTexture(this.groundCornerHeightWdtInterior);
			Gdx.gl30.glDeleteTexture(this.groundCornerNormal);
			Gdx.gl30.glDeleteTexture(this.groundCornerNormalWdtInterior);
		}

		@Override
		protected RenderBatch getBatch(final TextureMapper textureMapper) {
			throw new UnsupportedOperationException("NOT API");
		}

		@Override
		public void setReplaceableTexture(final int replaceableTextureId, final String replaceableTextureFile) {

		}

		@Override
		public boolean isBatched() {
			return super.isBatched();
		}

		@Override
		protected void removeLights(final Scene scene2) {
		}

		@Override
		public void setReplaceableTextureHD(final int replaceableTextureId, final String replaceableTextureFile) {
		}

	}

	private class WdtChunkModel extends Model {

		private final TileHeader tileHeader;

		public List<GroundTexture> groundTextures = new ArrayList<>();
		private final Map<String, Integer> groundTextureToId = new HashMap<>();
		private float wowXOffset;
		private float wowYOffset;

		private int blockX;

		private int blockY;

		public WdtChunkModel(final ModelHandler handler, final ModelViewer viewer, final String extension,
				final PathSolver pathSolver, final String fetchUrl, final TileHeader tileHeader) {
			super(handler, viewer, extension, pathSolver, fetchUrl);
			this.tileHeader = tileHeader;
			this.ok = true;
		}

		@Override
		protected ModelInstance createInstance(final int type) {
			return new WdtChunkModelInstance(this, this.tileHeader.chunkInfos.get(type),
					this.tileHeader.chunks.get(type));
		}

		@Override
		protected void lateLoad() {
		}

		@Override
		protected void load(final InputStream src, final Object options) {
			final int tileIdx = this.tileHeader.idx;
			this.blockX = tileIdx % 64;
			this.wowXOffset = this.blockX * WdtChunkModelInstance.tilesize;
			this.blockY = 63 - (tileIdx / 64);
			this.wowYOffset = this.blockY * WdtChunkModelInstance.tilesize;

			final float size = 8 * WdtChunkModelInstance.wowToWc3Factor;
			final float height = 18000 * WdtChunkModelInstance.wowToWc3Factor;
			final float halfSize = size / 2;
			this.bounds.fromExtents(new float[] { -halfSize, -halfSize, -halfSize },
					new float[] { halfSize, halfSize, halfSize }, 0);

			// Ground textures
			for (final String fileName : this.tileHeader.textureFileNames) {
				GroundTextureWdt groundTextureWdt = TerrainWdt.this.pathToTexture.get(fileName);
				try {
					if (groundTextureWdt == null) {
						groundTextureWdt = new GroundTextureWdt(fileName, null, TerrainWdt.this.dataSource, Gdx.gl30);
						TerrainWdt.this.pathToTexture.put(fileName, groundTextureWdt);
					}
					this.groundTextures.add(groundTextureWdt);
				}
				catch (final IOException e) {
					throw new RuntimeException(e);
				}
				this.groundTextureToId.put(fileName, this.groundTextures.size() - 1);
			}
		}

		@Override
		protected void error(final Exception e) {
		}

	}

	private static final class AlphaMapData {
		private static final int ALPHA_SEG_LENGTH = 2048;
		private final int offset;
		private final short[] data;

		public AlphaMapData(final int offset, final short[] data) {
			this.offset = offset;
			this.data = data;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			for (int i = 0; i < ALPHA_SEG_LENGTH; i++) {
				result = (prime * result) + Short.hashCode(this.data[this.offset + i]);
			}
			return result;
		}

		@Override
		public boolean equals(final Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			final AlphaMapData other = (AlphaMapData) obj;
			return Arrays.equals(this.data, this.offset, this.offset + ALPHA_SEG_LENGTH, other.data, other.offset,
					other.offset + ALPHA_SEG_LENGTH);
		}
	}

	private static final class AlphaMapDataValue {
		private final AlphaMapData key;
		public int glTextureHandle;
		public int referenceCount;

		public AlphaMapDataValue(final AlphaMapData key) {
			this.key = key;
		}
	}

	private final class Tile {
		private final TileHeader tileHeader;
		private final WdtChunkModel terrainModel;
		private final float[] myCornerXY;
		private ActiveTile activeTile;
		private final Chunk[][] chunks;

		public Tile(final TileHeader tileHeader, final WdtChunkModel wdtChunkModel, final float myCornerX,
				final float myCornerY) {
			this.tileHeader = tileHeader;
			this.terrainModel = wdtChunkModel;
			this.myCornerXY = new float[] { myCornerX, myCornerY };
			this.chunks = new Chunk[16][16];
			for (final Chunk chunk : tileHeader.chunks) {
				final long war3ChunkIndexY = 15 - chunk.getIndexY();
				final long war3ChunkIndexX = chunk.getIndexX();
				this.chunks[(int) war3ChunkIndexX][(int) war3ChunkIndexY] = chunk;
			}
		}

		public boolean activate() {
			if (this.activeTile == null) {
				this.activeTile = new ActiveTile(this);
				return true;
			}
			return false;
		}

		public void deactivate() {
			this.activeTile.dispose();
			this.activeTile = null;
		}
	}

	private final class ActiveTile {
		List<ModelInstance> modelInstances = new ArrayList<>();
		List<RenderDoodad> renderDoodads = new ArrayList<>();

		private int shadowMap;
		private final int groundCornerHeightLinear;
		private byte[] staticShadowData;
		private byte[] shadowData;
		private final int width;
		private final int height;

		private final Tile tile;

		final float[] groundCornerHeights;
		boolean disposed = false;
		final Set<Long> usedSet = new HashSet<>();

		public SoftwareGroundMesh softwareGroundMesh;
		public SoftwareWaterAndGroundMesh softwareWaterAndGroundMesh;
		private final int waterHeight;
		private final int waterExists;
		private float[] waterHeights;
		private byte[] waterExistsData;
		protected boolean loadingFinished;

		public ActiveTile(final Tile tile) {
			this.tile = tile;

			this.width = (16 * 8) + 1;
			this.height = this.width;

			TerrainWdt.this.tasks.add(new ChunkTask());

			final int columns = (this.width - 1) * 4;
			final int rows = (this.height - 1) * 4;

			final int shadowSize = columns * rows;
			this.staticShadowData = new byte[shadowSize];
			this.groundCornerHeights = new float[this.width * this.height];
			this.waterHeights = new float[this.width * this.height];
			this.waterExistsData = new byte[this.width * this.height];

			final GL30 gl = Gdx.gl30;
			this.groundCornerHeightLinear = gl.glGenTexture();
			gl.glBindTexture(GL30.GL_TEXTURE_2D, this.groundCornerHeightLinear);
			gl.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MAG_FILTER, GL30.GL_LINEAR);
			gl.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MIN_FILTER, GL30.GL_LINEAR);

			gl.glTexImage2D(GL30.GL_TEXTURE_2D, 0, GL30.GL_R16F, this.width, this.height, 0, GL30.GL_RED, GL30.GL_FLOAT,
					RenderMathUtils.wrap(this.groundCornerHeights));
			gl.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_WRAP_S, GL30.GL_CLAMP_TO_EDGE);
			gl.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_WRAP_T, GL30.GL_CLAMP_TO_EDGE);

			TerrainWdt.this.tasks.add(new DynamicTask() {
				@Override
				public boolean run() {
					if (!ActiveTile.this.disposed) {
						gl.glBindTexture(GL30.GL_TEXTURE_2D, ActiveTile.this.groundCornerHeightLinear);
						gl.glTexImage2D(GL30.GL_TEXTURE_2D, 0, GL30.GL_R16F, ActiveTile.this.width,
								ActiveTile.this.height, 0, GL30.GL_RED, GL30.GL_FLOAT,
								RenderMathUtils.wrap(ActiveTile.this.groundCornerHeights));
					}
					return true;
				}
			});
			this.shadowMap = gl.glGenTexture();
			TerrainWdt.this.tasks.add(new DynamicTask() {
				@Override
				public boolean run() {
					if (!ActiveTile.this.disposed) {
						initShadows();
					}
					return true;
				}
			});
			TerrainWdt.this.tasks.add(new DynamicTask() {
				@Override
				public boolean run() {
					ActiveTile.this.softwareGroundMesh = new SoftwareGroundMesh(ActiveTile.this.groundCornerHeights,
							tile.myCornerXY, ActiveTile.this.width, ActiveTile.this.height);
					ActiveTile.this.softwareWaterAndGroundMesh = new SoftwareWaterAndGroundMesh(0,
							ActiveTile.this.groundCornerHeights, ActiveTile.this.waterHeights,
							ActiveTile.this.waterExistsData, tile.myCornerXY, ActiveTile.this.width,
							ActiveTile.this.height);
					return true;
				}
			});
			// Water
			this.waterHeight = gl.glGenTexture();
			TerrainWdt.this.tasks.add(new DynamicTask() {
				@Override
				public boolean run() {
					if (!ActiveTile.this.disposed) {
						gl.glBindTexture(GL30.GL_TEXTURE_2D, ActiveTile.this.waterHeight);
						gl.glTexImage2D(GL30.GL_TEXTURE_2D, 0, GL30.GL_R16F, ActiveTile.this.width,
								ActiveTile.this.height, 0, GL30.GL_RED, GL30.GL_FLOAT,
								RenderMathUtils.wrap(ActiveTile.this.waterHeights));
						gl.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MAG_FILTER, GL30.GL_NEAREST);
						gl.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MIN_FILTER, GL30.GL_NEAREST);
					}
					return true;
				}
			});

			this.waterExists = gl.glGenTexture();
			TerrainWdt.this.tasks.add(new DynamicTask() {
				@Override
				public boolean run() {
					if (!ActiveTile.this.disposed) {
						gl.glBindTexture(GL30.GL_TEXTURE_2D, ActiveTile.this.waterExists);
						gl.glPixelStorei(GL30.GL_UNPACK_ALIGNMENT, 1);
						gl.glTexImage2D(GL30.GL_TEXTURE_2D, 0, GL30.GL_R8, ActiveTile.this.width,
								ActiveTile.this.height, 0, GL30.GL_RED, GL30.GL_UNSIGNED_BYTE,
								RenderMathUtils.wrap(ActiveTile.this.waterExistsData));
						gl.glPixelStorei(GL30.GL_UNPACK_ALIGNMENT, 4);
						gl.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MAG_FILTER, GL30.GL_NEAREST);
						gl.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MIN_FILTER, GL30.GL_NEAREST);
					}
					return true;
				}
			});
			TerrainWdt.this.tasks.add(new DynamicTask() {
				@Override
				public boolean run() {
					ActiveTile.this.loadingFinished = true;
					return true;
				}
			});
		}

		public void renderWater() {
			if (!this.loadingFinished) {
				return;
			}
			// Render water
			TerrainWdt.this.webGL.useShaderProgram(TerrainWdt.this.waterShader);

			final GL30 gl = Gdx.gl30;
			gl.glDepthMask(false);
			gl.glEnable(GL30.GL_CULL_FACE);
			gl.glEnable(GL30.GL_BLEND);
			gl.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);

			TerrainWdt.this.waterShader.setUniformMatrix4fv("MVP", TerrainWdt.this.camera.viewProjectionMatrix.val, 0,
					16);
			TerrainWdt.this.waterShader.setUniform4fv("shallow_color_min", TerrainWdt.this.minShallowColorApplied, 0,
					4);
			TerrainWdt.this.waterShader.setUniform4fv("shallow_color_max", TerrainWdt.this.maxShallowColorApplied, 0,
					4);
			TerrainWdt.this.waterShader.setUniform4fv("deep_color_min", TerrainWdt.this.minDeepColorApplied, 0, 4);
			TerrainWdt.this.waterShader.setUniform4fv("deep_color_max", TerrainWdt.this.maxDeepColorApplied, 0, 4);
			TerrainWdt.this.waterShader.setUniformf("water_offset", 0);
			TerrainWdt.this.waterShader.setUniformi("current_texture", (int) TerrainWdt.this.waterIndex);
			TerrainWdt.this.waterShader.setUniformf("centerOffsetX", this.tile.myCornerXY[0]);
			TerrainWdt.this.waterShader.setUniformf("centerOffsetY", this.tile.myCornerXY[1]);
			TerrainWdt.this.waterShader.setUniform4fv("mapBounds", TerrainWdt.this.shaderMapBounds, 0, 4);

			final W3xSceneLightManager lightManager = (W3xSceneLightManager) TerrainWdt.this.viewer.worldScene
					.getLightManager();
			final DataTexture terrainLightsTexture = lightManager.getTerrainLightsTexture();

			terrainLightsTexture.bind(3);
			TerrainWdt.this.waterShader.setUniformi("lightTexture", 3);
			TerrainWdt.this.waterShader.setUniformf("lightCount", lightManager.getTerrainLightCount());
			TerrainWdt.this.waterShader.setUniformf("lightTextureHeight", terrainLightsTexture.getHeight());
			TerrainWdt.this.waterShader.setUniformf("u_fogColor", TerrainWdt.this.viewer.worldScene.fogSettings.color);
			TerrainWdt.this.waterShader.setUniformf("u_fogParams",
					TerrainWdt.this.viewer.worldScene.fogSettings.style.ordinal(),
					TerrainWdt.this.viewer.worldScene.fogSettings.start,
					TerrainWdt.this.viewer.worldScene.fogSettings.end,
					TerrainWdt.this.viewer.worldScene.fogSettings.density);

			TerrainWdt.this.waterShader.setUniformi("water_height_texture", 0);
			TerrainWdt.this.waterShader.setUniformi("ground_height_texture", 1);
			TerrainWdt.this.waterShader.setUniformi("water_exists_texture", 2);
			TerrainWdt.this.waterShader.setUniformi("water_textures", 4);
			gl.glActiveTexture(GL30.GL_TEXTURE0);
			gl.glBindTexture(GL30.GL_TEXTURE_2D, this.waterHeight);
			gl.glActiveTexture(GL30.GL_TEXTURE1);
			gl.glBindTexture(GL30.GL_TEXTURE_2D, this.groundCornerHeightLinear); // TODO was not the linear one
			gl.glActiveTexture(GL30.GL_TEXTURE2);
			gl.glBindTexture(GL30.GL_TEXTURE_2D, this.waterExists);
			gl.glActiveTexture(GL30.GL_TEXTURE4);
			gl.glBindTexture(GL30.GL_TEXTURE_2D_ARRAY, TerrainWdt.this.waterTextureArray);

			gl.glBindBuffer(GL30.GL_ARRAY_BUFFER, Shapes.INSTANCE.vertexBuffer);
			gl.glVertexAttribPointer(TerrainWdt.this.waterShader.getAttributeLocation("vPosition"), 2, GL30.GL_FLOAT,
					false, 0, 0);

			gl.glBindBuffer(GL30.GL_ELEMENT_ARRAY_BUFFER, Shapes.INSTANCE.indexBuffer);
			gl.glDrawElementsInstanced(GL30.GL_TRIANGLES, Shapes.INSTANCE.quadIndices.length * 3, GL30.GL_UNSIGNED_INT,
					0, (this.width - 1) * (this.height - 1));

			gl.glEnable(GL30.GL_BLEND);
		}

		public void dispose() {
			Gdx.gl30.glDeleteTexture(this.groundCornerHeightLinear);
			Gdx.gl30.glDeleteTexture(this.shadowMap);
			Gdx.gl30.glDeleteTexture(this.waterHeight);
			Gdx.gl30.glDeleteTexture(this.waterExists);
			this.disposed = true;
			TerrainWdt.this.tasks.add(new DisposeTask());
		}

		private void initShadows() {
			final GL30 gl = Gdx.gl30;
			final int columns = (this.width - 1) * 4;
			final int rows = (this.height - 1) * 4;

			final int shadowSize = columns * rows;
			this.shadowData = new byte[columns * rows];

			reloadShadowData(TerrainWdt.this.centerOffset, columns, rows);

			gl.glBindTexture(GL30.GL_TEXTURE_2D, this.shadowMap);
			gl.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MAG_FILTER, GL30.GL_LINEAR);
			gl.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MIN_FILTER, GL30.GL_LINEAR);
			gl.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_WRAP_S, GL30.GL_CLAMP_TO_EDGE);
			gl.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_WRAP_T, GL30.GL_CLAMP_TO_EDGE);
			gl.glTexImage2D(GL30.GL_TEXTURE_2D, 0, GL30.GL_R8, columns, rows, 0, GL30.GL_RED, GL30.GL_UNSIGNED_BYTE,
					RenderMathUtils.wrap(this.shadowData));

//			this.fogOfWarMap = gl.glGenTexture();
//			gl.glBindTexture(GL30.GL_TEXTURE_2D, this.fogOfWarMap);
		}

		private void reloadShadowData(final float[] centerOffset, final int columns, final int rows) {
			System.arraycopy(this.staticShadowData, 0, this.shadowData, 0, this.staticShadowData.length);
			for (final Map.Entry<String, Texture> fileAndTexture : TerrainWdt.this.shadowTextures.entrySet()) {
				final String file = fileAndTexture.getKey();
				final Texture texture = fileAndTexture.getValue();

				final int width = texture.getWidth();
				final int height = texture.getHeight();
				final int ox = (int) Math.round(width * 0.3);
				final int oy = (int) Math.round(height * 0.7);
				for (final float[] location : TerrainWdt.this.shadows.get(file)) {
					blitShadowDataLocation(columns, rows, (RawOpenGLTextureResource) texture, width, height, ox, oy,
							centerOffset, location[0], location[1], this.shadowData);
				}
			}
		}

		public boolean blitShadowDataLocation(final int columns, final int rows, final RawOpenGLTextureResource texture,
				final int width, final int height, final int x01, final int y01, final float[] centerOffset,
				final float v, final float v2, final byte[] shadowData) {
			final int x0 = (int) Math.floor((v - centerOffset[0]) / 32.0) - x01;
			final int y0 = (int) Math.floor((v2 - centerOffset[1]) / 32.0) + y01;
			boolean anyDataWritten = false;
			for (int y = 0; y < height; ++y) {
				if (((y0 - y) < 0) || ((y0 - y) >= rows)) {
					continue;
				}
				for (int x = 0; x < width; ++x) {
					if (((x0 + x) < 0) || ((x0 + x) >= columns)) {
						continue;
					}
					if (texture.getData().get((((y * width) + x) * 4) + 3) != 0) {
						shadowData[((y0 - y) * columns) + x0 + x] = (byte) 128;
						anyDataWritten = true;
					}
				}
			}
			return anyDataWritten;
		}

		public boolean blitShadowData(final int columns, final int rows, final float shadowX, final float shadowY,
				final Texture texture) {
			final int width = texture.getWidth();
			final int height = texture.getHeight();
			final int ox = (int) Math.round(width * 0.3);
			final int oy = (int) Math.round(height * 0.7);
			return blitShadowDataLocation(columns, rows, (RawOpenGLTextureResource) texture, width, height, ox, oy,
					this.tile.myCornerXY, shadowX, shadowY, this.shadowData);
		}

		private void reloadShadowDataToGPU() {
			final int columns = (TerrainWdt.this.columns - 1) * 4;
			final int rows = (TerrainWdt.this.rows - 1) * 4;
			reloadShadowData(TerrainWdt.this.centerOffset, columns, rows);
			final GL30 gl = Gdx.gl30;
			gl.glBindTexture(GL30.GL_TEXTURE_2D, this.shadowMap);
			gl.glTexImage2D(GL30.GL_TEXTURE_2D, 0, GL30.GL_R8, columns, rows, 0, GL30.GL_RED, GL30.GL_UNSIGNED_BYTE,
					RenderMathUtils.wrap(this.shadowData));
		}

		private class DisposeTask implements DynamicTask {
			@Override
			public boolean run() {
				for (int i = ActiveTile.this.modelInstances.size() - 1, k = 35; (i >= 0) && (k >= 0); i--, k--) {
					final ModelInstance modelInstance = ActiveTile.this.modelInstances.get(i);
					modelInstance.detach();
					((WdtChunkModelInstance) modelInstance).unload();
					ActiveTile.this.modelInstances.remove(i);
				}
				for (int i = ActiveTile.this.renderDoodads.size() - 1, k = 20; (i >= 0) && (k >= 0); i--, k--) {
					final RenderDoodad renderDoodad = ActiveTile.this.renderDoodads.get(i);
					TerrainWdt.this.viewer.removeWdtDoodad(renderDoodad);
					ActiveTile.this.renderDoodads.remove(i);
				}
				final boolean empty = ActiveTile.this.modelInstances.isEmpty()
						&& ActiveTile.this.renderDoodads.isEmpty();
				return empty;
			}
		}

		private class ChunkTask implements DynamicTask {
			int i;

			@Override
			public boolean run() {
				for (int k = 0; (this.i < ActiveTile.this.tile.tileHeader.chunks.size()) && (k < 30); k++, this.i++) {
					final Chunk chunk = ActiveTile.this.tile.tileHeader.chunks.get(this.i);
					if (!chunk.getMapChunkLayers().isEmpty()) {
						final ModelInstance chunkInstance = ActiveTile.this.tile.terrainModel.addInstance(this.i);
						chunkInstance.setScene(TerrainWdt.this.viewer.worldScene);
						ActiveTile.this.modelInstances.add(chunkInstance);
					}

					// internet says: floor((32 - (axis / 533.33333)))
					final long indexX = chunk.getIndexX();
					final long indexY = chunk.getIndexY();

					final long war3ChunkIndexX = 15 - indexY;
					final long war3ChunkIndexY = indexX;

					final float[][] heightMap = chunk.getHeightMap();
					final long[] shadowMap = chunk.getShadows();
					for (int i = 0; i < 9; i++) {
						for (int j = 0; j < 9; j++) {
							final float heightValue = heightMap[i][j];

							final int war3IndexX = (int) ((war3ChunkIndexX * 8) + (9 - i - 1));
							final int war3IndexY = (int) ((war3ChunkIndexY * 8) + j);

							ActiveTile.this.groundCornerHeights[(war3IndexX * ActiveTile.this.width)
									+ war3IndexY] = heightValue * WdtChunkModelInstance.wowToHiveWEFactor;
						}
					}
					if (shadowMap != null) {
						for (int i = 0; i < 9; i++) {
							for (int j = 0; j < 9; j++) {
//								final float heightValue = heightMap[i][j];
//
//								final int war3IndexX = (int) ((war3ChunkIndexX * 8) + (9 - i - 1));
//								final int war3IndexY = (int) ((war3ChunkIndexY * 8) + j);
//
//								ActiveTile.this.groundCornerHeights[(war3IndexX * ActiveTile.this.width)
//										+ war3IndexY] = heightValue * WdtChunkModelInstance.wowToHiveWEFactor;
							}
						}
					}

					final List<MapChunkLiquidLayer> mapChunkLiquidLayers = chunk.getMapChunkLiquidLayers();
					for (final MapChunkLiquidLayer layer : mapChunkLiquidLayers) {
						for (int i = 0; i < 9; i++) {
							for (int j = 0; j < 9; j++) {
								final Object vert = layer.verts[i][j];
								float height = 0;
								byte depth = 0;
								if (vert instanceof SOVert) {
									final SOVert soVert = (SOVert) vert;
									height = soVert.height;
									depth = soVert.depth;
								}
								else if (vert instanceof SWVert) {
									final SWVert swVert = (SWVert) vert;
									height = swVert.height;
									depth = swVert.depth;
								}
								height = Math.max(layer.minHeight, Math.min(layer.maxHeight, height));
								final int war3IndexX = (int) ((war3ChunkIndexX * 8) + (9 - i - 1));
								final int war3IndexY = (int) ((war3ChunkIndexY * 8) + j);

								ActiveTile.this.waterExistsData[(war3IndexX * ActiveTile.this.width) + war3IndexY] = 1;
								ActiveTile.this.waterHeights[(war3IndexX * ActiveTile.this.width)
										+ war3IndexY] = (height * WdtChunkModelInstance.wowToHiveWEFactor);
							}
						}
					}

					final long[] doodadReferences = chunk.getDoodadReferences();
					if (doodadReferences != null) {
						for (final long ref : doodadReferences) {
							if (ref < ActiveTile.this.tile.tileHeader.doodads.size()) {
								if (ActiveTile.this.usedSet.add(ref)) {
									final DoodadDefinition doodad = ActiveTile.this.tile.tileHeader.doodads
											.get((int) ref);
									final long nameId = doodad.getNameId();
									final float[] position = doodad.getPosition();
									final float[] rotation = doodad.getRotation();
									final float scale = doodad.getScale();

									final List<War3ID> doodadNameKeys = TerrainWdt.this.tileHeaderToDoodadIds
											.get(ActiveTile.this.tile.tileHeader);
									final War3ID nameKey = doodadNameKeys.get((int) nameId);
									final GameObject row = TerrainWdt.this.viewer.getAllObjectData().getDoodads()
											.get(nameKey);
									final float finalScale = scale * WdtChunkModelInstance.wowToWc3Factor;

									final float[] location = {
											(((position[0]) * WdtChunkModelInstance.wowToWc3Factor))
													+ TerrainWdt.this.centerOffset[0],
											(((WdtChunkModelInstance.tilesize * 64) - (position[2]))
													* WdtChunkModelInstance.wowToWc3Factor)
													+ TerrainWdt.this.centerOffset[1],
											position[1] * WdtChunkModelInstance.wowToWc3Factor };

									final RenderDoodad renderDoodad = TerrainWdt.this.viewer.createWdtDoodad(row, 0,
											location, rotation, finalScale, (doodad.getFlags() & 0x2) != 0);
									ActiveTile.this.renderDoodads.add(renderDoodad);
									// ---
								}
							}
						}
					}
				}
				return this.i >= ActiveTile.this.tile.tileHeader.chunks.size();
			}
		}

		public boolean intersectRayTerrain(final Ray gdxRayHeap, final Vector3 out, final boolean intersectWithWater) {
			if ((this.softwareGroundMesh == null) || (this.softwareWaterAndGroundMesh == null)) {
				return false;
			}
			if (intersectWithWater) {
				return RenderMathUtils.intersectRayTriangles(gdxRayHeap, this.softwareWaterAndGroundMesh.vertices,
						this.softwareWaterAndGroundMesh.indices, 3, out);
			}
			else {
				return RenderMathUtils.intersectRayTriangles(gdxRayHeap, this.softwareGroundMesh.vertices,
						this.softwareGroundMesh.indices, 3, out);
			}
		}
	}

	public static interface DynamicTask {
		/* return true if complete */
		boolean run();
	}
}
