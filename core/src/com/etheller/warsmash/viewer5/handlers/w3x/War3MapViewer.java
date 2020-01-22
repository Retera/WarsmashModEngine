package com.etheller.warsmash.viewer5.handlers.w3x;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;
import com.etheller.warsmash.common.FetchDataTypeName;
import com.etheller.warsmash.common.LoadGenericCallback;
import com.etheller.warsmash.datasources.DataSource;
import com.etheller.warsmash.parsers.w3x.War3Map;
import com.etheller.warsmash.parsers.w3x.doo.War3MapDoo;
import com.etheller.warsmash.parsers.w3x.objectdata.Warcraft3MapObjectData;
import com.etheller.warsmash.parsers.w3x.unitsdoo.War3MapUnitsDoo;
import com.etheller.warsmash.parsers.w3x.w3e.Corner;
import com.etheller.warsmash.parsers.w3x.w3e.War3MapW3e;
import com.etheller.warsmash.parsers.w3x.w3i.War3MapW3i;
import com.etheller.warsmash.units.DataTable;
import com.etheller.warsmash.units.Element;
import com.etheller.warsmash.units.StandardObjectData;
import com.etheller.warsmash.units.manager.MutableObjectData;
import com.etheller.warsmash.units.manager.MutableObjectData.MutableGameObject;
import com.etheller.warsmash.units.manager.MutableObjectData.WorldEditorDataType;
import com.etheller.warsmash.util.MappedData;
import com.etheller.warsmash.util.MappedDataRow;
import com.etheller.warsmash.util.RenderMathUtils;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.CanvasProvider;
import com.etheller.warsmash.viewer5.GenericResource;
import com.etheller.warsmash.viewer5.Grid;
import com.etheller.warsmash.viewer5.ModelInstance;
import com.etheller.warsmash.viewer5.ModelViewer;
import com.etheller.warsmash.viewer5.PathSolver;
import com.etheller.warsmash.viewer5.Scene;
import com.etheller.warsmash.viewer5.Texture;
import com.etheller.warsmash.viewer5.gl.ANGLEInstancedArrays;
import com.etheller.warsmash.viewer5.gl.WebGL;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxComplexInstance;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxHandler;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxModel;

public class War3MapViewer extends ModelViewer {
	private static final float[] sizeHeap = new float[2];
	private static final War3ID sloc = War3ID.fromString("sloc");
	private static final LoadGenericCallback stringDataCallback = new StringDataCallbackImplementation();
	private static final StreamDataCallbackImplementation streamDataCallback = new StreamDataCallbackImplementation();

	private static final Vector3 normalHeap1 = new Vector3();
	private static final Vector3 normalHeap2 = new Vector3();

	public PathSolver wc3PathSolver = PathSolver.DEFAULT;
	public SolverParams solverParams = new SolverParams();
	public ShaderProgram groundShader;
	public ShaderProgram waterShader;
	public ShaderProgram cliffShader;
	public Scene worldScene;
	public float waterIndex;
	public float waterIncreasePerFrame;
	public float waterHeightOffset;
	public List<Texture> waterTextures = new ArrayList<>();
	public float[] maxDeepColor = new float[4];
	public float[] minDeepColor = new float[4];
	public float[] maxShallowColor = new float[4];
	public float[] minShallowColor = new float[4];
	public boolean anyReady;
	public boolean terrainCliffsAndWaterLoaded;
	public MappedData terrainData = new MappedData();
	public MappedData cliffTypesData = new MappedData();
	public MappedData waterData = new MappedData();
	public boolean terrainReady;
	public boolean cliffsReady;
	public boolean doodadsAndDestructiblesLoaded;
	public MappedData doodadsData = new MappedData();
	public MappedData doodadMetaData = new MappedData();
	public MappedData destructableMetaData = new MappedData();
	public List<Doodad> doodads = new ArrayList<>();
	public List<Object> terrainDoodads = new ArrayList<>();
	public boolean doodadsReady;
	public boolean unitsAndItemsLoaded;
	public MappedData unitsData = new MappedData();
	public MappedData unitMetaData = new MappedData();
	public List<Unit> units = new ArrayList<>();
	public boolean unitsReady;
	public List<Texture> tilesetTextures = new ArrayList<>();
	public List<Texture> cliffTextures = new ArrayList<>();
	public List<TerrainModel> cliffModels = new ArrayList<>();
	public War3Map mapMpq;
	public PathSolver mapPathSolver = PathSolver.DEFAULT;
	public Corner[][] corners;
	public float[] centerOffset = new float[2];
	public int[] mapSize = new int[2];
	public List<MappedDataRow> tilesets = new ArrayList<>(); // TODO
	public int blightTextureIndex = -1;
	public List<MappedDataRow> cliffTilesets = new ArrayList<>();
	public int columns;
	public int rows;
	public int vertexBuffer;
	public int faceBuffer;
	public int instanceBuffer;
	public int textureBuffer;
	public int variationBuffer;
	public int waterBuffer;
	public int heightMap;
	public int waterHeightMap;
	public int cliffHeightMap;

	private final DataSource gameDataSource;

	public War3MapViewer(final DataSource dataSource, final CanvasProvider canvas) {
		super(dataSource, canvas);
		this.gameDataSource = dataSource;

		final WebGL webGL = this.webGL;

		this.addHandler(new MdxHandler());

		this.wc3PathSolver = PathSolver.DEFAULT;

		this.groundShader = this.webGL.createShaderProgram(W3xShaders.Ground.vert, W3xShaders.Ground.frag);
		this.waterShader = this.webGL.createShaderProgram(W3xShaders.Water.vert, W3xShaders.Water.frag);
		this.cliffShader = this.webGL.createShaderProgram(W3xShaders.Cliffs.vert, W3xShaders.Cliffs.frag);

		this.worldScene = this.addScene();

		loadSLKs();
	}

