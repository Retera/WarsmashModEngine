package com.etheller.warsmash.viewer5.handlers.w3x;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.channels.SeekableByteChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Consumer;

import javax.imageio.ImageIO;

import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.compress.utils.SeekableInMemoryByteChannel;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Plane;
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
import com.etheller.warsmash.networking.GameTurnManager;
import com.etheller.warsmash.parsers.fdf.GameUI;
import com.etheller.warsmash.parsers.w3x.War3Map;
import com.etheller.warsmash.parsers.w3x.doo.War3MapDoo;
import com.etheller.warsmash.parsers.w3x.objectdata.Warcraft3MapObjectData;
import com.etheller.warsmash.parsers.w3x.objectdata.Warcraft3MapRuntimeObjectData;
import com.etheller.warsmash.parsers.w3x.unitsdoo.War3MapUnitsDoo;
import com.etheller.warsmash.parsers.w3x.w3e.War3MapW3e;
import com.etheller.warsmash.parsers.w3x.w3i.War3MapW3i;
import com.etheller.warsmash.parsers.w3x.w3i.War3MapW3iFlags;
import com.etheller.warsmash.parsers.w3x.wpm.War3MapWpm;
import com.etheller.warsmash.units.DataTable;
import com.etheller.warsmash.units.Element;
import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.units.ObjectData;
import com.etheller.warsmash.units.StandardObjectData;
import com.etheller.warsmash.units.custom.WTS;
import com.etheller.warsmash.units.manager.MutableObjectData.WorldEditorDataType;
import com.etheller.warsmash.util.MappedData;
import com.etheller.warsmash.util.Quadtree;
import com.etheller.warsmash.util.QuadtreeIntersector;
import com.etheller.warsmash.util.RenderMathUtils;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.util.WorldEditStrings;
import com.etheller.warsmash.viewer5.Bounds;
import com.etheller.warsmash.viewer5.CanvasProvider;
import com.etheller.warsmash.viewer5.FogSettings;
import com.etheller.warsmash.viewer5.GenericResource;
import com.etheller.warsmash.viewer5.Grid;
import com.etheller.warsmash.viewer5.ModelInstance;
import com.etheller.warsmash.viewer5.ModelViewer;
import com.etheller.warsmash.viewer5.PathSolver;
import com.etheller.warsmash.viewer5.Scene;
import com.etheller.warsmash.viewer5.SceneLightManager;
import com.etheller.warsmash.viewer5.Texture;
import com.etheller.warsmash.viewer5.WorldScene;
import com.etheller.warsmash.viewer5.gl.WebGL;
import com.etheller.warsmash.viewer5.handlers.AbstractMdxModelViewer;
import com.etheller.warsmash.viewer5.handlers.mdx.Attachment;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxComplexInstance;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxHandler;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxHandler.ShaderEnvironmentType;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxModel;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxNode;
import com.etheller.warsmash.viewer5.handlers.mdx.SequenceLoopMode;
import com.etheller.warsmash.viewer5.handlers.tga.TgaFile;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.SecondaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.SplatModel.SplatMover;
import com.etheller.warsmash.viewer5.handlers.w3x.environment.BuildingShadow;
import com.etheller.warsmash.viewer5.handlers.w3x.environment.GroundTexture;
import com.etheller.warsmash.viewer5.handlers.w3x.environment.PathingGrid;
import com.etheller.warsmash.viewer5.handlers.w3x.environment.PathingGrid.PathingType;
import com.etheller.warsmash.viewer5.handlers.w3x.environment.PathingGrid.RemovablePathingMapInstance;
import com.etheller.warsmash.viewer5.handlers.w3x.environment.RenderCorner;
import com.etheller.warsmash.viewer5.handlers.w3x.environment.Terrain;
import com.etheller.warsmash.viewer5.handlers.w3x.environment.Terrain.Splat;
import com.etheller.warsmash.viewer5.handlers.w3x.lightning.LightningEffectModel;
import com.etheller.warsmash.viewer5.handlers.w3x.lightning.LightningEffectModelHandler;
import com.etheller.warsmash.viewer5.handlers.w3x.lightning.LightningEffectNode;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.RenderAttackInstant;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.RenderDestructable;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.RenderDoodad;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.RenderEffect;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.RenderItem;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.RenderItemType;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.RenderItemTypeData;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.RenderLightningEffect;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.RenderProjectile;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.RenderShadowType;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.RenderSpellEffect;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.RenderUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.RenderUnitType;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.RenderUnitTypeData;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.RenderWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.ability.AbilityDataUI;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.ability.AbilityUI;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.ability.BuffUI;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.ability.EffectAttachmentUI;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.ability.EffectAttachmentUIMissile;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CDestructable;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CItem;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitClassification;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidgetFilterFunction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttackInstant;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttackListener;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttackMissile;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.projectile.CAbilityCollisionProjectileListener;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.projectile.CAbilityProjectile;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.projectile.CAbilityProjectileListener;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.projectile.CAttackProjectile;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.projectile.CAttackProjectileMissile;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.projectile.CCollisionProjectile;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.projectile.CJassProjectile;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.projectile.CPsuedoProjectile;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.config.War3MapConfig;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CAllianceType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.vision.CPlayerFogOfWar;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.timers.CTimer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CEffectType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.SimulationRenderComponent;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.SimulationRenderComponentLightning;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.SimulationRenderComponentLightningMovable;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.SimulationRenderComponentModel;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.SimulationRenderController;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.TextTagConfigType;
import com.etheller.warsmash.viewer5.handlers.w3x.ui.command.SettableCommandErrorListener;
import com.etheller.warsmash.viewer5.handlers.w3x.ui.sound.KeyedSounds;

import mpq.MPQArchive;
import mpq.MPQException;

public class War3MapViewer extends AbstractMdxModelViewer implements MdxAssetLoader {
	private static final List<String> ORIGIN_STRING_LIST = Arrays.asList("origin");

	public static int DEBUG_DEPTH = 9999;

	private static final float DEFAULT_TEXT_TAG_HEIGHT = 0.024f;
	private static final int BUILTIN_TEXT_TAG_REPORTED_HANDLE_ID = -1;
	private static final War3ID ABILITY_HERO_RAWCODE = War3ID.fromString("AHer");
	private static final War3ID ABILITY_REVIVE_RAWCODE = War3ID.fromString("Arev");
	private static final Color PLACEHOLDER_LUMBER_COLOR = new Color(0.0f, 200f / 255f, 80f / 255f, 1.0f);
	private static final Color PLACEHOLDER_GOLD_COLOR = new Color(1.0f, 220f / 255f, 0f, 1.0f);
	private static final String DESTRUCTABLE_PATHING = "pathTex"; // replaced from 'bptx'
	private static final String DESTRUCTABLE_PATHING_DEATH = "pathTexDeath"; // replaced from 'bptd'
	private static final War3ID sloc = War3ID.fromString("sloc");
	private static final LoadGenericCallback stringDataCallback = new StringDataCallbackImplementation();
	private static final float[] rayHeap = new float[6];
	public static final Ray gdxRayHeap = new Ray();
	public static final Plane planeHeap = new Plane();
	private static final Vector2 mousePosHeap = new Vector2();
	private static final Vector3 normalHeap = new Vector3();
	public static final Vector3 intersectionHeap = new Vector3();
	public static final Vector3 intersectionHeap2 = new Vector3();
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
	public List<RenderDoodad> decals = new ArrayList<>();
	public List<TerrainDoodad> terrainDoodads = new ArrayList<>();
	public boolean doodadsReady;
	public boolean unitsAndItemsLoaded;
	public MappedData unitsData = new MappedData();
	public MappedData unitMetaData = new MappedData();
	public List<RenderWidget> widgets = new ArrayList<>();
	public List<RenderUnit> units = new ArrayList<>();
	public List<RenderEffect> projectiles = new ArrayList<>();
	public boolean unitsReady;
	public War3Map mapMpq;

	private final DataSource gameDataSource;

	public Terrain terrain;
	public int renderPathing = 0;
	public int renderLighting = 1;

	private final Set<String> selectedSplatModelKeys = new HashSet<>();
	public List<RenderWidget> selected = new ArrayList<>();
	private final Set<String> mouseHighlightSplatModelKeys = new HashSet<>();
	private final List<RenderWidget> mouseHighlightWidgets = new ArrayList<>();
	private DataTable unitAckSoundsTable;
	private DataTable unitCombatSoundsTable;
	public DataTable miscData;
	private Element misc;
	private final Map<String, TextTagConfig> keyToTextTagConfig = new HashMap<>();
	private DataTable unitGlobalStrings;
	public DataTable uiSoundsTable;
	private DataTable lightningDataTable;
	private Map<War3ID, LightningEffectModel> lightningTypeToModel;
	private MdxComplexInstance confirmationInstance;
	public MdxComplexInstance dncUnit;
	public MdxComplexInstance dncTerrain;
	public MdxComplexInstance dncTarget;
	public CSimulation simulation;
	private float updateTime = 0;

	// for World Editor, I think
	public Vector2[] startLocations = new Vector2[WarsmashConstants.MAX_PLAYERS];

	private final DynamicShadowManager dynamicShadowManager = new DynamicShadowManager();

	private final Random seededRandom = new Random(1337L);

	private final Map<String, BufferedImage> filePathToPathingMap = new HashMap<>();

	private final List<SelectionCircleSize> selectionCircleSizes = new ArrayList<>();

	private final Map<CUnit, RenderUnit> unitToRenderPeer = new HashMap<>();
	private final Map<CDestructable, RenderDestructable> destructableToRenderPeer = new HashMap<>();
	private final Map<CItem, RenderItem> itemToRenderPeer = new HashMap<>();
	private GameUI gameUI;
	private Vector3 lightDirection;

	private Quadtree<MdxComplexInstance> walkableObjectsTree;
	private final QuadtreeIntersectorFindsWalkableRenderHeight walkablesIntersector = new QuadtreeIntersectorFindsWalkableRenderHeight();
	private final QuadtreeIntersectorFindsHitPoint walkablesIntersectionFinder = new QuadtreeIntersectorFindsHitPoint();
	private final QuadtreeIntersectorFindsHighestWalkable intersectorFindsHighestWalkable = new QuadtreeIntersectorFindsHighestWalkable();

	private KeyedSounds uiSounds;
	private int localPlayerIndex;
	private final SettableCommandErrorListener commandErrorListener;

	public final List<TextTag> textTags = new ArrayList<>();

	private final War3MapConfig mapConfig;

	private GameTurnManager gameTurnManager;

	private War3MapW3i lastLoadedMapInformation;

	private FogSettings defaultFogSettings;

	private int successfulFrames = 0;

	public War3MapViewer(final DataSource dataSource, final CanvasProvider canvas, final War3MapConfig mapConfig,
			final GameTurnManager gameTurnManager) {
		super(dataSource, canvas);
		this.gameTurnManager = gameTurnManager;
		MdxHandler.CURRENT_SHADER_TYPE = ShaderEnvironmentType.GAME;
		this.gameDataSource = dataSource;

		final WebGL webGL = this.webGL;

		addHandler(new MdxHandler());

		this.wc3PathSolver = PathSolver.DEFAULT;

		this.worldScene = addWorldScene();

		if (!this.dynamicShadowManager.setup(webGL)) {
			throw new IllegalStateException("FrameBuffer setup failed");
		}

		this.commandErrorListener = new SettableCommandErrorListener();
		this.mapConfig = mapConfig;
	}

