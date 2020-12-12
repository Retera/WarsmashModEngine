package com.etheller.warsmash.viewer5.handlers.w3x;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.channels.SeekableByteChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;

import javax.imageio.ImageIO;

import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.compress.utils.SeekableInMemoryByteChannel;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.etheller.warsmash.common.FetchDataTypeName;
import com.etheller.warsmash.common.LoadGenericCallback;
import com.etheller.warsmash.datasources.CompoundDataSource;
import com.etheller.warsmash.datasources.DataSource;
import com.etheller.warsmash.datasources.MpqDataSource;
import com.etheller.warsmash.datasources.SubdirDataSource;
import com.etheller.warsmash.parsers.fdf.GameUI;
import com.etheller.warsmash.parsers.w3x.War3Map;
import com.etheller.warsmash.parsers.w3x.doo.War3MapDoo;
import com.etheller.warsmash.parsers.w3x.objectdata.Warcraft3MapObjectData;
import com.etheller.warsmash.parsers.w3x.unitsdoo.War3MapUnitsDoo;
import com.etheller.warsmash.parsers.w3x.w3e.War3MapW3e;
import com.etheller.warsmash.parsers.w3x.w3i.War3MapW3i;
import com.etheller.warsmash.parsers.w3x.wpm.War3MapWpm;
import com.etheller.warsmash.units.DataTable;
import com.etheller.warsmash.units.Element;
import com.etheller.warsmash.units.StandardObjectData;
import com.etheller.warsmash.units.manager.MutableObjectData;
import com.etheller.warsmash.units.manager.MutableObjectData.MutableGameObject;
import com.etheller.warsmash.units.manager.MutableObjectData.WorldEditorDataType;
import com.etheller.warsmash.util.MappedData;
import com.etheller.warsmash.util.Quadtree;
import com.etheller.warsmash.util.QuadtreeIntersector;
import com.etheller.warsmash.util.RenderMathUtils;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.util.WorldEditStrings;
import com.etheller.warsmash.viewer5.CanvasProvider;
import com.etheller.warsmash.viewer5.GenericResource;
import com.etheller.warsmash.viewer5.Grid;
import com.etheller.warsmash.viewer5.ModelInstance;
import com.etheller.warsmash.viewer5.PathSolver;
import com.etheller.warsmash.viewer5.Scene;
import com.etheller.warsmash.viewer5.SceneLightManager;
import com.etheller.warsmash.viewer5.Texture;
import com.etheller.warsmash.viewer5.WorldScene;
import com.etheller.warsmash.viewer5.gl.WebGL;
import com.etheller.warsmash.viewer5.handlers.AbstractMdxModelViewer;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxComplexInstance;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxHandler;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxModel;
import com.etheller.warsmash.viewer5.handlers.mdx.SequenceLoopMode;
import com.etheller.warsmash.viewer5.handlers.tga.TgaFile;
import com.etheller.warsmash.viewer5.handlers.w3x.SplatModel.SplatMover;
import com.etheller.warsmash.viewer5.handlers.w3x.environment.BuildingShadow;
import com.etheller.warsmash.viewer5.handlers.w3x.environment.PathingGrid.RemovablePathingMapInstance;
import com.etheller.warsmash.viewer5.handlers.w3x.environment.Terrain;
import com.etheller.warsmash.viewer5.handlers.w3x.environment.Terrain.Splat;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.RenderAttackInstant;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.RenderAttackProjectile;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.RenderEffect;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.RenderItem;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.RenderUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.RenderUnitTypeData;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.ability.AbilityDataUI;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitClassification;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitFilterFunction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttackInstant;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttackMissile;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.projectile.CAttackProjectile;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.SimulationRenderController;
import com.etheller.warsmash.viewer5.handlers.w3x.ui.command.CommandErrorListener;
import com.etheller.warsmash.viewer5.handlers.w3x.ui.sound.KeyedSounds;

import mpq.MPQArchive;
import mpq.MPQException;

public class War3MapViewer extends AbstractMdxModelViewer {
	private static final War3ID UNIT_FILE = War3ID.fromString("umdl");
	private static final War3ID UNIT_SPECIAL = War3ID.fromString("uspa");
	private static final War3ID UBER_SPLAT = War3ID.fromString("uubs");
	private static final War3ID UNIT_SHADOW = War3ID.fromString("ushu");
	private static final War3ID UNIT_SHADOW_X = War3ID.fromString("ushx");
	private static final War3ID UNIT_SHADOW_Y = War3ID.fromString("ushy");
	private static final War3ID UNIT_SHADOW_W = War3ID.fromString("ushw");
	private static final War3ID UNIT_SHADOW_H = War3ID.fromString("ushh");
	private static final War3ID BUILDING_SHADOW = War3ID.fromString("ushb");
	public static final War3ID UNIT_SELECT_SCALE = War3ID.fromString("ussc");
	private static final War3ID UNIT_SELECT_HEIGHT = War3ID.fromString("uslz");
	private static final War3ID UNIT_SOUNDSET = War3ID.fromString("usnd");
	private static final War3ID ITEM_FILE = War3ID.fromString("ifil");
	private static final War3ID UNIT_PATHING = War3ID.fromString("upat");
	private static final War3ID DESTRUCTABLE_PATHING = War3ID.fromString("bptx");
	private static final War3ID ELEVATION_SAMPLE_RADIUS = War3ID.fromString("uerd");
	private static final War3ID MAX_PITCH = War3ID.fromString("umxp");
	private static final War3ID MAX_ROLL = War3ID.fromString("umxr");
	private static final War3ID sloc = War3ID.fromString("sloc");
	private static final LoadGenericCallback stringDataCallback = new StringDataCallbackImplementation();
	private static final float[] rayHeap = new float[6];
	public static final Ray gdxRayHeap = new Ray();
	private static final Vector2 mousePosHeap = new Vector2();
	private static final Vector3 normalHeap = new Vector3();
	public static final Vector3 intersectionHeap = new Vector3();
	private static final Rectangle rectangleHeap = new Rectangle();
	public static final StreamDataCallbackImplementation streamDataCallback = new StreamDataCallbackImplementation();

	public WorldScene worldScene;
	public boolean anyReady;
	public MappedData terrainData = new MappedData();
	public MappedData cliffTypesData = new MappedData();
	public MappedData waterData = new MappedData();
	public boolean terrainReady;
	public boolean cliffsReady;
	public boolean doodadsAndDestructiblesLoaded;
	public MappedData doodadsData = new MappedData();
	public MappedData doodadMetaData = new MappedData();
	public MappedData destructableMetaData = new MappedData();
	public List<RenderDoodad> doodads = new ArrayList<>();
	public List<TerrainDoodad> terrainDoodads = new ArrayList<>();
	public boolean doodadsReady;
	public boolean unitsAndItemsLoaded;
	public MappedData unitsData = new MappedData();
	public MappedData unitMetaData = new MappedData();
	public List<RenderUnit> units = new ArrayList<>();
	public List<RenderItem> items = new ArrayList<>();
	public List<RenderEffect> projectiles = new ArrayList<>();
	public boolean unitsReady;
	public War3Map mapMpq;

	private final DataSource gameDataSource;

	public Terrain terrain;
	public int renderPathing = 0;
	public int renderLighting = 1;

	public List<SplatModel> selModels = new ArrayList<>();
	public List<RenderUnit> selected = new ArrayList<>();
	private DataTable unitAckSoundsTable;
	private DataTable unitCombatSoundsTable;
	public DataTable miscData;
	private DataTable unitGlobalStrings;
	public DataTable uiSoundsTable;
	private MdxComplexInstance confirmationInstance;
	public MdxComplexInstance dncUnit;
	public MdxComplexInstance dncUnitDay;
	public MdxComplexInstance dncTerrain;
	public MdxComplexInstance dncTarget;
	public CSimulation simulation;
	private float updateTime;

