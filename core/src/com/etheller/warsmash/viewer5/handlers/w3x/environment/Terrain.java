package com.etheller.warsmash.viewer5.handlers.w3x.environment;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.function.Consumer;

import org.apache.commons.compress.utils.IOUtils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.etheller.warsmash.datasources.DataSource;
import com.etheller.warsmash.parsers.w3x.w3e.Corner;
import com.etheller.warsmash.parsers.w3x.w3e.War3MapW3e;
import com.etheller.warsmash.parsers.w3x.w3i.War3MapW3i;
import com.etheller.warsmash.parsers.w3x.wpm.War3MapWpm;
import com.etheller.warsmash.units.DataTable;
import com.etheller.warsmash.units.Element;
import com.etheller.warsmash.util.ImageUtils;
import com.etheller.warsmash.util.ImageUtils.AnyExtensionImage;
import com.etheller.warsmash.util.RenderMathUtils;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.util.WorldEditStrings;
import com.etheller.warsmash.viewer5.Camera;
import com.etheller.warsmash.viewer5.PathSolver;
import com.etheller.warsmash.viewer5.RawOpenGLTextureResource;
import com.etheller.warsmash.viewer5.Texture;
import com.etheller.warsmash.viewer5.gl.DataTexture;
import com.etheller.warsmash.viewer5.gl.Extensions;
import com.etheller.warsmash.viewer5.gl.WebGL;
import com.etheller.warsmash.viewer5.handlers.w3x.DynamicShadowManager;
import com.etheller.warsmash.viewer5.handlers.w3x.SplatModel;
import com.etheller.warsmash.viewer5.handlers.w3x.SplatModel.SplatMover;
import com.etheller.warsmash.viewer5.handlers.w3x.Variations;
import com.etheller.warsmash.viewer5.handlers.w3x.W3xSceneLightManager;
import com.etheller.warsmash.viewer5.handlers.w3x.W3xShaders;
import com.etheller.warsmash.viewer5.handlers.w3x.War3MapViewer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CFogMaskSettings;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.vision.CPlayerFogOfWar;

public class Terrain {
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
	public List<GroundTexture> groundTextures = new ArrayList<>();
	public List<UnloadedTexture> cliffTextures = new ArrayList<>();
	public RenderCorner[][] corners;
	public int columns;
	public int rows;
	public int blightTextureIndex = -1;
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
	private final Map<String, Integer> groundTextureToId = new HashMap<>();
	private final List<Integer> cliffToGroundTexture = new ArrayList<>();
	private final List<IVec3> cliffs = new ArrayList<>();
	private final DataSource dataSource;
	private final float[] groundHeights;
	private final float[] groundCornerHeights;
	private final short[] groundTextureList;
	private final float[] waterHeights;
	private final byte[] waterExistsData;

	private int groundTextureData = -1;
	private final int groundHeight;
	private final int groundCornerHeight;
	private final int groundCornerHeightLinear;
	private final int cliffTextureArray;
	private final int waterHeight;
	private final int waterExists;
	private final int waterTextureArray;
	private final Camera camera;
	private final War3MapViewer viewer;
	public float[] centerOffset;
	private final WebGL webGL;
	private final ShaderProgram uberSplatShader;
	public final DataTable uberSplatTable;

	private final Map<String, SplatModel> uberSplatModels;
	private final List<SplatModel> uberSplatModelsList;
	private int shadowMap;
	private int fogOfWarMap;
	public final Map<String, Splat> splats = new HashMap<>();
	public final Map<String, List<float[]>> shadows = new HashMap<>();
	public final Map<String, Texture> shadowTextures = new HashMap<>();
	private final int[] mapBounds;
	private final float[] shaderMapBounds;
	private final int[] mapSize;
	public final SoftwareGroundMesh softwareGroundMesh;
	public final SoftwareWaterAndGroundMesh softwareWaterAndGroundMesh;
	private final int testArrayBuffer;
	private final int testElementBuffer;
	private boolean initShadowsFinished = false;
	private byte[] staticShadowData;
	private byte[] shadowData;
	private CPlayerFogOfWar fogOfWarData;
	private ByteBuffer visualFogData;

