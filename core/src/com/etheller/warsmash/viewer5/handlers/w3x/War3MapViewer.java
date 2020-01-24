package com.etheller.warsmash.viewer5.handlers.w3x;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.channels.SeekableByteChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.compress.utils.SeekableInMemoryByteChannel;

import com.etheller.warsmash.common.FetchDataTypeName;
import com.etheller.warsmash.common.LoadGenericCallback;
import com.etheller.warsmash.datasources.CompoundDataSource;
import com.etheller.warsmash.datasources.DataSource;
import com.etheller.warsmash.datasources.MpqDataSource;
import com.etheller.warsmash.parsers.w3x.War3Map;
import com.etheller.warsmash.parsers.w3x.doo.War3MapDoo;
import com.etheller.warsmash.parsers.w3x.objectdata.Warcraft3MapObjectData;
import com.etheller.warsmash.parsers.w3x.unitsdoo.War3MapUnitsDoo;
import com.etheller.warsmash.parsers.w3x.w3e.War3MapW3e;
import com.etheller.warsmash.parsers.w3x.w3i.War3MapW3i;
import com.etheller.warsmash.units.manager.MutableObjectData;
import com.etheller.warsmash.units.manager.MutableObjectData.MutableGameObject;
import com.etheller.warsmash.units.manager.MutableObjectData.WorldEditorDataType;
import com.etheller.warsmash.util.MappedData;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.util.WorldEditStrings;
import com.etheller.warsmash.viewer5.CanvasProvider;
import com.etheller.warsmash.viewer5.GenericResource;
import com.etheller.warsmash.viewer5.Grid;
import com.etheller.warsmash.viewer5.ModelInstance;
import com.etheller.warsmash.viewer5.ModelViewer;
import com.etheller.warsmash.viewer5.PathSolver;
import com.etheller.warsmash.viewer5.Scene;
import com.etheller.warsmash.viewer5.gl.WebGL;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxComplexInstance;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxHandler;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxModel;
import com.etheller.warsmash.viewer5.handlers.w3x.environment.Terrain;

import mpq.MPQArchive;
import mpq.MPQException;

public class War3MapViewer extends ModelViewer {
	private static final War3ID UNIT_FILE = War3ID.fromString("umdl");
	private static final War3ID ITEM_FILE = War3ID.fromString("ifil");
	private static final War3ID sloc = War3ID.fromString("sloc");
	private static final LoadGenericCallback stringDataCallback = new StringDataCallbackImplementation();
	public static final StreamDataCallbackImplementation streamDataCallback = new StreamDataCallbackImplementation();

	public PathSolver wc3PathSolver = PathSolver.DEFAULT;
	public SolverParams solverParams = new SolverParams();
	public Scene worldScene;
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
	public War3Map mapMpq;
	public PathSolver mapPathSolver = PathSolver.DEFAULT;

	private final DataSource gameDataSource;

	public Terrain terrain;
	public int renderPathing = 0;
	public int renderLighting = 0;

	public War3MapViewer(final DataSource dataSource, final CanvasProvider canvas) {
		super(dataSource, canvas);
		this.gameDataSource = dataSource;

		final WebGL webGL = this.webGL;

		this.addHandler(new MdxHandler());

		this.wc3PathSolver = PathSolver.DEFAULT;

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
			return loadGeneric(path, dataType, callback, this.dataSource);
		}
	}

	public void loadMap(final String mapFilePath) throws IOException {
		final War3Map war3Map = new War3Map(this.gameDataSource, mapFilePath);

		this.mapMpq = war3Map;

//		loadSLKs();

		final PathSolver wc3PathSolver = this.wc3PathSolver;

		char tileset = 'A';

		final War3MapW3i w3iFile = this.mapMpq.readMapInformation();

		tileset = w3iFile.getTileset();

		final DataSource tilesetSource;
		try {
			// Slightly complex. Here's the theory:
			// 1.) Copy map into RAM
			// 2.) Setup a Data Source that will read assets
			// from either the map or the game, giving the map priority.
			SeekableByteChannel sbc;
			try (InputStream mapStream = war3Map.getCompoundDataSource().getResourceAsStream(tileset + ".mpq")) {
				final byte[] mapData = IOUtils.toByteArray(mapStream);
				sbc = new SeekableInMemoryByteChannel(mapData);
				final DataSource internalMpqContentsDataSource = new MpqDataSource(new MPQArchive(sbc), sbc);
				tilesetSource = new CompoundDataSource(
						Arrays.asList(war3Map.getCompoundDataSource(), internalMpqContentsDataSource));
			}
		}
		catch (final IOException e) {
			throw new RuntimeException(e);
		}
		catch (final MPQException e) {
			throw new RuntimeException(e);
		}
		setDataSource(tilesetSource);

		this.solverParams.tileset = Character.toLowerCase(tileset);

		final War3MapW3e terrainData = this.mapMpq.readEnvironment();

		this.terrain = new Terrain(terrainData, this.webGL, this.dataSource, new WorldEditStrings(this.dataSource),
				this);

		final float[] centerOffset = terrainData.getCenterOffset();
		final int[] mapSize = terrainData.getMapSize();

		this.terrainReady = true;
		this.anyReady = true;
		this.cliffsReady = true;

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

	}

	private void loadDoodadsAndDestructibles(final Warcraft3MapObjectData modifications) throws IOException {
		final War3MapDoo dooFile = this.mapMpq.readDoodads();

		this.applyModificationFile(this.doodadsData, this.doodadMetaData, modifications.getDoodads(),
				WorldEditorDataType.DOODADS);
		this.applyModificationFile(this.doodadsData, this.destructableMetaData, modifications.getDestructibles(),
				WorldEditorDataType.DESTRUCTIBLES);

		final War3MapDoo doo = this.mapMpq.readDoodads();

		for (final com.etheller.warsmash.parsers.w3x.doo.Doodad doodad : doo.getDoodads()) {
			WorldEditorDataType type = WorldEditorDataType.DOODADS;
			MutableGameObject row = modifications.getDoodads().get(doodad.getId());
			if (row == null) {
				row = modifications.getDestructibles().get(doodad.getId());
				type = WorldEditorDataType.DESTRUCTIBLES;
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

			this.doodads.add(new Doodad(this, model, row, doodad, type));
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
					path = row.getFieldAsString(ITEM_FILE, 0);

					if (path.toLowerCase().endsWith(".mdl") || path.toLowerCase().endsWith(".mdx")) {
						path = path.substring(0, path.length() - 4);
					}
					if (row.readSLKTagInt("fileVerFlags") == 2) {
						path += "_V1";
					}

					path += ".mdx";
				}
				else {
					path = row.getFieldAsString(UNIT_FILE, 0);

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
			this.terrain.update();

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
			this.terrain.renderGround();
//			this.terrain.renderCliffs();
			worldScene.renderOpaque();
			this.terrain.renderWater();
			worldScene.renderTranslucent();

			final List<Scene> scenes = this.scenes;
			for (final Scene scene : scenes) {
				if (scene != worldScene) {
					scene.startFrame();
					scene.renderOpaque();
					scene.renderTranslucent();
				}
			}
		}
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