	public void loadSLKs() {
		final GenericResource terrain = this.loadMapGeneric("TerrainArt\\Terrain.slk", FetchDataTypeName.SLK,
				stringDataCallback);
		final GenericResource cliffTypes = this.loadMapGeneric("TerrainArt\\CliffTypes.slk", FetchDataTypeName.SLK,
				stringDataCallback);
		final GenericResource water = this.loadMapGeneric("TerrainArt\\Water.slk", FetchDataTypeName.SLK,
				stringDataCallback);

		// == when loaded, which is always in our system ==
		this.terrainCliffsAndWaterLoaded = true;
		this.terrainData.load(terrain.data.toString());
		this.cliffTypesData.load(cliffTypes.data.toString());
		this.waterData.load(water.data.toString());
		// emit terrain loaded??

		final GenericResource doodads = this.loadMapGeneric("Doodads\\Doodads.slk", FetchDataTypeName.SLK,
				stringDataCallback);
		final GenericResource doodadMetaData = this.loadMapGeneric("Doodads\\DoodadMetaData.slk", FetchDataTypeName.SLK,
				stringDataCallback);
		final GenericResource destructableData = this.loadMapGeneric("Units\\DestructableData.slk",
				FetchDataTypeName.SLK, stringDataCallback);
		final GenericResource destructableMetaData = this.loadMapGeneric("Units\\DestructableMetaData.slk",
				FetchDataTypeName.SLK, stringDataCallback);

		// == when loaded, which is always in our system ==
		this.doodadsAndDestructiblesLoaded = true;
		this.doodadsData.load(doodads.data.toString());
		this.doodadMetaData.load(doodadMetaData.data.toString());
		this.doodadsData.load(destructableData.data.toString());
		this.destructableMetaData.load(destructableData.data.toString());
		// emit doodads loaded

		final GenericResource unitData = this.loadMapGeneric("Units\\UnitData.slk", FetchDataTypeName.SLK,
				stringDataCallback);
		final GenericResource unitUi = this.loadMapGeneric("Units\\unitUI.slk", FetchDataTypeName.SLK,
				stringDataCallback);
		final GenericResource itemData = this.loadMapGeneric("Units\\ItemData.slk", FetchDataTypeName.SLK,
				stringDataCallback);
		final GenericResource unitMetaData = this.loadMapGeneric("Units\\UnitMetaData.slk", FetchDataTypeName.SLK,
				stringDataCallback);

		// == when loaded, which is always in our system ==
		this.unitsAndItemsLoaded = true;
		this.unitsData.load(unitData.data.toString());
		this.unitsData.load(unitUi.data.toString());
		this.unitsData.load(itemData.data.toString());
		this.unitMetaData.load(unitMetaData.data.toString());
		// emit loaded

	}

	public GenericResource loadMapGeneric(final String path, final FetchDataTypeName dataType,
			final LoadGenericCallback callback) {
		if (this.mapMpq == null) {
			return loadGeneric(path, dataType, callback);
		}
		else {
			return loadGeneric(path, dataType, callback, this.mapMpq.getCompoundDataSource());
		}
	}

	public void loadMap(final String mapFilePath) throws IOException {
		final War3Map war3Map = new War3Map(this.gameDataSource, mapFilePath);

		this.mapMpq = war3Map;
		setDataSource(war3Map.getCompoundDataSource());

//		loadSLKs();

		final PathSolver wc3PathSolver = this.wc3PathSolver;

		char tileset = 'A';

		final War3MapW3i w3iFile = this.mapMpq.readMapInformation();

		tileset = w3iFile.getTileset();

		this.solverParams.tileset = Character.toLowerCase(tileset);

		final War3MapW3e terrainData = this.mapMpq.readEnvironment();
		final float[] centerOffset = terrainData.getCenterOffset();
		final int[] mapSize = terrainData.getMapSize();

		this.corners = terrainData.getCorners();
		System.arraycopy(centerOffset, 0, this.centerOffset, 0, centerOffset.length);
		System.arraycopy(mapSize, 0, this.mapSize, 0, mapSize.length);

		// Override the grid based on the map.
		this.worldScene.grid = new Grid(centerOffset[0], centerOffset[1], (mapSize[0] * 128) - 128,
				(mapSize[1] * 128) - 128, 16 * 128, 16 * 128);

		if (this.terrainCliffsAndWaterLoaded) {
			this.loadTerrainCliffsAndWater(terrainData);
		}
		else {
			throw new IllegalStateException("transcription of JS has not loaded a map and has no JS async promises");
		}

		final Warcraft3MapObjectData modifications = this.mapMpq.readModifications();

		if (this.doodadsAndDestructiblesLoaded) {
			this.loadDoodadsAndDestructibles(modifications);
		}
		else {
			throw new IllegalStateException("transcription of JS has not loaded a map and has no JS async promises");
		}

		if (this.unitsAndItemsLoaded) {
			this.loadUnitsAndItems(modifications);
		}
		else {
			throw new IllegalStateException("transcription of JS has not loaded a map and has no JS async promises");
		}
	}