	// for World Editor, I think
	public Vector2[] startLocations = new Vector2[WarsmashConstants.MAX_PLAYERS];

	private final DynamicShadowManager dynamicShadowManager = new DynamicShadowManager();

	private final Random seededRandom = new Random(1337L);

	private final Map<String, BufferedImage> filePathToPathingMap = new HashMap<>();

	private final List<SelectionCircleSize> selectionCircleSizes = new ArrayList<>();

	private final Map<CUnit, RenderUnit> unitToRenderPeer = new HashMap<>();
	private final Map<War3ID, RenderUnitTypeData> unitIdToTypeData = new HashMap<>();
	private GameUI gameUI;
	private Vector3 lightDirection;

	private Quadtree<MdxComplexInstance> walkableObjectsTree;
	private final QuadtreeIntersectorFindsWalkableRenderHeight walkablesIntersector = new QuadtreeIntersectorFindsWalkableRenderHeight();
	private final QuadtreeIntersectorFindsHitPoint walkablesIntersectionFinder = new QuadtreeIntersectorFindsHitPoint();
	private final QuadtreeIntersectorFindsHighestWalkable intersectorFindsHighestWalkable = new QuadtreeIntersectorFindsHighestWalkable();

	private KeyedSounds uiSounds;
	private int localPlayerIndex;
	private final CommandErrorListener commandErrorListener;

	public War3MapViewer(final DataSource dataSource, final CanvasProvider canvas,
			final CommandErrorListener errorListener) {
		super(dataSource, canvas);
		this.gameDataSource = dataSource;

		final WebGL webGL = this.webGL;

		this.addHandler(new MdxHandler());

		this.wc3PathSolver = PathSolver.DEFAULT;

		this.worldScene = this.addWorldScene();

		if (!this.dynamicShadowManager.setup(webGL)) {
			throw new IllegalStateException("FrameBuffer setup failed");
		}

		this.commandErrorListener = errorListener;
	}

