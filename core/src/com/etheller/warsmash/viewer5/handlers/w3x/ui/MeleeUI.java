package com.etheller.warsmash.viewer5.handlers.w3x.ui;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Blending;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.glutils.PixmapTextureData;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.warsmash.datasources.DataSource;
import com.etheller.warsmash.parsers.fdf.GameUI;
import com.etheller.warsmash.parsers.fdf.datamodel.AnchorDefinition;
import com.etheller.warsmash.parsers.fdf.datamodel.FramePoint;
import com.etheller.warsmash.parsers.fdf.datamodel.TextJustify;
import com.etheller.warsmash.parsers.fdf.datamodel.Vector4Definition;
import com.etheller.warsmash.parsers.fdf.frames.AbstractUIFrame;
import com.etheller.warsmash.parsers.fdf.frames.FilterModeTextureFrame;
import com.etheller.warsmash.parsers.fdf.frames.GlueTextButtonFrame;
import com.etheller.warsmash.parsers.fdf.frames.SetPoint;
import com.etheller.warsmash.parsers.fdf.frames.SimpleButtonFrame;
import com.etheller.warsmash.parsers.fdf.frames.SimpleFrame;
import com.etheller.warsmash.parsers.fdf.frames.SimpleStatusBarFrame;
import com.etheller.warsmash.parsers.fdf.frames.SingleStringFrame;
import com.etheller.warsmash.parsers.fdf.frames.SpriteFrame;
import com.etheller.warsmash.parsers.fdf.frames.StringFrame;
import com.etheller.warsmash.parsers.fdf.frames.TextureFrame;
import com.etheller.warsmash.parsers.fdf.frames.UIFrame;
import com.etheller.warsmash.parsers.jass.Jass2.RootFrameListener;
import com.etheller.warsmash.units.DataTable;
import com.etheller.warsmash.units.Element;
import com.etheller.warsmash.units.manager.MutableObjectData;
import com.etheller.warsmash.util.DataSourceFileHandle;
import com.etheller.warsmash.util.FastNumberFormat;
import com.etheller.warsmash.util.ImageUtils;
import com.etheller.warsmash.util.RenderMathUtils;
import com.etheller.warsmash.util.StringBundle;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.Bounds;
import com.etheller.warsmash.viewer5.Scene;
import com.etheller.warsmash.viewer5.ViewerTextureRenderable;
import com.etheller.warsmash.viewer5.handlers.mdx.Attachment;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxComplexInstance;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxModel;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxNode;
import com.etheller.warsmash.viewer5.handlers.mdx.ReplaceableIds;
import com.etheller.warsmash.viewer5.handlers.mdx.SequenceLoopMode;
import com.etheller.warsmash.viewer5.handlers.tga.TgaFile;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.PrimaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.SecondaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.SequenceUtils;
import com.etheller.warsmash.viewer5.handlers.w3x.SplatModel;
import com.etheller.warsmash.viewer5.handlers.w3x.SplatModel.SplatMover;
import com.etheller.warsmash.viewer5.handlers.w3x.TextTag;
import com.etheller.warsmash.viewer5.handlers.w3x.UnitSound;
import com.etheller.warsmash.viewer5.handlers.w3x.War3MapViewer;
import com.etheller.warsmash.viewer5.handlers.w3x.camera.CameraPreset;
import com.etheller.warsmash.viewer5.handlers.w3x.camera.CameraRates;
import com.etheller.warsmash.viewer5.handlers.w3x.camera.GameCameraManager;
import com.etheller.warsmash.viewer5.handlers.w3x.camera.PortraitCameraManager;
import com.etheller.warsmash.viewer5.handlers.w3x.environment.PathingGrid;
import com.etheller.warsmash.viewer5.handlers.w3x.environment.PathingGrid.PathingFlags;
import com.etheller.warsmash.viewer5.handlers.w3x.environment.Terrain;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.RenderDestructable;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.RenderItem;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.RenderUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.RenderWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.ability.AbilityDataUI;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.ability.IconUI;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.ability.ItemUI;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.ability.UnitIconUI;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.commandbuttons.CommandButtonListener;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CDestructable;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CGameplayConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CItem;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CItemType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CPlayerStateListener;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit.QueueItemType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitClassification;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitStateListener;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidgetFilterFunction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityAttack;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityGenericDoNothing;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityMove;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityView;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.build.AbstractCAbilityBuild;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.build.CAbilityBuildInProgress;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.build.CAbilityHumanBuild;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.build.CAbilityNagaBuild;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.build.CAbilityNeutralBuild;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.build.CAbilityNightElfBuild;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.build.CAbilityOrcBuild;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.build.CAbilityUndeadBuild;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.cargohold.CAbilityCargoHold;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.combat.CAbilityColdArrows;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.GenericNoIconAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.GenericSingleIconActiveAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.harvest.CAbilityReturnResources;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.hero.CAbilityHero;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.hero.CPrimaryAttribute;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.inventory.CAbilityInventory;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.mine.CAbilityGoldMine;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.queue.CAbilityQueue;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.queue.CAbilityRally;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.queue.CAbilityReviveHero;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.upgrade.CAbilityUpgrade;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CAttackType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CDefenseType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CodeKeyType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttack;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttackMissileSplash;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.data.CUnitRace;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.COrder;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.pathing.CBuildingPathingType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CAllianceType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayerUnitOrderListener;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CRace;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.timers.CTimer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit.BuildOnBuildingIntersector;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityActivationErrorHandler;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.BooleanAbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.BooleanAbilityTargetCheckReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.CWidgetAbilityTargetCheckReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.MeleeUIAbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.PointAbilityTargetCheckReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.ResourceType;
import com.etheller.warsmash.viewer5.handlers.w3x.ui.command.ClickableActionFrame;
import com.etheller.warsmash.viewer5.handlers.w3x.ui.command.ClickableFrame;
import com.etheller.warsmash.viewer5.handlers.w3x.ui.command.CommandCardCommandListener;
import com.etheller.warsmash.viewer5.handlers.w3x.ui.command.CommandErrorListener;
import com.etheller.warsmash.viewer5.handlers.w3x.ui.command.MultiSelectionIconListener;
import com.etheller.warsmash.viewer5.handlers.w3x.ui.command.QueueIconListener;
import com.etheller.warsmash.viewer5.handlers.w3x.ui.dialog.CScriptDialog;
import com.etheller.warsmash.viewer5.handlers.w3x.ui.dialog.CScriptDialogButton;
import com.etheller.warsmash.viewer5.handlers.w3x.ui.dialog.CTimerDialog;
import com.hiveworkshop.rms.parsers.mdlx.MdlxLayer.FilterMode;