	private void loadTerrainCliffsAndWater(final War3MapW3e w3e) {
		final String texturesExt = this.solverParams.reforged ? ".dds" : ".blp";
		final char tileset = w3e.getTileset();

		for (final War3ID groundTile : w3e.getGroundTiles()) {
			final MappedDataRow row = this.terrainData.getRow(groundTile.asStringValue());

			this.tilesets.add(row);
			this.tilesetTextures
					.add((Texture) this.load(row.get("dir").toString() + "\\" + row.get("file") + texturesExt,
							this.mapPathSolver, this.solverParams));
		}

		final StandardObjectData standardObjectData = new StandardObjectData(this.mapMpq.getCompoundDataSource());
		final DataTable worldEditData = standardObjectData.getWorldEditData();
		final Element tilesets = worldEditData.get("TileSets");

		this.blightTextureIndex = this.tilesetTextures.size();
		this.tilesetTextures
				.add((Texture) this.load(tilesets.getField(Character.toString(tileset)).split(",")[1] + texturesExt,
						this.mapPathSolver, this.solverParams));

		for (final War3ID cliffTile : w3e.getCliffTiles()) {
			final MappedDataRow row = this.cliffTypesData.getRow(cliffTile.asStringValue());

			this.cliffTilesets.add(row);
			this.cliffTextures
					.add((Texture) this.load(row.get("texDir").toString() + "\\" + row.get("texFile") + texturesExt,
							this.mapPathSolver, this.solverParams));
		}

		final MappedDataRow waterRow = this.waterData.getRow(tileset + "Sha");

		this.waterHeightOffset = ((Number) waterRow.get("height")).floatValue();
		this.waterIncreasePerFrame = ((Number) waterRow.get("texRate")).intValue() / (float) 60;
		this.waterTextures.clear();
		this.maxDeepColor[0] = ((Number) waterRow.get("Dmax_R")).floatValue();
		this.maxDeepColor[1] = ((Number) waterRow.get("Dmax_G")).floatValue();
		this.maxDeepColor[2] = ((Number) waterRow.get("Dmax_B")).floatValue();
		this.maxDeepColor[3] = ((Number) waterRow.get("Dmax_A")).floatValue();
		this.minDeepColor[0] = ((Number) waterRow.get("Dmin_R")).floatValue();
		this.minDeepColor[1] = ((Number) waterRow.get("Dmin_G")).floatValue();
		this.minDeepColor[2] = ((Number) waterRow.get("Dmin_B")).floatValue();
		this.minDeepColor[3] = ((Number) waterRow.get("Dmin_A")).floatValue();
		this.maxShallowColor[0] = ((Number) waterRow.get("Smax_R")).floatValue();
		this.maxShallowColor[1] = ((Number) waterRow.get("Smax_G")).floatValue();
		this.maxShallowColor[2] = ((Number) waterRow.get("Smax_B")).floatValue();
		this.maxShallowColor[3] = ((Number) waterRow.get("Smax_A")).floatValue();
		this.minShallowColor[0] = ((Number) waterRow.get("Smin_R")).floatValue();
		this.minShallowColor[1] = ((Number) waterRow.get("Smin_G")).floatValue();
		this.minShallowColor[2] = ((Number) waterRow.get("Smin_B")).floatValue();
		this.minShallowColor[3] = ((Number) waterRow.get("Smin_A")).floatValue();

		for (int i = 0, l = ((Number) waterRow.get("numTex")).intValue(); i < l; i++) {
			this.waterTextures.add(
					(Texture) this.load(waterRow.get("texFile").toString() + ((i < 10) ? "0" : "") + i + texturesExt,
							this.mapPathSolver, this.solverParams));
		}

		final GL20 gl = this.gl;

		final Corner[][] corners = w3e.getCorners();
		final int columns = this.mapSize[0];
		final int rows = this.mapSize[1];
		final float[] centerOffset = this.centerOffset;
		final int instanceCount = (columns - 1) * (rows - 1);
		final float[] cliffHeights = new float[columns * rows];
		final float[] cornerHeights = new float[columns * rows];
		final float[] waterHeights = new float[columns * rows];
		final short[] cornerTextures = new short[instanceCount * 4];
		final short[] cornerVariations = new short[instanceCount * 4];
		final short[] waterFlags = new short[instanceCount];
		int instance = 0;
		final Map<String, CliffInfo> cliffs = new HashMap<>();

		this.columns = columns - 1;
		this.rows = rows - 1;

		for (int y = 0; y < rows; y++) {
			for (int x = 0; x < columns; x++) {
				final Corner bottomLeft = corners[y][x];
				final int index = (y * columns) + x;

				cliffHeights[index] = bottomLeft.getGroundHeight();
				cornerHeights[index] = (bottomLeft.getGroundHeight() + bottomLeft.getLayerHeight()) - 2;
				waterHeights[index] = bottomLeft.getWaterHeight();

				if ((y < (rows - 1)) && (x < (columns - 1))) {
					// Water can be used with cliffs and normal corners, so store water state
					// regardless.
					waterFlags[instance] = this.isWater(x, y);

					// Is this a cliff, or a normal corner?
					if (this.isCliff(x, y)) {
						final int bottomLeftLayer = bottomLeft.getLayerHeight();
						final int bottomRightLayer = corners[y][x + 1].getLayerHeight();
						final int topLeftLayer = corners[y + 1][x].getLayerHeight();
						final int topRightLayer = corners[y + 1][x + 1].getLayerHeight();
						final int base = Math.min(Math.min(bottomLeftLayer, bottomRightLayer),
								Math.min(topLeftLayer, topRightLayer));
						final String fileName = this.cliffFileName(bottomLeftLayer, bottomRightLayer, topLeftLayer,
								topRightLayer, base);

						if (!"AAAA".equals(fileName)) {
							int cliffTexture = bottomLeft.getCliffTexture();

							// ?
							if (cliffTexture == 15) {
								cliffTexture = 1;
							}

							final MappedDataRow cliffRow = this.cliffTilesets.get(cliffTexture);
							final String dir = cliffRow.get("cliffModelDir").toString();
							final String path = "Doodads\\Terrain\\" + dir + "\\" + dir + fileName
									+ Variations.getCliffVariation(dir, fileName, bottomLeft.getCliffVariation())
									+ ".mdx";

							if (!cliffs.containsKey(path)) {
								cliffs.put(path, new CliffInfo());
							}

							cliffs.get(path).locations.add(new float[] { ((x + 1) * 128) + centerOffset[0],
									(y * 128) + centerOffset[1], (base - 2) * 128 });
							cliffs.get(path).textures.add(cliffTexture);
						}
					}
					else {
						final int bottomLeftTexture = this.cornerTexture(x, y);
						final int bottomRightTexture = this.cornerTexture(x + 1, y);
						final int topLeftTexture = this.cornerTexture(x, y + 1);
						final int topRightTexture = this.cornerTexture(x + 1, y + 1);
						final LinkedHashSet<Integer> texturesUnique = new LinkedHashSet<>();
						texturesUnique.add(bottomLeftTexture);
						texturesUnique.add(bottomRightTexture);
						texturesUnique.add(topLeftTexture);
						texturesUnique.add(topRightTexture);
						final List<Integer> textures = new ArrayList<>(texturesUnique);
						Collections.sort(textures);

						int texture = textures.remove(0);

						cornerTextures[instance * 4] = (short) (texture + 1);
						cornerVariations[instance * 4] = this.getVariation(texture, bottomLeft.getGroundVariation());

						for (int i = 0, l = textures.size(); i < l; i++) {
							int bitset = 0;

							texture = textures.get(i);

							if (bottomRightTexture == texture) {
								bitset |= 0b0001;
							}

							if (bottomLeftTexture == texture) {
								bitset |= 0b0010;
							}

							if (topRightTexture == texture) {
								bitset |= 0b0100;
							}

							if (topLeftTexture == texture) {
								bitset |= 0b1000;
							}

							cornerTextures[(instance * 4) + 1 + i] = (short) (texture + 1);
							cornerVariations[(instance * 4) + 1 + i] = (short) (bitset);
						}
					}

					instance += 1;

				}
			}
		}

		this.vertexBuffer = gl.glGenBuffer();
		gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, this.vertexBuffer);
		gl.glBufferData(GL20.GL_ARRAY_BUFFER, 8 * 4, RenderMathUtils.wrap(new float[] { 0, 0, 1, 0, 0, 1, 1, 1 }),
				GL20.GL_STATIC_DRAW);