	public Terrain(final War3MapW3e w3eFile, final War3MapWpm terrainPathing, final War3MapW3i w3iFile,
			final WebGL webGL, final DataSource dataSource, final WorldEditStrings worldEditStrings,
			final War3MapViewer viewer, final DataTable worldEditData) throws IOException {
		this.webGL = webGL;
		this.viewer = viewer;
		this.camera = viewer.worldScene.camera;
		this.dataSource = dataSource;
		final String texturesExt = ".blp";
		final Corner[][] corners = w3eFile.getCorners();
		this.corners = new RenderCorner[corners[0].length][corners.length];
		for (int i = 0; i < corners.length; i++) {
			for (int j = 0; j < corners[i].length; j++) {
				this.corners[j][i] = new RenderCorner(corners[i][j]);
			}
		}
		final int width = w3eFile.getMapSize()[0];
		final int height = w3eFile.getMapSize()[1];
		this.columns = width;
		this.rows = height;
		for (int i = 0; i < (width - 1); i++) {
			for (int j = 0; j < (height - 1); j++) {
				final RenderCorner bottomLeft = this.corners[i][j];
				final RenderCorner bottomRight = this.corners[i + 1][j];
				final RenderCorner topLeft = this.corners[i][j + 1];
				final RenderCorner topRight = this.corners[i + 1][j + 1];

				bottomLeft.cliff = (bottomLeft.getLayerHeight() != bottomRight.getLayerHeight())
						|| (bottomLeft.getLayerHeight() != topLeft.getLayerHeight())
						|| (bottomLeft.getLayerHeight() != topRight.getLayerHeight());
			}
		}

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

		Map<String, Integer> cliffVars = Variations.CLIFF_VARS;
		for (final Map.Entry<String, Integer> cliffVar : cliffVars.entrySet()) {
			final Integer maxVariations = cliffVar.getValue();
			for (int variation = 0; variation <= maxVariations; variation++) {
				final String fileName = "Doodads\\Terrain\\Cliffs\\Cliffs" + cliffVar.getKey() + variation + ".mdx";
				this.cliffMeshes.add(new CliffMesh(fileName, dataSource, Gdx.gl30));
				this.pathToCliff.put("Cliffs" + cliffVar.getKey() + variation, this.cliffMeshes.size() - 1);
			}
		}
		cliffVars = Variations.CITY_CLIFF_VARS;
		for (final Map.Entry<String, Integer> cliffVar : cliffVars.entrySet()) {
			final Integer maxVariations = cliffVar.getValue();
			for (int variation = 0; variation <= maxVariations; variation++) {
				final String fileName = "Doodads\\Terrain\\CityCliffs\\CityCliffs" + cliffVar.getKey() + variation
						+ ".mdx";
				this.cliffMeshes.add(new CliffMesh(fileName, dataSource, Gdx.gl30));
				this.pathToCliff.put("CityCliffs" + cliffVar.getKey() + variation, this.cliffMeshes.size() - 1);
			}
		}

		// Ground textures
		for (final War3ID groundTile : w3eFile.getGroundTiles()) {
			final Element terrainTileInfo = this.terrainTable.get(groundTile.asStringValue());
			if (terrainTileInfo == null) {
				throw new RuntimeException("No terrain info for: " + groundTile.asStringValue());
			}
			final String dir = terrainTileInfo.getField("dir");
			final String file = terrainTileInfo.getField("file");
			this.groundTextures
					.add(new GroundTexture(dir + "\\" + file + texturesExt, terrainTileInfo, dataSource, Gdx.gl30));
			this.groundTextureToId.put(groundTile.asStringValue(), this.groundTextures.size() - 1);
		}

		final Element tilesets = worldEditData.get("TileSets");

		this.blightTextureIndex = this.groundTextures.size();
		this.groundTextures
				.add(new GroundTexture(tilesets.getField(Character.toString(tileset)).split(",")[1] + texturesExt, null,
						dataSource, Gdx.gl30));

		// Cliff Textures
		for (final War3ID cliffTile : w3eFile.getCliffTiles()) {
			final Element cliffInfo = this.cliffTable.get(cliffTile.asStringValue());
			if (cliffInfo == null) {
				System.err.println("Missing cliff type: " + cliffTile.asStringValue());
				continue;
			}
			final String texDir = cliffInfo.getField("texDir");
			final String texFile = cliffInfo.getField("texFile");
			final AnyExtensionImage imageInfo = ImageUtils.getAnyExtensionImageFixRGB(dataSource,
					texDir + "\\" + texFile + texturesExt, "cliff texture");
			final BufferedImage image = imageInfo.getRGBCorrectImageData();
			this.cliffTextures
					.add(new UnloadedTexture(image.getWidth(), image.getHeight(), ImageUtils.getTextureBuffer(image),
							cliffInfo.getField("cliffModelDir"), cliffInfo.getField("rampModelDir")));
			this.cliffTexturesSize = Math.max(this.cliffTexturesSize,
					this.cliffTextures.get(this.cliffTextures.size() - 1).width);
			this.cliffToGroundTexture.add(this.groundTextureToId.get(cliffInfo.getField("groundTile")));
		}

		updateCliffMeshes(new Rectangle(0, 0, width - 1, height - 1));

		// prepare GPU data
		this.groundHeights = new float[width * height];
		this.groundCornerHeights = new float[width * height];
		this.groundTextureList = new short[(width - 1) * (height - 1) * 4];
		this.waterHeights = new float[width * height];
		this.waterExistsData = new byte[width * height];

		updateGroundTextures(new Rectangle(0, 0, width - 1, height - 1));
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				this.groundCornerHeights[(j * width) + i] = this.corners[i][j].computeFinalGroundHeight();
				this.waterExistsData[(j * width) + i] = (byte) this.corners[i][j].getWater();
				this.groundHeights[(j * width) + i] = this.corners[i][j].getGroundHeight();
				this.waterHeights[(j * width) + i] = this.corners[i][j].getWaterHeight();
			}
		}

		final GL30 gl = Gdx.gl30;
		// Ground
		this.groundTextureData = gl.glGenTexture();
		gl.glBindTexture(GL30.GL_TEXTURE_2D, this.groundTextureData);
		gl.glTexImage2D(GL30.GL_TEXTURE_2D, 0, GL30.GL_RGBA16UI, width - 1, height - 1, 0, GL30.GL_RGBA_INTEGER,
				GL30.GL_UNSIGNED_SHORT, RenderMathUtils.wrapShort(this.groundTextureList));
		gl.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MAG_FILTER, GL30.GL_NEAREST);
		gl.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MIN_FILTER, GL30.GL_NEAREST);

		this.groundHeight = gl.glGenTexture();
		gl.glBindTexture(GL30.GL_TEXTURE_2D, this.groundHeight);
		gl.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MAG_FILTER, GL30.GL_LINEAR);
		gl.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MIN_FILTER, GL30.GL_LINEAR);

		gl.glTexImage2D(GL30.GL_TEXTURE_2D, 0, GL30.GL_R16F, width, height, 0, GL30.GL_RED, GL30.GL_FLOAT,
				RenderMathUtils.wrap(this.groundHeights));
		gl.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_WRAP_S, GL30.GL_CLAMP_TO_EDGE);
		gl.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_WRAP_T, GL30.GL_CLAMP_TO_EDGE);

		this.groundCornerHeight = gl.glGenTexture();
		gl.glBindTexture(GL30.GL_TEXTURE_2D, this.groundCornerHeight);
		gl.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MAG_FILTER, GL30.GL_NEAREST);
		gl.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MIN_FILTER, GL30.GL_NEAREST);

		gl.glTexImage2D(GL30.GL_TEXTURE_2D, 0, GL30.GL_R16F, width, height, 0, GL30.GL_RED, GL30.GL_FLOAT,
				RenderMathUtils.wrap(this.groundCornerHeights));
		gl.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_WRAP_S, GL30.GL_CLAMP_TO_EDGE);
		gl.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_WRAP_T, GL30.GL_CLAMP_TO_EDGE);

		this.groundCornerHeightLinear = gl.glGenTexture();
		gl.glBindTexture(GL30.GL_TEXTURE_2D, this.groundCornerHeightLinear);
		gl.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MAG_FILTER, GL30.GL_LINEAR);
		gl.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MIN_FILTER, GL30.GL_LINEAR);

		gl.glTexImage2D(GL30.GL_TEXTURE_2D, 0, GL30.GL_R16F, width, height, 0, GL30.GL_RED, GL30.GL_FLOAT,
				RenderMathUtils.wrap(this.groundCornerHeights));
		gl.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_WRAP_S, GL30.GL_CLAMP_TO_EDGE);
		gl.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_WRAP_T, GL30.GL_CLAMP_TO_EDGE);

		// Cliff
		this.cliffTextureArray = gl.glGenTexture();
		gl.glBindTexture(GL30.GL_TEXTURE_2D_ARRAY, this.cliffTextureArray);
		gl.glTexImage3D(GL30.GL_TEXTURE_2D_ARRAY, 0, GL30.GL_RGBA8, this.cliffTexturesSize, this.cliffTexturesSize,
				this.cliffTextures.size(), 0, GL30.GL_RGBA, GL30.GL_UNSIGNED_BYTE, null);
		gl.glTexParameteri(GL30.GL_TEXTURE_2D_ARRAY, GL30.GL_TEXTURE_MIN_FILTER, GL30.GL_LINEAR_MIPMAP_LINEAR);
		gl.glTexParameteri(GL30.GL_TEXTURE_2D_ARRAY, GL30.GL_TEXTURE_BASE_LEVEL, 0);

		int sub = 0;
		for (final UnloadedTexture i : this.cliffTextures) {
			gl.glTexSubImage3D(GL30.GL_TEXTURE_2D_ARRAY, 0, 0, 0, sub, i.width, i.height, 1, GL30.GL_RGBA,
					GL30.GL_UNSIGNED_BYTE, i.data);
			sub += 1;
		}
		gl.glGenerateMipmap(GL30.GL_TEXTURE_2D_ARRAY);

		// Water
		this.waterHeight = gl.glGenTexture();
		gl.glBindTexture(GL30.GL_TEXTURE_2D, this.waterHeight);
		gl.glTexImage2D(GL30.GL_TEXTURE_2D, 0, GL30.GL_R16F, width, height, 0, GL30.GL_RED, GL30.GL_FLOAT,
				RenderMathUtils.wrap(this.waterHeights));
		gl.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MAG_FILTER, GL30.GL_NEAREST);
		gl.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MIN_FILTER, GL30.GL_NEAREST);

		this.waterExists = gl.glGenTexture();
		gl.glBindTexture(GL30.GL_TEXTURE_2D, this.waterExists);
		gl.glPixelStorei(GL30.GL_UNPACK_ALIGNMENT, 1);
		gl.glTexImage2D(GL30.GL_TEXTURE_2D, 0, GL30.GL_R8, width, height, 0, GL30.GL_RED, GL30.GL_UNSIGNED_BYTE,
				RenderMathUtils.wrap(this.waterExistsData));
		gl.glPixelStorei(GL30.GL_UNPACK_ALIGNMENT, 4);
		gl.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MAG_FILTER, GL30.GL_NEAREST);
		gl.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MIN_FILTER, GL30.GL_NEAREST);

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

		updateGroundHeights(new Rectangle(0, 0, width - 1, height - 1));

		this.groundShader = webGL.createShaderProgram(TerrainShaders.Terrain.vert(), TerrainShaders.Terrain.frag);
		this.cliffShader = webGL.createShaderProgram(TerrainShaders.Cliffs.vert(), TerrainShaders.Cliffs.frag);
		this.waterShader = webGL.createShaderProgram(TerrainShaders.Water.vert(), TerrainShaders.Water.frag);

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
		this.softwareGroundMesh = new SoftwareGroundMesh(this.groundHeights, this.groundCornerHeights,
				this.centerOffset, width, height);
		this.softwareWaterAndGroundMesh = new SoftwareWaterAndGroundMesh(this.waterHeightOffset,
				this.groundCornerHeights, this.waterHeights, this.waterExistsData, this.centerOffset, width, height);

		this.testArrayBuffer = gl.glGenBuffer();
		gl.glBindBuffer(GL30.GL_ARRAY_BUFFER, this.testArrayBuffer);
		gl.glBufferData(GL30.GL_ARRAY_BUFFER, this.softwareGroundMesh.vertices.length,
				RenderMathUtils.wrap(this.softwareGroundMesh.vertices), GL30.GL_STATIC_DRAW);

		this.testElementBuffer = gl.glGenBuffer();