public class MeleeUI implements CUnitStateListener, CommandButtonListener, CommandCardCommandListener,
		QueueIconListener, CommandErrorListener, CPlayerStateListener {
	private static final long WORLD_FRAME_MESSAGE_FADEOUT_MILLIS = TimeUnit.SECONDS.toMillis(9);
	private static final long WORLD_FRAME_MESSAGE_EXPIRE_MILLIS = TimeUnit.SECONDS.toMillis(10);
	private static final long WORLD_FRAME_MESSAGE_FADE_DURATION = WORLD_FRAME_MESSAGE_EXPIRE_MILLIS
			- WORLD_FRAME_MESSAGE_FADEOUT_MILLIS;
	private static final String BUILDING_PATHING_PREVIEW_KEY = "buildingPathingPreview";
	public static final float DEFAULT_COMMAND_CARD_ICON_WIDTH = 0.039f;
	public static final float DEFAULT_INVENTORY_ICON_WIDTH = 0.03125f;
	private static final int COMMAND_CARD_WIDTH = 4;
	private static final int COMMAND_CARD_HEIGHT = 3;
	private static final int INVENTORY_WIDTH = 2;
	private static final int INVENTORY_HEIGHT = 3;

	private static final Vector2 screenCoordsVector = new Vector2();
	private static final Vector3 clickLocationTemp = new Vector3();
	private static final AbilityPointTarget clickLocationTemp2 = new AbilityPointTarget();
	private final DataSource dataSource;
	private final ExtendViewport uiViewport;
	private final Scene uiScene;
	private final Scene portraitScene;
	private final GameCameraManager cameraManager;
	private final War3MapViewer war3MapViewer;
	private final RootFrameListener rootFrameListener;
	private GameUI rootFrame;
	private UIFrame consoleUI;
	private UIFrame resourceBar;
	private StringFrame resourceBarGoldText;
	private StringFrame resourceBarLumberText;
	private StringFrame resourceBarSupplyText;
	private StringFrame resourceBarUpkeepText;
	private SpriteFrame timeIndicator;
	private UIFrame unitPortrait;
	private StringFrame unitLifeText;
	private StringFrame unitManaText;
	private Portrait portrait;
	private final Rectangle tempRect = new Rectangle();
	private final Vector2 projectionTemp1 = new Vector2();
	private final Vector2 projectionTemp2 = new Vector2();

	// tooltip
	private UIFrame tooltipFrame;
	private StringFrame tooltipText;
	private StringFrame tooltipUberTipText;
	private UIFrame[] tooltipResourceFrames;
	private TextureFrame[] tooltipResourceIconFrames;
	private StringFrame[] tooltipResourceTextFrames;

	// hovertip
	private UIFrame hovertipFrame;
	private StringFrame hovertipText;

	private UIFrame simpleInfoPanelUnitDetail;
	private StringFrame simpleNameValue;
	private StringFrame simpleClassValue;
	private StringFrame simpleBuildingActionLabel;
	private SimpleStatusBarFrame simpleBuildTimeIndicator;
	private SimpleStatusBarFrame simpleHeroLevelBar;

	private UIFrame simpleInfoPanelBuildingDetail;
	private StringFrame simpleBuildingNameValue;
	private StringFrame simpleBuildingDescriptionValue;
	private StringFrame simpleBuildingBuildingActionLabel;
	private SimpleStatusBarFrame simpleBuildingBuildTimeIndicator;
	private final QueueIcon[] queueIconFrames = new QueueIcon[WarsmashConstants.BUILD_QUEUE_SIZE];
	private QueueIcon selectWorkerInsideFrame;
	private final UIFrame[] selectedUnitHighlightBackdrop = new UIFrame[WarsmashConstants.MAX_SELECTION_SIZE];
	private final MultiSelectionIcon[] selectedUnitFrames = new MultiSelectionIcon[WarsmashConstants.MAX_SELECTION_SIZE];
	private final UIFrame[] cargoBackdrop = new UIFrame[WarsmashConstants.MAX_SELECTION_SIZE];
	private final MultiSelectionIcon[] cargoUnitFrames = new MultiSelectionIcon[WarsmashConstants.MAX_SELECTION_SIZE];

	private UIFrame attack1Icon;
	private TextureFrame attack1IconBackdrop;
	private StringFrame attack1InfoPanelIconValue;
	private StringFrame attack1InfoPanelIconLevel;
	private UIFrame attack2Icon;
	private TextureFrame attack2IconBackdrop;
	private StringFrame attack2InfoPanelIconValue;
	private StringFrame attack2InfoPanelIconLevel;
	private UIFrame armorIcon;
	private TextureFrame armorIconBackdrop;
	private StringFrame armorInfoPanelIconValue;
	private StringFrame armorInfoPanelIconLevel;
	private InfoPanelIconBackdrops damageBackdrops;
	private InfoPanelIconBackdrops defenseBackdrops;

	private UIFrame heroInfoPanel;

	private SimpleFrame inventoryBarFrame;
	private StringFrame inventoryTitleFrame;
	private final CommandCardIcon[][] inventoryIcons = new CommandCardIcon[INVENTORY_HEIGHT][INVENTORY_WIDTH];
	private Texture consoleInventoryNoCapacityTexture;

	private final CommandCardIcon[][] commandCard = new CommandCardIcon[COMMAND_CARD_HEIGHT][COMMAND_CARD_WIDTH];

	private RenderUnit selectedUnit;
	private final List<Integer> subMenuOrderIdStack = new ArrayList<>();

	// TODO remove this & replace with FDF
	private final Texture activeButtonTexture;
	private UIFrame inventoryCover;
	private SpriteFrame cursorFrame;
	private MeleeUIMinimap meleeUIMinimap;
	private final CPlayerUnitOrderListener unitOrderListener;
	private StringFrame errorMessageFrame;
	// TODO array of game msgs?
//	private final List<StringFrame> gameMessageFrames = new ArrayList<>();
	private StringFrame gameMessagesFrame;
	private long lastErrorMessageExpireTime;
	private long lastErrorMessageFadeTime;
	private long lastGameMessageExpireTime;
	private long lastGameMessageFadeTime;

	private MenuCursorState cursorState;
	private Color cursorColor;
	private CAbilityView activeCommand;
	private int activeCommandOrderId;
	private RenderUnit activeCommandUnit;
	private MdxComplexInstance cursorModelInstance;
	private MdxComplexInstance rallyPointInstance;
	private BufferedImage cursorModelPathing;
	private Pixmap cursorModelUnderneathPathingRedGreenPixmap;
	private Texture cursorModelUnderneathPathingRedGreenPixmapTexture;
	private PixmapTextureData cursorModelUnderneathPathingRedGreenPixmapTextureData;
	private SplatModel cursorModelUnderneathPathingRedGreenSplatModel;
	private CUnitType cursorBuildingUnitType;
	private SplatMover placementCursor;
	private final CursorTargetSetupVisitor cursorTargetSetupVisitor;

	private int selectedSoundCount;
	private final ActiveCommandUnitTargetFilter activeCommandUnitTargetFilter;

	// TODO these corrections are used for old hardcoded UI stuff, we should
	// probably remove them later
	private final float widthRatioCorrection;
	private final float heightRatioCorrection;
	private ClickableFrame mouseDownUIFrame;
	private ClickableFrame mouseOverUIFrame;
	private UIFrame smashSimpleInfoPanel;
	private SimpleFrame smashAttack1IconWrapper;
	private SimpleFrame smashAttack2IconWrapper;
	private SimpleFrame smashArmorIconWrapper;
	private final RallyPositioningVisitor rallyPositioningVisitor;
	private final CPlayer localPlayer;
	private MeleeUIAbilityActivationReceiver meleeUIAbilityActivationReceiver;
	private MdxModel waypointModel;
	private final List<MdxComplexInstance> waypointModelInstances = new ArrayList<>();
	private List<RenderUnit> selectedUnits = Collections.emptyList();
	private Set<RenderUnit> dragSelectPreviewUnits = new HashSet<>();
	private Set<RenderUnit> dragSelectPreviewUnitsUpcoming = new HashSet<>();
	private BitmapFont textTagFont;
	private SetPoint uberTipNoResourcesSetPoint;
	private SetPoint uberTipWithResourcesSetPoint;
	private TextureFrame primaryAttributeIcon;
	private StringFrame strengthValue;
	private StringFrame agilityValue;
	private StringFrame intelligenceValue;
	private SimpleFrame smashHeroInfoPanelWrapper;

	private final StringBuilder recycleStringBuilder = new StringBuilder();
	private CItem draggingItem;
	private final ItemCommandCardCommandListener itemCommandCardCommandListener;
	private SimpleButtonFrame questsButton;
	private SimpleButtonFrame menuButton;
	private SimpleButtonFrame alliesButton;
	private SimpleButtonFrame chatButton;
	private final Runnable exitGameRunnable;
	private SimpleFrame smashEscMenu;
	private RenderWidget mouseOverUnit;
	private RenderWidget currentHoverTipUnit;
	private final Vector3 lastMouseDragStart = new Vector3();
	private final Vector3 lastMouseClickLocation = new Vector3();

	private final List<SimpleStatusBarFrame> hpBarFrames = new ArrayList<>();
	private int hpBarFrameIndex;
	private boolean allowDrag;
	private int currentlyDraggingPointer = -1;
	private final ShapeRenderer shapeRenderer = new ShapeRenderer();
	private final List<MultiSelectUnitStateListener> multiSelectUnitStateListeners = new ArrayList<>();
	private long lastUnitClickTime;
	private RenderWidget lastClickUnit;
	private MultiSelectionIconListener multiSelectClickListener;
	private MultiSelectionIconListener cargoClickListener;
	private float frontQueueIconWidth;
	private int draggingMouseButton;
	private Music[] currentMusics;
	private int currentMusicIndex;
	private boolean currentMusicRandomizeIndex;
	private final List<CTimerDialog> timerDialogs = new ArrayList<>();
	private final AnyClickableUnitFilter anyClickableUnitFilter;
	private final AnyTargetableUnitFilter anyTargetableUnitFilter;
	private final DataTable musicSLK;

	private final BuildOnBuildingIntersector buildOnBuildingIntersector = new BuildOnBuildingIntersector();

	public int[][] commandCardGridHotkeys ={{Input.Keys.Q,Input.Keys.W,Input.Keys.E,Input.Keys.R},
											{Input.Keys.A,Input.Keys.S,Input.Keys.D,Input.Keys.F},
											{Input.Keys.Z,Input.Keys.X,Input.Keys.C,Input.Keys.V}};

	public MeleeUI(final DataSource dataSource, final ExtendViewport uiViewport, final Scene uiScene,
			final Scene portraitScene, final CameraPreset[] cameraPresets, final CameraRates cameraRates,
			final War3MapViewer war3MapViewer, final RootFrameListener rootFrameListener,
			final CPlayerUnitOrderListener unitOrderListener, final Runnable exitGameRunnable) {
		this.dataSource = dataSource;
		this.uiViewport = uiViewport;
		this.uiScene = uiScene;
		this.portraitScene = portraitScene;
		this.war3MapViewer = war3MapViewer;
		this.rootFrameListener = rootFrameListener;
		this.unitOrderListener = unitOrderListener;
		this.exitGameRunnable = exitGameRunnable;

		cameraManager = new GameCameraManager(cameraPresets, cameraRates);

		cameraManager.setupCamera(war3MapViewer.worldScene);
		localPlayer = this.war3MapViewer.simulation.getPlayer(war3MapViewer.getLocalPlayerIndex());
		final float[] startLocation = localPlayer.getStartLocation();
		cameraManager.target.x = startLocation[0];
		cameraManager.target.y = startLocation[1];

		activeButtonTexture = ImageUtils.getAnyExtensionTexture(war3MapViewer.mapMpq,
				"UI\\Widgets\\Console\\Human\\CommandButton\\human-activebutton.blp");
		activeCommandUnitTargetFilter = new ActiveCommandUnitTargetFilter();
		widthRatioCorrection = this.uiViewport.getMinWorldWidth() / 1600f;
		heightRatioCorrection = this.uiViewport.getMinWorldHeight() / 1200f;
		rallyPositioningVisitor = new RallyPositioningVisitor();
		cursorTargetSetupVisitor = new CursorTargetSetupVisitor();

		localPlayer.addStateListener(this);

		itemCommandCardCommandListener = new ItemCommandCardCommandListener();
		anyClickableUnitFilter = new AnyClickableUnitFilter();
		anyTargetableUnitFilter = new AnyTargetableUnitFilter();

		musicSLK = new DataTable(StringBundle.EMPTY);
		final String musicSLKPath = "UI\\SoundInfo\\Music.SLK";
		if (war3MapViewer.dataSource.has(musicSLKPath)) {
			try (InputStream miscDataTxtStream = war3MapViewer.dataSource.getResourceAsStream(musicSLKPath)) {
				musicSLK.readSLK(miscDataTxtStream);
			}
			catch (final IOException e) {
				e.printStackTrace();
			}
		}
	}

	private MeleeUIMinimap createMinimap(final War3MapViewer war3MapViewer) {
		final Rectangle minimapDisplayArea = new Rectangle(18.75f * widthRatioCorrection,
				13.75f * heightRatioCorrection, 278.75f * widthRatioCorrection,
				276.25f * heightRatioCorrection);
		Texture minimapTexture = null;
		if (war3MapViewer.dataSource.has("war3mapMap.tga")) {
			try {
				minimapTexture = ImageUtils.getTextureNoColorCorrection(TgaFile.readTGA("war3mapMap.tga",
						war3MapViewer.dataSource.getResourceAsStream("war3mapMap.tga")));
			}
			catch (final IOException e) {
				System.err.println("Could not load minimap TGA file");
				e.printStackTrace();
			}
		}
		else if (war3MapViewer.dataSource.has("war3mapMap.blp")) {
			minimapTexture = ImageUtils.getAnyExtensionTexture(war3MapViewer.dataSource, "war3mapMap.blp");
		}
		final Texture[] teamColors = IntStream.range(0, WarsmashConstants.MAX_PLAYERS)
				.mapToObj(i -> ImageUtils.getAnyExtensionTexture(war3MapViewer.dataSource,
				"ReplaceableTextures\\" + ReplaceableIds.getPathString(1) + ReplaceableIds.getIdString(i) + ".blp"))
				.toArray(Texture[]::new);
		final Rectangle playableMapArea = war3MapViewer.terrain.getPlayableMapArea();
		return new MeleeUIMinimap(minimapDisplayArea, playableMapArea, minimapTexture, teamColors);
	}

	/**
	 * Called "main" because this was originally written in JASS so that maps could
	 * override it, and I may convert it back to the JASS at some point.
	 */
	public void main() {
		// =================================
		// Load skins and templates
		// =================================
		final CRace race = localPlayer.getRace();
		final String racialSkinKey;
		int racialCommandIndex;
		if (race == null) {
			racialSkinKey = "Human";
			racialCommandIndex = 0;
		}
		else {
			switch (race) {
				case ORC:
					racialSkinKey = "Orc";
					racialCommandIndex = 1;
					break;
				case NIGHTELF:
					racialSkinKey = "NightElf";
					racialCommandIndex = 3;
					break;
				case UNDEAD:
					racialSkinKey = "Undead";
					racialCommandIndex = 2;
					break;
				case DEMON:
				case OTHER:
				case HUMAN:
				default:
					racialSkinKey = "Human";
					racialCommandIndex = 0;
					break;
			}
		}
		rootFrame = new GameUI(dataSource, GameUI.loadSkin(dataSource, racialSkinKey), uiViewport,
				uiScene, war3MapViewer, racialCommandIndex, war3MapViewer.getAllObjectData().getWts());
		rootFrameListener.onCreate(rootFrame);
		try {
			rootFrame.loadTOCFile("UI\\FrameDef\\FrameDef.toc");
		}
		catch (final IOException exc) {
			throw new IllegalStateException("Unable to load FrameDef.toc", exc);
		}
		try {
			rootFrame.loadTOCFile("UI\\FrameDef\\SmashFrameDef.toc");
		}
		catch (final IOException exc) {
			throw new IllegalStateException("Unable to load SmashFrameDef.toc", exc);
		}
		damageBackdrops = new InfoPanelIconBackdrops(CAttackType.values(), rootFrame, "Damage", "Neutral");
		defenseBackdrops = new InfoPanelIconBackdrops(CDefenseType.values(), rootFrame, "Armor", "Neutral");

		// =================================
		// Load major UI components
		// =================================
		// Console UI is the background with the racial theme
		consoleUI = rootFrame.createSimpleFrame("ConsoleUI", rootFrame, 0);
		consoleUI.setSetAllPoints(true);

		// Resource bar is a 3 part bar with Gold, Lumber, and Food.
		// Its template does not specify where to put it, so we must
		// put it in the "TOPRIGHT" corner.
		resourceBar = rootFrame.createSimpleFrame("ResourceBarFrame", consoleUI, 0);
		resourceBar.addSetPoint(new SetPoint(FramePoint.TOPRIGHT, consoleUI, FramePoint.TOPRIGHT, 0, 0));
		resourceBarGoldText = (StringFrame) rootFrame.getFrameByName("ResourceBarGoldText", 0);
		goldChanged();
		resourceBarLumberText = (StringFrame) rootFrame.getFrameByName("ResourceBarLumberText", 0);
		lumberChanged();
		resourceBarSupplyText = (StringFrame) rootFrame.getFrameByName("ResourceBarSupplyText", 0);
		foodChanged();
		resourceBarUpkeepText = (StringFrame) rootFrame.getFrameByName("ResourceBarUpkeepText", 0);
		upkeepChanged();

		final UIFrame upperButtonBar = rootFrame.createSimpleFrame("UpperButtonBarFrame", consoleUI, 0);
		upperButtonBar.addSetPoint(new SetPoint(FramePoint.TOPLEFT, consoleUI, FramePoint.TOPLEFT, 0, 0));

		questsButton = (SimpleButtonFrame) rootFrame.getFrameByName("UpperButtonBarQuestsButton", 0);
		questsButton.setEnabled(false);
		menuButton = (SimpleButtonFrame) rootFrame.getFrameByName("UpperButtonBarMenuButton", 0);
		alliesButton = (SimpleButtonFrame) rootFrame.getFrameByName("UpperButtonBarAlliesButton", 0);
		alliesButton.setEnabled(false);
		chatButton = (SimpleButtonFrame) rootFrame.getFrameByName("UpperButtonBarChatButton", 0);
		chatButton.setEnabled(false);

		smashEscMenu = (SimpleFrame) rootFrame.createSimpleFrame("SmashEscMenu", rootFrame, 0);
		smashEscMenu.addAnchor(new AnchorDefinition(FramePoint.TOP, 0, GameUI.convertY(uiViewport, -0.05f)));
		final UIFrame escMenuBackdrop = rootFrame.createFrame("EscMenuBackdrop", smashEscMenu, 0, 0);
		escMenuBackdrop.setVisible(false);
		final UIFrame escMenuMainPanel = rootFrame.createFrame("EscMenuMainPanel", smashEscMenu, 0, 0);
		escMenuMainPanel.setVisible(false);
		smashEscMenu.add(escMenuBackdrop);
		smashEscMenu.add(escMenuMainPanel);

		final UIFrame escMenuInnerMainPanel = rootFrame.getFrameByName("MainPanel", 0);
		final GlueTextButtonFrame pauseButton = (GlueTextButtonFrame) rootFrame.getFrameByName("PauseButton", 0);
		pauseButton.setEnabled(false);
		final GlueTextButtonFrame saveGameButton = (GlueTextButtonFrame) rootFrame.getFrameByName("SaveGameButton",
				0);
		saveGameButton.setEnabled(false);
		final GlueTextButtonFrame loadGameButton = (GlueTextButtonFrame) rootFrame.getFrameByName("LoadGameButton",
				0);
		loadGameButton.setEnabled(false);
		final GlueTextButtonFrame optionsButton = (GlueTextButtonFrame) rootFrame.getFrameByName("OptionsButton",
				0);
		optionsButton.setEnabled(false);
		final GlueTextButtonFrame helpButton = (GlueTextButtonFrame) rootFrame.getFrameByName("HelpButton", 0);
		helpButton.setEnabled(false);
		final GlueTextButtonFrame tipsButton = (GlueTextButtonFrame) rootFrame.getFrameByName("TipsButton", 0);
		tipsButton.setEnabled(false);
		final GlueTextButtonFrame endGameButton = (GlueTextButtonFrame) rootFrame.getFrameByName("EndGameButton",
				0);
		final GlueTextButtonFrame returnButton = (GlueTextButtonFrame) rootFrame.getFrameByName("ReturnButton", 0);

		final UIFrame escMenuInnerEndGamePanel = rootFrame.getFrameByName("EndGamePanel", 0);
		final GlueTextButtonFrame endGamePreviousButton = (GlueTextButtonFrame) rootFrame
				.getFrameByName("PreviousButton", 0);
		final GlueTextButtonFrame endGameQuitButton = (GlueTextButtonFrame) rootFrame.getFrameByName("QuitButton",
				0);
		final GlueTextButtonFrame endGameRestartButton = (GlueTextButtonFrame) rootFrame
				.getFrameByName("RestartButton", 0);
		endGameRestartButton.setEnabled(false);
		final GlueTextButtonFrame endGameExitButton = (GlueTextButtonFrame) rootFrame.getFrameByName("ExitButton",
				0);

		final UIFrame escMenuInnerConfirmQuitPanel = rootFrame.getFrameByName("ConfirmQuitPanel", 0);
		final GlueTextButtonFrame confirmQuitCancelButton = (GlueTextButtonFrame) rootFrame
				.getFrameByName("ConfirmQuitCancelButton", 0);
		final GlueTextButtonFrame confirmQuitQuitButton = (GlueTextButtonFrame) rootFrame
				.getFrameByName("ConfirmQuitQuitButton", 0);
		final UIFrame escMenuInnerHelpPanel = rootFrame.getFrameByName("HelpPanel", 0);
		final UIFrame escMenuInnerTipsPanel = rootFrame.getFrameByName("TipsPanel", 0);
		escMenuInnerMainPanel.setVisible(false);
		escMenuInnerEndGamePanel.setVisible(false);
		escMenuInnerConfirmQuitPanel.setVisible(false);
		escMenuInnerHelpPanel.setVisible(false);
		escMenuInnerTipsPanel.setVisible(false);

		menuButton.setOnClick(() -> {
			escMenuBackdrop.setVisible(true);
			escMenuMainPanel.setVisible(true);
			smashEscMenu.setVisible(true);
			escMenuInnerMainPanel.setVisible(true);
			updateEscMenuCurrentPanel(escMenuBackdrop, escMenuMainPanel, escMenuInnerMainPanel);
		});
		returnButton.setOnClick(() -> {
			escMenuBackdrop.setVisible(false);
			escMenuMainPanel.setVisible(false);
			smashEscMenu.setVisible(false);
			escMenuInnerMainPanel.setVisible(false);
		});
		endGameButton.setOnClick(() -> {
			escMenuInnerMainPanel.setVisible(false);
			escMenuInnerEndGamePanel.setVisible(true);
			updateEscMenuCurrentPanel(escMenuBackdrop, escMenuMainPanel, escMenuInnerEndGamePanel);
		});
		endGamePreviousButton.setOnClick(() -> {
			escMenuInnerEndGamePanel.setVisible(false);
			escMenuInnerMainPanel.setVisible(true);
			updateEscMenuCurrentPanel(escMenuBackdrop, escMenuMainPanel, escMenuInnerMainPanel);
		});
		endGameQuitButton.setOnClick(() -> {
			escMenuInnerEndGamePanel.setVisible(false);
			exitGameRunnable.run();
		});
		endGameExitButton.setOnClick(() -> {
			escMenuInnerEndGamePanel.setVisible(false);
			escMenuInnerConfirmQuitPanel.setVisible(true);
			updateEscMenuCurrentPanel(escMenuBackdrop, escMenuMainPanel, escMenuInnerConfirmQuitPanel);
		});
		confirmQuitCancelButton.setOnClick(() -> {
			escMenuInnerEndGamePanel.setVisible(true);
			escMenuInnerConfirmQuitPanel.setVisible(false);
			updateEscMenuCurrentPanel(escMenuBackdrop, escMenuMainPanel, escMenuInnerEndGamePanel);
		});
		confirmQuitQuitButton.setOnClick(() -> Gdx.app.exit());

		// Create the Time Indicator (clock)
		timeIndicator = (SpriteFrame) rootFrame.createFrame("TimeOfDayIndicator", rootFrame, 0, 0);
		timeIndicator.setSequence(0); // play the stand
		timeIndicator.setAnimationSpeed(0.0f); // do not advance automatically

		// Create the unit portrait stuff
		portrait = new Portrait(war3MapViewer, portraitScene);
		positionPortrait();
		unitPortrait = rootFrame.createSimpleFrame("UnitPortrait", consoleUI, 0);
		unitLifeText = (StringFrame) rootFrame.getFrameByName("UnitPortraitHitPointText", 0);
		unitManaText = (StringFrame) rootFrame.getFrameByName("UnitPortraitManaPointText", 0);

		final float infoPanelUnitDetailWidth = GameUI.convertY(uiViewport, 0.180f);
		final float infoPanelUnitDetailHeight = GameUI.convertY(uiViewport, 0.120f);
		smashSimpleInfoPanel = rootFrame.createSimpleFrame("SmashSimpleInfoPanel", rootFrame, 0);
		smashSimpleInfoPanel
				.addAnchor(new AnchorDefinition(FramePoint.BOTTOM, 0, GameUI.convertY(uiViewport, 0.0f)));
		smashSimpleInfoPanel.setWidth(infoPanelUnitDetailWidth);
		smashSimpleInfoPanel.setHeight(infoPanelUnitDetailHeight);

		// Create Simple Info Unit Detail
		simpleInfoPanelUnitDetail = rootFrame.createSimpleFrame("SimpleInfoPanelUnitDetail",
				smashSimpleInfoPanel, 0);
		simpleNameValue = (StringFrame) rootFrame.getFrameByName("SimpleNameValue", 0);
		simpleClassValue = (StringFrame) rootFrame.getFrameByName("SimpleClassValue", 0);
		simpleBuildingActionLabel = (StringFrame) rootFrame.getFrameByName("SimpleBuildingActionLabel", 0);
		simpleBuildTimeIndicator = (SimpleStatusBarFrame) rootFrame.getFrameByName("SimpleBuildTimeIndicator",
				0);
		final TextureFrame simpleBuildTimeIndicatorBar = simpleBuildTimeIndicator.getBarFrame();
		simpleBuildTimeIndicatorBar.setTexture("SimpleBuildTimeIndicator", rootFrame);
		final TextureFrame simpleBuildTimeIndicatorBorder = simpleBuildTimeIndicator.getBorderFrame();
		simpleBuildTimeIndicatorBorder.setTexture("SimpleBuildTimeIndicatorBorder", rootFrame);
		final float buildTimeIndicatorWidth = GameUI.convertX(uiViewport, 0.10538f);
		final float buildTimeIndicatorHeight = GameUI.convertY(uiViewport, 0.0103f);
		simpleBuildTimeIndicator.setWidth(buildTimeIndicatorWidth);
		simpleBuildTimeIndicator.setHeight(buildTimeIndicatorHeight);

		simpleHeroLevelBar = (SimpleStatusBarFrame) rootFrame.getFrameByName("SimpleHeroLevelBar", 0);
		final TextureFrame simpleHeroLevelBarBar = simpleHeroLevelBar.getBarFrame();
		simpleHeroLevelBarBar.setTexture("SimpleXpBarConsole", rootFrame);
		simpleHeroLevelBarBar.setColor(new Color(138f / 255f, 0, 131f / 255f, 1f));
		final TextureFrame simpleHeroLevelBarBorder = simpleHeroLevelBar.getBorderFrame();
		simpleHeroLevelBarBorder.setTexture("SimpleXpBarBorder", rootFrame);
		simpleHeroLevelBar.setWidth(infoPanelUnitDetailWidth);

		// Create Simple Info Panel Building Detail
		simpleInfoPanelBuildingDetail = rootFrame.createSimpleFrame("SimpleInfoPanelBuildingDetail",
				smashSimpleInfoPanel, 0);
		simpleBuildingNameValue = (StringFrame) rootFrame.getFrameByName("SimpleBuildingNameValue", 0);
		simpleBuildingDescriptionValue = (StringFrame) rootFrame
				.getFrameByName("SimpleBuildingDescriptionValue", 0);
		simpleBuildingBuildingActionLabel = (StringFrame) rootFrame
				.getFrameByName("SimpleBuildingActionLabel", 0);
		simpleBuildingBuildTimeIndicator = (SimpleStatusBarFrame) rootFrame
				.getFrameByName("SimpleBuildTimeIndicator", 0);
		final TextureFrame simpleBuildingBuildTimeIndicatorBar = simpleBuildingBuildTimeIndicator.getBarFrame();
		simpleBuildingBuildTimeIndicatorBar.setTexture("SimpleBuildTimeIndicator", rootFrame);
		final TextureFrame simpleBuildingBuildTimeIndicatorBorder = simpleBuildingBuildTimeIndicator
				.getBorderFrame();
		simpleBuildingBuildTimeIndicatorBorder.setTexture("SimpleBuildTimeIndicatorBorder", rootFrame);
		simpleBuildingBuildTimeIndicator.setWidth(buildTimeIndicatorWidth);
		simpleBuildingBuildTimeIndicator.setHeight(buildTimeIndicatorHeight);
		simpleInfoPanelBuildingDetail.setVisible(false);
		final TextureFrame simpleBuildQueueBackdrop = (TextureFrame) rootFrame
				.getFrameByName("SimpleBuildQueueBackdrop", 0);
		simpleBuildQueueBackdrop.setWidth(infoPanelUnitDetailWidth);
		simpleBuildQueueBackdrop.setHeight(infoPanelUnitDetailWidth * 0.5f);

		queueIconFrames[0] = new QueueIcon("SmashBuildQueueIcon0", smashSimpleInfoPanel, this, 0);
		final TextureFrame queueIconFrameBackdrop0 = new TextureFrame("SmashBuildQueueIcon0Backdrop",
				queueIconFrames[0], false, new Vector4Definition(0, 1, 0, 1));
		queueIconFrameBackdrop0
				.addSetPoint(new SetPoint(FramePoint.CENTER, queueIconFrames[0], FramePoint.CENTER, 0, 0));
		queueIconFrames[0].set(queueIconFrameBackdrop0);
		queueIconFrames[0]
				.addSetPoint(new SetPoint(FramePoint.CENTER, smashSimpleInfoPanel, FramePoint.BOTTOMLEFT,
						(infoPanelUnitDetailWidth * (15 + 19f)) / 256, (infoPanelUnitDetailWidth * (66 + 19f)) / 256));
		frontQueueIconWidth = (infoPanelUnitDetailWidth * 38) / 256;
		queueIconFrames[0].setWidth(frontQueueIconWidth);
		queueIconFrames[0].setHeight(frontQueueIconWidth);
		queueIconFrameBackdrop0.setWidth(frontQueueIconWidth);
		queueIconFrameBackdrop0.setHeight(frontQueueIconWidth);
		rootFrame.add(queueIconFrames[0]);

		for (int i = 1; i < queueIconFrames.length; i++) {
			queueIconFrames[i] = new QueueIcon("SmashBuildQueueIcon" + i, smashSimpleInfoPanel, this, i);
			final TextureFrame queueIconFrameBackdrop = new TextureFrame("SmashBuildQueueIcon" + i + "Backdrop",
					queueIconFrames[i], false, new Vector4Definition(0, 1, 0, 1));
			queueIconFrames[i].set(queueIconFrameBackdrop);
			queueIconFrameBackdrop
					.addSetPoint(new SetPoint(FramePoint.CENTER, queueIconFrames[i], FramePoint.CENTER, 0, 0));
			queueIconFrames[i].addSetPoint(new SetPoint(FramePoint.CENTER, smashSimpleInfoPanel,
					FramePoint.BOTTOMLEFT, (infoPanelUnitDetailWidth * (13 + 14.5f + (40 * (i - 1)))) / 256,
					(infoPanelUnitDetailWidth * (24 + 14.5f)) / 256));
			final float queueIconWidth = (infoPanelUnitDetailWidth * 29) / 256;
			queueIconFrames[i].setWidth(queueIconWidth);
			queueIconFrames[i].setHeight(queueIconWidth);
			queueIconFrameBackdrop.setWidth(queueIconWidth);
			queueIconFrameBackdrop.setHeight(queueIconWidth);
			rootFrame.add(queueIconFrames[i]);
		}
		selectWorkerInsideFrame = new QueueIcon("SmashBuildQueueWorkerIcon", smashSimpleInfoPanel, this, 1);
		final TextureFrame selectWorkerInsideIconFrameBackdrop = new TextureFrame("SmashBuildQueueWorkerIconBackdrop",
				selectWorkerInsideFrame, false, new Vector4Definition(0, 1, 0, 1));
		selectWorkerInsideFrame.set(selectWorkerInsideIconFrameBackdrop);
		selectWorkerInsideIconFrameBackdrop
				.addSetPoint(new SetPoint(FramePoint.CENTER, selectWorkerInsideFrame, FramePoint.CENTER, 0, 0));
		selectWorkerInsideFrame
				.addSetPoint(new SetPoint(FramePoint.TOPLEFT, queueIconFrames[1], FramePoint.TOPLEFT, 0, 0));
		selectWorkerInsideFrame.setWidth(frontQueueIconWidth);
		selectWorkerInsideFrame.setHeight(frontQueueIconWidth);
		selectWorkerInsideIconFrameBackdrop.setWidth(frontQueueIconWidth);
		selectWorkerInsideIconFrameBackdrop.setHeight(frontQueueIconWidth);
		rootFrame.add(selectWorkerInsideFrame);

		final int halfSelectionMaxSize = selectedUnitFrames.length / 2;
		for (int i = 0; i < selectedUnitFrames.length; i++) {
			{
				final FilterModeTextureFrame selectedSubgroupHighlightBackdrop = new FilterModeTextureFrame(
						"SmashMultiSelectUnitIconHighlightBackdrop", smashSimpleInfoPanel, true,
						new Vector4Definition(0, 1, 0, 1));
				selectedSubgroupHighlightBackdrop.setFilterMode(FilterMode.ADDITIVE);
				selectedUnitHighlightBackdrop[i] = selectedSubgroupHighlightBackdrop;
				selectedSubgroupHighlightBackdrop.setTexture("SelectedSubgroupHighlight", rootFrame);
				selectedSubgroupHighlightBackdrop.setWidth(frontQueueIconWidth * 1.37f);
				selectedSubgroupHighlightBackdrop.setHeight(frontQueueIconWidth * 1.75f);
				selectedSubgroupHighlightBackdrop.setColor(1.0f, 1.0f, 0.0f, 1.0f);
				rootFrame.add(selectedSubgroupHighlightBackdrop);
				selectedSubgroupHighlightBackdrop
						.addSetPoint(new SetPoint(FramePoint.TOPLEFT, smashSimpleInfoPanel, FramePoint.TOPLEFT,
								((-frontQueueIconWidth * .37f) / 2) + (frontQueueIconWidth * .10f)
										+ (frontQueueIconWidth * 1.10f * (i % halfSelectionMaxSize)),
								(((frontQueueIconWidth * .37f) / 2) + (frontQueueIconWidth * -.75f))
										- (frontQueueIconWidth * 1.5f * (i / halfSelectionMaxSize))));
			}
			{
				final TextureFrame cargoBackdrop = new TextureFrame("SmashCargoBackdrop", smashSimpleInfoPanel,
						true, new Vector4Definition(0, 1, 0, 1));
				cargoBackdrop.setVisible(false);
				this.cargoBackdrop[i] = cargoBackdrop;
				cargoBackdrop.setTexture("CargoBackdrop", rootFrame);
				cargoBackdrop.setWidth(frontQueueIconWidth * 1.37f);
				cargoBackdrop.setHeight(frontQueueIconWidth * 1.75f);
				cargoBackdrop.setColor(1.0f, 1.0f, 0.0f, 1.0f);
				rootFrame.add(cargoBackdrop);
				cargoBackdrop
						.addSetPoint(new SetPoint(FramePoint.TOPLEFT, smashSimpleInfoPanel, FramePoint.TOPLEFT,
								((-frontQueueIconWidth * .37f) / 2) + (frontQueueIconWidth * .10f)
										+ (frontQueueIconWidth * 1.10f * (i % halfSelectionMaxSize)),
								(((frontQueueIconWidth * .37f) / 2) + (frontQueueIconWidth * -.75f))
										- (frontQueueIconWidth * 1.5f * (i / halfSelectionMaxSize))));
			}
		}
		cargoClickListener = new MultiSelectionIconListener() {
			@Override
			public void multiSelectIconRelease(final int index) {

			}

			@Override
			public void multiSelectIconPress(final int index) {

			}

			@Override
			public void multiSelectIconClicked(final int index) {

			}
		};
		multiSelectClickListener = new MultiSelectionIconListener() {
			@Override
			public void multiSelectIconClicked(final int index) {
				if (index < selectedUnits.size()) {
					final RenderUnit clickUnit = selectedUnits.get(index);
					if (activeCommand != null) {
						useActiveCommandOnUnit(isShiftDown(), clickUnit);
					}
					else if (Objects.equals(clickUnit, selectedUnit)) {
						final List<RenderWidget> newSelection = Collections.singletonList(selectedUnit);
						selectWidgets(newSelection);
						war3MapViewer.doSelectUnit(newSelection);
					}
					else {
						selectUnit(clickUnit);
					}
				}
			}

			@Override
			public void multiSelectIconPress(final int index) {
			}

			@Override
			public void multiSelectIconRelease(final int index) {
			}
		};
		for (int i = 0; i < selectedUnitFrames.length; i++) {
			selectedUnitFrames[i] = new MultiSelectionIcon("SmashMultiSelectUnitIcon", smashSimpleInfoPanel,
					multiSelectClickListener, i);
			final TextureFrame multiSelectUnitIconFrameBackdrop = new TextureFrame("SmashMultiSelectUnitIconBackdrop",
					selectedUnitFrames[i], false, new Vector4Definition(0, 1, 0, 1));

			final SimpleStatusBarFrame hpBarFrame = new SimpleStatusBarFrame(
					"SmashMultiSelectHpBar" + hpBarFrameIndex, rootFrame, true, true, 3.0f);
			hpBarFrame.getBarFrame().setTexture("SimpleHpBarConsole", rootFrame);
			hpBarFrame.getBorderFrame().setTexture("Textures\\Black32.blp", rootFrame);
			hpBarFrame.setWidth(frontQueueIconWidth);
			hpBarFrame.setHeight(frontQueueIconWidth * MultiSelectionIcon.HP_BAR_HEIGHT_RATIO);
			hpBarFrame.addSetPoint(new SetPoint(FramePoint.TOP, multiSelectUnitIconFrameBackdrop, FramePoint.BOTTOM, 0,
					-frontQueueIconWidth * MultiSelectionIcon.HP_BAR_SPACING_RATIO));

			final SimpleStatusBarFrame manaBarFrame = new SimpleStatusBarFrame(
					"SmashMultiSelectManaBar" + hpBarFrameIndex, rootFrame, true, true, 3.0f);
			manaBarFrame.getBarFrame().setTexture("SimpleManaBarConsole", rootFrame);
			manaBarFrame.getBorderFrame().setTexture("Textures\\Black32.blp", rootFrame);
			manaBarFrame.setWidth(frontQueueIconWidth);
			manaBarFrame.setHeight(frontQueueIconWidth * MultiSelectionIcon.HP_BAR_HEIGHT_RATIO);
			manaBarFrame.addSetPoint(new SetPoint(FramePoint.TOP, hpBarFrame, FramePoint.BOTTOM, 0,
					-frontQueueIconWidth * MultiSelectionIcon.HP_BAR_SPACING_RATIO));
			manaBarFrame.getBarFrame().setColor(0f, 0f, 1f, 1f);

			selectedUnitFrames[i].set(multiSelectUnitIconFrameBackdrop, hpBarFrame, manaBarFrame);
			multiSelectUnitIconFrameBackdrop
					.addSetPoint(new SetPoint(FramePoint.CENTER, selectedUnitFrames[i], FramePoint.CENTER, 0, 0));
			selectedUnitFrames[i]
					.addSetPoint(new SetPoint(FramePoint.TOPLEFT, smashSimpleInfoPanel, FramePoint.TOPLEFT,
							(frontQueueIconWidth * .10f)
									+ (frontQueueIconWidth * 1.10f * (i % halfSelectionMaxSize)),
							(frontQueueIconWidth * -.75f)
									- (frontQueueIconWidth * 1.5f * (i / halfSelectionMaxSize))));
			selectedUnitFrames[i].setWidth(frontQueueIconWidth);
			selectedUnitFrames[i].setHeight(frontQueueIconWidth);
			multiSelectUnitIconFrameBackdrop.setWidth(frontQueueIconWidth);
			multiSelectUnitIconFrameBackdrop.setHeight(frontQueueIconWidth);
			rootFrame.add(selectedUnitFrames[i]);

			selectedUnitFrames[i].setVisible(false);
		}
		for (int i = 0; i < cargoUnitFrames.length; i++) {
			cargoUnitFrames[i] = new MultiSelectionIcon("SmashMultiSelectUnitIcon", smashSimpleInfoPanel,
					cargoClickListener, i);
			cargoUnitFrames[i].setVisible(false);
			final TextureFrame cargoUnitIconFrameBackdrop = new TextureFrame("SmashCargoUnitIconBackdrop",
					cargoUnitFrames[i], false, new Vector4Definition(0, 1, 0, 1));

			final SimpleStatusBarFrame hpBarFrame = new SimpleStatusBarFrame(
					"SmashMultiSelectHpBar" + hpBarFrameIndex, rootFrame, true, true, 3.0f);
			hpBarFrame.getBarFrame().setTexture("SimpleHpBarConsole", rootFrame);
			hpBarFrame.getBorderFrame().setTexture("Textures\\Black32.blp", rootFrame);
			hpBarFrame.setWidth(frontQueueIconWidth);
			hpBarFrame.setHeight(frontQueueIconWidth * MultiSelectionIcon.HP_BAR_HEIGHT_RATIO);
			hpBarFrame.addSetPoint(new SetPoint(FramePoint.TOP, cargoUnitIconFrameBackdrop, FramePoint.BOTTOM, 0,
					-frontQueueIconWidth * MultiSelectionIcon.HP_BAR_SPACING_RATIO));

			final SimpleStatusBarFrame manaBarFrame = new SimpleStatusBarFrame(
					"SmashMultiSelectManaBar" + hpBarFrameIndex, rootFrame, true, true, 3.0f);
			manaBarFrame.getBarFrame().setTexture("SimpleManaBarConsole", rootFrame);
			manaBarFrame.getBorderFrame().setTexture("Textures\\Black32.blp", rootFrame);
			manaBarFrame.setWidth(frontQueueIconWidth);
			manaBarFrame.setHeight(frontQueueIconWidth * MultiSelectionIcon.HP_BAR_HEIGHT_RATIO);
			manaBarFrame.addSetPoint(new SetPoint(FramePoint.TOP, hpBarFrame, FramePoint.BOTTOM, 0,
					-frontQueueIconWidth * MultiSelectionIcon.HP_BAR_SPACING_RATIO));
			manaBarFrame.getBarFrame().setColor(0f, 0f, 1f, 1f);

			cargoUnitFrames[i].set(cargoUnitIconFrameBackdrop, hpBarFrame, manaBarFrame);
			cargoUnitIconFrameBackdrop
					.addSetPoint(new SetPoint(FramePoint.CENTER, cargoUnitFrames[i], FramePoint.CENTER, 0, 0));
			cargoUnitFrames[i]
					.addSetPoint(new SetPoint(FramePoint.TOPLEFT, smashSimpleInfoPanel, FramePoint.TOPLEFT,
							(frontQueueIconWidth * .10f)
									+ (frontQueueIconWidth * 1.10f * (i % halfSelectionMaxSize)),
							(frontQueueIconWidth * -.75f)
									- (frontQueueIconWidth * 1.5f * (i / halfSelectionMaxSize))));
			cargoUnitFrames[i].setWidth(frontQueueIconWidth);
			cargoUnitFrames[i].setHeight(frontQueueIconWidth);
			cargoUnitIconFrameBackdrop.setWidth(frontQueueIconWidth);
			cargoUnitIconFrameBackdrop.setHeight(frontQueueIconWidth);
			rootFrame.add(cargoUnitFrames[i]);

			cargoUnitFrames[i].setVisible(false);
		}

		smashAttack1IconWrapper = (SimpleFrame) rootFrame.createSimpleFrame("SmashSimpleInfoPanelIconDamage",
				simpleInfoPanelUnitDetail, 0);
		smashAttack1IconWrapper.addSetPoint(new SetPoint(FramePoint.TOPLEFT, simpleInfoPanelUnitDetail,
				FramePoint.TOPLEFT, 0, GameUI.convertY(uiViewport, -0.040f)));
		smashAttack1IconWrapper.setWidth(GameUI.convertX(uiViewport, 0.1f));
		smashAttack1IconWrapper.setHeight(GameUI.convertY(uiViewport, 0.030125f));
		attack1Icon = rootFrame.createSimpleFrame("SimpleInfoPanelIconDamage", smashAttack1IconWrapper,
				0);
		attack1IconBackdrop = (TextureFrame) rootFrame.getFrameByName("InfoPanelIconBackdrop", 0);
		attack1InfoPanelIconValue = (StringFrame) rootFrame.getFrameByName("InfoPanelIconValue", 0);
		attack1InfoPanelIconLevel = (StringFrame) rootFrame.getFrameByName("InfoPanelIconLevel", 0);

		smashAttack2IconWrapper = (SimpleFrame) rootFrame.createSimpleFrame("SmashSimpleInfoPanelIconDamage",
				simpleInfoPanelUnitDetail, 0);
		smashAttack2IconWrapper
				.addSetPoint(new SetPoint(FramePoint.TOPLEFT, simpleInfoPanelUnitDetail, FramePoint.TOPLEFT,
						GameUI.convertX(uiViewport, 0.1f), GameUI.convertY(uiViewport, -0.03925f)));
		smashAttack2IconWrapper.setWidth(GameUI.convertX(uiViewport, 0.1f));
		smashAttack2IconWrapper.setHeight(GameUI.convertY(uiViewport, 0.030125f));
		attack2Icon = rootFrame.createSimpleFrame("SimpleInfoPanelIconDamage", smashAttack2IconWrapper,
				1);
		attack2IconBackdrop = (TextureFrame) rootFrame.getFrameByName("InfoPanelIconBackdrop", 1);
		attack2InfoPanelIconValue = (StringFrame) rootFrame.getFrameByName("InfoPanelIconValue", 1);
		attack2InfoPanelIconLevel = (StringFrame) rootFrame.getFrameByName("InfoPanelIconLevel", 1);

		smashArmorIconWrapper = (SimpleFrame) rootFrame.createSimpleFrame("SmashSimpleInfoPanelIconArmor",
				simpleInfoPanelUnitDetail, 0);
		smashArmorIconWrapper.addSetPoint(new SetPoint(FramePoint.TOPLEFT, simpleInfoPanelUnitDetail,
				FramePoint.TOPLEFT, GameUI.convertX(uiViewport, 0f), GameUI.convertY(uiViewport, -0.0705f)));
		smashArmorIconWrapper.setWidth(GameUI.convertX(uiViewport, 0.1f));
		smashArmorIconWrapper.setHeight(GameUI.convertY(uiViewport, 0.030125f));
		armorIcon = rootFrame.createSimpleFrame("SimpleInfoPanelIconArmor", smashArmorIconWrapper, 0);
		armorIconBackdrop = (TextureFrame) rootFrame.getFrameByName("InfoPanelIconBackdrop", 0);
		armorInfoPanelIconValue = (StringFrame) rootFrame.getFrameByName("InfoPanelIconValue", 0);
		armorInfoPanelIconLevel = (StringFrame) rootFrame.getFrameByName("InfoPanelIconLevel", 0);

		smashHeroInfoPanelWrapper = (SimpleFrame) rootFrame.createSimpleFrame("SmashSimpleInfoPanelIconHero",
				simpleInfoPanelUnitDetail, 0);
		smashHeroInfoPanelWrapper.addSetPoint(new SetPoint(FramePoint.TOPLEFT, simpleInfoPanelUnitDetail,
				FramePoint.TOPLEFT, GameUI.convertX(uiViewport, 0.1f), GameUI.convertY(uiViewport, -0.037f)));
		smashHeroInfoPanelWrapper.setWidth(GameUI.convertX(uiViewport, 0.1f));
		smashHeroInfoPanelWrapper.setHeight(GameUI.convertY(uiViewport, 0.0625f));
		heroInfoPanel = rootFrame.createSimpleFrame("SimpleInfoPanelIconHero", smashHeroInfoPanelWrapper,
				0);
		primaryAttributeIcon = (TextureFrame) rootFrame.getFrameByName("InfoPanelIconHeroIcon", 0);
		strengthValue = (StringFrame) rootFrame.getFrameByName("InfoPanelIconHeroStrengthValue", 0);
		agilityValue = (StringFrame) rootFrame.getFrameByName("InfoPanelIconHeroAgilityValue", 0);
		intelligenceValue = (StringFrame) rootFrame.getFrameByName("InfoPanelIconHeroIntellectValue", 0);

		inventoryBarFrame = (SimpleFrame) rootFrame.createSimpleFrame("SmashSimpleInventoryBar",
				rootFrame, 0);
		inventoryBarFrame.setWidth(GameUI.convertX(uiViewport, 0.079f));
		inventoryBarFrame.setHeight(GameUI.convertY(uiViewport, 0.115f));
		inventoryBarFrame.addSetPoint(new SetPoint(FramePoint.BOTTOMRIGHT, consoleUI, FramePoint.BOTTOMLEFT,
				GameUI.convertX(uiViewport, 0.591f), GameUI.convertY(uiViewport, 0.0f)));

		if (GameUI.DEBUG) {
			final FilterModeTextureFrame placeholderPreview = new FilterModeTextureFrame(null, inventoryBarFrame,
					false, null);
			placeholderPreview.setFilterMode(FilterMode.ADDALPHA);
			placeholderPreview.setTexture("ReplaceableTextures\\TeamColor\\TeamColor06.blp", rootFrame);
			placeholderPreview.setSetAllPoints(true);
			inventoryBarFrame.add(placeholderPreview);
		}

		int commandButtonIndex = 0;
		for (int j = 0; j < INVENTORY_HEIGHT; j++) {
			for (int i = 0; i < INVENTORY_WIDTH; i++) {
				final CommandCardIcon commandCardIcon = new CommandCardIcon(
						"SmashInventoryButton_" + commandButtonIndex, inventoryBarFrame,
						itemCommandCardCommandListener);
				inventoryBarFrame.add(commandCardIcon);
				final TextureFrame iconFrame = new TextureFrame(
						"SmashInventoryButton_" + (commandButtonIndex) + "_Icon", rootFrame, false, null);
				final SpriteFrame cooldownFrame = (SpriteFrame) rootFrame.createFrameByType("SPRITE",
						"SmashInventoryButton_" + (commandButtonIndex) + "_Cooldown", rootFrame, "", 0);
				commandCardIcon.addSetPoint(new SetPoint(FramePoint.TOPLEFT, inventoryBarFrame, FramePoint.TOPLEFT,
						GameUI.convertX(uiViewport, 0.0037f + (0.04f * i)),
						GameUI.convertY(uiViewport, -0.0021f - (0.03815f * j))));
				commandCardIcon.setWidth(GameUI.convertX(uiViewport, DEFAULT_INVENTORY_ICON_WIDTH));
				commandCardIcon.setHeight(GameUI.convertY(uiViewport, DEFAULT_INVENTORY_ICON_WIDTH));
				iconFrame.addSetPoint(new SetPoint(FramePoint.CENTER, commandCardIcon, FramePoint.CENTER, 0, 0));
				iconFrame.setWidth(GameUI.convertX(uiViewport, DEFAULT_INVENTORY_ICON_WIDTH));
				iconFrame.setHeight(GameUI.convertY(uiViewport, DEFAULT_INVENTORY_ICON_WIDTH));
				iconFrame.setTexture(ImageUtils.DEFAULT_ICON_PATH, rootFrame);
				cooldownFrame.addSetPoint(new SetPoint(FramePoint.CENTER, commandCardIcon, FramePoint.CENTER, 0, 0));
				rootFrame.setSpriteFrameModel(cooldownFrame, rootFrame.getSkinField("CommandButtonCooldown"));
				cooldownFrame.setWidth(GameUI.convertX(uiViewport, DEFAULT_INVENTORY_ICON_WIDTH));
				cooldownFrame.setHeight(GameUI.convertY(uiViewport, DEFAULT_INVENTORY_ICON_WIDTH));
				commandCardIcon.set(iconFrame, null, cooldownFrame, null, null, null);
				inventoryIcons[j][i] = commandCardIcon;
				commandCardIcon.clear();
				commandButtonIndex++;
			}
		}
		inventoryTitleFrame = rootFrame.createStringFrame("SmashInventoryText", inventoryBarFrame,
				new Color(0xFCDE12FF), TextJustify.CENTER, TextJustify.MIDDLE, 0.0109f);
		rootFrame.setText(inventoryTitleFrame, rootFrame.getTemplates().getDecoratedString("INVENTORY"));
		inventoryTitleFrame
				.addSetPoint(new SetPoint(FramePoint.TOPLEFT, inventoryBarFrame, FramePoint.TOPLEFT,
						GameUI.convertX(uiViewport, 0.004f), GameUI.convertY(uiViewport, 0.0165625f)));
		inventoryTitleFrame.setWidth(GameUI.convertX(uiViewport, 0.071f));
		inventoryTitleFrame.setHeight(GameUI.convertX(uiViewport, 0.01125f));
		inventoryTitleFrame.setFontShadowColor(new Color(0f, 0f, 0f, 0.9f));
		inventoryTitleFrame.setFontShadowOffsetX(GameUI.convertX(uiViewport, 0.001f));
		inventoryTitleFrame.setFontShadowOffsetY(GameUI.convertY(uiViewport, -0.001f));
		consoleInventoryNoCapacityTexture = ImageUtils.getAnyExtensionTexture(dataSource,
				rootFrame.getSkinField("ConsoleInventoryNoCapacity"));

		inventoryCover = rootFrame.createSimpleFrame("SmashConsoleInventoryCover", rootFrame, 0);

		final Element fontHeights = war3MapViewer.miscData.get("FontHeights");
		final float worldFrameMessageFontHeight = fontHeights.getFieldFloatValue("WorldFrameMessage");
		errorMessageFrame = rootFrame.createStringFrame("SmashErrorMessageFrame", rootFrame,
				new Color(0xFFCC00FF), TextJustify.LEFT, TextJustify.MIDDLE, worldFrameMessageFontHeight);
		errorMessageFrame.addAnchor(new AnchorDefinition(FramePoint.BOTTOMLEFT,
				GameUI.convertX(uiViewport, 0.212f), GameUI.convertY(uiViewport, 0.182f)));
		errorMessageFrame.setWidth(GameUI.convertX(uiViewport, 0.35f));
		errorMessageFrame.setHeight(GameUI.convertY(uiViewport, worldFrameMessageFontHeight));

		errorMessageFrame.setFontShadowColor(new Color(0f, 0f, 0f, 0.9f));
		errorMessageFrame.setFontShadowOffsetX(GameUI.convertX(uiViewport, 0.001f));
		errorMessageFrame.setFontShadowOffsetY(GameUI.convertY(uiViewport, -0.001f));
		errorMessageFrame.setVisible(false);

		final float worldFrameUnitMessageFontHeight = fontHeights.getFieldFloatValue("WorldFrameUnitMessage");
		gameMessagesFrame = rootFrame.createStringFrame("SmashUnitMessageFrame", rootFrame, Color.WHITE,
				TextJustify.LEFT, TextJustify.MIDDLE, worldFrameUnitMessageFontHeight);
		gameMessagesFrame.addAnchor(new AnchorDefinition(FramePoint.LEFT, 0, 0));
		gameMessagesFrame.setWidth(GameUI.convertX(uiViewport, 0.35f));
		gameMessagesFrame.setHeight(GameUI.convertY(uiViewport, worldFrameUnitMessageFontHeight));

		gameMessagesFrame.setFontShadowColor(new Color(0f, 0f, 0f, 0.9f));
		gameMessagesFrame.setFontShadowOffsetX(GameUI.convertX(uiViewport, 0.001f));
		gameMessagesFrame.setFontShadowOffsetY(GameUI.convertY(uiViewport, -0.001f));
		gameMessagesFrame.setVisible(true);

		commandButtonIndex = 0;
		for (int j = 0; j < COMMAND_CARD_HEIGHT; j++) {
			for (int i = 0; i < COMMAND_CARD_WIDTH; i++) {
				final CommandCardIcon commandCardIcon = new CommandCardIcon("SmashCommandButton_" + commandButtonIndex,
						rootFrame, this);
				rootFrame.add(commandCardIcon);
				final TextureFrame iconFrame = new TextureFrame("SmashCommandButton_" + (commandButtonIndex) + "_Icon",
						rootFrame, false, null);
				final FilterModeTextureFrame activeHighlightFrame = new FilterModeTextureFrame(
						"SmashCommandButton_" + (commandButtonIndex) + "_ActiveHighlight", rootFrame, true, null);
				activeHighlightFrame.setFilterMode(FilterMode.ADDALPHA);
				final TextureFrame numberOverlayFrame = new TextureFrame(
						"SmashCommandButton_" + (commandButtonIndex) + "_NumberOverlay", rootFrame, true, null);
				final SpriteFrame cooldownFrame = (SpriteFrame) rootFrame.createFrameByType("SPRITE",
						"SmashCommandButton_" + (commandButtonIndex) + "_Cooldown", rootFrame, "", 0);
				final SpriteFrame autocastFrame = (SpriteFrame) rootFrame.createFrameByType("SPRITE",
						"SmashCommandButton_" + (commandButtonIndex) + "_Autocast", rootFrame, "", 0);
				commandCardIcon.addAnchor(new AnchorDefinition(FramePoint.BOTTOMLEFT,
						GameUI.convertX(uiViewport, 0.6175f + (0.0434f * i)),
						GameUI.convertY(uiViewport, 0.095f - (0.044f * j))));
				commandCardIcon.setWidth(GameUI.convertX(uiViewport, DEFAULT_COMMAND_CARD_ICON_WIDTH));
				commandCardIcon.setHeight(GameUI.convertY(uiViewport, DEFAULT_COMMAND_CARD_ICON_WIDTH));
				iconFrame.addSetPoint(new SetPoint(FramePoint.CENTER, commandCardIcon, FramePoint.CENTER, 0, 0));
				iconFrame.setWidth(GameUI.convertX(uiViewport, DEFAULT_COMMAND_CARD_ICON_WIDTH));
				iconFrame.setHeight(GameUI.convertY(uiViewport, DEFAULT_COMMAND_CARD_ICON_WIDTH));
				iconFrame.setTexture(ImageUtils.DEFAULT_ICON_PATH, rootFrame);
				activeHighlightFrame
						.addSetPoint(new SetPoint(FramePoint.CENTER, commandCardIcon, FramePoint.CENTER, 0, 0));
				activeHighlightFrame.setWidth(GameUI.convertX(uiViewport, DEFAULT_COMMAND_CARD_ICON_WIDTH));
				activeHighlightFrame.setHeight(GameUI.convertY(uiViewport, DEFAULT_COMMAND_CARD_ICON_WIDTH));
				activeHighlightFrame.setTexture("CommandButtonActiveHighlight", rootFrame);

				numberOverlayFrame.addSetPoint(
						new SetPoint(FramePoint.BOTTOMRIGHT, commandCardIcon, FramePoint.BOTTOMRIGHT, 0, 0));
				numberOverlayFrame.setWidth(GameUI.convertX(uiViewport, DEFAULT_COMMAND_CARD_ICON_WIDTH) * 0.4f);
				numberOverlayFrame.setHeight(GameUI.convertY(uiViewport, DEFAULT_COMMAND_CARD_ICON_WIDTH) * 0.4f);
				numberOverlayFrame.setTexture("CommandButtonNumberOverlay", rootFrame);
				final SingleStringFrame numberOverlayStringFrame = new SingleStringFrame(
						"SmashCommandButton_NumberOverlayText", numberOverlayFrame, Color.WHITE, TextJustify.CENTER,
						TextJustify.BOTTOM, rootFrame.getFont());
//				numberOverlayStringFrame.addAnchor(new AnchorDefinition(FramePoint.CENTER, 0, 0));
				numberOverlayStringFrame.setSetAllPoints(true);
				cooldownFrame.addSetPoint(new SetPoint(FramePoint.CENTER, commandCardIcon, FramePoint.CENTER, 0, 0));
				rootFrame.setSpriteFrameModel(cooldownFrame, rootFrame.getSkinField("CommandButtonCooldown"));
				cooldownFrame.setWidth(GameUI.convertX(uiViewport, DEFAULT_COMMAND_CARD_ICON_WIDTH));
				cooldownFrame.setHeight(GameUI.convertY(uiViewport, DEFAULT_COMMAND_CARD_ICON_WIDTH));
				autocastFrame.addSetPoint(new SetPoint(FramePoint.CENTER, commandCardIcon, FramePoint.CENTER, 0, 0));
				rootFrame.setSpriteFrameModel(autocastFrame, rootFrame.getSkinField("CommandButtonAutocast"));
				autocastFrame.setWidth(GameUI.convertX(uiViewport, DEFAULT_COMMAND_CARD_ICON_WIDTH));
				autocastFrame.setHeight(GameUI.convertY(uiViewport, DEFAULT_COMMAND_CARD_ICON_WIDTH));
				commandCardIcon.set(iconFrame, activeHighlightFrame, cooldownFrame, autocastFrame, numberOverlayFrame,
						numberOverlayStringFrame);
				commandCard[j][i] = commandCardIcon;
				commandCardIcon.clear();
				commandButtonIndex++;
			}
		}

		tooltipFrame = rootFrame.createFrame("SmashToolTip", rootFrame, 0, 0);
		tooltipFrame.addAnchor(new AnchorDefinition(FramePoint.BOTTOMRIGHT, GameUI.convertX(uiViewport, 0.f),
				GameUI.convertY(uiViewport, 0.176f)));
		tooltipFrame.setWidth(GameUI.convertX(uiViewport, 0.280f));
		tooltipText = (StringFrame) rootFrame.getFrameByName("SmashToolTipText", 0);
		tooltipText.setWidth(GameUI.convertX(uiViewport, 0.274f));
		tooltipText.addAnchor(new AnchorDefinition(FramePoint.TOPLEFT, GameUI.convertX(uiViewport, 0.003f),
				GameUI.convertY(uiViewport, -0.003f)));
		tooltipFrame.setVisible(false);

		hovertipFrame = rootFrame.createFrame("SmashHoverTip", rootFrame, 0, 0);
		hovertipText = (StringFrame) rootFrame.getFrameByName("SmashHoverTipText", 0);
		hovertipText.setWidth(GameUI.convertX(uiViewport, 0.274f));
		hovertipText.addAnchor(new AnchorDefinition(FramePoint.TOPLEFT, GameUI.convertX(uiViewport, 0.006f),
				GameUI.convertY(uiViewport, -0.006f)));
		hovertipFrame.setVisible(false);

		tooltipUberTipText = (StringFrame) rootFrame.getFrameByName("SmashUberTipText", 0);
		tooltipUberTipText.setWidth(GameUI.convertX(uiViewport, 0.274f));
		uberTipNoResourcesSetPoint = new SetPoint(FramePoint.TOPLEFT, tooltipText, FramePoint.BOTTOMLEFT, 0,
				GameUI.convertY(uiViewport, -0.004f));
		uberTipWithResourcesSetPoint = new SetPoint(FramePoint.TOPLEFT, tooltipText, FramePoint.BOTTOMLEFT, 0,
				GameUI.convertY(uiViewport, -0.014f));
		tooltipUberTipText.addSetPoint(uberTipNoResourcesSetPoint);
		tooltipResourceFrames = new UIFrame[ResourceType.VALUES.length];
		tooltipResourceIconFrames = new TextureFrame[ResourceType.VALUES.length];
		tooltipResourceTextFrames = new StringFrame[ResourceType.VALUES.length];
		for (int i = 0; i < tooltipResourceFrames.length; i++) {
			tooltipResourceFrames[i] = rootFrame.createFrame("SmashToolTipIconResource", tooltipFrame, 0,
					i);
			tooltipResourceIconFrames[i] = (TextureFrame) rootFrame
					.getFrameByName("SmashToolTipIconResourceBackdrop", i);
			tooltipResourceTextFrames[i] = (StringFrame) rootFrame
					.getFrameByName("SmashToolTipIconResourceLabel", i);
			tooltipResourceFrames[i].addSetPoint(new SetPoint(FramePoint.TOPLEFT, tooltipText,
					FramePoint.BOTTOMLEFT, GameUI.convertX(uiViewport, 0.004f + (0.032f * i)),
					GameUI.convertY(uiViewport, -0.001f)));
			// have we really no better API than the below???
			((AbstractUIFrame) tooltipFrame).add(tooltipResourceFrames[i]);
			rootFrame.remove(tooltipResourceFrames[i]);
		}
//		this.tooltipFrame = this.rootFrame.createFrameByType("BACKDROP", "SmashToolTipBackdrop", this.rootFrame, "", 0);

		cursorFrame = (SpriteFrame) rootFrame.createFrameByType("SPRITE", "SmashCursorFrame", rootFrame,
				"", 0);
		rootFrame.setSpriteFrameModel(cursorFrame, rootFrame.getSkinField("Cursor"));
		cursorFrame.setSequence("Normal");
		cursorFrame.setZDepth(1.0f);
		if (WarsmashConstants.CATCH_CURSOR) {
			Gdx.input.setCursorCatched(true);
		}

		meleeUIMinimap = createMinimap(war3MapViewer);

		meleeUIAbilityActivationReceiver = new MeleeUIAbilityActivationReceiver(
				new AbilityActivationErrorHandler(war3MapViewer.getLocalPlayerIndex(),
						rootFrame.getErrorString("NoGold"),
						war3MapViewer.getUiSounds().getSound(rootFrame.getSkinField("NoGoldSound"))),
				new AbilityActivationErrorHandler(war3MapViewer.getLocalPlayerIndex(),
						rootFrame.getErrorString("NoLumber"),
						war3MapViewer.getUiSounds().getSound(rootFrame.getSkinField("NoLumberSound"))),
				new AbilityActivationErrorHandler(war3MapViewer.getLocalPlayerIndex(),
						rootFrame.getErrorString("NoFood"),
						war3MapViewer.getUiSounds().getSound(rootFrame.getSkinField("NoFoodSound"))),
				new AbilityActivationErrorHandler(war3MapViewer.getLocalPlayerIndex(),
						rootFrame.getErrorString("Nomana"),
						war3MapViewer.getUiSounds().getSound(rootFrame.getSkinField("NoManaSound"))),
				new AbilityActivationErrorHandler(war3MapViewer.getLocalPlayerIndex(), "",
						war3MapViewer.getUiSounds().getSound("InterfaceError")),
				new AbilityActivationErrorHandler(war3MapViewer.getLocalPlayerIndex(),
						rootFrame.getErrorString("Cooldown"),
						war3MapViewer.getUiSounds().getSound("InterfaceError")));

		final MdxModel rallyModel = war3MapViewer.loadModelMdx(rootFrame.getSkinField("RallyIndicatorDst"));
		rallyPointInstance = (MdxComplexInstance) rallyModel.addInstance();
		rallyPointInstance.rotate(RenderUnit.tempQuat.setFromAxis(RenderMathUtils.VEC3_UNIT_Z,
				war3MapViewer.simulation.getGameplayConstants().getBuildingAngle()));
		rallyPointInstance.setSequenceLoopMode(SequenceLoopMode.ALWAYS_LOOP);
		SequenceUtils.randomStandSequence(rallyPointInstance);
		rallyPointInstance.hide();
		waypointModel = war3MapViewer.loadModelMdx(rootFrame.getSkinField("WaypointIndicator"));

		final FreeTypeFontParameter fontParam = new FreeTypeFontParameter();
		fontParam.size = (int) GameUI.convertY(uiViewport, 0.012f);
		textTagFont = rootFrame.getFontGenerator().generateFont(fontParam);

		rootFrame.positionBounds(rootFrame, uiViewport);

		selectUnit(null);

	}

	private void updateEscMenuCurrentPanel(final UIFrame escMenuBackdrop, final UIFrame escMenuMainPanel,
			final UIFrame escMenuInnerMainPanel) {
		smashEscMenu.setWidth(escMenuInnerMainPanel.getAssignedWidth());
		smashEscMenu.setHeight(escMenuInnerMainPanel.getAssignedHeight());
		escMenuBackdrop.setWidth(escMenuInnerMainPanel.getAssignedWidth());
		escMenuBackdrop.setHeight(escMenuInnerMainPanel.getAssignedHeight());
		smashEscMenu.positionBounds(rootFrame, uiViewport);

	}

	@Override
	public void onClick(final int abilityHandleId, final int orderId, final boolean rightClick) {
		if (selectedUnit == null) {
			return;
		}
		if (orderId == 0) {
			return;
		}
		// TODO not O(N)
		CAbilityView abilityToUse = selectedUnit.getSimulationUnit()
				.getAbilities()
				.stream()
				.filter(ability -> ability.getHandleId() == abilityHandleId)
				.findFirst()
				.orElse(null);
		if (abilityToUse != null) {
			abilityToUse.checkCanUse(war3MapViewer.simulation, selectedUnit.getSimulationUnit(), orderId,
					meleeUIAbilityActivationReceiver.reset(this, war3MapViewer.worldScene.audioContext,
							selectedUnit));
			if (meleeUIAbilityActivationReceiver.isUseOk()) {
				final BooleanAbilityTargetCheckReceiver<Void> noTargetReceiver = BooleanAbilityTargetCheckReceiver
						.<Void>getInstance().reset();
				abilityToUse.checkCanTargetNoTarget(war3MapViewer.simulation,
						selectedUnit.getSimulationUnit(), orderId, noTargetReceiver);
				if (noTargetReceiver.isTargetable()) {
					final boolean shiftDown = isShiftDown();
					unitOrderListener.issueImmediateOrder(selectedUnit.getSimulationUnit().getHandleId(),
							abilityHandleId, orderId, shiftDown);
					if (abilityToUse instanceof CAbilityHero) {
						if ((((CAbilityHero) abilityToUse).getSkillPoints() <= 1) && (orderId != OrderIds.skillmenu)) {
							// using up the last skill point, so close the menu
							// TODO this is kind of a stupid hack and should probably be improved later
							openMenu(0);
						}
					}
					if (selectedUnits.size() > 1) {
						for (final RenderUnit otherSelectedUnit : selectedUnits) {
							if (!Objects.equals(otherSelectedUnit, activeCommandUnit)) {
								abilityToUse = null;
								for (final CAbility ability : otherSelectedUnit.getSimulationUnit().getAbilities()) {
									final BooleanAbilityTargetCheckReceiver<Void> receiver = BooleanAbilityTargetCheckReceiver
											.<Void>getInstance().reset();
									ability.checkCanTargetNoTarget(war3MapViewer.simulation,
											otherSelectedUnit.getSimulationUnit(), activeCommandOrderId, receiver);
									if (receiver.isTargetable()) {
										abilityToUse = ability;
									}
								}
								if (abilityToUse != null) {
									unitOrderListener.issueImmediateOrder(
											otherSelectedUnit.getSimulationUnit().getHandleId(),
											abilityToUse.getHandleId(), activeCommandOrderId, shiftDown);
								}
							}
						}
					}
				}
				else {
					activeCommand = abilityToUse;
					activeCommandOrderId = orderId;
					activeCommandUnit = selectedUnit;
					clearAndRepopulateCommandCard();
				}
			}
		}
		else {
			unitOrderListener.issueImmediateOrder(selectedUnit.getSimulationUnit().getHandleId(),
					abilityHandleId, orderId, isShiftDown());
			if (selectedUnits.size() > 1) {
				for (final RenderUnit otherSelectedUnit : selectedUnits) {
					if (!Objects.equals(otherSelectedUnit, activeCommandUnit)) {
						unitOrderListener.issueImmediateOrder(otherSelectedUnit.getSimulationUnit().getHandleId(),
								abilityHandleId, orderId, isShiftDown());
					}
				}
			}
		}
		if (rightClick) {
			war3MapViewer.getUiSounds().getSound("AutoCastButtonClick").play(uiScene.audioContext, 0, 0, 0);
		}
	}

	@Override
	public void openMenu(final int orderId) {
		if (orderId == 0) {
			subMenuOrderIdStack.clear();
			activeCommandUnit = null;
			activeCommand = null;
			activeCommandOrderId = -1;
		}
		else {
			subMenuOrderIdStack.add(orderId);
		}
		clearAndRepopulateCommandCard();
	}

	@Override
	public void showCommandError(final int playerIndex, final String message) {
		if (playerIndex == war3MapViewer.getLocalPlayerIndex()) {
			rootFrame.setText(errorMessageFrame, message);
			errorMessageFrame.setVisible(true);
			final long millis = TimeUtils.millis();
			lastErrorMessageExpireTime = millis + WORLD_FRAME_MESSAGE_EXPIRE_MILLIS;
			lastErrorMessageFadeTime = millis + WORLD_FRAME_MESSAGE_FADEOUT_MILLIS;
			errorMessageFrame.setAlpha(1.0f);
		}
	}

	public void showGameMessage(final String message, final float expireTime) {
		rootFrame.setText(gameMessagesFrame, message);
		gameMessagesFrame.setVisible(true);
		final long millis = TimeUtils.millis();

		lastGameMessageExpireTime = millis + (long) (expireTime * 1000);
		lastGameMessageFadeTime = millis + (long) (expireTime * 900);
		gameMessagesFrame.setAlpha(1.0f);
	}

	@Override
	public void showCantPlaceError(final int playerIndex) {
		if (playerIndex == war3MapViewer.getLocalPlayerIndex()) {
			showCommandError(playerIndex, rootFrame.getErrorString("Cantplace"));
			war3MapViewer.getUiSounds().getSound(rootFrame.getSkinField("CantPlaceSound"))
					.play(uiScene.audioContext, 0, 0, 0);
		}
	}

	@Override
	public void showNoFoodError(final int playerIndex) {
		if (playerIndex == war3MapViewer.getLocalPlayerIndex()) {
			showCommandError(playerIndex, rootFrame.getErrorString("NoFood"));
			war3MapViewer.getUiSounds().getSound(rootFrame.getSkinField("NoFoodSound"))
					.play(uiScene.audioContext, 0, 0, 0);
		}
	}

	@Override
	public void showNoManaError(final int playerIndex) {
		if (playerIndex == war3MapViewer.getLocalPlayerIndex()) {
			showCommandError(playerIndex, rootFrame.getErrorString("Nomana"));
			war3MapViewer.getUiSounds().getSound(rootFrame.getSkinField("NoManaSound"))
					.play(uiScene.audioContext, 0, 0, 0);
		}
	}

	@Override
	public void showUnableToFindCoupleTargetError(final int playerIndex) {
		if (playerIndex == war3MapViewer.getLocalPlayerIndex()) {
			showCommandError(playerIndex, rootFrame.getErrorString("Cantfindcoupletarget"));
			war3MapViewer.getUiSounds().getSound("InterfaceError").play(uiScene.audioContext, 0, 0, 0);
		}
	}

	@Override
	public void showInventoryFullError(final int playerIndex) {
		if (playerIndex == war3MapViewer.getLocalPlayerIndex()) {
			showCommandError(playerIndex, rootFrame.getErrorString("InventoryFull"));
			war3MapViewer.getUiSounds().getSound(rootFrame.getSkinField("InventoryFullSound"))
					.play(uiScene.audioContext, 0, 0, 0);
		}
	}

	@Override
	public void showBlightRingFullError(final int playerIndex) {
		if (playerIndex == war3MapViewer.getLocalPlayerIndex()) {
			showCommandError(playerIndex, rootFrame.getErrorString("Blightringfull"));
			war3MapViewer.getUiSounds().getSound("InterfaceError").play(uiScene.audioContext, 0, 0, 0);
		}
	}

	public void update(final float deltaTime) {
		portrait.update();

		final int baseMouseX = Gdx.input.getX();
		int mouseX = baseMouseX;
		final int baseMouseY = Gdx.input.getY();
		int mouseY = baseMouseY;
		final int minX = uiViewport.getScreenX();
		final int maxX = minX + uiViewport.getScreenWidth();
		final int minY = uiViewport.getScreenY();
		final int maxY = minY + uiViewport.getScreenHeight();
		final boolean left = (mouseX <= (minX + 3)) && WarsmashConstants.CATCH_CURSOR;
		final boolean right = (mouseX >= (maxX - 3)) && WarsmashConstants.CATCH_CURSOR;
		final boolean up = (mouseY <= (minY + 3)) && WarsmashConstants.CATCH_CURSOR;
		final boolean down = (mouseY >= (maxY - 3)) && WarsmashConstants.CATCH_CURSOR;
		cameraManager.applyVelocity(deltaTime, up, down, left, right);

		mouseX = Math.max(minX, Math.min(maxX, mouseX));
		mouseY = Math.max(minY, Math.min(maxY, mouseY));
		if (Gdx.input.isCursorCatched()) {
			if (WarsmashConstants.CATCH_CURSOR) {
				Gdx.input.setCursorPosition(mouseX, mouseY);
			}
		}
		hpBarFrameIndex = 0;
		if (currentlyDraggingPointer == -1) {
			if ((mouseOverUnit != null) && isUnitSelectable(mouseOverUnit)) {
				final SimpleStatusBarFrame simpleStatusBarFrame = getHpBar();
				positionHealthBar(simpleStatusBarFrame, mouseOverUnit, 1.0f);
				final String hoverTipTextValue = getWorldFrameHoverTipText(mouseOverUnit);
				hovertipFrame.setVisible(hoverTipTextValue != null);
				if (hoverTipTextValue != null) {
					rootFrame.setText(hovertipText, hoverTipTextValue);
					final float predictedViewportHeight = hovertipText.getPredictedViewportHeight()
							+ GameUI.convertY(uiViewport, 0.009f);
					hovertipFrame.setHeight(predictedViewportHeight);
					hovertipFrame.setWidth(
							hovertipText.getPredictedViewportWidth() + GameUI.convertX(uiViewport, 0.012f));
					hovertipFrame.positionBounds(rootFrame, uiViewport);
					hovertipFrame.addSetPoint(new SetPoint(FramePoint.BOTTOM, simpleStatusBarFrame, FramePoint.TOP,
							0, GameUI.convertY(uiViewport, 0.003f)));
				}
				if (mouseOverUnit.getSimulationWidget().isInvulnerable()
						|| (mouseOverUnit instanceof RenderItem)) {
					// this is a bit silly, for now I'm using it to position the "Gold" text on gold
					// mines even though they are invulnerable
					simpleStatusBarFrame.setVisible(false);
				}
			}
			else {
				hovertipFrame.setVisible(false);
			}
		}
		else if (currentlyDraggingPointer == Input.Buttons.LEFT) {
			final float minDragX = Math.min(lastMouseClickLocation.x, lastMouseDragStart.x);
			final float minDragY = Math.min(lastMouseClickLocation.y, lastMouseDragStart.y);
			final float maxDragX = Math.max(lastMouseClickLocation.x, lastMouseDragStart.x);
			final float maxDragY = Math.max(lastMouseClickLocation.y, lastMouseDragStart.y);
			tempRect.set(minDragX, minDragY, maxDragX - minDragX, maxDragY - minDragY);
			dragSelectPreviewUnitsUpcoming.clear();
			war3MapViewer.simulation.getWorldCollision().enumUnitsInRect(tempRect, unit -> {
				final RenderUnit renderUnit = war3MapViewer.getRenderPeer(unit);
				if (!unit.isDead() && renderUnit.isSelectable()
						&& dragSelectPreviewUnitsUpcoming.add(renderUnit)) {
					final SimpleStatusBarFrame simpleStatusBarFrame = getHpBar();
					if (!unit.isInvulnerable()) {
						positionHealthBar(simpleStatusBarFrame, renderUnit, 1.0f);
					}
					if (!dragSelectPreviewUnits.contains(renderUnit)) {
						war3MapViewer.showUnitMouseOverHighlight(renderUnit);
					}
				}
				return false;
			});
			for (final RenderUnit unit : dragSelectPreviewUnits) {
				if (!dragSelectPreviewUnitsUpcoming.contains(unit)) {
					war3MapViewer.clearUnitMouseOverHighlight(unit);
				}
			}
			final Set<RenderUnit> temp = dragSelectPreviewUnits;
			dragSelectPreviewUnits = dragSelectPreviewUnitsUpcoming;
			dragSelectPreviewUnitsUpcoming = temp;
		}
		if (false) {
			for (final RenderUnit unit : selectedUnits) {
				final SimpleStatusBarFrame simpleStatusBarFrame = getHpBar();
				positionHealthBar(simpleStatusBarFrame, unit, 1.0f);
			}
		}
		for (int i = hpBarFrameIndex; i < hpBarFrames.size(); i++) {
			hpBarFrames.get(i).setVisible(false);
		}

		screenCoordsVector.set(mouseX, mouseY);
		uiViewport.unproject(screenCoordsVector);
		cursorFrame.setFramePointX(FramePoint.LEFT, screenCoordsVector.x);
		cursorFrame.setFramePointY(FramePoint.BOTTOM, screenCoordsVector.y);

		if (activeCommand != null) {
			if (draggingItem != null) {
				setCursorState(MenuCursorState.HOLD_ITEM);
			}
			else {
				setCursorState(MenuCursorState.TARGET_CURSOR);
				activeCommand.visit(cursorTargetSetupVisitor.reset(baseMouseX, baseMouseY));
			}
		}
		else {
			if (cursorModelInstance != null) {
				cursorModelInstance.detach();
				cursorModelInstance = null;
				cursorFrame.setVisible(true);
			}
			if (placementCursor != null) {
				placementCursor.destroy(Gdx.gl30, war3MapViewer.terrain.centerOffset);
				placementCursor = null;
				cursorFrame.setVisible(true);
			}
			if (cursorModelUnderneathPathingRedGreenSplatModel != null) {
				war3MapViewer.terrain.removeSplatBatchModel(BUILDING_PATHING_PREVIEW_KEY);
				cursorModelUnderneathPathingRedGreenSplatModel = null;
			}
			if (down) {
				if (left) {
					setCursorState(MenuCursorState.SCROLL_DOWN_LEFT);
				}
				else if (right) {
					setCursorState(MenuCursorState.SCROLL_DOWN_RIGHT);
				}
				else {
					setCursorState(MenuCursorState.SCROLL_DOWN);
				}
			}
			else if (up) {
				if (left) {
					setCursorState(MenuCursorState.SCROLL_UP_LEFT);
				}
				else if (right) {
					setCursorState(MenuCursorState.SCROLL_UP_RIGHT);
				}
				else {
					setCursorState(MenuCursorState.SCROLL_UP);
				}
			}
			else if (left) {
				setCursorState(MenuCursorState.SCROLL_LEFT);
			}
			else if (right) {
				setCursorState(MenuCursorState.SCROLL_RIGHT);
			}
			else if (mouseOverUnit != null) {
				if (mouseOverUnit instanceof RenderUnit) {
					final RenderUnit mouseOverUnitUnit = (RenderUnit) mouseOverUnit;
					final int playerIndex = mouseOverUnitUnit.getSimulationUnit().getPlayerIndex();
					if (!localPlayer.hasAlliance(playerIndex, CAllianceType.PASSIVE)) {
						setCursorState(MenuCursorState.SELECT, Color.RED);
					}
					else if (localPlayer.hasAlliance(playerIndex, CAllianceType.SHARED_CONTROL)) {
						setCursorState(MenuCursorState.SELECT, Color.GREEN);
					}
					else {
						setCursorState(MenuCursorState.SELECT, Color.YELLOW);
					}
				}
				else {
					setCursorState(MenuCursorState.SELECT, Color.YELLOW);
				}
			}
			else {
				setCursorState(MenuCursorState.NORMAL);
			}
		}
		if (selectedUnit != null) {
			if (simpleBuildTimeIndicator.isVisible()) {
				simpleBuildTimeIndicator
						.setValue(Math.min(selectedUnit.getSimulationUnit().getConstructionProgress()
								/ selectedUnit.getSimulationUnit().getUnitType().getBuildTime(), 0.99f));
			}
			if (simpleBuildingBuildTimeIndicator.isVisible()) {
				simpleBuildingBuildTimeIndicator
						.setValue(Math.min(
								selectedUnit.getSimulationUnit().getConstructionProgress() / selectedUnit
										.getSimulationUnit().getBuildQueueTimeRemaining(war3MapViewer.simulation),
								0.99f));
			}
		}

		final float groundHeight = Math.max(
				war3MapViewer.terrain.getGroundHeight(cameraManager.target.x, cameraManager.target.y),
				war3MapViewer.terrain.getWaterHeight(cameraManager.target.x, cameraManager.target.y));
		cameraManager.updateTargetZ(groundHeight);
		cameraManager.updateCamera();
		final long currentMillis = TimeUtils.millis();
		if (currentMillis > lastErrorMessageExpireTime) {
			errorMessageFrame.setVisible(false);
		}
		else if (currentMillis > lastErrorMessageFadeTime) {
			final float fadeAlpha = (lastErrorMessageExpireTime - currentMillis)
					/ (float) WORLD_FRAME_MESSAGE_FADE_DURATION;
			errorMessageFrame.setAlpha(fadeAlpha);
		}
		if (currentMillis > lastGameMessageExpireTime) {
			gameMessagesFrame.setVisible(false);
		}
		else if (currentMillis > lastGameMessageFadeTime) {
			final float fadeAlpha = (lastGameMessageExpireTime - currentMillis)
					/ (float) (lastGameMessageExpireTime - lastGameMessageFadeTime);
			gameMessagesFrame.setAlpha(fadeAlpha);
		}
		if (currentMusics != null) {
			if (!currentMusics[currentMusicIndex].isPlaying()) {
				if (currentMusicRandomizeIndex) {
					currentMusicIndex = (int) (Math.random() * currentMusics.length);
				}
				else {
					currentMusicIndex = (currentMusicIndex + 1) % currentMusics.length;
				}
				currentMusics[currentMusicIndex].play();
			}
		}
		for (final CTimerDialog timerDialog : timerDialogs) {
			timerDialog.update(rootFrame, war3MapViewer.simulation);
		}
	}

	private static boolean isUnitSelectable(final RenderWidget mouseOverUnit) {
		return mouseOverUnit.isSelectable() && !mouseOverUnit.getSimulationWidget().isDead();
	}

	private String getWorldFrameHoverTipText(final RenderWidget whichUnit) {
		if (whichUnit instanceof RenderUnit) {
			final RenderUnit renderUnit = (RenderUnit) whichUnit;
			final CUnit simulationUnit = renderUnit.getSimulationUnit();
			final CAbilityHero heroData = simulationUnit.getHeroData();
			if (heroData != null) {
				final String level = rootFrame.getTemplates().getDecoratedString("LEVEL");
				return heroData.getProperName() + "|n" + level + ' ' + heroData.getHeroLevel();
			}
			final boolean neutralHostile = simulationUnit.getPlayerIndex() == (WarsmashConstants.MAX_PLAYERS - 4);
			final boolean neutralPassive = simulationUnit.getPlayerIndex() == (WarsmashConstants.MAX_PLAYERS - 1);
			if ((neutralPassive && simulationUnit.isBuilding()) || neutralHostile) {
				String returnValue = simulationUnit.getUnitType().getName();
				final CAbilityGoldMine goldMineData = simulationUnit.getGoldMineData();
				if (goldMineData != null) {
					final String colonGold = rootFrame.getTemplates().getDecoratedString("COLON_GOLD");
					returnValue += "|n" + colonGold + ' ' + goldMineData.getGold();
				}
				final int creepLevel = simulationUnit.getUnitType().getLevel();
				if (neutralHostile && (creepLevel > 0)) {
					final String level = rootFrame.getTemplates().getDecoratedString("LEVEL");
					returnValue += "|n" + level + ' ' + creepLevel;
				}
				return returnValue;
			}
		}
		else if (whichUnit instanceof RenderItem) {
			final RenderItem renderItem = (RenderItem) whichUnit;
			final ItemUI itemUI = war3MapViewer.getAbilityDataUI()
					.getItemUI(renderItem.getSimulationItem().getTypeId());
			return itemUI.getName();
		}
		else if (whichUnit instanceof RenderDestructable) {
			final RenderDestructable renderDest = (RenderDestructable) whichUnit;
			final String name = renderDest.getSimulationDestructable().getDestType().getName();
			return name;
		}
		return null;
	}

	private void positionHealthBar(final SimpleStatusBarFrame simpleStatusBarFrame, final RenderWidget unit,
			final float alpha) {
		simpleStatusBarFrame.setVisible(true);
		clickLocationTemp.x = unit.getX();
		clickLocationTemp.y = unit.getY();
		clickLocationTemp.z = unit.getZ();
		final Bounds unitBounds = unit.getInstance().getBounds();
		if (unitBounds != null) {
			final BoundingBox unitBoundsBox = unitBounds.getBoundingBox();
			if (unitBoundsBox != null) {
				clickLocationTemp.z += unitBoundsBox.max.z;
			}
		}
		war3MapViewer.worldScene.camera.worldToScreen(screenCoordsVector, clickLocationTemp);
		simpleStatusBarFrame.getBarFrame().setTexture("SimpleHpBarConsole", rootFrame);
		simpleStatusBarFrame.getBorderFrame().setTexture("Textures\\Black32.blp", rootFrame);
		simpleStatusBarFrame.getBorderFrame().setColor(0f, 0f, 0f, alpha);
		final float lifeRatioRemaining = unit.getSimulationWidget().getLife() / unit.getSimulationWidget().getMaxLife();
		simpleStatusBarFrame.getBarFrame().setColor(Math.min(1.0f, 2.0f - (lifeRatioRemaining * 2)),
				Math.min(1.0f, lifeRatioRemaining * 2), 0, alpha);
		final Vector2 unprojected = uiViewport.unproject(screenCoordsVector);
		simpleStatusBarFrame.setWidth((unit.getSelectionScale() * 1.5f * Gdx.graphics.getWidth()) / 2560);
		simpleStatusBarFrame.setHeight(16);
		simpleStatusBarFrame.addSetPoint(
				new SetPoint(FramePoint.CENTER, rootFrame, FramePoint.BOTTOMLEFT, unprojected.x, unprojected.y));
		simpleStatusBarFrame.setValue(lifeRatioRemaining);
		simpleStatusBarFrame.positionBounds(rootFrame, uiViewport);
	}

	private SimpleStatusBarFrame getHpBar() {
		final SimpleStatusBarFrame simpleStatusBarFrame;
		if (hpBarFrameIndex >= hpBarFrames.size()) {
			simpleStatusBarFrame = new SimpleStatusBarFrame("SmashHpBar" + hpBarFrameIndex, rootFrame, true,
					true, 3.0f);
			rootFrame.add(simpleStatusBarFrame);
			hpBarFrames.add(simpleStatusBarFrame);
		}
		else {
			simpleStatusBarFrame = hpBarFrames.get(hpBarFrameIndex);
		}
		hpBarFrameIndex++;
		return simpleStatusBarFrame;
	}

	private void setCursorState(final MenuCursorState state, final Color color) {
		if (state != cursorState) {
			if (state.getAnimationName() != null) {
				cursorFrame.setSequence(state.getAnimationName());
			}
		}
		if (!Objects.equals(color, cursorColor)) {
			cursorFrame.setVertexColor(color);
		}
		cursorState = state;
	}

	private void setCursorState(final MenuCursorState state) {
		setCursorState(state, Color.WHITE);
	}

	public void render(final SpriteBatch batch, final GlyphLayout glyphLayout) {
		final BitmapFont font = rootFrame.getFont();
		font.setColor(Color.YELLOW);
		final String fpsString = "FPS: " + Gdx.graphics.getFramesPerSecond();
		glyphLayout.setText(font, fpsString);
		font.draw(batch, fpsString, (uiViewport.getMinWorldWidth() - glyphLayout.width) / 2,
				1100 * heightRatioCorrection);
		rootFrame.render(batch, rootFrame.getFont20(), glyphLayout);
		if (selectedUnit != null) {
			rootFrame.getFont20().setColor(Color.WHITE);

		}

		meleeUIMinimap.render(batch, war3MapViewer.units);
		timeIndicator.setFrameByRatio(war3MapViewer.simulation.getGameTimeOfDay()
				/ war3MapViewer.simulation.getGameplayConstants().getGameDayHours());
		for (final TextTag textTag : war3MapViewer.textTags) {
			war3MapViewer.worldScene.camera.worldToScreen(screenCoordsVector, textTag.getPosition());
			if (war3MapViewer.worldScene.camera.rect.contains(screenCoordsVector.x,
					(Gdx.graphics.getHeight() - screenCoordsVector.y) + textTag.getScreenCoordsZHeight())) {
				final Vector2 unprojected = uiViewport.unproject(screenCoordsVector);
				final float remainingLife = textTag.getRemainingLife();
				final float alpha = (Math.min(remainingLife, 1.0f));
				textTagFont.setColor(textTag.getColor().r, textTag.getColor().g, textTag.getColor().b,
						textTag.getColor().a * alpha);
				glyphLayout.setText(textTagFont, textTag.getText());
				textTagFont.draw(batch, textTag.getText(), unprojected.x - (glyphLayout.width / 2),
						(unprojected.y - (glyphLayout.height / 2)) + textTag.getScreenCoordsZHeight());
			}
		}
		if (currentlyDraggingPointer == Input.Buttons.LEFT) {
			batch.end();
			shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
			shapeRenderer.setColor(Color.GREEN);
			Gdx.gl.glLineWidth(2);
			shapeRenderer.begin(ShapeType.Line);
			cameraManager.camera.worldToScreen(screenCoordsVector, lastMouseDragStart);
			final Vector2 unprojected = uiViewport.unproject(screenCoordsVector);
			final float x = unprojected.x;
			final float y = unprojected.y;
			cameraManager.camera.worldToScreen(screenCoordsVector, lastMouseClickLocation);
			final Vector2 unprojectedEnd = uiViewport.unproject(screenCoordsVector);
			final float minX = Math.min(x, unprojectedEnd.x);
			final float minY = Math.min(y, unprojectedEnd.y);
			shapeRenderer.rect(minX, minY, Math.max(x, unprojectedEnd.x) - minX,
					Math.max(y, unprojectedEnd.y) - minY);
			shapeRenderer.end();
			Gdx.gl.glLineWidth(1);
			batch.begin();
		}
	}

	public void portraitTalk() {
		portrait.talk();
	}

	private final class AnyClickableUnitFilter implements CWidgetFilterFunction {
		@Override
		public boolean call(final CWidget unit) {
			final RenderWidget renderPeer = war3MapViewer.getRenderPeer(unit);
			return !unit.isDead() && renderPeer.isSelectable();
		}
	}

	private static final class AnyTargetableUnitFilter implements CWidgetFilterFunction {
		@Override
		public boolean call(final CWidget unit) {
			return !unit.isDead();
		}
	}

	private final class CursorTargetSetupVisitor implements CAbilityVisitor<Void> {
		private int baseMouseX;
		private int baseMouseY;

		private CursorTargetSetupVisitor reset(final int baseMouseX, final int baseMouseY) {
			this.baseMouseX = baseMouseX;
			this.baseMouseY = baseMouseY;
			return this;
		}

		@Override
		public Void accept(final CAbilityAttack ability) {
			if (activeCommandOrderId == OrderIds.attackground) {
				float radius = 0;
				for (final CUnitAttack attack : activeCommandUnit.getSimulationUnit().getAttacks()) {
					if (attack.getWeaponType().isAttackGroundSupported()) {
						if (attack instanceof CUnitAttackMissileSplash) {
							final int areaOfEffectSmallDamage = ((CUnitAttackMissileSplash) attack)
									.getAreaOfEffectSmallDamage();
							radius = areaOfEffectSmallDamage;
						}
					}
				}
				handlePlacementCursor(ability, radius);
			}
			else {
				handleTargetCursor(ability);
			}
			return null;
		}

		@Override
		public Void accept(final CAbilityMove ability) {
			handleTargetCursor(ability);
			return null;
		}

		@Override
		public Void accept(final CAbilityOrcBuild ability) {
			handleBuildCursor(ability);
			return null;
		}

		@Override
		public Void accept(final CAbilityHumanBuild ability) {
			handleBuildCursor(ability);
			return null;
		}

		@Override
		public Void accept(final CAbilityUndeadBuild ability) {
			handleBuildCursor(ability);
			return null;
		}

		@Override
		public Void accept(final CAbilityNightElfBuild ability) {
			handleBuildCursor(ability);
			return null;
		}

		@Override
		public Void accept(final CAbilityGenericDoNothing ability) {
			handleTargetCursor(ability);
			return null;
		}

		@Override
		public Void accept(final CAbilityColdArrows ability) {
			handleTargetCursor(ability);
			return null;
		}

		@Override
		public Void accept(final CAbilityNagaBuild ability) {
			handleBuildCursor(ability);
			return null;
		}

		@Override
		public Void accept(final CAbilityNeutralBuild ability) {
			handleBuildCursor(ability);
			return null;
		}

		@Override
		public Void accept(final CAbilityBuildInProgress ability) {
			handleTargetCursor(ability);
			return null;
		}

		@Override
		public Void accept(final CAbilityQueue ability) {
			handleTargetCursor(ability);
			return null;
		}

		@Override
		public Void accept(final CAbilityUpgrade ability) {
			handleTargetCursor(ability);
			return null;
		}

		@Override
		public Void accept(final CAbilityReviveHero ability) {
			handleTargetCursor(ability);
			return null;
		}

		@Override
		public Void accept(final GenericSingleIconActiveAbility ability) {
			handleTargetCursor(ability);
			return null;
		}

		@Override
		public Void accept(final GenericNoIconAbility ability) {
			// this should probably never happen
			handleTargetCursor(ability);
			return null;
		}

		@Override
		public Void accept(final CAbilityReturnResources ability) {
			// this should probably never happen
			handleTargetCursor(ability);
			return null;
		}

		@Override
		public Void accept(final CAbilityRally ability) {
			handleTargetCursor(ability);
			return null;
		}

		@Override
		public Void accept(final CAbilityHero ability) {
			handleTargetCursor(ability);
			return null;
		}

		private void handleTargetCursor(final CAbility ability) {
			if (cursorModelInstance != null) {
				cursorModelInstance.detach();
				cursorModelInstance = null;
				cursorFrame.setVisible(true);
			}
			cursorFrame.setSequence("Target");
		}

		private void handleBuildCursor(final AbstractCAbilityBuild ability) {
			boolean justLoaded = false;
			final War3MapViewer viewer = war3MapViewer;
			if (cursorModelInstance == null) {
				final MutableObjectData unitData = viewer.getAllObjectData().getUnits();
				final War3ID buildingTypeId = new War3ID(activeCommandOrderId);
				cursorBuildingUnitType = viewer.simulation.getUnitData().getUnitType(buildingTypeId);
				final String unitModelPath = viewer.getUnitModelPath(unitData.get(buildingTypeId));
				final MdxModel model = viewer.loadModelMdx(unitModelPath);
				cursorModelInstance = (MdxComplexInstance) model.addInstance();
//				MeleeUI.this.cursorModelInstance.setVertexColor(new float[] { 1, 1, 1, 0.5f });
				final int playerColorIndex = viewer.simulation
						.getPlayer(activeCommandUnit.getSimulationUnit().getPlayerIndex()).getColor();
				cursorModelInstance.setTeamColor(playerColorIndex);
				cursorModelInstance.rotate(RenderUnit.tempQuat.setFromAxis(RenderMathUtils.VEC3_UNIT_Z,
						viewer.simulation.getGameplayConstants().getBuildingAngle()));
				cursorModelInstance.setAnimationSpeed(0f);
				justLoaded = true;
				final CUnitType buildingUnitType = cursorBuildingUnitType;
				cursorModelPathing = buildingUnitType.getBuildingPathingPixelMap();

				if (cursorModelPathing != null) {
					cursorModelUnderneathPathingRedGreenPixmap = new Pixmap(
							cursorModelPathing.getWidth(), cursorModelPathing.getHeight(),
							Format.RGBA8888);
					cursorModelUnderneathPathingRedGreenPixmap.setBlending(Blending.None);
					cursorModelUnderneathPathingRedGreenPixmapTextureData = new PixmapTextureData(
							cursorModelUnderneathPathingRedGreenPixmap, Format.RGBA8888, false, false);
					cursorModelUnderneathPathingRedGreenPixmapTexture = new Texture(
							cursorModelUnderneathPathingRedGreenPixmapTextureData);
					final ViewerTextureRenderable greenPixmap = new ViewerTextureRenderable.GdxViewerTextureRenderable(
							cursorModelUnderneathPathingRedGreenPixmapTexture);
					cursorModelUnderneathPathingRedGreenSplatModel = new SplatModel(Gdx.gl30, greenPixmap,
							new ArrayList<>(), viewer.terrain.centerOffset, new ArrayList<>(), true, false, true);
					cursorModelUnderneathPathingRedGreenSplatModel.color[3] = 0.20f;
				}
			}
			viewer.getClickLocation(clickLocationTemp, baseMouseX, Gdx.graphics.getHeight() - baseMouseY);
			if (cursorModelPathing != null) {
				clickLocationTemp.x = (float) Math.floor(clickLocationTemp.x / 64f) * 64f;
				clickLocationTemp.y = (float) Math.floor(clickLocationTemp.y / 64f) * 64f;
				if (((cursorModelPathing.getWidth() / 2) % 2) == 1) {
					clickLocationTemp.x += 32f;
				}
				if (((cursorModelPathing.getHeight() / 2) % 2) == 1) {
					clickLocationTemp.y += 32f;
				}
				clickLocationTemp.z = viewer.terrain.getGroundHeight(clickLocationTemp.x, clickLocationTemp.y);

				final int cursorWidthCells = cursorModelPathing.getWidth();
				final int halfCursorWidthCells = cursorWidthCells / 2;
				final int cursorHeightCells = cursorModelPathing.getHeight();
				final int halfCursorHeightCells = cursorHeightCells / 2;
				final PathingGrid pathingGrid = viewer.simulation.getPathingGrid();
				boolean blockAll = false;
				final int cellX = pathingGrid.getCellX(clickLocationTemp.x);
				final int cellY = pathingGrid.getCellY(clickLocationTemp.y);
				if ((cellX < halfCursorWidthCells) || (cellX > (pathingGrid.getWidth() - halfCursorWidthCells))
						|| (cellY < halfCursorHeightCells)
						|| (cellY > (pathingGrid.getHeight() - halfCursorHeightCells))) {
					blockAll = true;
				}
				final boolean canBeBuiltOnThem = cursorBuildingUnitType.isCanBeBuiltOnThem();
				if (canBeBuiltOnThem) {
					viewer.simulation.getWorldCollision().enumBuildingsAtPoint(clickLocationTemp.x, clickLocationTemp.y,
							buildOnBuildingIntersector.reset(clickLocationTemp.x, clickLocationTemp.y));
					blockAll = (buildOnBuildingIntersector.getUnitToBuildOn() == null);
				}
				final float halfRenderWidth = cursorWidthCells * 16;
				final float halfRenderHeight = cursorHeightCells * 16;
				if (blockAll) {
					for (int i = 0; i < cursorModelUnderneathPathingRedGreenPixmap.getWidth(); i++) {
						for (int j = 0; j < cursorModelUnderneathPathingRedGreenPixmap.getHeight(); j++) {
							cursorModelUnderneathPathingRedGreenPixmap.drawPixel(i,
									cursorModelUnderneathPathingRedGreenPixmap.getHeight() - 1 - j,
									Color.rgba8888(1, 0, 0, 1.0f));
						}
					}
				}
				else if (!canBeBuiltOnThem) {
					for (int i = 0; i < cursorModelUnderneathPathingRedGreenPixmap.getWidth(); i++) {
						for (int j = 0; j < cursorModelUnderneathPathingRedGreenPixmap.getHeight(); j++) {
							boolean blocked = false;
							final short pathing = pathingGrid.getPathing(
									(clickLocationTemp.x + (i * 32)) - halfRenderWidth,
									(clickLocationTemp.y + (j * 32)) - halfRenderHeight);
							for (final CBuildingPathingType preventedType : cursorBuildingUnitType
									.getPreventedPathingTypes()) {
								if (PathingFlags.isPathingFlag(pathing, preventedType)) {
									blocked = true;
								}
							}
							for (final CBuildingPathingType requiredType : cursorBuildingUnitType
									.getRequiredPathingTypes()) {
								if (!PathingFlags.isPathingFlag(pathing, requiredType)) {
									blocked = true;
								}
							}
							final int color = blocked ? Color.rgba8888(1, 0, 0, 1.0f) : Color.rgba8888(0, 1, 0, 1.0f);
							cursorModelUnderneathPathingRedGreenPixmap.drawPixel(i,
									cursorModelUnderneathPathingRedGreenPixmap.getHeight() - 1 - j, color);
						}
					}
				}
				else {
					for (int i = 0; i < cursorModelUnderneathPathingRedGreenPixmap.getWidth(); i++) {
						for (int j = 0; j < cursorModelUnderneathPathingRedGreenPixmap.getHeight(); j++) {
							cursorModelUnderneathPathingRedGreenPixmap.drawPixel(i,
									cursorModelUnderneathPathingRedGreenPixmap.getHeight() - 1 - j,
									Color.rgba8888(0, 1, 0, 1.0f));
						}
					}
				}
				cursorModelUnderneathPathingRedGreenPixmapTexture
						.load(cursorModelUnderneathPathingRedGreenPixmapTextureData);

				if (justLoaded) {
					viewer.terrain.addSplatBatchModel(BUILDING_PATHING_PREVIEW_KEY,
							cursorModelUnderneathPathingRedGreenSplatModel);
					placementCursor = cursorModelUnderneathPathingRedGreenSplatModel.add(
							clickLocationTemp.x - halfRenderWidth, clickLocationTemp.y - halfRenderHeight,
							clickLocationTemp.x + halfRenderWidth, clickLocationTemp.y + halfRenderHeight, 10,
							viewer.terrain.centerOffset);
				}
				placementCursor.setLocation(clickLocationTemp.x, clickLocationTemp.y,
						viewer.terrain.centerOffset);
			}
			cursorModelInstance.setLocation(clickLocationTemp);
			SequenceUtils.randomSequence(cursorModelInstance, PrimaryTag.STAND);
			cursorFrame.setVisible(false);
			if (justLoaded) {
				cursorModelInstance.setScene(viewer.worldScene);
			}
		}

		private void handlePlacementCursor(final CAbility ability, final float radius) {
			final War3MapViewer viewer = war3MapViewer;
			viewer.getClickLocation(clickLocationTemp, baseMouseX, Gdx.graphics.getHeight() - baseMouseY);
			if (placementCursor == null) {
				placementCursor = viewer.terrain.addUberSplat(
						rootFrame.getSkinField("PlacementCursor"), clickLocationTemp.x,
						clickLocationTemp.y, 10, radius, true, true, true);
			}
			placementCursor.setLocation(clickLocationTemp.x, clickLocationTemp.y,
					viewer.terrain.centerOffset);
			cursorFrame.setVisible(false);
		}
	}

	private final class RallyPositioningVisitor implements AbilityTargetVisitor<Void> {
		private MdxComplexInstance rallyPointInstance;

		public RallyPositioningVisitor reset(final MdxComplexInstance rallyPointInstance) {
			this.rallyPointInstance = rallyPointInstance;
			return this;
		}

		@Override
		public Void accept(final AbilityPointTarget target) {
			rallyPointInstance.setParent(null);
			final float rallyPointX = target.getX();
			final float rallyPointY = target.getY();
			rallyPointInstance.setLocation(rallyPointX, rallyPointY,
					war3MapViewer.terrain.getGroundHeight(rallyPointX, rallyPointY));
			return null;
		}

		@Override
		public Void accept(final CUnit target) {
			final RenderUnit renderUnit = war3MapViewer.getRenderPeer(target);
			final MdxModel model = (MdxModel) renderUnit.instance.model;
			int index = -1;
			for (int i = 0; i < model.attachments.size(); i++) {
				final Attachment attachment = model.attachments.get(i);
				if (attachment.getName().toLowerCase(Locale.US).startsWith("sprite rally")) {
					index = i;
					break;
				}
			}
			if (index == -1) {
				for (int i = 0; i < model.attachments.size(); i++) {
					final Attachment attachment = model.attachments.get(i);
					if (attachment.getName().toLowerCase(Locale.US).startsWith("sprite")) {
						index = i;
						break;
					}
				}
			}
			if (index == -1) {
				for (int i = 0; i < model.attachments.size(); i++) {
					final Attachment attachment = model.attachments.get(i);
					if (attachment.getName().toLowerCase(Locale.US).startsWith("overhead ref")) {
						index = i;
					}
				}
			}
			if (index != -1) {
				final MdxNode attachment = renderUnit.instance.getAttachment(index);
				rallyPointInstance.setParent(attachment);
				rallyPointInstance.setLocation(0, 0, 0);
			}
			else {
				rallyPointInstance.setParent(null);
				final float rallyPointX = target.getX();
				final float rallyPointY = target.getY();
				rallyPointInstance.setLocation(rallyPointX, rallyPointY,
						war3MapViewer.terrain.getGroundHeight(rallyPointX, rallyPointY));
			}
			return null;
		}

		@Override
		public Void accept(final CDestructable target) {
			rallyPointInstance.setParent(null);
			final float rallyPointX = target.getX();
			final float rallyPointY = target.getY();
			rallyPointInstance.setLocation(rallyPointX, rallyPointY,
					war3MapViewer.terrain.getGroundHeight(rallyPointX, rallyPointY) + 192);
			return null;
		}

		@Override
		public Void accept(final CItem target) {
			rallyPointInstance.setParent(null);
			final float rallyPointX = target.getX();
			final float rallyPointY = target.getY();
			rallyPointInstance.setLocation(rallyPointX, rallyPointY,
					war3MapViewer.terrain.getGroundHeight(rallyPointX, rallyPointY));
			return null;
		}
	}

	private final class ActiveCommandUnitTargetFilter implements CWidgetFilterFunction {
		@Override
		public boolean call(final CWidget unit) {
			final BooleanAbilityTargetCheckReceiver<CWidget> targetReceiver = BooleanAbilityTargetCheckReceiver
					.getInstance();
			activeCommand.checkCanTarget(war3MapViewer.simulation,
					activeCommandUnit.getSimulationUnit(), activeCommandOrderId, unit,
					targetReceiver);
			return targetReceiver.isTargetable();
		}
	}

	private static final class Portrait {
		private MdxComplexInstance modelInstance;
		private final PortraitCameraManager portraitCameraManager;
		private final Scene portraitScene;
		private final EnumSet<AnimationTokens.SecondaryTag> recycleSet = EnumSet
				.noneOf(AnimationTokens.SecondaryTag.class);
		private RenderUnit unit;

		private Portrait(final War3MapViewer war3MapViewer, final Scene portraitScene) {
			this.portraitScene = portraitScene;
			portraitCameraManager = new PortraitCameraManager();
			portraitCameraManager.setupCamera(this.portraitScene);
			this.portraitScene.camera.viewport(new Rectangle(100, 0, 6400, 48));
		}

		public void update() {
			portraitCameraManager.updateCamera();
			if ((modelInstance != null)
					&& (modelInstance.sequenceEnded || (modelInstance.sequence == -1))) {
				recycleSet.clear();
				recycleSet.addAll(unit.getSecondaryAnimationTags());
				SequenceUtils.randomSequence(modelInstance, PrimaryTag.PORTRAIT, recycleSet, true);
			}
		}

		public void talk() {
			// TODO we somehow called talk from null by clicking a unit right at the same
			// time it died, so I do a null check here until I study that case further.
			if (modelInstance != null) {
				recycleSet.clear();
				recycleSet.addAll(unit.getSecondaryAnimationTags());
				recycleSet.add(SecondaryTag.TALK);
				SequenceUtils.randomSequence(modelInstance, PrimaryTag.PORTRAIT, recycleSet, true);
			}
		}

		public void setSelectedUnit(final RenderUnit unit) {
			if (!Objects.equals(this.unit, unit)) {
				this.unit = unit;
				if (unit == null) {
					if (modelInstance != null) {
						portraitScene.removeInstance(modelInstance);
					}
					modelInstance = null;
					portraitCameraManager.setModelInstance(null, null);
				}
				else {
					final MdxModel portraitModel = unit.portraitModel;
					if (portraitModel != null) {
						if (modelInstance != null) {
							portraitScene.removeInstance(modelInstance);
						}
						modelInstance = (MdxComplexInstance) portraitModel.addInstance();
						portraitCameraManager.setModelInstance(modelInstance, portraitModel);
						modelInstance.setBlendTime(portraitModel.blendTime);
						modelInstance.setSequenceLoopMode(SequenceLoopMode.NEVER_LOOP);
						modelInstance.setScene(portraitScene);
						modelInstance.setVertexColor(unit.instance.vertexColor);
						modelInstance.setTeamColor(unit.playerIndex);
					}
				}
			}
		}
	}

	public void setDraggingItem(final CItem itemInSlot) {
		draggingItem = itemInSlot;
		if (itemInSlot != null) {
			final String iconPath = war3MapViewer.getAbilityDataUI().getItemUI(itemInSlot.getTypeId())
					.getItemIconPathForDragging();
			cursorFrame.setReplaceableId(21, war3MapViewer.blp(iconPath));

			int index = 0;
			final CAbilityInventory inventory = selectedUnit.getSimulationUnit().getInventoryData();
			for (int i = 0; i < INVENTORY_HEIGHT; i++) {
				for (int j = 0; j < INVENTORY_WIDTH; j++) {
					final CommandCardIcon inventoryIcon = inventoryIcons[i][j];
					final CItem item = inventory.getItemInSlot(index);
					if (item == null) {
						if (index < inventory.getItemCapacity()) {
							inventoryIcon.setCommandButtonData(null, 0, 0, index + 1, true, false, false, null, null,
									'\0', 0, 0, 0, 0, false, 0, 0, -1);
						}
					}
					index++;
				}
			}
		}
		else {
			if (selectedUnit != null) {
				final CAbilityInventory inventory = selectedUnit.getSimulationUnit().getInventoryData();
				if (inventory != null) {
					int index = 0;
					for (int i = 0; i < INVENTORY_HEIGHT; i++) {
						for (int j = 0; j < INVENTORY_WIDTH; j++) {
							final CommandCardIcon inventoryIcon = inventoryIcons[i][j];
							final CItem item = inventory.getItemInSlot(index);
							if (item == null) {
								if (index < inventory.getItemCapacity()) {
									inventoryIcon.clear();
								}
							}
							index++;
						}
					}
				}
			}

		}
	}

	public void selectUnit(RenderUnit unit) {
		subMenuOrderIdStack.clear();
		if ((unit != null) && unit.getSimulationUnit().isDead()) {
			unit = null;
		}
		if (selectedUnit != null) {
			selectedUnit.getSimulationUnit().removeStateListener(this);
		}
		portrait.setSelectedUnit(unit);
		selectedUnit = unit;
		setDraggingItem(null);
		if (unit == null) {
			clearCommandCard();
			rootFrame.setText(simpleNameValue, "");
			rootFrame.setText(unitLifeText, "");
			rootFrame.setText(unitManaText, "");
			rootFrame.setText(simpleClassValue, "");
			rootFrame.setText(simpleBuildingActionLabel, "");
			attack1Icon.setVisible(false);
			attack2Icon.setVisible(false);
			rootFrame.setText(attack1InfoPanelIconLevel, "");
			rootFrame.setText(attack2InfoPanelIconLevel, "");
			rootFrame.setText(simpleBuildingBuildingActionLabel, "");
			rootFrame.setText(simpleBuildingNameValue, "");
			armorIcon.setVisible(false);
			rootFrame.setText(armorInfoPanelIconLevel, "");
			simpleBuildTimeIndicator.setVisible(false);
			simpleHeroLevelBar.setVisible(false);
			simpleBuildingBuildTimeIndicator.setVisible(false);
			simpleInfoPanelBuildingDetail.setVisible(false);
			simpleInfoPanelUnitDetail.setVisible(false);
			for (final QueueIcon queueIconFrame : queueIconFrames) {
				queueIconFrame.setVisible(false);
			}
			selectWorkerInsideFrame.setVisible(false);
			heroInfoPanel.setVisible(false);
			rallyPointInstance.hide();
			rallyPointInstance.detach();
			inventoryCover.setVisible(true);
			inventoryBarFrame.setVisible(false);
			for (final MultiSelectionIcon iconFrame : selectedUnitFrames) {
				iconFrame.setVisible(false);
			}
			for (final UIFrame frame : selectedUnitHighlightBackdrop) {
				frame.setVisible(false);
			}
			repositionWaypointFlags(null);
		}
		else {
			unit.getSimulationUnit().addStateListener(this);
			reloadSelectedUnitUI(unit);
		}
	}

	@Override
	public void rallyPointChanged() {
		if (selectedUnit != null) {
			final CUnit simulationUnit = selectedUnit.getSimulationUnit();
			repositionRallyPoint(simulationUnit);
		}
	}

	private void repositionRallyPoint(final CUnit simulationUnit) {
		final AbilityTarget rallyPoint = simulationUnit.getRallyPoint();
		if (rallyPoint != null) {
			rallyPointInstance
					.setTeamColor(war3MapViewer.simulation.getPlayer(simulationUnit.getPlayerIndex()).getColor());
			rallyPointInstance.show();
			rallyPointInstance.detach();
			rallyPoint.visit(rallyPositioningVisitor.reset(rallyPointInstance));
			rallyPointInstance.setScene(war3MapViewer.worldScene);
		}
		else {
			rallyPointInstance.hide();
			rallyPointInstance.detach();
		}
	}

	@Override
	public void waypointsChanged() {
		if (selectedUnit != null) {
			final CUnit simulationUnit = selectedUnit.getSimulationUnit();
			repositionWaypointFlags(simulationUnit);
		}
		else {
			repositionWaypointFlags(null);
		}
	}

	private void repositionWaypointFlags(final CUnit simulationUnit) {
		final Iterator<COrder> iterator;
		int orderIndex = 0;
		if (simulationUnit != null) {
			final Queue<COrder> orderQueue = simulationUnit.getOrderQueue();
			iterator = orderQueue.iterator();
			final COrder order = simulationUnit.getCurrentOrder();
			if ((order != null) && order.isQueued()) {
				final MdxComplexInstance waypointModelInstance = getOrCreateWaypointIndicator(orderIndex);
				final AbilityTarget target = order.getTarget(war3MapViewer.simulation);
				if (target != null) {
					waypointModelInstance.show();
					waypointModelInstance.detach();
					target.visit(rallyPositioningVisitor.reset(waypointModelInstance));
					waypointModelInstance.setScene(war3MapViewer.worldScene);
				}
				else {
					waypointModelInstance.hide();
					waypointModelInstance.detach();
				}
				orderIndex++;
			}
		}
		else {
			iterator = Collections.emptyIterator();
		}
		for (; (orderIndex < waypointModelInstances.size()) || (iterator.hasNext()); orderIndex++) {
			final MdxComplexInstance waypointModelInstance = getOrCreateWaypointIndicator(orderIndex);
			if (iterator.hasNext()) {
				final COrder order = iterator.next();
				final AbilityTarget target = order.getTarget(war3MapViewer.simulation);
				if (target != null) {
					waypointModelInstance.show();
					waypointModelInstance.detach();
					target.visit(rallyPositioningVisitor.reset(waypointModelInstance));
					waypointModelInstance.setScene(war3MapViewer.worldScene);
				}
				else {
					waypointModelInstance.hide();
					waypointModelInstance.detach();
				}
			}
			else {
				waypointModelInstance.hide();
				waypointModelInstance.detach();
			}
		}
	}

	private MdxComplexInstance getOrCreateWaypointIndicator(final int index) {
		while (index >= waypointModelInstances.size()) {
			final MdxComplexInstance waypointModelInstance = (MdxComplexInstance) waypointModel.addInstance();
			waypointModelInstance.rotate(RenderUnit.tempQuat.setFromAxis(RenderMathUtils.VEC3_UNIT_Z,
					war3MapViewer.simulation.getGameplayConstants().getBuildingAngle()));
			waypointModelInstance.setSequenceLoopMode(SequenceLoopMode.ALWAYS_LOOP);
			SequenceUtils.randomStandSequence(waypointModelInstance);
			waypointModelInstance.hide();
			waypointModelInstances.add(waypointModelInstance);
		}
		return waypointModelInstances.get(index);
	}

	private void reloadSelectedUnitUI(final RenderUnit unit) {
		if (unit == null) {
			return;
		}
		final CUnit simulationUnit = unit.getSimulationUnit();
		final float lifeRatioRemaining = simulationUnit.getLife() / simulationUnit.getMaxLife();
		rootFrame.setText(unitLifeText, FastNumberFormat.formatWholeNumber(simulationUnit.getLife()) + " / "
				+ FastNumberFormat.formatWholeNumber(simulationUnit.getMaxLife()));
		unitLifeText.setColor(new Color(Math.min(1.0f, 2.0f - (lifeRatioRemaining * 2)),
				Math.min(1.0f, lifeRatioRemaining * 2), 0, 1.0f));
		final int maximumMana = simulationUnit.getMaximumMana();
		if (maximumMana > 0) {
			rootFrame.setText(unitManaText,
					FastNumberFormat.formatWholeNumber(simulationUnit.getMana()) + " / " + maximumMana);
		}
		else {
			rootFrame.setText(unitManaText, "");
		}
		final boolean multiSelect = selectedUnits.size() > 1;
		repositionRallyPoint(simulationUnit);
		repositionWaypointFlags(simulationUnit);
		if (!multiSelect) {
			for (int i = 0; i < selectedUnitFrames.length; i++) {
				selectedUnitFrames[i].setVisible(false);
				selectedUnitHighlightBackdrop[i].setVisible(false);
			}
		}
		if ((simulationUnit.getBuildQueue()[0] != null)
				&& (simulationUnit.getPlayerIndex() == war3MapViewer.getLocalPlayerIndex())) {
			for (int i = 0; i < queueIconFrames.length; i++) {
				final QueueItemType queueItemType = simulationUnit.getBuildQueueTypes()[i];
				if (queueItemType == null) {
					queueIconFrames[i].setVisible(false);
				}
				else {
					queueIconFrames[i].setVisible(true);
					switch (queueItemType) {
					case RESEARCH:
						final IconUI upgradeUI = war3MapViewer.getAbilityDataUI()
								.getUpgradeUI(simulationUnit.getBuildQueue()[i], 0);
						queueIconFrames[i].setTexture(upgradeUI.getIcon());
						queueIconFrames[i].setToolTip(upgradeUI.getToolTip());
						queueIconFrames[i].setUberTip(upgradeUI.getUberTip());
						break;
					case HERO_REVIVE: {
						final War3ID handleIdEncoded = simulationUnit.getBuildQueue()[i];
						final CUnit hero = war3MapViewer.simulation.getUnit(handleIdEncoded.getValue());
						final UnitIconUI unitUI = war3MapViewer.getAbilityDataUI().getUnitUI(hero.getTypeId());
						queueIconFrames[i].setTexture(unitUI.getIcon());
						queueIconFrames[i]
								.setToolTip(unitUI.getReviveTip() + " - " + hero.getHeroData().getProperName());
						queueIconFrames[i].setUberTip(unitUI.getUberTip());
						break;
					}
					case UNIT:
					default: {
						final IconUI unitUI = war3MapViewer.getAbilityDataUI()
								.getUnitUI(simulationUnit.getBuildQueue()[i]);
						queueIconFrames[i].setTexture(unitUI.getIcon());
						queueIconFrames[i].setToolTip(unitUI.getToolTip());
						queueIconFrames[i].setUberTip(unitUI.getUberTip());
						break;
					}
					}
				}
			}
			simpleInfoPanelBuildingDetail.setVisible(!multiSelect);
			simpleInfoPanelUnitDetail.setVisible(false);
			rootFrame.setText(simpleBuildingNameValue, simulationUnit.getUnitType().getName());
			rootFrame.setText(simpleBuildingDescriptionValue, "");

			simpleBuildingBuildTimeIndicator.setVisible(true);
			simpleBuildTimeIndicator.setVisible(false);
			simpleHeroLevelBar.setVisible(false);
			if (simulationUnit.getBuildQueueTypes()[0] == QueueItemType.UNIT) {
				rootFrame.setText(simpleBuildingBuildingActionLabel,
						rootFrame.getTemplates().getDecoratedString("TRAINING"));
			}
			else if (simulationUnit.getBuildQueueTypes()[0] == QueueItemType.HERO_REVIVE) {
				rootFrame.setText(simpleBuildingBuildingActionLabel,
						rootFrame.getTemplates().getDecoratedString("REVIVING"));
			}
			else {
				rootFrame.setText(simpleBuildingBuildingActionLabel,
						rootFrame.getTemplates().getDecoratedString("RESEARCHING"));
			}
			attack1Icon.setVisible(false);
			attack2Icon.setVisible(false);
			armorIcon.setVisible(false);
			heroInfoPanel.setVisible(false);
			selectWorkerInsideFrame.setVisible(false);
		}
		else if (multiSelect) {
			for (QueueIcon queueIconFrame : queueIconFrames) {
				queueIconFrame.setVisible(false);
			}
			for (int i = 0; i < selectedUnitFrames.length; i++) {
				final boolean useIcon = i < selectedUnits.size();
				selectedUnitFrames[i].setVisible(useIcon);
				final boolean focused = useIcon && selectedUnits.get(i).groupsWith(selectedUnit);
				selectedUnitHighlightBackdrop[i].setVisible(focused);
				if (useIcon) {
					final CUnit multiSelectedUnit = selectedUnits.get(i).getSimulationUnit();
					final CUnitType unitType = multiSelectedUnit.getUnitType();
					final IconUI unitUI = war3MapViewer.getAbilityDataUI().getUnitUI(unitType.getTypeId());
					selectedUnitFrames[i].setTexture(unitUI.getIcon());
					selectedUnitFrames[i].setToolTip(unitUI.getToolTip());
					selectedUnitFrames[i].setUberTip(unitUI.getUberTip());
					selectedUnitFrames[i]
							.setLifeRatioRemaining(multiSelectedUnit.getLife() / multiSelectedUnit.getMaximumLife());
					final boolean useManaBar = multiSelectedUnit.getMaximumMana() > 0;
					selectedUnitFrames[i].setManaBarVisible(useManaBar);
					if (useManaBar) {
						selectedUnitFrames[i].setManaRatioRemaining(
								multiSelectedUnit.getMana() / multiSelectedUnit.getMaximumMana());
					}
					if (focused) {
						selectedUnitFrames[i].showFocused(rootFrame, uiViewport);
						if (useManaBar) {
							selectedUnitHighlightBackdrop[i].setHeight(frontQueueIconWidth * 1.75f);
						}
						else {
							selectedUnitHighlightBackdrop[i].setHeight(frontQueueIconWidth * 1.55f);
						}
						selectedUnitHighlightBackdrop[i].positionBounds(rootFrame, uiViewport);
					}
					else {
						selectedUnitFrames[i].showUnFocused(rootFrame, uiViewport);
					}
				}
			}
			simpleInfoPanelBuildingDetail.setVisible(false);
			simpleInfoPanelUnitDetail.setVisible(false);
			simpleBuildingBuildTimeIndicator.setVisible(false);
			simpleBuildTimeIndicator.setVisible(false);
			simpleHeroLevelBar.setVisible(false);
			attack1Icon.setVisible(false);
			attack2Icon.setVisible(false);
			armorIcon.setVisible(false);
			heroInfoPanel.setVisible(false);
			selectWorkerInsideFrame.setVisible(false);
		}
		else {
			final CAbilityCargoHold cargoData = simulationUnit.getCargoData();
			if (cargoData != null) {

			}
			else {
				for (final QueueIcon queueIconFrame : queueIconFrames) {
					queueIconFrame.setVisible(false);
				}
				simpleInfoPanelBuildingDetail.setVisible(false);
				simpleInfoPanelUnitDetail.setVisible(true);
				final String unitTypeName = simulationUnit.getUnitType().getName();

				final boolean anyAttacks = !simulationUnit.getAttacks().isEmpty();
				final boolean constructing = simulationUnit.isConstructingOrUpgrading();
				final UIFrame localArmorIcon = armorIcon;
				final TextureFrame localArmorIconBackdrop = armorIconBackdrop;
				final StringFrame localArmorInfoPanelIconValue = armorInfoPanelIconValue;
				if (anyAttacks && !constructing) {
					final CUnitAttack attackOne = simulationUnit.getAttacks().get(0);
					attack1Icon.setVisible(attackOne.isShowUI());
					attack1IconBackdrop.setTexture(damageBackdrops.getTexture(attackOne.getAttackType()));
					String attackOneDmgText = attackOne.getMinDamageDisplay() + " - " + attackOne.getMaxDamageDisplay();
					final int attackOneTemporaryDamageBonus = attackOne.getTotalTemporaryDamageBonus();
					if (attackOneTemporaryDamageBonus != 0) {
						attackOneDmgText += (attackOneTemporaryDamageBonus > 0 ? "|cFF00FF00 +" : "|cFFFF0000 ")
								+ attackOneTemporaryDamageBonus;
					}
					rootFrame.setText(attack1InfoPanelIconValue, attackOneDmgText);
					if (simulationUnit.getAttacks().size() > 1) {
						final CUnitAttack attackTwo = simulationUnit.getAttacks().get(1);
						attack2Icon.setVisible(attackTwo.isShowUI());
						attack2IconBackdrop.setTexture(damageBackdrops.getTexture(attackTwo.getAttackType()));
						String attackTwoDmgText = attackTwo.getMinDamage() + " - " + attackTwo.getMaxDamage();
						final int attackTwoTemporaryDamageBonus = attackTwo.getTotalTemporaryDamageBonus();
						if (attackTwoTemporaryDamageBonus != 0) {
							attackTwoDmgText += (attackTwoTemporaryDamageBonus > 0 ? "|cFF00FF00 +" : "|cFFFF0000 ")
									+ attackTwoTemporaryDamageBonus;
						}
						rootFrame.setText(attack2InfoPanelIconValue, attackTwoDmgText);
					}
					else {
						attack2Icon.setVisible(false);
					}

					smashArmorIconWrapper.addSetPoint(
							new SetPoint(FramePoint.TOPLEFT, simpleInfoPanelUnitDetail, FramePoint.TOPLEFT,
									GameUI.convertX(uiViewport, 0f), GameUI.convertY(uiViewport, -0.0705f)));
				}
				else {
					attack1Icon.setVisible(false);
					attack2Icon.setVisible(false);

					smashArmorIconWrapper.addSetPoint(
							new SetPoint(FramePoint.TOPLEFT, simpleInfoPanelUnitDetail, FramePoint.TOPLEFT,
									GameUI.convertX(uiViewport, 0f), GameUI.convertY(uiViewport, -0.040f)));
				}
				smashArmorIconWrapper.positionBounds(rootFrame, uiViewport);
				armorIcon.positionBounds(rootFrame, uiViewport);

				final CAbilityHero heroData = simulationUnit.getHeroData();
				final boolean hero = heroData != null;
				heroInfoPanel.setVisible(hero);
				if (hero) {
					final CPrimaryAttribute primaryAttribute = simulationUnit.getUnitType().getPrimaryAttribute();
					String iconKey;
					switch (primaryAttribute) {
					case AGILITY:
						iconKey = "InfoPanelIconHeroIconAGI";
						break;
					case INTELLIGENCE:
						iconKey = "InfoPanelIconHeroIconINT";
						break;
					default:
					case STRENGTH:
						iconKey = "InfoPanelIconHeroIconSTR";
						break;
					}
					primaryAttributeIcon.setTexture(iconKey, rootFrame);

					rootFrame.setText(strengthValue, heroData.getStrength().getDisplayText());
					rootFrame.setText(agilityValue, heroData.getAgility().getDisplayText());
					rootFrame.setText(intelligenceValue, heroData.getIntelligence().getDisplayText());
					final String infopanelLevelClass = rootFrame.getTemplates()
							.getDecoratedString("INFOPANEL_LEVEL_CLASS").replace("%u", "%d"); // :(
					final int heroLevel = heroData.getHeroLevel();
					simpleClassValue.setVisible(true);
					rootFrame.setText(simpleClassValue,
							String.format(infopanelLevelClass, heroLevel, unitTypeName));
					rootFrame.setText(simpleNameValue, heroData.getProperName());
					simpleHeroLevelBar.setVisible(true);
					final CGameplayConstants gameplayConstants = war3MapViewer.simulation.getGameplayConstants();
					simpleHeroLevelBar
							.setValue((heroData.getXp() - gameplayConstants.getNeedHeroXPSum(heroLevel - 1))
									/ (float) gameplayConstants.getNeedHeroXP(heroLevel));
				}
				else {
					simpleClassValue.setVisible(!simulationUnit.isBuilding());
					rootFrame.setText(simpleNameValue, unitTypeName);
					String classText = null;
					for (final CUnitClassification classification : simulationUnit.getClassifications()) {
						if (classification.getDisplayName() != null) {
							classText = classification.getDisplayName();
						}
					}
					if (classText != null) {
						rootFrame.setText(simpleClassValue, classText);
					}
					else {
						rootFrame.setText(simpleClassValue, "");
					}
					simpleHeroLevelBar.setVisible(false);
				}

				localArmorIcon.setVisible(!constructing);
				simpleBuildTimeIndicator.setVisible(constructing);
				simpleBuildingBuildTimeIndicator.setVisible(false);
				if (constructing) {
					War3ID constructingTypeId = simulationUnit.getTypeId();
					if (simulationUnit.isUpgrading()) {
						constructingTypeId = simulationUnit.getUpgradeIdType();
					}

					rootFrame.setText(simpleBuildingActionLabel,
							rootFrame.getTemplates().getDecoratedString("CONSTRUCTING"));
					queueIconFrames[0].setVisible(true);
					queueIconFrames[0]
							.setTexture(war3MapViewer.getAbilityDataUI().getUnitUI(constructingTypeId).getIcon());

					if (simulationUnit.getWorkerInside() != null) {
						selectWorkerInsideFrame.setVisible(true);
						selectWorkerInsideFrame.setTexture(war3MapViewer.getAbilityDataUI()
								.getUnitUI(simulationUnit.getWorkerInside().getTypeId()).getIcon());
					}
					else {
						selectWorkerInsideFrame.setVisible(false);
					}
				}
				else {
					rootFrame.setText(simpleBuildingActionLabel, "");
					selectWorkerInsideFrame.setVisible(false);
				}
				final Texture defenseTexture = defenseBackdrops
						.getTexture(simulationUnit.getUnitType().getDefenseType());
				Objects.requireNonNull(defenseTexture, () -> simulationUnit.getUnitType().getDefenseType() + " can't find texture!");
				localArmorIconBackdrop.setTexture(defenseTexture);

				String defenseDisplayString;
				if (simulationUnit.isInvulnerable()) {
					defenseDisplayString = rootFrame.getTemplates().getDecoratedString("INVULNERABLE");
				}
				else {
					defenseDisplayString = Integer.toString(simulationUnit.getCurrentDefenseDisplay());
					final float temporaryDefenseBonus = simulationUnit.getTotalTemporaryDefenseBonus();
					if (temporaryDefenseBonus != 0) {
						if (temporaryDefenseBonus > 0) {
							defenseDisplayString += "|cFF00FF00 +" + String.format("%.1f", temporaryDefenseBonus);
						}
						else {
							defenseDisplayString += "|cFFFF0000 " + String.format("%.1f", temporaryDefenseBonus);
						}
					}
				}
				rootFrame.setText(localArmorInfoPanelIconValue, defenseDisplayString);
			}
		}
		final CAbilityInventory inventory = simulationUnit.getInventoryData();
		inventoryCover.setVisible(inventory == null);
		if (inventory != null) {
			inventoryBarFrame.setVisible(true);
			int index = 0;
			for (int i = 0; i < INVENTORY_HEIGHT; i++) {
				for (int j = 0; j < INVENTORY_WIDTH; j++) {
					final CommandCardIcon inventoryIcon = inventoryIcons[i][j];
					final CItem item = inventory.getItemInSlot(index);
					if (item != null) {
						final ItemUI itemUI = war3MapViewer.getAbilityDataUI().getItemUI(item.getTypeId());
						final IconUI iconUI = itemUI.getIconUI();
						final CItemType itemType = item.getItemType();
						// TODO: below we set menu=false, this is bad, item should be based on item abil
						final boolean activelyUsed = itemType.isActivelyUsed();
						final boolean pawnable = itemType.isPawnable();
						final String uberTip = iconUI.getUberTip();
						recycleStringBuilder.setLength(0);
						if (pawnable) {
							recycleStringBuilder
									.append(rootFrame.getTemplates().getDecoratedString("ITEM_PAWN_TOOLTIP"));
							recycleStringBuilder.append("|n");
						}
						if (activelyUsed) {
							recycleStringBuilder
									.append(rootFrame.getTemplates().getDecoratedString("ITEM_USE_TOOLTIP"));
							recycleStringBuilder.append("|n");
						}
						recycleStringBuilder.append(uberTip);
						inventoryIcon.setCommandButtonData(iconUI.getIcon(), 0,
								activelyUsed ? (OrderIds.itemuse00 + index) : 0, index + 1, activelyUsed, false, false,
								itemUI.getName(), recycleStringBuilder.toString(), '\0', itemType.getGoldCost(),
								itemType.getLumberCost(), 0, 0, false, 0, 0, -1);
					}
					else {
						if (index >= inventory.getItemCapacity()) {
							inventoryIcon.setCommandButtonData(consoleInventoryNoCapacityTexture, 0, 0, 0, false,
									false, false, null, null, '\0', 0, 0, 0, 0, false, 0, 0, -1);
						}
						else {
							if (draggingItem != null) {
								inventoryIcon.setCommandButtonData(null, 0, 0, index + 1, true, false, false, null,
										null, '\0', 0, 0, 0, 0, false, 0, 0, -1);
							}
							else {
								inventoryIcon.clear();
							}
						}
					}
					index++;
				}
			}
		}
		clearAndRepopulateCommandCard();
	}

	private void clearCommandCard() {
		for (int j = 0; j < COMMAND_CARD_HEIGHT; j++) {
			for (int i = 0; i < COMMAND_CARD_WIDTH; i++) {
				commandCard[j][i].clear();
			}
		}
	}

	@Override
	public void commandButton(final int buttonPositionX, final int buttonPositionY, final Texture icon,
							  final int abilityHandleId, final int orderId, final int autoCastOrderId, final boolean active,
							  final boolean autoCastActive, final boolean menuButton, final String tip, final String uberTip,
							  final char hotkey, final int goldCost, final int lumberCost, final int foodCost, final int manaCost,
							  final float cooldownRemaining, final float cooldownMax, final int numberOverlay) {
		int x = Math.max(0, Math.min(COMMAND_CARD_WIDTH - 1, buttonPositionX));
		int y = Math.max(0, Math.min(COMMAND_CARD_HEIGHT - 1, buttonPositionY));
		while ((x < COMMAND_CARD_WIDTH) && (y < COMMAND_CARD_HEIGHT) && commandCard[y][x].isVisible()) {
			x++;
			if (x >= COMMAND_CARD_WIDTH) {
				x = 0;
				y++;
			}
		}
		if ((x < COMMAND_CARD_WIDTH) && (y < COMMAND_CARD_HEIGHT)) {
			commandCard[y][x].setCommandButtonData(icon, abilityHandleId, orderId, autoCastOrderId, active,
					autoCastActive, menuButton, tip, uberTip, hotkey, goldCost, lumberCost, foodCost, manaCost,
					selectedUnit.getSimulationUnit().getMana() < manaCost, cooldownRemaining, cooldownMax,
					numberOverlay);
		}
	}

	public void resize(final Rectangle viewport) {
		cameraManager.resize(viewport);
		positionPortrait();
	}

	public void positionPortrait() {
		projectionTemp1.x = 422 * widthRatioCorrection;
		projectionTemp1.y = 57 * heightRatioCorrection;
		projectionTemp2.x = (422 + 167) * widthRatioCorrection;
		projectionTemp2.y = (57 + 170) * heightRatioCorrection;
		uiViewport.project(projectionTemp1);
		uiViewport.project(projectionTemp2);

		tempRect.x = projectionTemp1.x + uiViewport.getScreenX();
		tempRect.y = projectionTemp1.y + uiViewport.getScreenY();
		tempRect.width = projectionTemp2.x - projectionTemp1.x;
		tempRect.height = projectionTemp2.y - projectionTemp1.y;
		portrait.portraitScene.camera.viewport(tempRect);
	}

	private static final class InfoPanelIconBackdrops {
		private final Texture[] damageBackdropTextures;

		private InfoPanelIconBackdrops(final CodeKeyType[] attackTypes, final GameUI gameUI, final String prefix,
									   final String suffix) {
			damageBackdropTextures = new Texture[attackTypes.length];
			for (int index = 0; index < attackTypes.length; index++) {
				final CodeKeyType attackType = attackTypes[index];
				String skinLookupKey = "InfoPanelIcon" + prefix + attackType.getCodeKey() + suffix;
				final Texture suffixTexture = gameUI.loadTexture(gameUI.getSkinField(skinLookupKey));
				if (suffixTexture != null) {
					damageBackdropTextures[index] = suffixTexture;
				}
				else {
					skinLookupKey = "InfoPanelIcon" + prefix + attackType.getCodeKey();
					damageBackdropTextures[index] = gameUI.loadTexture(gameUI.getSkinField(skinLookupKey));
				}
			}
		}

		public Texture getTexture(final CodeKeyType attackType) {
			if (attackType != null) {
				final int ordinal = attackType.ordinal();
				if ((ordinal >= 0) && (ordinal < damageBackdropTextures.length)) {
					return damageBackdropTextures[ordinal];
				}
			}
			return damageBackdropTextures[0];
		}
	}

	@Override
	public void lifeChanged() {
		if (selectedUnit == null) {
			return;
		}
		if (selectedUnit.getSimulationUnit().isDead()) {
			final RenderUnit preferredSelectionReplacement = selectedUnit.getPreferredSelectionReplacement();
			final List<RenderWidget> newSelection = new ArrayList<>(selectedUnits);
			newSelection.remove(selectedUnit);
			if (preferredSelectionReplacement != null) {
				newSelection.add(preferredSelectionReplacement);
			}
			selectWidgets(newSelection);
			war3MapViewer.doSelectUnit(newSelection);
		}
		else {
			final float lifeRatioRemaining = selectedUnit.getSimulationUnit().getLife()
					/ selectedUnit.getSimulationUnit().getMaxLife();
			rootFrame.setText(unitLifeText,
					FastNumberFormat.formatWholeNumber(selectedUnit.getSimulationUnit().getLife()) + " / "
							+ FastNumberFormat.formatWholeNumber(selectedUnit.getSimulationUnit().getMaxLife()));
			unitLifeText.setColor(new Color(Math.min(1.0f, 2.0f - (lifeRatioRemaining * 2)),
					Math.min(1.0f, lifeRatioRemaining * 2), 0, 1.0f));
		}
	}

	@Override
	public void manaChanged() {
		rootFrame.setText(unitManaText,
				FastNumberFormat.formatWholeNumber(selectedUnit.getSimulationUnit().getMana()) + " / "
						+ FastNumberFormat.formatWholeNumber(selectedUnit.getSimulationUnit().getMaximumMana()));
	}

	@Override
	public void goldChanged() {
		rootFrame.setText(resourceBarGoldText, Integer.toString(localPlayer.getGold()));
	}

	@Override
	public void lumberChanged() {
		rootFrame.setText(resourceBarLumberText, Integer.toString(localPlayer.getLumber()));
	}

	@Override
	public void foodChanged() {
		final int foodCap = localPlayer.getFoodCap();
		if (foodCap == 0) {
			rootFrame.setText(resourceBarSupplyText, Integer.toString(localPlayer.getFoodUsed()));
			resourceBarSupplyText.setColor(Color.WHITE);
		}
		else {
			rootFrame.setText(resourceBarSupplyText, localPlayer.getFoodUsed() + "/" + foodCap);
			resourceBarSupplyText.setColor(localPlayer.getFoodUsed() > foodCap ? Color.RED : Color.WHITE);
		}
	}

	@Override
	public void upkeepChanged() {
		rootFrame.setText(resourceBarUpkeepText, "Upkeep NYI");
		resourceBarUpkeepText.setColor(Color.CYAN);
	}

	@Override
	public void heroDeath() {
		if (selectedUnit != null) {
			if (selectedUnit.getSimulationUnit().getUnitType().isRevivesHeroes()) {
				reloadSelectedUnitUI(selectedUnit);
			}
		}
	}

	@Override
	public void heroTokensChanged() {
		// TODO Auto-generated method stub
	}

	@Override
	public void ordersChanged() {
		reloadSelectedUnitUI(selectedUnit);
		if (mouseOverUIFrame instanceof ClickableActionFrame) {
			loadTooltip((ClickableActionFrame) mouseOverUIFrame);
		}
	}

	@Override
	public void heroStatsChanged() {
		reloadSelectedUnitUI(selectedUnit);
	}

	@Override
	public void inventoryChanged() {
		reloadSelectedUnitUI(selectedUnit);
	}

	@Override
	public void queueChanged() {
		reloadSelectedUnitUI(selectedUnit);
	}

	private void clearAndRepopulateCommandCard() {
		clearCommandCard();
		if (selectedUnit.getSimulationUnit().getPlayerIndex() == war3MapViewer.getLocalPlayerIndex()) {
			final AbilityDataUI abilityDataUI = war3MapViewer.getAbilityDataUI();
			final int menuOrderId = getSubMenuOrderId();
			if ((activeCommand != null) && (draggingItem == null)) {
				final IconUI cancelUI = abilityDataUI.getCancelUI();
				commandButton(cancelUI.getButtonPositionX(), cancelUI.getButtonPositionY(), cancelUI.getIcon(), 0,
						menuOrderId, 0, false, false, true, cancelUI.getToolTip(), cancelUI.getUberTip(),
						cancelUI.getHotkey(), 0, 0, 0, 0, 0, 0, -1);
			}
			else {
				if (menuOrderId != 0) {
					final int exitOrderId = subMenuOrderIdStack.size() > 1
							? subMenuOrderIdStack.get(subMenuOrderIdStack.size() - 2)
							: 0;
					final IconUI cancelUI = abilityDataUI.getCancelUI();
					commandButton(cancelUI.getButtonPositionX(), cancelUI.getButtonPositionY(), cancelUI.getIcon(), 0,
							exitOrderId, 0, false, false, true, cancelUI.getToolTip(), cancelUI.getUberTip(),
							cancelUI.getHotkey(), 0, 0, 0, 0, 0, 0, -1);
				}
				selectedUnit.populateCommandCard(war3MapViewer.simulation, rootFrame, this,
						abilityDataUI, menuOrderId, selectedUnits.size() > 1);
			}
		}
	}

	private int getSubMenuOrderId() {
		return subMenuOrderIdStack.isEmpty() ? 0
				: subMenuOrderIdStack.get(subMenuOrderIdStack.size() - 1);
	}

	public RenderUnit getSelectedUnit() {
		return selectedUnit;
	}

	public boolean keyDown(final int keycode) {
		if (WarsmashConstants.ENABLE_DEBUG) {
			if (keycode == Input.Keys.Z) {
				War3MapViewer.DEBUG_DEPTH++;
			}
			if (keycode == Input.Keys.X) {
				War3MapViewer.DEBUG_DEPTH = 0;
			}
			if (keycode == Input.Keys.W) {
				Terrain.WIREFRAME_TERRAIN = !Terrain.WIREFRAME_TERRAIN;
			}
		}
		if (keycode == Input.Keys.TAB) {
			if (selectedUnits.size() > 1) {
				advanceSelectedSubGroup();
				war3MapViewer.getUiSounds().getSound("SubGroupSelectionChange").play(uiScene.audioContext, 0,
						0, 0);
			}
		}
		final String keyString = Input.Keys.toString(keycode);
		final char c = keyString.length() == 1 ? keyString.charAt(0) : ' ';
		for (int j = 0; j < COMMAND_CARD_HEIGHT; j++) {
			for (int i = 0; i < COMMAND_CARD_WIDTH; i++) {
				boolean match = false;
				switch (WarsmashConstants.INPUT_HOTKEY_MODE){
					case 0:
						if (commandCard[j][i].checkHotkey(c, keycode)) {
							match = true;
						}
						break;
					case 1:
						if(keycode == commandCardGridHotkeys[j][i]) {
							match = true;
						}
						break;
					default:
						throw new IllegalStateException("Unexpected value: " + WarsmashConstants.INPUT_HOTKEY_MODE);
				}
				if(match){
					commandCard[j][i].onClick(Input.Buttons.LEFT);
					war3MapViewer.getUiSounds().getSound("InterfaceClick").play(uiScene.audioContext, 0, 0,0);
				}
			}
		}
		return cameraManager.keyDown(keycode);
	}

	public boolean keyUp(final int keycode) {
		return cameraManager.keyUp(keycode);
	}

	public void scrolled(final int amount) {
		cameraManager.scrolled(amount);
	}

	public boolean touchDown(final int screenX, final int screenY, final float worldScreenY, final int button) {
		allowDrag = false;
		if (button == Input.Buttons.FORWARD) {
			if (selectedUnits.size() > 1) {
				advanceSelectedSubGroup();
				war3MapViewer.getUiSounds().getSound("SubGroupSelectionChange").play(uiScene.audioContext, 0,
						0, 0);
			}
			return false;
		}
		if (button == Input.Buttons.BACK) {
			if (selectedUnits.size() > 1) {
				advanceSelectedSubGroupReverse();
				war3MapViewer.getUiSounds().getSound("SubGroupSelectionChange").play(uiScene.audioContext, 0,
						0, 0);
			}
			return false;
		}
		screenCoordsVector.set(screenX, screenY);
		uiViewport.unproject(screenCoordsVector);
		if (meleeUIMinimap.containsMouse(screenCoordsVector.x, screenCoordsVector.y)) {
			final Vector2 worldPoint = meleeUIMinimap.getWorldPointFromScreen(screenCoordsVector.x,
					screenCoordsVector.y);
			cameraManager.target.x = worldPoint.x;
			cameraManager.target.y = worldPoint.y;
			return true;
		}
		final UIFrame clickedUIFrame = rootFrame.touchDown(screenCoordsVector.x, screenCoordsVector.y, button);
		if (clickedUIFrame == null) {
			// try to interact with world
			if (activeCommand != null) {
				if (button == Input.Buttons.RIGHT) {
					activeCommandUnit = null;
					activeCommand = null;
					activeCommandOrderId = -1;
					if (draggingItem != null) {
						setDraggingItem(null);
					}
					clearAndRepopulateCommandCard();
				}
				else {
					final boolean shiftDown = isShiftDown();
					final RenderWidget rayPickUnit = war3MapViewer.rayPickUnit(screenX, worldScreenY,
							activeCommandUnitTargetFilter);
					if (rayPickUnit != null) {
						useActiveCommandOnUnit(shiftDown, rayPickUnit);
					}
					else {
						war3MapViewer.getClickLocation(clickLocationTemp, screenX, (int) worldScreenY);
						clickLocationTemp2.set(clickLocationTemp.x, clickLocationTemp.y);

						if (draggingItem != null) {
							war3MapViewer.showConfirmation(clickLocationTemp, 0, 1, 0);

							unitOrderListener.issueDropItemAtPointOrder(
									activeCommandUnit.getSimulationUnit().getHandleId(),
									activeCommand.getHandleId(), activeCommandOrderId,
									draggingItem.getHandleId(), clickLocationTemp2.x, clickLocationTemp2.y,
									shiftDown);
							if (getSelectedUnit().soundset.yes
									.playUnitResponse(war3MapViewer.worldScene.audioContext, getSelectedUnit())) {
								portraitTalk();
							}
							activeCommandUnit = null;
							activeCommand = null;
							activeCommandOrderId = -1;
							setDraggingItem(null);
							clearAndRepopulateCommandCard();
						}
						else {
							activeCommand.checkCanTarget(war3MapViewer.simulation,
									activeCommandUnit.getSimulationUnit(), activeCommandOrderId,
									clickLocationTemp2, PointAbilityTargetCheckReceiver.INSTANCE);
							final Vector2 target = PointAbilityTargetCheckReceiver.INSTANCE.getTarget();
							if (target != null) {
								if ((activeCommand instanceof CAbilityAttack)
										&& (activeCommandOrderId == OrderIds.attack)) {
									war3MapViewer.showConfirmation(clickLocationTemp, 1, 0, 0);
								}
								else {
									war3MapViewer.showConfirmation(clickLocationTemp, 0, 1, 0);
								}
								unitOrderListener.issuePointOrder(
										activeCommandUnit.getSimulationUnit().getHandleId(),
										activeCommand.getHandleId(), activeCommandOrderId,
										clickLocationTemp2.x, clickLocationTemp2.y, shiftDown);
								if (selectedUnits.size() > 1) {
									for (final RenderUnit otherSelectedUnit : selectedUnits) {
										if (!Objects.equals(otherSelectedUnit, activeCommandUnit)) {
											CAbility abilityToUse = null;
											AbilityPointTarget targetToUse = null;
											for (final CAbility ability : otherSelectedUnit.getSimulationUnit()
													.getAbilities()) {
												final PointAbilityTargetCheckReceiver receiver = PointAbilityTargetCheckReceiver.INSTANCE
														.reset();
												ability.checkCanTarget(war3MapViewer.simulation,
														otherSelectedUnit.getSimulationUnit(),
														activeCommandOrderId, clickLocationTemp2, receiver);
												if (receiver.getTarget() != null) {
													abilityToUse = ability;
													targetToUse = receiver.getTarget();
												}
											}
											if (abilityToUse != null) {
												unitOrderListener.issuePointOrder(
														otherSelectedUnit.getSimulationUnit().getHandleId(),
														abilityToUse.getHandleId(), activeCommandOrderId,
														targetToUse.getX(), targetToUse.getY(), shiftDown);
											}
										}
									}
								}
								if (getSelectedUnit().soundset.yes.playUnitResponse(
										war3MapViewer.worldScene.audioContext, getSelectedUnit())) {
									portraitTalk();
								}
								selectedSoundCount = 0;
								if (activeCommand instanceof AbstractCAbilityBuild) {
									war3MapViewer.getUiSounds().getSound("PlaceBuildingDefault")
											.play(uiScene.audioContext, 0, 0, 0);
								}
								else if (activeCommand instanceof CAbilityRally) {
									war3MapViewer.getUiSounds().getSound("RallyPointPlace")
											.play(uiScene.audioContext, 0, 0, 0);
								}
								if (!shiftDown) {
									subMenuOrderIdStack.clear();
									activeCommandUnit = null;
									activeCommand = null;
									activeCommandOrderId = -1;
									clearAndRepopulateCommandCard();
								}

							}
						}

					}
				}
			}
			else {
				if (button == Input.Buttons.RIGHT) {
					if ((getSelectedUnit() != null) && (getSelectedUnit().getSimulationUnit()
							.getPlayerIndex() == war3MapViewer.getLocalPlayerIndex())) {
						RenderWidget rayPickUnit = war3MapViewer.rayPickUnit(screenX, worldScreenY,
								anyClickableUnitFilter);
						if (rayPickUnit == null) {
							rayPickUnit = war3MapViewer.rayPickUnit(screenX, worldScreenY,
									anyTargetableUnitFilter);
						}
						if (rayPickUnit != null) {
							boolean ordered = false;
							boolean rallied = false;
							boolean attacked = false;
							for (final RenderUnit unit : selectedUnits) {
								CAbility abilityToUse = null;
								CWidget targetToUse = null;
								for (final CAbility ability : unit.getSimulationUnit().getAbilities()) {
									ability.checkCanTarget(war3MapViewer.simulation, unit.getSimulationUnit(),
											OrderIds.smart, rayPickUnit.getSimulationWidget(),
											CWidgetAbilityTargetCheckReceiver.INSTANCE);
									final CWidget targetWidget = CWidgetAbilityTargetCheckReceiver.INSTANCE.getTarget();
									if (targetWidget != null) {
										abilityToUse = ability;
										targetToUse = targetWidget;
									}
								}
								if (abilityToUse != null) {
									unitOrderListener.issueTargetOrder(unit.getSimulationUnit().getHandleId(),
											abilityToUse.getHandleId(), OrderIds.smart, targetToUse.getHandleId(),
											isShiftDown());
									rallied |= abilityToUse instanceof CAbilityRally;
									attacked |= abilityToUse instanceof CAbilityAttack;
									ordered = true;
								}
							}
							if (ordered) {
								final UnitSound yesSound = attacked ? getSelectedUnit().soundset.yesAttack
										: getSelectedUnit().soundset.yes;
								if (yesSound.playUnitResponse(war3MapViewer.worldScene.audioContext,
										getSelectedUnit())) {
									portraitTalk();
								}
								if (rallied) {
									war3MapViewer.getUiSounds().getSound("RallyPointPlace")
											.play(uiScene.audioContext, 0, 0, 0);
								}
								selectedSoundCount = 0;
							}
							else {
								rightClickMove(screenX, worldScreenY);
							}
						}
						else {
							rightClickMove(screenX, worldScreenY);
						}
					}
				}
				else {
					war3MapViewer.getClickLocation(lastMouseClickLocation, screenX, (int) worldScreenY);
					if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
						final short pathing = war3MapViewer.simulation.getPathingGrid()
								.getPathing(lastMouseClickLocation.x, lastMouseClickLocation.y);
						System.out.println(Integer.toBinaryString(pathing));
					}
					lastMouseDragStart.set(lastMouseClickLocation);
					allowDrag = true;
					draggingMouseButton = button;
				}
			}
		}
		else {
			if (clickedUIFrame instanceof ClickableFrame) {
				mouseDownUIFrame = (ClickableFrame) clickedUIFrame;
				mouseDownUIFrame.mouseDown(rootFrame, uiViewport);
			}
		}
		return false;
	}

	private void advanceSelectedSubGroup() {
		boolean foundSubSelection = false;
		for (final RenderUnit unit : selectedUnits) {
			if (foundSubSelection) {
				if (!unit.groupsWith(selectedUnit)) {
					selectUnit(unit);
					return;
				}
			}
			else if (Objects.equals(unit, selectedUnit)) {
				foundSubSelection = true;
			}
		}
		if (!selectedUnits.isEmpty()) {
			selectUnit(selectedUnits.get(0));
		}
	}

	private void advanceSelectedSubGroupReverse() {
		boolean foundSubSelection = false;
		for (int i = selectedUnits.size() - 1; i >= 0; i--) {
			final RenderUnit unit = selectedUnits.get(i);
			if (foundSubSelection) {
				if (!unit.groupsWith(selectedUnit)) {
					selectUnit(unit);
					return;
				}
			}
			else if (Objects.equals(unit, selectedUnit)) {
				foundSubSelection = true;
			}
		}
		if (!selectedUnits.isEmpty()) {
			selectUnit(selectedUnits.get(selectedUnits.size() - 1));
		}
	}

	private void useActiveCommandOnUnit(final boolean shiftDown, final RenderWidget rayPickUnit) {
		if (draggingItem != null) {
			unitOrderListener.issueDropItemAtTargetOrder(activeCommandUnit.getSimulationUnit().getHandleId(),
					activeCommand.getHandleId(), activeCommandOrderId, draggingItem.getHandleId(),
					rayPickUnit.getSimulationWidget().getHandleId(), shiftDown);
			setDraggingItem(null);
		}
		else {
			unitOrderListener.issueTargetOrder(activeCommandUnit.getSimulationUnit().getHandleId(),
					activeCommand.getHandleId(), activeCommandOrderId,
					rayPickUnit.getSimulationWidget().getHandleId(), shiftDown);
			if (selectedUnits.size() > 1) {
				for (final RenderUnit otherSelectedUnit : selectedUnits) {
					if (!Objects.equals(otherSelectedUnit, activeCommandUnit)) {
						CAbility abilityToUse = null;
						CWidget targetToUse = null;
						for (final CAbility ability : otherSelectedUnit.getSimulationUnit().getAbilities()) {
							final CWidgetAbilityTargetCheckReceiver receiver = CWidgetAbilityTargetCheckReceiver.INSTANCE
									.reset();
							ability.checkCanTarget(war3MapViewer.simulation, otherSelectedUnit.getSimulationUnit(),
									activeCommandOrderId, rayPickUnit.getSimulationWidget(), receiver);
							if (receiver.getTarget() != null) {
								abilityToUse = ability;
								targetToUse = receiver.getTarget();
							}
						}
						if (abilityToUse != null) {
							unitOrderListener.issueTargetOrder(otherSelectedUnit.getSimulationUnit().getHandleId(),
									abilityToUse.getHandleId(), activeCommandOrderId, targetToUse.getHandleId(),
									shiftDown);
						}
					}
				}
			}
		}
		final UnitSound yesSound = (activeCommand instanceof CAbilityAttack) ? getSelectedUnit().soundset.yesAttack
				: getSelectedUnit().soundset.yes;
		if (yesSound.playUnitResponse(war3MapViewer.worldScene.audioContext, getSelectedUnit())) {
			portraitTalk();
		}
		selectedSoundCount = 0;
		if (activeCommand instanceof CAbilityRally) {
			war3MapViewer.getUiSounds().getSound("RallyPointPlace").play(uiScene.audioContext, 0, 0, 0);
		}
		if (!shiftDown) {
			subMenuOrderIdStack.clear();
			activeCommandUnit = null;
			activeCommand = null;
			activeCommandOrderId = -1;
			clearAndRepopulateCommandCard();
		}
	}

	private void rightClickMove(final int screenX, final float worldScreenY) {
		war3MapViewer.getClickLocation(clickLocationTemp, screenX, (int) worldScreenY);
		war3MapViewer.showConfirmation(clickLocationTemp, 0, 1, 0);
		clickLocationTemp2.set(clickLocationTemp.x, clickLocationTemp.y);

		boolean ordered = false;
		boolean rallied = false;
		for (final RenderUnit unit : selectedUnits) {
			if (unit.getSimulationUnit().getPlayerIndex() == war3MapViewer.getLocalPlayerIndex()) {
				for (final CAbility ability : unit.getSimulationUnit().getAbilities()) {
					ability.checkCanUse(war3MapViewer.simulation, unit.getSimulationUnit(), OrderIds.smart,
							BooleanAbilityActivationReceiver.INSTANCE);
					if (BooleanAbilityActivationReceiver.INSTANCE.isOk()) {
						ability.checkCanTarget(war3MapViewer.simulation, unit.getSimulationUnit(), OrderIds.smart,
								clickLocationTemp2, PointAbilityTargetCheckReceiver.INSTANCE);
						final Vector2 target = PointAbilityTargetCheckReceiver.INSTANCE.getTarget();
						if (target != null) {
							unitOrderListener.issuePointOrder(unit.getSimulationUnit().getHandleId(),
									ability.getHandleId(), OrderIds.smart, clickLocationTemp2.x, clickLocationTemp2.y,
									isShiftDown());
							rallied |= ability instanceof CAbilityRally;
							ordered = true;
						}
					}
				}
			}

		}

		if (ordered) {
			if (getSelectedUnit().soundset.yes.playUnitResponse(war3MapViewer.worldScene.audioContext,
					getSelectedUnit())) {
				portraitTalk();
			}
			if (rallied) {
				war3MapViewer.getUiSounds().getSound("RallyPointPlace").play(uiScene.audioContext, 0, 0, 0);
			}
			selectedSoundCount = 0;
		}
	}

	private void selectWidgets(final List<RenderWidget> selectedUnits) {
		selectedUnits.sort((widget1, widget2) -> {
			final CUnitType unitType1 = ((RenderUnit) widget2).getSimulationUnit().getUnitType();
			final CUnitType unitType2 = ((RenderUnit) widget1).getSimulationUnit().getUnitType();
			final int prioSort = unitType1.getPriority() - unitType2.getPriority();
			if (prioSort == 0) {
				final int levelSort = unitType1.getLevel() - unitType2.getLevel();
				if (levelSort == 0) {
					return unitType1.getTypeId().getValue() - unitType2.getTypeId().getValue();
				}
				return levelSort;
			}
			return prioSort;
		});
		final List<RenderUnit> units = selectedUnits.stream()
				.filter(widget -> widget instanceof RenderUnit)
				.map(widget -> (RenderUnit) widget)
				.collect(Collectors.toList());
		selectUnits(units);
	}

	private void selectUnits(final List<RenderUnit> selectedUnits) {
		final List<RenderUnit> prevSelectedUnits = this.selectedUnits;
		this.selectedUnits = selectedUnits;
		if (!selectedUnits.isEmpty()) {
			final RenderUnit unit = selectedUnits.get(0);
			boolean selectionChanged = (!Objects.equals(unit, selectedUnit))
					|| (prevSelectedUnits.size() != selectedUnits.size());
			for (int i = 0; (i < prevSelectedUnits.size()) && (i < selectedUnits.size()); i++) {
				if (!Objects.equals(prevSelectedUnits.get(i), selectedUnits.get(i))) {
					selectionChanged = true;
					break;
				}
			}
			if (selectionChanged) {
				selectedSoundCount = 0;
			}
			boolean playedNewSound = false;
			if ((unit.getSimulationUnit().getPlayerIndex() == war3MapViewer.getLocalPlayerIndex())
					|| (unit.getSimulationUnit().getUnitType().getRace() == CUnitRace.CRITTERS)
					|| ((unit.getSimulationUnit().getUnitType().getRace() == CUnitRace.OTHER)
							&& (unit.getSimulationUnit().getPlayerIndex() == (WarsmashConstants.MAX_PLAYERS - 1)))) {
				if (unit.soundset != null) {
					UnitSound ackSoundToPlay = unit.soundset.what;
					int soundIndex;
					final int pissedSoundCount = unit.soundset.pissed.getSoundCount();
					if (unit.getSimulationUnit().isConstructing()) {
						ackSoundToPlay = war3MapViewer.getUiSounds()
								.getSound(rootFrame.getSkinField("ConstructingBuilding"));
						soundIndex = (int) (Math.random() * ackSoundToPlay.getSoundCount());
					}
					else {
						if ((selectedSoundCount >= 3) && (pissedSoundCount > 0)) {
							soundIndex = selectedSoundCount - 3;
							ackSoundToPlay = unit.soundset.pissed;
						}
						else {
							soundIndex = (int) (Math.random() * ackSoundToPlay.getSoundCount());
						}
					}
					if ((ackSoundToPlay != null) && ackSoundToPlay
							.playUnitResponse(war3MapViewer.worldScene.audioContext, unit, soundIndex)) {
						selectedSoundCount++;
						if ((selectedSoundCount - 3) >= pissedSoundCount) {
							selectedSoundCount = 0;
						}
						playedNewSound = true;
					}
				}
			}
			else {
				war3MapViewer.getUiSounds().getSound("InterfaceClick").play(uiScene.audioContext, 0, 0, 0);
			}
			if (selectionChanged) {
				for (final MultiSelectUnitStateListener listener : multiSelectUnitStateListeners) {
					listener.dispose();
				}
				multiSelectUnitStateListeners.clear();
				selectUnit(unit);
				if (selectedUnits.size() > 1) {
					int index = 0;
					for (final RenderUnit renderUnit : selectedUnits) {
						if (index >= 12) {
							break; // TODO handle >12 unit selections
						}
						final MultiSelectUnitStateListener multiSelectUnitStateListener = new MultiSelectUnitStateListener(
								renderUnit, index++);
						renderUnit.getSimulationUnit().addStateListener(multiSelectUnitStateListener);
						multiSelectUnitStateListeners.add(multiSelectUnitStateListener);
					}
				}
			}
			if (playedNewSound) {
				portraitTalk();
			}
		}
		else {
			selectUnit(null);
		}
	}

	public boolean touchUp(final int screenX, final int screenY, final float worldScreenY, final int button) {
		if (button == Input.Buttons.FORWARD) {
			return false;
		}
		if (button == Input.Buttons.BACK) {
			return false;
		}
		currentlyDraggingPointer = -1;
		screenCoordsVector.set(screenX, screenY);
		uiViewport.unproject(screenCoordsVector);
		final UIFrame clickedUIFrame = rootFrame.touchUp(screenCoordsVector.x, screenCoordsVector.y, button);
		if (mouseDownUIFrame != null) {
			if (Objects.equals(clickedUIFrame, mouseDownUIFrame)) {
				mouseDownUIFrame.onClick(button);
				String soundKey;
				if (mouseDownUIFrame instanceof ClickableActionFrame) {
					if (mouseDownUIFrame instanceof MultiSelectionIcon) {
						soundKey = "SubGroupSelectionChange";
					}
					else {
						soundKey = "InterfaceClick";
					}
				}
				else {
					soundKey = "MenuButtonClick";
				}
				war3MapViewer.getUiSounds().getSound(soundKey).play(uiScene.audioContext, 0, 0, 0);
			}
			mouseDownUIFrame.mouseUp(rootFrame, uiViewport);
		}
		else {
			if (!dragSelectPreviewUnits.isEmpty()) {
				if (allowDrag) {
					final List<RenderWidget> selectedWidgets = new ArrayList<>();
					boolean foundGoal = false;
					for (final RenderUnit unit : dragSelectPreviewUnits) {
						if ((unit.getSimulationUnit().getPlayerIndex() == war3MapViewer.getLocalPlayerIndex())
								&& !unit.getSimulationUnit().isBuilding()) {
							foundGoal = true;
							selectedWidgets.add(unit);
						}
					}
					if (!foundGoal) {
						selectedWidgets.addAll(dragSelectPreviewUnits);
					}
					final boolean shiftDown = isShiftDown();
					if (shiftDown) {
						for (final RenderUnit unit : selectedUnits) {
							if (!selectedWidgets.contains(unit)) {
								selectedWidgets.add(unit);
							}
						}
					}

					war3MapViewer.clearUnitMouseOverHighlight();

					war3MapViewer.doSelectUnit(selectedWidgets);
					selectWidgets(selectedWidgets);
				}
				dragSelectPreviewUnits.clear();
			}
			else {
				if (allowDrag) {
					if ((button == Input.Buttons.LEFT) && (mouseOverUnit != null)
							&& isUnitSelectable(mouseOverUnit)) {
						final long currentMillis = TimeUtils.millis();
						final List<RenderWidget> unitList = new ArrayList<>();
						final boolean shiftDown = isShiftDown();
						final boolean controlDown = isControlDown() || (((currentMillis - lastUnitClickTime) < 500)
								&& (Objects.equals(mouseOverUnit, lastClickUnit)));
						if (shiftDown) {
							unitList.addAll(selectedUnits);
						}
						if ((mouseOverUnit instanceof RenderUnit) && controlDown) {
							processSelectNearbyUnits(unitList, shiftDown, (RenderUnit) mouseOverUnit);
						}
						else {
							processClickSelect(unitList, shiftDown, mouseOverUnit);
						}
						war3MapViewer.doSelectUnit(unitList);
						selectWidgets(unitList);
						lastUnitClickTime = currentMillis;
						lastClickUnit = mouseOverUnit;
					}
				}
			}
		}
		mouseDownUIFrame = null;
		return false;
	}

	private void processSelectNearbyUnits(final List<RenderWidget> unitList, final boolean shiftDown,
			final RenderUnit mouseOverUnit) {
		war3MapViewer.simulation.getWorldCollision().enumUnitsInRect(
				new Rectangle(this.mouseOverUnit.getX() - 1024, this.mouseOverUnit.getY() - 1024, 2048, 2048),
				unit -> {
					if (Objects.equals(unit.getUnitType(), mouseOverUnit.getSimulationUnit().getUnitType())) {
						final RenderUnit renderPeer = war3MapViewer.getRenderPeer(unit);
						processClickSelect(unitList, shiftDown, renderPeer);
					}
					return false;
				});
	}

	private void processClickSelect(final List<RenderWidget> unitList, final boolean shiftDown,
			final RenderWidget mouseOverUnit) {
		if (shiftDown) {
			if (selectedUnits.contains(mouseOverUnit)) {
				unitList.remove(mouseOverUnit);
			}
			else {
				unitList.add(mouseOverUnit);
			}
		}
		else {
			unitList.add(mouseOverUnit);
		}
	}

	private static boolean isShiftDown() {
		return Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT);
	}

	private static boolean isControlDown() {
		return Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) || Gdx.input.isKeyPressed(Input.Keys.CONTROL_RIGHT);
	}

	public boolean touchDragged(final int screenX, final int screenY, final float worldScreenY, final int pointer) {
		screenCoordsVector.set(screenX, screenY);
		uiViewport.unproject(screenCoordsVector);

		if (meleeUIMinimap.containsMouse(screenCoordsVector.x, screenCoordsVector.y)) {
			final Vector2 worldPoint = meleeUIMinimap.getWorldPointFromScreen(screenCoordsVector.x,
					screenCoordsVector.y);
			cameraManager.target.x = worldPoint.x;
			cameraManager.target.y = worldPoint.y;
		}
		else {
			if (allowDrag) {
				if (null != mouseOverUnit) {
					war3MapViewer.clearUnitMouseOverHighlight();
					dragSelectPreviewUnits.clear();
					mouseOverUnit = null;
				}

				war3MapViewer.getClickLocation(clickLocationTemp, screenX, (int) worldScreenY);
				currentlyDraggingPointer = pointer;
				if (draggingMouseButton == Input.Buttons.MIDDLE) {
					cameraManager.target.add(lastMouseClickLocation.sub(clickLocationTemp).scl(-1));
				}
				else if (draggingMouseButton == Input.Buttons.LEFT) {
					// update mouseover
				}
				lastMouseClickLocation.set(clickLocationTemp);
			}
			else {
				if (mouseDownUIFrame != null) {
					mouseDownUIFrame.mouseDragged(rootFrame, uiViewport, screenCoordsVector.x,
							screenCoordsVector.y);
				}
			}
		}
		return false;
	}

	public boolean mouseMoved(final int screenX, final int screenY, final float worldScreenY) {
		screenCoordsVector.set(screenX, screenY);
		uiViewport.unproject(screenCoordsVector);
		final UIFrame mousedUIFrame = rootFrame.getFrameChildUnderMouse(screenCoordsVector.x,
				screenCoordsVector.y);
		if (!Objects.equals(mousedUIFrame, mouseOverUIFrame)) {
			if (mouseOverUIFrame != null) {
				mouseOverUIFrame.mouseExit(rootFrame, uiViewport);
			}
			if (mousedUIFrame instanceof ClickableFrame) {
				mouseOverUIFrame = (ClickableFrame) mousedUIFrame;
				if (mouseOverUIFrame != null) {
					mouseOverUIFrame.mouseEnter(rootFrame, uiViewport);
				}
				if (mousedUIFrame instanceof ClickableActionFrame) {
					loadTooltip((ClickableActionFrame) mousedUIFrame);
				}
			}
			else {
				mouseOverUIFrame = null;
				tooltipFrame.setVisible(false);
			}
		}
		if (mousedUIFrame == null) {
			final RenderWidget newMouseOverUnit = war3MapViewer.rayPickUnit(screenX, worldScreenY,
					anyClickableUnitFilter);
			if (!Objects.equals(newMouseOverUnit, mouseOverUnit)) {
				war3MapViewer.clearUnitMouseOverHighlight();
				dragSelectPreviewUnits.clear();
				if (newMouseOverUnit != null) {
					war3MapViewer.showUnitMouseOverHighlight(newMouseOverUnit);
				}
				mouseOverUnit = newMouseOverUnit;
			}
		}
		return false;
	}

	private void loadTooltip(final ClickableActionFrame mousedUIFrame) {
		final int goldCost = mousedUIFrame.getToolTipGoldCost();
		final int lumberCost = mousedUIFrame.getToolTipLumberCost();
		final int foodCost = mousedUIFrame.getToolTipFoodCost();
		final int manaCost = mousedUIFrame.getToolTipManaCost();
		final String toolTip = mousedUIFrame.getToolTip();
		final String uberTip = mousedUIFrame.getUberTip();
		if ((toolTip == null) || (uberTip == null)) {
			tooltipFrame.setVisible(false);
		}
		else {
			rootFrame.setText(tooltipUberTipText, uberTip);
			int resourceIndex = 0;
			if (goldCost != 0) {
				tooltipResourceFrames[resourceIndex].setVisible(true);
				tooltipResourceIconFrames[resourceIndex].setTexture("ToolTipGoldIcon", rootFrame);
				rootFrame.setText(tooltipResourceTextFrames[resourceIndex], Integer.toString(goldCost));
				resourceIndex++;
			}
			if (lumberCost != 0) {
				tooltipResourceFrames[resourceIndex].setVisible(true);
				tooltipResourceIconFrames[resourceIndex].setTexture("ToolTipLumberIcon", rootFrame);
				rootFrame.setText(tooltipResourceTextFrames[resourceIndex], Integer.toString(lumberCost));
				resourceIndex++;
			}
			if (foodCost != 0) {
				tooltipResourceFrames[resourceIndex].setVisible(true);
				tooltipResourceIconFrames[resourceIndex].setTexture("ToolTipSupplyIcon", rootFrame);
				rootFrame.setText(tooltipResourceTextFrames[resourceIndex], Integer.toString(foodCost));
				resourceIndex++;
			}
			if (manaCost != 0) {
				tooltipResourceFrames[resourceIndex].setVisible(true);
				tooltipResourceIconFrames[resourceIndex].setTexture("ToolTipManaIcon", rootFrame);
				rootFrame.setText(tooltipResourceTextFrames[resourceIndex], Integer.toString(manaCost));
				resourceIndex++;
			}
			for (int i = resourceIndex; i < tooltipResourceFrames.length; i++) {
				tooltipResourceFrames[i].setVisible(false);
			}
			float resourcesHeight;
			if (resourceIndex != 0) {
				tooltipUberTipText.addSetPoint(uberTipWithResourcesSetPoint);
				resourcesHeight = 0.014f;
			}
			else {
				tooltipUberTipText.addSetPoint(uberTipNoResourcesSetPoint);
				resourcesHeight = 0.004f;
			}
			rootFrame.setText(tooltipText, toolTip);
			final float predictedViewportHeight = tooltipText.getPredictedViewportHeight()
					+ GameUI.convertY(uiViewport, resourcesHeight)
					+ tooltipUberTipText.getPredictedViewportHeight() + GameUI.convertY(uiViewport, 0.003f);
			tooltipFrame.setHeight(predictedViewportHeight);
			tooltipFrame.positionBounds(rootFrame, uiViewport);
			tooltipFrame.setVisible(true);
		}
	}

	public float getHeightRatioCorrection() {
		return heightRatioCorrection;
	}

	@Override
	public void queueIconClicked(final int index) {
		final CUnit simulationUnit = selectedUnit.getSimulationUnit();
		if (simulationUnit.isConstructingOrUpgrading()) {
			switch (index) {
				case 0:
					for (final CAbility ability : simulationUnit.getAbilities()) {
						ability.checkCanUse(war3MapViewer.simulation, simulationUnit, OrderIds.cancel,
								BooleanAbilityActivationReceiver.INSTANCE);
						if (BooleanAbilityActivationReceiver.INSTANCE.isOk()) {

							final BooleanAbilityTargetCheckReceiver<Void> targetCheckReceiver = BooleanAbilityTargetCheckReceiver
									.<Void>getInstance().reset();
							ability.checkCanTargetNoTarget(war3MapViewer.simulation, simulationUnit, OrderIds.cancel,
									targetCheckReceiver);
							if (targetCheckReceiver.isTargetable()) {
								unitOrderListener.issueImmediateOrder(simulationUnit.getHandleId(),
										ability.getHandleId(), OrderIds.cancel, false);
							}
						}
					}
					break;
				case 1:
					final List<RenderWidget> unitList = Collections.singletonList(
							war3MapViewer.getRenderPeer(selectedUnit.getSimulationUnit().getWorkerInside()));
					war3MapViewer.doSelectUnit(unitList);
					selectWidgets(unitList);
					break;
				default:
					throw new IllegalStateException("Unexpected value: " + index);
			}
		}
		else {
			unitOrderListener.unitCancelTrainingItem(simulationUnit.getHandleId(), index);
		}
	}

	public void dispose() {
		if (rootFrame != null) {
			rootFrame.dispose();
		}
	}

	private class ItemCommandCardCommandListener implements CommandCardCommandListener {
		@Override
		public void onClick(final int abilityHandleId, final int orderId, final boolean rightClick) {
			final RenderUnit selectedUnit2 = selectedUnit;
			final CUnit simulationUnit = selectedUnit2.getSimulationUnit();
			if (rightClick) {
				final CAbilityInventory inventoryData = simulationUnit.getInventoryData();
				final int slot = orderId - 1;
				final CItem itemInSlot = inventoryData.getItemInSlot(slot);
				if (draggingItem != null) {
					final CUnit activeCmdSimUnit = activeCommandUnit.getSimulationUnit();
					unitOrderListener.issueTargetOrder(activeCmdSimUnit.getHandleId(),
							activeCmdSimUnit.getInventoryData().getHandleId(), OrderIds.itemdrag00 + slot,
							draggingItem.getHandleId(), false);
					setDraggingItem(null);
					activeCommand = null;
					activeCommandUnit = null;
				}
				else {
					if (itemInSlot != null) {
						setDraggingItem(itemInSlot);
						activeCommand = inventoryData;
						activeCommandUnit = selectedUnit2;
						activeCommandOrderId = OrderIds.dropitem;
					}
				}
			}
			else {
				final CSimulation game = war3MapViewer.simulation;
				final BooleanAbilityActivationReceiver receiver = BooleanAbilityActivationReceiver.INSTANCE;
				final CAbilityInventory inventoryData = simulationUnit.getInventoryData();
				inventoryData.checkCanUse(game, simulationUnit, orderId, receiver);
				if (receiver.isOk()) {
					final BooleanAbilityTargetCheckReceiver<Void> targetReceiver = BooleanAbilityTargetCheckReceiver
							.getInstance();
					targetReceiver.reset();
					inventoryData.checkCanTargetNoTarget(game, simulationUnit, orderId, targetReceiver);
					if (targetReceiver.isTargetable()) {
						unitOrderListener.issueImmediateOrder(simulationUnit.getHandleId(),
								inventoryData.getHandleId(), orderId, isShiftDown());
					}
				}
			}
		}

		@Override
		public void openMenu(final int orderId) {
			MeleeUI.this.openMenu(orderId);
		}

	}

	private final class MultiSelectUnitStateListener implements CUnitStateListener {
		private final RenderUnit sourceUnit;
		private int index;
		private boolean disposed;

		private MultiSelectUnitStateListener(final RenderUnit sourceUnit, final int index) {
			this.sourceUnit = sourceUnit;
			this.index = index;
		}

		public void dispose() {
			sourceUnit.getSimulationUnit().removeStateListener(this);
			disposed = true;
		}

		@Override
		public void lifeChanged() {
			if (disposed) {
				return;
			}
			if (sourceUnit.getSimulationUnit().isDead()) {
				selectedUnits.remove(sourceUnit);
				war3MapViewer.doUnselectUnit(sourceUnit);
				multiSelectUnitStateListeners.remove(index);
				for (int i = index; i < multiSelectUnitStateListeners.size(); i++) {
					multiSelectUnitStateListeners.get(i).index--;
				}
				dispose();
				reloadSelectedUnitUI(selectedUnit);
			}
			else {
				selectedUnitFrames[index]
						.setLifeRatioRemaining(sourceUnit.getSimulationUnit().getLife()
								/ sourceUnit.getSimulationUnit().getMaximumLife());
			}
		}

		@Override
		public void manaChanged() {
			if (disposed) {
				return;
			}
			selectedUnitFrames[index]
					.setManaRatioRemaining(sourceUnit.getSimulationUnit().getMana()
							/ sourceUnit.getSimulationUnit().getMaximumMana());
		}

		@Override
		public void ordersChanged() {
			if (disposed) {
				return;
			}

		}

		@Override
		public void queueChanged() {
			if (disposed) {
				return;
			}

		}

		@Override
		public void rallyPointChanged() {
			if (disposed) {
				return;
			}

		}

		@Override
		public void waypointsChanged() {
			if (disposed) {
				return;
			}

		}

		@Override
		public void heroStatsChanged() {
			if (disposed) {
				return;
			}

		}

		@Override
		public void inventoryChanged() {
			if (disposed) {
				return;
			}

		}

	}

	public GameCameraManager getCameraManager() {
		return cameraManager;
	}

	public Music playMusic(final String musicField, final boolean random, int index) {
		if (WarsmashConstants.ENABLE_MUSIC) {
			stopMusic();

			final String[] semicolonMusics = musicField.split(";");
			final List<String> musicPaths = new ArrayList<>();
			for (String musicPath : semicolonMusics) {
				// dumb support for comma as well as semicolon, I wonder if we can
				// clean this up, simplify?
				if (musicSLK.get(musicPath) != null) {
					musicPath = musicSLK.get(musicPath).getField("FileNames");
				}
				final String[] moreSplitMusics = musicPath.split(",");
				Collections.addAll(musicPaths, moreSplitMusics);
			}
			final String[] musics = musicPaths.toArray(new String[0]);

			if (random) {
				index = (int) (Math.random() * musics.length);
			}
			currentMusics = new Music[musics.length];
			for (int i = 0; i < musics.length; i++) {
				final Music newMusic = Gdx.audio
						.newMusic(new DataSourceFileHandle(war3MapViewer.dataSource, musics[i]));
				newMusic.setVolume(1.0f);
				currentMusics[i] = newMusic;
			}
			currentMusicIndex = index;
			currentMusicRandomizeIndex = random;
			currentMusics[index].play();
		}
		return null;
	}

	public void gameClosed() {
		stopMusic();
	}

	private void stopMusic() {
		if (currentMusics != null) {
			for (final Music music : currentMusics) {
				music.stop();
			}
			currentMusics = null;
		}
	}

	public Scene getUiScene() {
		return uiScene;
	}

	public CTimerDialog createTimerDialog(final CTimer timer) {
		final UIFrame timerDialog = rootFrame.createFrame("TimerDialog", rootFrame, 0, 0);
		final StringFrame valueFrame = (StringFrame) rootFrame.getFrameByName("TimeDialogValue", 0);
		final StringFrame titleFrame = (StringFrame) rootFrame.getFrameByName("TimerDialogTitle", 0);
		return new CTimerDialog(timer, timerDialog, valueFrame, titleFrame);
	}

	public void displayTimedText(final float x, final float y, final float duration, final String message) {
		showGameMessage(message, duration); // TODO x y
	}

	public CScriptDialog createScriptDialog(final GlobalScope globalScope) {
		final SimpleFrame scriptDialog = (SimpleFrame) rootFrame.createFrame("ScriptDialog", rootFrame, 0, 0);
		scriptDialog.addAnchor(new AnchorDefinition(FramePoint.TOP, 0, GameUI.convertY(uiViewport, -0.05f)));
		scriptDialog.setVisible(false);
		final StringFrame scriptDialogTextFrame = (StringFrame) rootFrame.getFrameByName("ScriptDialogText", 0);
		scriptDialog.positionBounds(rootFrame, uiViewport);
		return new CScriptDialog(globalScope, scriptDialog, scriptDialogTextFrame);
	}

	public CScriptDialogButton createScriptDialogButton(final CScriptDialog scriptDialog, final String text,
			final char hotkey) {
		// TODO use hotkey
		final GlueTextButtonFrame scriptDialogButton = (GlueTextButtonFrame) rootFrame
				.createFrame("ScriptDialogButton", scriptDialog.getScriptDialogFrame(), 0, 0);
		scriptDialogButton.setHeight(GameUI.convertY(uiViewport, 0.03f));
		final StringFrame scriptDialogTextFrame = (StringFrame) rootFrame.getFrameByName("ScriptDialogButtonText",
				0);
		rootFrame.setText(scriptDialogTextFrame, text);
		scriptDialogButton.addSetPoint(new SetPoint(FramePoint.TOP, scriptDialog.getLastAddedComponent(),
				FramePoint.BOTTOM, 0, GameUI.convertY(uiViewport, -0.005f)));
		final CScriptDialogButton newButton = new CScriptDialogButton(scriptDialogButton, scriptDialogTextFrame);
		scriptDialog.addButton(rootFrame, uiViewport, newButton);
		return newButton;
	}

	public void destroyDialog(final CScriptDialog dialog) {
		rootFrame.remove(dialog.getScriptDialogFrame());
	}

	public void clearDialog(final CScriptDialog dialog) {
		destroyDialog(dialog);
		final SimpleFrame scriptDialog = (SimpleFrame) rootFrame.createFrame("ScriptDialog", rootFrame, 0, 0);
		scriptDialog.addAnchor(new AnchorDefinition(FramePoint.TOP, 0, GameUI.convertY(uiViewport, -0.05f)));
		scriptDialog.setVisible(false);
		final StringFrame scriptDialogTextFrame = (StringFrame) rootFrame.getFrameByName("ScriptDialogText", 0);
		scriptDialog.positionBounds(rootFrame, uiViewport);
		dialog.reset(scriptDialog, scriptDialogTextFrame);
	}
}