		this.faceBuffer = gl.glGenBuffer();
		gl.glBindBuffer(GL20.GL_ELEMENT_ARRAY_BUFFER, this.faceBuffer);
		gl.glBufferData(GL20.GL_ELEMENT_ARRAY_BUFFER, 6, RenderMathUtils.wrap(new byte[] { 0, 1, 2, 1, 3, 2 }),
				GL20.GL_STATIC_DRAW);

		this.cliffHeightMap = gl.glGenTexture();
		gl.glBindTexture(GL20.GL_TEXTURE_2D, this.cliffHeightMap);
		this.webGL.setTextureMode(GL20.GL_CLAMP_TO_EDGE, GL20.GL_CLAMP_TO_EDGE, GL20.GL_NEAREST, GL20.GL_NEAREST);
		gl.glTexImage2D(GL20.GL_TEXTURE_2D, 0, GL30.GL_R32F, columns, rows, 0, GL30.GL_RED, GL20.GL_FLOAT,
				RenderMathUtils.wrap(cliffHeights));

		this.heightMap = gl.glGenTexture();
		gl.glBindTexture(GL20.GL_TEXTURE_2D, this.heightMap);
		this.webGL.setTextureMode(GL20.GL_CLAMP_TO_EDGE, GL20.GL_CLAMP_TO_EDGE, GL20.GL_NEAREST, GL20.GL_NEAREST);
		gl.glTexImage2D(GL20.GL_TEXTURE_2D, 0, GL30.GL_R32F, columns, rows, 0, GL30.GL_RED, GL20.GL_FLOAT,
				RenderMathUtils.wrap(cornerHeights));

		this.waterHeightMap = gl.glGenTexture();
		gl.glBindTexture(GL20.GL_TEXTURE_2D, this.waterHeightMap);
		this.webGL.setTextureMode(GL20.GL_CLAMP_TO_EDGE, GL20.GL_CLAMP_TO_EDGE, GL20.GL_NEAREST, GL20.GL_NEAREST);
		gl.glTexImage2D(GL20.GL_TEXTURE_2D, 0, GL30.GL_R32F, columns, rows, 0, GL30.GL_RED, GL20.GL_FLOAT,
				RenderMathUtils.wrap(waterHeights));