	public void loadSLKs(final WorldEditStrings worldEditStrings) throws IOException {
		final GenericResource terrain = loadMapGeneric("TerrainArt\\Terrain.slk", FetchDataTypeName.SLK,
				stringDataCallback);
		final GenericResource cliffTypes = loadMapGeneric("TerrainArt\\CliffTypes.slk", FetchDataTypeName.SLK,
				stringDataCallback);
		final GenericResource water = loadMapGeneric("TerrainArt\\Water.slk", FetchDataTypeName.SLK,
				stringDataCallback);

		// == when loaded, which is always in our system ==
		this.terrainData.load(terrain.data.toString());
		this.cliffTypesData.load(cliffTypes.data.toString());
		this.waterData.load(water.data.toString());
		// emit terrain loaded??

		final GenericResource doodads = loadMapGeneric("Doodads\\Doodads.slk", FetchDataTypeName.SLK,
				stringDataCallback);
		final GenericResource doodadMetaData = loadMapGeneric("Doodads\\DoodadMetaData.slk", FetchDataTypeName.SLK,
				stringDataCallback);
		final GenericResource destructableData = loadMapGeneric("Units\\DestructableData.slk", FetchDataTypeName.SLK,
				stringDataCallback);
		final GenericResource destructableMetaData = loadMapGeneric("Units\\DestructableMetaData.slk",
				FetchDataTypeName.SLK, stringDataCallback);

		// == when loaded, which is always in our system ==
		this.doodadsAndDestructiblesLoaded = true;
		this.doodadsData.load(doodads.data.toString());
		this.doodadMetaData.load(doodadMetaData.data.toString());
		this.doodadsData.load(destructableData.data.toString());
		this.destructableMetaData.load(destructableData.data.toString());
		// emit doodads loaded

		final GenericResource unitData = loadMapGeneric("Units\\UnitData.slk", FetchDataTypeName.SLK,
				stringDataCallback);
		final GenericResource unitUi = loadMapGeneric("Units\\unitUI.slk", FetchDataTypeName.SLK, stringDataCallback);
		final GenericResource itemData = loadMapGeneric("Units\\ItemData.slk", FetchDataTypeName.SLK,
				stringDataCallback);
		final GenericResource unitMetaData = loadMapGeneric("Units\\UnitMetaData.slk", FetchDataTypeName.SLK,
				stringDataCallback);

		loadLightningData(worldEditStrings);

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
		this.misc = this.miscData.get("Misc");
		// TODO Find the upkeep constants inside the assets files ?????
		if (!this.misc.hasField("UpkeepUsage")) {
			this.misc.setField("UpkeepUsage", "50,80,10000,10000,10000,10000,10000,10000,10000,10000");
		}
		if (!this.misc.hasField("UpkeepGoldTax")) {
			this.misc.setField("UpkeepGoldTax", "0.00,0.30,0.60,0.60,0.60,0.60,0.60,0.60,0.60,0.60");
		}
		if (!this.misc.hasField("UpkeepLumberTax")) {
			this.misc.setField("UpkeepLumberTax", "0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00");
		}
		final Element zFogElement = this.miscData.get("DefaultZFog");
		if (zFogElement != null) {
			this.defaultFogSettings = FogSettings.parse(zFogElement, WarsmashConstants.GAME_VERSION);
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
		this.imageWalkableZOffset = selectionCircleData.getFieldValue("ImageWalkableZOffset");
		this.selectionCircleColorFriend = parseColor(selectionCircleData, "ColorFriend");
		this.selectionCircleColorNeutral = parseColor(selectionCircleData, "ColorNeutral");
		this.selectionCircleColorEnemy = parseColor(selectionCircleData, "ColorEnemy");

		this.uiSoundsTable = new DataTable(worldEditStrings);
		try (InputStream miscDataTxtStream = this.dataSource.getResourceAsStream("UI\\SoundInfo\\UISounds.slk")) {
			this.uiSoundsTable.readSLK(miscDataTxtStream);
		}
		try (InputStream miscDataTxtStream = this.dataSource.getResourceAsStream("UI\\SoundInfo\\AmbienceSounds.slk")) {
			this.uiSoundsTable.readSLK(miscDataTxtStream);
		}
		try (InputStream miscDataTxtStream = this.dataSource.getResourceAsStream("UI\\SoundInfo\\AbilitySounds.slk")) {
			this.uiSoundsTable.readSLK(miscDataTxtStream);
		}
	}

	private TextTagConfig getTextTagConfig(final String key) {
		TextTagConfig textTagConfig = this.keyToTextTagConfig.get(key);
		if (textTagConfig == null) {
			textTagConfig = parseTextTagConfig(this.misc, key);
			this.keyToTextTagConfig.put(key, textTagConfig);
		}
		return textTagConfig;
	}

	private static TextTagConfig parseTextTagConfig(final Element misc, final String name) {
		final Color color = parseColor(misc, name + "TextColor");
		final String velocityKey = name + "TextVelocity";
		final float[] velocity = { misc.getFieldFloatValue(velocityKey, 0), misc.getFieldFloatValue(velocityKey, 1),
				misc.getFieldFloatValue(velocityKey, 2) };
		final float lifetime = misc.getFieldFloatValue(name + "TextLifetime");
		final float fadeStart = misc.getFieldFloatValue(name + "TextFadeStart");
		final float height = misc.getFieldFloatValue(name + "TextHeight");
		return new TextTagConfig(color, velocity, lifetime, fadeStart, height);
	}

	private void loadLightningData(final WorldEditStrings worldEditStrings) throws IOException {
		this.lightningDataTable = new DataTable(worldEditStrings);
		try (InputStream slkStream = this.dataSource.getResourceAsStream("Splats\\LightningData.slk")) {
			this.lightningDataTable.readSLK(slkStream);
		}
		this.lightningTypeToModel = new HashMap<>();
		final LightningEffectModelHandler lightningEffectModelHandler = new LightningEffectModelHandler();
		lightningEffectModelHandler.load(this);
		for (final String key : this.lightningDataTable.keySet()) {
			final War3ID typeId = War3ID.fromString(key);
			final Element element = this.lightningDataTable.get(key);
			final String textureFilePath = element.getField("Dir") + "\\" + element.getField("file");
			final float avgSegLen = element.getFieldFloatValue("AvgSegLen");
			final float width = element.getFieldFloatValue("Width");
			final float r = element.getFieldFloatValue("R");
			final float g = element.getFieldFloatValue("G");
			final float b = element.getFieldFloatValue("B");
			final float a = element.getFieldFloatValue("A");
			final float noiseScale = element.getFieldFloatValue("NoiseScale");
			final float texCoordScale = element.getFieldFloatValue("TexCoordScale");
			final float duration = element.getFieldFloatValue("Duration");
			final int version = element.getFieldValue("version");
			final LightningEffectModel lightningEffectModel = new LightningEffectModel(lightningEffectModelHandler,
					this, ".lightning", this.mapPathSolver, "<lightning:" + key + ">", typeId, textureFilePath,
					avgSegLen, width, new float[] { r / 255f, g / 255f, b / 255f, a / 255f }, noiseScale, texCoordScale,
					duration, version);
			lightningEffectModel.loadData(null, null);
			this.lightningTypeToModel.put(typeId, lightningEffectModel);
		}
	}

	private static Color parseColor(final Element selectionCircleData, final String field) {
		return new Color(selectionCircleData.getFieldFloatValue(field, 1) / 255f,
				selectionCircleData.getFieldFloatValue(field, 2) / 255f,
				selectionCircleData.getFieldFloatValue(field, 3) / 255f,
				selectionCircleData.getFieldFloatValue(field, 0) / 255f);
	}

	public GenericResource loadMapGeneric(final String path, final FetchDataTypeName dataType,
			final LoadGenericCallback callback) {
		if (this.mapMpq == null) {
			return this.loadGeneric(path, dataType, callback);
		}
		else {
			return this.loadGeneric(path, dataType, callback, this.dataSource);
		}
	}

	public static War3Map beginLoadingMap(final DataSource gameDataSource, final String mapFilePath)
			throws IOException {
		if (mapFilePath.startsWith("/") || !gameDataSource.has(mapFilePath)) {
			final File mapFile = new File(mapFilePath);
			if (mapFile.exists()) {
				return new War3Map(gameDataSource, mapFile);
			}
			else {
				throw new IllegalArgumentException("No such map file: " + mapFilePath);
			}
		}
		return new War3Map(gameDataSource, mapFilePath);
	}

	public DataTable loadWorldEditData(final War3Map map) {
		final StandardObjectData standardObjectData = new StandardObjectData(map);
		this.worldEditData = standardObjectData.getWorldEditData();
		return this.worldEditData;
	}

	public WTS preloadWTS(final War3Map map) {
		try {
			this.preloadedWTS = Warcraft3MapObjectData.loadWTS(map);
			return this.preloadedWTS;
		}
		catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	public MapLoader createMapLoader(final War3Map war3Map, final War3MapW3i w3iFile, final int localPlayerIndex)
			throws IOException {
		this.localPlayerIndex = localPlayerIndex;
		this.mapMpq = war3Map;
		this.lastLoadedMapInformation = w3iFile;

		return new MapLoader(war3Map, w3iFile, localPlayerIndex);
	}

	public SimulationRenderComponentLightning createLightning(final War3ID lightningId,
			final RenderUnit renderPeerSource, final RenderWidget renderPeerTarget) {
		return this.createLightning(lightningId, renderPeerSource, renderPeerTarget, null);
	}

	public SimulationRenderComponentLightning createLightning(final War3ID lightningId,
			final RenderUnit renderPeerSource, final RenderWidget renderPeerTarget, final Float duration) {
		final LightningEffectModel lightningEffectModel = this.lightningTypeToModel.get(lightningId);
		if (lightningEffectModel != null) {
			final LightningEffectNode source = (LightningEffectNode) lightningEffectModel.addInstance();
			final LightningEffectNode target = (LightningEffectNode) lightningEffectModel.addInstance();
			// ----- NOTE -----
			// Automatic ingame lightnings apply their builtin durations below. For user
			// code, we DONT do the same
			// on the other arbitrary (non-unit-bound) createLightning func.
			// Later on we might want a user API for creating unit-attached lightnings that
			// have a duration specified
			// by user code
			if (duration != null) {
				if (duration >= 0) {
					source.setLifeSpanRemaining(duration);
					target.setLifeSpanRemaining(duration);
				}
			}
			else {
				source.setLifeSpanRemaining(lightningEffectModel.getDuration());
				target.setLifeSpanRemaining(lightningEffectModel.getDuration());
			}
			source.setFriend(target);
			target.setFriend(source);
			source.setSource(true);
			source.setParent(renderPeerSource.getInstance());
			source.setLocation(0, 0, this.simulation.getUnitData()
					.getProjectileLaunchZ(renderPeerSource.getSimulationUnit().getTypeId()));
			target.setParent(renderPeerTarget.getInstance());
			target.setLocation(0, 0, renderPeerTarget.getSimulationWidget().getImpactZ());
			source.setScene(this.worldScene);
			target.setScene(this.worldScene);
			return new RenderLightningEffect(source, target, this);
		}
		return SimulationRenderComponentLightning.DO_NOTHING;
	}

	public SimulationRenderComponentLightningMovable createLightning(final War3ID lightningId, final float x1,
			final float y1, final float z1, final float x2, final float y2, final float z2) {
		final LightningEffectModel lightningEffectModel = this.lightningTypeToModel.get(lightningId);
		if (lightningEffectModel != null) {
			final LightningEffectNode source = (LightningEffectNode) lightningEffectModel.addInstance();
			final LightningEffectNode target = (LightningEffectNode) lightningEffectModel.addInstance();
			source.setFriend(target);
			target.setFriend(source);
			source.setSource(true);
			source.setLocation(x1, y1, z1);
			target.setLocation(x2, y2, z2);
			source.setScene(this.worldScene);
			target.setScene(this.worldScene);
			return new RenderLightningEffect(source, target, this);
		}
		return SimulationRenderComponentLightningMovable.DO_NOTHING;
	}

	public void spawnFxOnOrigin(final RenderUnit renderUnit, final String heroLevelUpArt) {
		final MdxModel heroLevelUpModel = loadModelMdx(heroLevelUpArt);
		if (heroLevelUpModel != null) {
			final MdxComplexInstance modelInstance = (MdxComplexInstance) heroLevelUpModel.addInstance();
			modelInstance.setTeamColor(renderUnit.playerIndex);

			final MdxModel model = (MdxModel) renderUnit.instance.model;
			int index = -1;
			for (int i = 0; i < model.attachments.size(); i++) {
				final Attachment attachment = model.attachments.get(i);
				if (attachment.getName().startsWith("origin ref")) {
					index = i;
					break;
				}
			}
			if ((index != -1) && false) {
				final MdxNode attachment = renderUnit.instance.getAttachment(index);
				modelInstance.setParent(attachment);
			}
			else {
				modelInstance.setLocation(renderUnit.location);
			}

			modelInstance.setScene(War3MapViewer.this.worldScene);
			SequenceUtils.randomBirthSequence(modelInstance);
			War3MapViewer.this.projectiles.add(new RenderAttackInstant(modelInstance, War3MapViewer.this,
					(float) Math.toRadians(renderUnit.getSimulationUnit().getFacing())));
		}
	}

	protected BufferedImage getDestructablePathingPixelMap(final GameObject row) {
		return loadPathingTexture(row.getFieldAsString(DESTRUCTABLE_PATHING, 0));
	}

	protected BufferedImage getDestructablePathingDeathPixelMap(final GameObject row) {
		return loadPathingTexture(row.getFieldAsString(DESTRUCTABLE_PATHING_DEATH, 0));
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
			loadUnitsAndItems(this.allObjectData, this.lastLoadedMapInformation);
		}
		else {
			throw new IllegalStateException("transcription of JS has not loaded a map and has no JS async promises");
		}

		// After we finish loading units, we need to update & create the stored shadow
		// information for all unit shadows
		this.terrain.initShadows();
		this.terrain.setFogOfWarData(this.simulation, this.simulation.getPlayer(this.localPlayerIndex).getFogOfWar());
		final CTimer fogUpdateTimer = new CTimer() {
			@Override
			public void onFire(final CSimulation simulation) {
			}
		};
		fogUpdateTimer.setTimeoutTime(1.0f);
		fogUpdateTimer.setRepeats(true);
		fogUpdateTimer.start(this.simulation);
		final CTimer fogGpuUpdateTimer = new CTimer() {
			@Override
			public void onFire(final CSimulation simulation) {
				War3MapViewer.this.terrain.reloadFogOfWarDataToGPU(simulation);
				for (final RenderDoodad doodad : War3MapViewer.this.decals) {
					doodad.updateFog(War3MapViewer.this);
				}
			}
		};
		fogGpuUpdateTimer.setTimeoutTime(0.03f);
		fogGpuUpdateTimer.setRepeats(true);
		fogGpuUpdateTimer.start(this.simulation);
	}

	private void loadDoodadsAndDestructibles(final Warcraft3MapRuntimeObjectData modifications,
			final War3MapW3i w3iFile) throws IOException {
		applyModificationFile(this.doodadsData, this.doodadMetaData, modifications.getDoodads(),
				WorldEditorDataType.DOODADS);
		applyModificationFile(this.doodadsData, this.destructableMetaData, modifications.getDestructibles(),
				WorldEditorDataType.DESTRUCTIBLES);

		final War3MapDoo doo = this.mapMpq.readDoodads(w3iFile);

		for (final com.etheller.warsmash.parsers.w3x.doo.Doodad doodad : doo.getDoodads()) {
			if ((doodad.getFlags() & 0x2) == 0) {
				continue;
			}
			final War3ID doodadId = doodad.getId();
			final int doodadVariation = doodad.getVariation();
			final float[] location = doodad.getLocation();
			final float facingRadians = doodad.getAngle();
			final short lifePercent = doodad.getLife();
			final float[] scale = doodad.getScale();
			createDestructableOrDoodad(doodadId, modifications, doodadVariation, location, facingRadians, lifePercent,
					scale);
		}

		// Cliff/Terrain doodads.
		for (final com.etheller.warsmash.parsers.w3x.doo.TerrainDoodad doodad : doo.getTerrainDoodads()) {
			final GameObject row = modifications.getDoodads().get(doodad.getId());
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
			final MdxModel model = (MdxModel) load(file, this.mapPathSolver, this.solverParams);

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
				final int minCellX = (int) doodad.getLocation()[0];
				final int minCellY = (int) doodad.getLocation()[1];
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

	private void createDoodad(final GameObject row, final int doodadVariation, final float[] location,
			final float facingRadians, final float[] scale) {
		final MdxModel model = getDoodadModel(doodadVariation, row);
		final float maxPitch = row.readSLKTagFloat("maxPitch");
		final float maxRoll = row.readSLKTagFloat("maxRoll");
		final float defScale = row.readSLKTagFloat("defScale");
		final RenderDoodad renderDoodad = new RenderDoodad(this, model, row, location, scale, facingRadians, maxPitch,
				maxRoll, defScale, doodadVariation);
		renderDoodad.instance.uniformScale(defScale);
		this.doodads.add(renderDoodad);
		this.decals.add(renderDoodad);
	}

	private RenderDestructable createNewDestructable(final War3ID doodadId, final GameObject row,
			final int doodadVariation, final float[] location, final float facingRadians, final short lifePercent,
			final float[] scale) {
		BuildingShadow destructableShadow = null;
		RemovablePathingMapInstance destructablePathing = null;
		RemovablePathingMapInstance destructablePathingDeath = null;
		final MdxModel model = getDoodadModel(doodadVariation, row);

		final String portraitModelPath = row.readSLKTag("portraitmodel");
		final MdxModel portraitModel = loadModelMdx(portraitModelPath);

		final float maxPitch = row.readSLKTagFloat("maxPitch");
		final float maxRoll = row.readSLKTagFloat("maxRoll");
		final String shadowString = row.readSLKTag("shadow");
		if ((shadowString != null) && (shadowString.length() > 0) && !"_".equals(shadowString)) {
			destructableShadow = this.terrain.addShadow(shadowString, location[0], location[1]);
		}

		final BufferedImage destructablePathingPixelMap = getDestructablePathingPixelMap(row);
		if (destructablePathingPixelMap != null) {
			destructablePathing = this.terrain.pathingGrid.createRemovablePathingOverlayTexture(location[0],
					location[1], (int) Math.toDegrees(facingRadians), destructablePathingPixelMap);
			if (lifePercent > 0) {
				destructablePathing.add();
			}
		}
		final BufferedImage destructablePathingDeathPixelMap = getDestructablePathingDeathPixelMap(row);
		if (destructablePathingDeathPixelMap != null) {
			destructablePathingDeath = this.terrain.pathingGrid.createRemovablePathingOverlayTexture(location[0],
					location[1], (int) Math.toDegrees(facingRadians), destructablePathingDeathPixelMap);
			if (lifePercent <= 0) {
				destructablePathingDeath.add();
			}
		}
		final float x = location[0];
		final float y = location[1];
		final CDestructable simulationDestructable = this.simulation.internalCreateDestructable(
				War3ID.fromString(row.getId()), x, y, destructablePathing, destructablePathingDeath);
		// Used to be this, but why: (float) Math.sqrt((scale[0]) * (scale[1]) *
		// (scale[2]));
		final float selectionScale = 1.0f;
		simulationDestructable.setLife(this.simulation, simulationDestructable.getLife() * (lifePercent / 100f));
		final RenderDestructable renderDestructable = new RenderDestructable(this, model, portraitModel, row, location,
				scale, facingRadians, selectionScale, maxPitch, maxRoll, lifePercent, destructableShadow,
				simulationDestructable, doodadVariation);
		if (row.readSLKTagBoolean("walkable")) {
			final float angle = facingRadians;
			BoundingBox boundingBox = model.bounds.getBoundingBox();
			if (boundingBox == null) {
				// TODO this is a hack and should be fixed later
				boundingBox = new BoundingBox(new Vector3(-10, -10, 0), new Vector3(10, 10, 0));
			}
			final Rectangle renderDestructableBounds = getRotatedBoundingBox(x, y, angle, boundingBox);
			this.walkableObjectsTree.add((MdxComplexInstance) renderDestructable.instance, renderDestructableBounds);
			renderDestructable.walkableBounds = renderDestructableBounds;
		}
		this.widgets.add(renderDestructable);
		this.decals.add(renderDestructable);
		this.destructableToRenderPeer.put(simulationDestructable, renderDestructable);
		return renderDestructable;
	}

	private void createDestructableOrDoodad(final War3ID doodadId, final Warcraft3MapRuntimeObjectData modifications,
			final int doodadVariation, final float[] location, final float facingRadians, final short lifePercent,
			final float[] scale) {
		GameObject row = modifications.getDoodads().get(doodadId);
		if (row == null) {
			row = modifications.getDestructibles().get(doodadId);
			if (row != null) {
				createNewDestructable(doodadId, row, doodadVariation, location, facingRadians, lifePercent, scale);
			}
		}
		else {
			createDoodad(row, doodadVariation, location, facingRadians, scale);
		}
	}

	private MdxModel getDoodadModel(final int doodadVariation, final GameObject row) {
		String file = row.readSLKTag("file");
		final int numVar = row.readSLKTagInt("numVar");

		if (file.endsWith(".mdx") || file.endsWith(".mdl")) {
			file = file.substring(0, file.length() - 4);
		}

		String fileVar = file;

		file += ".mdx";

		if (numVar > 1) {
			fileVar += Math.min(doodadVariation, numVar - 1);
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
			model = (MdxModel) load(path, this.mapPathSolver, this.solverParams);
		}
		else {
			model = (MdxModel) load(fileVar, this.mapPathSolver, this.solverParams);
		}
		return model;
	}

	private Rectangle getRotatedBoundingBox(final float x, final float y, final float angle,
			final BoundingBox boundingBox) {
		final float x1 = boundingBox.min.x;
		final float y1 = boundingBox.min.y;
		final float x2 = boundingBox.min.x + boundingBox.getWidth();
		final float y2 = boundingBox.min.y;
		final float x3 = boundingBox.min.x + boundingBox.getWidth();
		final float y3 = boundingBox.min.y + boundingBox.getHeight();
		final float x4 = boundingBox.min.x;
		final float y4 = boundingBox.min.y + boundingBox.getHeight();
		final float angle1 = (float) StrictMath.atan2(y1, x1) + angle;
		final float len1 = (float) StrictMath.sqrt((x1 * x1) + (y1 * y1));
		final float angle2 = (float) StrictMath.atan2(y2, x2) + angle;
		final float len2 = (float) StrictMath.sqrt((x2 * x2) + (y2 * y2));
		final float angle3 = (float) StrictMath.atan2(y3, x3) + angle;
		final float len3 = (float) StrictMath.sqrt((x3 * x3) + (y3 * y3));
		final float angle4 = (float) StrictMath.atan2(y4, x4) + angle;
		final float len4 = (float) StrictMath.sqrt((x4 * x4) + (y4 * y4));
		final double x1prime = StrictMath.cos(angle1) * len1;
		final double x2prime = StrictMath.cos(angle2) * len2;
		final double x3prime = StrictMath.cos(angle3) * len3;
		final double x4prime = StrictMath.cos(angle4) * len4;
		final double y1prime = StrictMath.sin(angle1) * len1;
		final double y2prime = StrictMath.sin(angle2) * len2;
		final double y3prime = StrictMath.sin(angle3) * len3;
		final double y4prime = StrictMath.sin(angle4) * len4;
		final float minX = (float) StrictMath.min(StrictMath.min(x1prime, x2prime), StrictMath.min(x3prime, x4prime));
		final float minY = (float) StrictMath.min(StrictMath.min(y1prime, y2prime), StrictMath.min(y3prime, y4prime));
		final float maxX = (float) StrictMath.max(StrictMath.max(x1prime, x2prime), StrictMath.max(x3prime, x4prime));
		final float maxY = (float) StrictMath.max(StrictMath.max(y1prime, y2prime), StrictMath.max(y3prime, y4prime));
		return new Rectangle(x + minX, y + minY, maxX - minX, maxY - minY);
	}

	private void applyModificationFile(final MappedData doodadsData2, final MappedData doodadMetaData2,
			final ObjectData destructibles, final WorldEditorDataType dataType) {
		// TODO condense ported MappedData from Ghostwolf and MutableObjectData from
		// Retera

	}

	private void loadUnitsAndItems(final Warcraft3MapRuntimeObjectData modifications, final War3MapW3i mapInformation)
			throws IOException {
		final War3Map mpq = this.mapMpq;
		this.unitsReady = false;

		this.renderUnitTypeData = new RenderUnitTypeData(modifications.getUnits(), mpq, this, this.unitAckSoundsTable,
				this.terrain.uberSplatTable);
		this.renderItemTypeData = new RenderItemTypeData(modifications.getItems(), mpq, this, this.miscData);

		if (this.dataSource.has("war3mapUnits.doo") && WarsmashConstants.LOAD_UNITS_FROM_WORLDEDIT_DATA) {
			final War3MapUnitsDoo dooFile = mpq.readUnits(mapInformation);

			// Collect the units and items data.
			for (final com.etheller.warsmash.parsers.w3x.unitsdoo.Unit unit : dooFile.getUnits()) {
				final War3ID unitId = unit.getId();
				final float unitX = unit.getLocation()[0];
				final float unitY = unit.getLocation()[1];
				final float unitZ = unit.getLocation()[2];
				final int playerIndex = unit.getPlayer();
				final int customTeamColor = unit.getCustomTeamColor();
				final float unitAngle = unit.getAngle();
				final int editorConfigHitPointPercent = unit.getHitpoints();

				final CWidget widgetCreated = createNewUnit(modifications, unitId, unitX, unitY, playerIndex,
						customTeamColor, unitAngle);
				if (widgetCreated instanceof CUnit) {
					final CUnit unitCreated = (CUnit) widgetCreated;
					if (editorConfigHitPointPercent > 0) {
						unitCreated.setLife(this.simulation,
								unitCreated.getMaximumLife() * (editorConfigHitPointPercent / 100f));
					}
					if (unit.getGoldAmount() != 0) {
						unitCreated.setGold(unit.getGoldAmount());
					}
				}
			}
		}
		this.simulation.unitsLoaded();

		this.terrain.loadSplats();

		this.unitsReady = true;
		this.anyReady = true;
	}

	private CWidget createNewUnit(final Warcraft3MapRuntimeObjectData modifications, final War3ID unitId, float unitX,
			float unitY, final int playerIndex, int customTeamColor, final float unitAngle) {
		Splat buildingUberSplat = null;
		SplatMover buildingUberSplatDynamicIngame = null;
		BufferedImage buildingPathingPixelMap = null;
		BuildingShadow buildingShadowInstance = null;

		// Hardcoded?
		if (sloc.equals(unitId)) {
			// path = "Objects\\StartLocation\\StartLocation.mdx";
			this.startLocations[playerIndex] = new Vector2(unitX, unitY);
		}
		else {
			final RenderUnitType renderUnitType = this.renderUnitTypeData.get(unitId);
			if (renderUnitType == null) {
				final RenderItemType renderItemType = this.renderItemTypeData.get(unitId);
				if (renderItemType != null) {

					final CItem simulationItem = this.simulation.internalCreateItem(unitId, unitX, unitY);
					final float unitZ = Math.max(getWalkableRenderHeight(unitX, unitY),
							War3MapViewer.this.terrain.getGroundHeight(unitX, unitY));
					final RenderItem renderItem = new RenderItem(this, renderItemType, unitX, unitY, unitZ, unitAngle,
							simulationItem);
					this.widgets.add(renderItem);
					this.itemToRenderPeer.put(simulationItem, renderItem);

					renderItem.shadow = createUnitShadow(renderItemType.getRenderShadowType(), unitX, unitY);
					return simulationItem;

				}
				else {
					System.err.println("Unknown unit ID: " + unitId);
				}
			}
			else {
				buildingPathingPixelMap = renderUnitType.getBuildingPathingPixelMap();

				final float angle = (float) Math.toDegrees(unitAngle);
				final CUnit simulationUnit = this.simulation.internalCreateUnit(unitId, playerIndex, unitX, unitY,
						angle, buildingPathingPixelMap);
				unitX = simulationUnit.getX();
				unitY = simulationUnit.getY();
				if (!renderUnitType.isAllowCustomTeamColor() || (customTeamColor == -1)) {
					if (renderUnitType.getTeamColor() != -1) {
						customTeamColor = renderUnitType.getTeamColor();
					}
					else {
						customTeamColor = this.simulation.getPlayer(playerIndex).getColor();
					}
				}
				final float unitZ = Math.max(getWalkableRenderHeight(unitX, unitY),
						War3MapViewer.this.terrain.getGroundHeight(unitX, unitY)) + simulationUnit.getFlyHeight();

				final String texturePath = renderUnitType.getUberSplat();
				final float s = renderUnitType.getUberSplatScaleValue();
				if (texturePath != null) {
					if (this.unitsReady) {
						buildingUberSplatDynamicIngame = addUberSplatIngame(unitX, unitY, texturePath, s);
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

				final String buildingShadow = renderUnitType.getBuildingShadow();
				if (buildingShadow != null) {
					buildingShadowInstance = this.terrain.addShadow(buildingShadow, unitX, unitY);
				}

				final RenderUnit renderUnit = new RenderUnit(this, unitX, unitY, unitZ, customTeamColor, simulationUnit,
						renderUnitType, buildingShadowInstance, this.selectionCircleScaleFactor,
						renderUnitType.getAnimationWalkSpeed(), renderUnitType.getAnimationRunSpeed(),
						renderUnitType.getScalingValue());
				this.unitToRenderPeer.put(simulationUnit, renderUnit);
				this.widgets.add(renderUnit);
				this.units.add(renderUnit);
				renderUnit.shadow = createUnitShadow(renderUnitType.getRenderShadowType(), unitX, unitY);
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
		}
		return null;
	}

	private SplatMover createUnitShadow(RenderShadowType renderShadowType, float unitX, float unitY) {
		if (renderShadowType != null) {
			final float x = unitX - renderShadowType.getX();
			final float y = unitY - renderShadowType.getY();
			return this.terrain.addUnitShadowSplat(renderShadowType.getTexture(), x, y, x + renderShadowType.getWidth(),
					y + renderShadowType.getHeight(), 3, 0.5f, false);
		}
		return null;
	}

	public SplatMover addUberSplatIngame(final float unitX, final float unitY, final String texturePath,
			final float s) {
		SplatMover buildingUberSplatDynamicIngame;
		buildingUberSplatDynamicIngame = this.terrain.addUberSplat(texturePath, unitX, unitY, 1, s, false, false, false,
				false);
		return buildingUberSplatDynamicIngame;
	}

	@Override
	public BufferedImage loadPathingTexture(final String pathingTexture) {
		BufferedImage buildingPathingPixelMap = null;
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
				catch (final Exception exc) {
					System.err.println("Failure to get pathing: " + exc.getClass() + ":" + exc.getMessage());
				}
			}
		}
		return buildingPathingPixelMap;
	}

	public RenderUnitType getUnitTypeData(final War3ID key) {
		return this.renderUnitTypeData.get(key);
	}

	@Override
	public void update() {
		if (this.anyReady) {
			this.successfulFrames++;
			final float deltaTime = Gdx.graphics.getDeltaTime();
			this.terrain.update(deltaTime);

			super.update();

			final float rawDeltaTime = Gdx.graphics.getRawDeltaTime();
			this.updateTime += rawDeltaTime;

			final Iterator<TextTag> textTagIterator = this.textTags.iterator();
			while (textTagIterator.hasNext()) {
				if (textTagIterator.next().update(deltaTime)) {
					textTagIterator.remove();
				}
			}
			for (final RenderWidget unit : this.widgets) {
				unit.updateAnimations(this);
			}
			final Iterator<RenderEffect> projectileIterator = this.projectiles.iterator();
			while (projectileIterator.hasNext()) {
				final RenderEffect projectile = projectileIterator.next();
				if (projectile.updateAnimations(this, Gdx.graphics.getDeltaTime())) {
					projectileIterator.remove();
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

			while (this.updateTime >= WarsmashConstants.SIMULATION_STEP_TIME) {
				if (this.gameTurnManager.getLatestCompletedTurn() >= this.simulation.getGameTurnTick()) {
					this.updateTime -= WarsmashConstants.SIMULATION_STEP_TIME;
					this.simulation.update();
					this.gameTurnManager.turnCompleted(this.simulation.getGameTurnTick());
				}
				else {
					if (this.updateTime > (WarsmashConstants.SIMULATION_STEP_TIME * 3)) {
						this.gameTurnManager.framesSkipped(this.updateTime / WarsmashConstants.SIMULATION_STEP_TIME);
						this.updateTime = 0;
					}
					break;
				}
			}
			if (this.dncTerrain != null) {
				this.dncTerrain.setFrameByRatio(
						this.simulation.getGameTimeOfDay() / this.simulation.getGameplayConstants().getGameDayHours());
				this.dncTerrain.update(rawDeltaTime, null);
			}
			if (this.dncUnit != null) {
				this.dncUnit.setFrameByRatio(
						this.simulation.getGameTimeOfDay() / this.simulation.getGameplayConstants().getGameDayHours());
				this.dncUnit.update(rawDeltaTime, null);
			}

			if (this.dncTarget != null) {
				this.dncTarget.setFrameByRatio(
						this.simulation.getGameTimeOfDay() / this.simulation.getGameplayConstants().getGameDayHours());
				this.dncTarget.update(rawDeltaTime, null);
			}
		}
	}

	@Override
	public void render() {
		if (this.anyReady) {
			final Scene worldScene = this.worldScene;

			startFrame();
			worldScene.startFrame();
			if (DEBUG_DEPTH > 0) {
				worldScene.renderOpaque(this.dynamicShadowManager, this.webGL);
			}
			if (DEBUG_DEPTH > 1) {
				this.terrain.renderGround(this.dynamicShadowManager);
			}
			if (DEBUG_DEPTH > 2) {
				this.terrain.renderCliffs();
			}
			if (DEBUG_DEPTH > 3) {
				worldScene.renderOpaque();
			}
			if (DEBUG_DEPTH > 4) {
				this.terrain.renderUberSplats(false);
			}
			if (DEBUG_DEPTH > 5) {
				this.terrain.renderWater();
			}
			if (DEBUG_DEPTH > 6) {
				worldScene.renderTranslucent();
			}
			if (DEBUG_DEPTH > 7) {
				this.terrain.renderUberSplats(true);
			}

			final List<Scene> scenes = this.scenes;
			for (final Scene scene : scenes) {
				if (scene != worldScene) {
					scene.startFrame();
					if (DEBUG_DEPTH > 8) {
						scene.renderOpaque();
					}
					if (DEBUG_DEPTH > 9) {
						scene.renderTranslucent();
					}
				}
			}

			final int glGetError = Gdx.gl.glGetError();
			if (glGetError != GL20.GL_NO_ERROR) {
				throw new IllegalStateException("GL ERROR: " + glGetError);
			}
		}
	}

	public void deselect() {
		for (final String key : this.selectedSplatModelKeys) {
			this.terrain.removeSplatBatchModel(key);
		}
		for (final RenderWidget unit : this.selected) {
			unit.unassignSelectionCircle();
		}
		this.selectedSplatModelKeys.clear();
		this.selected.clear();
	}

	public void doUnselectUnit(final RenderWidget widget) {
		if (this.selected.remove(widget)) {
			widget.getSelectionCircle();
		}
	}

	public void doSelectUnit(final List<RenderWidget> units) {
		deselect();
		if (units.isEmpty()) {
			return;
		}

		final Map<String, Terrain.Splat> splats = new HashMap<String, Terrain.Splat>();
		for (final RenderWidget unit : units) {
			if (unit.getSelectionScale() > 0) {
				String allyKey = "n:";
				final float selectionSize = unit.getSelectionScale();
				String path = null;
				for (int i = 0; i < this.selectionCircleSizes.size(); i++) {
					final SelectionCircleSize selectionCircleSize = this.selectionCircleSizes.get(i);
					if ((selectionSize < selectionCircleSize.size) || (i == (this.selectionCircleSizes.size() - 1))) {
						path = selectionCircleSize.texture;
						break;
					}
				}
				if (!path.toLowerCase().endsWith(".blp")) {
					path += ".blp";
				}
				if (unit instanceof RenderUnit) {
					final int selectedUnitPlayerIndex = ((RenderUnit) unit).getSimulationUnit().getPlayerIndex();
					final CPlayer localPlayer = this.simulation.getPlayer(this.localPlayerIndex);
					if (!localPlayer.hasAlliance(selectedUnitPlayerIndex, CAllianceType.PASSIVE)) {
						allyKey = "e:";
					}
					else if (localPlayer.hasAlliance(selectedUnitPlayerIndex, CAllianceType.SHARED_CONTROL)) {
						allyKey = "f:";
					}
				}
				path = allyKey + path;
				if (unit.isShowSelectionCircleAboveWater()) {
					path = path + ":abovewater";
				}
				final SplatModel splatModel = this.terrain.getSplatModel("selection:" + path);
				if (splatModel != null) {
					final float x = unit.getX();
					final float y = unit.getY();
					final SplatMover splatInstance = splatModel.add(x - (selectionSize / 2), y - (selectionSize / 2),
							x + (selectionSize / 2), y + (selectionSize / 2), 5, this.terrain.centerOffset);
					unit.assignSelectionCircle(splatInstance);
					if (unit.getInstance().hidden()) {
						splatInstance.hide();
					}
				}
				else {
					if (!splats.containsKey(path)) {
						splats.put(path, new Splat());
					}
					final float x = unit.getX();
					final float y = unit.getY();
					System.out.println("Selecting a unit at " + x + "," + y);
					if (unit.isShowSelectionCircleAboveWater()) {
						splats.get(path).aboveWater = true;
					}
					splats.get(path).locations.add(new float[] { x - (selectionSize / 2), y - (selectionSize / 2),
							x + (selectionSize / 2), y + (selectionSize / 2), 5 });
					splats.get(path).unitMapping.add(new Consumer<SplatModel.SplatMover>() {
						@Override
						public void accept(final SplatMover t) {
							unit.assignSelectionCircle(t);
							if (unit.getInstance().hidden()) {
								t.hide();
							}
						}
					});
				}
			}
			this.selected.add(unit);
		}
		for (final Map.Entry<String, Terrain.Splat> entry : splats.entrySet()) {
			final String path = entry.getKey();
			String filePath = path.substring(2);
			if (filePath.endsWith(":abovewater")) {
				filePath = filePath.substring(0, filePath.length() - 11);
			}
			final String allyKey = path.substring(0, 2);
			final Splat locations = entry.getValue();
			final SplatModel model = new SplatModel(Gdx.gl30, (Texture) load(filePath, PathSolver.DEFAULT, null),
					locations.locations, this.terrain.centerOffset, locations.unitMapping, true, false, true,
					locations.aboveWater);
			switch (allyKey) {
			case "e:":
				model.color[0] = this.selectionCircleColorEnemy.r;
				model.color[1] = this.selectionCircleColorEnemy.g;
				model.color[2] = this.selectionCircleColorEnemy.b;
				model.color[3] = this.selectionCircleColorEnemy.a;
				break;
			case "f:":
				model.color[0] = this.selectionCircleColorFriend.r;
				model.color[1] = this.selectionCircleColorFriend.g;
				model.color[2] = this.selectionCircleColorFriend.b;
				model.color[3] = this.selectionCircleColorFriend.a;
				break;
			default:
				model.color[0] = this.selectionCircleColorNeutral.r;
				model.color[1] = this.selectionCircleColorNeutral.g;
				model.color[2] = this.selectionCircleColorNeutral.b;
				model.color[3] = this.selectionCircleColorNeutral.a;
				break;
			}
			this.terrain.addSplatBatchModel("selection:" + path, model);
			this.selectedSplatModelKeys.add("selection:" + path);
		}
	}

	public void clearUnitMouseOverHighlight(final RenderWidget unit) {
		this.mouseHighlightWidgets.remove(unit);
		unit.getSelectionPreviewHighlight().destroy(Gdx.gl30, this.terrain.centerOffset);
		unit.unassignSelectionPreviewHighlight();
	}

	public void clearUnitMouseOverHighlight() {
		for (final String modelKey : this.mouseHighlightSplatModelKeys) {
			this.terrain.removeSplatBatchModel(modelKey);
		}
		for (final RenderWidget widget : this.mouseHighlightWidgets) {
			widget.unassignSelectionPreviewHighlight();
		}
		this.mouseHighlightSplatModelKeys.clear();
		this.mouseHighlightWidgets.clear();
	}

	public void showUnitMouseOverHighlight(final RenderWidget unit) {
		final Map<String, Terrain.Splat> splats = new HashMap<String, Terrain.Splat>();
		if (unit.getSelectionScale() > 0) {
			String allyKey = "n:";
			final float selectionSize = unit.getSelectionScale();
			String path = null;
			for (int i = 0; i < this.selectionCircleSizes.size(); i++) {
				final SelectionCircleSize selectionCircleSize = this.selectionCircleSizes.get(i);
				if ((selectionSize < selectionCircleSize.size) || (i == (this.selectionCircleSizes.size() - 1))) {
					path = selectionCircleSize.texture;
					break;
				}
			}
			if (!path.toLowerCase().endsWith(".blp")) {
				path += ".blp";
			}
			if (unit instanceof RenderUnit) {
				final CUnit simulationUnit = ((RenderUnit) unit).getSimulationUnit();
				final int selectedUnitPlayerIndex = simulationUnit.getPlayerIndex();
				final CPlayer localPlayer = this.simulation.getPlayer(this.localPlayerIndex);
				if (!localPlayer.hasAlliance(selectedUnitPlayerIndex, CAllianceType.PASSIVE)) {
					allyKey = "e:";
				}
				else if (localPlayer.hasAlliance(selectedUnitPlayerIndex, CAllianceType.SHARED_CONTROL)) {
					allyKey = "f:";
				}
			}
			path = allyKey + path;
			if (unit.isShowSelectionCircleAboveWater()) {
				path = path + ":abovewater";
			}
			final SplatModel splatModel = this.terrain.getSplatModel("mouseover:" + path);
			if (splatModel != null) {
				final float x = unit.getX();
				final float y = unit.getY();
				final SplatMover splatInstance = splatModel.add(x - (selectionSize / 2), y - (selectionSize / 2),
						x + (selectionSize / 2), y + (selectionSize / 2), 4, this.terrain.centerOffset);
				unit.assignSelectionPreviewHighlight(splatInstance);
				if (unit.getInstance().hidden()) {
					splatInstance.hide();
				}
			}
			else {
				if (!splats.containsKey(path)) {
					splats.put(path, new Splat());
				}
				final float x = unit.getX();
				final float y = unit.getY();
				if (unit.isShowSelectionCircleAboveWater()) {
					splats.get(path).aboveWater = true;
				}
				splats.get(path).locations.add(new float[] { x - (selectionSize / 2), y - (selectionSize / 2),
						x + (selectionSize / 2), y + (selectionSize / 2), 4 });
				splats.get(path).unitMapping.add(new Consumer<SplatModel.SplatMover>() {
					@Override
					public void accept(final SplatMover t) {
						unit.assignSelectionPreviewHighlight(t);
						if (unit.getInstance().hidden()) {
							t.hide();
						}
					}
				});
			}
		}
		this.mouseHighlightWidgets.add(unit);
		for (final Map.Entry<String, Terrain.Splat> entry : splats.entrySet()) {
			final String path = entry.getKey();
			String filePath = path.substring(2);
			if (filePath.endsWith(":abovewater")) {
				filePath = filePath.substring(0, filePath.length() - 11);
			}
			final String allyKey = path.substring(0, 2);
			final Splat locations = entry.getValue();
			final SplatModel model = new SplatModel(Gdx.gl30, (Texture) load(filePath, PathSolver.DEFAULT, null),
					locations.locations, this.terrain.centerOffset, locations.unitMapping, true, false, true,
					locations.aboveWater);
			switch (allyKey) {
			case "e:":
				model.color[0] = this.selectionCircleColorEnemy.r;
				model.color[1] = this.selectionCircleColorEnemy.g;
				model.color[2] = this.selectionCircleColorEnemy.b;
				model.color[3] = this.selectionCircleColorEnemy.a * 0.5f;
				break;
			case "f:":
				model.color[0] = this.selectionCircleColorFriend.r;
				model.color[1] = this.selectionCircleColorFriend.g;
				model.color[2] = this.selectionCircleColorFriend.b;
				model.color[3] = this.selectionCircleColorFriend.a * 0.5f;
				break;
			default:
				model.color[0] = this.selectionCircleColorNeutral.r;
				model.color[1] = this.selectionCircleColorNeutral.g;
				model.color[2] = this.selectionCircleColorNeutral.b;
				model.color[3] = this.selectionCircleColorNeutral.a * 0.5f;
				break;
			}
			this.mouseHighlightSplatModelKeys.add("mouseover:" + path);
			this.terrain.addSplatBatchModel("mouseover:" + path, model);
		}
	}

	public void getClickLocation(final Vector3 out, final int screenX, final int screenY,
			final boolean intersectWithWater, final boolean pushResultZAboveWater) {
		final float[] ray = rayHeap;
		mousePosHeap.set(screenX, screenY);
		this.worldScene.camera.screenToWorldRay(ray, mousePosHeap);
		gdxRayHeap.set(ray[0], ray[1], ray[2], ray[3] - ray[0], ray[4] - ray[1], ray[5] - ray[2]);
		gdxRayHeap.direction.nor();// needed for libgdx
		if (intersectWithWater) {
			RenderMathUtils.intersectRayTriangles(gdxRayHeap, this.terrain.softwareWaterAndGroundMesh.vertices,
					this.terrain.softwareWaterAndGroundMesh.indices, 3, out);
		}
		else {
			RenderMathUtils.intersectRayTriangles(gdxRayHeap, this.terrain.softwareGroundMesh.vertices,
					this.terrain.softwareGroundMesh.indices, 3, out);
		}
		rectangleHeap.set(Math.min(out.x, gdxRayHeap.origin.x), Math.min(out.y, gdxRayHeap.origin.y),
				Math.abs(out.x - gdxRayHeap.origin.x), Math.abs(out.y - gdxRayHeap.origin.y));
		this.walkableObjectsTree.intersect(rectangleHeap, this.walkablesIntersectionFinder.reset(gdxRayHeap));
		if (this.walkablesIntersectionFinder.found) {
			out.set(this.walkablesIntersectionFinder.intersection);
		}
		else {
			final float oldZ = out.z;
			out.z = Math.max(getWalkableRenderHeight(out.x, out.y), this.terrain.getGroundHeight(out.x, out.y));

			if (pushResultZAboveWater) {
				out.z = Math.max(out.z, oldZ);
				final short pathing = this.simulation.getPathingGrid().getPathing(out.x, out.y);
				if (PathingGrid.isPathingFlag(pathing, PathingType.SWIMMABLE)
						&& !PathingGrid.isPathingFlag(pathing, PathingType.WALKABLE)) {
					out.z = Math.max(out.z, this.terrain.getWaterHeight(out.x, out.y));
				}
			}
		}
	}

	public void getClickLocationOnZPlane(final Vector3 out, final int screenX, final int screenY, final float worldZ) {
		final float[] ray = rayHeap;
		mousePosHeap.set(screenX, screenY);
		this.worldScene.camera.screenToWorldRay(ray, mousePosHeap);
		gdxRayHeap.set(ray[0], ray[1], ray[2], ray[3] - ray[0], ray[4] - ray[1], ray[5] - ray[2]);
		gdxRayHeap.direction.nor();// needed for libgdx
		planeHeap.set(0, 0, -1, worldZ);
		Intersector.intersectRayPlane(gdxRayHeap, planeHeap, out);
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

	public RenderWidget rayPickUnit(final float x, final float y) {
		return this.rayPickUnit(x, y, CWidgetFilterFunction.ACCEPT_ALL);
	}

	public RenderWidget rayPickUnit(final float x, final float y, final CWidgetFilterFunction filter) {
		final float[] ray = rayHeap;
		mousePosHeap.set(x, y);
		this.worldScene.camera.screenToWorldRay(ray, mousePosHeap);
		gdxRayHeap.set(ray[0], ray[1], ray[2], ray[3] - ray[0], ray[4] - ray[1], ray[5] - ray[2]);
		gdxRayHeap.direction.nor();// needed for libgdx

		RenderWidget entity = null;
		intersectionHeap2.set(ray[3], ray[4], ray[5]);
		for (final RenderWidget unit : this.widgets) {
			final MdxComplexInstance instance = unit.getInstance();
			if (instance.shown() && instance.isVisible(this.worldScene.camera)
					&& instance.intersectRayWithCollisionSimple(gdxRayHeap, intersectionHeap)) {
				if (filter.call(unit.getSimulationWidget())) {
					final float groundHeight = this.terrain.getGroundHeight(intersectionHeap.x, intersectionHeap.y);
					if (intersectionHeap.z > groundHeight) {
						if ((entity == null) && !unit.isIntersectedOnMeshAlways()) {
							entity = unit;
						}
						else {
							if (instance.intersectRayWithMeshSlow(gdxRayHeap, intersectionHeap)) {
								if (intersectionHeap.z > this.terrain.getGroundHeight(intersectionHeap.x,
										intersectionHeap.y)) {
									this.worldScene.camera.worldToCamera(intersectionHeap, intersectionHeap);
									if ((entity == null) || (intersectionHeap.z < intersectionHeap2.z)) {
										entity = unit;
										intersectionHeap2.set(intersectionHeap);
									}
								}
							}
						}
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
	private Warcraft3MapRuntimeObjectData allObjectData;
	private AbilityDataUI abilityDataUI;
	public int imageWalkableZOffset;
	private WTS preloadedWTS;

	private Color selectionCircleColorFriend;

	private Color selectionCircleColorNeutral;

	private Color selectionCircleColorEnemy;

	private int localPlayerServerSlot;

	private final Rectangle tempBlightRect = new Rectangle();

	private RenderUnitTypeData renderUnitTypeData;
	private RenderItemTypeData renderItemTypeData;

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
		return numElements < 0 ? 1 : numElements >= MAXIMUM_ACCEPTED ? MAXIMUM_ACCEPTED : numElements + 1;
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
		final MdxModel terrainDNCModel = loadModelMdx(terrainDNCFile);
		this.dncTerrain = (MdxComplexInstance) terrainDNCModel.addInstance();
		this.dncTerrain.setSequenceLoopMode(SequenceLoopMode.ALWAYS_LOOP);
		this.dncTerrain.setSequence(0);
		final MdxModel unitDNCModel = loadModelMdx(unitDNCFile);
		this.dncUnit = (MdxComplexInstance) unitDNCModel.addInstance();
		this.dncUnit.setSequenceLoopMode(SequenceLoopMode.ALWAYS_LOOP);
		this.dncUnit.setSequence(0);
		final MdxModel targetDNCModel = loadModelMdx(
				"Environment\\DNC\\DNCLordaeron\\DNCLordaeronTarget\\DNCLordaeronTarget.mdl");
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

	public static String mdl(String mdxPath) {
		if (mdxPath.toLowerCase().endsWith(".mdx")) {
			mdxPath = mdxPath.substring(0, mdxPath.length() - 4);
		}
		if (!mdxPath.toLowerCase().endsWith(".mdl")) {
			mdxPath += ".mdl";
		}
		return mdxPath;
	}

	public String blp(String iconPath) {
		final int lastDotIndex = iconPath.lastIndexOf('.');
		if (lastDotIndex != -1) {
			iconPath = iconPath.substring(0, lastDotIndex);
		}
		if (!iconPath.toLowerCase().endsWith(".blp")) {
			iconPath += ".blp";
		}
		return iconPath;
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
		this.abilityDataUI = new AbilityDataUI(this.allObjectData, gameUI, this);
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

	public Warcraft3MapRuntimeObjectData getAllObjectData() {
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

	public RenderDestructable getRenderPeer(final CDestructable dest) {
		return this.destructableToRenderPeer.get(dest);
	}

	public RenderItem getRenderPeer(final CItem item) {
		return this.itemToRenderPeer.get(item);
	}

	public RenderWidget getRenderPeer(final CWidget damagedDestructable) {
		RenderWidget damagedWidget = War3MapViewer.this.unitToRenderPeer.get(damagedDestructable);
		if (damagedWidget == null) {
			damagedWidget = War3MapViewer.this.destructableToRenderPeer.get(damagedDestructable);
		}
		if (damagedWidget == null) {
			damagedWidget = War3MapViewer.this.itemToRenderPeer.get(damagedDestructable);
		}
		return damagedWidget;
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

	public void add(final TextTag textTag) {
		this.textTags.add(textTag);
	}

	public SettableCommandErrorListener getCommandErrorListener() {
		return this.commandErrorListener;
	}

	public War3MapConfig getMapConfig() {
		return this.mapConfig;
	}

	public void setLocalPlayerIndex(final int playerIndex) {
		// TODO this is HACKY to not do this on INIT, but it is a cheese way to try to
		// do the networking for now!!!
		this.localPlayerIndex = playerIndex;
	}

	public void setLocalPlayerServerSlot(final int localPlayerServerSlot) {
		this.localPlayerServerSlot = localPlayerServerSlot;
	}

	public void setGameTurnManager(final GameTurnManager gameTurnManager) {
		this.gameTurnManager = gameTurnManager;
	}

	public RenderSpellEffect spawnSpellEffectOnUnitEx(final CWidget unit, final War3ID alias,
			final CEffectType effectType, final int index) {
		final EffectAttachmentUI effectAttachmentUI = getEffectAttachmentUI(alias, effectType, index);
		if (effectAttachmentUI == null) {
			return null;
		}
		final String modelPath = effectAttachmentUI.getModelPath();
		final List<String> attachmentPoint = effectAttachmentUI.getAttachmentPoint();
		final RenderSpellEffect specialEffect = addSpecialEffectTarget(modelPath, unit, attachmentPoint);
		return specialEffect;
	}

	public RenderSpellEffect spawnSpellEffectOnUnitEx(final CWidget unit, final War3ID alias,
			final CEffectType effectType, final int index, final String attachPointName) {
		final EffectAttachmentUI effectAttachmentUI = getEffectAttachmentUI(alias, effectType, index);
		if (effectAttachmentUI == null) {
			return null;
		}
		final String modelPath = effectAttachmentUI.getModelPath();
		final RenderSpellEffect specialEffect = addSpecialEffectTarget(modelPath, unit,
				Arrays.asList(attachPointName.split(",")));
		return specialEffect;
	}

	public List<RenderSpellEffect> spawnSpellEffectOnUnitEx(final CUnit unit, final War3ID alias,
			final CEffectType effectType) {
		final List<EffectAttachmentUI> effectAttachmentUI = getEffectAttachmentUIList(alias, effectType);
		if (effectAttachmentUI == null) {
			return null;
		}
		final List<RenderSpellEffect> renderEffects = new ArrayList<>();
		for (final EffectAttachmentUI effect : effectAttachmentUI) {
			final String modelPath = effect.getModelPath();
			final List<String> attachmentPoint = effect.getAttachmentPoint();
			final RenderSpellEffect specialEffect = addSpecialEffectTarget(modelPath, unit, attachmentPoint);
			if (specialEffect != null) {
				renderEffects.add(specialEffect);
			}
		}
		return renderEffects;
	}

	public RenderSpellEffect spawnSpellEffectEx(final float x, final float y, final float facing, final War3ID alias,
			final CEffectType effectType, final int index) {
		final EffectAttachmentUI effectAttachmentUI = getEffectAttachmentUI(alias, effectType, index);
		if (effectAttachmentUI == null) {
			return null;
		}
		final String modelPath = effectAttachmentUI.getModelPath();
		final List<String> attachmentPoint = effectAttachmentUI.getAttachmentPoint();
		final RenderSpellEffect specialEffect = addSpecialEffect(modelPath, x, y, (float) StrictMath.toRadians(facing));
		return specialEffect;
	}

	public List<EffectAttachmentUI> getEffectAttachmentUIList(final War3ID alias, final CEffectType effectType) {
		final AbilityUI abilityUI = War3MapViewer.this.abilityDataUI.getUI(alias);
		List<EffectAttachmentUI> effectAttachmentUI = null;
		if (abilityUI != null) {
			switch (effectType) {
			case EFFECT:
				effectAttachmentUI = abilityUI.getEffectArt();
				break;
			case TARGET:
				effectAttachmentUI = abilityUI.getTargetArt();
				break;
			case CASTER:
				effectAttachmentUI = abilityUI.getCasterArt();
				break;
			case SPECIAL:
				effectAttachmentUI = abilityUI.getSpecialArt();
				break;
			case AREA_EFFECT:
				effectAttachmentUI = abilityUI.getAreaEffectArt();
				break;
			case MISSILE:
				effectAttachmentUI = new ArrayList<>(abilityUI.getMissileArt());
				break;
			default:
				throw new IllegalArgumentException("Unsupported effect type: " + effectType);
			}
		}
		else {
			final BuffUI buffUI = War3MapViewer.this.abilityDataUI.getBuffUI(alias);
			if (buffUI != null) {
				switch (effectType) {
				case EFFECT:
					effectAttachmentUI = buffUI.getEffectArt();
					break;
				case TARGET:
					effectAttachmentUI = buffUI.getTargetArt();
					break;
				case SPECIAL:
					effectAttachmentUI = buffUI.getSpecialArt();
					break;
				case MISSILE:
					effectAttachmentUI = buffUI.getMissileArt();
					break;
				default:
					throw new IllegalArgumentException("Unsupported effect type: " + effectType);
				}
			}
			else {
				return null;
			}
		}
		return effectAttachmentUI;
	}

	public EffectAttachmentUI getEffectAttachmentUI(final War3ID alias, final CEffectType effectType, final int index) {
		final AbilityUI abilityUI = War3MapViewer.this.abilityDataUI.getUI(alias);
		EffectAttachmentUI effectAttachmentUI = null;
		if (abilityUI != null) {
			switch (effectType) {
			case EFFECT:
				effectAttachmentUI = abilityUI.getEffectArt(index);
				break;
			case TARGET:
				effectAttachmentUI = abilityUI.getTargetArt(index);
				break;
			case CASTER:
				effectAttachmentUI = abilityUI.getCasterArt(index);
				break;
			case SPECIAL:
				effectAttachmentUI = abilityUI.getSpecialArt(index);
				break;
			case AREA_EFFECT:
				effectAttachmentUI = abilityUI.getAreaEffectArt(index);
				break;
			case MISSILE:
				effectAttachmentUI = abilityUI.getMissileArt(index);
				break;
			default:
				throw new IllegalArgumentException("Unsupported effect type: " + effectType);
			}
		}
		else {
			final BuffUI buffUI = War3MapViewer.this.abilityDataUI.getBuffUI(alias);
			if (buffUI != null) {
				switch (effectType) {
				case EFFECT:
					effectAttachmentUI = buffUI.getEffectArt(index);
					break;
				case TARGET:
					effectAttachmentUI = buffUI.getTargetArt(index);
					break;
				case SPECIAL:
					effectAttachmentUI = buffUI.getSpecialArt(index);
					break;
				case MISSILE:
					effectAttachmentUI = buffUI.getMissileArt(index);
					break;
				default:
					throw new IllegalArgumentException("Unsupported effect type: " + effectType);
				}
			}
			else {
				return null;
			}
		}
		return effectAttachmentUI;
	}

	public List<War3ID> getLightningEffectList(final War3ID alias) {
		final AbilityUI abilityUI = War3MapViewer.this.abilityDataUI.getUI(alias);
		return abilityUI.getLightningEffects();
	}

	public RenderSpellEffect addSpecialEffectTarget(final String modelName, final CWidget targetWidget,
			final String attachPointName) {
		return addSpecialEffectTarget(modelName, targetWidget, Arrays.asList(attachPointName.split("\\s+")));
	}

	public RenderSpellEffect addSpecialEffectTarget(final String modelName, final CWidget targetWidget,
			List<String> attachPointNames) {
		if (targetWidget instanceof CUnit) {
			if (attachPointNames.isEmpty() || ((attachPointNames.size() == 1) && attachPointNames.get(0).isEmpty())) {
				attachPointNames = ORIGIN_STRING_LIST;
			}
			final RenderUnit renderUnit = War3MapViewer.this.unitToRenderPeer.get(targetWidget);
			if (renderUnit == null) {
				final NullPointerException nullPointerException = new NullPointerException(
						"renderUnit is null! targetWidget is \"" + ((CUnit) targetWidget).getUnitType().getName()
								+ "\", attachPointName=\"" + attachPointNames + "\"");
				if (WarsmashConstants.ENABLE_DEBUG) {
					throw nullPointerException;
				}
				else {
					nullPointerException.printStackTrace();
				}
			}
			final MdxModel spawnedEffectModel = loadModelMdx(modelName);
			if (spawnedEffectModel != null) {
				final MdxComplexInstance modelInstance = (MdxComplexInstance) spawnedEffectModel.addInstance();
				float yaw = 0;
				if (renderUnit != null) {
					modelInstance.setTeamColor(renderUnit.playerIndex);
					{
						final MdxModel model = (MdxModel) renderUnit.instance.model;
						int index = -1;
						int bestFitAttachmentNameLength = Integer.MAX_VALUE;
						for (int i = 0; i < model.attachments.size(); i++) {
							final Attachment attachment = model.attachments.get(i);
							boolean match = true;
							for (final String attachmentPointNameToken : attachPointNames) {
								if (!attachment.getName().contains(attachmentPointNameToken)) {
									match = false;
								}
							}
							final int attachmentNameLength = attachment.getName().length();
							if (match && (attachmentNameLength < bestFitAttachmentNameLength)) {
								index = i;
								bestFitAttachmentNameLength = attachmentNameLength;
							}
						}
						if (index != -1) {
							modelInstance.detach();
							final MdxNode attachment = renderUnit.instance.getAttachment(index);
							modelInstance.setParent(attachment);
							modelInstance.setLocation(0, 0, 0);
						}
						else {
							// TODO This is not consistent with War3, is it? Should look nice though.
							modelInstance.setLocation(renderUnit.location);
							yaw = (float) Math.toRadians(renderUnit.getSimulationUnit().getFacing());
						}
					}
				}
				else {
					modelInstance.setLocation(0, 0, 0);
				}
				modelInstance.setScene(War3MapViewer.this.worldScene);
				final EnumSet<SecondaryTag> requiredAnimationNamesForAttachments = renderUnit == null
						? SequenceUtils.EMPTY
						: renderUnit.getTypeData().getRequiredAnimationNamesForAttachments();
				final RenderSpellEffect renderAttackInstant = new RenderSpellEffect(modelInstance, War3MapViewer.this,
						yaw, RenderSpellEffect.DEFAULT_ANIMATION_QUEUE, requiredAnimationNamesForAttachments);
				War3MapViewer.this.projectiles.add(renderAttackInstant);
				return renderAttackInstant;
			}
		}
		else if (targetWidget instanceof CItem) {
			// TODO this is stupid api, who would do this?
			throw new UnsupportedOperationException("API for addSpecialEffectTarget() on item is NYI");
		}
		else if (targetWidget instanceof CDestructable) {
			// TODO this is stupid api, who would do this?
			throw new UnsupportedOperationException("API for addSpecialEffectTarget() on destructable is NYI");
		}
		return null;
	}

	public RenderSpellEffect addSpecialEffect(final String modelName, final float x, final float y, final float yaw) {
		final MdxModel spawnedEffectModel = loadModelMdx(modelName);
		if (spawnedEffectModel != null) {
			final MdxComplexInstance modelInstance = (MdxComplexInstance) spawnedEffectModel.addInstance();
			{
				modelInstance.setLocation(x, y,
						Math.max(getWalkableRenderHeight(x, y), this.terrain.getGroundHeight(x, y)));
			}
			modelInstance.setScene(War3MapViewer.this.worldScene);
			final RenderSpellEffect renderAttackInstant = new RenderSpellEffect(modelInstance, War3MapViewer.this, yaw,
					RenderSpellEffect.DEFAULT_ANIMATION_QUEUE, SequenceUtils.EMPTY);
			War3MapViewer.this.projectiles.add(renderAttackInstant);
			return renderAttackInstant;
		}
		return null;
	}

	@Override
	public MdxModel loadModelMdx(final String path) {
		return loadModelMdx(this.dataSource, this, path, this.mapPathSolver, this.solverParams);
	}

	public static MdxModel loadModelMdx(final DataSource dataSource, final ModelViewer modelViewer, final String path,
			final PathSolver pathSolver, final Object solverParams) {
		if ("".equals(path)) {
			return null;
		}
		final String mdxPath = mdx(path);
		if (dataSource.has(mdxPath)) {
			return (MdxModel) modelViewer.load(mdxPath, pathSolver, solverParams);
		}
		else {
			final String mdlPath = mdl(mdxPath);
			if (dataSource.has(mdlPath)) {
				return (MdxModel) modelViewer.load(mdlPath, pathSolver, solverParams);
			}
		}
		return (MdxModel) modelViewer.load(mdxPath, pathSolver, solverParams);
	}

	public void setBlight(float whichLocationX, float whichLocationY, final float radius, final boolean blighted) {
		final int cellX = this.terrain.get128CellX(whichLocationX);
		final int cellY = this.terrain.get128CellY(whichLocationY);
		whichLocationX = this.terrain.get128WorldCoordinateFromCellX(cellX);
		whichLocationY = this.terrain.get128WorldCoordinateFromCellY(cellY);
		final Rectangle blightRectangle = new Rectangle(whichLocationX - radius, whichLocationY - radius, radius * 2,
				radius * 2);
		final float blightRectangleMaxX = blightRectangle.x + blightRectangle.width;
		final float blightRectangleMaxY = blightRectangle.y + blightRectangle.height;
		final float rSquared = radius * radius;
		boolean changedData = false;
		for (float x = blightRectangle.x; x < blightRectangleMaxX; x += 128.0f) {
			for (float y = blightRectangle.y; y < blightRectangleMaxY; y += 128.0f) {
				final float dx = x - whichLocationX;
				final float dy = y - whichLocationY;
				final float distSquared = (dx * dx) + (dy * dy);
				if (distSquared <= rSquared) {
					final RenderCorner corner = this.terrain.getCorner(x, y);
					if (corner == null) {
						continue;
					}
					final GroundTexture currentTex = this.terrain.groundTextures.get(corner.getGroundTexture());
					if (currentTex.isBuildable()) {
						changedData |= corner.setBlight(blighted);
					}
					else {
						continue;
					}

					for (float pathX = -64; pathX < 64; pathX += 32f) {
						for (float pathY = -64; pathY < 64; pathY += 32f) {
							final float blightX = x + pathX + 16;
							final float blightY = y + pathY + 16;
							if (this.simulation.getPathingGrid().contains(blightX, blightY)) {
								this.simulation.getPathingGrid().setBlighted(blightX, blightY, blighted);
							}
						}
					}
				}
			}
		}
		if (changedData) {
			final int cellMinX = this.terrain.get128CellX(blightRectangle.x);
			final int cellMinY = this.terrain.get128CellY(blightRectangle.y);
			final int cellMaxX = this.terrain.get128CellX(blightRectangleMaxX);
			final int cellMaxY = this.terrain.get128CellY(blightRectangleMaxY);
			final Rectangle blightRectangleCellUnits = this.tempBlightRect.set(cellMinX, cellMinY, cellMaxX - cellMinX,
					cellMaxY - cellMinY);
			this.terrain.updateGroundTextures(blightRectangleCellUnits);
		}

		if (blighted) {
			this.simulation.getWorldCollision().enumDestructablesInRect(blightRectangle,
					TreeBlightingCallback.INSTANCE.reset(this.simulation));
		}
	}

	public CPlayerFogOfWar getFogOfWar() {
		return this.simulation.getPlayer(this.localPlayerIndex).getFogOfWar();
	}

	public static byte fadeLineOfSightColor(final byte lastFogStateColor, final byte state) {
		final short prevValue = (short) (lastFogStateColor & 0xFF);
		final short newValue = (short) (state & 0xFF);
		final short delta = (short) (newValue - prevValue);
		final short appliedMagnitude = (short) Math.min(9, Math.abs(delta));

		return (byte) ((prevValue + (appliedMagnitude * (delta < 0 ? -1 : 1))) & 0xFF);
	}

	public final class MapLoader {
		private final LinkedList<LoadMapTask> loadMapTasks = new LinkedList<>();
		private int startingTaskCount;

		private char tileset;
		private DataSource tilesetSource;

		private War3MapW3e terrainData;

		private War3MapWpm terrainPathing;

		private MapLoader(final War3Map war3Map, final War3MapW3i w3iFile, final int localPlayerIndex) {
			final PathSolver wc3PathSolver = War3MapViewer.this.wc3PathSolver;

			this.loadMapTasks.add(() -> {
				this.tileset = 'A';
				this.tileset = w3iFile.getTileset();
				int gameDataSet = w3iFile.getGameDataSet();

				try {
					// Slightly complex. Here's the theory:
					// 1.) Copy map into RAM
					// 2.) Setup a Data Source that will read assets
					// from either the map or the game, giving the map priority.
					SeekableByteChannel sbc;
					final DataSource compoundDataSource = war3Map.getCompoundDataSource();
					final List<DataSource> dataSources = new ArrayList<>();
					dataSources.add(compoundDataSource);
					if (WarsmashConstants.FIX_FLAT_FILES_TILESET_LOADING) {
						dataSources.add(new SubdirDataSource(compoundDataSource, this.tileset + ".mpq/"));
					}
					else {
						try (InputStream mapStream = compoundDataSource.getResourceAsStream(this.tileset + ".mpq")) {
							if (mapStream == null) {
								dataSources.add(new SubdirDataSource(compoundDataSource, this.tileset + ".mpq/"));
								dataSources.add(new SubdirDataSource(compoundDataSource,
										"_tilesets/" + this.tileset + ".w3mod/"));
							}
							else {
								final byte[] mapData = IOUtils.toByteArray(mapStream);
								sbc = new SeekableInMemoryByteChannel(mapData);
								final DataSource internalMpqContentsDataSource = new MpqDataSource(new MPQArchive(sbc),
										sbc);
								dataSources.add(internalMpqContentsDataSource);
							}
						}
						catch (final IOException exc) {
							dataSources.add(new SubdirDataSource(compoundDataSource, this.tileset + ".mpq/"));
							dataSources.add(
									new SubdirDataSource(compoundDataSource, "_tilesets/" + this.tileset + ".w3mod/"));
						}
					}
					if (gameDataSet <= 0) {
						gameDataSet = w3iFile.hasFlag(War3MapW3iFlags.MELEE_MAP) ? 2 : 1;
					}
					if (gameDataSet == 1) {
						// Custom data
						dataSources.add(new SubdirDataSource(compoundDataSource,
								"Custom_V" + WarsmashConstants.GAME_VERSION + "\\"));
					}
					else {
						// Melee data
						dataSources.add(new SubdirDataSource(compoundDataSource,
								"Melee_V" + WarsmashConstants.GAME_VERSION + "\\"));
					}
					this.tilesetSource = new CompoundDataSource(dataSources);
				}
				catch (final MPQException e) {
					throw new RuntimeException(e);
				}
			});

			this.loadMapTasks.add(() -> {
				setDataSource(this.tilesetSource);
				War3MapViewer.this.mapMpq.setDataSource(this.tilesetSource);
			});
			this.loadMapTasks.add(() -> {
				War3MapViewer.this.worldEditStrings = new WorldEditStrings(War3MapViewer.this.dataSource);
			});
			this.loadMapTasks.add(() -> {
				loadSLKs(War3MapViewer.this.worldEditStrings);
			});

			this.loadMapTasks.add(() -> {
				War3MapViewer.this.solverParams.tileset = Character.toLowerCase(this.tileset);
			});

			this.loadMapTasks.add(() -> {
				this.terrainData = War3MapViewer.this.mapMpq.readEnvironment();
			});

			this.loadMapTasks.add(() -> {
				this.terrainPathing = War3MapViewer.this.mapMpq.readPathing();
			});

			this.loadMapTasks.add(() -> {
				War3MapViewer.this.terrain = new Terrain(this.terrainData, this.terrainPathing, w3iFile,
						War3MapViewer.this.webGL, War3MapViewer.this.dataSource, War3MapViewer.this.worldEditStrings,
						War3MapViewer.this, War3MapViewer.this.worldEditData);
			});

			this.loadMapTasks.add(() -> {
				final float[] centerOffset = this.terrainData.getCenterOffset();
				final int[] mapSize = this.terrainData.getMapSize();

				War3MapViewer.this.terrainReady = true;
				War3MapViewer.this.anyReady = true;
				War3MapViewer.this.cliffsReady = true;

				// Override the grid based on the map.
				War3MapViewer.this.worldScene.grid = new Grid(centerOffset[0], centerOffset[1],
						(mapSize[0] * 128) - 128, (mapSize[1] * 128) - 128, 16 * 128, 16 * 128);
			});

			this.loadMapTasks.add(() -> {
				final MdxModel confirmation = (MdxModel) load("UI\\Feedback\\Confirmation\\Confirmation.mdx",
						PathSolver.DEFAULT, null);
				War3MapViewer.this.confirmationInstance = (MdxComplexInstance) confirmation.addInstance();
				War3MapViewer.this.confirmationInstance
						.setSequenceLoopMode(SequenceLoopMode.NEVER_LOOP_AND_HIDE_WHEN_DONE);
				War3MapViewer.this.confirmationInstance.setSequence(0);
				War3MapViewer.this.confirmationInstance.setScene(War3MapViewer.this.worldScene);
			});

			this.loadMapTasks.add(() -> {
				if (War3MapViewer.this.preloadedWTS != null) {
					War3MapViewer.this.allObjectData = War3MapViewer.this.mapMpq
							.readModifications(War3MapViewer.this.preloadedWTS);
				}
				else {
					War3MapViewer.this.allObjectData = War3MapViewer.this.mapMpq.readModifications();
				}
			});
			this.loadMapTasks.add(() -> {
				War3MapViewer.this.simulation = new CSimulation(War3MapViewer.this.mapConfig, w3iFile.getVersion(),
						War3MapViewer.this.miscData, War3MapViewer.this.allObjectData.getUnits(),
						War3MapViewer.this.allObjectData.getItems(),
						War3MapViewer.this.allObjectData.getDestructibles(),
						War3MapViewer.this.allObjectData.getAbilities(), War3MapViewer.this.allObjectData.getUpgrades(),
						War3MapViewer.this.allObjectData.getStandardUpgradeEffectMeta(),
						new SimulationRenderController() {
							private final Map<String, UnitSound> keyToCombatSound = new HashMap<>();

							@Override
							public CAttackProjectile createAttackProjectile(final CSimulation simulation,
									final float launchX, final float launchY, final float launchFacing,
									final CUnit source, final CUnitAttackMissile unitAttack, final AbilityTarget target,
									final float damage, final int bounceIndex,
									final CUnitAttackListener attackListener) {
								final War3ID typeId = source.getTypeId();
								final int projectileSpeed = unitAttack.getProjectileSpeed();
								final float projectileArc = unitAttack.getProjectileArc();
								final String missileArt = unitAttack.getProjectileArt();
								final float projectileLaunchX = simulation.getUnitData().getProjectileLaunchX(typeId);
								final float projectileLaunchY = simulation.getUnitData().getProjectileLaunchY(typeId);
								final float projectileLaunchZ = simulation.getUnitData().getProjectileLaunchZ(typeId);

								final float facing = launchFacing;
								final float sinFacing = (float) Math.sin(facing);
								final float cosFacing = (float) Math.cos(facing);
								final float x = launchX + (projectileLaunchY * cosFacing)
										+ (projectileLaunchX * sinFacing);
								final float y = (launchY + (projectileLaunchY * sinFacing))
										- (projectileLaunchX * cosFacing);

								final float height = War3MapViewer.this.terrain.getGroundHeight(x, y)
										+ source.getFlyHeight() + projectileLaunchZ;
								final CAttackProjectile simulationAttackProjectile = new CAttackProjectileMissile(x, y,
										projectileSpeed, target, source, damage, unitAttack, bounceIndex,
										attackListener);

								final MdxModel model = loadModelMdx(missileArt);
								if (model != null) {
									final MdxComplexInstance modelInstance = (MdxComplexInstance) model.addInstance();
									modelInstance.setTeamColor(getRenderPeer(source).playerIndex);
									modelInstance.setScene(War3MapViewer.this.worldScene);
									if (bounceIndex == 0) {
										SequenceUtils.randomBirthSequence(modelInstance);
									}
									else {
										SequenceUtils.randomStandSequence(modelInstance);
									}
									modelInstance.setLocation(x, y, height);
									final RenderProjectile renderAttackProjectile = new RenderProjectile(
											simulationAttackProjectile, modelInstance, height, projectileArc,
											War3MapViewer.this);

									War3MapViewer.this.projectiles.add(renderAttackProjectile);
								}

								return simulationAttackProjectile;
							}

							@Override
							public CAbilityProjectile createProjectile(final CSimulation cSimulation,
									final float launchX, final float launchY, final float launchFacing,
									final float projectileSpeed, final boolean homing, final CUnit source,
									final War3ID spellAlias, final AbilityTarget target,
									final CAbilityProjectileListener projectileListener) {
								final War3ID typeId = source.getTypeId();
								final AbilityUI spellDataUI = War3MapViewer.this.abilityDataUI.getUI(spellAlias);
								final EffectAttachmentUIMissile abilityMissileArt = spellDataUI.getMissileArt(0);
								final String modelPath = abilityMissileArt == null ? ""
										: abilityMissileArt.getModelPath();
								final float projectileArc = abilityMissileArt == null ? 0 : abilityMissileArt.getArc();
								final String missileArt = abilityMissileArt == null ? ""
										: abilityMissileArt.getModelPath();
								final float projectileLaunchX = War3MapViewer.this.simulation.getUnitData()
										.getProjectileLaunchX(typeId);
								final float projectileLaunchY = War3MapViewer.this.simulation.getUnitData()
										.getProjectileLaunchY(typeId);
								final float projectileLaunchZ = War3MapViewer.this.simulation.getUnitData()
										.getProjectileLaunchZ(typeId);

								final float facing = launchFacing;
								final float sinFacing = (float) Math.sin(facing);
								final float cosFacing = (float) Math.cos(facing);
								final float x = launchX + (projectileLaunchY * cosFacing)
										+ (projectileLaunchX * sinFacing);
								final float y = (launchY + (projectileLaunchY * sinFacing))
										- (projectileLaunchX * cosFacing);

								final float height = War3MapViewer.this.terrain.getGroundHeight(x, y)
										+ source.getFlyHeight() + projectileLaunchZ;
								final CAbilityProjectile simulationAbilityProjectile = new CAbilityProjectile(x, y,
										projectileSpeed, target, homing, source, projectileListener);

								final MdxModel model = loadModelMdx(missileArt);
								final MdxComplexInstance modelInstance = (MdxComplexInstance) model.addInstance();

								final RenderUnit renderPeer = getRenderPeer(source);
								modelInstance.setTeamColor(renderPeer.playerIndex);
								modelInstance.setScene(War3MapViewer.this.worldScene);
								SequenceUtils.randomBirthSequence(modelInstance);
								modelInstance.setLocation(x, y, height);
								final RenderProjectile renderProjectile = new RenderProjectile(
										simulationAbilityProjectile, modelInstance, height, projectileArc,
										War3MapViewer.this);

								War3MapViewer.this.projectiles.add(renderProjectile);

								return simulationAbilityProjectile;
							}

							@Override
							public CJassProjectile createJassProjectile(final CSimulation cSimulation,
									final float launchX, final float launchY, final float launchFacing,
									final float projectileSpeed, final boolean homing, final CUnit source,
									final War3ID spellAlias, final AbilityTarget target) {
								final War3ID typeId = source.getTypeId();
								final AbilityUI spellDataUI = War3MapViewer.this.abilityDataUI.getUI(spellAlias);
								final EffectAttachmentUIMissile abilityMissileArt = spellDataUI.getMissileArt(0);
								final float projectileArc = abilityMissileArt == null ? 0 : abilityMissileArt.getArc();
								final String missileArt = abilityMissileArt == null ? ""
										: abilityMissileArt.getModelPath();
								final float projectileLaunchX = War3MapViewer.this.simulation.getUnitData()
										.getProjectileLaunchX(typeId);
								final float projectileLaunchY = War3MapViewer.this.simulation.getUnitData()
										.getProjectileLaunchY(typeId);
								final float projectileLaunchZ = War3MapViewer.this.simulation.getUnitData()
										.getProjectileLaunchZ(typeId);

								final float facing = launchFacing;
								final float sinFacing = (float) Math.sin(facing);
								final float cosFacing = (float) Math.cos(facing);
								final float x = launchX + (projectileLaunchY * cosFacing)
										+ (projectileLaunchX * sinFacing);
								final float y = (launchY + (projectileLaunchY * sinFacing))
										- (projectileLaunchX * cosFacing);

								final float height = War3MapViewer.this.terrain.getGroundHeight(x, y)
										+ source.getFlyHeight() + projectileLaunchZ;
								final CJassProjectile simulationAbilityProjectile = new CJassProjectile(x, y,
										projectileSpeed, target, homing, source);

								final MdxModel model = loadModelMdx(missileArt);
								final MdxComplexInstance modelInstance = (MdxComplexInstance) model.addInstance();

								final RenderUnit renderPeer = getRenderPeer(source);
								modelInstance.setTeamColor(renderPeer.playerIndex);
								modelInstance.setScene(War3MapViewer.this.worldScene);
								SequenceUtils.randomBirthSequence(modelInstance);
								modelInstance.setLocation(x, y, height);
								final RenderProjectile renderProjectile = new RenderProjectile(
										simulationAbilityProjectile, modelInstance, height, projectileArc,
										War3MapViewer.this);

								War3MapViewer.this.projectiles.add(renderProjectile);

								return simulationAbilityProjectile;
							}

							@Override
							public CCollisionProjectile createCollisionProjectile(final CSimulation cSimulation,
									final float launchX, final float launchY, final float launchFacing,
									final float projectileSpeed, final boolean homing, final CUnit source,
									final War3ID spellAlias, final AbilityTarget target, final int maxHits,
									final int hitsPerTarget, final float startingRadius, final float finalRadius,
									final float collisionInterval,
									final CAbilityCollisionProjectileListener projectileListener,
									final boolean provideCounts) {
								final War3ID typeId = source.getTypeId();
								final AbilityUI spellDataUI = War3MapViewer.this.abilityDataUI.getUI(spellAlias);
								final EffectAttachmentUIMissile abilityMissileArt = spellDataUI.getMissileArt(0);
								final float projectileArc = abilityMissileArt == null ? 0 : abilityMissileArt.getArc();
								final String missileArt = abilityMissileArt == null ? ""
										: abilityMissileArt.getModelPath();
								final float projectileLaunchX = War3MapViewer.this.simulation.getUnitData()
										.getProjectileLaunchX(typeId);
								final float projectileLaunchY = War3MapViewer.this.simulation.getUnitData()
										.getProjectileLaunchY(typeId);
								final float projectileLaunchZ = War3MapViewer.this.simulation.getUnitData()
										.getProjectileLaunchZ(typeId);

								final float facing = launchFacing;
								final float sinFacing = (float) Math.sin(facing);
								final float cosFacing = (float) Math.cos(facing);
								final float x = launchX + (projectileLaunchY * cosFacing)
										+ (projectileLaunchX * sinFacing);
								final float y = (launchY + (projectileLaunchY * sinFacing))
										- (projectileLaunchX * cosFacing);

								final float height = War3MapViewer.this.terrain.getGroundHeight(x, y)
										+ source.getFlyHeight() + projectileLaunchZ;
								final CCollisionProjectile simulationAbilityProjectile = new CCollisionProjectile(x, y,
										projectileSpeed, target, homing, source, maxHits, hitsPerTarget, startingRadius,
										finalRadius, collisionInterval, projectileListener, provideCounts);

								final MdxModel model = loadModelMdx(missileArt);
								if (model != null) {
									final MdxComplexInstance modelInstance = (MdxComplexInstance) model.addInstance();

									final RenderUnit renderPeer = getRenderPeer(source);
									modelInstance.setTeamColor(renderPeer.playerIndex);
									modelInstance.setScene(War3MapViewer.this.worldScene);
									SequenceUtils.randomBirthSequence(modelInstance);
									modelInstance.setLocation(x, y, height);
									final RenderProjectile renderProjectile = new RenderProjectile(
											simulationAbilityProjectile, modelInstance, height, projectileArc,
											War3MapViewer.this);

									War3MapViewer.this.projectiles.add(renderProjectile);
								}

								return simulationAbilityProjectile;
							}

							@Override
							public CPsuedoProjectile createPseudoProjectile(final CSimulation cSimulation,
									final float launchX, final float launchY, final float launchFacing,
									final float projectileSpeed, final float projectileStepInterval,
									final int projectileArtSkip, final boolean homing, final CUnit source,
									final War3ID spellAlias, final CEffectType effectType, final int effectArtIndex,
									final AbilityTarget target, final int maxHits, final int hitsPerTarget,
									final float startingRadius, final float finalRadius,
									final CAbilityCollisionProjectileListener projectileListener,
									final boolean provideCounts) {

								final War3ID typeId = source.getTypeId();
								final float projectileLaunchX = War3MapViewer.this.simulation.getUnitData()
										.getProjectileLaunchX(typeId);
								final float projectileLaunchY = War3MapViewer.this.simulation.getUnitData()
										.getProjectileLaunchY(typeId);

								final float facing = launchFacing;
								final float sinFacing = (float) Math.sin(facing);
								final float cosFacing = (float) Math.cos(facing);
								final float x = launchX + (projectileLaunchY * cosFacing)
										+ (projectileLaunchX * sinFacing);
								final float y = (launchY + (projectileLaunchY * sinFacing))
										- (projectileLaunchX * cosFacing);

								final CPsuedoProjectile simulationAbilityProjectile = new CPsuedoProjectile(x, y,
										projectileSpeed, projectileStepInterval, projectileArtSkip, target, homing,
										source, spellAlias, effectType, effectArtIndex, maxHits, hitsPerTarget,
										startingRadius, finalRadius, projectileListener, provideCounts);

								return simulationAbilityProjectile;
							}

							@Override
							public void createInstantAttackEffect(final CSimulation cSimulation, final CUnit source,
									final CUnitAttackInstant unitAttack, final CWidget target) {
								final War3ID typeId = source.getTypeId();

								final String missileArt = unitAttack.getProjectileArt();
								final float projectileLaunchX = War3MapViewer.this.simulation.getUnitData()
										.getProjectileLaunchX(typeId);
								final float projectileLaunchY = War3MapViewer.this.simulation.getUnitData()
										.getProjectileLaunchY(typeId);
								final float facing = (float) Math.toRadians(source.getFacing());
								final float sinFacing = (float) Math.sin(facing);
								final float cosFacing = (float) Math.cos(facing);
								final float x = source.getX() + (projectileLaunchY * cosFacing)
										+ (projectileLaunchX * sinFacing);
								final float y = (source.getY() + (projectileLaunchY * sinFacing))
										- (projectileLaunchX * cosFacing);

								final float targetX = target.getX();
								final float targetY = target.getY();
								final float angleToTarget = (float) Math.atan2(targetY - y, targetX - x);

								final float height = War3MapViewer.this.terrain.getGroundHeight(targetX, targetY)
										+ target.getFlyHeight() + target.getImpactZ();

								final MdxModel model = loadModelMdx(missileArt);
								final MdxComplexInstance modelInstance = (MdxComplexInstance) model.addInstance();
								modelInstance.setTeamColor(source.getPlayerIndex());
								SequenceUtils.randomBirthSequence(modelInstance);
								modelInstance.setLocation(targetX, targetY, height);
								modelInstance.setScene(War3MapViewer.this.worldScene);
								War3MapViewer.this.projectiles
										.add(new RenderAttackInstant(modelInstance, War3MapViewer.this, angleToTarget));
							}

							@Override
							public SimulationRenderComponentLightning createLightning(final CSimulation simulation,
									final War3ID lightningId, final CUnit source, final CUnit target) {
								final RenderUnit renderPeerSource = War3MapViewer.this.getRenderPeer(source);
								final RenderWidget renderPeerTarget = War3MapViewer.this.getRenderPeer(target);
								return War3MapViewer.this.createLightning(lightningId, renderPeerSource,
										renderPeerTarget);
							}

							@Override
							public SimulationRenderComponentLightning createLightning(final CSimulation simulation,
									final War3ID lightningId, final CUnit source, final CUnit target,
									final Float duration) {
								final RenderUnit renderPeerSource = War3MapViewer.this.getRenderPeer(source);
								final RenderWidget renderPeerTarget = War3MapViewer.this.getRenderPeer(target);
								return War3MapViewer.this.createLightning(lightningId, renderPeerSource,
										renderPeerTarget, duration);
							}

							@Override
							public SimulationRenderComponentLightning createAbilityLightning(
									final CSimulation simulation, final War3ID lightningId, final CUnit source,
									final CUnit target, final int index) {
								final RenderUnit renderPeerSource = War3MapViewer.this.getRenderPeer(source);
								final RenderWidget renderPeerTarget = War3MapViewer.this.getRenderPeer(target);
								final List<War3ID> lightnings = getLightningEffectList(lightningId);
								return War3MapViewer.this.createLightning(lightnings.get(index), renderPeerSource,
										renderPeerTarget);
							}

							@Override
							public SimulationRenderComponentLightning createAbilityLightning(
									final CSimulation simulation, final War3ID lightningId, final CUnit source,
									final CUnit target, final int index, final Float duration) {
								final RenderUnit renderPeerSource = War3MapViewer.this.getRenderPeer(source);
								final RenderWidget renderPeerTarget = War3MapViewer.this.getRenderPeer(target);
								final List<War3ID> lightnings = getLightningEffectList(lightningId);
								return War3MapViewer.this.createLightning(lightnings.get(index), renderPeerSource,
										renderPeerTarget, duration);
							}

							@Override
							public void spawnDamageSound(final CWidget damagedDestructable, final String weaponSound,
									final String armorType) {
								final RenderWidget damagedWidget = War3MapViewer.this
										.getRenderPeer(damagedDestructable);
								if (damagedWidget == null) {
									return;
								}
								final String key = weaponSound + armorType;
								UnitSound combatSound = this.keyToCombatSound.get(key);
								if (combatSound == null) {
									combatSound = UnitSound.create(War3MapViewer.this.dataSource,
											War3MapViewer.this.unitCombatSoundsTable, weaponSound, armorType);
									this.keyToCombatSound.put(key, combatSound);
								}
								combatSound.play(War3MapViewer.this.worldScene.audioContext, damagedDestructable.getX(),
										damagedDestructable.getY(), damagedWidget.getZ());
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
							public void unitUpgradingEvent(final CUnit unit, final War3ID upgradeIdType) {
//						final RenderUnit renderUnit = War3MapViewer.this.unitToRenderPeer.get(unit);
								final EnumSet<SecondaryTag> originalRequiredAnimationNames = War3MapViewer.this.renderUnitTypeData
										.get(unit.getTypeId()).getRequiredAnimationNames();
								boolean changedAnimationProperties = false;
								for (final SecondaryTag secondaryTag : originalRequiredAnimationNames) {
									if (unit.getUnitAnimationListener().removeSecondaryTag(secondaryTag)) {
										changedAnimationProperties = true;
									}
								}
								final EnumSet<SecondaryTag> requiredAnimationNames = War3MapViewer.this.renderUnitTypeData
										.get(upgradeIdType).getRequiredAnimationNames();
								for (final SecondaryTag secondaryTag : requiredAnimationNames) {
									if (unit.getUnitAnimationListener().addSecondaryTag(secondaryTag)) {
										changedAnimationProperties = true;
									}
								}

								if (changedAnimationProperties) {
									unit.getUnitAnimationListener().forceResetCurrentAnimation();
								}
							}

							@Override
							public void unitCancelUpgradingEvent(final CUnit unit, final War3ID upgradeIdType) {
								final EnumSet<SecondaryTag> requiredAnimationNames = War3MapViewer.this.renderUnitTypeData
										.get(upgradeIdType).getRequiredAnimationNames();
								boolean changedAnimationProperties = false;
								for (final SecondaryTag secondaryTag : requiredAnimationNames) {
									if (unit.getUnitAnimationListener().removeSecondaryTag(secondaryTag)) {
										changedAnimationProperties = true;
									}
								}
								final EnumSet<SecondaryTag> originalRequiredAnimationNames = War3MapViewer.this.renderUnitTypeData
										.get(unit.getTypeId()).getRequiredAnimationNames();
								for (final SecondaryTag secondaryTag : originalRequiredAnimationNames) {
									if (unit.getUnitAnimationListener().addSecondaryTag(secondaryTag)) {
										changedAnimationProperties = true;
									}
								}

								if (changedAnimationProperties) {
									unit.getUnitAnimationListener().forceResetCurrentAnimation();
								}
							}

							@Override
							public void removeUnit(final CUnit unit) {
								final RenderUnit renderUnit = War3MapViewer.this.unitToRenderPeer.remove(unit);
								if (renderUnit == null) {
									System.err.println("Attempted to remove a null unit!");
									return;
								}
								War3MapViewer.this.widgets.remove(renderUnit);
								War3MapViewer.this.units.remove(renderUnit);
								War3MapViewer.this.worldScene.removeInstance(renderUnit.instance);
								renderUnit.onRemove(War3MapViewer.this);
							}

							@Override
							public void removeDestructable(final CDestructable dest) {
								final RenderDestructable renderPeer = War3MapViewer.this.destructableToRenderPeer
										.remove(dest);
								War3MapViewer.this.worldScene.removeInstance(renderPeer.instance);
								if (renderPeer.walkableBounds != null) {
									War3MapViewer.this.walkableObjectsTree.remove(
											(MdxComplexInstance) renderPeer.instance, renderPeer.walkableBounds);
								}
							}

							@Override
							public BufferedImage getBuildingPathingPixelMap(final War3ID rawcode) {
								return War3MapViewer.this.renderUnitTypeData.get(rawcode).getBuildingPathingPixelMap();
							}

							@Override
							public BufferedImage getDestructablePathingDeathPixelMap(final War3ID rawcode) {
								return War3MapViewer.this.getDestructablePathingDeathPixelMap(
										War3MapViewer.this.allObjectData.getDestructibles().get(rawcode));
							}

							@Override
							public BufferedImage getDestructablePathingPixelMap(final War3ID rawcode) {
								return War3MapViewer.this.getDestructablePathingPixelMap(
										War3MapViewer.this.allObjectData.getDestructibles().get(rawcode));
							}

							@Override
							public void spawnUnitConstructionFinishSound(final CUnit constructedStructure) {
								final UnitSound constructingBuilding = War3MapViewer.this.uiSounds
										.getSound(War3MapViewer.this.gameUI.getSkinField("JobDoneSound"));
								final RenderUnit renderUnit = War3MapViewer.this.unitToRenderPeer
										.get(constructedStructure);
								if ((constructingBuilding != null) && (renderUnit.getSimulationUnit()
										.getPlayerIndex() == War3MapViewer.this.localPlayerIndex)) {
									constructingBuilding.play(War3MapViewer.this.worldScene.audioContext,
											constructedStructure.getX(), constructedStructure.getY(),
											renderUnit.getZ());
								}
							}

							@Override
							public void spawnUnitUpgradeFinishSound(final CUnit constructedStructure) {
								final UnitSound constructingBuilding = War3MapViewer.this.uiSounds
										.getSound(War3MapViewer.this.gameUI.getSkinField("UpgradeComplete"));
								final RenderUnit renderUnit = War3MapViewer.this.unitToRenderPeer
										.get(constructedStructure);
								if ((constructingBuilding != null) && (renderUnit.getSimulationUnit()
										.getPlayerIndex() == War3MapViewer.this.localPlayerIndex)) {
									constructingBuilding.play(War3MapViewer.this.worldScene.audioContext,
											constructedStructure.getX(), constructedStructure.getY(),
											renderUnit.getZ());
								}
							}

							@Override
							public CUnit createUnit(final CSimulation simulation, final War3ID typeId,
									final int playerIndex, final float x, final float y, final float facing) {
								return (CUnit) createNewUnit(War3MapViewer.this.allObjectData, typeId, x, y,
										playerIndex, playerIndex, (float) Math.toRadians(facing));
							}

							@Override
							public CDestructable createDestructable(final War3ID typeId, final float x, final float y,
									final float facing, final float scale, final int variation) {
								return createDestructableZ(typeId, x, y,
										Math.max(getWalkableRenderHeight(x, y),
												War3MapViewer.this.terrain.getGroundHeight(x, y)),
										facing, scale, variation);
							}

							@Override
							public CDestructable createDestructableZ(final War3ID typeId, final float x, final float y,
									final float z, final float facing, final float scale, final int variation) {
								final GameObject row = War3MapViewer.this.allObjectData.getDestructibles().get(typeId);
								final float[] location3d = { x, y, z };
								final float[] scale3d = { scale, scale, scale };
								final RenderDestructable newDestructable = createNewDestructable(typeId, row, variation,
										location3d, (float) Math.toRadians(facing), (short) 100, scale3d);
								return newDestructable.getSimulationDestructable();
							}

							@Override
							public CItem createItem(final CSimulation simulation, final War3ID typeId, final float x,
									final float y) {
								return (CItem) createNewUnit(War3MapViewer.this.allObjectData, typeId, x, y, -1, -1,
										(float) Math.toRadians(War3MapViewer.this.simulation.getGameplayConstants()
												.getBuildingAngle()));
							}

							@Override
							public void spawnDeathExplodeEffect(final CUnit source,
									final War3ID explodesOnDeathBuffId) {
								final RenderUnit renderUnit = War3MapViewer.this.unitToRenderPeer.get(source);
								MdxComplexInstance modelInstance = null;
								if (explodesOnDeathBuffId != null) {
									final EffectAttachmentUI effectAttachmentUI = getEffectAttachmentUI(
											explodesOnDeathBuffId, CEffectType.EFFECT, 0);
									final String modelPath = effectAttachmentUI.getModelPath();
									final MdxModel spawnedEffectModel = loadModelMdx(modelPath);
									if (spawnedEffectModel != null) {
										modelInstance = (MdxComplexInstance) spawnedEffectModel.addInstance();
									}
								}
								if ((modelInstance == null) && (renderUnit.specialArtModel != null)) {
									modelInstance = (MdxComplexInstance) renderUnit.specialArtModel.addInstance();
								}
								if (modelInstance != null) {
									modelInstance.setTeamColor(source.getPlayerIndex());
									modelInstance.setLocation(renderUnit.location);
									modelInstance.setScene(War3MapViewer.this.worldScene);
									SequenceUtils.randomBirthSequence(modelInstance);
									War3MapViewer.this.projectiles.add(new RenderAttackInstant(modelInstance,
											War3MapViewer.this,
											(float) Math.toRadians(renderUnit.getSimulationUnit().getFacing())));
								}
							}

							@Override
							public void spawnGainLevelEffect(final CUnit source) {
								final AbilityUI heroUI = War3MapViewer.this.abilityDataUI.getUI(ABILITY_HERO_RAWCODE);
								final RenderUnit renderUnit = War3MapViewer.this.unitToRenderPeer.get(source);
								final String heroLevelUpArt = heroUI.getCasterArt(0).getModelPath();
								// TODO use addSpellEffectTarget
								spawnFxOnOrigin(renderUnit, heroLevelUpArt);
							}

							@Override
							public void heroRevived(final CUnit source) {
								final AbilityUI reviveUI = War3MapViewer.this.abilityDataUI
										.getUI(ABILITY_REVIVE_RAWCODE);
								final RenderUnit renderUnit = War3MapViewer.this.unitToRenderPeer.get(source);
								renderUnit.instance.additiveOverrideMeshMode = false;
								renderUnit.instance.setVertexAlpha(1.0f);
								final CPlayer player = War3MapViewer.this.simulation.getPlayer(source.getPlayerIndex());
								final String heroReviveArt = reviveUI.getTargetArt(player.getRace().ordinal())
										.getModelPath();
								// TODO use addSpellEffectTarget, maybe?
								spawnFxOnOrigin(renderUnit, heroReviveArt);

								// Recreate unit shadow.... is needed here
								renderUnit.shadow = createUnitShadow(renderUnit.getTypeData().getRenderShadowType(),
										source.getX(), source.getY());
							}

							@Override
							public void heroDeathEvent(final CUnit source) {
								final RenderUnit renderUnit = War3MapViewer.this.unitToRenderPeer.get(source);
								renderUnit.instance.additiveOverrideMeshMode = true;
							}

							@Override
							public void spawnEffectOnUnit(final CUnit unit, final String effectPath) {
								final RenderSpellEffect spellEffect = addSpecialEffectTarget(effectPath, unit, "");
								if (spellEffect != null) {
									spellEffect.setKillWhenDone(true);
								}
							}

							@Override
							public void spawnTemporarySpellEffectOnUnit(final CUnit unit, final War3ID alias,
									final CEffectType effectType) {
								final RenderSpellEffect spellEffect = spawnSpellEffectOnUnitEx(unit, alias, effectType,
										0);
								if (spellEffect != null) {
									spellEffect.setKillWhenDone(true);
								}
							}

							@Override
							public SimulationRenderComponentModel spawnPersistentSpellEffectOnUnit(final CUnit unit,
									final War3ID alias, final CEffectType effectType) {
								final List<RenderSpellEffect> specialEffects = spawnSpellEffectOnUnitEx(unit, alias,
										effectType);
								if ((specialEffects == null) || specialEffects.isEmpty()) {
									return SimulationRenderComponentModel.DO_NOTHING;
								}
								return new SimulationRenderComponentModel() {
									@Override
									public void remove() {
										for (final RenderSpellEffect effect : specialEffects) {
											effect.setAnimations(RenderSpellEffect.DEATH_ONLY, true);
										}
									}

									@Override
									public void setHeight(final float height) {
										for (final RenderSpellEffect effect : specialEffects) {
											effect.setHeight(height);
										}
									}
								};
							}

							@Override
							public SimulationRenderComponentModel spawnPersistentSpellEffectOnUnit(final CUnit unit,
									final War3ID alias, final CEffectType effectType, final int index) {
								final RenderSpellEffect specialEffect = spawnSpellEffectOnUnitEx(unit, alias,
										effectType, index);
								if (specialEffect == null) {
									return SimulationRenderComponentModel.DO_NOTHING;
								}
								return new SimulationRenderComponentModel() {
									@Override
									public void remove() {
										specialEffect.setAnimations(RenderSpellEffect.DEATH_ONLY, true);
									}

									@Override
									public void setHeight(final float height) {
										specialEffect.setHeight(height);
									}
								};
							}

							@Override
							public SimulationRenderComponentModel createSpellEffectOverDestructable(final CUnit source,
									final CDestructable target, final War3ID alias, final float artAttachmentHeight) {
								final AbilityUI abilityUI = War3MapViewer.this.abilityDataUI.getUI(alias);
								final String effectPath = abilityUI.getTargetArt(0).getModelPath();
								// TODO use addSpellEffectTarget maybe?
								final RenderDestructable renderDestructable = War3MapViewer.this.destructableToRenderPeer
										.get(target);
								final MdxModel spawnedEffectModel = loadModelMdx(effectPath);
								if (spawnedEffectModel != null) {
									final MdxComplexInstance modelInstance = (MdxComplexInstance) spawnedEffectModel
											.addInstance();
									modelInstance.setTeamColor(War3MapViewer.this.simulation
											.getPlayer(source.getPlayerIndex()).getColor());
									final float startingHeight = renderDestructable.getZ() + artAttachmentHeight;
									modelInstance.setLocation(renderDestructable.getX(), renderDestructable.getY(),
											startingHeight);
									modelInstance.setScene(War3MapViewer.this.worldScene);
									final RenderSpellEffect renderAttackInstant = new RenderSpellEffect(modelInstance,
											War3MapViewer.this, 0, RenderSpellEffect.STAND_ONLY, SequenceUtils.EMPTY);
									renderAttackInstant.setAnimations(RenderSpellEffect.STAND_ONLY, false);
									War3MapViewer.this.projectiles.add(renderAttackInstant);
									return new SimulationRenderComponentModel() {
										@Override
										public void remove() {
											renderAttackInstant.setAnimations(RenderSpellEffect.DEATH_ONLY, true);
										}

										@Override
										public void setHeight(final float height) {
											renderAttackInstant.setHeight(startingHeight + height);
										}
									};
								}
								return null;
							}

							@Override
							public SimulationRenderComponentModel spawnSpellEffectOnPoint(final float x, final float y,
									final float facing, final War3ID alias, final CEffectType effectType,
									final int index) {
								final RenderSpellEffect specialEffect = spawnSpellEffectEx(x, y, facing, alias,
										effectType, index);
								if (specialEffect == null) {
									return SimulationRenderComponentModel.DO_NOTHING;
								}
								return new SimulationRenderComponentModel() {
									@Override
									public void remove() {
										specialEffect.setAnimations(RenderSpellEffect.DEATH_ONLY, true);
									}

									@Override
									public void setHeight(final float height) {
										specialEffect.setHeight(Math.max(getWalkableRenderHeight(x, y),
												War3MapViewer.this.terrain.getGroundHeight(x, y)) + height);
									}
								};
							}

							@Override
							public void spawnTemporarySpellEffectOnPoint(final float x, final float y,
									final float facing, final War3ID alias, final CEffectType effectType,
									final int index) {
								final RenderSpellEffect specialEffect = spawnSpellEffectEx(x, y, facing, alias,
										effectType, index);
								if (specialEffect != null) {
									specialEffect.setKillWhenDone(true);
								}
							}

							@Override
							public void spawnUnitReadySound(final CUnit trainedUnit) {
								if (trainedUnit.getPlayerIndex() == War3MapViewer.this.localPlayerIndex) {
									final RenderUnit renderPeer = War3MapViewer.this.unitToRenderPeer.get(trainedUnit);
									renderPeer.soundset.ready
											.playUnitResponse(War3MapViewer.this.worldScene.audioContext, renderPeer);
								}
							}

							@Override
							public void unitRepositioned(final CUnit cUnit) {
								final RenderUnit renderPeer = War3MapViewer.this.unitToRenderPeer.get(cUnit);
								renderPeer.repositioned(War3MapViewer.this);
							}

							@Override
							public void unitUpdatedType(final CUnit simulationUnit, final War3ID typeId) {
								final RenderUnit renderPeer = War3MapViewer.this.unitToRenderPeer.get(simulationUnit);

								final RenderUnitType typeData = getUnitTypeData(typeId);
								final int customTeamColor = War3MapViewer.this.simulation
										.getPlayer(simulationUnit.getPlayerIndex()).getColor();
								final float unitX = simulationUnit.getX();
								final float unitY = simulationUnit.getY();
								final float unitZ = Math.max(getWalkableRenderHeight(unitX, unitY),
										War3MapViewer.this.terrain.getGroundHeight(unitX, unitY))
										+ simulationUnit.getFlyHeight();

								BuildingShadow buildingShadowInstance = null;
								final String buildingShadow = typeData.getBuildingShadow();
								if (buildingShadow != null) {
									buildingShadowInstance = War3MapViewer.this.terrain.addShadow(buildingShadow, unitX,
											unitY);
								}

								if (renderPeer.shadow != null) {
									renderPeer.shadow.destroy(Gdx.gl30, War3MapViewer.this.terrain.centerOffset);
									renderPeer.shadow = null;
								}
								renderPeer.shadow = createUnitShadow(typeData.getRenderShadowType(), unitX, unitY);

								renderPeer.resetRenderUnit(War3MapViewer.this, unitX, unitY, unitZ,
										customTeamColor /* renderPeer.playerIndex */, simulationUnit, typeData,
										buildingShadowInstance, War3MapViewer.this.selectionCircleScaleFactor,
										typeData.getAnimationWalkSpeed(), typeData.getAnimationRunSpeed(),
										typeData.getScalingValue());
							}

							@Override
							public void changeUnitColor(final CUnit unit, final int playerIndex) {
								final RenderUnit renderPeer = War3MapViewer.this.unitToRenderPeer.get(unit);
								renderPeer.setPlayerColor(
										War3MapViewer.this.simulation.getPlayer(playerIndex).getColor());
							}

							@Override
							public void changeUnitVertexColor(final CUnit unit, final Color color) {
								final RenderUnit renderPeer = War3MapViewer.this.unitToRenderPeer.get(unit);
								renderPeer.setVertexColoring(War3MapViewer.this, color);
							}

							@Override
							public void changeUnitVertexColor(final CUnit unit, final float r, final float g,
									final float b) {
								final RenderUnit renderPeer = War3MapViewer.this.unitToRenderPeer.get(unit);
								renderPeer.setVertexColoring(War3MapViewer.this, r, g, b);
							}

							@Override
							public void changeUnitVertexColor(final CUnit unit, final float r, final float g,
									final float b, final float a) {
								final RenderUnit renderPeer = War3MapViewer.this.unitToRenderPeer.get(unit);
								renderPeer.setVertexColoring(War3MapViewer.this, r, g, b, a);
							}

							@Override
							public float[] getUnitVertexColor(final CUnit unit) {
								final RenderUnit renderPeer = War3MapViewer.this.unitToRenderPeer.get(unit);
								return renderPeer.getVertexColoring();
							}

							@Override
							public TextTag spawnTextTag(final CUnit unit, final TextTagConfigType configType,
									final int displayAmount) {
								final RenderUnit renderPeer = War3MapViewer.this.unitToRenderPeer.get(unit);
								final TextTagConfig textTagConfig = getTextTagConfig(configType.getKey());
								String text;
								switch (configType) {
								case GOLD:
								case GOLD_BOUNTY:
								case LUMBER:
								case LUMBER_BOUNTY:
								case XP: {
									text = "+" + displayAmount;
									break;
								}
								case MISS_TEXT:
									text = "miss!";
									break;
								default:
								case MANA_BURN:
								case CRITICAL_STRIKE:
								case SHADOW_STRIKE:
								case BASH: {
									text = displayAmount + "!";
									break;
								}
								}
								final Vector3 unitPosition = new Vector3(renderPeer.location);
								final Bounds bounds = renderPeer.instance.getBounds();
								final TextTag textTag = new TextTag(unitPosition, new Vector2(0, 60f), text,
										textTagConfig.getColor(), textTagConfig.getLifetime(),
										textTagConfig.getFadeStart(), textTagConfig.getHeight(),
										BUILTIN_TEXT_TAG_REPORTED_HANDLE_ID);
								War3MapViewer.this.textTags.add(textTag);
								return textTag;
							}

							@Override
							public TextTag spawnTextTag(final CUnit unit, final TextTagConfigType configType,
									final String message) {
								final RenderUnit renderPeer = War3MapViewer.this.unitToRenderPeer.get(unit);
								final TextTagConfig textTagConfig = getTextTagConfig(configType.getKey());
								final Vector3 unitPosition;
								if (renderPeer != null) {
									unitPosition = new Vector3(renderPeer.location);
								}
								else if (unit != null) {
									// try to fudge it and spawn in place of stale unit
									unitPosition = new Vector3(unit.getX(), unit.getY(),
											War3MapViewer.this.terrain.getGroundHeight(unit.getX(), unit.getY()));
								}
								else {
									unitPosition = new Vector3(0, 0, 0);
								}
								final TextTag textTag = new TextTag(unitPosition, new Vector2(0, 60f), message,
										textTagConfig.getColor(), textTagConfig.getLifetime(),
										textTagConfig.getFadeStart(), textTagConfig.getHeight(),
										BUILTIN_TEXT_TAG_REPORTED_HANDLE_ID);
								War3MapViewer.this.textTags.add(textTag);
								return textTag;
							}

							int nextUserTextTagId = 0;

							@Override
							public TextTag createTextTag() {
								// Read online in the jassdoc that the API expects us
								// to be limited to 100 text tags, and that they return
								// from 0 to 99 as their handle ID. At the moment,
								// we're using the same class for builtins and user tags,
								// so they are async and would be desync prone in jass.
								// Please don't rely on TextTag handle ID in Warsmash,
								// but if you attempt to do so, builtins report -1 and
								// custom tags report an ever-increasing unique number
								// for now (see below)
								final TextTag genericTextTag = new TextTag(new Vector3(Vector3.Zero), new Vector2(0, 0),
										"", Color.WHITE, 100, 0, DEFAULT_TEXT_TAG_HEIGHT, this.nextUserTextTagId++);
								genericTextTag.setPermanent(true);
								War3MapViewer.this.textTags.add(genericTextTag);
								return genericTextTag;
							}

							@Override
							public void destroyTextTag(final TextTag textTag) {
								War3MapViewer.this.textTags.remove(textTag);
							}

							@Override
							public void spawnUIUnitGetItemSound(final CUnit cUnit, final CItem item) {
								final RenderUnit renderPeer = War3MapViewer.this.unitToRenderPeer.get(cUnit);
								if (localPlayerIndex == renderPeer.getSimulationUnit().getPlayerIndex()) {
									War3MapViewer.this.uiSounds.getSound("ItemGet").play(
											War3MapViewer.this.worldScene.audioContext, renderPeer.getX(),
											renderPeer.getY(), renderPeer.getZ());
								}
							}

							@Override
							public void spawnUIUnitDropItemSound(final CUnit cUnit, final CItem item) {
								final RenderUnit renderPeer = War3MapViewer.this.unitToRenderPeer.get(cUnit);
								if (localPlayerIndex == renderPeer.getSimulationUnit().getPlayerIndex()) {
									War3MapViewer.this.uiSounds.getSound("ItemDrop").play(
											War3MapViewer.this.worldScene.audioContext, renderPeer.getX(),
											renderPeer.getY(), renderPeer.getZ());
								}
							}

							@Override
							public SimulationRenderComponent spawnAbilitySoundEffect(final CUnit caster,
									final War3ID alias) {
								final RenderUnit renderPeer = War3MapViewer.this.unitToRenderPeer.get(caster);
								final AbilityUI abilityUi = War3MapViewer.this.abilityDataUI.getUI(alias);
								if ((abilityUi == null) || (abilityUi.getEffectSound() == null)) {
									return SimulationRenderComponent.DO_NOTHING;
								}
								final long soundId = War3MapViewer.this.uiSounds.getSound(abilityUi.getEffectSound())
										.play(War3MapViewer.this.worldScene.audioContext, renderPeer.getX(),
												renderPeer.getY(), renderPeer.getZ());
								if (soundId == -1) {
									return SimulationRenderComponent.DO_NOTHING;
								}
								return new SimulationRenderComponent() {
									@Override
									public void remove() {
										War3MapViewer.this.uiSounds.getSound(abilityUi.getEffectSoundLooped())
												.stop(soundId);
										;
									}
								};
							}

							@Override
							public SimulationRenderComponent loopAbilitySoundEffect(final CUnit caster,
									final War3ID alias) {
								final RenderUnit renderPeer = War3MapViewer.this.unitToRenderPeer.get(caster);
								final AbilityUI abilityUi = War3MapViewer.this.abilityDataUI.getUI(alias);
								if ((abilityUi == null) || (abilityUi.getEffectSound() == null)) {
									return SimulationRenderComponent.DO_NOTHING;
								}
								final long soundId = War3MapViewer.this.uiSounds
										.getSound(abilityUi.getEffectSoundLooped())
										.play(War3MapViewer.this.worldScene.audioContext, renderPeer.getX(),
												renderPeer.getY(), renderPeer.getZ(), true);
								if (soundId == -1) {
									return SimulationRenderComponent.DO_NOTHING;
								}
								return new SimulationRenderComponent() {
									@Override
									public void remove() {
										War3MapViewer.this.uiSounds.getSound(abilityUi.getEffectSoundLooped())
												.stop(soundId);
										;
									}
								};
							}

							@Override
							public void stopAbilitySoundEffect(final CUnit caster, final War3ID alias) {
								final RenderUnit renderPeer = War3MapViewer.this.unitToRenderPeer.get(caster);
								final AbilityUI abilityUi = War3MapViewer.this.abilityDataUI.getUI(alias);
								if (abilityUi.getEffectSoundLooped() != null) {
									// TODO below this probably stops all instances of the sound, which is silly
									// and busted. Would be better to keep a notion of sound instance
									War3MapViewer.this.uiSounds.getSound(abilityUi.getEffectSoundLooped()).stop();
								}
							}

							@Override
							public void unitPreferredSelectionReplacement(final CUnit oldUnit, final CUnit newUnit) {
								final RenderUnit oldRenderPeer = War3MapViewer.this.unitToRenderPeer.get(oldUnit);
								final RenderUnit newRenderPeer = War3MapViewer.this.unitToRenderPeer.get(newUnit);
								oldRenderPeer.setPreferredSelectionReplacement(newRenderPeer);

							}

							@Override
							public void setBlight(final float x, final float y, final float radius,
									final boolean blighted) {
								War3MapViewer.this.setBlight(x, y, radius, blighted);
							}

							@Override
							public int getTerrainHeight(final float x, final float y) {
								final RenderCorner corner = War3MapViewer.this.terrain.getCorner(x, y);
								return corner == null ? 999 : corner.getLayerHeight();
							}

							@Override
							public boolean isTerrainRomp(final float x, final float y) {
								final RenderCorner corner = War3MapViewer.this.terrain.getCorner(x, y);
								return corner == null ? false : corner.romp;
							}

							@Override
							public boolean isTerrainWater(final float x, final float y) {
								final RenderCorner corner = War3MapViewer.this.terrain.getCorner(x, y);
								return corner == null ? false : corner.getWater() != 0;
							}

							@Override
							public void changeUnitPlayerColor(CUnit unit, int previousColor, int newColor) {
								final RenderUnit renderUnit = getRenderPeer(unit);
								if (renderUnit.playerIndex == previousColor) {
									renderUnit.setPlayerColor(newColor);
								}

							}
						}, War3MapViewer.this.terrain.pathingGrid, War3MapViewer.this.terrain.getEntireMap(),
						War3MapViewer.this.seededRandom, War3MapViewer.this.commandErrorListener);
			});

			this.loadMapTasks.add(() -> {
				War3MapViewer.this.walkableObjectsTree = new Quadtree<>(War3MapViewer.this.terrain.getEntireMap());
				if (War3MapViewer.this.doodadsAndDestructiblesLoaded) {
					loadDoodadsAndDestructibles(War3MapViewer.this.allObjectData, w3iFile);
				}
				else {
					throw new IllegalStateException(
							"transcription of JS has not loaded a map and has no JS async promises");
				}
			});

			this.loadMapTasks.add(() -> {
				loadSounds();
			});

			this.loadMapTasks.add(() -> {
				War3MapViewer.this.terrain.createWaves();
			});

			this.startingTaskCount = this.loadMapTasks.size();
		}

		public boolean process() throws IOException {
			final LoadMapTask nextTask = this.loadMapTasks.pollFirst();
			nextTask.run();
			return this.loadMapTasks.isEmpty();
		}

		public void addLoadTask(final LoadMapTask task) {
			this.loadMapTasks.add(task);
		}

		public float getCompletionRatio() {
			return 1.0f - (this.loadMapTasks.size() / (float) this.startingTaskCount);
		}
	}

	public static interface LoadMapTask {
		void run() throws IOException;
	}

	public float getElapsedSecondsForRender(final CTimer timer) {
		return Math.min(timer.getElapsed(this.simulation) + this.updateTime, timer.getTimeoutTime());
	}

	public float getRemainingSecondsForRender(final CTimer timer) {
		return timer.getTimeoutTime() - getElapsedSecondsForRender(timer);
	}

	public void resetTerrainFog() {
		if (this.defaultFogSettings != null) {
			this.worldScene.fogSettings.color = this.defaultFogSettings.color;
			this.worldScene.fogSettings.style = this.defaultFogSettings.style;
			this.worldScene.fogSettings.density = this.defaultFogSettings.density;
			this.worldScene.fogSettings.start = this.defaultFogSettings.start;
			this.worldScene.fogSettings.end = this.defaultFogSettings.end;
		}
	}
}