	public void loadSLKs(final WorldEditStrings worldEditStrings) throws IOException {
		final GenericResource terrain = this.loadMapGeneric("TerrainArt\\Terrain.slk", FetchDataTypeName.SLK,
				stringDataCallback);
		final GenericResource cliffTypes = this.loadMapGeneric("TerrainArt\\CliffTypes.slk", FetchDataTypeName.SLK,
				stringDataCallback);
		final GenericResource water = this.loadMapGeneric("TerrainArt\\Water.slk", FetchDataTypeName.SLK,
				stringDataCallback);

		// == when loaded, which is always in our system ==
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

		this.unitAckSoundsTable = new DataTable(worldEditStrings);
		try (InputStream terrainSlkStream = this.dataSource.getResourceAsStream("UI\\SoundInfo\\UnitAckSounds.slk")) {
			this.unitAckSoundsTable.readSLK(terrainSlkStream);
		}
		this.unitCombatSoundsTable = new DataTable(worldEditStrings);
		try (InputStream terrainSlkStream = this.dataSource
				.getResourceAsStream("UI\\SoundInfo\\UnitCombatSounds.slk")) {
			this.unitCombatSoundsTable.readSLK(terrainSlkStream);
		}
		this.miscData = new DataTable(worldEditStrings);
		try (InputStream miscDataTxtStream = this.dataSource.getResourceAsStream("UI\\MiscData.txt")) {
			this.miscData.readTXT(miscDataTxtStream, true);
		}
		try (InputStream miscDataTxtStream = this.dataSource.getResourceAsStream("Units\\MiscData.txt")) {
			this.miscData.readTXT(miscDataTxtStream, true);
		}
		try (InputStream miscDataTxtStream = this.dataSource.getResourceAsStream("Units\\MiscGame.txt")) {
			this.miscData.readTXT(miscDataTxtStream, true);
		}
		try (InputStream miscDataTxtStream = this.dataSource.getResourceAsStream("UI\\MiscUI.txt")) {
			this.miscData.readTXT(miscDataTxtStream, true);
		}
		try (InputStream miscDataTxtStream = this.dataSource.getResourceAsStream("UI\\SoundInfo\\MiscData.txt")) {
			this.miscData.readTXT(miscDataTxtStream, true);
		}
		if (this.dataSource.has("war3mapMisc.txt")) {
			try (InputStream miscDataTxtStream = this.dataSource.getResourceAsStream("war3mapMisc.txt")) {
				this.miscData.readTXT(miscDataTxtStream, true);
			}
		}
		final Element light = this.miscData.get("Light");
		final float lightX = light.getFieldFloatValue("Direction", 0);
		final float lightY = light.getFieldFloatValue("Direction", 1);
		final float lightZ = light.getFieldFloatValue("Direction", 2);
		this.lightDirection = new Vector3(lightX, lightY, lightZ).nor();
		this.unitGlobalStrings = new DataTable(worldEditStrings);
		try (InputStream miscDataTxtStream = this.dataSource.getResourceAsStream("Units\\UnitGlobalStrings.txt")) {
			this.unitGlobalStrings.readTXT(miscDataTxtStream, true);
		}
		final Element categories = this.unitGlobalStrings.get("Categories");
		for (final CUnitClassification unitClassification : CUnitClassification.values()) {
			if (unitClassification.getLocaleKey() != null) {
				final String displayName = categories.getField(unitClassification.getLocaleKey());
				unitClassification.setDisplayName(displayName);
			}
		}
		this.selectionCircleSizes.clear();
		final Element selectionCircleData = this.miscData.get("SelectionCircle");
		final int selectionCircleNumSizes = selectionCircleData.getFieldValue("NumSizes");
		for (int i = 0; i < selectionCircleNumSizes; i++) {
			final String indexString = i < 10 ? "0" + i : Integer.toString(i);
			final float size = selectionCircleData.getFieldFloatValue("Size" + indexString);
			final String texture = selectionCircleData.getField("Texture" + indexString);
			final String textureDotted = selectionCircleData.getField("TextureDotted" + indexString);
			this.selectionCircleSizes.add(new SelectionCircleSize(size, texture, textureDotted));
		}
		this.selectionCircleScaleFactor = selectionCircleData.getFieldFloatValue("ScaleFactor");

		this.uiSoundsTable = new DataTable(worldEditStrings);
		try (InputStream miscDataTxtStream = this.dataSource.getResourceAsStream("UI\\SoundInfo\\UISounds.slk")) {
			this.uiSoundsTable.readSLK(miscDataTxtStream);
		}
		try (InputStream miscDataTxtStream = this.dataSource.getResourceAsStream("UI\\SoundInfo\\AmbienceSounds.slk")) {
			this.uiSoundsTable.readSLK(miscDataTxtStream);
		}
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

	public void loadMap(final String mapFilePath, final int localPlayerIndex) throws IOException {
		this.localPlayerIndex = localPlayerIndex;
		final War3Map war3Map = new War3Map(this.gameDataSource, mapFilePath);

		this.mapMpq = war3Map;

		final PathSolver wc3PathSolver = this.wc3PathSolver;

		char tileset = 'A';

		final War3MapW3i w3iFile = this.mapMpq.readMapInformation();

		tileset = w3iFile.getTileset();

		DataSource tilesetSource;
		try {
			// Slightly complex. Here's the theory:
			// 1.) Copy map into RAM
			// 2.) Setup a Data Source that will read assets
			// from either the map or the game, giving the map priority.
			SeekableByteChannel sbc;
			final CompoundDataSource compoundDataSource = war3Map.getCompoundDataSource();
			try (InputStream mapStream = compoundDataSource.getResourceAsStream(tileset + ".mpq")) {
				if (mapStream == null) {
					tilesetSource = new CompoundDataSource(Arrays.asList(compoundDataSource,
							new SubdirDataSource(compoundDataSource, tileset + ".mpq/"),
							new SubdirDataSource(compoundDataSource, "_tilesets/" + tileset + ".w3mod/")));
				}
				else {
					final byte[] mapData = IOUtils.toByteArray(mapStream);
					sbc = new SeekableInMemoryByteChannel(mapData);
					final DataSource internalMpqContentsDataSource = new MpqDataSource(new MPQArchive(sbc), sbc);
					tilesetSource = new CompoundDataSource(
							Arrays.asList(compoundDataSource, internalMpqContentsDataSource));
				}
			}
			catch (final IOException exc) {
				tilesetSource = new CompoundDataSource(
						Arrays.asList(compoundDataSource, new SubdirDataSource(compoundDataSource, tileset + ".mpq/"),
								new SubdirDataSource(compoundDataSource, "_tilesets/" + tileset + ".w3mod/")));
			}
		}
		catch (final MPQException e) {
			throw new RuntimeException(e);
		}
		setDataSource(tilesetSource);
		this.worldEditStrings = new WorldEditStrings(this.dataSource);
		loadSLKs(this.worldEditStrings);

		this.solverParams.tileset = Character.toLowerCase(tileset);

		final War3MapW3e terrainData = this.mapMpq.readEnvironment();

		final War3MapWpm terrainPathing = this.mapMpq.readPathing();

		final StandardObjectData standardObjectData = new StandardObjectData(this.dataSource);
		this.worldEditData = standardObjectData.getWorldEditData();

		this.terrain = new Terrain(terrainData, terrainPathing, w3iFile, this.webGL, this.dataSource,
				this.worldEditStrings, this, this.worldEditData);

		final float[] centerOffset = terrainData.getCenterOffset();
		final int[] mapSize = terrainData.getMapSize();

		this.terrainReady = true;
		this.anyReady = true;
		this.cliffsReady = true;

		// Override the grid based on the map.
		this.worldScene.grid = new Grid(centerOffset[0], centerOffset[1], (mapSize[0] * 128) - 128,
				(mapSize[1] * 128) - 128, 16 * 128, 16 * 128);

		final MdxModel confirmation = (MdxModel) load("UI\\Feedback\\Confirmation\\Confirmation.mdx",
				PathSolver.DEFAULT, null);
		this.confirmationInstance = (MdxComplexInstance) confirmation.addInstance();
		this.confirmationInstance.setSequenceLoopMode(SequenceLoopMode.NEVER_LOOP_AND_HIDE_WHEN_DONE);
		this.confirmationInstance.setSequence(0);
		this.confirmationInstance.setScene(this.worldScene);

		this.allObjectData = this.mapMpq.readModifications();
		this.simulation = new CSimulation(this.miscData, this.allObjectData.getUnits(),
				this.allObjectData.getAbilities(), new SimulationRenderController() {
					private final Map<String, UnitSound> keyToCombatSound = new HashMap<>();

					@Override
					public CAttackProjectile createAttackProjectile(final CSimulation simulation, final float launchX,
							final float launchY, final float launchFacing, final CUnit source,
							final CUnitAttackMissile unitAttack, final AbilityTarget target, final float damage,
							final int bounceIndex) {
						final War3ID typeId = source.getTypeId();
						final int projectileSpeed = unitAttack.getProjectileSpeed();
						final float projectileArc = unitAttack.getProjectileArc();
						String missileArt = unitAttack.getProjectileArt();
						final float projectileLaunchX = simulation.getUnitData().getProjectileLaunchX(typeId);
						final float projectileLaunchY = simulation.getUnitData().getProjectileLaunchY(typeId);
						final float projectileLaunchZ = simulation.getUnitData().getProjectileLaunchZ(typeId);

						missileArt = mdx(missileArt);
						final float facing = launchFacing;
						final float sinFacing = (float) Math.sin(facing);
						final float cosFacing = (float) Math.cos(facing);
						final float x = (launchX + (projectileLaunchY * cosFacing)) + (projectileLaunchX * sinFacing);
						final float y = (launchY + (projectileLaunchY * sinFacing)) - (projectileLaunchX * cosFacing);

						final float height = War3MapViewer.this.terrain.getGroundHeight(x, y) + source.getFlyHeight()
								+ projectileLaunchZ;
						final CAttackProjectile simulationAttackProjectile = new CAttackProjectile(x, y,
								projectileSpeed, target, source, damage, unitAttack, bounceIndex);

						final MdxModel model = (MdxModel) load(missileArt, War3MapViewer.this.mapPathSolver,
								War3MapViewer.this.solverParams);
						final MdxComplexInstance modelInstance = (MdxComplexInstance) model.addInstance();
						modelInstance.setTeamColor(source.getPlayerIndex());
						modelInstance.setScene(War3MapViewer.this.worldScene);
						if (bounceIndex == 0) {
							SequenceUtils.randomBirthSequence(modelInstance);
						}
						else {
							SequenceUtils.randomStandSequence(modelInstance);
						}
						modelInstance.setLocation(x, y, height);
						final RenderAttackProjectile renderAttackProjectile = new RenderAttackProjectile(
								simulationAttackProjectile, modelInstance, height, projectileArc, War3MapViewer.this);

						War3MapViewer.this.projectiles.add(renderAttackProjectile);

						return simulationAttackProjectile;
					}

					@Override
					public void createInstantAttackEffect(final CSimulation cSimulation, final CUnit source,
							final CUnitAttackInstant unitAttack, final CWidget target) {
						final War3ID typeId = source.getTypeId();

						String missileArt = unitAttack.getProjectileArt();
						final float projectileLaunchX = War3MapViewer.this.simulation.getUnitData()
								.getProjectileLaunchX(typeId);
						final float projectileLaunchY = War3MapViewer.this.simulation.getUnitData()
								.getProjectileLaunchY(typeId);
						missileArt = mdx(missileArt);
						final float facing = (float) Math.toRadians(source.getFacing());
						final float sinFacing = (float) Math.sin(facing);
						final float cosFacing = (float) Math.cos(facing);
						final float x = (source.getX() + (projectileLaunchY * cosFacing))
								+ (projectileLaunchX * sinFacing);
						final float y = (source.getY() + (projectileLaunchY * sinFacing))
								- (projectileLaunchX * cosFacing);

						final float targetX = target.getX();
						final float targetY = target.getY();
						final float angleToTarget = (float) Math.atan2(targetY - y, targetX - x);

						final float height = War3MapViewer.this.terrain.getGroundHeight(targetX, targetY)
								+ target.getFlyHeight() + target.getImpactZ();

						final MdxModel model = (MdxModel) load(missileArt, War3MapViewer.this.mapPathSolver,
								War3MapViewer.this.solverParams);
						final MdxComplexInstance modelInstance = (MdxComplexInstance) model.addInstance();
						modelInstance.setTeamColor(source.getPlayerIndex());
						modelInstance.setScene(War3MapViewer.this.worldScene);
						SequenceUtils.randomBirthSequence(modelInstance);
						modelInstance.setLocation(targetX, targetY, height);
						War3MapViewer.this.projectiles
								.add(new RenderAttackInstant(modelInstance, War3MapViewer.this, angleToTarget));
					}

					@Override
					public void spawnUnitDamageSound(final CUnit damagedUnit, final String weaponSound,
							final String armorType) {
						final String key = weaponSound + armorType;
						UnitSound combatSound = this.keyToCombatSound.get(key);
						if (combatSound == null) {
							combatSound = UnitSound.create(War3MapViewer.this.dataSource,
									War3MapViewer.this.unitCombatSoundsTable, weaponSound, armorType);
							this.keyToCombatSound.put(key, combatSound);
						}
						combatSound.play(War3MapViewer.this.worldScene.audioContext, damagedUnit.getX(),
								damagedUnit.getY());
					}

					@Override
					public void spawnUnitConstructionSound(final CUnit constructingUnit,
							final CUnit constructedStructure) {
						final UnitSound constructingBuilding = War3MapViewer.this.uiSounds
								.getSound(War3MapViewer.this.gameUI.getSkinField("ConstructingBuilding"));
						if (constructingBuilding != null) {
							constructingBuilding.playUnitResponse(War3MapViewer.this.worldScene.audioContext,
									War3MapViewer.this.unitToRenderPeer.get(constructedStructure));
						}
					}

					@Override
					public void removeUnit(final CUnit unit) {
						final RenderUnit renderUnit = War3MapViewer.this.unitToRenderPeer.remove(unit);
						War3MapViewer.this.units.remove(renderUnit);
						War3MapViewer.this.worldScene.removeInstance(renderUnit.instance);
					}

					@Override
					public BufferedImage getBuildingPathingPixelMap(final War3ID rawcode) {
						return War3MapViewer.this
								.getBuildingPathingPixelMap(War3MapViewer.this.allObjectData.getUnits().get(rawcode));
					}

					@Override
					public void spawnUnitConstructionFinishSound(final CUnit constructedStructure) {
						final UnitSound constructingBuilding = War3MapViewer.this.uiSounds
								.getSound(War3MapViewer.this.gameUI.getSkinField("JobDoneSound"));
						if (constructingBuilding != null) {
							constructingBuilding.play(War3MapViewer.this.worldScene.audioContext,
									constructedStructure.getX(), constructedStructure.getY());
						}
					}

					@Override
					public CUnit createUnit(final CSimulation simulation, final War3ID typeId, final int playerIndex,
							final float x, final float y, final float facing) {
						return createNewUnit(War3MapViewer.this.allObjectData, typeId, x, y, 0f, playerIndex,
								(float) Math.toRadians(facing));
					}

					@Override
					public void spawnBuildingDeathEffect(final CUnit source) {
						final RenderUnit renderUnit = War3MapViewer.this.unitToRenderPeer.get(source);
						if (renderUnit.specialArtModel != null) {
							final MdxComplexInstance modelInstance = (MdxComplexInstance) renderUnit.specialArtModel
									.addInstance();
							modelInstance.setTeamColor(source.getPlayerIndex());
							modelInstance.setLocation(renderUnit.location);
							modelInstance.setScene(War3MapViewer.this.worldScene);
							SequenceUtils.randomBirthSequence(modelInstance);
							War3MapViewer.this.projectiles
									.add(new RenderAttackInstant(modelInstance, War3MapViewer.this,
											(float) Math.toRadians(renderUnit.getSimulationUnit().getFacing())));
						}
					}

					@Override
					public void spawnUnitReadySound(final CUnit trainedUnit) {
						final RenderUnit renderPeer = War3MapViewer.this.unitToRenderPeer.get(trainedUnit);
						renderPeer.soundset.ready.playUnitResponse(War3MapViewer.this.worldScene.audioContext,
								renderPeer);
					}

					@Override
					public void unitRepositioned(final CUnit cUnit) {
						final RenderUnit renderPeer = War3MapViewer.this.unitToRenderPeer.get(cUnit);
						renderPeer.repositioned(War3MapViewer.this);
					}
				}, this.terrain.pathingGrid, this.terrain.getEntireMap(), this.seededRandom, w3iFile.getPlayers(),
				this.commandErrorListener);

		this.walkableObjectsTree = new Quadtree<>(this.terrain.getEntireMap());
		if (this.doodadsAndDestructiblesLoaded) {
			this.loadDoodadsAndDestructibles(this.allObjectData);
		}
		else {
			throw new IllegalStateException("transcription of JS has not loaded a map and has no JS async promises");
		}

		loadSounds();

		this.terrain.createWaves();

		loadLightsAndShading(tileset);
	}

	private void loadSounds() {
		this.uiSounds = new KeyedSounds(this.uiSoundsTable, this.mapMpq);
	}

	/**
	 * Loads the map information that should be loaded after UI, such as units, who
	 * need to be able to setup their UI counterparts (icons, etc) for their
	 * abilities while loading. This allows the dynamic creation of units while the
	 * game is playing to better share code with the startup sequence's creation of
	 * units.
	 *
	 * @throws IOException
	 */
	public void loadAfterUI() throws IOException {
		if (this.unitsAndItemsLoaded) {
			this.loadUnitsAndItems(this.allObjectData);
		}
		else {
			throw new IllegalStateException("transcription of JS has not loaded a map and has no JS async promises");
		}

		// After we finish loading units, we need to update & create the stored shadow
		// information for all unit shadows
		this.terrain.initShadows();
	}

	private void loadLightsAndShading(final char tileset) {
		// TODO this should be set by the war3map.j actually, not by the tileset, so the
		// call to set day night models is just for testing to make the test look pretty
		final Element defaultTerrainLights = this.worldEditData.get("TerrainLights");
		final Element defaultUnitLights = this.worldEditData.get("UnitLights");
		setDayNightModels(defaultTerrainLights.getField(Character.toString(tileset)),
				defaultUnitLights.getField(Character.toString(tileset)));

	}

	private void loadDoodadsAndDestructibles(final Warcraft3MapObjectData modifications) throws IOException {
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
			if (row != null) {
				String file = row.readSLKTag("file");
				final int numVar = row.readSLKTagInt("numVar");

				if (file.endsWith(".mdx") || file.endsWith(".mdl")) {
					file = file.substring(0, file.length() - 4);
				}

				String fileVar = file;

				file += ".mdx";

				if (numVar > 1) {
					fileVar += Math.min(doodad.getVariation(), numVar - 1);
				}

				fileVar += ".mdx";

				final float maxPitch = row.readSLKTagFloat("maxPitch");
				final float maxRoll = row.readSLKTagFloat("maxRoll");
				if (type == WorldEditorDataType.DESTRUCTIBLES) {
					final String shadowString = row.readSLKTag("shadow");
					if ((shadowString != null) && (shadowString.length() > 0) && !"_".equals(shadowString)) {
						this.terrain.addShadow(shadowString, doodad.getLocation()[0], doodad.getLocation()[1]);
					}

					final String pathingTexture = row.readSLKTag("pathTex");
					if ((pathingTexture != null) && (pathingTexture.length() > 0) && !"_".equals(pathingTexture)) {

						BufferedImage bufferedImage = this.filePathToPathingMap.get(pathingTexture.toLowerCase());
						if (bufferedImage == null) {
							if (this.mapMpq.has(pathingTexture)) {
								try {
									bufferedImage = TgaFile.readTGA(pathingTexture,
											this.mapMpq.getResourceAsStream(pathingTexture));
									this.filePathToPathingMap.put(pathingTexture.toLowerCase(), bufferedImage);
								}
								catch (final Exception exc) {
									exc.printStackTrace();
								}
							}
						}
						if (bufferedImage != null) {
							this.terrain.pathingGrid.blitRemovablePathingOverlayTexture(doodad.getLocation()[0],
									doodad.getLocation()[1], (int) Math.toDegrees(doodad.getAngle()), bufferedImage);
						}
					}
				}
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

				if (type == WorldEditorDataType.DESTRUCTIBLES) {
					final RenderDestructable renderDestructable = new RenderDestructable(this, model, row, doodad, type,
							maxPitch, maxRoll, doodad.getLife());
					if (row.readSLKTagBoolean("walkable")) {
						final float x = doodad.getLocation()[0];
						final float y = doodad.getLocation()[1];
						final BoundingBox boundingBox = model.bounds.getBoundingBox();
						final float minX = boundingBox.min.x + x;
						final float minY = boundingBox.min.y + y;
						final Rectangle renderDestructableBounds = new Rectangle(minX, minY, boundingBox.getWidth(),
								boundingBox.getHeight());
						this.walkableObjectsTree.add((MdxComplexInstance) renderDestructable.instance,
								renderDestructableBounds);
					}
					this.doodads.add(renderDestructable);
				}
				else {
					this.doodads.add(new RenderDoodad(this, model, row, doodad, type, maxPitch, maxRoll));
				}
			}
		}

		// Cliff/Terrain doodads.
		for (final com.etheller.warsmash.parsers.w3x.doo.TerrainDoodad doodad : doo.getTerrainDoodads()) {
			final MutableGameObject row = modifications.getDoodads().get(doodad.getId());
			String file = row.readSLKTag("file");//
			if ("".equals(file)) {
				final String blaBla = row.readSLKTag("file");
				System.out.println("bla");
			}
			if (file.toLowerCase().endsWith(".mdl")) {
				file = file.substring(0, file.length() - 4);
			}
			if (!file.toLowerCase().endsWith(".mdx")) {
				file += ".mdx";
			}
			final MdxModel model = (MdxModel) this.load(file, this.mapPathSolver, this.solverParams);

			final String pathingTexture = row.readSLKTag("pathTex");
			BufferedImage pathingTextureImage;
			if ((pathingTexture != null) && (pathingTexture.length() > 0) && !"_".equals(pathingTexture)) {

				pathingTextureImage = this.filePathToPathingMap.get(pathingTexture.toLowerCase());
				if (pathingTextureImage == null) {
					if (this.mapMpq.has(pathingTexture)) {
						try {
							pathingTextureImage = TgaFile.readTGA(pathingTexture,
									this.mapMpq.getResourceAsStream(pathingTexture));
							this.filePathToPathingMap.put(pathingTexture.toLowerCase(), pathingTextureImage);
						}
						catch (final Exception exc) {
							exc.printStackTrace();
						}
					}
				}
			}
			else {
				pathingTextureImage = null;
			}
			if (pathingTextureImage != null) {
				// blit out terrain cells under this TerrainDoodad
				final int textureWidth = pathingTextureImage.getWidth();
				final int textureHeight = pathingTextureImage.getHeight();
				final int textureWidthTerrainCells = textureWidth / 4;
				final int textureHeightTerrainCells = textureHeight / 4;
				final int minCellX = ((int) doodad.getLocation()[0]);
				final int minCellY = ((int) doodad.getLocation()[1]);
				final int maxCellX = (minCellX + textureWidthTerrainCells) - 1;
				final int maxCellY = (minCellY + textureHeightTerrainCells) - 1;
				for (int j = minCellY; j <= maxCellY; j++) {
					for (int i = minCellX; i <= maxCellX; i++) {
						this.terrain.removeTerrainCellWithoutFlush(i, j);
					}
				}
				this.terrain.flushRemovedTerrainCells();
			}

			System.out.println("Loading terrain doodad: " + file);
			this.terrainDoodads.add(new TerrainDoodad(this, model, row, doodad, pathingTextureImage));
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
		this.unitsReady = false;

		this.soundsetNameToSoundset = new HashMap<>();

		if (this.dataSource.has("war3mapUnits.doo")) {
			final War3MapUnitsDoo dooFile = mpq.readUnits();

			// Collect the units and items data.
			for (final com.etheller.warsmash.parsers.w3x.unitsdoo.Unit unit : dooFile.getUnits()) {
				final War3ID unitId = unit.getId();
				final float unitX = unit.getLocation()[0];
				final float unitY = unit.getLocation()[1];
				final float unitZ = unit.getLocation()[2];
				final int playerIndex = unit.getPlayer();
				final float unitAngle = unit.getAngle();

				createNewUnit(modifications, unitId, unitX, unitY, unitZ, playerIndex, unitAngle);
			}
		}

		this.terrain.loadSplats();

		this.unitsReady = true;
		this.anyReady = true;
	}

	private CUnit createNewUnit(final Warcraft3MapObjectData modifications, final War3ID unitId, float unitX,
			float unitY, final float unitZ, final int playerIndex, final float unitAngle) {
		UnitSoundset soundset = null;
		MutableGameObject row = null;
		String path = null;
		Splat unitShadowSplat = null;
		SplatMover unitShadowSplatDynamicIngame = null;
		Splat buildingUberSplat = null;
		SplatMover buildingUberSplatDynamicIngame = null;
		BufferedImage buildingPathingPixelMap = null;
		final float unitVertexScale = 1.0f;
		RemovablePathingMapInstance pathingInstance = null;
		BuildingShadow buildingShadowInstance = null;

		// Hardcoded?
		WorldEditorDataType type = null;
		if (sloc.equals(unitId)) {
//				path = "Objects\\StartLocation\\StartLocation.mdx";
			type = null; /// ??????
			this.startLocations[playerIndex] = new Vector2(unitX, unitY);
		}
		else {
			row = modifications.getUnits().get(unitId);
			if (row == null) {
				row = modifications.getItems().get(unitId);
				if (row != null) {
					type = WorldEditorDataType.ITEM;
					path = row.getFieldAsString(ITEM_FILE, 0);

					if (path.toLowerCase().endsWith(".mdl") || path.toLowerCase().endsWith(".mdx")) {
						path = path.substring(0, path.length() - 4);
					}

					final Element misc = this.miscData.get("Misc");
					final String itemShadowFile = misc.getField("ItemShadowFile");
					final int itemShadowWidth = misc.getFieldValue("ItemShadowSize", 0);
					final int itemShadowHeight = misc.getFieldValue("ItemShadowSize", 1);
					final int itemShadowX = misc.getFieldValue("ItemShadowOffset", 0);
					final int itemShadowY = misc.getFieldValue("ItemShadowOffset", 1);
					if ((itemShadowFile != null) && !"_".equals(itemShadowFile)) {
						final String texture = "ReplaceableTextures\\Shadows\\" + itemShadowFile + ".blp";
						final float shadowX = itemShadowX;
						final float shadowY = itemShadowY;
						final float shadowWidth = itemShadowWidth;
						final float shadowHeight = itemShadowHeight;
						if (!this.terrain.splats.containsKey(texture)) {
							final Splat splat = new Splat();
							splat.opacity = 0.5f;
							this.terrain.splats.put(texture, splat);
						}
						final float x = unitX - shadowX;
						final float y = unitY - shadowY;
						this.terrain.splats.get(texture).locations
								.add(new float[] { x, y, x + shadowWidth, y + shadowHeight, 3 });
						unitShadowSplat = this.terrain.splats.get(texture);
					}

					path += ".mdx";
				}
			}
			else {
				type = WorldEditorDataType.UNITS;
				path = getUnitModelPath(row);

				buildingPathingPixelMap = getBuildingPathingPixelMap(row);
				if (buildingPathingPixelMap != null) {
					unitX = (float) Math.floor(unitX / 64f) * 64f;
					unitY = (float) Math.floor(unitY / 64f) * 64f;
					if (((buildingPathingPixelMap.getWidth() / 2) % 2) == 1) {
						unitX += 32f;
					}
					if (((buildingPathingPixelMap.getHeight() / 2) % 2) == 1) {
						unitY += 32f;
					}
					pathingInstance = this.terrain.pathingGrid.blitRemovablePathingOverlayTexture(unitX, unitY,
							(int) Math.toDegrees(unitAngle), buildingPathingPixelMap);
				}

				final String uberSplat = row.getFieldAsString(UBER_SPLAT, 0);
				if (uberSplat != null) {
					final Element uberSplatInfo = this.terrain.uberSplatTable.get(uberSplat);
					if (uberSplatInfo != null) {
						final String texturePath = uberSplatInfo.getField("Dir") + "\\" + uberSplatInfo.getField("file")
								+ ".blp";
						final float s = uberSplatInfo.getFieldFloatValue("Scale");
						if (this.unitsReady) {
							buildingUberSplatDynamicIngame = this.terrain.addUberSplat(texturePath, unitX, unitY, 1, s,
									false, false);
						}
						else {
							if (!this.terrain.splats.containsKey(texturePath)) {
								this.terrain.splats.put(texturePath, new Splat());
							}
							final float x = unitX;
							final float y = unitY;
							buildingUberSplat = this.terrain.splats.get(texturePath);
							buildingUberSplat.locations.add(new float[] { x - s, y - s, x + s, y + s, 1 });
						}
					}
				}

				final String unitShadow = row.getFieldAsString(UNIT_SHADOW, 0);
				if ((unitShadow != null) && !"_".equals(unitShadow)) {
					final String texture = "ReplaceableTextures\\Shadows\\" + unitShadow + ".blp";
					final float shadowX = row.getFieldAsFloat(UNIT_SHADOW_X, 0);
					final float shadowY = row.getFieldAsFloat(UNIT_SHADOW_Y, 0);
					final float shadowWidth = row.getFieldAsFloat(UNIT_SHADOW_W, 0);
					final float shadowHeight = row.getFieldAsFloat(UNIT_SHADOW_H, 0);
					if (this.mapMpq.has(texture)) {
						final float x = unitX - shadowX;
						final float y = unitY - shadowY;
						if (this.unitsReady) {
							unitShadowSplatDynamicIngame = this.terrain.addUnitShadowSplat(texture, x, y,
									x + shadowWidth, y + shadowHeight, 3, 0.5f);
						}
						else {
							if (!this.terrain.splats.containsKey(texture)) {
								final Splat splat = new Splat();
								splat.opacity = 0.5f;
								this.terrain.splats.put(texture, splat);
							}
							this.terrain.splats.get(texture).locations
									.add(new float[] { x, y, x + shadowWidth, y + shadowHeight, 3 });
							unitShadowSplat = this.terrain.splats.get(texture);
						}
					}
				}

				final String buildingShadow = row.getFieldAsString(BUILDING_SHADOW, 0);
				if ((buildingShadow != null) && !"_".equals(buildingShadow)) {
					buildingShadowInstance = this.terrain.addShadow(buildingShadow, unitX, unitY);
				}

				final String soundName = row.getFieldAsString(UNIT_SOUNDSET, 0);
				UnitSoundset unitSoundset = this.soundsetNameToSoundset.get(soundName);
				if (unitSoundset == null) {
					unitSoundset = new UnitSoundset(this.dataSource, this.unitAckSoundsTable, soundName);
					this.soundsetNameToSoundset.put(soundName, unitSoundset);
				}
				soundset = unitSoundset;

			}
		}

		if (path != null) {
			final String unitSpecialArtPath = row.getFieldAsString(UNIT_SPECIAL, 0);
			MdxModel specialArtModel;
			if (unitSpecialArtPath != null) {
				try {
					specialArtModel = (MdxModel) this.load(mdx(unitSpecialArtPath), this.mapPathSolver,
							this.solverParams);
				}
				catch (final Exception exc) {
					exc.printStackTrace();
					specialArtModel = null;
				}
			}
			else {
				specialArtModel = null;
			}
			final MdxModel model = (MdxModel) this.load(path, this.mapPathSolver, this.solverParams);
			MdxModel portraitModel;
			final String portraitPath = path.substring(0, path.length() - 4) + "_portrait.mdx";
			if (this.dataSource.has(portraitPath)) {
				portraitModel = (MdxModel) this.load(portraitPath, this.mapPathSolver, this.solverParams);
			}
			else {
				portraitModel = model;
			}
			if (type == WorldEditorDataType.UNITS) {
				final float angle = (float) Math.toDegrees(unitAngle);
				final CUnit simulationUnit = this.simulation.createUnit(row.getAlias(), playerIndex, unitX, unitY,
						angle, buildingPathingPixelMap, pathingInstance, buildingShadowInstance);
				final RenderUnitTypeData typeData = getUnitTypeData(unitId, row);
				final RenderUnit renderUnit = new RenderUnit(this, model, row, unitX, unitY, unitZ, playerIndex,
						soundset, portraitModel, simulationUnit, typeData, specialArtModel);
				this.unitToRenderPeer.put(simulationUnit, renderUnit);
				this.units.add(renderUnit);
				if (unitShadowSplat != null) {
					unitShadowSplat.unitMapping.add(new Consumer<SplatModel.SplatMover>() {
						@Override
						public void accept(final SplatMover t) {
							renderUnit.shadow = t;
						}
					});
				}
				if (unitShadowSplatDynamicIngame != null) {
					renderUnit.shadow = unitShadowSplatDynamicIngame;
				}
				if (buildingUberSplat != null) {
					buildingUberSplat.unitMapping.add(new Consumer<SplatModel.SplatMover>() {
						@Override
						public void accept(final SplatMover t) {
							renderUnit.uberSplat = t;
						}
					});
				}
				if (buildingUberSplatDynamicIngame != null) {
					renderUnit.uberSplat = buildingUberSplatDynamicIngame;
				}
				return simulationUnit;
			}
			else {
				this.items
						.add(new RenderItem(this, model, row, unitX, unitY, unitZ, unitAngle, soundset, portraitModel)); // TODO
				// store
				// somewhere
				if (unitShadowSplat != null) {
					unitShadowSplat.unitMapping.add(new Consumer<SplatModel.SplatMover>() {
						@Override
						public void accept(final SplatMover t) {

						}
					});
				}
				if (unitShadowSplatDynamicIngame != null) {
				}
			}
		}
		else {
			System.err.println("Unknown unit ID: " + unitId);
		}
		return null;
	}

	public String getUnitModelPath(final MutableGameObject row) {
		String path;
		path = row.getFieldAsString(UNIT_FILE, 0);

		if (path.toLowerCase().endsWith(".mdl") || path.toLowerCase().endsWith(".mdx")) {
			path = path.substring(0, path.length() - 4);
		}
		if ((row.readSLKTagInt("fileVerFlags") == 2) && this.dataSource.has(path + "_V1.mdx")) {
			path += "_V1";
		}

		path += ".mdx";
		return path;
	}

	private BufferedImage getBuildingPathingPixelMap(final MutableGameObject row) {
		BufferedImage buildingPathingPixelMap = null;
		final String pathingTexture = row.getFieldAsString(UNIT_PATHING, 0);
		if ((pathingTexture != null) && (pathingTexture.length() > 0) && !"_".equals(pathingTexture)) {
			buildingPathingPixelMap = this.filePathToPathingMap.get(pathingTexture.toLowerCase());
			if (buildingPathingPixelMap == null) {
				try {
					if (pathingTexture.toLowerCase().endsWith(".tga")) {
						buildingPathingPixelMap = TgaFile.readTGA(pathingTexture,
								this.mapMpq.getResourceAsStream(pathingTexture));
					}
					else {
						try (InputStream stream = this.mapMpq.getResourceAsStream(pathingTexture)) {
							buildingPathingPixelMap = ImageIO.read(stream);
							System.out.println("LOADING BLP PATHING: " + pathingTexture);
						}
					}
					this.filePathToPathingMap.put(pathingTexture.toLowerCase(), buildingPathingPixelMap);
				}
				catch (final IOException exc) {
					System.err.println("Failure to get pathing: " + exc.getClass() + ":" + exc.getMessage());
				}
			}
		}
		return buildingPathingPixelMap;
	}

	public RenderUnitTypeData getUnitTypeData(final War3ID key, final MutableGameObject row) {
		RenderUnitTypeData unitTypeData = this.unitIdToTypeData.get(key);
		if (unitTypeData == null) {
			unitTypeData = new RenderUnitTypeData(row.getFieldAsFloat(MAX_PITCH, 0), row.getFieldAsFloat(MAX_ROLL, 0),
					row.getFieldAsFloat(ELEVATION_SAMPLE_RADIUS, 0));
			this.unitIdToTypeData.put(key, unitTypeData);
		}
		return unitTypeData;
	}

	@Override
	public void update() {
		if (this.anyReady) {
			this.terrain.update();

			super.update();

			for (final RenderUnit unit : this.units) {
				unit.updateAnimations(this);
			}
			final Iterator<RenderEffect> projectileIterator = this.projectiles.iterator();
			while (projectileIterator.hasNext()) {
				final RenderEffect projectile = projectileIterator.next();
				if (projectile.updateAnimations(this, Gdx.graphics.getDeltaTime())) {
					projectileIterator.remove();
				}
			}
			for (final RenderItem item : this.items) {
				final MdxComplexInstance instance = item.instance;
				final MdxComplexInstance mdxComplexInstance = instance;
				if (mdxComplexInstance.sequenceEnded || (mdxComplexInstance.sequence == -1)) {
					SequenceUtils.randomStandSequence(mdxComplexInstance);
				}
			}
			for (final RenderDoodad item : this.doodads) {
				final ModelInstance instance = item.instance;
				if (instance instanceof MdxComplexInstance) {
					final MdxComplexInstance mdxComplexInstance = (MdxComplexInstance) instance;
					if ((mdxComplexInstance.sequence == -1) || (mdxComplexInstance.sequenceEnded
							&& ((item.getAnimation() != AnimationTokens.PrimaryTag.DEATH)
									|| (((MdxModel) mdxComplexInstance.model).sequences.get(mdxComplexInstance.sequence)
											.getFlags() == 0)))) {
						SequenceUtils.randomSequence(mdxComplexInstance, item.getAnimation(), SequenceUtils.EMPTY,
								true);

					}
				}
			}

			final float rawDeltaTime = Gdx.graphics.getRawDeltaTime();
			this.updateTime += rawDeltaTime;
			while (this.updateTime >= WarsmashConstants.SIMULATION_STEP_TIME) {
				this.updateTime -= WarsmashConstants.SIMULATION_STEP_TIME;
				this.simulation.update();
			}
			this.dncTerrain.setFrameByRatio(
					this.simulation.getGameTimeOfDay() / this.simulation.getGameplayConstants().getGameDayHours());
			this.dncTerrain.update(rawDeltaTime, null);
			this.dncUnit.setFrameByRatio(
					this.simulation.getGameTimeOfDay() / this.simulation.getGameplayConstants().getGameDayHours());
			this.dncUnit.update(rawDeltaTime, null);
			this.dncUnitDay.setFrameByRatio(0.5f);
			this.dncUnitDay.update(rawDeltaTime, null);
			this.dncTarget.setFrameByRatio(
					this.simulation.getGameTimeOfDay() / this.simulation.getGameplayConstants().getGameDayHours());
			this.dncTarget.update(rawDeltaTime, null);
		}
	}

	@Override
	public void render() {
		if (this.anyReady) {
			final Scene worldScene = this.worldScene;

			startFrame();
			worldScene.startFrame();
			worldScene.renderOpaque(this.dynamicShadowManager, this.webGL);
			this.terrain.renderGround(this.dynamicShadowManager);
			this.terrain.renderCliffs();
			worldScene.renderOpaque();
			this.terrain.renderUberSplats(false);
			this.terrain.renderWater();
			worldScene.renderTranslucent();
			this.terrain.renderUberSplats(true);

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

	public void deselect() {
		if (!this.selModels.isEmpty()) {
			for (final SplatModel model : this.selModels) {
				this.terrain.removeSplatBatchModel("selection");
			}
			this.selModels.clear();
			for (final RenderUnit unit : this.selected) {
				unit.selectionCircle = null;
			}
		}
		this.selected.clear();
	}

	public void doSelectUnit(final List<RenderUnit> units) {
		deselect();
		if (units.isEmpty()) {
			return;
		}

		final Map<String, Terrain.Splat> splats = new HashMap<String, Terrain.Splat>();
		for (final RenderUnit unit : units) {
			if (unit.row != null) {
				if (unit.selectionScale > 0) {
					final float selectionSize = unit.selectionScale * this.selectionCircleScaleFactor;
					String path = null;
					for (int i = 0; i < this.selectionCircleSizes.size(); i++) {
						final SelectionCircleSize selectionCircleSize = this.selectionCircleSizes.get(i);
						if ((selectionSize < selectionCircleSize.size)
								|| (i == (this.selectionCircleSizes.size() - 1))) {
							path = selectionCircleSize.texture;
							break;
						}
					}
					if (!path.toLowerCase().endsWith(".blp")) {
						path += ".blp";
					}
					if (!splats.containsKey(path)) {
						splats.put(path, new Splat());
					}
					final float x = unit.location[0];
					final float y = unit.location[1];
					System.out.println("Selecting a unit at " + x + "," + y);
					final float z = unit.row.getFieldAsFloat(UNIT_SELECT_HEIGHT, 0);
					splats.get(path).locations.add(new float[] { x - (selectionSize / 2), y - (selectionSize / 2),
							x + (selectionSize / 2), y + (selectionSize / 2), z + 5 });
					splats.get(path).unitMapping.add(new Consumer<SplatModel.SplatMover>() {
						@Override
						public void accept(final SplatMover t) {
							unit.selectionCircle = t;
						}
					});
				}
				this.selected.add(unit);
			}
		}
		this.selModels.clear();
		for (final Map.Entry<String, Terrain.Splat> entry : splats.entrySet()) {
			final String path = entry.getKey();
			final Splat locations = entry.getValue();
			final SplatModel model = new SplatModel(Gdx.gl30, (Texture) load(path, PathSolver.DEFAULT, null),
					locations.locations, this.terrain.centerOffset, locations.unitMapping, true, false);
			model.color[0] = 0;
			model.color[1] = 1;
			model.color[2] = 0;
			model.color[3] = 1;
			this.selModels.add(model);
			this.terrain.addSplatBatchModel("selection", model);
		}
	}

	public void getClickLocation(final Vector3 out, final int screenX, final int screenY) {
		final float[] ray = rayHeap;
		mousePosHeap.set(screenX, screenY);
		this.worldScene.camera.screenToWorldRay(ray, mousePosHeap);
		gdxRayHeap.set(ray[0], ray[1], ray[2], ray[3] - ray[0], ray[4] - ray[1], ray[5] - ray[2]);
		gdxRayHeap.direction.nor();// needed for libgdx
		RenderMathUtils.intersectRayTriangles(gdxRayHeap, this.terrain.softwareGroundMesh.vertices,
				this.terrain.softwareGroundMesh.indices, 3, out);
		rectangleHeap.set(Math.min(out.x, gdxRayHeap.origin.x), Math.min(out.y, gdxRayHeap.origin.y),
				Math.abs(out.x - gdxRayHeap.origin.x), Math.abs(out.y - gdxRayHeap.origin.y));
		this.walkableObjectsTree.intersect(rectangleHeap, this.walkablesIntersectionFinder.reset(gdxRayHeap));
		if (this.walkablesIntersectionFinder.found) {
			out.set(this.walkablesIntersectionFinder.intersection);
		}
		else {
			out.z = Math.max(getWalkableRenderHeight(out.x, out.y), this.terrain.getGroundHeight(out.x, out.y));
		}
	}

	public void showConfirmation(final Vector3 position, final float red, final float green, final float blue) {
		this.confirmationInstance.show();
		this.confirmationInstance.setSequence(0);
		this.confirmationInstance.setLocation(position);
		this.worldScene.instanceMoved(this.confirmationInstance, position.x, position.y);
		this.confirmationInstance.vertexColor[0] = red;
		this.confirmationInstance.vertexColor[1] = green;
		this.confirmationInstance.vertexColor[2] = blue;
	}

	public List<RenderUnit> selectUnit(final float x, final float y, final boolean toggle) {
		System.out.println("world: " + x + "," + y);
		final RenderUnit entity = rayPickUnit(x, y, CUnitFilterFunction.ACCEPT_ALL_LIVING);
		List<RenderUnit> sel;
		if (entity != null) {
			if (toggle) {
				sel = new ArrayList<>(this.selected);
				final int idx = sel.indexOf(entity);
				if (idx >= 0) {
					sel.remove(idx);
				}
				else {
					sel.add(entity);
				}
			}
			else {
				sel = Arrays.asList(entity);
			}
		}
		else {
			sel = Collections.emptyList();
		}
		this.doSelectUnit(sel);
		return sel;
	}

	public RenderUnit rayPickUnit(final float x, final float y) {
		return rayPickUnit(x, y, CUnitFilterFunction.ACCEPT_ALL);
	}

	public RenderUnit rayPickUnit(final float x, final float y, final CUnitFilterFunction filter) {
		final float[] ray = rayHeap;
		mousePosHeap.set(x, y);
		this.worldScene.camera.screenToWorldRay(ray, mousePosHeap);
		gdxRayHeap.set(ray[0], ray[1], ray[2], ray[3] - ray[0], ray[4] - ray[1], ray[5] - ray[2]);
		gdxRayHeap.direction.nor();// needed for libgdx

		RenderUnit entity = null;
		for (final RenderUnit unit : this.units) {
			final MdxComplexInstance instance = unit.instance;
			if (instance.shown() && instance.isVisible(this.worldScene.camera) && instance.intersectRayWithCollision(
					gdxRayHeap, intersectionHeap, unit.getSimulationUnit().getUnitType().isBuilding(), false)) {
				if (filter.call(unit.getSimulationUnit()) && (intersectionHeap.z > this.terrain
						.getGroundHeight(intersectionHeap.x, intersectionHeap.y))) {
					if ((entity == null) || (entity.instance.depth > instance.depth)) {
						entity = unit;
					}
				}
			}
		}
		return entity;
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
	private float selectionCircleScaleFactor;
	private DataTable worldEditData;
	private WorldEditStrings worldEditStrings;
	private Warcraft3MapObjectData allObjectData;
	private AbilityDataUI abilityDataUI;
	private Map<String, UnitSoundset> soundsetNameToSoundset;

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

	public void standOnRepeat(final MdxComplexInstance instance) {
		instance.setSequenceLoopMode(SequenceLoopMode.ALWAYS_LOOP);
		SequenceUtils.randomStandSequence(instance);
	}

	private static final class SelectionCircleSize {
		private final float size;
		private final String texture;
		private final String textureDotted;

		public SelectionCircleSize(final float size, final String texture, final String textureDotted) {
			this.size = size;
			this.texture = texture;
			this.textureDotted = textureDotted;
		}
	}

	public void setDayNightModels(final String terrainDNCFile, final String unitDNCFile) {
		final MdxModel terrainDNCModel = (MdxModel) load(mdx(terrainDNCFile), PathSolver.DEFAULT, null);
		this.dncTerrain = (MdxComplexInstance) terrainDNCModel.addInstance();
		this.dncTerrain.setSequenceLoopMode(SequenceLoopMode.ALWAYS_LOOP);
		this.dncTerrain.setSequence(0);
		final MdxModel unitDNCModel = (MdxModel) load(mdx(unitDNCFile), PathSolver.DEFAULT, null);
		this.dncUnit = (MdxComplexInstance) unitDNCModel.addInstance();
		this.dncUnit.setSequenceLoopMode(SequenceLoopMode.ALWAYS_LOOP);
		this.dncUnit.setSequence(0);
		this.dncUnitDay = (MdxComplexInstance) unitDNCModel.addInstance();
		this.dncUnitDay.setSequenceLoopMode(SequenceLoopMode.ALWAYS_LOOP);
		this.dncUnitDay.setSequence(0);
		final MdxModel targetDNCModel = (MdxModel) load(
				mdx("Environment\\DNC\\DNCLordaeron\\DNCLordaeronTarget\\DNCLordaeronTarget.mdl"), PathSolver.DEFAULT,
				null);
		this.dncTarget = (MdxComplexInstance) targetDNCModel.addInstance();
		this.dncTarget.setSequenceLoopMode(SequenceLoopMode.ALWAYS_LOOP);
		this.dncTarget.setSequence(0);
	}

	public static String mdx(String mdxPath) {
		if (mdxPath.toLowerCase().endsWith(".mdl")) {
			mdxPath = mdxPath.substring(0, mdxPath.length() - 4);
		}
		if (!mdxPath.toLowerCase().endsWith(".mdx")) {
			mdxPath += ".mdx";
		}
		return mdxPath;
	}

	@Override
	public SceneLightManager createLightManager(final boolean simple) {
		if (simple) {
			return new W3xScenePortraitLightManager(this, this.lightDirection);
		}
		else {
			return new W3xSceneWorldLightManager(this);
		}
	}

	@Override
	public WorldEditStrings getWorldEditStrings() {
		return this.worldEditStrings;
	}

	public void setGameUI(final GameUI gameUI) {
		this.gameUI = gameUI;
		this.abilityDataUI = new AbilityDataUI(this.allObjectData.getAbilities(), this.allObjectData.getUnits(),
				this.allObjectData.getUpgrades(), gameUI);
	}

	public GameUI getGameUI() {
		return this.gameUI;
	}

	public AbilityDataUI getAbilityDataUI() {
		return this.abilityDataUI;
	}

	public KeyedSounds getUiSounds() {
		return this.uiSounds;
	}

	public Warcraft3MapObjectData getAllObjectData() {
		return this.allObjectData;
	}

	public float getWalkableRenderHeight(final float x, final float y) {
		this.walkableObjectsTree.intersect(x, y, this.walkablesIntersector.reset(x, y));
		return this.walkablesIntersector.z;
	}

	public MdxComplexInstance getHighestWalkableUnder(final float x, final float y) {
		this.walkableObjectsTree.intersect(x, y, this.intersectorFindsHighestWalkable.reset(x, y));
		return this.intersectorFindsHighestWalkable.highestInstance;
	}

	public int getLocalPlayerIndex() {
		return this.localPlayerIndex;
	}

	public RenderUnit getRenderPeer(final CUnit unit) {
		return this.unitToRenderPeer.get(unit);
	}

	private static final class QuadtreeIntersectorFindsWalkableRenderHeight
			implements QuadtreeIntersector<MdxComplexInstance> {
		private float z;
		private final Ray ray = new Ray();
		private final Vector3 intersection = new Vector3();

		private QuadtreeIntersectorFindsWalkableRenderHeight reset(final float x, final float y) {
			this.z = -Float.MAX_VALUE;
			this.ray.set(x, y, 4096, 0, 0, -8192);
			return this;
		}

		@Override
		public boolean onIntersect(final MdxComplexInstance intersectingObject) {
			if (intersectingObject.intersectRayWithCollision(this.ray, this.intersection, true, true)) {
				this.z = Math.max(this.z, this.intersection.z);
			}
			return false;
		}
	}

	private static final class QuadtreeIntersectorFindsHighestWalkable
			implements QuadtreeIntersector<MdxComplexInstance> {
		private float z;
		private final Ray ray = new Ray();
		private final Vector3 intersection = new Vector3();
		private MdxComplexInstance highestInstance;

		private QuadtreeIntersectorFindsHighestWalkable reset(final float x, final float y) {
			this.z = -Float.MAX_VALUE;
			this.ray.set(x, y, 4096, 0, 0, -8192);
			this.highestInstance = null;
			return this;
		}

		@Override
		public boolean onIntersect(final MdxComplexInstance intersectingObject) {
			if (intersectingObject.intersectRayWithCollision(this.ray, this.intersection, true, true)) {
				if (this.intersection.z > this.z) {
					this.z = this.intersection.z;
					this.highestInstance = intersectingObject;
				}
			}
			return false;
		}
	}

	private static final class QuadtreeIntersectorFindsHitPoint implements QuadtreeIntersector<MdxComplexInstance> {
		private Ray ray;
		private final Vector3 intersection = new Vector3();
		private boolean found;

		private QuadtreeIntersectorFindsHitPoint reset(final Ray ray) {
			this.ray = ray;
			this.found = false;
			return this;
		}

		@Override
		public boolean onIntersect(final MdxComplexInstance intersectingObject) {
			if (intersectingObject.intersectRayWithCollision(this.ray, this.intersection, true, true)) {
				this.found = true;
				return true;
			}
			return false;
		}
	}
}