		this.instanceBuffer = gl.glGenBuffer();
		gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, this.instanceBuffer);
		final float[] instanceBufferData = new float[instanceCount];
		for (int i = 0; i < instanceBufferData.length; i++) {
			instanceBufferData[i] = i;
		}
		gl.glBufferData(GL20.GL_ARRAY_BUFFER, instanceBufferData.length * 4, RenderMathUtils.wrap(instanceBufferData),
				GL20.GL_STATIC_DRAW);

		this.textureBuffer = gl.glGenBuffer();
		gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, this.textureBuffer);
		gl.glBufferData(GL20.GL_ARRAY_BUFFER, cornerTextures.length, RenderMathUtils.wrap(cornerTextures),
				GL20.GL_STATIC_DRAW);

		this.variationBuffer = gl.glGenBuffer();
		gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, this.variationBuffer);
		gl.glBufferData(GL20.GL_ARRAY_BUFFER, cornerVariations.length, RenderMathUtils.wrap(cornerVariations),
				GL20.GL_STATIC_DRAW);

		this.waterBuffer = gl.glGenBuffer();
		gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, this.waterBuffer);
		gl.glBufferData(GL20.GL_ARRAY_BUFFER, waterFlags.length, RenderMathUtils.wrap(waterFlags), GL20.GL_STATIC_DRAW);

		this.terrainReady = true;
		this.anyReady = true;

		final ShaderProgram cliffShader = this.cliffShader;
		this.cliffModels.clear();
		for (final Map.Entry<String, CliffInfo> entry : cliffs.entrySet()) {
			final String path = entry.getKey();
			final CliffInfo cliffInfo = entry.getValue();

			final GenericResource resource = this.loadMapGeneric(path, FetchDataTypeName.ARRAY_BUFFER,
					streamDataCallback);

			this.cliffModels.add(new TerrainModel(this, (InputStream) resource.data, cliffInfo.locations,
					cliffInfo.textures, cliffShader));
		}
		this.cliffsReady = true;

	}

	private void loadDoodadsAndDestructibles(final Warcraft3MapObjectData modifications) throws IOException {
		final War3MapDoo dooFile = this.mapMpq.readDoodads();

		this.applyModificationFile(this.doodadsData, this.doodadMetaData, modifications.getDoodads(),
				WorldEditorDataType.DOODADS);
		this.applyModificationFile(this.doodadsData, this.destructableMetaData, modifications.getDestructibles(),
				WorldEditorDataType.DESTRUCTIBLES);

		final War3MapDoo doo = this.mapMpq.readDoodads();

		for (final com.etheller.warsmash.parsers.w3x.doo.Doodad doodad : doo.getDoodads()) {

			MutableGameObject row = modifications.getDoodads().get(doodad.getId());
			if (row == null) {
				row = modifications.getDestructibles().get(doodad.getId());
			}
			String file = row.readSLKTag("file");
			final int numVar = row.readSLKTagInt("numVar");

			if (file.endsWith(".mdx")) {
				file = file.substring(0, file.length() - 4);
			}

			String fileVar = file;

			file += ".mdx";

			if (numVar > 1) {
				fileVar += Math.min(doodad.getVariation(), numVar - 1);
			}

			fileVar += ".mdx";

			// First see if the model is local.
			// Doodads referring to local models may have invalid variations, so if the
			// variation doesn't exist, try without a variation.

			String path;
			if (this.mapMpq.has(fileVar)) {
				path = fileVar;
			}
			else {
				path = file;
			}
			MdxModel model;
			if (this.mapMpq.has(path)) {
				model = (MdxModel) this.load(path, this.mapPathSolver, this.solverParams);
			}
			else {
				model = (MdxModel) this.load(fileVar, this.mapPathSolver, this.solverParams);
			}

			this.doodads.add(new Doodad(this, model, row, doodad));
		}

		// Cliff/Terrain doodads.
		for (final com.etheller.warsmash.parsers.w3x.doo.TerrainDoodad doodad : doo.getTerrainDoodads()) {
			final MutableGameObject row = modifications.getDoodads().get(doodad.getId());
			String file = row.readSLKTag("file");
			if (file.toLowerCase().endsWith(".mdl")) {
				file = file.substring(0, file.length() - 4);
			}
			if (!file.toLowerCase().endsWith(".mdx")) {
				file += ".mdx";
			}
			final MdxModel model = (MdxModel) this.load(file, this.mapPathSolver, this.solverParams);

			this.terrainDoodads.add(new TerrainDoodad(this, model, row, doodad));
		}

		this.doodadsReady = true;
		this.anyReady = true;
	}

	private void applyModificationFile(final MappedData doodadsData2, final MappedData doodadMetaData2,
			final MutableObjectData destructibles, final WorldEditorDataType dataType) {
		// TODO condense ported MappedData from Ghostwolf and MutableObjectData from
		// Retera

	}

	private void loadUnitsAndItems(final Warcraft3MapObjectData modifications) throws IOException {
		final War3Map mpq = this.mapMpq;

		final War3MapUnitsDoo dooFile = mpq.readUnits();

		// Collect the units and items data.
		for (final com.etheller.warsmash.parsers.w3x.unitsdoo.Unit unit : dooFile.getUnits()) {
			MutableGameObject row = null;
			String path = null;

			// Hardcoded?
			if (sloc.equals(unit.getId())) {
				path = "Objects\\StartLocation\\StartLocation.mdx";
			}
			else {
				row = modifications.getUnits().get(unit.getId());
				if (row == null) {
					row = modifications.getItems().get(unit.getId());
				}

				if (row != null) {
					path = row.readSLKTag("file");

					if (path.toLowerCase().endsWith(".mdl") || path.toLowerCase().endsWith(".mdx")) {
						path = path.substring(0, path.length() - 4);
					}
					if (row.readSLKTagInt("fileVerFlags") == 2) {
						path += "_V1";
					}

					path += ".mdx";
				}
			}

			if (path != null) {
				final MdxModel model = (MdxModel) this.load(path, this.mapPathSolver, this.solverParams);

				this.units.add(new Unit(this, model, row, unit));
			}
			else {
				System.err.println("Unknown unit ID: " + unit.getId());
			}
		}

		this.unitsReady = true;
		this.anyReady = true;
	}

	@Override
	public void update() {
		if (this.anyReady) {
			this.waterIndex += this.waterIncreasePerFrame;

			if (this.waterIndex >= this.waterTextures.size()) {
				this.waterIndex = 0;
			}

			super.update();

			final List<ModelInstance> instances = this.worldScene.instances;

			for (final ModelInstance instance : instances) {
				if (instance instanceof MdxComplexInstance) {
					final MdxComplexInstance mdxComplexInstance = (MdxComplexInstance) instance;
					if (mdxComplexInstance.sequenceEnded || (mdxComplexInstance.sequence == -1)) {
						StandSequence.randomStandSequence(mdxComplexInstance);
					}
				}
			}
		}
	}

	@Override
	public void render() {
		if (this.anyReady) {
			final Scene worldScene = this.worldScene;

			worldScene.startFrame();
			this.renderGround();
			this.renderCliffs();
			worldScene.renderOpaque();
			this.renderWater();
			worldScene.renderTranslucent();
		}
	}

	public void renderGround() {
		if (this.terrainReady) {
			final GL20 gl = this.gl;
			final WebGL webgl = this.webGL;
			final ANGLEInstancedArrays instancedArrays = webgl.instancedArrays;
			final ShaderProgram shader = this.groundShader;
			final List<Texture> tilesetTextures = this.tilesetTextures;
			final int instanceAttrib = shader.getAttributeLocation("a_InstanceID");
			final int positionAttrib = shader.getAttributeLocation("a_position");
			final int texturesAttrib = shader.getAttributeLocation("a_textures");
			final int variationsAttrib = shader.getAttributeLocation("a_variations");
			final int tilesetCount = tilesetTextures.size();

			gl.glEnable(GL20.GL_BLEND);
			gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

			webgl.useShaderProgram(shader);

			shader.setUniformMatrix("u_VP", this.worldScene.camera.viewProjectionMatrix);
			shader.setUniform2fv("u_offset", this.centerOffset, 0, 2);
			sizeHeap[0] = this.columns;
			sizeHeap[1] = this.rows;
			shader.setUniform2fv("u_size", sizeHeap, 0, 2);
			shader.setUniformi("u_heightMap", 15);

			gl.glActiveTexture(GL20.GL_TEXTURE15);
			gl.glBindTexture(GL20.GL_TEXTURE_2D, this.heightMap);

			gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, this.vertexBuffer);
			shader.setVertexAttribute(positionAttrib, 2, GL20.GL_FLOAT, false, 8, 0);

			gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, this.instanceBuffer);
			shader.setVertexAttribute(instanceAttrib, 1, GL20.GL_FLOAT, false, 4, 0);
			instancedArrays.glVertexAttribDivisorANGLE(instanceAttrib, 1);

			gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, this.textureBuffer);
			shader.setVertexAttribute(texturesAttrib, 4, GL20.GL_UNSIGNED_BYTE, false, 4, 0);
			instancedArrays.glVertexAttribDivisorANGLE(texturesAttrib, 1);

			gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, this.variationBuffer);
			shader.setVertexAttribute(variationsAttrib, 4, GL20.GL_UNSIGNED_BYTE, false, 4, 0);
			instancedArrays.glVertexAttribDivisorANGLE(variationsAttrib, 1);

			gl.glBindBuffer(GL20.GL_ELEMENT_ARRAY_BUFFER, this.faceBuffer);

			shader.setUniformi("u_baseTileset", 0);

			for (int i = 0, l = Math.min(tilesetCount, 15); i < l; i++) {
				final int isExtended = (tilesetTextures.get(i).getWidth() > tilesetTextures.get(i).getHeight()) ? 1 : 0;

				shader.setUniformf("u_extended[" + i + "]", isExtended);
				shader.setUniformi("u_tilesets[" + i + "]", i);

				tilesetTextures.get(i).bind(i);
			}

			instancedArrays.glDrawElementsInstancedANGLE(GL20.GL_TRIANGLES, 6, GL20.GL_UNSIGNED_BYTE, 0,
					this.rows * this.columns);

			if (tilesetCount > 15) {
				shader.setUniformi("u_baseTileset", 15);

				for (int i = 0, l = tilesetCount - 15; i < l; i++) {
					final int isExtended = (tilesetTextures.get(i + 15).getWidth() > tilesetTextures.get(i + 15)
							.getHeight()) ? 1 : 0;

					shader.setUniformf("u_extended[" + i + "]", isExtended);

					tilesetTextures.get(i + 15).bind(i);
				}

				instancedArrays.glDrawElementsInstancedANGLE(GL20.GL_TRIANGLES, 6, GL20.GL_UNSIGNED_BYTE, 0,
						this.rows * this.columns);
			}

			instancedArrays.glVertexAttribDivisorANGLE(texturesAttrib, 0);
			instancedArrays.glVertexAttribDivisorANGLE(variationsAttrib, 0);
			instancedArrays.glVertexAttribDivisorANGLE(instanceAttrib, 0);
		}
	}

	public void renderWater() {
		if (this.terrainReady) {
			final GL20 gl = this.gl;
			final WebGL webgl = this.webGL;
			final ANGLEInstancedArrays instancedArrays = webgl.instancedArrays;
			final ShaderProgram shader = this.waterShader;
			final int instanceAttrib = shader.getAttributeLocation("a_InstanceID");
			final int positionAttrib = shader.getAttributeLocation("a_position");
			final int isWaterAttrib = shader.getAttributeLocation("a_isWater");

			gl.glDepthMask(false);

			gl.glEnable(GL20.GL_BLEND);
			gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

			webgl.useShaderProgram(shader);

			shader.setUniformMatrix("u_VP", this.worldScene.camera.viewProjectionMatrix);
			shader.setUniform2fv("u_offset", this.centerOffset, 0, 2);
			sizeHeap[0] = this.columns;
			sizeHeap[1] = this.rows;
			shader.setUniform2fv("u_size", sizeHeap, 0, 2);
			shader.setUniformi("u_heightMap", 0);
			shader.setUniformi("u_waterHeightMap", 1);
			shader.setUniformi("u_waterTexture", 2);
			shader.setUniformf("u_offsetHeight", this.waterHeightOffset);
			shader.setUniform4fv("u_maxDeepColor", this.maxDeepColor, 0, 4);
			shader.setUniform4fv("u_minDeepColor", this.minDeepColor, 0, 4);
			shader.setUniform4fv("u_maxShallowColor", this.maxShallowColor, 0, 4);
			shader.setUniform4fv("u_minShallowColor", this.minShallowColor, 0, 4);

			gl.glActiveTexture(GL20.GL_TEXTURE0);
			gl.glBindTexture(GL20.GL_TEXTURE_2D, this.heightMap);

			gl.glActiveTexture(GL20.GL_TEXTURE1);
			gl.glBindTexture(GL20.GL_TEXTURE_2D, this.waterHeightMap);

			this.waterTextures.get((int) this.waterIndex).bind(2);

			gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, this.vertexBuffer);
			shader.setVertexAttribute(positionAttrib, 2, GL20.GL_FLOAT, false, 8, 0);

			gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, this.instanceBuffer);
			shader.setVertexAttribute(instanceAttrib, 1, GL20.GL_FLOAT, false, 4, 0);
			instancedArrays.glVertexAttribDivisorANGLE(instanceAttrib, 1);

			gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, this.waterBuffer);
			shader.setVertexAttribute(isWaterAttrib, 1, GL20.GL_UNSIGNED_BYTE, false, 1, 0);
			instancedArrays.glVertexAttribDivisorANGLE(isWaterAttrib, 1);

			gl.glBindBuffer(GL20.GL_ELEMENT_ARRAY_BUFFER, this.faceBuffer);
			instancedArrays.glDrawElementsInstancedANGLE(GL20.GL_TRIANGLES, 6, GL20.GL_UNSIGNED_BYTE, 0,
					this.rows * this.columns);

			instancedArrays.glVertexAttribDivisorANGLE(isWaterAttrib, 0);
			instancedArrays.glVertexAttribDivisorANGLE(instanceAttrib, 0);
		}
	}

	public void renderCliffs() {
		if (this.cliffsReady) {
			final GL20 gl = this.gl;
			final WebGL webGL = this.webGL;
			final ANGLEInstancedArrays instancedArrays = webGL.instancedArrays;
			final ShaderProgram shader = this.cliffShader;

			gl.glDisable(GL20.GL_BLEND);

			webGL.useShaderProgram(shader);

			shader.setUniformMatrix("u_VP", this.worldScene.camera.viewProjectionMatrix);
			shader.setUniformi("u_heightMap", 0);
			shader.setUniformf("u_pixel[0]", 1 / (float) (this.columns + 1));
			shader.setUniformf("u_pixel[1]", 1 / (float) (this.rows + 1));
			shader.setUniform2fv("u_centerOffset", this.centerOffset, 0, 2);
			shader.setUniformi("u_texture1", 1);
			shader.setUniformi("u_texture2", 2);

			gl.glActiveTexture(GL20.GL_TEXTURE0);
			gl.glBindTexture(GL20.GL_TEXTURE_2D, this.cliffHeightMap);

			gl.glActiveTexture(GL20.GL_TEXTURE1);
			gl.glBindTexture(GL20.GL_TEXTURE_2D, this.cliffTextures.get(0).getGlTarget());

			if (this.cliffTextures.size() > 1) {
				gl.glActiveTexture(GL20.GL_TEXTURE2);
				gl.glBindTexture(GL20.GL_TEXTURE_2D, this.cliffTextures.get(1).getGlTarget());
			}

			// Set instanced attributes.
			for (final TerrainModel cliff : this.cliffModels) {
				cliff.render(shader);
			}
		}
	}

	public String cliffFileName(final int bottomLeftLayer, final int bottomRightLayer, final int topLeftLayer,
			final int topRightLayer, final int base) {
		return Character.toString((char) ((65 + bottomLeftLayer) - base))
				+ Character.toString((char) ((65 + topLeftLayer) - base))
				+ Character.toString((char) ((65 + topRightLayer) - base))
				+ Character.toString((char) ((65 + bottomRightLayer) - base));
	}

	public short getVariation(final int groundTexture, final int variation) {
		final Texture texture = this.tilesetTextures.get(groundTexture);

		// Extended ?
		if (texture.getWidth() > texture.getHeight()) {
			if (variation < 16) {
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

	public boolean isCliff(final int column, final int row) {
		if ((column < 1) || (column > (this.columns - 1)) || (row < 1) || (row > (this.rows - 1))) {
			return false;
		}

		final Corner[][] corners = this.corners;
		final int bottomLeft = corners[row][column].getLayerHeight();
		final int bottomRight = corners[row][column + 1].getLayerHeight();
		final int topLeft = corners[row + 1][column].getLayerHeight();
		final int topRight = corners[row + 1][column + 1].getLayerHeight();

		return (bottomLeft != bottomRight) || (bottomLeft != topLeft) || (bottomLeft != topRight);
	}

	public short isWater(final int column, final int row) {
		return ((this.corners[row][column].getWater() != 0) || (this.corners[row][column + 1].getWater() != 0)
				|| (this.corners[row + 1][column].getWater() != 0)
				|| (this.corners[row + 1][column + 1].getWater() != 0)) ? (short) 1 : (short) 0;
	}

	public int cliffGroundIndex(final int whichCliff) {
		final String whichTile = this.cliffTilesets.get(whichCliff).get("groundTile").toString();
		final List<MappedDataRow> tilesets = this.tilesets;

		for (int i = 0, l = tilesets.size(); i < l; i++) {
			if (tilesets.get(i).get("tileID").toString().equals(whichTile)) {
				return i;
			}
		}
		throw new IllegalArgumentException(Integer.toString(whichCliff));
	}

	public int cornerTexture(final int column, final int row) {
		final Corner[][] corners = this.corners;
		final int columns = this.columns;
		final int rows = this.rows;

		for (int y = -1; y < 1; y++) {
			for (int x = -1; x < 1; x++) {
				if (((column + x) > 0) && ((column + x) < (columns - 1)) && ((row + y) > 0)
						&& ((row + y) < (rows - 1))) {
					if (this.isCliff(column + x, row + y)) {
						int texture = corners[row + y][column + x].getCliffTexture();

						if (texture == 15) {
							texture = 1;
						}

						return this.cliffGroundIndex(texture);
					}
				}
			}
		}

		final Corner corner = corners[row][column];
		if (corner.getBlight() != 0) {
			return this.blightTextureIndex;
		}
		return corner.getGroundTexture();
	}

	public Vector3 groundNormal(final Vector3 out, int x, int y) {
		final float[] centerOffset = this.centerOffset;
		final int[] mapSize = this.mapSize;

		x = (int) ((x - centerOffset[0]) / 128);
		y = (int) ((y - centerOffset[1]) / 128);

		final int cellX = x;
		final int cellY = y;

		// See if this coordinate is in the map

		if ((cellX >= 0) && (cellX < (mapSize[0] - 1)) && (cellY >= 0) && (cellY < (mapSize[1] - 1))) {
			// See http://gamedev.stackexchange.com/a/24574
			final Corner[][] corners = this.corners;
			final int bottomLeft = corners[cellY][cellX].getGroundHeight();
			final int bottomRight = corners[cellY][cellX + 1].getGroundHeight();
			final int topLeft = corners[cellY + 1][cellX].getGroundHeight();
			final int topRight = corners[cellY + 1][cellX + 1].getGroundHeight();
			final int sqX = x - cellX;
			final int sqY = y - cellY;

			if ((sqX + sqY) < 1) {
				normalHeap1.set(1, 0, bottomRight - bottomLeft);
				normalHeap2.set(0, 1, topLeft - bottomLeft);
			}
			else {
				normalHeap1.set(-1, 0, topRight - topLeft);
				normalHeap2.set(0, 1, topRight - bottomRight);
			}

			out.set(normalHeap1.crs(normalHeap2)).nor();
		}
		else {
			out.set(0, 0, 1);
		}

		return out;
	}

	private static final class MappedDataCallbackImplementation implements LoadGenericCallback {
		@Override
		public Object call(final InputStream data) {
			final StringBuilder stringBuilder = new StringBuilder();
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(data, "utf-8"))) {
				String line;
				while ((line = reader.readLine()) != null) {
					stringBuilder.append(line);
					stringBuilder.append("\n");
				}
			}
			catch (final UnsupportedEncodingException e) {
				throw new RuntimeException(e);
			}
			catch (final IOException e) {
				throw new RuntimeException(e);
			}
			return new MappedData(stringBuilder.toString());
		}
	}

	private static final class StringDataCallbackImplementation implements LoadGenericCallback {
		@Override
		public Object call(final InputStream data) {
			if (data == null) {
				System.err.println("data null");
			}
			final StringBuilder stringBuilder = new StringBuilder();
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(data, "utf-8"))) {
				String line;
				while ((line = reader.readLine()) != null) {
					stringBuilder.append(line);
					stringBuilder.append("\n");
				}
			}
			catch (final UnsupportedEncodingException e) {
				throw new RuntimeException(e);
			}
			catch (final IOException e) {
				throw new RuntimeException(e);
			}
			return stringBuilder.toString();
		}
	}

	private static final class StreamDataCallbackImplementation implements LoadGenericCallback {
		@Override
		public Object call(final InputStream data) {
			return data;
		}
	}

	public static final class SolverParams {
		public char tileset;
		public boolean reforged;
		public boolean hd;
	}

	public static final class CliffInfo {
		public List<float[]> locations = new ArrayList<>();
		public List<Integer> textures = new ArrayList<>();
	}

	private static final int MAXIMUM_ACCEPTED = 1 << 30;

	/**
	 * Returns a power of two size for the given target capacity.
	 */
	private static final int pow2GreaterThan(final int capacity) {
		int numElements = capacity - 1;
		numElements |= numElements >>> 1;
		numElements |= numElements >>> 2;
		numElements |= numElements >>> 4;
		numElements |= numElements >>> 8;
		numElements |= numElements >>> 16;
		return (numElements < 0) ? 1 : (numElements >= MAXIMUM_ACCEPTED) ? MAXIMUM_ACCEPTED : numElements + 1;
	}

}