//		gl.glBindBuffer(GL30.GL_ELEMENT_ARRAY_BUFFER, this.testElementBuffer);
//		gl.glBufferData(GL30.GL_ELEMENT_ARRAY_BUFFER, this.softwareGroundMesh.indices.length,
//				RenderMathUtils.wrap(this.softwareGroundMesh.indices), GL30.GL_STATIC_DRAW);

		this.waveBuilder = new WaveBuilder(this.mapSize, this.waterTable, viewer, this.corners, this.centerOffset,
				this.waterHeightOffset, w3eFile, w3iFile);
		this.pathingGrid = new PathingGrid(terrainPathing, this.centerOffset);
	}

	public void createWaves() {
		this.waveBuilder.createWaves(this);
	}

	private void updateGroundHeights(final Rectangle area) {
		for (int j = (int) area.y; j < (area.y + area.height); j++) {
			for (int i = (int) area.x; i < (area.x + area.width); i++) {
				this.groundHeights[(j * this.columns) + i] = this.corners[i][j].getGroundHeight();

				float rampHeight = 0f;
				// Check if in one of the configurations the bottom_left is a ramp
				XLoop: for (int xOffset = -1; xOffset <= 0; xOffset++) {
					for (int yOffset = -1; yOffset <= 0; yOffset++) {
						if (((i + xOffset) >= 0) && ((i + xOffset) < (this.columns - 1)) && ((j + yOffset) >= 0)
								&& ((j + yOffset) < (this.rows - 1))) {
							final RenderCorner bottomLeft = this.corners[i + xOffset][j + yOffset];
							final RenderCorner bottomRight = this.corners[i + 1 + xOffset][j + yOffset];
							final RenderCorner topLeft = this.corners[i + xOffset][j + 1 + yOffset];
							final RenderCorner topRight = this.corners[i + 1 + xOffset][j + 1 + yOffset];

							final int base = Math.min(
									Math.min(bottomLeft.getLayerHeight(), bottomRight.getLayerHeight()),
									Math.min(topLeft.getLayerHeight(), topRight.getLayerHeight()));
							if (this.corners[i][j].getLayerHeight() != base) {
								continue;
							}

							if (isCornerRampEntrance(i + xOffset, j + yOffset)) {
								rampHeight = 0.5f;
								break XLoop;
							}
						}
					}
				}

				final RenderCorner corner = this.corners[i][j];
				final float newGroundCornerHeight = corner.computeFinalGroundHeight() + rampHeight;
				this.groundCornerHeights[(j * this.columns) + i] = newGroundCornerHeight;
				corner.depth = corner.getWater() != 0
						? (this.waterHeightOffset + corner.getWaterHeight()) - newGroundCornerHeight
						: 0;
			}
		}
		updateGroundHeights();
		updateCornerHeights();
	}

	private void updateGroundHeights() {
		Gdx.gl30.glBindTexture(GL30.GL_TEXTURE_2D, this.groundHeight);
		Gdx.gl30.glTexSubImage2D(GL30.GL_TEXTURE_2D, 0, 0, 0, this.columns, this.rows, GL30.GL_RED, GL30.GL_FLOAT,
				RenderMathUtils.wrap(this.groundHeights));
	}

	private void updateCornerHeights() {
		final FloatBuffer groundCornerHeightsWrapped = RenderMathUtils.wrap(this.groundCornerHeights);
		Gdx.gl30.glBindTexture(GL30.GL_TEXTURE_2D, this.groundCornerHeight);
		Gdx.gl30.glTexSubImage2D(GL30.GL_TEXTURE_2D, 0, 0, 0, this.columns, this.rows, GL30.GL_RED, GL30.GL_FLOAT,
				groundCornerHeightsWrapped);
		Gdx.gl30.glBindTexture(GL30.GL_TEXTURE_2D, this.groundCornerHeightLinear);
		Gdx.gl30.glTexSubImage2D(GL30.GL_TEXTURE_2D, 0, 0, 0, this.columns, this.rows, GL30.GL_RED, GL30.GL_FLOAT,
				groundCornerHeightsWrapped);
	}

	/**
	 * calculateRamps() is copied from Riv whereas a lot of the rest of the terrain
	 * was copied from HiveWE
	 */
	private void calculateRamps() {
		final int columns = this.mapSize[0];
		final int rows = this.mapSize[1];

		final String[] ramps = { "AAHL", "AALH", "ABHL", "AHLA", "ALHA", "ALHB", "BALH", "BHLA", "HAAL", "HBAL", "HLAA",
				"HLAB", "LAAH", "LABH", "LHAA", "LHBA" };

		// Adjust terrain height inside ramps (set rampAdjust)
		for (int y = 1; y < (rows - 1); ++y) {
			for (int x = 1; x < (columns - 1); ++x) {
				final RenderCorner o = this.corners[x][y];
				if (!o.isRamp()) {
					continue;
				}
				final RenderCorner a = this.corners[x - 1][y - 1];
				final RenderCorner b = this.corners[x - 1][y];
				final RenderCorner c = this.corners[x - 1][y + 1];
				final RenderCorner d = this.corners[x][y + 1];
				final RenderCorner e = this.corners[x + 1][y + 1];
				final RenderCorner f = this.corners[x + 1][y];
				final RenderCorner g = this.corners[x + 1][y - 1];
				final RenderCorner h = this.corners[x][y - 1];
				final int base = o.getLayerHeight();
				if ((b.isRamp() && f.isRamp()) || (d.isRamp() && h.isRamp())) {
					float adjust = 0;
					if (b.isRamp() && f.isRamp()) {
						adjust = Math.max(adjust, ((b.getLayerHeight() + f.getLayerHeight()) / 2) - base);
					}
					if (d.isRamp() && h.isRamp()) {
						adjust = Math.max(adjust, ((d.getLayerHeight() + h.getLayerHeight()) / 2) - base);
					}
					if (a.isRamp() && e.isRamp()) {
						adjust = Math.max(adjust, (((a.getLayerHeight() + e.getLayerHeight()) / 2) - base) / 2);
					}
					if (c.isRamp() && g.isRamp()) {
						adjust = Math.max(adjust, (((c.getLayerHeight() + g.getLayerHeight()) / 2) - base) / 2);
					}
					o.rampAdjust = adjust;
				}
			}
		}
	}

	/// TODO clean
	/// Function is a bit of a mess
	/// Updates the cliff and ramp meshes for an area
	private void updateCliffMeshes(final Rectangle area) throws IOException {
		// Remove all existing cliff meshes in area
		for (int i = this.cliffs.size(); i-- > 0;) {
			final IVec3 pos = this.cliffs.get(i);
			if (area.contains(pos.x, pos.y)) {
				this.cliffs.remove(i);
			}
		}

		for (int i = (int) area.getX(); i < (int) (area.getX() + area.getWidth()); i++) {
			for (int j = (int) area.getY(); j < (int) (area.getY() + area.getHeight()); j++) {
				this.corners[i][j].romp = false;
			}
		}

		final Rectangle adjusted = new Rectangle(area.x - 2, area.y - 2, area.width + 4, area.height + 4);
		final Rectangle rampArea = new Rectangle();
		Intersector.intersectRectangles(new Rectangle(0, 0, this.columns, this.rows), adjusted, rampArea);

		// Add new cliff meshes
		final int xLimit = (int) ((rampArea.getX() + rampArea.getWidth()) - 1);
		for (int i = (int) rampArea.getX(); i < xLimit; i++) {
			final int yLimit = (int) ((rampArea.getY() + rampArea.getHeight()) - 1);
			for (int j = (int) rampArea.getY(); j < yLimit; j++) {
				final RenderCorner bottomLeft = this.corners[i][j];
				final RenderCorner bottomRight = this.corners[i + 1][j];
				final RenderCorner topLeft = this.corners[i][j + 1];
				final RenderCorner topRight = this.corners[i + 1][j + 1];

				if (bottomLeft.cliff && !bottomLeft.hideCliff) {
					final int base = Math.min(Math.min(bottomLeft.getLayerHeight(), bottomRight.getLayerHeight()),
							Math.min(topLeft.getLayerHeight(), topRight.getLayerHeight()));

					final boolean facingDown = (topLeft.getLayerHeight() >= bottomLeft.getLayerHeight())
							&& (topRight.getLayerHeight() >= bottomRight.getLayerHeight());
					final boolean facingLeft = (bottomRight.getLayerHeight() >= bottomLeft.getLayerHeight())
							&& (topRight.getLayerHeight() >= topLeft.getLayerHeight());

					int bottomLeftCliffTex = bottomLeft.getCliffTexture();
					if (bottomLeftCliffTex == 15) {
						boolean foundTexture = false;
						final int topLeftCliffTex = topLeft.getCliffTexture();
						if (topLeftCliffTex == 15) {
							final int topRightCliffTex = topRight.getCliffTexture();
							if (topRightCliffTex == 15) {
								final int bottomRightCliffTex = bottomRight.getCliffTexture();
								if (bottomRightCliffTex == 15) {
									bottomLeftCliffTex -= 14;
								}
								else {
									bottomLeftCliffTex = bottomRightCliffTex;
									foundTexture = true;
								}
							}
							else {
								bottomLeftCliffTex = topRightCliffTex;
								foundTexture = true;
							}
						}
						else {
							bottomLeftCliffTex = topLeftCliffTex;
							foundTexture = true;
						}
						if (foundTexture) {
							bottomLeft.setCliffTexture(bottomLeftCliffTex);
						}
					}
					if (!(facingDown && (j == 0)) && !(!facingDown && (j >= (this.rows - 2)))
							&& !(facingLeft && (i == 0)) && !(!facingLeft && (i >= (this.columns - 2)))) {
						final boolean verticalRamp = (bottomLeft.isRamp() != bottomRight.isRamp())
								&& (topLeft.isRamp() != topRight.isRamp());

						final boolean horizontalRamp = (bottomLeft.isRamp() != topLeft.isRamp())
								&& (bottomRight.isRamp() != topRight.isRamp());

						if (verticalRamp || horizontalRamp) {
							final boolean rampBlockedByCliff = (verticalRamp
									&& this.corners[i][j + (facingDown ? -1 : 1)].cliff)
									|| (horizontalRamp && this.corners[i + (facingLeft ? -1 : 1)][j].cliff);
							final int topLeftHeight = topLeft.getLayerHeight() - base;
							final int topRightHeight = topRight.getLayerHeight() - base;
							final int bottomRightHeight = bottomRight.getLayerHeight() - base;
							final int bottomLeftHeight = bottomLeft.getLayerHeight() - base;
							boolean invalidRamp = false;
							if (DISALLOW_HEIGHT_3_RAMPS) {
								if (rampBlockedByCliff) {
									invalidRamp = true;
								}
								else if (topLeftHeight > 1) {
									invalidRamp = true;
									topLeft.setRamp(0);
								}
								else if (topRightHeight > 1) {
									invalidRamp = true;
									topRight.setRamp(0);
								}
								else if (bottomRightHeight > 1) {
									invalidRamp = true;
									bottomRight.setRamp(0);
								}
								else if (bottomLeftHeight > 1) {
									invalidRamp = true;
									bottomLeft.setRamp(0);
								}
							}
							if (!invalidRamp) {
								String fileName = "" + getRampLetter(topLeftHeight, topLeft.isRamp())
										+ getRampLetter(topRightHeight, topRight.isRamp())
										+ getRampLetter(bottomRightHeight, bottomRight.isRamp())
										+ getRampLetter(bottomLeftHeight, bottomLeft.isRamp());

								final String rampModelDir = this.cliffTextures.get(bottomLeftCliffTex).rampModelDir;
								fileName = "Doodads\\Terrain\\" + rampModelDir + "\\" + rampModelDir + fileName
										+ "0.mdx";

								if (this.dataSource.has(fileName)) {
									if (!this.pathToCliff.containsKey(fileName)) {
										this.cliffMeshes.add(new CliffMesh(fileName, this.dataSource, Gdx.gl30));
										this.pathToCliff.put(fileName, this.cliffMeshes.size() - 1);
									}

									for (int ji = this.cliffs.size(); ji-- > 0;) {
										final IVec3 pos = this.cliffs.get(ji);
										if ((pos.x == (i + ((horizontalRamp ? 1 : 0) * (facingLeft ? -1 : 0))))
												&& (pos.y == (j - ((verticalRamp ? 1 : 0) * (facingDown ? 1 : 0))))) {
											this.cliffs.remove(ji);
											break;
										}
									}

									this.cliffs.add(new IVec3(i + ((horizontalRamp ? 1 : 0) * (facingLeft ? -1 : 0)),
											j - ((verticalRamp ? 1 : 0) * (facingDown ? 1 : 0)),
											this.pathToCliff.get(fileName)));
									bottomLeft.romp = true;
									bottomLeft.setCliffTexture(bottomLeftCliffTex);
									bottomRight.setCliffTexture(bottomLeftCliffTex);
									topLeft.setCliffTexture(bottomLeftCliffTex);
									topRight.setCliffTexture(bottomLeftCliffTex);
									this.corners[i + ((facingLeft ? -1 : 1) * (horizontalRamp ? 1 : 0))][j
											+ ((facingDown ? -1 : 1) * (verticalRamp ? 1 : 0))]
											.setCliffTexture(bottomLeftCliffTex);

									this.corners[i + ((facingLeft ? -1 : 1) * (horizontalRamp ? 1 : 0))][j
											+ ((facingDown ? -1 : 1) * (verticalRamp ? 1 : 0))].romp = true;

									continue;
								}
							}
						}
					}

					if (isCornerRampEntrance(i, j)) {
						continue;
					}

					// Ramps move 1 right/down in some cases and thus their area is one bigger to
					// the top and left.
					if (!area.contains(i, j)) {
						continue;
					}

					// Cliff model path

					String fileName = "" + (char) (('A' + topLeft.getLayerHeight()) - base)
							+ (char) (('A' + topRight.getLayerHeight()) - base)
							+ (char) (('A' + bottomRight.getLayerHeight()) - base)
							+ (char) (('A' + bottomLeft.getLayerHeight()) - base);

					if ("AAAA".equals(fileName)) {
						continue;
					}

					// Clamp to within max variations

					fileName = this.cliffTextures.get(bottomLeftCliffTex).cliffModelDir + fileName
							+ Variations.getCliffVariation(this.cliffTextures.get(bottomLeftCliffTex).cliffModelDir,
									fileName, bottomLeft.getCliffVariation());
					if (!this.pathToCliff.containsKey(fileName)) {
						throw new IllegalArgumentException("No such pathToCliff entry: " + fileName);
					}
					this.cliffs.add(new IVec3(i, j, this.pathToCliff.get(fileName)));
				}
			}
		}
		for (int i = (int) rampArea.getX(); i < xLimit; i++) {
			final int yLimit = (int) ((rampArea.getY() + rampArea.getHeight()) - 1);
			for (int j = (int) rampArea.getY(); j < yLimit; j++) {
				final RenderCorner bottomLeft = this.corners[i][j];
				if (bottomLeft.isRamp() && !bottomLeft.romp) {
					bottomLeft.hideCliff = false;
				}
			}
		}

	}

	public void logRomp(final int x, final int y) {
		System.out.println("romp: " + this.corners[x][y].romp);
		System.out.println("ramp: " + this.corners[x][y].isRamp());
		System.out.println("cliff: " + this.corners[x][y].cliff);
	}

	public void updateGroundTextures(final Rectangle area) {
		final Rectangle adjusted = new Rectangle(area.x - 1, area.y - 1, area.width + 2, area.height + 2);
		final Rectangle updateArea = new Rectangle();
		Intersector.intersectRectangles(new Rectangle(0, 0, this.columns - 1, this.rows - 1), adjusted, updateArea);

		for (int j = (int) updateArea.getY(); j <= (int) ((updateArea.getY() + updateArea.getHeight()) - 1); j++) {
			for (int i = (int) updateArea.getX(); i <= (int) ((updateArea.getX() + updateArea.getWidth()) - 1); i++) {
				getTextureVariations(i, j, this.groundTextureList, ((j * (this.columns - 1)) + i) * 4);

				if (this.corners[i][j].cliff || this.corners[i][j].romp) {
					if (isCornerRampEntrance(i, j)) {
						continue;
					}
					this.groundTextureList[(((j * (this.columns - 1)) + i) * 4) + 3] |= 0b1000000000000000;
				}
			}
		}

		uploadGroundTexture();
	}

	public void removeTerrainCell(final int i, final int j) {
		this.groundTextureList[(((j * (this.columns - 1)) + i) * 4) + 3] |= 0b1000000000000000;
		this.corners[i][j].hideCliff = true;
		uploadGroundTexture();
		try {
			updateCliffMeshes(new Rectangle(i - 1, j - 1, 1, 1)); // TODO does this work?
		}
		catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void removeTerrainCellWithoutFlush(final int i, final int j) {
		this.groundTextureList[(((j * (this.columns - 1)) + i) * 4) + 3] |= 0b1000000000000000;
		this.corners[i][j].hideCliff = true;
	}

	public void flushRemovedTerrainCells() {
		uploadGroundTexture();
		try {
			updateCliffMeshes(new Rectangle(0, 0, this.columns - 1, this.rows - 1));
		}
		catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void uploadGroundTexture() {
		if (this.groundTextureData != -1) {
			Gdx.gl30.glBindTexture(GL30.GL_TEXTURE_2D, this.groundTextureData);
			Gdx.gl30.glTexSubImage2D(GL30.GL_TEXTURE_2D, 0, 0, 0, this.columns - 1, this.rows - 1, GL30.GL_RGBA_INTEGER,
					GL30.GL_UNSIGNED_SHORT, RenderMathUtils.wrapShort(this.groundTextureList));
		}
	}

	/// The 4 ground textures of the tilepoint. First 5 bits are which texture array
	/// to use and the next 5 bits are which subtexture to use
	private void getTextureVariations(final int x, final int y, final short[] out, final int outStartOffset) {
		final int bottomLeft = realTileTexture(x, y);
		final int bottomRight = realTileTexture(x + 1, y);
		final int topLeft = realTileTexture(x, y + 1);
		final int topRight = realTileTexture(x + 1, y + 1);

		final TreeSet<Integer> set = new TreeSet<>();
		set.add(bottomLeft);
		set.add(bottomRight);
		set.add(topLeft);
		set.add(topRight);
		Arrays.fill(out, outStartOffset, outStartOffset + 4, (short) 17);
		int component = outStartOffset + 1;

		final Iterator<Integer> iterator = set.iterator();
		iterator.hasNext();
		final short firstValue = iterator.next().shortValue();
		out[outStartOffset] = (short) (firstValue
				+ (getVariation(firstValue, this.corners[x][y].getGroundVariation()) << 5));

		int index;
		while (iterator.hasNext()) {
			index = 0;
			final int texture = iterator.next().intValue();
			index |= (bottomRight == texture ? 1 : 0) << 0;
			index |= (bottomLeft == texture ? 1 : 0) << 1;
			index |= (topRight == texture ? 1 : 0) << 2;
			index |= (topLeft == texture ? 1 : 0) << 3;

			out[component++] = (short) (texture + (index << 5));
		}
	}

	private int realTileTexture(final int x, final int y) {
		ILoop: for (int i = -1; i < 1; i++) {
			for (int j = -1; j < 1; j++) {
				if (((x + i) >= 0) && ((x + i) < this.columns) && ((y + j) >= 0) && ((y + j) < this.rows)) {
					if (this.corners[x + i][y + j].cliff) {
						if (((x + i) < (this.columns - 1)) && ((y + j) < (this.rows - 1))) {
							final RenderCorner bottomLeft = this.corners[x + i][y + j];
							final RenderCorner bottomRight = this.corners[x + i + 1][y + j];
							final RenderCorner topLeft = this.corners[x + i][y + j + 1];
							final RenderCorner topRight = this.corners[x + i + 1][y + j + 1];

							if (bottomLeft.isRamp() && topLeft.isRamp() && bottomRight.isRamp() && topRight.isRamp()
									&& !bottomLeft.romp && !bottomRight.romp && !topLeft.romp && !topRight.romp) {
								break ILoop;
							}
						}
					}
					if (this.corners[x + i][y + j].romp || this.corners[x + i][y + j].cliff) {
						int texture = this.corners[x + i][y + j].getCliffTexture();
						// Number 15 seems to be something
						if (texture == 15) {
							texture -= 14;
						}

						return this.cliffToGroundTexture.get(texture);
					}
				}
			}
		}

		if (this.corners[x][y].getBlight() != 0) {
			return this.blightTextureIndex;
		}

		return this.corners[x][y].getGroundTexture();
	}

	private boolean isCornerRampEntrance(final int x, final int y) {
		if ((x == this.columns) || (y == this.rows)) {
			return false;
		}

		final RenderCorner bottomLeft = this.corners[x][y];
		final RenderCorner bottomRight = this.corners[x + 1][y];
		final RenderCorner topLeft = this.corners[x][y + 1];
		final RenderCorner topRight = this.corners[x + 1][y + 1];

		return bottomLeft.isRamp() && topLeft.isRamp() && bottomRight.isRamp() && topRight.isRamp()
				&& !((bottomLeft.getLayerHeight() == topRight.getLayerHeight())
						&& (topLeft.getLayerHeight() == bottomRight.getLayerHeight()));
	}

	private static void loadWaterColor(final float[] out, final String prefix, final Element waterInfo) {
		for (int i = 0; i < colorTags.length; i++) {
			final String colorTag = colorTags[i];
			out[i] = waterInfo == null ? 0.0f : waterInfo.getFieldFloatValue(prefix + "_" + colorTag) / 255f;
		}
	}

	public short getVariation(final int groundTexture, final int variation) {
		final GroundTexture texture = this.groundTextures.get(groundTexture);

		// Extended ?
		if (texture.extended) {
			if (variation <= 15) {
				return (short) (16 + variation);
			}
			else if (variation == 16) {
				return 15;
			}
			else {
				return 0;
			}
		}
		else {
			if (variation == 0) {
				return 0;
			}
			else {
				return 15;
			}
		}
	}

	public void update(final float deltaTime) {
		this.waterIndex += this.waterIncreasePerFrame * deltaTime;

		if (this.waterIndex >= this.waterTextureCount) {
			this.waterIndex = 0;
		}
	}

	public void renderGround(final DynamicShadowManager dynamicShadowManager) {
		// Render tiles

		this.webGL.useShaderProgram(this.groundShader);

		final GL30 gl = Gdx.gl30;
		gl.glEnable(GL20.GL_CULL_FACE);
		gl.glDisable(GL30.GL_BLEND);
		gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		gl.glEnable(GL20.GL_DEPTH_TEST);
		gl.glDepthMask(true);

		gl.glUniformMatrix4fv(this.groundShader.getUniformLocation("MVP"), 1, false,
				this.camera.viewProjectionMatrix.val, 0);
		gl.glUniform1i(this.groundShader.getUniformLocation("show_pathing_map"), this.viewer.renderPathing);
		gl.glUniform1i(this.groundShader.getUniformLocation("show_lighting"), this.viewer.renderLighting);
		gl.glUniform1i(this.groundShader.getUniformLocation("height_texture"), 0);
		gl.glUniform1i(this.groundShader.getUniformLocation("height_cliff_texture"), 1);
		gl.glUniform1i(this.groundShader.getUniformLocation("terrain_texture_list"), 2);
		gl.glUniform1i(this.groundShader.getUniformLocation("shadowMap"), 20);
		gl.glUniform1f(this.groundShader.getUniformLocation("centerOffsetX"), this.centerOffset[0]);
		gl.glUniform1f(this.groundShader.getUniformLocation("centerOffsetY"), this.centerOffset[1]);

		final W3xSceneLightManager lightManager = (W3xSceneLightManager) this.viewer.worldScene.getLightManager();
		final DataTexture unitLightsTexture = lightManager.getTerrainLightsTexture();

		unitLightsTexture.bind(21);
		gl.glUniform1i(this.groundShader.getUniformLocation("lightTexture"), 21);
		gl.glUniform1f(this.groundShader.getUniformLocation("lightCount"), lightManager.getTerrainLightCount());
		gl.glUniform1f(this.groundShader.getUniformLocation("lightTextureHeight"), unitLightsTexture.getHeight());
		this.groundShader.setUniformf("u_fogColor", this.viewer.worldScene.fogSettings.color);
		this.groundShader.setUniformf("u_fogParams", this.viewer.worldScene.fogSettings.style.ordinal(),
				this.viewer.worldScene.fogSettings.start, this.viewer.worldScene.fogSettings.end,
				this.viewer.worldScene.fogSettings.density);

		gl.glUniformMatrix4fv(this.groundShader.getUniformLocation("DepthBiasMVP"), 1, false,
				dynamicShadowManager.getDepthBiasMVP().val, 0);

		gl.glUniform1i(this.groundShader.getUniformLocation("cliff_textures"), 0);
		gl.glActiveTexture(GL30.GL_TEXTURE0);
		gl.glBindTexture(GL30.GL_TEXTURE_2D, this.groundHeight);

		gl.glActiveTexture(GL30.GL_TEXTURE1);
		gl.glBindTexture(GL30.GL_TEXTURE_2D, this.groundCornerHeight);

		gl.glUniform1i(this.groundShader.getUniformLocation("pathing_map_static"), 2);
		gl.glActiveTexture(GL30.GL_TEXTURE2);
		gl.glBindTexture(GL30.GL_TEXTURE_2D, this.groundTextureData);

		gl.glUniform1i(this.groundShader.getUniformLocation("sample0"), 3);
		gl.glUniform1i(this.groundShader.getUniformLocation("sample1"), 4);
		gl.glUniform1i(this.groundShader.getUniformLocation("sample2"), 5);
		gl.glUniform1i(this.groundShader.getUniformLocation("sample3"), 6);
		gl.glUniform1i(this.groundShader.getUniformLocation("sample4"), 7);
		gl.glUniform1i(this.groundShader.getUniformLocation("sample5"), 8);
		gl.glUniform1i(this.groundShader.getUniformLocation("sample6"), 9);
		gl.glUniform1i(this.groundShader.getUniformLocation("sample7"), 10);
		gl.glUniform1i(this.groundShader.getUniformLocation("sample8"), 11);
		gl.glUniform1i(this.groundShader.getUniformLocation("sample9"), 12);
		gl.glUniform1i(this.groundShader.getUniformLocation("sample10"), 13);
		gl.glUniform1i(this.groundShader.getUniformLocation("sample11"), 14);
		gl.glUniform1i(this.groundShader.getUniformLocation("sample12"), 15);
		gl.glUniform1i(this.groundShader.getUniformLocation("sample13"), 16);
		gl.glUniform1i(this.groundShader.getUniformLocation("sample14"), 17);
		gl.glUniform1i(this.groundShader.getUniformLocation("sample15"), 18);
		gl.glUniform1i(this.groundShader.getUniformLocation("sample16"), 19);
		gl.glUniform1i(this.groundShader.getUniformLocation("shadowMap"), 20);
		gl.glUniform1i(this.groundShader.getUniformLocation("fogOfWarMap"), 22);
		for (int i = 0; i < this.groundTextures.size(); i++) {
			gl.glActiveTexture(GL30.GL_TEXTURE3 + i);
			gl.glBindTexture(GL30.GL_TEXTURE_2D_ARRAY, this.groundTextures.get(i).id);
		}

//		gl.glActiveTexture(GL30.GL_TEXTURE20, /*pathingMap.getTextureStatic()*/);
//		gl.glActiveTexture(GL30.GL_TEXTURE21, /*pathingMap.getTextureDynamic()*/);

		gl.glActiveTexture(GL30.GL_TEXTURE20);
		gl.glBindTexture(GL30.GL_TEXTURE_2D, this.shadowMap);

		gl.glActiveTexture(GL30.GL_TEXTURE22);
		gl.glBindTexture(GL30.GL_TEXTURE_2D, this.fogOfWarMap);

//		gl.glEnableVertexAttribArray(0);
		gl.glBindBuffer(GL30.GL_ARRAY_BUFFER, Shapes.INSTANCE.vertexBuffer);
		gl.glVertexAttribPointer(this.groundShader.getAttributeLocation("vPosition"), 2, GL30.GL_FLOAT, false, 0, 0);

		gl.glBindBuffer(GL30.GL_ELEMENT_ARRAY_BUFFER, Shapes.INSTANCE.indexBuffer);
		if (WIREFRAME_TERRAIN) {
			Extensions.wireframeExtension.glPolygonMode(GL20.GL_FRONT_AND_BACK, Extensions.GL_LINE);
		}
		gl.glDrawElementsInstanced(GL30.GL_TRIANGLES, Shapes.INSTANCE.quadIndices.length * 3, GL30.GL_UNSIGNED_INT, 0,
				(this.columns - 1) * (this.rows - 1));
		if (WIREFRAME_TERRAIN) {
			Extensions.wireframeExtension.glPolygonMode(GL20.GL_FRONT_AND_BACK, Extensions.GL_FILL);
		}

//		gl.glDisableVertexAttribArray(0);

		gl.glEnable(GL30.GL_BLEND);

	}

	public void renderUberSplats(final boolean onTopLayer) {
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
		shader.setUniform2fv("u_centerOffset", this.centerOffset, 0, 2);
		shader.setUniformi("u_texture", 1);
		shader.setUniformi("u_shadowMap", 2);
		shader.setUniformi("u_waterHeightsMap", 3);
		shader.setUniformi("u_fogOfWarMap", 4);
		shader.setUniformf("u_waterHeightOffset", this.waterHeightOffset);

		gl.glActiveTexture(GL30.GL_TEXTURE0);
		gl.glBindTexture(GL30.GL_TEXTURE_2D, this.groundCornerHeightLinear);

		gl.glActiveTexture(GL30.GL_TEXTURE2);
		gl.glBindTexture(GL30.GL_TEXTURE_2D, this.shadowMap);

		gl.glActiveTexture(GL30.GL_TEXTURE3);
		gl.glBindTexture(GL30.GL_TEXTURE_2D, this.waterHeight);

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

	public void renderWater() {
		// Render water
		this.webGL.useShaderProgram(this.waterShader);

		final GL30 gl = Gdx.gl30;
		gl.glDepthMask(false);
		gl.glEnable(GL30.GL_CULL_FACE);
		gl.glEnable(GL30.GL_BLEND);
		gl.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);

		this.waterShader.setUniformMatrix4fv("MVP", this.camera.viewProjectionMatrix.val, 0, 16);
		this.waterShader.setUniform4fv("shallow_color_min", this.minShallowColorApplied, 0, 4);
		this.waterShader.setUniform4fv("shallow_color_max", this.maxShallowColorApplied, 0, 4);
		this.waterShader.setUniform4fv("deep_color_min", this.minDeepColorApplied, 0, 4);
		this.waterShader.setUniform4fv("deep_color_max", this.maxDeepColorApplied, 0, 4);
		this.waterShader.setUniformf("water_offset", this.waterHeightOffset);
		this.waterShader.setUniformi("current_texture", (int) this.waterIndex);
		this.waterShader.setUniformf("centerOffsetX", this.centerOffset[0]);
		this.waterShader.setUniformf("centerOffsetY", this.centerOffset[1]);
		this.waterShader.setUniform4fv("mapBounds", this.shaderMapBounds, 0, 4);

		final W3xSceneLightManager lightManager = (W3xSceneLightManager) this.viewer.worldScene.getLightManager();
		final DataTexture terrainLightsTexture = lightManager.getTerrainLightsTexture();

		terrainLightsTexture.bind(3);
		this.waterShader.setUniformi("lightTexture", 3);
		this.waterShader.setUniformf("lightCount", lightManager.getTerrainLightCount());
		this.waterShader.setUniformf("lightTextureHeight", terrainLightsTexture.getHeight());
		this.waterShader.setUniformf("u_fogColor", this.viewer.worldScene.fogSettings.color);
		this.waterShader.setUniformf("u_fogParams", this.viewer.worldScene.fogSettings.style.ordinal(),
				this.viewer.worldScene.fogSettings.start, this.viewer.worldScene.fogSettings.end,
				this.viewer.worldScene.fogSettings.density);

		this.waterShader.setUniformi("water_height_texture", 0);
		this.waterShader.setUniformi("ground_height_texture", 1);
		this.waterShader.setUniformi("water_exists_texture", 2);
		this.waterShader.setUniformi("water_textures", 4);
		this.waterShader.setUniformi("fogOfWarMap", 5);
		gl.glActiveTexture(GL30.GL_TEXTURE0);
		gl.glBindTexture(GL30.GL_TEXTURE_2D, this.waterHeight);
		gl.glActiveTexture(GL30.GL_TEXTURE1);
		gl.glBindTexture(GL30.GL_TEXTURE_2D, this.groundCornerHeight);
		gl.glActiveTexture(GL30.GL_TEXTURE2);
		gl.glBindTexture(GL30.GL_TEXTURE_2D, this.waterExists);
		gl.glActiveTexture(GL30.GL_TEXTURE4);
		gl.glBindTexture(GL30.GL_TEXTURE_2D_ARRAY, this.waterTextureArray);
		gl.glActiveTexture(GL30.GL_TEXTURE5);
		gl.glBindTexture(GL30.GL_TEXTURE_2D, this.fogOfWarMap);

		gl.glBindBuffer(GL30.GL_ARRAY_BUFFER, Shapes.INSTANCE.vertexBuffer);
		gl.glVertexAttribPointer(this.waterShader.getAttributeLocation("vPosition"), 2, GL30.GL_FLOAT, false, 0, 0);

		gl.glBindBuffer(GL30.GL_ELEMENT_ARRAY_BUFFER, Shapes.INSTANCE.indexBuffer);
		gl.glDrawElementsInstanced(GL30.GL_TRIANGLES, Shapes.INSTANCE.quadIndices.length * 3, GL30.GL_UNSIGNED_INT, 0,
				(this.columns - 1) * (this.rows - 1));

		gl.glEnable(GL30.GL_BLEND);
	}

	public void renderCliffs() {

		// Render cliffs
		for (final IVec3 i : this.cliffs) {
			final RenderCorner bottomLeft = this.corners[i.x][i.y];
			final RenderCorner bottomRight = this.corners[i.x + 1][i.y];
			final RenderCorner topLeft = this.corners[i.x][i.y + 1];
			final RenderCorner topRight = this.corners[i.x + 1][i.y + 1];

			final float min = Math.min(Math.min(bottomLeft.getLayerHeight() - 2, bottomRight.getLayerHeight() - 2),
					Math.min(topLeft.getLayerHeight() - 2, topRight.getLayerHeight() - 2));

			fourComponentHeap[0] = i.x;
			fourComponentHeap[1] = i.y;
			fourComponentHeap[2] = min;
			fourComponentHeap[3] = bottomLeft.getCliffTexture();
			this.cliffMeshes.get(i.z).renderQueue(fourComponentHeap);
		}

		this.webGL.useShaderProgram(this.cliffShader);

		final GL30 gl = Gdx.gl30;
		gl.glDisable(GL30.GL_BLEND);

		// WC3 models are 128x too large
		tempMatrix.set(this.camera.viewProjectionMatrix);
		gl.glUniformMatrix4fv(this.cliffShader.getUniformLocation("MVP"), 1, false, tempMatrix.val, 0);
		gl.glUniform1i(this.cliffShader.getUniformLocation("show_lighting"), this.viewer.renderLighting);

		final W3xSceneLightManager lightManager = (W3xSceneLightManager) this.viewer.worldScene.getLightManager();
		final DataTexture unitLightsTexture = lightManager.getTerrainLightsTexture();

		unitLightsTexture.bind(21);
		gl.glUniform1i(this.cliffShader.getUniformLocation("lightTexture"), 21);
		gl.glUniform1f(this.cliffShader.getUniformLocation("lightCount"), lightManager.getTerrainLightCount());
		gl.glUniform1f(this.cliffShader.getUniformLocation("lightTextureHeight"), unitLightsTexture.getHeight());
		this.cliffShader.setUniformf("u_fogColor", this.viewer.worldScene.fogSettings.color);
		this.cliffShader.setUniformf("u_fogParams", this.viewer.worldScene.fogSettings.style.ordinal(),
				this.viewer.worldScene.fogSettings.start, this.viewer.worldScene.fogSettings.end,
				this.viewer.worldScene.fogSettings.density);

		this.cliffShader.setUniformi("shadowMap", 2);
		gl.glActiveTexture(GL30.GL_TEXTURE2);
		gl.glBindTexture(GL30.GL_TEXTURE_2D, this.shadowMap);

		this.cliffShader.setUniformi("fogOfWarMap", 3);
		gl.glActiveTexture(GL30.GL_TEXTURE3);
		gl.glBindTexture(GL30.GL_TEXTURE_2D, this.fogOfWarMap);

		this.cliffShader.setUniformi("cliff_textures", 0);
		gl.glActiveTexture(GL30.GL_TEXTURE0);
		gl.glBindTexture(GL30.GL_TEXTURE_2D_ARRAY, this.cliffTextureArray);
		this.cliffShader.setUniformi("height_texture", 1);
		gl.glActiveTexture(GL30.GL_TEXTURE1);
		gl.glBindTexture(GL30.GL_TEXTURE_2D, this.groundHeight);
//		gl.glActiveTexture(GL30.GL_TEXTURE2);
		for (final CliffMesh i : this.cliffMeshes) {
			gl.glUniform1f(this.cliffShader.getUniformLocation("centerOffsetX"), this.centerOffset[0]);
			gl.glUniform1f(this.cliffShader.getUniformLocation("centerOffsetY"), this.centerOffset[1]);
			i.render(this.cliffShader);
		}
	}

	public BuildingShadow addShadow(final String file, final float shadowX, final float shadowY) {
		if (!this.shadows.containsKey(file)) {
			final String path = "ReplaceableTextures\\Shadows\\" + file + ".blp";
			this.shadows.put(file, new ArrayList<>());
			this.shadowTextures.put(file, (Texture) this.viewer.load(path, PathSolver.DEFAULT, null));
		}
		final List<float[]> shadowList = this.shadows.get(file);
		final float[] shadowPositionArray = new float[] { shadowX, shadowY };
		shadowList.add(shadowPositionArray);
		if (this.initShadowsFinished) {
			final Texture texture = this.shadowTextures.get(file);

			final int columns = (this.columns - 1) * 4;
			final int rows = (this.rows - 1) * 4;
			blitShadowData(columns, rows, shadowX, shadowY, texture);
			final GL30 gl = Gdx.gl30;
			gl.glBindTexture(GL30.GL_TEXTURE_2D, this.shadowMap);
			gl.glTexImage2D(GL30.GL_TEXTURE_2D, 0, GL30.GL_R8, columns, rows, 0, GL30.GL_RED, GL30.GL_UNSIGNED_BYTE,
					RenderMathUtils.wrap(this.shadowData));
		}
		return new BuildingShadow() {
			@Override
			public void remove() {
				shadowList.remove(shadowPositionArray);
				reloadShadowDataToGPU();
			}

			@Override
			public void move(final float x, final float y) {
				shadowPositionArray[0] = x;
				shadowPositionArray[1] = y;
				reloadShadowDataToGPU();
			}
		};
	}

	public void blitShadowData(final int columns, final int rows, final float shadowX, final float shadowY,
			final Texture texture) {
		final int width = texture.getWidth();
		final int height = texture.getHeight();
		final int ox = (int) Math.round(width * 0.3);
		final int oy = (int) Math.round(height * 0.7);
		blitShadowDataLocation(columns, rows, (RawOpenGLTextureResource) texture, width, height, ox, oy,
				this.centerOffset, shadowX, shadowY, this.shadowData);
	}

	public void initShadows() throws IOException {
		final GL30 gl = Gdx.gl30;
		final float[] centerOffset = this.centerOffset;
		final int columns = (this.columns - 1) * 4;
		final int rows = (this.rows - 1) * 4;

		final int shadowSize = columns * rows;
		this.staticShadowData = new byte[columns * rows];
		this.shadowData = new byte[columns * rows];
		if (this.viewer.mapMpq.has("war3map.shd")) {
			final byte[] buffer;

			try (final InputStream shadowSource = this.viewer.mapMpq.getResourceAsStream("war3map.shd")) {
				buffer = IOUtils.toByteArray(shadowSource);
			}

			for (int i = 0; i < shadowSize; i++) {
				this.staticShadowData[i] = (byte) ((buffer[i] & 0xFF) / 2f);
			}
		}

		final byte outsideArea = (byte) 204;
		final int x0 = this.mapBounds[0] * 4, x1 = (this.mapSize[0] - this.mapBounds[1] - 1) * 4,
				y0 = this.mapBounds[2] * 4, y1 = (this.mapSize[1] - this.mapBounds[3] - 1) * 4;
		for (int y = y0; y < y1; ++y) {
			for (int x = x0; x < x1; ++x) {
				final RenderCorner c = this.corners[x >> 2][y >> 2];
				if (c.getBoundary() != 0) {
					this.staticShadowData[(y * columns) + x] = outsideArea;
				}
			}
		}
		for (int y = 0; y < rows; ++y) {
			for (int x = 0; x < x0; ++x) {
				this.staticShadowData[(y * columns) + x] = outsideArea;
			}
			for (int x = x1; x < columns; ++x) {
				this.staticShadowData[(y * columns) + x] = outsideArea;
			}
		}
		for (int x = x0; x < x1; ++x) {
			for (int y = 0; y < y0; ++y) {
				this.staticShadowData[(y * columns) + x] = outsideArea;
			}
			for (int y = y1; y < rows; ++y) {
				this.staticShadowData[(y * columns) + x] = outsideArea;
			}
		}
		reloadShadowData(centerOffset, columns, rows);

		this.shadowMap = gl.glGenTexture();
		gl.glBindTexture(GL30.GL_TEXTURE_2D, this.shadowMap);
		gl.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MAG_FILTER, GL30.GL_LINEAR);
		gl.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MIN_FILTER, GL30.GL_LINEAR);
		gl.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_WRAP_S, GL30.GL_CLAMP_TO_EDGE);
		gl.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_WRAP_T, GL30.GL_CLAMP_TO_EDGE);
		gl.glTexImage2D(GL30.GL_TEXTURE_2D, 0, GL30.GL_R8, columns, rows, 0, GL30.GL_RED, GL30.GL_UNSIGNED_BYTE,
				RenderMathUtils.wrap(this.shadowData));
		this.initShadowsFinished = true;

		this.fogOfWarMap = gl.glGenTexture();
		gl.glBindTexture(GL30.GL_TEXTURE_2D, this.fogOfWarMap);
	}

	private void reloadShadowData(final float[] centerOffset, final int columns, final int rows) {
		System.arraycopy(this.staticShadowData, 0, this.shadowData, 0, this.staticShadowData.length);
		for (final Map.Entry<String, Texture> fileAndTexture : this.shadowTextures.entrySet()) {
			final String file = fileAndTexture.getKey();
			final Texture texture = fileAndTexture.getValue();

			final int width = texture.getWidth();
			final int height = texture.getHeight();
			final int ox = (int) Math.round(width * 0.3);
			final int oy = (int) Math.round(height * 0.7);
			for (final float[] location : this.shadows.get(file)) {
				blitShadowDataLocation(columns, rows, (RawOpenGLTextureResource) texture, width, height, ox, oy,
						centerOffset, location[0], location[1], this.shadowData);
			}
		}
	}

	public void blitShadowDataLocation(final int columns, final int rows, final RawOpenGLTextureResource texture,
			final int width, final int height, final int x01, final int y01, final float[] centerOffset, final float v,
			final float v2, final byte[] shadowData) {
		final int x0 = (int) Math.floor((v - centerOffset[0]) / 32.0) - x01;
		final int y0 = (int) Math.floor((v2 - centerOffset[1]) / 32.0) + y01;
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
				}
			}
		}
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

	private final WaveBuilder waveBuilder;
	public PathingGrid pathingGrid;
	private final Rectangle shaderMapBoundsRectangle;
	private final Rectangle entireMapRectangle;
	private final float[] defaultCameraBounds;

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

	public float getGroundHeight(final float x, final float y) {
		final float userCellSpaceX = (x - this.centerOffset[0]) / 128.0f;
		final float userCellSpaceY = (y - this.centerOffset[1]) / 128.0f;
		final int cellX = (int) userCellSpaceX;
		final int cellY = (int) userCellSpaceY;

		if ((cellX >= 0) && (cellX < (this.mapSize[0] - 1)) && (cellY >= 0) && (cellY < (this.mapSize[1] - 1))) {
			final float bottomLeft = this.groundCornerHeights[(cellY * this.columns) + cellX];
			final float bottomRight = this.groundCornerHeights[(cellY * this.columns) + cellX + 1];
			final float topLeft = this.groundCornerHeights[((cellY + 1) * this.columns) + cellX];
			final float topRight = this.groundCornerHeights[((cellY + 1) * this.columns) + cellX + 1];
			final float sqX = userCellSpaceX - cellX;
			final float sqY = userCellSpaceY - cellY;
			float height;

			if ((sqX + sqY) < 1) {
				height = bottomLeft + ((bottomRight - bottomLeft) * sqX) + ((topLeft - bottomLeft) * sqY);
			}
			else {
				height = topRight + ((bottomRight - topRight) * (1 - sqY)) + ((topLeft - topRight) * (1 - sqX));
			}

			return height * 128.0f;
		}

		return 0;
	}

	public int get128CellX(final float x) {
		final float userCellSpaceX = (x - this.centerOffset[0]) / 128.0f;
		final int cellX = (int) userCellSpaceX;
		return cellX;
	}

	public float get128WorldCoordinateFromCellX(final int cellX) {
		return (cellX * 128.0f) + this.centerOffset[0];
	}

	public int get128CellY(final float y) {
		final float userCellSpaceY = (y - this.centerOffset[1]) / 128.0f;
		final int cellY = (int) userCellSpaceY;
		return cellY;
	}

	public float get128WorldCoordinateFromCellY(final int cellY) {
		return (cellY * 128.0f) + this.centerOffset[1];
	}

	public RenderCorner getCorner(final float x, final float y) {
		final float userCellSpaceX = (x - this.centerOffset[0]) / 128.0f;
		final float userCellSpaceY = (y - this.centerOffset[1]) / 128.0f;
		final int cellX = (int) userCellSpaceX;
		final int cellY = (int) userCellSpaceY;

		if ((cellX >= 0) && (cellX < (this.mapSize[0] - 1)) && (cellY >= 0) && (cellY < (this.mapSize[1] - 1))) {
			return this.corners[cellX][cellY];
		}

		return null;
	}

	public float getWaterHeight(final float x, final float y) {
		final float userCellSpaceX = (x - this.centerOffset[0]) / 128.0f;
		final float userCellSpaceY = (y - this.centerOffset[1]) / 128.0f;
		final int cellX = (int) userCellSpaceX;
		final int cellY = (int) userCellSpaceY;

		if ((cellX >= 0) && (cellX < (this.mapSize[0] - 1)) && (cellY >= 0) && (cellY < (this.mapSize[1] - 1))) {
			final float bottomLeft = this.waterHeights[(cellY * this.columns) + cellX];
			final float bottomRight = this.waterHeights[(cellY * this.columns) + cellX + 1];
			final float topLeft = this.waterHeights[((cellY + 1) * this.columns) + cellX];
			final float topRight = this.waterHeights[((cellY + 1) * this.columns) + cellX + 1];
			final float sqX = userCellSpaceX - cellX;
			final float sqY = userCellSpaceY - cellY;
			float height;

			if ((sqX + sqY) < 1) {
				height = bottomLeft + ((bottomRight - bottomLeft) * sqX) + ((topLeft - bottomLeft) * sqY);
			}
			else {
				height = topRight + ((bottomRight - topRight) * (1 - sqY)) + ((topLeft - topRight) * (1 - sqX));
			}

			return (height + this.waterHeightOffset) * 128.0f;
		}

		return this.waterHeightOffset * 128.0f;
	}

	public static final class Splat {
		public List<float[]> locations = new ArrayList<>();
		public List<Consumer<SplatMover>> unitMapping = new ArrayList<>();
		public float opacity = 1;
		public boolean aboveWater = false;
	}

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

	public void removeSplatBatchModel(final String path) {
		this.uberSplatModelsList.remove(this.uberSplatModels.remove(path));
	}

	public void addSplatBatchModel(final String path, final SplatModel model) {
		this.uberSplatModels.put(path, model);
		this.uberSplatModelsList.add(model);
		Collections.sort(this.uberSplatModelsList);
	}

	public SplatModel getSplatModel(final String pathKey) {
		return this.uberSplatModels.get(pathKey);
	}

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

	public static final class SoftwareGroundMesh {
		public final float[] vertices;
		public final int[] indices;

		private SoftwareGroundMesh(final float[] groundHeights, final float[] groundCornerHeights,
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

		private SoftwareWaterAndGroundMesh(final float waterHeightOffset, final float[] groundCornerHeights,
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
		return this.corners[(int) Math.floor(x)][(int) Math.floor(y)].getBoundary() == 0;
	}

	public Rectangle getPlayableMapArea() {
		return this.shaderMapBoundsRectangle;
	}

	public Rectangle getEntireMap() {
		return this.entireMapRectangle;
	}

	private void reloadShadowDataToGPU() {
		final int columns = (Terrain.this.columns - 1) * 4;
		final int rows = (Terrain.this.rows - 1) * 4;
		reloadShadowData(Terrain.this.centerOffset, columns, rows);
		final GL30 gl = Gdx.gl30;
		gl.glBindTexture(GL30.GL_TEXTURE_2D, Terrain.this.shadowMap);
		gl.glTexImage2D(GL30.GL_TEXTURE_2D, 0, GL30.GL_R8, columns, rows, 0, GL30.GL_RED, GL30.GL_UNSIGNED_BYTE,
				RenderMathUtils.wrap(Terrain.this.shadowData));
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

	public float[] getDefaultCameraBounds() {
		return this.defaultCameraBounds;
	}

	public void setFogOfWarData(CFogMaskSettings fogMaskSettings, final CPlayerFogOfWar fogOfWarData) {
		this.fogOfWarData = fogOfWarData;
		this.visualFogData = ByteBuffer.allocateDirect(fogOfWarData.getFogOfWarBuffer().capacity());
		reloadFogOfWarDataToGPU(fogMaskSettings);
	}

	public void reloadFogOfWarDataToGPU(CFogMaskSettings fogMaskSettings) {
		final GL30 gl = Gdx.gl30;
		final ByteBuffer fogOfWarBuffer = this.fogOfWarData.getFogOfWarBuffer();
		for (int i = 0; i < this.visualFogData.capacity(); i++) {
			this.visualFogData.put(i, War3MapViewer.fadeLineOfSightColor(this.visualFogData.get(i),
					fogMaskSettings.getFogStateFromSettings(fogOfWarBuffer.get(i))));
		}
		gl.glBindTexture(GL30.GL_TEXTURE_2D, Terrain.this.fogOfWarMap);
		gl.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MAG_FILTER, GL30.GL_LINEAR);
		gl.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MIN_FILTER, GL30.GL_LINEAR);
		gl.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_WRAP_S, GL30.GL_CLAMP_TO_EDGE);
		gl.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_WRAP_T, GL30.GL_CLAMP_TO_EDGE);
		gl.glTexImage2D(GL30.GL_TEXTURE_2D, 0, GL30.GL_R8, this.fogOfWarData.getWidth(), this.fogOfWarData.getHeight(),
				0, GL30.GL_RED, GL30.GL_UNSIGNED_BYTE, this.visualFogData);
	}

	public int getFogOfWarMap() {
		return this.fogOfWarMap;
	}

	public void setWaterBaseColor(float red, float green, float blue, float alpha) {
		final float[] rgba = { red, green, blue, alpha };
		for (int i = 0; i < 4; i++) {
			this.maxDeepColorApplied[i] = this.maxDeepColor[i] * rgba[i];
			this.minDeepColorApplied[i] = this.minDeepColor[i] * rgba[i];
			this.maxShallowColorApplied[i] = this.maxShallowColor[i] * rgba[i];
			this.minShallowColorApplied[i] = this.minShallowColor[i] * rgba[i];
		}
	}
}
