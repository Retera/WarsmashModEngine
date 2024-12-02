package com.etheller.warsmash.viewer5.handlers.w3x.ui;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.TimeUnit;

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
import com.badlogic.gdx.utils.viewport.Viewport;
import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.warsmash.datasources.DataSource;
import com.etheller.warsmash.parsers.fdf.GameUI;
import com.etheller.warsmash.parsers.fdf.datamodel.AnchorDefinition;
import com.etheller.warsmash.parsers.fdf.datamodel.FramePoint;
import com.etheller.warsmash.parsers.fdf.datamodel.TextJustify;
import com.etheller.warsmash.parsers.fdf.datamodel.Vector4Definition;
import com.etheller.warsmash.parsers.fdf.frames.AbstractRenderableFrame;
import com.etheller.warsmash.parsers.fdf.frames.AbstractUIFrame;
import com.etheller.warsmash.parsers.fdf.frames.FilterModeTextureFrame;
import com.etheller.warsmash.parsers.fdf.frames.GlueTextButtonFrame;
import com.etheller.warsmash.parsers.fdf.frames.SetPoint;
import com.etheller.warsmash.parsers.fdf.frames.SimpleButtonFrame;
import com.etheller.warsmash.parsers.fdf.frames.SimpleFrame;
import com.etheller.warsmash.parsers.fdf.frames.SimpleStatusBarFrame;
import com.etheller.warsmash.parsers.fdf.frames.SingleStringFrame;
import com.etheller.warsmash.parsers.fdf.frames.SpriteFrame;
import com.etheller.warsmash.parsers.fdf.frames.SpriteFrame2;
import com.etheller.warsmash.parsers.fdf.frames.StringFrame;
import com.etheller.warsmash.parsers.fdf.frames.TextureFrame;
import com.etheller.warsmash.parsers.fdf.frames.UIFrame;
import com.etheller.warsmash.parsers.jass.Jass2.RootFrameListener;
import com.etheller.warsmash.units.DataTable;
import com.etheller.warsmash.units.Element;
import com.etheller.warsmash.util.FastNumberFormat;
import com.etheller.warsmash.util.ImageUtils;
import com.etheller.warsmash.util.RenderMathUtils;
import com.etheller.warsmash.util.StringBundle;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.Bounds;
import com.etheller.warsmash.viewer5.Scene;
import com.etheller.warsmash.viewer5.ViewerTextureRenderable;
import com.etheller.warsmash.viewer5.gl.Extensions;
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
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.RenderUnitType;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.RenderWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.ability.AbilityDataUI;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.ability.IconUI;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.ability.ItemUI;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.ability.OrderButtonUI;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.ability.UnitIconUI;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.commandbuttons.CommandButtonListener;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.commandbuttons.CommandCardActivationReceiverPreviewCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CDestructable;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CGameplayConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CItem;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CItemType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CPlayerStateListener;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit.QueueItemType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitClassification;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitEnumFunction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitStateListener;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUpgradeType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUpgradeType.UpgradeLevel;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidgetFilterFunction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityAttack;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityGenericDoNothing;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityMove;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityView;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.COrderButton;
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
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.CBuff;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.GenericNoIconAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.GenericSingleIconActiveAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.GenericSingleIconPassiveAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.harvest.CAbilityReturnResources;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.hero.CAbilityHero;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.hero.CPrimaryAttribute;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.inventory.CAbilityInventory;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.item.shop.CAbilityNeutralBuilding;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.item.shop.CAbilitySellItems;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.jass.CAbilityJass;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.mine.CAbilityGoldMinable;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.mine.CAbilityOverlayedMine;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.nightelf.root.CAbilityRoot;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.queue.CAbilityQueue;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.queue.CAbilityRally;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.queue.CAbilityReviveHero;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.upgrade.CAbilityUpgrade;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.ability.AbilityBuilderActiveAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CAttackType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CDefenseType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CUpgradeClass;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CodeKeyType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttack;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttackMissileLine;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttackMissileSplash;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.data.CUnitRace;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.COrder;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.pathing.CBuildingPathingType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CAllianceType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayerColor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayerUnitOrderListener;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CRace;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CRaceManagerEntry;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.timers.CTimer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.JassGameEventsWar3;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit.BuildOnBuildingIntersector;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.BooleanAbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.BooleanAbilityTargetCheckReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.CWidgetAbilityTargetCheckReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.CommandStringErrorKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.ExternStringMsgAbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.ExternStringMsgTargetCheckReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.MeleeUIAbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.PointAbilityTargetCheckReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.ResourceType;
import com.etheller.warsmash.viewer5.handlers.w3x.ui.command.AbstractClickableActionFrame;
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
		QueueIconListener, CommandErrorListener, CPlayerStateListener, WarsmashUI, WarsmashToggleableUI {
	private static final long WORLD_FRAME_MESSAGE_FADEOUT_MILLIS = TimeUnit.SECONDS.toMillis(9);
	private static final long WORLD_FRAME_MESSAGE_EXPIRE_MILLIS = TimeUnit.SECONDS.toMillis(10);
	private static final long WORLD_FRAME_MESSAGE_FADE_DURATION = WORLD_FRAME_MESSAGE_EXPIRE_MILLIS
			- WORLD_FRAME_MESSAGE_FADEOUT_MILLIS;
	private static final String BUILDING_PATHING_PREVIEW_KEY = "buildingPathingPreview";
	public static final float DEFAULT_COMMAND_CARD_ICON_WIDTH = 0.039f;
	public static final float DEFAULT_INVENTORY_ICON_WIDTH = 0.03125f;
	private static final int COMMAND_CARD_WIDTH = 4;
	private static final int COMMAND_CARD_HEIGHT = 3;
	private static final int INVENTORY_WIDTH = WarsmashConstants.USE_NINE_ITEM_INVENTORY ? 3 : 2;
	private static final int INVENTORY_HEIGHT = 3;

	private static final Vector2 screenCoordsVector = new Vector2();
	private static final Vector3 clickLocationTemp = new Vector3();
	private static final AbilityPointTarget clickLocationTemp2 = new AbilityPointTarget();
	private static final int BUFF_DISPLAY_MAX = 10;
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
	private SimpleFrame unitPortrait;
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

	private SimpleFrame simpleInfoPanelUnitDetail;
	private StringFrame simpleNameValue;
	private StringFrame simpleClassValue;
	private StringFrame simpleBuildingActionLabel;
	private SimpleStatusBarFrame simpleBuildTimeIndicator;
	private SimpleStatusBarFrame simpleHeroLevelBar;
	private SimpleStatusBarFrame simpleProgressIndicator;

	private final BuffBarIcon[] buffBarIcons = new BuffBarIcon[BUFF_DISPLAY_MAX];
	private int currentBuffBarIconIndex = 0;

	private UIFrame simpleInfoPanelBuildingDetail;
	private StringFrame simpleBuildingNameValue;
	private StringFrame simpleBuildingDescriptionValue;
	private StringFrame simpleBuildingBuildingActionLabel;
	private SimpleStatusBarFrame simpleBuildingBuildTimeIndicator;
	private TextureFrame simpleBuildQueueBackdrop;
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
	private InfoPanelIconBackdrops damageBackdropsNeutral;
	private InfoPanelIconBackdrops defenseBackdropsNeutral;

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
	private final ArrayDeque<GameMessage> gameMessages = new ArrayDeque<>();
	private long lastErrorMessageExpireTime;
	private long lastErrorMessageFadeTime;

	private MenuCursorState cursorState;
	private Color cursorColor;
	private CAbilityView activeCommand;
	private int activeCommandOrderId;
	private RenderUnit activeCommandUnit;
	private MdxComplexInstance cursorModelInstance = null;
	private MdxComplexInstance rallyPointInstance = null;
	private BufferedImage cursorModelPathing;
	private Pixmap cursorModelUnderneathPathingRedGreenPixmap;
	private Texture cursorModelUnderneathPathingRedGreenPixmapTexture;
	private PixmapTextureData cursorModelUnderneathPathingRedGreenPixmapTextureData;
	private SplatModel cursorModelUnderneathPathingRedGreenSplatModel;
	private CUnitType cursorBuildingUnitType;
	private SplatMover placementCursor = null;
	private final CursorTargetSetupVisitor cursorTargetSetupVisitor;

	private int selectedSoundCount = 0;
	private final ActiveCommandUnitTargetFilter activeCommandUnitTargetFilter;

	// TODO these corrections are used for old hardcoded UI stuff, we should
	// probably remove them later
	private final float widthRatioCorrection;
	private final float heightRatioCorrection;
	private ClickableFrame mouseDownUIFrame;
	private ClickableFrame mouseOverUIFrame;
	private SimpleFrame smashSimpleInfoPanel;
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
	private final FreeTypeFontParameter textTagFontParam = new FreeTypeFontParameter();
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
	private final Vector3 lastMouseDragCameraTargetStart = new Vector3();
	private final Vector3 lastMouseClickLocation = new Vector3();

	private final List<SimpleStatusBarFrame> hpBarFrames = new ArrayList<>();
	private int hpBarFrameIndex = 0;
	private boolean allowDrag;
	private int currentlyDraggingPointer = -1;
	private String[] includeFrames;
	private String[] ignoreFrames;
	private final ShapeRenderer shapeRenderer = new ShapeRenderer();
	private final List<MultiSelectUnitStateListener> multiSelectUnitStateListeners = new ArrayList<>();
	private long lastUnitClickTime = 0;
	private RenderWidget lastClickUnit;
	private MultiSelectionIconListener multiSelectClickListener;
	private MultiSelectionIconListener cargoClickListener;
	private float frontQueueIconWidth;
	private int draggingMouseButton;
	private MusicPlayer musicPlayer;
	private final List<CTimerDialog> timerDialogs = new ArrayList<>();
	private final AnyClickableUnitFilter anyClickableUnitFilter;
	private final AnyTargetableUnitFilter anyTargetableUnitFilter;
	private final DataTable musicSLK;

	private final BuildOnBuildingIntersector buildOnBuildingIntersector = new BuildOnBuildingIntersector();

	public int[][] commandCardGridHotkeys = { { Input.Keys.Q, Input.Keys.W, Input.Keys.E, Input.Keys.R },
			{ Input.Keys.A, Input.Keys.S, Input.Keys.D, Input.Keys.F },
			{ Input.Keys.Z, Input.Keys.X, Input.Keys.C, Input.Keys.V } };
	private ClickablePortrait clickablePortrait;
	private float simpleProgressIndicatorDurationRemaining;
	private float simpleProgressIndicatorDurationMax;
	private StringFrame smashBuffStatusBar;

	private boolean showing;
	private final CommandCardActivationReceiverPreviewCallback activationReceiverPreviewCallback = new CommandCardActivationReceiverPreviewCallback();
	private float worldFrameUnitMessageFontHeight;
	private UIFrame upperButtonBar;
	private UIFrame cinematicPanel;
	private StringFrame cinematicSpeakerText;
	private StringFrame cinematicDialogueText;
	private UIFrame simpleInfoPanelItemDetail;
	private StringFrame simpleItemNameValue;
	private StringFrame simpleItemDescriptionValue;
	private UIFrame simpleInfoPanelDestructableDetail;
	private StringFrame simpleDestructableNameValue;
	private SimpleFrame smashCommandButtons;
	private boolean userControlEnabled = true;
	private boolean subtitleDisplayOverride;
	private UIFrame cinematicScenePanel;
	private CinematicPortrait cinematicPortrait;

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

		this.cameraManager = new GameCameraManager(cameraPresets, cameraRates);

		this.cameraManager.setupCamera(war3MapViewer.worldScene);
		this.localPlayer = this.war3MapViewer.simulation.getPlayer(war3MapViewer.getLocalPlayerIndex());
		final float[] startLocation = this.localPlayer.getStartLocation();
		this.cameraManager.target.x = startLocation[0];
		this.cameraManager.target.y = startLocation[1];

		this.activeButtonTexture = ImageUtils.getAnyExtensionTexture(war3MapViewer.mapMpq,
				"UI\\Widgets\\Console\\Human\\CommandButton\\human-activebutton.blp");
		this.activeCommandUnitTargetFilter = new ActiveCommandUnitTargetFilter();
		this.widthRatioCorrection = this.uiViewport.getMinWorldWidth() / 1600f;
		this.heightRatioCorrection = this.uiViewport.getMinWorldHeight() / 1200f;
		this.rallyPositioningVisitor = new RallyPositioningVisitor();
		this.cursorTargetSetupVisitor = new CursorTargetSetupVisitor();

		this.localPlayer.addStateListener(this);

		this.itemCommandCardCommandListener = new ItemCommandCardCommandListener();
		this.anyClickableUnitFilter = new AnyClickableUnitFilter();
		this.anyTargetableUnitFilter = new AnyTargetableUnitFilter();

		this.musicSLK = new DataTable(StringBundle.EMPTY);
		final String musicSLKPath = "UI\\SoundInfo\\Music.SLK";
		if (war3MapViewer.dataSource.has(musicSLKPath)) {
			try (InputStream miscDataTxtStream = war3MapViewer.dataSource.getResourceAsStream(musicSLKPath)) {
				this.musicSLK.readSLK(miscDataTxtStream);
			}
			catch (final IOException e) {
				e.printStackTrace();
			}
		}

		if (WarsmashConstants.ENABLE_MUSIC) {
			this.musicPlayer = new MusicPlayerLibGDX(dataSource, this.musicSLK);
		}
		else {
			this.musicPlayer = MusicPlayer.DO_NOTHING;
		}
	}

	private MeleeUIMinimap createMinimap(final War3MapViewer war3MapViewer) {
		final Rectangle minimapDisplayArea = new Rectangle(18.75f * this.widthRatioCorrection,
				13.75f * this.heightRatioCorrection, 278.75f * this.widthRatioCorrection,
				276.25f * this.heightRatioCorrection);
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
		final Texture[] teamColors = new Texture[WarsmashConstants.MAX_PLAYERS];
		for (int i = 0; i < teamColors.length; i++) {
			teamColors[i] = ImageUtils.getAnyExtensionTexture(war3MapViewer.dataSource,
					"ReplaceableTextures\\" + ReplaceableIds.getPathString(1) + ReplaceableIds.getIdString(i) + ".blp");
		}
		final Texture[] specialIcons = new Texture[5];
		specialIcons[0] = ImageUtils.getAnyExtensionTexture(war3MapViewer.mapMpq, "UI\\MiniMap\\minimap-gold.blp");
		specialIcons[1] = ImageUtils.getAnyExtensionTexture(war3MapViewer.mapMpq,
				"UI\\MiniMap\\minimap-neutralbuilding.blp");
		specialIcons[2] = ImageUtils.getAnyExtensionTexture(war3MapViewer.mapMpq, "UI\\MiniMap\\minimap-hero.blp");
		specialIcons[3] = ImageUtils.getAnyExtensionTexture(war3MapViewer.mapMpq,
				"UI\\MiniMap\\minimap-gold-entangled.blp");
		specialIcons[4] = ImageUtils.getAnyExtensionTexture(war3MapViewer.mapMpq,
				"UI\\MiniMap\\minimap-gold-haunted.blp");
		final Rectangle playableMapArea = war3MapViewer.terrain.getPlayableMapArea();
		return new MeleeUIMinimap(minimapDisplayArea, playableMapArea, minimapTexture, teamColors, specialIcons);
	}

	/**
	 * Called "main" because this was originally written in JASS so that maps could
	 * override it, and I may convert it back to the JASS at some point.
	 */
	@Override
	public void main() {
		// =================================
		// Load skins and templates
		// =================================
		final CRace race = this.localPlayer.getRace();
		final CRaceManagerEntry raceEntry = WarsmashConstants.RACE_MANAGER.get(race);
		final String racialSkinKey = raceEntry.getKey();
		final int racialCommandIndex = raceEntry.getRaceId() - 1;

		this.rootFrame = new GameUI(this.dataSource, GameUI.loadSkin(this.dataSource, racialSkinKey), this.uiViewport,
				this.uiScene, this.war3MapViewer, racialCommandIndex, this.war3MapViewer.getAllObjectData().getWts());
		this.rootFrameListener.onCreate(this.rootFrame);
		try {
			this.rootFrame.loadTOCFile("UI\\FrameDef\\FrameDef.toc");
		}
		catch (final IOException exc) {
			throw new IllegalStateException("Unable to load FrameDef.toc", exc);
		}
		try {
			this.rootFrame.loadTOCFile("UI\\FrameDef\\SmashFrameDef.toc");
		}
		catch (final IOException exc) {
			throw new IllegalStateException("Unable to load SmashFrameDef.toc", exc);
		}
		this.damageBackdrops = new InfoPanelIconBackdrops(CAttackType.values(), this.rootFrame, "Damage", "");
		this.defenseBackdrops = new InfoPanelIconBackdrops(CDefenseType.values(), this.rootFrame, "Armor", "");
		this.damageBackdropsNeutral = new InfoPanelIconBackdrops(CAttackType.values(), this.rootFrame, "Damage",
				"Neutral");
		this.defenseBackdropsNeutral = new InfoPanelIconBackdrops(CDefenseType.values(), this.rootFrame, "Armor",
				"Neutral");

		// =================================
		// Load major UI components
		// =================================
		// Console UI is the background with the racial theme
		this.consoleUI = this.rootFrame.createSimpleFrame("ConsoleUI", this.rootFrame, 0);
		this.consoleUI.setSetAllPoints(true);

		// Resource bar is a 3 part bar with Gold, Lumber, and Food.
		// Its template does not specify where to put it, so we must
		// put it in the "TOPRIGHT" corner.
		this.resourceBar = this.rootFrame.createSimpleFrame("ResourceBarFrame", this.consoleUI, 0);
		this.resourceBar.addSetPoint(new SetPoint(FramePoint.TOPRIGHT, this.consoleUI, FramePoint.TOPRIGHT, 0, 0));
		this.resourceBarGoldText = (StringFrame) this.rootFrame.getFrameByName("ResourceBarGoldText", 0);
		goldChanged();
		this.resourceBarLumberText = (StringFrame) this.rootFrame.getFrameByName("ResourceBarLumberText", 0);
		lumberChanged();
		this.resourceBarSupplyText = (StringFrame) this.rootFrame.getFrameByName("ResourceBarSupplyText", 0);
		foodChanged();
		this.resourceBarUpkeepText = (StringFrame) this.rootFrame.getFrameByName("ResourceBarUpkeepText", 0);
		upkeepChanged();
		this.resourceBarSupplyText.setWidth(this.resourceBarUpkeepText.getAssignedWidth());
		this.resourceBarLumberText.setWidth(this.resourceBarUpkeepText.getAssignedWidth());
		this.resourceBarGoldText.setWidth(this.resourceBarUpkeepText.getAssignedWidth());

		this.upperButtonBar = this.rootFrame.createSimpleFrame("UpperButtonBarFrame", this.consoleUI, 0);
		this.upperButtonBar.addSetPoint(new SetPoint(FramePoint.TOPLEFT, this.consoleUI, FramePoint.TOPLEFT, 0, 0));

		this.questsButton = (SimpleButtonFrame) this.rootFrame.getFrameByName("UpperButtonBarQuestsButton", 0);
		this.questsButton.setEnabled(false);
		this.menuButton = (SimpleButtonFrame) this.rootFrame.getFrameByName("UpperButtonBarMenuButton", 0);
		this.alliesButton = (SimpleButtonFrame) this.rootFrame.getFrameByName("UpperButtonBarAlliesButton", 0);
		this.alliesButton.setEnabled(false);
		this.chatButton = (SimpleButtonFrame) this.rootFrame.getFrameByName("UpperButtonBarChatButton", 0);
		this.chatButton.setEnabled(false);

		this.smashEscMenu = (SimpleFrame) this.rootFrame.createSimpleFrame("SmashEscMenu", this.rootFrame, 0);
		this.smashEscMenu.addAnchor(new AnchorDefinition(FramePoint.TOP, 0, GameUI.convertY(this.uiViewport, -0.05f)));
		final UIFrame escMenuBackdrop = this.rootFrame.createFrame("EscMenuBackdrop", this.smashEscMenu, 0, 0);
		escMenuBackdrop.setVisible(false);
		final UIFrame escMenuMainPanel = this.rootFrame.createFrame("EscMenuMainPanel", this.smashEscMenu, 0, 0);
		escMenuMainPanel.setVisible(false);
		this.smashEscMenu.add(escMenuBackdrop);
		this.smashEscMenu.add(escMenuMainPanel);

		final UIFrame escMenuInnerMainPanel = this.rootFrame.getFrameByName("MainPanel", 0);
		final GlueTextButtonFrame pauseButton = (GlueTextButtonFrame) this.rootFrame.getFrameByName("PauseButton", 0);
		pauseButton.setEnabled(false);
		final GlueTextButtonFrame saveGameButton = (GlueTextButtonFrame) this.rootFrame.getFrameByName("SaveGameButton",
				0);
		saveGameButton.setEnabled(false);
		final GlueTextButtonFrame loadGameButton = (GlueTextButtonFrame) this.rootFrame.getFrameByName("LoadGameButton",
				0);
		loadGameButton.setEnabled(false);
		final GlueTextButtonFrame optionsButton = (GlueTextButtonFrame) this.rootFrame.getFrameByName("OptionsButton",
				0);
		optionsButton.setEnabled(false);
		final GlueTextButtonFrame helpButton = (GlueTextButtonFrame) this.rootFrame.getFrameByName("HelpButton", 0);
		helpButton.setEnabled(false);
		final GlueTextButtonFrame tipsButton = (GlueTextButtonFrame) this.rootFrame.getFrameByName("TipsButton", 0);
		tipsButton.setEnabled(false);
		final GlueTextButtonFrame endGameButton = (GlueTextButtonFrame) this.rootFrame.getFrameByName("EndGameButton",
				0);
		final GlueTextButtonFrame returnButton = (GlueTextButtonFrame) this.rootFrame.getFrameByName("ReturnButton", 0);

		final UIFrame escMenuInnerEndGamePanel = this.rootFrame.getFrameByName("EndGamePanel", 0);
		final GlueTextButtonFrame endGamePreviousButton = (GlueTextButtonFrame) this.rootFrame
				.getFrameByName("PreviousButton", 0);
		final GlueTextButtonFrame endGameQuitButton = (GlueTextButtonFrame) this.rootFrame.getFrameByName("QuitButton",
				0);
		final GlueTextButtonFrame endGameRestartButton = (GlueTextButtonFrame) this.rootFrame
				.getFrameByName("RestartButton", 0);
		endGameRestartButton.setEnabled(false);
		final GlueTextButtonFrame endGameExitButton = (GlueTextButtonFrame) this.rootFrame.getFrameByName("ExitButton",
				0);

		final UIFrame escMenuInnerConfirmQuitPanel = this.rootFrame.getFrameByName("ConfirmQuitPanel", 0);
		final GlueTextButtonFrame confirmQuitCancelButton = (GlueTextButtonFrame) this.rootFrame
				.getFrameByName("ConfirmQuitCancelButton", 0);
		final GlueTextButtonFrame confirmQuitQuitButton = (GlueTextButtonFrame) this.rootFrame
				.getFrameByName("ConfirmQuitQuitButton", 0);
		final UIFrame escMenuInnerHelpPanel = this.rootFrame.getFrameByName("HelpPanel", 0);
		final UIFrame escMenuInnerTipsPanel = this.rootFrame.getFrameByName("TipsPanel", 0);
		escMenuInnerMainPanel.setVisible(false);
		escMenuInnerEndGamePanel.setVisible(false);
		escMenuInnerConfirmQuitPanel.setVisible(false);
		escMenuInnerHelpPanel.setVisible(false);
		escMenuInnerTipsPanel.setVisible(false);

		this.menuButton.setOnClick(new Runnable() {
			@Override
			public void run() {
				escMenuBackdrop.setVisible(true);
				escMenuMainPanel.setVisible(true);
				MeleeUI.this.smashEscMenu.setVisible(true);
				escMenuInnerMainPanel.setVisible(true);
				updateEscMenuCurrentPanel(escMenuBackdrop, escMenuMainPanel, escMenuInnerMainPanel);
			}
		});
		returnButton.setOnClick(new Runnable() {
			@Override
			public void run() {
				escMenuBackdrop.setVisible(false);
				escMenuMainPanel.setVisible(false);
				MeleeUI.this.smashEscMenu.setVisible(false);
				escMenuInnerMainPanel.setVisible(false);
			}
		});
		endGameButton.setOnClick(new Runnable() {
			@Override
			public void run() {
				escMenuInnerMainPanel.setVisible(false);
				escMenuInnerEndGamePanel.setVisible(true);
				updateEscMenuCurrentPanel(escMenuBackdrop, escMenuMainPanel, escMenuInnerEndGamePanel);
			}
		});
		endGamePreviousButton.setOnClick(new Runnable() {
			@Override
			public void run() {
				escMenuInnerEndGamePanel.setVisible(false);
				escMenuInnerMainPanel.setVisible(true);
				updateEscMenuCurrentPanel(escMenuBackdrop, escMenuMainPanel, escMenuInnerMainPanel);
			}
		});
		endGameQuitButton.setOnClick(new Runnable() {
			@Override
			public void run() {
				escMenuInnerEndGamePanel.setVisible(false);
				MeleeUI.this.exitGameRunnable.run();
			}
		});
		endGameExitButton.setOnClick(new Runnable() {
			@Override
			public void run() {
				escMenuInnerEndGamePanel.setVisible(false);
				escMenuInnerConfirmQuitPanel.setVisible(true);
				updateEscMenuCurrentPanel(escMenuBackdrop, escMenuMainPanel, escMenuInnerConfirmQuitPanel);
			}
		});
		confirmQuitCancelButton.setOnClick(new Runnable() {
			@Override
			public void run() {
				escMenuInnerEndGamePanel.setVisible(true);
				escMenuInnerConfirmQuitPanel.setVisible(false);
				updateEscMenuCurrentPanel(escMenuBackdrop, escMenuMainPanel, escMenuInnerEndGamePanel);
			}
		});
		confirmQuitQuitButton.setOnClick(new Runnable() {
			@Override
			public void run() {
				Gdx.app.exit();
			}
		});

		// Create the Time Indicator (clock)
		this.timeIndicator = (SpriteFrame) this.rootFrame.createFrame("TimeOfDayIndicator", this.rootFrame, 0, 0);
		this.timeIndicator.setSequence(0); // play the stand
		this.timeIndicator.setAnimationSpeed(0.0f); // do not advance automatically

		// Create the unit portrait stuff
		this.portrait = new Portrait(this.war3MapViewer, this.portraitScene);
		this.unitPortrait = (SimpleFrame) this.rootFrame.createSimpleFrame("UnitPortrait", this.consoleUI, 0);
		final SimpleFrame unitPortraitModel = (SimpleFrame) this.rootFrame.getFrameByName("UnitPortraitModel", 0);
		this.clickablePortrait = new ClickablePortrait("SmashClickablePortrait", unitPortraitModel);
		this.clickablePortrait.setSetAllPoints(true);
		unitPortraitModel.add(this.clickablePortrait);

		this.unitLifeText = (StringFrame) this.rootFrame.getFrameByName("UnitPortraitHitPointText", 0);
		this.unitManaText = (StringFrame) this.rootFrame.getFrameByName("UnitPortraitManaPointText", 0);

		final float infoPanelUnitDetailWidth = GameUI.convertY(this.uiViewport, 0.180f);
		final float infoPanelUnitDetailHeight = GameUI.convertY(this.uiViewport, 0.120f);
		this.smashSimpleInfoPanel = (SimpleFrame) this.rootFrame.createSimpleFrame("SmashSimpleInfoPanel",
				this.rootFrame, 0);
		this.smashSimpleInfoPanel
				.addAnchor(new AnchorDefinition(FramePoint.BOTTOM, 0, GameUI.convertY(this.uiViewport, 0.0f)));
		this.smashSimpleInfoPanel.setWidth(infoPanelUnitDetailWidth);
		this.smashSimpleInfoPanel.setHeight(infoPanelUnitDetailHeight);

		// Create Simple Info Unit Detail
		this.simpleInfoPanelUnitDetail = (SimpleFrame) this.rootFrame.createSimpleFrame("SimpleInfoPanelUnitDetail",
				this.smashSimpleInfoPanel, 0);
		this.simpleNameValue = (StringFrame) this.rootFrame.getFrameByName("SimpleNameValue", 0);
		this.simpleClassValue = (StringFrame) this.rootFrame.getFrameByName("SimpleClassValue", 0);
		this.simpleBuildingActionLabel = (StringFrame) this.rootFrame.getFrameByName("SimpleBuildingActionLabel", 0);
		this.simpleBuildTimeIndicator = (SimpleStatusBarFrame) this.rootFrame.getFrameByName("SimpleBuildTimeIndicator",
				0);
		final TextureFrame simpleBuildTimeIndicatorBar = this.simpleBuildTimeIndicator.getBarFrame();
		simpleBuildTimeIndicatorBar.setTexture("SimpleBuildTimeIndicator", this.rootFrame);
		final TextureFrame simpleBuildTimeIndicatorBorder = this.simpleBuildTimeIndicator.getBorderFrame();
		simpleBuildTimeIndicatorBorder.setTexture("SimpleBuildTimeIndicatorBorder", this.rootFrame);
		final float buildTimeIndicatorWidth = GameUI.convertX(this.uiViewport, 0.10538f);
		final float buildTimeIndicatorHeight = GameUI.convertY(this.uiViewport, 0.0103f);
		this.simpleBuildTimeIndicator.setWidth(buildTimeIndicatorWidth);
		this.simpleBuildTimeIndicator.setHeight(buildTimeIndicatorHeight);

		this.simpleHeroLevelBar = (SimpleStatusBarFrame) this.rootFrame.getFrameByName("SimpleHeroLevelBar", 0);
		final TextureFrame simpleHeroLevelBarBar = this.simpleHeroLevelBar.getBarFrame();
		simpleHeroLevelBarBar.setTexture("SimpleXpBarConsole", this.rootFrame);
		simpleHeroLevelBarBar.setColor(new Color(138f / 255f, 0, 131f / 255f, 1f));
		final TextureFrame simpleHeroLevelBarBorder = this.simpleHeroLevelBar.getBorderFrame();
		simpleHeroLevelBarBorder.setTexture("SimpleXpBarBorder", this.rootFrame);
		this.simpleHeroLevelBar.setWidth(infoPanelUnitDetailWidth);

		this.simpleProgressIndicator = (SimpleStatusBarFrame) this.rootFrame.getFrameByName("SimpleProgressIndicator",
				0);
		final TextureFrame simpleProgressIndicatorBar = this.simpleProgressIndicator.getBarFrame();
		simpleProgressIndicatorBar.setTexture("SimpleProgressBarConsole", this.rootFrame);
		simpleProgressIndicatorBar.setColor(new Color(0.254902f, 0.509804f, 0.823529f, 1f));
		// 0.823529, 0.509804, 0.254902
		final TextureFrame simpleProgressIndicatorBorder = this.simpleProgressIndicator.getBorderFrame();
		simpleProgressIndicatorBorder.setTexture("SimpleProgressBarBorder", this.rootFrame);
		this.simpleProgressIndicator.setWidth(infoPanelUnitDetailWidth);
		this.simpleProgressIndicator.setVisible(false);

		this.smashBuffStatusBar = this.rootFrame.createStringFrame("SmashBuffStatusBar", this.simpleInfoPanelUnitDetail,
				Color.WHITE, TextJustify.LEFT, TextJustify.MIDDLE, 0.01f);
		this.rootFrame.remove(this.smashBuffStatusBar);
		this.simpleInfoPanelUnitDetail.add(this.smashBuffStatusBar);
		this.smashBuffStatusBar.setHeight(GameUI.convertY(this.uiViewport, 0.01f));
		this.smashBuffStatusBar.addAnchor(new AnchorDefinition(FramePoint.BOTTOMLEFT,
				GameUI.convertX(this.uiViewport, 0.03f), GameUI.convertY(this.uiViewport, 0.003f)));
		this.smashBuffStatusBar.setWidth(GameUI.convertX(this.uiViewport, 0.035f));
		this.rootFrame.setDecoratedText(this.smashBuffStatusBar, "COLON_STATUS");
		UIFrame buffBarPreviousFrame = this.smashBuffStatusBar;
		for (int i = 0; i < BUFF_DISPLAY_MAX; i++) {
			final BuffBarIcon buffBarIcon = new BuffBarIcon("SmashBuffStatusBarIcon" + i,
					this.simpleInfoPanelUnitDetail);
			buffBarIcon.setWidth(GameUI.convertX(this.uiViewport, 0.015f));
			buffBarIcon.setHeight(GameUI.convertY(this.uiViewport, 0.015f));
			final TextureFrame barIconTex = new TextureFrame("SmashBuffStatusBarIcon" + i + "Texture", buffBarIcon,
					false, TextureFrame.DEFAULT_TEX_COORDS);
			barIconTex.setSetAllPoints(true);
			buffBarIcon.setIconFrame(barIconTex);
			this.simpleInfoPanelUnitDetail.add(buffBarIcon);
//			this.rootFrame.add(buffBarIcon);
			buffBarIcon.addSetPoint(new SetPoint(FramePoint.LEFT, buffBarPreviousFrame, FramePoint.RIGHT,
					GameUI.convertX(this.uiViewport, 0.001f), 0));
			buffBarPreviousFrame = buffBarIcon;
			this.buffBarIcons[i] = buffBarIcon;
		}

		// Create Simple Info Panel Building Detail
		this.simpleInfoPanelBuildingDetail = this.rootFrame.createSimpleFrame("SimpleInfoPanelBuildingDetail",
				this.smashSimpleInfoPanel, 0);
		this.simpleBuildingNameValue = (StringFrame) this.rootFrame.getFrameByName("SimpleBuildingNameValue", 0);
		this.simpleBuildingDescriptionValue = (StringFrame) this.rootFrame
				.getFrameByName("SimpleBuildingDescriptionValue", 0);
		this.simpleBuildingBuildingActionLabel = (StringFrame) this.rootFrame
				.getFrameByName("SimpleBuildingActionLabel", 0);
		this.simpleBuildingBuildTimeIndicator = (SimpleStatusBarFrame) this.rootFrame
				.getFrameByName("SimpleBuildTimeIndicator", 0);
		final TextureFrame simpleBuildingBuildTimeIndicatorBar = this.simpleBuildingBuildTimeIndicator.getBarFrame();
		simpleBuildingBuildTimeIndicatorBar.setTexture("SimpleBuildTimeIndicator", this.rootFrame);
		final TextureFrame simpleBuildingBuildTimeIndicatorBorder = this.simpleBuildingBuildTimeIndicator
				.getBorderFrame();
		simpleBuildingBuildTimeIndicatorBorder.setTexture("SimpleBuildTimeIndicatorBorder", this.rootFrame);
		this.simpleBuildingBuildTimeIndicator.setWidth(buildTimeIndicatorWidth);
		this.simpleBuildingBuildTimeIndicator.setHeight(buildTimeIndicatorHeight);
		this.simpleInfoPanelBuildingDetail.setVisible(false);
		this.simpleBuildQueueBackdrop = (TextureFrame) this.rootFrame.getFrameByName("SimpleBuildQueueBackdrop", 0);
		this.simpleBuildQueueBackdrop.setWidth(infoPanelUnitDetailWidth);
		this.simpleBuildQueueBackdrop.setHeight(infoPanelUnitDetailWidth * 0.5f);
		this.simpleBuildQueueBackdrop.setVisible(false);

		// Create Simple Info Panel Item Detail
		this.simpleInfoPanelItemDetail = this.rootFrame.createSimpleFrame("SimpleInfoPanelItemDetail",
				this.smashSimpleInfoPanel, 0);
		this.simpleItemNameValue = (StringFrame) this.rootFrame.getFrameByName("SimpleItemNameValue", 0);
		this.simpleItemDescriptionValue = (StringFrame) this.rootFrame.getFrameByName("SimpleItemDescriptionValue", 0);
		this.simpleInfoPanelItemDetail.setVisible(false);

		// Create Simple Info Panel Destructable Detail
		this.simpleInfoPanelDestructableDetail = this.rootFrame.createSimpleFrame("SimpleInfoPanelDestructableDetail",
				this.smashSimpleInfoPanel, 0);
		this.simpleDestructableNameValue = (StringFrame) this.rootFrame.getFrameByName("SimpleDestructableNameValue",
				0);
		this.simpleInfoPanelDestructableDetail.setVisible(false);

		this.queueIconFrames[0] = new QueueIcon("SmashBuildQueueIcon0", this.smashSimpleInfoPanel, this, 0);
		final TextureFrame queueIconFrameBackdrop0 = new TextureFrame("SmashBuildQueueIcon0Backdrop",
				this.queueIconFrames[0], false, new Vector4Definition(0, 1, 0, 1));
		queueIconFrameBackdrop0
				.addSetPoint(new SetPoint(FramePoint.CENTER, this.queueIconFrames[0], FramePoint.CENTER, 0, 0));
		this.queueIconFrames[0].set(queueIconFrameBackdrop0);
		this.queueIconFrames[0]
				.addSetPoint(new SetPoint(FramePoint.CENTER, this.smashSimpleInfoPanel, FramePoint.BOTTOMLEFT,
						(infoPanelUnitDetailWidth * (15 + 19f)) / 256, (infoPanelUnitDetailWidth * (66 + 19f)) / 256));
		this.frontQueueIconWidth = (infoPanelUnitDetailWidth * 38) / 256;
		this.queueIconFrames[0].setWidth(this.frontQueueIconWidth);
		this.queueIconFrames[0].setHeight(this.frontQueueIconWidth);
		queueIconFrameBackdrop0.setWidth(this.frontQueueIconWidth);
		queueIconFrameBackdrop0.setHeight(this.frontQueueIconWidth);
		this.smashSimpleInfoPanel.add(this.queueIconFrames[0]);

		for (int i = 1; i < this.queueIconFrames.length; i++) {
			this.queueIconFrames[i] = new QueueIcon("SmashBuildQueueIcon" + i, this.smashSimpleInfoPanel, this, i);
			final TextureFrame queueIconFrameBackdrop = new TextureFrame("SmashBuildQueueIcon" + i + "Backdrop",
					this.queueIconFrames[i], false, new Vector4Definition(0, 1, 0, 1));
			this.queueIconFrames[i].set(queueIconFrameBackdrop);
			queueIconFrameBackdrop
					.addSetPoint(new SetPoint(FramePoint.CENTER, this.queueIconFrames[i], FramePoint.CENTER, 0, 0));
			this.queueIconFrames[i].addSetPoint(new SetPoint(FramePoint.CENTER, this.smashSimpleInfoPanel,
					FramePoint.BOTTOMLEFT, (infoPanelUnitDetailWidth * (13 + 14.5f + (40 * (i - 1)))) / 256,
					(infoPanelUnitDetailWidth * (24 + 14.5f)) / 256));
			final float queueIconWidth = (infoPanelUnitDetailWidth * 29) / 256;
			this.queueIconFrames[i].setWidth(queueIconWidth);
			this.queueIconFrames[i].setHeight(queueIconWidth);
			queueIconFrameBackdrop.setWidth(queueIconWidth);
			queueIconFrameBackdrop.setHeight(queueIconWidth);
			this.smashSimpleInfoPanel.add(this.queueIconFrames[i]);
		}
		this.selectWorkerInsideFrame = new QueueIcon("SmashBuildQueueWorkerIcon", this.smashSimpleInfoPanel, this, 1);
		final TextureFrame selectWorkerInsideIconFrameBackdrop = new TextureFrame("SmashBuildQueueWorkerIconBackdrop",
				this.selectWorkerInsideFrame, false, new Vector4Definition(0, 1, 0, 1));
		this.selectWorkerInsideFrame.set(selectWorkerInsideIconFrameBackdrop);
		selectWorkerInsideIconFrameBackdrop
				.addSetPoint(new SetPoint(FramePoint.CENTER, this.selectWorkerInsideFrame, FramePoint.CENTER, 0, 0));
		this.selectWorkerInsideFrame
				.addSetPoint(new SetPoint(FramePoint.TOPLEFT, this.queueIconFrames[1], FramePoint.TOPLEFT, 0, 0));
		this.selectWorkerInsideFrame.setWidth(this.frontQueueIconWidth);
		this.selectWorkerInsideFrame.setHeight(this.frontQueueIconWidth);
		selectWorkerInsideIconFrameBackdrop.setWidth(this.frontQueueIconWidth);
		selectWorkerInsideIconFrameBackdrop.setHeight(this.frontQueueIconWidth);
		this.smashSimpleInfoPanel.add(this.selectWorkerInsideFrame);

		final int halfSelectionMaxSize = this.selectedUnitFrames.length / 3;
		for (int i = 0; i < this.selectedUnitFrames.length; i++) {
			{
				final FilterModeTextureFrame selectedSubgroupHighlightBackdrop = new FilterModeTextureFrame(
						"SmashMultiSelectUnitIconHighlightBackdrop", this.smashSimpleInfoPanel, true,
						new Vector4Definition(0, 1, 0, 1));
				selectedSubgroupHighlightBackdrop.setFilterMode(FilterMode.ADDITIVE);
				this.selectedUnitHighlightBackdrop[i] = selectedSubgroupHighlightBackdrop;
				selectedSubgroupHighlightBackdrop.setTexture("SelectedSubgroupHighlight", this.rootFrame);
				selectedSubgroupHighlightBackdrop.setWidth(this.frontQueueIconWidth * 1.37f);
				selectedSubgroupHighlightBackdrop.setHeight(this.frontQueueIconWidth * 1.75f);
				selectedSubgroupHighlightBackdrop.setColor(1.0f, 1.0f, 0.0f, 1.0f);
				this.smashSimpleInfoPanel.add(selectedSubgroupHighlightBackdrop);
				selectedSubgroupHighlightBackdrop
						.addSetPoint(new SetPoint(FramePoint.TOPLEFT, this.smashSimpleInfoPanel, FramePoint.TOPLEFT,
								((-this.frontQueueIconWidth * .37f) / 2) + (this.frontQueueIconWidth * .10f)
										+ (this.frontQueueIconWidth * 1.10f * (i % halfSelectionMaxSize)),
								(((this.frontQueueIconWidth * .37f) / 2) + (this.frontQueueIconWidth * -.75f))
										- (this.frontQueueIconWidth * 1.5f * (i / halfSelectionMaxSize))));
			}
			{
				final TextureFrame cargoBackdrop = new TextureFrame("SmashCargoBackdrop", this.smashSimpleInfoPanel,
						true, new Vector4Definition(0, 1, 0, 1));
				cargoBackdrop.setVisible(false);
				this.cargoBackdrop[i] = cargoBackdrop;
				cargoBackdrop.setTexture("CargoBackdrop", this.rootFrame);
				cargoBackdrop.setWidth(this.frontQueueIconWidth * 1.37f);
				cargoBackdrop.setHeight(this.frontQueueIconWidth * 1.75f);
				cargoBackdrop.setColor(1.0f, 1.0f, 0.0f, 1.0f);
				this.smashSimpleInfoPanel.add(cargoBackdrop);
				cargoBackdrop
						.addSetPoint(new SetPoint(FramePoint.TOPLEFT, this.smashSimpleInfoPanel, FramePoint.TOPLEFT,
								((-this.frontQueueIconWidth * .37f) / 2) + (this.frontQueueIconWidth * .0f)
										+ (this.frontQueueIconWidth * 1.20f * (i % halfSelectionMaxSize)),
								(((this.frontQueueIconWidth * .37f) / 2) + (this.frontQueueIconWidth * -.75f))
										- (this.frontQueueIconWidth * 1.5f * (i / halfSelectionMaxSize))));
			}
		}
		this.cargoClickListener = new MultiSelectionIconListener() {
			@Override
			public void multiSelectIconRelease(final int index) {

			}

			@Override
			public void multiSelectIconPress(final int index) {

			}

			@Override
			public void multiSelectIconClicked(final int index) {
				if (MeleeUI.this.selectedUnit != null) {
					final CUnit simulationUnit = MeleeUI.this.selectedUnit.getSimulationUnit();
					final CAbilityCargoHold cargoData = simulationUnit.getCargoData();
					if (cargoData != null) {
						final CUnit unitInside = cargoData.getUnit(index);
						if ((index >= 0) && (index < cargoData.getCargoCount())) {
							final BooleanAbilityActivationReceiver activationReceiver = BooleanAbilityActivationReceiver.INSTANCE;
							final CSimulation simulation = MeleeUI.this.war3MapViewer.simulation;
							cargoData.checkCanUse(simulation, simulationUnit, OrderIds.unload, activationReceiver);
							if (activationReceiver.isOk()) {
								final CWidgetAbilityTargetCheckReceiver targetCheckReceiver = CWidgetAbilityTargetCheckReceiver.INSTANCE;
								cargoData.checkCanTarget(simulation, simulationUnit, OrderIds.unload, unitInside,
										targetCheckReceiver.reset());
								if (targetCheckReceiver.getTarget() != null) {
									final CPlayer player = simulation.getPlayer(simulationUnit.getPlayerIndex());
									MeleeUI.this.unitOrderListener.issueTargetOrder(simulationUnit.getHandleId(),
											cargoData.getHandleId(), OrderIds.unload,
											targetCheckReceiver.getTarget().getHandleId(), false);
								}
							}
						}
					}
				}
			}
		};
		this.multiSelectClickListener = new MultiSelectionIconListener() {
			@Override
			public void multiSelectIconClicked(final int index) {
				if (index < MeleeUI.this.selectedUnits.size()) {
					final RenderUnit clickUnit = MeleeUI.this.selectedUnits.get(index);
					if (MeleeUI.this.activeCommand != null) {
						useActiveCommandOnUnit(isShiftDown(), clickUnit);
					}
					else if (clickUnit == MeleeUI.this.selectedUnit) {
						final List<RenderWidget> newSelection = Arrays.asList(MeleeUI.this.selectedUnit);
						selectWidgets(newSelection);
						MeleeUI.this.war3MapViewer.doSelectUnit(newSelection);
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
		for (int i = 0; i < this.selectedUnitFrames.length; i++) {
			this.selectedUnitFrames[i] = new MultiSelectionIcon("SmashMultiSelectUnitIcon", this.smashSimpleInfoPanel,
					this.multiSelectClickListener, i);
			final TextureFrame multiSelectUnitIconFrameBackdrop = new TextureFrame("SmashMultiSelectUnitIconBackdrop",
					this.selectedUnitFrames[i], false, new Vector4Definition(0, 1, 0, 1));

			final SimpleStatusBarFrame hpBarFrame = new SimpleStatusBarFrame(
					"SmashMultiSelectHpBar" + this.hpBarFrameIndex, this.smashSimpleInfoPanel, true, true, 3.0f);
			hpBarFrame.getBarFrame().setTexture("SimpleHpBarConsole", this.rootFrame);
			hpBarFrame.getBorderFrame().setTexture("Textures\\Black32.blp", this.rootFrame);
			hpBarFrame.setWidth(this.frontQueueIconWidth);
			hpBarFrame.setHeight(this.frontQueueIconWidth * MultiSelectionIcon.HP_BAR_HEIGHT_RATIO);
			hpBarFrame.addSetPoint(new SetPoint(FramePoint.TOP, multiSelectUnitIconFrameBackdrop, FramePoint.BOTTOM, 0,
					-this.frontQueueIconWidth * MultiSelectionIcon.HP_BAR_SPACING_RATIO));

			final SimpleStatusBarFrame manaBarFrame = new SimpleStatusBarFrame(
					"SmashMultiSelectManaBar" + this.hpBarFrameIndex, this.smashSimpleInfoPanel, true, true, 3.0f);
			manaBarFrame.getBarFrame().setTexture("SimpleManaBarConsole", this.rootFrame);
			manaBarFrame.getBorderFrame().setTexture("Textures\\Black32.blp", this.rootFrame);
			manaBarFrame.setWidth(this.frontQueueIconWidth);
			manaBarFrame.setHeight(this.frontQueueIconWidth * MultiSelectionIcon.HP_BAR_HEIGHT_RATIO);
			manaBarFrame.addSetPoint(new SetPoint(FramePoint.TOP, hpBarFrame, FramePoint.BOTTOM, 0,
					-this.frontQueueIconWidth * MultiSelectionIcon.HP_BAR_SPACING_RATIO));
			manaBarFrame.getBarFrame().setColor(0f, 0f, 1f, 1f);

			this.selectedUnitFrames[i].set(multiSelectUnitIconFrameBackdrop, hpBarFrame, manaBarFrame);
			multiSelectUnitIconFrameBackdrop
					.addSetPoint(new SetPoint(FramePoint.CENTER, this.selectedUnitFrames[i], FramePoint.CENTER, 0, 0));
			this.selectedUnitFrames[i]
					.addSetPoint(new SetPoint(FramePoint.TOPLEFT, this.smashSimpleInfoPanel, FramePoint.TOPLEFT,
							(this.frontQueueIconWidth * .10f)
									+ (this.frontQueueIconWidth * 1.10f * (i % halfSelectionMaxSize)),
							(this.frontQueueIconWidth * -.75f)
									- (this.frontQueueIconWidth * 1.5f * (i / halfSelectionMaxSize))));
			this.selectedUnitFrames[i].setWidth(this.frontQueueIconWidth);
			this.selectedUnitFrames[i].setHeight(this.frontQueueIconWidth);
			multiSelectUnitIconFrameBackdrop.setWidth(this.frontQueueIconWidth);
			multiSelectUnitIconFrameBackdrop.setHeight(this.frontQueueIconWidth);
			this.smashSimpleInfoPanel.add(this.selectedUnitFrames[i]);

			this.selectedUnitFrames[i].setVisible(false);
		}
		for (int i = 0; i < this.cargoUnitFrames.length; i++) {
			this.cargoUnitFrames[i] = new MultiSelectionIcon("SmashMultiSelectUnitIcon", this.smashSimpleInfoPanel,
					this.cargoClickListener, i);
			this.cargoUnitFrames[i].setVisible(false);
			final TextureFrame cargoUnitIconFrameBackdrop = new TextureFrame("SmashCargoUnitIconBackdrop",
					this.cargoUnitFrames[i], false, new Vector4Definition(0, 1, 0, 1));

			final SimpleStatusBarFrame hpBarFrame = new SimpleStatusBarFrame(
					"SmashMultiSelectHpBar" + this.hpBarFrameIndex, this.smashSimpleInfoPanel, true, true, 3.0f);
			hpBarFrame.getBarFrame().setTexture("SimpleHpBarConsole", this.rootFrame);
			hpBarFrame.getBorderFrame().setTexture("Textures\\Black32.blp", this.rootFrame);
			hpBarFrame.setWidth(this.frontQueueIconWidth);
			hpBarFrame.setHeight(this.frontQueueIconWidth * MultiSelectionIcon.HP_BAR_HEIGHT_RATIO);
			hpBarFrame.addSetPoint(new SetPoint(FramePoint.TOP, cargoUnitIconFrameBackdrop, FramePoint.BOTTOM, 0,
					-this.frontQueueIconWidth * MultiSelectionIcon.HP_BAR_SPACING_RATIO));

			final SimpleStatusBarFrame manaBarFrame = new SimpleStatusBarFrame(
					"SmashMultiSelectManaBar" + this.hpBarFrameIndex, this.smashSimpleInfoPanel, true, true, 3.0f);
			manaBarFrame.getBarFrame().setTexture("SimpleManaBarConsole", this.rootFrame);
			manaBarFrame.getBorderFrame().setTexture("Textures\\Black32.blp", this.rootFrame);
			manaBarFrame.setWidth(this.frontQueueIconWidth);
			manaBarFrame.setHeight(this.frontQueueIconWidth * MultiSelectionIcon.HP_BAR_HEIGHT_RATIO);
			manaBarFrame.addSetPoint(new SetPoint(FramePoint.TOP, hpBarFrame, FramePoint.BOTTOM, 0,
					-this.frontQueueIconWidth * MultiSelectionIcon.HP_BAR_SPACING_RATIO));
			manaBarFrame.getBarFrame().setColor(0f, 0f, 1f, 1f);

			this.cargoUnitFrames[i].set(cargoUnitIconFrameBackdrop, hpBarFrame, manaBarFrame);
			cargoUnitIconFrameBackdrop
					.addSetPoint(new SetPoint(FramePoint.CENTER, this.cargoUnitFrames[i], FramePoint.CENTER, 0, 0));
			this.cargoUnitFrames[i]
					.addSetPoint(new SetPoint(FramePoint.TOPLEFT, this.smashSimpleInfoPanel, FramePoint.TOPLEFT,
							(this.frontQueueIconWidth * .0f)
									+ (this.frontQueueIconWidth * 1.20f * (i % halfSelectionMaxSize)),
							(this.frontQueueIconWidth * -.75f)
									- (this.frontQueueIconWidth * 1.5f * (i / halfSelectionMaxSize))));
			this.cargoUnitFrames[i].setWidth(this.frontQueueIconWidth);
			this.cargoUnitFrames[i].setHeight(this.frontQueueIconWidth);
			cargoUnitIconFrameBackdrop.setWidth(this.frontQueueIconWidth);
			cargoUnitIconFrameBackdrop.setHeight(this.frontQueueIconWidth);
			this.smashSimpleInfoPanel.add(this.cargoUnitFrames[i]);

			this.cargoUnitFrames[i].setVisible(false);
		}

		this.smashAttack1IconWrapper = (SimpleFrame) this.rootFrame.createSimpleFrame("SmashSimpleInfoPanelIconDamage",
				this.simpleInfoPanelUnitDetail, 0);
		this.smashAttack1IconWrapper.addSetPoint(new SetPoint(FramePoint.TOPLEFT, this.simpleInfoPanelUnitDetail,
				FramePoint.TOPLEFT, 0, GameUI.convertY(this.uiViewport, -0.040f)));
		this.smashAttack1IconWrapper.setWidth(GameUI.convertX(this.uiViewport, 0.1f));
		this.smashAttack1IconWrapper.setHeight(GameUI.convertY(this.uiViewport, 0.030125f));
		this.attack1Icon = this.rootFrame.createSimpleFrame("SimpleInfoPanelIconDamage", this.smashAttack1IconWrapper,
				0);
		this.attack1IconBackdrop = (TextureFrame) this.rootFrame.getFrameByName("InfoPanelIconBackdrop", 0);
		this.attack1InfoPanelIconValue = (StringFrame) this.rootFrame.getFrameByName("InfoPanelIconValue", 0);
		this.attack1InfoPanelIconLevel = (StringFrame) this.rootFrame.getFrameByName("InfoPanelIconLevel", 0);

		this.smashAttack2IconWrapper = (SimpleFrame) this.rootFrame.createSimpleFrame("SmashSimpleInfoPanelIconDamage",
				this.simpleInfoPanelUnitDetail, 0);
		this.smashAttack2IconWrapper
				.addSetPoint(new SetPoint(FramePoint.TOPLEFT, this.simpleInfoPanelUnitDetail, FramePoint.TOPLEFT,
						GameUI.convertX(this.uiViewport, 0.1f), GameUI.convertY(this.uiViewport, -0.03925f)));
		this.smashAttack2IconWrapper.setWidth(GameUI.convertX(this.uiViewport, 0.1f));
		this.smashAttack2IconWrapper.setHeight(GameUI.convertY(this.uiViewport, 0.030125f));
		this.attack2Icon = this.rootFrame.createSimpleFrame("SimpleInfoPanelIconDamage", this.smashAttack2IconWrapper,
				1);
		this.attack2IconBackdrop = (TextureFrame) this.rootFrame.getFrameByName("InfoPanelIconBackdrop", 1);
		this.attack2InfoPanelIconValue = (StringFrame) this.rootFrame.getFrameByName("InfoPanelIconValue", 1);
		this.attack2InfoPanelIconLevel = (StringFrame) this.rootFrame.getFrameByName("InfoPanelIconLevel", 1);

		this.smashArmorIconWrapper = (SimpleFrame) this.rootFrame.createSimpleFrame("SmashSimpleInfoPanelIconArmor",
				this.simpleInfoPanelUnitDetail, 0);
		this.smashArmorIconWrapper.addSetPoint(new SetPoint(FramePoint.TOPLEFT, this.simpleInfoPanelUnitDetail,
				FramePoint.TOPLEFT, GameUI.convertX(this.uiViewport, 0f), GameUI.convertY(this.uiViewport, -0.0705f)));
		this.smashArmorIconWrapper.setWidth(GameUI.convertX(this.uiViewport, 0.1f));
		this.smashArmorIconWrapper.setHeight(GameUI.convertY(this.uiViewport, 0.030125f));
		this.armorIcon = this.rootFrame.createSimpleFrame("SimpleInfoPanelIconArmor", this.smashArmorIconWrapper, 0);
		this.armorIconBackdrop = (TextureFrame) this.rootFrame.getFrameByName("InfoPanelIconBackdrop", 0);
		this.armorInfoPanelIconValue = (StringFrame) this.rootFrame.getFrameByName("InfoPanelIconValue", 0);
		this.armorInfoPanelIconLevel = (StringFrame) this.rootFrame.getFrameByName("InfoPanelIconLevel", 0);

		this.smashHeroInfoPanelWrapper = (SimpleFrame) this.rootFrame.createSimpleFrame("SmashSimpleInfoPanelIconHero",
				this.simpleInfoPanelUnitDetail, 0);
		this.smashHeroInfoPanelWrapper.addSetPoint(new SetPoint(FramePoint.TOPLEFT, this.simpleInfoPanelUnitDetail,
				FramePoint.TOPLEFT, GameUI.convertX(this.uiViewport, 0.1f), GameUI.convertY(this.uiViewport, -0.037f)));
		this.smashHeroInfoPanelWrapper.setWidth(GameUI.convertX(this.uiViewport, 0.1f));
		this.smashHeroInfoPanelWrapper.setHeight(GameUI.convertY(this.uiViewport, 0.0625f));
		this.heroInfoPanel = this.rootFrame.createSimpleFrame("SimpleInfoPanelIconHero", this.smashHeroInfoPanelWrapper,
				0);
		this.primaryAttributeIcon = (TextureFrame) this.rootFrame.getFrameByName("InfoPanelIconHeroIcon", 0);
		this.strengthValue = (StringFrame) this.rootFrame.getFrameByName("InfoPanelIconHeroStrengthValue", 0);
		this.agilityValue = (StringFrame) this.rootFrame.getFrameByName("InfoPanelIconHeroAgilityValue", 0);
		this.intelligenceValue = (StringFrame) this.rootFrame.getFrameByName("InfoPanelIconHeroIntellectValue", 0);

		this.inventoryBarFrame = (SimpleFrame) this.rootFrame.createSimpleFrame("SmashSimpleInventoryBar",
				this.smashSimpleInfoPanel, 0);
		this.inventoryBarFrame.setWidth(GameUI.convertX(this.uiViewport, 0.079f));
		this.inventoryBarFrame.setHeight(GameUI.convertY(this.uiViewport, 0.115f));
		this.inventoryBarFrame.addSetPoint(new SetPoint(FramePoint.BOTTOMRIGHT, this.consoleUI, FramePoint.BOTTOMLEFT,
				GameUI.convertX(this.uiViewport, 0.591f), GameUI.convertY(this.uiViewport, 0.0f)));

		if (GameUI.DEBUG) {
			final FilterModeTextureFrame placeholderPreview = new FilterModeTextureFrame(null, this.inventoryBarFrame,
					false, null);
			placeholderPreview.setFilterMode(FilterMode.ADDALPHA);
			placeholderPreview.setTexture("ReplaceableTextures\\TeamColor\\TeamColor06.blp", this.rootFrame);
			placeholderPreview.setSetAllPoints(true);
			this.inventoryBarFrame.add(placeholderPreview);
		}

		int commandButtonIndex = 0;
		final BitmapFont inventoryNumberOverlayFont = this.rootFrame.generateFont(DEFAULT_INVENTORY_ICON_WIDTH * 0.25f);
		for (int j = 0; j < INVENTORY_HEIGHT; j++) {
			for (int i = 0; i < INVENTORY_WIDTH; i++) {
				final CommandCardIcon commandCardIcon = new CommandCardIcon(
						"SmashInventoryButton_" + commandButtonIndex, this.inventoryBarFrame,
						this.itemCommandCardCommandListener);
				this.inventoryBarFrame.add(commandCardIcon);
				final TextureFrame iconFrame = new TextureFrame("SmashInventoryButton_" + commandButtonIndex + "_Icon",
						this.rootFrame, false, null);
				final TextureFrame numberOverlayFrame = new TextureFrame(
						"SmashCommandButton_" + commandButtonIndex + "_NumberOverlay", this.rootFrame, true, null);
				final SpriteFrame cooldownFrame = (SpriteFrame) this.rootFrame.createFrameByType("SPRITE",
						"SmashInventoryButton_" + commandButtonIndex + "_Cooldown", this.rootFrame, "", 0);
				commandCardIcon.addSetPoint(new SetPoint(FramePoint.TOPLEFT, this.inventoryBarFrame, FramePoint.TOPLEFT,
						GameUI.convertX(this.uiViewport, 0.0037f + (0.04f * i)),
						GameUI.convertY(this.uiViewport, -0.0021f - (0.03815f * j))));
				commandCardIcon.setWidth(GameUI.convertX(this.uiViewport, DEFAULT_INVENTORY_ICON_WIDTH));
				commandCardIcon.setHeight(GameUI.convertY(this.uiViewport, DEFAULT_INVENTORY_ICON_WIDTH));
				iconFrame.addSetPoint(new SetPoint(FramePoint.CENTER, commandCardIcon, FramePoint.CENTER, 0, 0));
				iconFrame.setWidth(GameUI.convertX(this.uiViewport, DEFAULT_INVENTORY_ICON_WIDTH));
				iconFrame.setHeight(GameUI.convertY(this.uiViewport, DEFAULT_INVENTORY_ICON_WIDTH));
				iconFrame.setTexture(ImageUtils.DEFAULT_ICON_PATH, this.rootFrame);
				cooldownFrame.addSetPoint(new SetPoint(FramePoint.CENTER, commandCardIcon, FramePoint.CENTER, 0, 0));
				this.rootFrame.setSpriteFrameModel(cooldownFrame, this.rootFrame.getSkinField("CommandButtonCooldown"));
				cooldownFrame.setWidth(GameUI.convertX(this.uiViewport, DEFAULT_INVENTORY_ICON_WIDTH));
				cooldownFrame.setHeight(GameUI.convertY(this.uiViewport, DEFAULT_INVENTORY_ICON_WIDTH));
				cooldownFrame.setModelScale(DEFAULT_INVENTORY_ICON_WIDTH / DEFAULT_COMMAND_CARD_ICON_WIDTH);

				numberOverlayFrame.addSetPoint(
						new SetPoint(FramePoint.BOTTOMRIGHT, commandCardIcon, FramePoint.BOTTOMRIGHT, 0, 0));
				numberOverlayFrame.setWidth(GameUI.convertX(this.uiViewport, DEFAULT_INVENTORY_ICON_WIDTH) * 0.4f);
				numberOverlayFrame.setHeight(GameUI.convertY(this.uiViewport, DEFAULT_INVENTORY_ICON_WIDTH) * 0.4f);
				numberOverlayFrame.setTexture("CommandButtonNumberOverlay", this.rootFrame);
				final SingleStringFrame numberOverlayStringFrame = new SingleStringFrame(
						"SmashCommandButton_NumberOverlayText", numberOverlayFrame, Color.WHITE, TextJustify.CENTER,
						TextJustify.BOTTOM, inventoryNumberOverlayFont);
				numberOverlayStringFrame.setSetAllPoints(true);

				commandCardIcon.set(iconFrame, null, cooldownFrame, null, numberOverlayFrame, numberOverlayStringFrame);
				this.inventoryIcons[j][i] = commandCardIcon;
				commandCardIcon.clear();
				commandButtonIndex++;
			}
		}
		this.inventoryTitleFrame = this.rootFrame.createStringFrame("SmashInventoryText", this.inventoryBarFrame,
				new Color(0xFCDE12FF), TextJustify.CENTER, TextJustify.MIDDLE, 0.0109f);
		this.rootFrame.setText(this.inventoryTitleFrame, this.rootFrame.getTemplates().getDecoratedString("INVENTORY"));
		this.inventoryTitleFrame
				.addSetPoint(new SetPoint(FramePoint.TOPLEFT, this.inventoryBarFrame, FramePoint.TOPLEFT,
						GameUI.convertX(this.uiViewport, 0.004f), GameUI.convertY(this.uiViewport, 0.0165625f)));
		this.inventoryTitleFrame.setWidth(
				GameUI.convertX(this.uiViewport, WarsmashConstants.USE_NINE_ITEM_INVENTORY ? 0.101f : 0.071f));
		this.inventoryTitleFrame.setHeight(GameUI.convertX(this.uiViewport, 0.01125f));
		this.inventoryTitleFrame.setFontShadowColor(new Color(0f, 0f, 0f, 0.9f));
		this.inventoryTitleFrame.setFontShadowOffsetX(GameUI.convertX(this.uiViewport, 0.001f));
		this.inventoryTitleFrame.setFontShadowOffsetY(GameUI.convertY(this.uiViewport, -0.001f));
		try {
			this.consoleInventoryNoCapacityTexture = ImageUtils.getAnyExtensionTexture(this.dataSource,
					this.rootFrame.getSkinField("ConsoleInventoryNoCapacity"));
		}
		catch (final Exception exc) {
			exc.printStackTrace();
		}

		this.inventoryCover = this.rootFrame.createSimpleFrame("SmashConsoleInventoryCover", this.rootFrame, 0);

		final Element fontHeights = this.war3MapViewer.miscData.get("FontHeights");
		final float worldFrameMessageFontHeight = fontHeights.getFieldFloatValue("WorldFrameMessage");
		this.errorMessageFrame = this.rootFrame.createStringFrame("SmashErrorMessageFrame", this.rootFrame,
				new Color(0xFFCC00FF), TextJustify.LEFT, TextJustify.MIDDLE, worldFrameMessageFontHeight);
		this.errorMessageFrame.addAnchor(new AnchorDefinition(FramePoint.BOTTOMLEFT,
				GameUI.convertX(this.uiViewport, 0.212f), GameUI.convertY(this.uiViewport, 0.182f)));
		this.errorMessageFrame.setWidth(GameUI.convertX(this.uiViewport, 0.35f));
		this.errorMessageFrame.setHeight(GameUI.convertY(this.uiViewport, worldFrameMessageFontHeight));

		this.errorMessageFrame.setFontShadowColor(new Color(0f, 0f, 0f, 0.9f));
		this.errorMessageFrame.setFontShadowOffsetX(GameUI.convertX(this.uiViewport, 0.001f));
		this.errorMessageFrame.setFontShadowOffsetY(GameUI.convertY(this.uiViewport, -0.001f));
		this.errorMessageFrame.setVisible(false);

		this.worldFrameUnitMessageFontHeight = fontHeights.getFieldFloatValue("WorldFrameUnitMessage");

		commandButtonIndex = 0;
		final BitmapFont commandCardNumberOverlayFont = this.rootFrame
				.generateFont(DEFAULT_COMMAND_CARD_ICON_WIDTH * 0.25f);
		this.smashCommandButtons = (SimpleFrame) this.rootFrame.createFrameByType("SIMPLEFRAME", "SmashCommandButtons",
				this.rootFrame, "", 0);
		for (int j = 0; j < COMMAND_CARD_HEIGHT; j++) {
			for (int i = 0; i < COMMAND_CARD_WIDTH; i++) {
				final CommandCardIcon commandCardIcon = new CommandCardIcon("SmashCommandButton_" + commandButtonIndex,
						this.rootFrame, this);
				this.smashCommandButtons.add(commandCardIcon);
				final TextureFrame iconFrame = new TextureFrame("SmashCommandButton_" + commandButtonIndex + "_Icon",
						this.rootFrame, false, null);
				final FilterModeTextureFrame activeHighlightFrame = new FilterModeTextureFrame(
						"SmashCommandButton_" + commandButtonIndex + "_ActiveHighlight", this.rootFrame, true, null);
				activeHighlightFrame.setFilterMode(FilterMode.ADDALPHA);
				final TextureFrame numberOverlayFrame = new TextureFrame(
						"SmashCommandButton_" + commandButtonIndex + "_NumberOverlay", this.rootFrame, true, null);
				final SpriteFrame cooldownFrame = (SpriteFrame) this.rootFrame.createFrameByType("SPRITE",
						"SmashCommandButton_" + commandButtonIndex + "_Cooldown", this.rootFrame, "", 0);
				final SpriteFrame autocastFrame = (SpriteFrame) this.rootFrame.createFrameByType("SPRITE",
						"SmashCommandButton_" + commandButtonIndex + "_Autocast", this.rootFrame, "", 0);
				commandCardIcon.addAnchor(new AnchorDefinition(FramePoint.BOTTOMLEFT,
						GameUI.convertX(this.uiViewport,
								0.6175f + (WarsmashConstants.USE_NINE_ITEM_INVENTORY ? 0.0405f : 0.0f) + (0.0434f * i)),
						GameUI.convertY(this.uiViewport, 0.095f - (0.044f * j))));
				commandCardIcon.setWidth(GameUI.convertX(this.uiViewport, DEFAULT_COMMAND_CARD_ICON_WIDTH));
				commandCardIcon.setHeight(GameUI.convertY(this.uiViewport, DEFAULT_COMMAND_CARD_ICON_WIDTH));
				iconFrame.addSetPoint(new SetPoint(FramePoint.CENTER, commandCardIcon, FramePoint.CENTER, 0, 0));
				iconFrame.setWidth(GameUI.convertX(this.uiViewport, DEFAULT_COMMAND_CARD_ICON_WIDTH));
				iconFrame.setHeight(GameUI.convertY(this.uiViewport, DEFAULT_COMMAND_CARD_ICON_WIDTH));
				iconFrame.setTexture(ImageUtils.DEFAULT_ICON_PATH, this.rootFrame);
				activeHighlightFrame
						.addSetPoint(new SetPoint(FramePoint.CENTER, commandCardIcon, FramePoint.CENTER, 0, 0));
				activeHighlightFrame.setWidth(GameUI.convertX(this.uiViewport, DEFAULT_COMMAND_CARD_ICON_WIDTH));
				activeHighlightFrame.setHeight(GameUI.convertY(this.uiViewport, DEFAULT_COMMAND_CARD_ICON_WIDTH));
				activeHighlightFrame.setTexture("CommandButtonActiveHighlight", this.rootFrame);

				numberOverlayFrame.addSetPoint(
						new SetPoint(FramePoint.BOTTOMRIGHT, commandCardIcon, FramePoint.BOTTOMRIGHT, 0, 0));
				numberOverlayFrame.setWidth(GameUI.convertX(this.uiViewport, DEFAULT_COMMAND_CARD_ICON_WIDTH) * 0.4f);
				numberOverlayFrame.setHeight(GameUI.convertY(this.uiViewport, DEFAULT_COMMAND_CARD_ICON_WIDTH) * 0.4f);
				numberOverlayFrame.setTexture("CommandButtonNumberOverlay", this.rootFrame);
				final SingleStringFrame numberOverlayStringFrame = new SingleStringFrame(
						"SmashCommandButton_NumberOverlayText", numberOverlayFrame, Color.WHITE, TextJustify.CENTER,
						TextJustify.BOTTOM, commandCardNumberOverlayFont);
//				numberOverlayStringFrame.addAnchor(new AnchorDefinition(FramePoint.CENTER, 0, 0));
				numberOverlayStringFrame.setSetAllPoints(true);
				cooldownFrame.addSetPoint(new SetPoint(FramePoint.CENTER, commandCardIcon, FramePoint.CENTER, 0, 0));
				this.rootFrame.setSpriteFrameModel(cooldownFrame, this.rootFrame.getSkinField("CommandButtonCooldown"));
				cooldownFrame.setWidth(GameUI.convertX(this.uiViewport, DEFAULT_COMMAND_CARD_ICON_WIDTH));
				cooldownFrame.setHeight(GameUI.convertY(this.uiViewport, DEFAULT_COMMAND_CARD_ICON_WIDTH));
				autocastFrame.addSetPoint(new SetPoint(FramePoint.CENTER, commandCardIcon, FramePoint.CENTER, 0, 0));
				this.rootFrame.setSpriteFrameModel(autocastFrame, this.rootFrame.getSkinField("CommandButtonAutocast"));
				autocastFrame.setWidth(GameUI.convertX(this.uiViewport, DEFAULT_COMMAND_CARD_ICON_WIDTH));
				autocastFrame.setHeight(GameUI.convertY(this.uiViewport, DEFAULT_COMMAND_CARD_ICON_WIDTH));
				commandCardIcon.set(iconFrame, activeHighlightFrame, cooldownFrame, autocastFrame, numberOverlayFrame,
						numberOverlayStringFrame);
				this.commandCard[j][i] = commandCardIcon;
				commandCardIcon.clear();
				commandButtonIndex++;
			}
		}

		this.tooltipFrame = this.rootFrame.createFrame("SmashToolTip", this.rootFrame, 0, 0);
		this.tooltipFrame.addAnchor(new AnchorDefinition(FramePoint.BOTTOMRIGHT, GameUI.convertX(this.uiViewport, 0.f),
				GameUI.convertY(this.uiViewport, 0.176f)));
		this.tooltipFrame.setWidth(GameUI.convertX(this.uiViewport, 0.280f));
		this.tooltipText = (StringFrame) this.rootFrame.getFrameByName("SmashToolTipText", 0);
		this.tooltipText.setWidth(GameUI.convertX(this.uiViewport, 0.274f));
		this.tooltipText.addAnchor(new AnchorDefinition(FramePoint.TOPLEFT, GameUI.convertX(this.uiViewport, 0.003f),
				GameUI.convertY(this.uiViewport, -0.003f)));
		this.tooltipFrame.setVisible(false);

		this.hovertipFrame = this.rootFrame.createFrame("SmashHoverTip", this.rootFrame, 0, 0);
		this.hovertipText = (StringFrame) this.rootFrame.getFrameByName("SmashHoverTipText", 0);
		this.hovertipText.setWidth(GameUI.convertX(this.uiViewport, 0.274f));
		this.hovertipText.addAnchor(new AnchorDefinition(FramePoint.TOPLEFT, GameUI.convertX(this.uiViewport, 0.006f),
				GameUI.convertY(this.uiViewport, -0.006f)));
		this.hovertipFrame.setVisible(false);

		this.tooltipUberTipText = (StringFrame) this.rootFrame.getFrameByName("SmashUberTipText", 0);
		this.tooltipUberTipText.setWidth(GameUI.convertX(this.uiViewport, 0.274f));
		this.uberTipNoResourcesSetPoint = new SetPoint(FramePoint.TOPLEFT, this.tooltipText, FramePoint.BOTTOMLEFT, 0,
				GameUI.convertY(this.uiViewport, -0.004f));
		this.uberTipWithResourcesSetPoint = new SetPoint(FramePoint.TOPLEFT, this.tooltipText, FramePoint.BOTTOMLEFT, 0,
				GameUI.convertY(this.uiViewport, -0.014f));
		this.tooltipUberTipText.addSetPoint(this.uberTipNoResourcesSetPoint);
		this.tooltipResourceFrames = new UIFrame[ResourceType.VALUES.length];
		this.tooltipResourceIconFrames = new TextureFrame[ResourceType.VALUES.length];
		this.tooltipResourceTextFrames = new StringFrame[ResourceType.VALUES.length];
		for (int i = 0; i < this.tooltipResourceFrames.length; i++) {
			this.tooltipResourceFrames[i] = this.rootFrame.createFrame("SmashToolTipIconResource", this.tooltipFrame, 0,
					i);
			this.tooltipResourceIconFrames[i] = (TextureFrame) this.rootFrame
					.getFrameByName("SmashToolTipIconResourceBackdrop", i);
			this.tooltipResourceTextFrames[i] = (StringFrame) this.rootFrame
					.getFrameByName("SmashToolTipIconResourceLabel", i);
			this.tooltipResourceFrames[i].addSetPoint(new SetPoint(FramePoint.TOPLEFT, this.tooltipText,
					FramePoint.BOTTOMLEFT, GameUI.convertX(this.uiViewport, 0.004f + (0.032f * i)),
					GameUI.convertY(this.uiViewport, -0.001f)));
			// have we really no better API than the below???
			((AbstractUIFrame) this.tooltipFrame).add(this.tooltipResourceFrames[i]);
			this.rootFrame.remove(this.tooltipResourceFrames[i]);
		}

		this.cinematicPanel = this.rootFrame.createFrame("CinematicPanel", this.rootFrame, 0, 0);
		this.cinematicPanel.setVisible(false);
		this.cinematicSpeakerText = (StringFrame) this.rootFrame.getFrameByName("CinematicSpeakerText", 0);
		this.rootFrame.setText(this.cinematicSpeakerText, "");
		this.cinematicDialogueText = (StringFrame) this.rootFrame.getFrameByName("CinematicDialogueText", 0);
		this.rootFrame.setText(this.cinematicDialogueText, "");
		this.cinematicScenePanel = this.rootFrame.getFrameByName("CinematicScenePanel", 0);
		final SpriteFrame2 cinematicPortraitSprite = (SpriteFrame2) this.rootFrame.getFrameByName("CinematicPortrait",
				0);
		this.cinematicPortrait = new CinematicPortrait(cinematicPortraitSprite, this.cinematicScenePanel);
		positionPortrait();

		// this.tooltipFrame = this.rootFrame.createFrameByType("BACKDROP",
		// "SmashToolTipBackdrop", this.rootFrame, "", 0);

		this.cursorFrame = (SpriteFrame) this.rootFrame.createFrameByType("SPRITE", "SmashCursorFrame", this.rootFrame,
				"", 0);
		this.rootFrame.setSpriteFrameModel(this.cursorFrame, this.rootFrame.getSkinField("Cursor"));
		this.cursorFrame.setSequence("Normal");
		this.cursorFrame.setZDepth(1.0f);
		if (WarsmashConstants.CATCH_CURSOR) {
			Gdx.input.setCursorCatched(true);
		}
		this.includeFrames = new String[] { "EscMenuBackdrop", "ScriptDialog", "SmashHoverTip", "SmashHpBar" };
		this.ignoreFrames = new String[] { "SmashHoverTip", "SmashHpBar", "SmashGameMessageFrame" };

		this.meleeUIMinimap = createMinimap(this.war3MapViewer);

		this.meleeUIAbilityActivationReceiver = new MeleeUIAbilityActivationReceiver(
				this.war3MapViewer.getLocalPlayerIndex(), this::getUiSoundForError);

		final MdxModel rallyModel = this.war3MapViewer.loadModelMdx(this.rootFrame.getSkinField("RallyIndicatorDst"));
		this.rallyPointInstance = (MdxComplexInstance) rallyModel.addInstance();
		this.rallyPointInstance.rotate(RenderUnit.tempQuat.setFromAxis(RenderMathUtils.VEC3_UNIT_Z,
				this.war3MapViewer.simulation.getGameplayConstants().getBuildingAngle()));
		this.rallyPointInstance.setSequenceLoopMode(SequenceLoopMode.ALWAYS_LOOP);
		SequenceUtils.randomStandSequence(this.rallyPointInstance);
		this.rallyPointInstance.hide();
		this.waypointModel = this.war3MapViewer.loadModelMdx(this.rootFrame.getSkinField("WaypointIndicator"));

		this.rootFrame.positionBounds(this.rootFrame, this.uiViewport);

		selectUnit(null);

	}

	private void updateEscMenuCurrentPanel(final UIFrame escMenuBackdrop, final UIFrame escMenuMainPanel,
			final UIFrame escMenuInnerMainPanel) {
		this.smashEscMenu.setWidth(escMenuInnerMainPanel.getAssignedWidth());
		this.smashEscMenu.setHeight(escMenuInnerMainPanel.getAssignedHeight());
		escMenuBackdrop.setWidth(escMenuInnerMainPanel.getAssignedWidth());
		escMenuBackdrop.setHeight(escMenuInnerMainPanel.getAssignedHeight());
		this.smashEscMenu.positionBounds(this.rootFrame, this.uiViewport);

	}

	@Override
	public void onClick(final int abilityHandleId, final int orderId, final boolean rightClick) {
		if (this.selectedUnit == null) {
			return;
		}
		if (orderId == 0) {
			return;
		}
		// TODO not O(N)
		CAbilityView abilityToUse = null;
		for (final CAbility ability : this.selectedUnit.getSimulationUnit().getAbilities()) {
			if (ability.getHandleId() == abilityHandleId) {
				abilityToUse = ability;
				break;
			}
		}
		if (abilityToUse != null) {
			abilityToUse.checkCanUse(this.war3MapViewer.simulation, this.selectedUnit.getSimulationUnit(), orderId,
					this.meleeUIAbilityActivationReceiver.reset(this, this.war3MapViewer.worldScene.audioContext,
							this.selectedUnit));
			if (this.meleeUIAbilityActivationReceiver.isUseOk()) {
				final BooleanAbilityTargetCheckReceiver<Void> noTargetReceiver = BooleanAbilityTargetCheckReceiver
						.<Void>getInstance().reset();
				abilityToUse.checkCanTargetNoTarget(this.war3MapViewer.simulation,
						this.selectedUnit.getSimulationUnit(), orderId, noTargetReceiver);
				if (noTargetReceiver.isTargetable()) {
					final boolean shiftDown = isShiftDown();
					this.unitOrderListener.issueImmediateOrder(this.selectedUnit.getSimulationUnit().getHandleId(),
							abilityHandleId, orderId, shiftDown);
					if (abilityToUse instanceof CAbilityHero) {
						if ((((CAbilityHero) abilityToUse).getSkillPoints() <= 1) && (orderId != OrderIds.skillmenu)) {
							// using up the last skill point, so close the menu
							// TODO this is kind of a stupid hack and should probably be improved later
							openMenu(0);
						}
					}
					if (this.selectedUnits.size() > 1) {
						for (final RenderUnit otherSelectedUnit : this.selectedUnits) {
							if (otherSelectedUnit != this.activeCommandUnit) {
								abilityToUse = null;
								for (final CAbility ability : otherSelectedUnit.getSimulationUnit().getAbilities()) {
									final BooleanAbilityTargetCheckReceiver<Void> receiver = BooleanAbilityTargetCheckReceiver
											.<Void>getInstance().reset();
									ability.checkCanTargetNoTarget(this.war3MapViewer.simulation,
											otherSelectedUnit.getSimulationUnit(), this.activeCommandOrderId, receiver);
									if (receiver.isTargetable()) {
										abilityToUse = ability;
									}
								}
								if (abilityToUse != null) {
									this.unitOrderListener.issueImmediateOrder(
											otherSelectedUnit.getSimulationUnit().getHandleId(),
											abilityToUse.getHandleId(), this.activeCommandOrderId, shiftDown);
								}
							}
						}
					}
				}
				else {
					this.activeCommand = abilityToUse;
					this.activeCommandOrderId = orderId;
					this.activeCommandUnit = this.selectedUnit;
					clearAndRepopulateCommandCard();
				}
			}
		}
		else {
			this.unitOrderListener.issueImmediateOrder(this.selectedUnit.getSimulationUnit().getHandleId(),
					abilityHandleId, orderId, isShiftDown());
			if (this.selectedUnits.size() > 1) {
				for (final RenderUnit otherSelectedUnit : this.selectedUnits) {
					if (otherSelectedUnit != this.activeCommandUnit) {
						this.unitOrderListener.issueImmediateOrder(otherSelectedUnit.getSimulationUnit().getHandleId(),
								abilityHandleId, orderId, isShiftDown());
					}
				}
			}
		}
		if (rightClick) {
			this.war3MapViewer.getUiSounds().getSound("AutoCastButtonClick").play(this.uiScene.audioContext, 0, 0, 0);
		}
	}

	@Override
	public void openMenu(final int orderId) {
		if (orderId == 0) {
			this.subMenuOrderIdStack.clear();
			this.activeCommandUnit = null;
			this.activeCommand = null;
			this.activeCommandOrderId = -1;
		}
		else {
			this.subMenuOrderIdStack.add(orderId);
		}
		clearAndRepopulateCommandCard();
	}

	@Override
	public void showInterfaceError(final int playerIndex, final String externStringKey) {
		if (playerIndex == this.war3MapViewer.getLocalPlayerIndex()) {
			showLocalCommandErrorString(playerIndex, externStringKey);
			getUiSoundForError(externStringKey).play(this.uiScene.audioContext, 0, 0, 0);
		}
	}

	private UnitSound getUiSoundForError(final String externStringKey) {
		String sound = "InterfaceError";
		final String soundKey = externStringKey + "Sound";
		if (this.rootFrame.hasSkinField(soundKey)) {
			sound = this.rootFrame.getSkinField(soundKey);
		}
		return this.war3MapViewer.getUiSounds().getSound(sound);
	}

	@Override
	public void showCommandErrorWithoutSound(final int playerIndex, final String message) {
		if (playerIndex == this.war3MapViewer.getLocalPlayerIndex()) {
			showLocalCommandErrorString(playerIndex, message);
		}
	}

	private void showLocalCommandErrorString(final int playerIndex, final String message) {
		String errorString = this.rootFrame.getErrorString(message);
		if (errorString.isEmpty() && !message.isEmpty()) {
			errorString = message; // this may show some NOTEXTERN engine garbage that we should fix later
		}
		innerShowLocalCommandErrorString(playerIndex, errorString);
	}

	private void innerShowLocalCommandErrorString(final int playerIndex, final String message) {
		this.rootFrame.setText(this.errorMessageFrame, message);
		this.errorMessageFrame.setVisible(true);
		final long millis = TimeUtils.millis();
		this.lastErrorMessageExpireTime = millis + WORLD_FRAME_MESSAGE_EXPIRE_MILLIS;
		this.lastErrorMessageFadeTime = millis + WORLD_FRAME_MESSAGE_FADEOUT_MILLIS;
		this.errorMessageFrame.setAlpha(1.0f);
	}

	private static final class GameMessage {
		private final StringFrame stringFrame;
		private final long lastGameMessageExpireTime;
		private final long lastGameMessageFadeTime;

		public GameMessage(final StringFrame stringFrame, final long lastGameMessageExpireTime,
				final long lastGameMessageFadeTime) {
			this.stringFrame = stringFrame;
			this.lastGameMessageExpireTime = lastGameMessageExpireTime;
			this.lastGameMessageFadeTime = lastGameMessageFadeTime;
		}
	}

	public void showGameMessage(final String message, final float expireTime) {
		final StringFrame gameMessagesFrame = this.rootFrame.createStringFrame("SmashGameMessageFrame", this.rootFrame,
				Color.WHITE, TextJustify.LEFT, TextJustify.MIDDLE, this.worldFrameUnitMessageFontHeight);
		gameMessagesFrame.setWidth(GameUI.convertX(this.uiViewport, 0.35f));
//		gameMessagesFrame.setHeight(GameUI.convertY(this.uiViewport, this.worldFrameUnitMessageFontHeight));

		gameMessagesFrame.setFontShadowColor(new Color(0f, 0f, 0f, 0.9f));
		gameMessagesFrame.setFontShadowOffsetX(GameUI.convertX(this.uiViewport, 0.001f));
		gameMessagesFrame.setFontShadowOffsetY(GameUI.convertY(this.uiViewport, -0.001f));

		this.rootFrame.setText(gameMessagesFrame, message);
		gameMessagesFrame.setVisible(true);

		final long millis = TimeUtils.millis();

		final long lastGameMessageExpireTime = millis + (long) (expireTime * 1000);
		final long lastGameMessageFadeTime = millis + (long) (expireTime * 900);
		gameMessagesFrame.addAnchor(new AnchorDefinition(FramePoint.LEFT, 0, 0));
		if (!this.gameMessages.isEmpty()) {
			final GameMessage lastFrame = this.gameMessages.getLast();
			lastFrame.stringFrame
					.addSetPoint(new SetPoint(FramePoint.BOTTOMLEFT, gameMessagesFrame, FramePoint.TOPLEFT, 0, 0));
		}
		this.gameMessages
				.addLast(new GameMessage(gameMessagesFrame, lastGameMessageExpireTime, lastGameMessageFadeTime));
		final Iterator<GameMessage> descendingIterator = this.gameMessages.descendingIterator();
		while (descendingIterator.hasNext()) {
			final GameMessage gameMessage = descendingIterator.next();
			gameMessage.stringFrame.positionBounds(this.rootFrame, this.uiViewport);
			gameMessage.stringFrame.setHeight(gameMessage.stringFrame.getPredictedViewportHeight());
		}
		gameMessagesFrame.setAlpha(1.0f);
	}

	@Override
	public void showUpgradeCompleteAlert(final int playerIndex, final War3ID queuedRawcode, final int level) {
		if (playerIndex == this.war3MapViewer.getLocalPlayerIndex()) {
			String upgradeName;
			final CUpgradeType upgradeType = this.war3MapViewer.simulation.getUpgradeData().getType(queuedRawcode);
			if (upgradeType != null) {
				final UpgradeLevel upgradeLevel = upgradeType.getLevel(level - 1);
				if (upgradeLevel != null) {
					upgradeName = upgradeLevel.getName();
				}
				else {
					upgradeName = "NOTEXTERN Unknown Level " + level + " for '" + queuedRawcode + "'";
				}
			}
			else {
				upgradeName = "NOTEXTERN Unknown ('" + queuedRawcode + "')";
			}
			innerShowLocalCommandErrorString(playerIndex,
					this.rootFrame.getTemplates().getDecoratedString("COLON_COMPLETED") + upgradeName);
			this.war3MapViewer.getUiSounds().getSound(this.rootFrame.getSkinField("ResearchComplete"))
					.play(this.uiScene.audioContext, 0, 0, 0);
		}
	}

	@Override
	public void update(final float deltaTime) {
		this.portrait.update(deltaTime);
		if (this.portrait.isResetNeeded()) {
			this.portrait.resetCinematicSequence();
			this.portrait.setSelectedUnit(this.selectedUnit);
		}
		if (this.cinematicPanel.isVisible()) {
			this.cinematicPortrait.update(deltaTime);
		}

		final int baseMouseX = Gdx.input.getX();
		int mouseX = baseMouseX;
		final int baseMouseY = Gdx.input.getY();
		int mouseY = baseMouseY;
		final int minX = this.uiViewport.getScreenX();
		final int maxX = minX + this.uiViewport.getScreenWidth();
		final int minY = this.uiViewport.getScreenY();
		final int maxY = minY + this.uiViewport.getScreenHeight();
		final boolean left = (mouseX <= (minX + 3)) && WarsmashConstants.CATCH_CURSOR && this.userControlEnabled;
		final boolean right = (mouseX >= (maxX - 3)) && WarsmashConstants.CATCH_CURSOR && this.userControlEnabled;
		final boolean up = (mouseY <= (minY + 3)) && WarsmashConstants.CATCH_CURSOR && this.userControlEnabled;
		final boolean down = (mouseY >= (maxY - 3)) && WarsmashConstants.CATCH_CURSOR && this.userControlEnabled;
		this.cameraManager.applyVelocity(deltaTime, up, down, left, right);

		mouseX = Math.max(minX, Math.min(maxX, mouseX));
		mouseY = Math.max(minY, Math.min(maxY, mouseY));
		if (Gdx.input.isCursorCatched()) {
			if (WarsmashConstants.CATCH_CURSOR && this.showing) {
				Gdx.input.setCursorPosition(mouseX, mouseY);
			}
		}
		this.hpBarFrameIndex = 0;
		if (!this.allowDrag) {
			if ((this.mouseOverUnit != null) && isUnitSelectable(this.mouseOverUnit)) {
				final SimpleStatusBarFrame simpleStatusBarFrame = getHpBar();
				positionHealthBar(simpleStatusBarFrame, this.mouseOverUnit, 1.0f);
				if (this.mouseOverUnit instanceof RenderUnit) {
					final RenderUnit renderUnit = (RenderUnit) this.mouseOverUnit;
					if (renderUnit.getSimulationUnit().getMaximumMana() > 0) {
						final SimpleStatusBarFrame simpleStatusManaBarFrame = getHpBar();
						positionManaBar(simpleStatusManaBarFrame, renderUnit, 1.0f);
					}
				}
				final String hoverTipTextValue = getWorldFrameHoverTipText(this.war3MapViewer.simulation,
						this.mouseOverUnit);
				this.hovertipFrame.setVisible(hoverTipTextValue != null);
				if (hoverTipTextValue != null) {
					this.rootFrame.setText(this.hovertipText, hoverTipTextValue);
					final float predictedViewportHeight = this.hovertipText.getPredictedViewportHeight()
							+ GameUI.convertY(this.uiViewport, 0.009f);
					this.hovertipFrame.setHeight(predictedViewportHeight);
					this.hovertipFrame.setWidth(
							this.hovertipText.getPredictedViewportWidth() + GameUI.convertX(this.uiViewport, 0.012f));
					this.hovertipFrame.positionBounds(this.rootFrame, this.uiViewport);
					this.hovertipFrame.addSetPoint(new SetPoint(FramePoint.BOTTOM, simpleStatusBarFrame, FramePoint.TOP,
							0, GameUI.convertY(this.uiViewport, 0.003f)));
				}
				if (this.mouseOverUnit.getSimulationWidget().isInvulnerable()
						|| (this.mouseOverUnit instanceof RenderItem)) {
					// this is a bit silly, for now I'm using it to position the "Gold" text on gold
					// mines even though they are invulnerable
					simpleStatusBarFrame.setVisible(false);
				}
			}
			else {
				this.hovertipFrame.setVisible(false);
			}
		}
		else if (this.draggingMouseButton == Input.Buttons.LEFT) {
			final float minDragX = Math.min(this.lastMouseClickLocation.x, this.lastMouseDragStart.x);
			final float minDragY = Math.min(this.lastMouseClickLocation.y, this.lastMouseDragStart.y);
			final float maxDragX = Math.max(this.lastMouseClickLocation.x, this.lastMouseDragStart.x);
			final float maxDragY = Math.max(this.lastMouseClickLocation.y, this.lastMouseDragStart.y);
			this.tempRect.set(minDragX, minDragY, maxDragX - minDragX, maxDragY - minDragY);
			this.dragSelectPreviewUnitsUpcoming.clear();
			this.war3MapViewer.simulation.getWorldCollision().enumUnitsInRect(this.tempRect, new CUnitEnumFunction() {
				@Override
				public boolean call(final CUnit unit) {
					final RenderUnit renderUnit = MeleeUI.this.war3MapViewer.getRenderPeer(unit);
					if (!unit.isDead()
							&& renderUnit.isSelectable(MeleeUI.this.war3MapViewer.simulation,
									MeleeUI.this.war3MapViewer.getLocalPlayerIndex())
							&& MeleeUI.this.dragSelectPreviewUnitsUpcoming.add(renderUnit)) {
						final SimpleStatusBarFrame simpleStatusBarFrame = getHpBar();
						if (!unit.isInvulnerable()) {
							positionHealthBar(simpleStatusBarFrame, renderUnit, 1.0f);
						}
						if (!MeleeUI.this.dragSelectPreviewUnits.contains(renderUnit)) {
							MeleeUI.this.war3MapViewer.showUnitMouseOverHighlight(renderUnit);
						}
					}
					return false;
				}
			});
			for (final RenderUnit unit : this.dragSelectPreviewUnits) {
				if (!this.dragSelectPreviewUnitsUpcoming.contains(unit)) {
					this.war3MapViewer.clearUnitMouseOverHighlight(unit);
				}
			}
			final Set<RenderUnit> temp = this.dragSelectPreviewUnits;
			this.dragSelectPreviewUnits = this.dragSelectPreviewUnitsUpcoming;
			this.dragSelectPreviewUnitsUpcoming = temp;
		}
		if ((this.selectedUnits != null) && false) {
			for (final RenderUnit unit : this.selectedUnits) {
				final SimpleStatusBarFrame simpleStatusBarFrame = getHpBar();
				positionHealthBar(simpleStatusBarFrame, unit, 1.0f);
			}
		}
		for (int i = this.hpBarFrameIndex; i < this.hpBarFrames.size(); i++) {
			this.hpBarFrames.get(i).setVisible(false);
		}
		if (this.simpleProgressIndicator.isVisible() && (this.simpleProgressIndicatorDurationRemaining > 0)) {
			// NOTE: this is some approximation, not an accurate bar visual
			this.simpleProgressIndicatorDurationRemaining -= deltaTime;
			this.simpleProgressIndicator
					.setValue(this.simpleProgressIndicatorDurationRemaining / this.simpleProgressIndicatorDurationMax);
			;
		}

		screenCoordsVector.set(mouseX, mouseY);
		this.uiViewport.unproject(screenCoordsVector);
		this.cursorFrame.setFramePointX(FramePoint.LEFT, screenCoordsVector.x);
		this.cursorFrame.setFramePointY(FramePoint.BOTTOM, screenCoordsVector.y);

		if (this.activeCommand != null) {
			if (this.draggingItem != null) {
				setCursorState(MenuCursorState.HOLD_ITEM);
			}
			else {
				setCursorState(MenuCursorState.TARGET_CURSOR);
				this.activeCommand.visit(this.cursorTargetSetupVisitor.reset(baseMouseX, baseMouseY));
			}
		}
		else {
			if (this.cursorModelInstance != null) {
				this.cursorModelInstance.detach();
				this.cursorModelInstance = null;
				this.cursorFrame.setVisible(true);
			}
			if (this.placementCursor != null) {
				this.placementCursor.destroy(Gdx.gl30, this.war3MapViewer.terrain.centerOffset);
				this.placementCursor = null;
				this.cursorFrame.setVisible(true);
			}
			if (this.cursorModelUnderneathPathingRedGreenSplatModel != null) {
				this.war3MapViewer.terrain.removeSplatBatchModel(BUILDING_PATHING_PREVIEW_KEY);
				this.cursorModelUnderneathPathingRedGreenSplatModel = null;
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
			else if (this.mouseOverUnit != null) {
				if (this.mouseOverUnit instanceof RenderUnit) {
					final RenderUnit mouseOverUnitUnit = (RenderUnit) this.mouseOverUnit;
					final int playerIndex = mouseOverUnitUnit.getSimulationUnit().getPlayerIndex();
					if (!this.localPlayer.hasAlliance(playerIndex, CAllianceType.PASSIVE)) {
						setCursorState(MenuCursorState.SELECT, Color.RED);
					}
					else if (this.localPlayer.hasAlliance(playerIndex, CAllianceType.SHARED_CONTROL)) {
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
		if (this.selectedUnit != null) {
			updateBuildTimeIndicators();
		}

		final float groundHeight = Math.max(
				this.war3MapViewer.terrain.getGroundHeight(this.cameraManager.target.x, this.cameraManager.target.y),
				this.war3MapViewer.terrain.getWaterHeight(this.cameraManager.target.x, this.cameraManager.target.y));
		this.cameraManager.updateTargetZ(groundHeight);
		this.cameraManager.updateCamera();
		if (this.allowDrag && (this.draggingMouseButton == Input.Buttons.MIDDLE)) {
			// in case camera updates while dragging mouse, update where we think mouse is
			// in 3d
			this.lastMouseDragCameraTargetStart.set(this.cameraManager.target);
		}

		final long currentMillis = TimeUtils.millis();
		if (currentMillis > this.lastErrorMessageExpireTime) {
			this.errorMessageFrame.setVisible(false);
		}
		else if (currentMillis > this.lastErrorMessageFadeTime) {
			final float fadeAlpha = (this.lastErrorMessageExpireTime - currentMillis)
					/ (float) WORLD_FRAME_MESSAGE_FADE_DURATION;
			this.errorMessageFrame.setAlpha(fadeAlpha);
		}
		final Iterator<GameMessage> iterator = this.gameMessages.iterator();
		while (iterator.hasNext()) {
			final GameMessage gameMessage = iterator.next();
			if (currentMillis > gameMessage.lastGameMessageExpireTime) {
				this.rootFrame.remove(gameMessage.stringFrame);
				iterator.remove();
			}
			else if (currentMillis > gameMessage.lastGameMessageFadeTime) {
				final float fadeAlpha = (gameMessage.lastGameMessageExpireTime - currentMillis)
						/ (float) (gameMessage.lastGameMessageExpireTime - gameMessage.lastGameMessageFadeTime);
				gameMessage.stringFrame.setAlpha(fadeAlpha);
			}
		}
		this.musicPlayer.update();
		for (final CTimerDialog timerDialog : this.timerDialogs) {
			timerDialog.update(this.rootFrame, this.war3MapViewer.simulation);
		}
	}

	private void updateBuildTimeIndicators() {
		if (this.simpleBuildTimeIndicator.isVisible()) {
			this.simpleBuildTimeIndicator
					.setValue(Math.min(this.selectedUnit.getSimulationUnit().getConstructionProgress()
							/ this.selectedUnit.getSimulationUnit().getUnitType().getBuildTime(), 0.99f));
		}
		if (this.simpleBuildingBuildTimeIndicator.isVisible()) {
			this.simpleBuildingBuildTimeIndicator
					.setValue(Math.min(
							this.selectedUnit.getSimulationUnit().getConstructionProgress() / this.selectedUnit
									.getSimulationUnit().getBuildQueueTimeRemaining(this.war3MapViewer.simulation),
							0.99f));
		}
	}

	private boolean isUnitSelectable(final RenderWidget mouseOverUnit) {
		return mouseOverUnit.isSelectable(this.war3MapViewer.simulation, this.war3MapViewer.getLocalPlayerIndex())
				&& !mouseOverUnit.getSimulationWidget().isDead();
	}

	private String getWorldFrameHoverTipText(final CSimulation game, final RenderWidget whichUnit) {
		if (whichUnit instanceof RenderUnit) {
			final RenderUnit renderUnit = (RenderUnit) whichUnit;
			final CUnit simulationUnit = renderUnit.getSimulationUnit();
			final CAbilityHero heroData = simulationUnit.getHeroData();
			if (heroData != null) {
				final String level = this.rootFrame.getTemplates().getDecoratedString("LEVEL");
				return heroData.getProperName() + "|n" + level + " " + heroData.getHeroLevel();
			}
			else {
				final int simulationUnitPlayerIndex = simulationUnit.getPlayerIndex();
				final boolean neutralHostile = simulationUnitPlayerIndex == (WarsmashConstants.MAX_PLAYERS - 4);
				final boolean neutralPassive = simulationUnitPlayerIndex == (WarsmashConstants.MAX_PLAYERS - 1);
				String returnValue = "";
				if ((simulationUnitPlayerIndex != this.localPlayer.getId())
						&& (simulationUnitPlayerIndex < (WarsmashConstants.MAX_PLAYERS - 4))) {
					final boolean ally = simulationUnit.isUnitAlly(this.localPlayer);
					final CPlayer unitPlayer = game.getPlayer(simulationUnitPlayerIndex);
					final String name = unitPlayer.getName();
					if (name != null) {
						if (ally) {
							if (unitPlayer.hasAlliance(this.localPlayer.getId(), CAllianceType.SHARED_CONTROL)) {
								returnValue = "|CFF00FF00" + name;
							}
							else {
								returnValue = "|CFFFFFF00" + name;
							}
						}
						else {
							returnValue = "|CFFFF0000" + name;
						}
					}
				}
				final CAbilityGoldMinable goldMineData = simulationUnit.getGoldMineData();
				final CAbilityOverlayedMine blightedGoldMineData = simulationUnit.getOverlayedGoldMineData();
				final boolean neutral = (neutralPassive && simulationUnit.isBuilding()) || neutralHostile
						|| (goldMineData != null) || (blightedGoldMineData != null);
				if (neutral) {
					if (!returnValue.isEmpty()) {
						returnValue += "|n";
					}
					returnValue += simulationUnit.getUnitType().getName();
					if (goldMineData != null) {
						final String colonGold = this.rootFrame.getTemplates().getDecoratedString("COLON_GOLD");
						returnValue += "|n" + colonGold + " " + goldMineData.getGold();
					}
					else {
						if (blightedGoldMineData != null) {
							final String colonGold = this.rootFrame.getTemplates().getDecoratedString("COLON_GOLD");
							returnValue += "|n" + colonGold + " " + blightedGoldMineData.getGold();
						}
					}
					final int creepLevel = simulationUnit.getUnitType().getLevel();
					if (neutralHostile && (creepLevel > 0)) {
						final String level = this.rootFrame.getTemplates().getDecoratedString("LEVEL");
						returnValue += "|n" + level + " " + creepLevel;
					}
				}
				if (!returnValue.isEmpty()) {
					return returnValue;
				}
			}
		}
		else if (whichUnit instanceof RenderItem) {
			final RenderItem renderItem = (RenderItem) whichUnit;
			final ItemUI itemUI = this.war3MapViewer.getAbilityDataUI()
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
		this.war3MapViewer.worldScene.camera.worldToScreen(screenCoordsVector, clickLocationTemp);
		simpleStatusBarFrame.getBarFrame().setTexture("SimpleHpBarConsole", this.rootFrame);
		simpleStatusBarFrame.getBorderFrame().setTexture("Textures\\Black32.blp", this.rootFrame);
		simpleStatusBarFrame.getBorderFrame().setColor(0f, 0f, 0f, alpha);
		final float lifeRatioRemaining = unit.getSimulationWidget().getLife() / unit.getSimulationWidget().getMaxLife();
		simpleStatusBarFrame.getBarFrame().setColor(Math.min(1.0f, 2.0f - (lifeRatioRemaining * 2)),
				Math.min(1.0f, lifeRatioRemaining * 2), 0, alpha);
		final Vector2 unprojected = this.uiViewport.unproject(screenCoordsVector);
		simpleStatusBarFrame.setWidth((unit.getSelectionScale() * 1.5f * Gdx.graphics.getWidth()) / 2560);
		simpleStatusBarFrame.setHeight(16);
		simpleStatusBarFrame.addSetPoint(
				new SetPoint(FramePoint.CENTER, this.rootFrame, FramePoint.BOTTOMLEFT, unprojected.x, unprojected.y));
		simpleStatusBarFrame.setValue(lifeRatioRemaining);
		simpleStatusBarFrame.positionBounds(this.rootFrame, this.uiViewport);
	}

	private void positionManaBar(final SimpleStatusBarFrame simpleStatusBarFrame, final RenderUnit unit,
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
		this.war3MapViewer.worldScene.camera.worldToScreen(screenCoordsVector, clickLocationTemp);
		simpleStatusBarFrame.getBarFrame().setTexture("SimpleHpBarConsole", this.rootFrame);
		simpleStatusBarFrame.getBorderFrame().setTexture("Textures\\Black32.blp", this.rootFrame);
		simpleStatusBarFrame.getBorderFrame().setColor(0f, 0f, 0f, alpha);
		final float lifeRatioRemaining = unit.getSimulationUnit().getMana() / unit.getSimulationUnit().getMaximumMana();
		simpleStatusBarFrame.getBarFrame().setColor(0, 0, 1, alpha);
		final Vector2 unprojected = this.uiViewport.unproject(screenCoordsVector);
		simpleStatusBarFrame.setWidth((unit.getSelectionScale() * 1.5f * Gdx.graphics.getWidth()) / 2560);
		simpleStatusBarFrame.setHeight(16);
		simpleStatusBarFrame.addSetPoint(new SetPoint(FramePoint.CENTER, this.rootFrame, FramePoint.BOTTOMLEFT,
				unprojected.x, unprojected.y - 16f));
		simpleStatusBarFrame.setValue(lifeRatioRemaining);
		simpleStatusBarFrame.positionBounds(this.rootFrame, this.uiViewport);
	}

	private SimpleStatusBarFrame getHpBar() {
		final SimpleStatusBarFrame simpleStatusBarFrame;
		if (this.hpBarFrameIndex >= this.hpBarFrames.size()) {
			simpleStatusBarFrame = new SimpleStatusBarFrame("SmashHpBar" + this.hpBarFrameIndex, this.rootFrame, true,
					true, 3.0f);
			this.rootFrame.add(simpleStatusBarFrame);
			this.hpBarFrames.add(simpleStatusBarFrame);
		}
		else {
			simpleStatusBarFrame = this.hpBarFrames.get(this.hpBarFrameIndex);
		}
		this.hpBarFrameIndex++;
		return simpleStatusBarFrame;
	}

	private UIFrame getHoveredFrame(final AbstractUIFrame startFrame, final float screenX, final float screenY,
			final String[] includeParent, final String[] ignoreFrame) {
		UIFrame outFrame = null;
		if (startFrame.isVisible()) {
			final ListIterator<UIFrame> curIterator = startFrame.getChildIterator();
			while (curIterator.hasPrevious()) {
				final UIFrame child = curIterator.previous();
				boolean found = false;

				if (child instanceof AbstractUIFrame) {
					if (checkFrameInArray(child, includeParent)) {
						final AbstractRenderableFrame renderFrame = (AbstractRenderableFrame) child;
						found = renderFrame.getRenderBounds().contains(screenX, screenY) && renderFrame.isVisible();
						if (found) {
							outFrame = renderFrame;
						}
					}
					else {
						outFrame = getHoveredFrame((AbstractUIFrame) child, screenX, screenY, includeParent,
								ignoreFrame);
						found = outFrame != null;
					}
				}
				else {
					final AbstractRenderableFrame renderFrame = (AbstractRenderableFrame) child;
					found = renderFrame.getRenderBounds().contains(screenX, screenY) && renderFrame.isVisible();
					if (found) {
						outFrame = renderFrame;
					}
				}

				if ((outFrame != null) && !checkFrameInArray(outFrame, ignoreFrame)) {
					return outFrame;
				}
			}
		}
		return outFrame;
	}

	private boolean checkFrameInArray(final UIFrame frame, final String[] targetFrames) {
		if ((targetFrames == null) || (frame == null)) {
			return false;
		}
		else {
			if (frame.getName() == null) {
				return false;
			}

			for (int ind = 0; ind < targetFrames.length; ind++) {
				if (frame.getName().startsWith(targetFrames[ind])) {
					return true;
				}
			}
			return false;
		}
	}

	private void setCursorState(final MenuCursorState state, final Color color) {
		if (state != this.cursorState) {
			if (state.getAnimationName() != null) {
				this.cursorFrame.setSequence(state.getAnimationName());
			}
		}
		if (color != this.cursorColor) {
			this.cursorFrame.setVertexColor(color);
		}
		this.cursorState = state;
	}

	private void setCursorState(final MenuCursorState state) {
		setCursorState(state, Color.WHITE);
	}

	@Override
	public void render(final SpriteBatch batch, final GlyphLayout glyphLayout) {
		final BitmapFont font = this.rootFrame.getFont();
		font.setColor(Color.YELLOW);
		if (WarsmashConstants.SHOW_FPS) {
			final String fpsString = "FPS: " + Gdx.graphics.getFramesPerSecond();
			glyphLayout.setText(font, fpsString);
			font.draw(batch, fpsString, (this.uiViewport.getMinWorldWidth() - glyphLayout.width) / 2,
					1100 * this.heightRatioCorrection);
		}
		this.rootFrame.render(batch, this.rootFrame.getFont20(), glyphLayout);
		if (this.selectedUnit != null) {
			this.rootFrame.getFont20().setColor(Color.WHITE);

		}

		this.meleeUIMinimap.render(this.war3MapViewer.simulation, batch, this.war3MapViewer.units,
				this.war3MapViewer.simulation.getPathingGrid(), this.war3MapViewer.getFogOfWar(),
				this.war3MapViewer.simulation.getPlayer(this.war3MapViewer.getLocalPlayerIndex()));
		this.timeIndicator.setSequence(this.war3MapViewer.simulation.isFalseTimeOfDay() ? 1 : 0);
		this.timeIndicator.setFrameByRatio(this.war3MapViewer.simulation.getGameTimeOfDay()
				/ this.war3MapViewer.simulation.getGameplayConstants().getGameDayHours());
		for (final TextTag textTag : this.war3MapViewer.textTags) {
			if (textTag.isVisible()) {
				this.war3MapViewer.worldScene.camera.worldToScreen(screenCoordsVector, textTag.getPosition());
				if (this.war3MapViewer.worldScene.camera.rect.contains(screenCoordsVector.x,
						(Gdx.graphics.getHeight() - screenCoordsVector.y))) {
					final Vector2 unprojected = this.uiViewport.unproject(screenCoordsVector);
					unprojected.add(textTag.getScreenCoordTravelOffset());
					this.textTagFontParam.size = (int) GameUI.convertY(this.uiViewport, textTag.getFontHeight() * 0.5f);
					// below: generateFont is a caching call, so hopefully this is not allocating
					// font object on every loop, which would be wasteful
					final BitmapFont myTextTagFont = this.rootFrame.getFontGenerator()
							.generateFont(this.textTagFontParam);
					myTextTagFont.setColor(0, 0, 0, textTag.getColor().a);
					float x = unprojected.x;
					float y = unprojected.y;
					if (textTag.isCentered()) {
						glyphLayout.setText(myTextTagFont, textTag.getText());
						x -= (glyphLayout.width / 2);
						y += (glyphLayout.height / 2);
					}
					else {
						y += glyphLayout.height;
					}
					myTextTagFont.draw(batch, textTag.getText(), x + 3, (y - 1));
					myTextTagFont.setColor(textTag.getColor().r, textTag.getColor().g, textTag.getColor().b,
							textTag.getColor().a);
					myTextTagFont.draw(batch, textTag.getText(), x, y);
				}
			}
		}
		if (this.draggingMouseButton == Input.Buttons.LEFT) {
			batch.end();
			this.shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
			this.shapeRenderer.setColor(Color.GREEN);
			Gdx.gl.glLineWidth(2);
			this.shapeRenderer.begin(ShapeType.Line);
			this.cameraManager.camera.worldToScreen(screenCoordsVector, this.lastMouseDragStart);
			final Vector2 unprojected = this.uiViewport.unproject(screenCoordsVector);
			final float x = unprojected.x;
			final float y = unprojected.y;
			this.cameraManager.camera.worldToScreen(screenCoordsVector, this.lastMouseClickLocation);
			final Vector2 unprojectedEnd = this.uiViewport.unproject(screenCoordsVector);
			final float minX = Math.min(x, unprojectedEnd.x);
			final float minY = Math.min(y, unprojectedEnd.y);
			this.shapeRenderer.rect(minX, minY, Math.max(x, unprojectedEnd.x) - minX,
					Math.max(y, unprojectedEnd.y) - minY);
			this.shapeRenderer.end();
			Gdx.gl.glLineWidth(1);
			batch.begin();
		}
	}

	public void portraitTalk(final UnitSound us) {
		this.portrait.talk(us, 0);
	}

	private final class AnyClickableUnitFilter implements CWidgetFilterFunction {
		@Override
		public boolean call(final CWidget unit) {
			final RenderWidget renderPeer = MeleeUI.this.war3MapViewer.getRenderPeer(unit);
			return !unit.isDead() && renderPeer.isSelectable(MeleeUI.this.war3MapViewer.simulation,
					MeleeUI.this.war3MapViewer.getLocalPlayerIndex());
		}
	}

	private final class AnyTargetableUnitFilter implements CWidgetFilterFunction {
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
			if (MeleeUI.this.activeCommandOrderId == OrderIds.attackground) {
				float radius = 0;
				for (final CUnitAttack attack : MeleeUI.this.activeCommandUnit.getSimulationUnit()
						.getCurrentAttacks()) {
					if (attack.getWeaponType().isAttackGroundSupported()) {
						if (attack instanceof CUnitAttackMissileSplash) {
							final int areaOfEffectSmallDamage = ((CUnitAttackMissileSplash) attack)
									.getAreaOfEffectSmallDamage();
							radius = areaOfEffectSmallDamage;
						}
						else if (attack instanceof CUnitAttackMissileLine) {
							final float areaOfEffectSmallDamage = ((CUnitAttackMissileLine) attack)
									.getDamageSpillRadius();
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
		public Void accept(final GenericSingleIconPassiveAbility ability) {
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
		public Void accept(final CAbilitySellItems ability) {
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
		public Void accept(final AbilityBuilderActiveAbility ability) {
			final float uiAreaOfEffect = ability.getUIAreaOfEffect();
			if (Float.isNaN(uiAreaOfEffect)) {
				handleTargetCursor(ability);
			}
			else {
				handlePlacementCursor(ability, uiAreaOfEffect);
			}
			return null;
		}

		@Override
		public Void accept(final GenericSingleIconActiveAbility ability) {
			final float uiAreaOfEffect = ability.getUIAreaOfEffect();
			if (Float.isNaN(uiAreaOfEffect)) {
				handleTargetCursor(ability);
			}
			else {
				handlePlacementCursor(ability, uiAreaOfEffect);
			}
			return null;
		}

		@Override
		public Void accept(final GenericNoIconAbility ability) {
			// this should probably never happen
			handleTargetCursor(ability);
			return null;
		}

		@Override
		public Void accept(final CBuff ability) {
			// this should probably never happen
			return null;
		}

		@Override
		public Void accept(final CAbilityReturnResources ability) {
			// this should probably never happen
			handleTargetCursor(ability);
			return null;
		}

		@Override
		public Void accept(final CAbilityNeutralBuilding ability) {
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

		@Override
		public Void accept(final CAbilityRoot ability) {
			handleBuildCursor(null, MeleeUI.this.activeCommandUnit.getSimulationUnit().getTypeId().getValue());
			return null;
		}

		@Override
		public Void accept(final CAbilityJass ability) {
			// TODO getting icon from type is currently inefficient
			final COrderButton orderCommandCardIcon = ability.getOrderCommandCardIcon(
					MeleeUI.this.war3MapViewer.simulation, MeleeUI.this.selectedUnit.getSimulationUnit(),
					MeleeUI.this.activeCommandOrderId);
			final OrderButtonUI renderPeer = MeleeUI.this.war3MapViewer.getAbilityDataUI()
					.getRenderPeer(orderCommandCardIcon);
			if (renderPeer.getMouseTargetRadius() > 0) {
				handlePlacementCursor(ability, renderPeer.getMouseTargetRadius());
			}
			else if (renderPeer.getPreviewBuildUnitId() != null) {
				handleBuildCursor(null, renderPeer.getPreviewBuildUnitId().getValue());
			}
			else {
				handleTargetCursor(ability);
			}
			return null;
		}

		private void handleTargetCursor(final CAbility ability) {
			if (MeleeUI.this.cursorModelInstance != null) {
				MeleeUI.this.cursorModelInstance.detach();
				MeleeUI.this.cursorModelInstance = null;
				MeleeUI.this.cursorFrame.setVisible(true);
			}
			MeleeUI.this.cursorFrame.setSequence("Target");
		}

		private void handleBuildCursor(final AbstractCAbilityBuild ability) {
			handleBuildCursor(ability, MeleeUI.this.activeCommandOrderId);
		}

		private void handleBuildCursor(final AbstractCAbilityBuild ability, final int previewBuildUnitId) {
			boolean justLoaded = false;
			final War3MapViewer viewer = MeleeUI.this.war3MapViewer;
			if (MeleeUI.this.cursorModelInstance == null) {
				final War3ID buildingTypeId = new War3ID(previewBuildUnitId);
				MeleeUI.this.cursorBuildingUnitType = viewer.simulation.getUnitData().getUnitType(buildingTypeId);
				final MdxModel model = viewer.getUnitTypeData(buildingTypeId).getModel();
				MeleeUI.this.cursorModelInstance = (MdxComplexInstance) model.addInstance();
//				MeleeUI.this.cursorModelInstance.setVertexColor(new float[] { 1, 1, 1, 0.5f });
				final int playerColorIndex = viewer.simulation
						.getPlayer(MeleeUI.this.activeCommandUnit.getSimulationUnit().getPlayerIndex()).getColor();
				MeleeUI.this.cursorModelInstance.setTeamColor(playerColorIndex);
				MeleeUI.this.cursorModelInstance.rotate(RenderUnit.tempQuat.setFromAxis(RenderMathUtils.VEC3_UNIT_Z,
						viewer.simulation.getGameplayConstants().getBuildingAngle()));
				final float scalingValue = viewer.getUnitTypeData(buildingTypeId).getScalingValue();
				MeleeUI.this.cursorModelInstance.scale(new float[] { scalingValue, scalingValue, scalingValue });
				MeleeUI.this.cursorModelInstance.setAnimationSpeed(0f);
				justLoaded = true;
				final CUnitType buildingUnitType = MeleeUI.this.cursorBuildingUnitType;
				MeleeUI.this.cursorModelPathing = buildingUnitType.getBuildingPathingPixelMap();

				if (MeleeUI.this.cursorModelPathing != null) {
					MeleeUI.this.cursorModelUnderneathPathingRedGreenPixmap = new Pixmap(
							MeleeUI.this.cursorModelPathing.getWidth(), MeleeUI.this.cursorModelPathing.getHeight(),
							Format.RGBA8888);
					MeleeUI.this.cursorModelUnderneathPathingRedGreenPixmap.setBlending(Blending.None);
					MeleeUI.this.cursorModelUnderneathPathingRedGreenPixmapTextureData = new PixmapTextureData(
							MeleeUI.this.cursorModelUnderneathPathingRedGreenPixmap, Format.RGBA8888, false, false);
					MeleeUI.this.cursorModelUnderneathPathingRedGreenPixmapTexture = new Texture(
							MeleeUI.this.cursorModelUnderneathPathingRedGreenPixmapTextureData);
					final ViewerTextureRenderable greenPixmap = new ViewerTextureRenderable.GdxViewerTextureRenderable(
							MeleeUI.this.cursorModelUnderneathPathingRedGreenPixmapTexture);
					MeleeUI.this.cursorModelUnderneathPathingRedGreenSplatModel = new SplatModel(Gdx.gl30, greenPixmap,
							new ArrayList<>(), viewer.terrain.centerOffset, new ArrayList<>(), true, false, true,
							false);
					MeleeUI.this.cursorModelUnderneathPathingRedGreenSplatModel.color[3] = 0.20f;
				}
			}
			viewer.getClickLocation(clickLocationTemp, this.baseMouseX, Gdx.graphics.getHeight() - this.baseMouseY,
					false, false);
			if (MeleeUI.this.cursorModelPathing != null) {
				clickLocationTemp.x = (float) Math.floor(clickLocationTemp.x / 64f) * 64f;
				clickLocationTemp.y = (float) Math.floor(clickLocationTemp.y / 64f) * 64f;
				if (((MeleeUI.this.cursorModelPathing.getWidth() / 2) % 2) == 1) {
					clickLocationTemp.x += 32f;
				}
				if (((MeleeUI.this.cursorModelPathing.getHeight() / 2) % 2) == 1) {
					clickLocationTemp.y += 32f;
				}
				clickLocationTemp.z = viewer.terrain.getGroundHeight(clickLocationTemp.x, clickLocationTemp.y);

				final int cursorWidthCells = MeleeUI.this.cursorModelPathing.getWidth();
				final int halfCursorWidthCells = cursorWidthCells / 2;
				final float halfRenderWidth = cursorWidthCells * 16;
				final int cursorHeightCells = MeleeUI.this.cursorModelPathing.getHeight();
				final int halfCursorHeightCells = cursorHeightCells / 2;
				final float halfRenderHeight = cursorHeightCells * 16;
				final PathingGrid pathingGrid = viewer.simulation.getPathingGrid();
				boolean blockAll = false;
				final int cellX = pathingGrid.getCellX(clickLocationTemp.x);
				final int cellY = pathingGrid.getCellY(clickLocationTemp.y);
				if ((cellX < halfCursorWidthCells) || (cellX > (pathingGrid.getWidth() - halfCursorWidthCells))
						|| (cellY < halfCursorHeightCells)
						|| (cellY > (pathingGrid.getHeight() - halfCursorHeightCells))) {
					blockAll = true;
				}
				final boolean canBeBuiltOnThem = MeleeUI.this.cursorBuildingUnitType.isCanBeBuiltOnThem();
				if (canBeBuiltOnThem) {
					viewer.simulation.getWorldCollision().enumBuildingsAtPoint(clickLocationTemp.x, clickLocationTemp.y,
							MeleeUI.this.buildOnBuildingIntersector.reset(clickLocationTemp.x, clickLocationTemp.y));
					blockAll = MeleeUI.this.buildOnBuildingIntersector.getUnitToBuildOn() == null;
				}
				if (blockAll) {
					for (int i = 0; i < MeleeUI.this.cursorModelUnderneathPathingRedGreenPixmap.getWidth(); i++) {
						for (int j = 0; j < MeleeUI.this.cursorModelUnderneathPathingRedGreenPixmap.getHeight(); j++) {
							MeleeUI.this.cursorModelUnderneathPathingRedGreenPixmap.drawPixel(i,
									MeleeUI.this.cursorModelUnderneathPathingRedGreenPixmap.getHeight() - 1 - j,
									Color.rgba8888(1, 0, 0, 1.0f));
						}
					}
				}
				else if (!canBeBuiltOnThem) {
					for (int i = 0; i < MeleeUI.this.cursorModelUnderneathPathingRedGreenPixmap.getWidth(); i++) {
						for (int j = 0; j < MeleeUI.this.cursorModelUnderneathPathingRedGreenPixmap.getHeight(); j++) {
							boolean blocked = false;
							final short pathing = pathingGrid.getPathing(
									(clickLocationTemp.x + (i * 32)) - halfRenderWidth,
									(clickLocationTemp.y + (j * 32)) - halfRenderHeight);
							for (final CBuildingPathingType preventedType : MeleeUI.this.cursorBuildingUnitType
									.getPreventedPathingTypes()) {
								if (PathingFlags.isPathingFlag(pathing, preventedType)) {
									blocked = true;
								}
							}
							for (final CBuildingPathingType requiredType : MeleeUI.this.cursorBuildingUnitType
									.getRequiredPathingTypes()) {
								if (!PathingFlags.isPathingFlag(pathing, requiredType)) {
									blocked = true;
								}
							}
							final int color = blocked ? Color.rgba8888(1, 0, 0, 1.0f) : Color.rgba8888(0, 1, 0, 1.0f);
							MeleeUI.this.cursorModelUnderneathPathingRedGreenPixmap.drawPixel(i,
									MeleeUI.this.cursorModelUnderneathPathingRedGreenPixmap.getHeight() - 1 - j, color);
						}
					}
				}
				else {
					for (int i = 0; i < MeleeUI.this.cursorModelUnderneathPathingRedGreenPixmap.getWidth(); i++) {
						for (int j = 0; j < MeleeUI.this.cursorModelUnderneathPathingRedGreenPixmap.getHeight(); j++) {
							MeleeUI.this.cursorModelUnderneathPathingRedGreenPixmap.drawPixel(i,
									MeleeUI.this.cursorModelUnderneathPathingRedGreenPixmap.getHeight() - 1 - j,
									Color.rgba8888(0, 1, 0, 1.0f));
						}
					}
				}
				MeleeUI.this.cursorModelUnderneathPathingRedGreenPixmapTexture
						.load(MeleeUI.this.cursorModelUnderneathPathingRedGreenPixmapTextureData);

				if (justLoaded) {
					viewer.terrain.addSplatBatchModel(BUILDING_PATHING_PREVIEW_KEY,
							MeleeUI.this.cursorModelUnderneathPathingRedGreenSplatModel);
					MeleeUI.this.placementCursor = MeleeUI.this.cursorModelUnderneathPathingRedGreenSplatModel.add(
							clickLocationTemp.x - halfRenderWidth, clickLocationTemp.y - halfRenderHeight,
							clickLocationTemp.x + halfRenderWidth, clickLocationTemp.y + halfRenderHeight, 10,
							viewer.terrain.centerOffset);
				}
				MeleeUI.this.placementCursor.setLocation(clickLocationTemp.x, clickLocationTemp.y,
						viewer.terrain.centerOffset);
			}
			MeleeUI.this.cursorModelInstance.setLocation(clickLocationTemp);
			SequenceUtils.randomSequence(MeleeUI.this.cursorModelInstance, PrimaryTag.STAND);
			MeleeUI.this.cursorFrame.setVisible(false);
			if (justLoaded) {
				MeleeUI.this.cursorModelInstance.setScene(viewer.worldScene);
			}
		}

		private void handlePlacementCursor(final CAbility ability, final float radius) {
			final War3MapViewer viewer = MeleeUI.this.war3MapViewer;
			viewer.getClickLocation(clickLocationTemp, this.baseMouseX, Gdx.graphics.getHeight() - this.baseMouseY,
					false, true); // TODO change this to true once the placement cursor can draw over water, which
			// it should upgrade to do probs
			if (MeleeUI.this.placementCursor == null) {
				MeleeUI.this.placementCursor = viewer.terrain.addUberSplat(
						MeleeUI.this.rootFrame.getSkinField("PlacementCursor"), clickLocationTemp.x,
						clickLocationTemp.y, 10, radius, true, true, true, true);
			}
			MeleeUI.this.placementCursor.setLocation(clickLocationTemp.x, clickLocationTemp.y,
					viewer.terrain.centerOffset);
			MeleeUI.this.cursorFrame.setVisible(false);
		}
	}

	private final class RallyPositioningVisitor implements AbilityTargetVisitor<Void> {
		private MdxComplexInstance rallyPointInstance = null;

		public RallyPositioningVisitor reset(final MdxComplexInstance rallyPointInstance) {
			this.rallyPointInstance = rallyPointInstance;
			return this;
		}

		@Override
		public Void accept(final AbilityPointTarget target) {
			this.rallyPointInstance.setParent(null);
			final float rallyPointX = target.getX();
			final float rallyPointY = target.getY();
			this.rallyPointInstance.setLocation(rallyPointX, rallyPointY,
					MeleeUI.this.war3MapViewer.terrain.getGroundHeight(rallyPointX, rallyPointY));
			return null;
		}

		@Override
		public Void accept(final CUnit target) {
			final RenderUnit renderUnit = MeleeUI.this.war3MapViewer.getRenderPeer(target);
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
				this.rallyPointInstance.setParent(attachment);
				this.rallyPointInstance.setLocation(0, 0, 0);
			}
			else {
				this.rallyPointInstance.setParent(null);
				final float rallyPointX = target.getX();
				final float rallyPointY = target.getY();
				this.rallyPointInstance.setLocation(rallyPointX, rallyPointY,
						MeleeUI.this.war3MapViewer.terrain.getGroundHeight(rallyPointX, rallyPointY));
			}
			return null;
		}

		@Override
		public Void accept(final CDestructable target) {
			this.rallyPointInstance.setParent(null);
			final float rallyPointX = target.getX();
			final float rallyPointY = target.getY();
			this.rallyPointInstance.setLocation(rallyPointX, rallyPointY,
					MeleeUI.this.war3MapViewer.terrain.getGroundHeight(rallyPointX, rallyPointY) + 192);
			return null;
		}

		@Override
		public Void accept(final CItem target) {
			this.rallyPointInstance.setParent(null);
			final float rallyPointX = target.getX();
			final float rallyPointY = target.getY();
			this.rallyPointInstance.setLocation(rallyPointX, rallyPointY,
					MeleeUI.this.war3MapViewer.terrain.getGroundHeight(rallyPointX, rallyPointY));
			return null;
		}
	}

	private final class ActiveCommandUnitTargetFilter implements CWidgetFilterFunction {
		private String lastFailureMessage = null;

		@Override
		public boolean call(final CWidget unit) {
			final ExternStringMsgTargetCheckReceiver<CWidget> targetReceiver = ExternStringMsgTargetCheckReceiver
					.<CWidget>getInstance().reset();
			MeleeUI.this.activeCommand.checkCanTarget(MeleeUI.this.war3MapViewer.simulation,
					MeleeUI.this.activeCommandUnit.getSimulationUnit(), MeleeUI.this.activeCommandOrderId, unit,
					targetReceiver);
			this.lastFailureMessage = targetReceiver.getExternStringKey();
			return targetReceiver.getTarget() != null;
		}

		public void reset() {
			this.lastFailureMessage = null;
		}
	}

	private static final class Portrait {
		private MdxComplexInstance modelInstance;
		private final PortraitCameraManager portraitCameraManager;
		private final Scene portraitScene;
		private final EnumSet<AnimationTokens.SecondaryTag> recycleSet = EnumSet
				.noneOf(AnimationTokens.SecondaryTag.class);
		private EnumSet<AnimationTokens.SecondaryTag> secondaryTags;
		private RenderWidget lastUnit;
		// If animationTargetDuration is 0, it will play the whole animation.
		private long portraitTargetDuration = 0;
		private long portraitCurrentDuration = 0;
		private long faceLockTime = 0;

		public Portrait(final War3MapViewer war3MapViewer, final Scene portraitScene) {
			this.portraitScene = portraitScene;
			this.portraitCameraManager = new PortraitCameraManager();
			this.portraitCameraManager.setupCamera(this.portraitScene);
			this.portraitScene.camera.viewport(new Rectangle(100, 0, 6400, 48));
		}

		public void resetCinematicSequence() {
			this.secondaryTags = SequenceUtils.EMPTY;
			if (this.modelInstance != null) {
				this.portraitScene.removeInstance(this.modelInstance);
			}
			this.modelInstance = null;
			this.portraitCameraManager.setModelInstance(null, null);
			this.faceLockTime = 0;
		}

		public void update(final float dt) {
			this.portraitCameraManager.updateCamera();
			this.portraitCurrentDuration += (dt * 1000);
			if (this.modelInstance != null) {
				if (this.portraitTargetDuration != 0) {
					if (this.portraitCurrentDuration < this.portraitTargetDuration) {
						this.modelInstance.sequenceLoopMode = SequenceLoopMode.ALWAYS_LOOP;
					}
					else {
						this.modelInstance.sequenceEnded = true;
						this.modelInstance.sequenceLoopMode = SequenceLoopMode.NEVER_LOOP;
						this.portraitTargetDuration = 0;
					}
				}

				if (this.modelInstance.sequenceEnded || (this.modelInstance.sequence == -1)) {
					this.recycleSet.clear();
					this.recycleSet.addAll(this.secondaryTags);
					if (SequenceUtils.randomSequence(this.modelInstance, PrimaryTag.PORTRAIT, this.recycleSet,
							true) == null) {
						SequenceUtils.randomSequence(this.modelInstance, PrimaryTag.STAND, this.recycleSet, true);
					}
				}
			}
		}

		public boolean isResetNeeded() {
			return (this.faceLockTime != 0) && (this.portraitCurrentDuration > this.faceLockTime);
		}

		public void talk(final UnitSound us, float extraDuration) {
			if ((this.faceLockTime > 0) && (this.portraitCurrentDuration <= this.faceLockTime)) {
				return;
			}
			innerTalk(us, extraDuration);
		}

		private void innerTalk(final UnitSound us, float extraDuration) {
			// TODO we somehow called talk from null by clicking a unit right at the same
			// time it died, so I do a null check here until I study that case further.
			if (this.modelInstance != null) {
				this.recycleSet.clear();
				this.recycleSet.addAll(this.secondaryTags);
				this.recycleSet.add(SecondaryTag.TALK);
				if (us != null) {
					this.portraitTargetDuration = (long) (1000 * Extensions.audio.getDuration(us.getLastPlayedSound()));
				}
				else {
					this.portraitTargetDuration = 0;
				}
				this.portraitTargetDuration += (long) (1000 * extraDuration);
				this.portraitCurrentDuration = 0;
				if (SequenceUtils.randomSequence(this.modelInstance, PrimaryTag.PORTRAIT, this.recycleSet,
						true) == null) {
					SequenceUtils.randomSequence(this.modelInstance, PrimaryTag.STAND, this.recycleSet, true);
				}
			}
		}

		public void setSelectedUnit(final RenderUnit unit) {
			if ((this.faceLockTime > 0) && (this.portraitCurrentDuration <= this.faceLockTime)) {
				return;
			}
			if (this.lastUnit != unit) {
				this.lastUnit = unit;
				if (unit == null) {
					this.secondaryTags = SequenceUtils.EMPTY;
					if (this.modelInstance != null) {
						this.portraitScene.removeInstance(this.modelInstance);
					}
					this.modelInstance = null;
					this.portraitCameraManager.setModelInstance(null, null);
				}
				else {
					this.secondaryTags = unit.getSecondaryAnimationTags();
					final MdxModel portraitModel = unit.portraitModel;
					if (portraitModel != null) {
						if (this.modelInstance != null) {
							this.portraitScene.removeInstance(this.modelInstance);
						}
						this.modelInstance = (MdxComplexInstance) portraitModel.addInstance();
						this.portraitCameraManager.setModelInstance(this.modelInstance, portraitModel);
						this.modelInstance.setBlendTime(portraitModel.blendTime);
						this.modelInstance.setSequenceLoopMode(SequenceLoopMode.NEVER_LOOP);
						this.modelInstance.setScene(this.portraitScene);
						this.modelInstance.setVertexColor(unit.instance.vertexColor);
						this.modelInstance.setTeamColor(unit.playerIndex);
					}
				}
			}
		}

		public void setSelectedItem(final RenderItem unit) {
			if ((this.faceLockTime > 0) && (this.portraitCurrentDuration <= this.faceLockTime)) {
				return;
			}
			if (this.lastUnit != unit) {
				this.lastUnit = unit;
				this.secondaryTags = SequenceUtils.EMPTY;
				if (unit == null) {
					if (this.modelInstance != null) {
						this.portraitScene.removeInstance(this.modelInstance);
					}
					this.modelInstance = null;
					this.portraitCameraManager.setModelInstance(null, null);
				}
				else {
					final MdxModel portraitModel = unit.portraitModel;
					if (portraitModel != null) {
						if (this.modelInstance != null) {
							this.portraitScene.removeInstance(this.modelInstance);
						}
						this.modelInstance = (MdxComplexInstance) portraitModel.addInstance();
						this.portraitCameraManager.setModelInstance(this.modelInstance, portraitModel);
						this.modelInstance.setBlendTime(portraitModel.blendTime);
						this.modelInstance.setSequenceLoopMode(SequenceLoopMode.NEVER_LOOP);
						this.modelInstance.setScene(this.portraitScene);
						this.modelInstance.setVertexColor(unit.instance.vertexColor);
					}
				}
			}
		}

		public void setSelectedDestructable(final RenderDestructable unit) {
			if ((this.faceLockTime > 0) && (this.portraitCurrentDuration <= this.faceLockTime)) {
				return;
			}
			if (this.lastUnit != unit) {
				this.lastUnit = unit;
				this.secondaryTags = SequenceUtils.EMPTY;
				if (unit == null) {
					if (this.modelInstance != null) {
						this.portraitScene.removeInstance(this.modelInstance);
					}
					this.modelInstance = null;
					this.portraitCameraManager.setModelInstance(null, null);
				}
				else {
					final MdxModel portraitModel = unit.getPortraitModel();
					if (portraitModel != null) {
						if (this.modelInstance != null) {
							this.portraitScene.removeInstance(this.modelInstance);
						}
						this.modelInstance = (MdxComplexInstance) portraitModel.addInstance();
						this.portraitCameraManager.setModelInstance(this.modelInstance, portraitModel);
						this.modelInstance.setBlendTime(portraitModel.blendTime);
						this.modelInstance.setSequenceLoopMode(SequenceLoopMode.NEVER_LOOP);
						this.modelInstance.setScene(this.portraitScene);
					}
				}
			}
		}

		public void setCinematicTalkingHead(MdxModel portraitModel, int teamColorIndex,
				EnumSet<SecondaryTag> secondaryTags, float faceLockTime, final UnitSound us, float extraDuration) {
			this.faceLockTime = (long) (1000 * faceLockTime);
			if (this.modelInstance != null) {
				this.portraitScene.removeInstance(this.modelInstance);
			}
			this.secondaryTags = secondaryTags;
			if (portraitModel != null) {
				this.modelInstance = (MdxComplexInstance) portraitModel.addInstance();
				this.portraitCameraManager.setModelInstance(this.modelInstance, portraitModel);
				this.modelInstance.setBlendTime(portraitModel.blendTime);
				this.modelInstance.setSequenceLoopMode(SequenceLoopMode.NEVER_LOOP);
				this.modelInstance.setScene(this.portraitScene);
				this.modelInstance.setTeamColor(teamColorIndex);
			}
			this.lastUnit = null;
			innerTalk(us, extraDuration);
		}
	}

	private static final class CinematicPortrait {
		private final EnumSet<AnimationTokens.SecondaryTag> recycleSet = EnumSet
				.noneOf(AnimationTokens.SecondaryTag.class);
		private EnumSet<AnimationTokens.SecondaryTag> secondaryTags;
		// If animationTargetDuration is 0, it will play the whole animation.
		private long portraitTargetDuration = 0;
		private long portraitCurrentDuration = 0;
		private long portraitShowDuration = -1;

		private final SpriteFrame2 cinematicPortrait;
		private final UIFrame cinematicScenePanel;

		public CinematicPortrait(SpriteFrame2 cinematicPortrait, UIFrame cinematicScenePanel) {
			this.cinematicPortrait = cinematicPortrait;
			this.cinematicScenePanel = cinematicScenePanel;
			this.secondaryTags = SequenceUtils.EMPTY;
		}

		public void endCinematicSequence() {
			this.secondaryTags = SequenceUtils.EMPTY;
			this.cinematicScenePanel.setVisible(false);
		}

		public void update(final float dt) {
			if (isResetNeeded()) {
				endCinematicSequence();
			}
			else {
				this.portraitCurrentDuration += (dt * 1000);
				if (this.portraitTargetDuration != 0) {
					if (this.portraitCurrentDuration < this.portraitTargetDuration) {
						this.cinematicPortrait.setSequenceLoopMode(SequenceLoopMode.ALWAYS_LOOP);
					}
					else {
						this.cinematicPortrait.setSequenceEnded(true);
						this.cinematicPortrait.setSequenceLoopMode(SequenceLoopMode.NEVER_LOOP);
						this.portraitTargetDuration = 0;
					}
				}

				if (this.cinematicPortrait.isSequenceEnded()) {
					this.recycleSet.clear();
					this.recycleSet.addAll(this.secondaryTags);
					if (this.cinematicPortrait.setSequence(PrimaryTag.PORTRAIT, this.recycleSet) == null) {
						this.cinematicPortrait.setSequence(PrimaryTag.STAND, this.recycleSet);
					}
				}
			}
		}

		private boolean isResetNeeded() {
			return this.portraitCurrentDuration > this.portraitShowDuration;
		}

		public void talk(final UnitSound us, float extraDuration) {
			// TODO we somehow called talk from null by clicking a unit right at the same
			// time it died, so I do a null check here until I study that case further.
			this.recycleSet.clear();
			this.recycleSet.addAll(this.secondaryTags);
			this.recycleSet.add(SecondaryTag.TALK);
			if (us != null) {
				this.portraitTargetDuration = (long) (1000 * Extensions.audio.getDuration(us.getLastPlayedSound()));
			}
			else {
				this.portraitTargetDuration = 0;
			}
			this.portraitTargetDuration += (long) (1000 * extraDuration);
			this.portraitCurrentDuration = 0;

			if (this.cinematicPortrait.setSequence(PrimaryTag.PORTRAIT, this.recycleSet) == null) {
				this.cinematicPortrait.setSequence(PrimaryTag.STAND, this.recycleSet);
			}
		}

		public void setCinematicTalkingHead(MdxModel portraitModel, int teamColorIndex,
				EnumSet<SecondaryTag> secondaryTags, float faceLockTime) {
			this.cinematicScenePanel.setVisible(true);
			this.portraitShowDuration = (long) (1000 * faceLockTime);
			this.secondaryTags = secondaryTags;
			this.cinematicPortrait.setModel(portraitModel);
			this.cinematicPortrait.setTeamColor(teamColorIndex);
		}
	}

	public void setDraggingItem(final CItem itemInSlot) {
		this.draggingItem = itemInSlot;
		if (itemInSlot != null) {
			final String iconPath = this.war3MapViewer.getAbilityDataUI().getItemUI(itemInSlot.getTypeId())
					.getItemIconPathForDragging();
			this.cursorFrame.setReplaceableId(21, this.war3MapViewer.blp(iconPath));

			int index = 0;
			final CAbilityInventory inventory = this.selectedUnit.getSimulationUnit().getInventoryData();
			for (int i = 0; i < INVENTORY_HEIGHT; i++) {
				for (int j = 0; j < INVENTORY_WIDTH; j++) {
					final CommandCardIcon inventoryIcon = this.inventoryIcons[i][j];
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
			if (this.selectedUnit != null) {
				final CAbilityInventory inventory = this.selectedUnit.getSimulationUnit().getInventoryData();
				if (inventory != null) {
					int index = 0;
					for (int i = 0; i < INVENTORY_HEIGHT; i++) {
						for (int j = 0; j < INVENTORY_WIDTH; j++) {
							final CommandCardIcon inventoryIcon = this.inventoryIcons[i][j];
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
		this.subMenuOrderIdStack.clear();
		if ((unit != null) && unit.getSimulationUnit().isDead()) {
			unit = null;
		}
		if (this.selectedUnit != null) {
			this.selectedUnit.getSimulationUnit().removeStateListener(this);
		}
		this.portrait.setSelectedUnit(unit);
		this.selectedUnit = unit;
		setDraggingItem(null);
		if (unit == null) {
			clearCommandCard();
			this.rootFrame.setText(this.simpleNameValue, "");
			this.rootFrame.setText(this.unitLifeText, "");
			this.rootFrame.setText(this.unitManaText, "");
			this.rootFrame.setText(this.simpleClassValue, "");
			this.rootFrame.setText(this.simpleBuildingActionLabel, "");
			this.attack1Icon.setVisible(false);
			this.attack2Icon.setVisible(false);
			this.rootFrame.setText(this.attack1InfoPanelIconLevel, "");
			this.rootFrame.setText(this.attack2InfoPanelIconLevel, "");
			this.rootFrame.setText(this.simpleBuildingBuildingActionLabel, "");
			this.rootFrame.setText(this.simpleBuildingNameValue, "");
			this.armorIcon.setVisible(false);
			this.rootFrame.setText(this.armorInfoPanelIconLevel, "");
			this.simpleBuildTimeIndicator.setVisible(false);
			this.simpleHeroLevelBar.setVisible(false);
			this.simpleBuildingBuildTimeIndicator.setVisible(false);
			this.simpleInfoPanelBuildingDetail.setVisible(false);
			this.simpleInfoPanelItemDetail.setVisible(false);
			this.simpleInfoPanelDestructableDetail.setVisible(false);
			this.simpleInfoPanelUnitDetail.setVisible(false);
			for (final QueueIcon queueIconFrame : this.queueIconFrames) {
				queueIconFrame.setVisible(false);
			}
			this.selectWorkerInsideFrame.setVisible(false);
			this.heroInfoPanel.setVisible(false);
			this.rallyPointInstance.hide();
			this.rallyPointInstance.detach();
			this.inventoryCover.setVisible(true);
			this.inventoryBarFrame.setVisible(false);
			for (final MultiSelectionIcon iconFrame : this.selectedUnitFrames) {
				iconFrame.setVisible(false);
			}
			for (final UIFrame frame : this.selectedUnitHighlightBackdrop) {
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
		if (this.selectedUnit != null) {
			final CUnit simulationUnit = this.selectedUnit.getSimulationUnit();
			repositionRallyPoint(simulationUnit);
		}
	}

	private void repositionRallyPoint(final CUnit simulationUnit) {
		final AbilityTarget rallyPoint = simulationUnit.getRallyPoint();
		if ((rallyPoint != null) && (simulationUnit.getFirstAbilityOfType(CAbilityRally.class) != null)) {
			this.rallyPointInstance
					.setTeamColor(this.war3MapViewer.simulation.getPlayer(simulationUnit.getPlayerIndex()).getColor());
			this.rallyPointInstance.show();
			this.rallyPointInstance.detach();
			rallyPoint.visit(this.rallyPositioningVisitor.reset(this.rallyPointInstance));
			this.rallyPointInstance.setScene(this.war3MapViewer.worldScene);
		}
		else {
			this.rallyPointInstance.hide();
			this.rallyPointInstance.detach();
		}
	}

	@Override
	public void waypointsChanged() {
		if (this.selectedUnit != null) {
			final CUnit simulationUnit = this.selectedUnit.getSimulationUnit();
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
				final AbilityTarget target = order.getTarget(this.war3MapViewer.simulation);
				if (target != null) {
					waypointModelInstance.show();
					waypointModelInstance.detach();
					target.visit(this.rallyPositioningVisitor.reset(waypointModelInstance));
					waypointModelInstance.setScene(this.war3MapViewer.worldScene);
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
		for (; (orderIndex < this.waypointModelInstances.size()) || iterator.hasNext(); orderIndex++) {
			final MdxComplexInstance waypointModelInstance = getOrCreateWaypointIndicator(orderIndex);
			if (iterator.hasNext()) {
				final COrder order = iterator.next();
				if (order != null) {
					final AbilityTarget target = order.getTarget(this.war3MapViewer.simulation);
					if (target != null) {
						waypointModelInstance.show();
						waypointModelInstance.detach();
						target.visit(this.rallyPositioningVisitor.reset(waypointModelInstance));
						waypointModelInstance.setScene(this.war3MapViewer.worldScene);
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
			else {
				waypointModelInstance.hide();
				waypointModelInstance.detach();
			}
		}
	}

	private MdxComplexInstance getOrCreateWaypointIndicator(final int index) {
		while (index >= this.waypointModelInstances.size()) {
			final MdxComplexInstance waypointModelInstance = (MdxComplexInstance) this.waypointModel.addInstance();
			waypointModelInstance.rotate(RenderUnit.tempQuat.setFromAxis(RenderMathUtils.VEC3_UNIT_Z,
					this.war3MapViewer.simulation.getGameplayConstants().getBuildingAngle()));
			waypointModelInstance.setSequenceLoopMode(SequenceLoopMode.ALWAYS_LOOP);
			SequenceUtils.randomStandSequence(waypointModelInstance);
			waypointModelInstance.hide();
			this.waypointModelInstances.add(waypointModelInstance);
		}
		return this.waypointModelInstances.get(index);
	}

	private void reloadSelectedUnitUI(final RenderUnit unit) {
		if (unit == null) {
			return;
		}
		final CUnit simulationUnit = unit.getSimulationUnit();
		final float lifeRatioRemaining = simulationUnit.getLife() / simulationUnit.getMaxLife();
		this.rootFrame.setText(this.unitLifeText, FastNumberFormat.formatWholeNumber(simulationUnit.getLife()) + " / "
				+ FastNumberFormat.formatWholeNumber(simulationUnit.getMaxLife()));
		this.unitLifeText.setColor(new Color(Math.min(1.0f, 2.0f - (lifeRatioRemaining * 2)),
				Math.min(1.0f, lifeRatioRemaining * 2), 0, 1.0f));
		final int maximumMana = simulationUnit.getMaximumMana();
		if (maximumMana > 0) {
			this.rootFrame.setText(this.unitManaText,
					FastNumberFormat.formatWholeNumber(simulationUnit.getMana()) + " / " + maximumMana);
		}
		else {
			this.rootFrame.setText(this.unitManaText, "");
		}
		final boolean multiSelect = this.selectedUnits.size() > 1;
		repositionRallyPoint(simulationUnit);
		repositionWaypointFlags(simulationUnit);
		if (!multiSelect) {
			for (int i = 0; i < this.selectedUnitFrames.length; i++) {
				this.selectedUnitFrames[i].setVisible(false);
				this.selectedUnitHighlightBackdrop[i].setVisible(false);
			}
		}
		for (int i = 0; i < this.cargoUnitFrames.length; i++) {
			this.cargoUnitFrames[i].setVisible(false);
			this.cargoBackdrop[i].setVisible(false);
		}
		this.simpleProgressIndicator.setVisible(false);
		if ((simulationUnit.getBuildQueue()[0] != null)
				&& (simulationUnit.getPlayerIndex() == this.war3MapViewer.getLocalPlayerIndex()) && !multiSelect) {
			for (int i = 0; i < this.queueIconFrames.length; i++) {
				final QueueItemType queueItemType = simulationUnit.getBuildQueueTypes()[i];
				if ((queueItemType == null)
						|| ((i > 0) && QueueItemType.SACRIFICE.equals(simulationUnit.getBuildQueueTypes()[0]))) {
					this.queueIconFrames[i].setVisible(false);
				}
				else {
					this.queueIconFrames[i].setVisible(true);
					switch (queueItemType) {
					case RESEARCH:
						final War3ID rawcode = simulationUnit.getBuildQueue()[i];
						final IconUI upgradeUI = this.war3MapViewer.getAbilityDataUI().getUpgradeUI(rawcode,
								this.war3MapViewer.simulation.getPlayer(simulationUnit.getPlayerIndex())
										.getTechtreeUnlocked(rawcode));
						this.queueIconFrames[i].setTexture(upgradeUI.getIcon());
						this.queueIconFrames[i].setToolTip(upgradeUI.getToolTip());
						this.queueIconFrames[i].setUberTip(upgradeUI.getUberTip());
						break;
					case HERO_REVIVE: {
						final War3ID handleIdEncoded = simulationUnit.getBuildQueue()[i];
						final CUnit hero = this.war3MapViewer.simulation.getUnit(handleIdEncoded.getValue());
						final UnitIconUI unitUI = this.war3MapViewer.getAbilityDataUI().getUnitUI(hero.getTypeId());
						this.queueIconFrames[i].setTexture(unitUI.getIcon());
						this.queueIconFrames[i]
								.setToolTip(unitUI.getReviveTip() + " - " + hero.getHeroData().getProperName());
						this.queueIconFrames[i].setUberTip(unitUI.getUberTip());
						break;
					}
					case UNIT:
					default: {
						final IconUI unitUI = this.war3MapViewer.getAbilityDataUI()
								.getUnitUI(simulationUnit.getBuildQueue()[i]);
						this.queueIconFrames[i].setTexture(unitUI.getIcon());
						this.queueIconFrames[i].setToolTip(unitUI.getToolTip());
						this.queueIconFrames[i].setUberTip(unitUI.getUberTip());
						break;
					}
					}
				}
			}
			this.simpleInfoPanelBuildingDetail.setVisible(!multiSelect);
			this.simpleInfoPanelItemDetail.setVisible(false);
			this.simpleInfoPanelDestructableDetail.setVisible(false);
			this.simpleBuildQueueBackdrop.setVisible(true);
			this.simpleInfoPanelUnitDetail.setVisible(false);
			this.rootFrame.setText(this.simpleBuildingNameValue, simulationUnit.getUnitType().getName());
			this.rootFrame.setText(this.simpleBuildingDescriptionValue, "");

			this.simpleBuildingBuildTimeIndicator.setVisible(true);
			this.simpleBuildTimeIndicator.setVisible(false);
			this.simpleHeroLevelBar.setVisible(false);
			if (simulationUnit.getBuildQueueTypes()[0] == QueueItemType.UNIT) {
				this.rootFrame.setText(this.simpleBuildingBuildingActionLabel,
						this.rootFrame.getTemplates().getDecoratedString("TRAINING"));
			}
			else if (simulationUnit.getBuildQueueTypes()[0] == QueueItemType.SACRIFICE) {
				this.rootFrame.setText(this.simpleBuildingBuildingActionLabel,
						this.rootFrame.getTemplates().getDecoratedString("TRAINING"));
				this.simpleBuildQueueBackdrop.setVisible(false);
			}
			else if (simulationUnit.getBuildQueueTypes()[0] == QueueItemType.HERO_REVIVE) {
				this.rootFrame.setText(this.simpleBuildingBuildingActionLabel,
						this.rootFrame.getTemplates().getDecoratedString("REVIVING"));
			}
			else {
				this.rootFrame.setText(this.simpleBuildingBuildingActionLabel,
						this.rootFrame.getTemplates().getDecoratedString("RESEARCHING"));
			}
			this.attack1Icon.setVisible(false);
			this.attack2Icon.setVisible(false);
			this.armorIcon.setVisible(false);
			this.heroInfoPanel.setVisible(false);
			this.selectWorkerInsideFrame.setVisible(false);
			this.smashBuffStatusBar.setVisible(false);
		}
		else if (multiSelect) {
			for (int i = 0; i < this.queueIconFrames.length; i++) {
				this.queueIconFrames[i].setVisible(false);
			}
			for (int i = 0; i < this.selectedUnitFrames.length; i++) {
				final boolean useIcon = i < this.selectedUnits.size();
				this.selectedUnitFrames[i].setVisible(useIcon);
				final boolean focused = useIcon && this.selectedUnits.get(i).groupsWith(this.selectedUnit);
				this.selectedUnitHighlightBackdrop[i].setVisible(focused);
				if (useIcon) {
					final CUnit multiSelectedUnit = this.selectedUnits.get(i).getSimulationUnit();
					final CUnitType unitType = multiSelectedUnit.getUnitType();
					final IconUI unitUI = this.war3MapViewer.getAbilityDataUI().getUnitUI(unitType.getTypeId());
					this.selectedUnitFrames[i].setTexture(unitUI.getIcon());
					this.selectedUnitFrames[i].setToolTip(unitUI.getToolTip());
					this.selectedUnitFrames[i].setUberTip(unitUI.getUberTip());
					this.selectedUnitFrames[i]
							.setLifeRatioRemaining(multiSelectedUnit.getLife() / multiSelectedUnit.getMaximumLife());
					final boolean useManaBar = multiSelectedUnit.getMaximumMana() > 0;
					this.selectedUnitFrames[i].setManaBarVisible(useManaBar);
					if (useManaBar) {
						this.selectedUnitFrames[i].setManaRatioRemaining(
								multiSelectedUnit.getMana() / multiSelectedUnit.getMaximumMana());
					}
					if (focused) {
						this.selectedUnitFrames[i].showFocused(this.rootFrame, this.uiViewport);
						if (useManaBar) {
							this.selectedUnitHighlightBackdrop[i].setHeight(this.frontQueueIconWidth * 1.75f);
						}
						else {
							this.selectedUnitHighlightBackdrop[i].setHeight(this.frontQueueIconWidth * 1.55f);
						}
						this.selectedUnitHighlightBackdrop[i].positionBounds(this.rootFrame, this.uiViewport);
					}
					else {
						this.selectedUnitFrames[i].showUnFocused(this.rootFrame, this.uiViewport);
					}
				}
			}
			this.simpleInfoPanelBuildingDetail.setVisible(false);
			this.simpleInfoPanelItemDetail.setVisible(false);
			this.simpleInfoPanelDestructableDetail.setVisible(false);
			this.simpleInfoPanelUnitDetail.setVisible(false);
			this.simpleBuildingBuildTimeIndicator.setVisible(false);
			this.simpleBuildTimeIndicator.setVisible(false);
			this.simpleHeroLevelBar.setVisible(false);
			this.attack1Icon.setVisible(false);
			this.attack2Icon.setVisible(false);
			this.armorIcon.setVisible(false);
			this.heroInfoPanel.setVisible(false);
			this.selectWorkerInsideFrame.setVisible(false);
			this.smashBuffStatusBar.setVisible(false);
		}
		else {
			for (final QueueIcon queueIconFrame : this.queueIconFrames) {
				queueIconFrame.setVisible(false);
			}
			this.simpleInfoPanelBuildingDetail.setVisible(false);
			this.simpleInfoPanelItemDetail.setVisible(false);
			this.simpleInfoPanelDestructableDetail.setVisible(false);
			this.simpleInfoPanelUnitDetail.setVisible(!multiSelect);
			final boolean constructing = simulationUnit.isConstructingOrUpgrading();
			this.smashBuffStatusBar.setVisible(!multiSelect && !simulationUnit.isBuilding() && !constructing);
			final CAbilityCargoHold cargoData = simulationUnit.getCargoData();
			if ((cargoData != null) && !cargoData.isEmpty() && !multiSelect && !constructing) {
				final String unitTypeName = simulationUnit.getUnitType().getName();
				this.attack1Icon.setVisible(false);
				this.attack2Icon.setVisible(false);
				this.armorIcon.setVisible(false);
				this.heroInfoPanel.setVisible(false);
				this.simpleBuildTimeIndicator.setVisible(false);
				this.simpleBuildingBuildTimeIndicator.setVisible(false);
				this.selectWorkerInsideFrame.setVisible(false);

				this.simpleClassValue.setVisible(!simulationUnit.isBuilding());
				this.rootFrame.setText(this.simpleNameValue, unitTypeName);
				this.rootFrame.setText(this.simpleClassValue, "");
				this.simpleHeroLevelBar.setVisible(false);

				for (int i = 0; i < this.cargoUnitFrames.length; i++) {
					final boolean cargoCapacityPresent = i < cargoData.getCargoCapacity();
					final boolean cargoUnitPresent = i < cargoData.getCargoCount();
					this.cargoUnitFrames[i].setVisible(cargoUnitPresent);
					this.cargoBackdrop[i].setVisible(cargoCapacityPresent);
					if (cargoUnitPresent) {
						final CUnit cargoContainedUnit = cargoData.getUnit(i);
						final UnitIconUI unitUI = this.war3MapViewer.getAbilityDataUI()
								.getUnitUI(cargoContainedUnit.getTypeId());
						this.cargoUnitFrames[i].setTexture(unitUI.getIcon());
						if (cargoContainedUnit.isHero()) {
							this.cargoUnitFrames[i].setToolTip(cargoContainedUnit.getHeroData().getProperName());
							this.cargoUnitFrames[i]
									.setUberTip("Level " + cargoContainedUnit.getHeroData().getHeroLevel());
						}
						else {
							this.cargoUnitFrames[i].setToolTip(cargoContainedUnit.getUnitType().getName());
							this.cargoUnitFrames[i].setUberTip(unitUI.getUberTip());
						}
						this.cargoUnitFrames[i].setLifeRatioRemaining(
								cargoContainedUnit.getLife() / cargoContainedUnit.getMaximumLife());
						this.cargoUnitFrames[i].showFocused(this.rootFrame, this.uiViewport);
						final boolean manaBar = cargoContainedUnit.getMaximumMana() > 0;
						this.cargoUnitFrames[i].setManaBarVisible(manaBar);
						if (manaBar) {
							this.cargoUnitFrames[i].setManaRatioRemaining(
									cargoContainedUnit.getMana() / cargoContainedUnit.getMaximumMana());
						}
					}
					else {
						this.cargoUnitFrames[i].setTexture(null);
					}
				}
			}
			else {
				for (int i = 0; i < this.cargoUnitFrames.length; i++) {
					this.cargoUnitFrames[i].setVisible(false);
				}
				final CUnitType unitType = simulationUnit.getUnitType();
				final String unitTypeName = unitType.getName();

				final boolean anyAttacks = simulationUnit.getCurrentAttacks().size() > 0;
				final UIFrame localArmorIcon = this.armorIcon;
				final TextureFrame localArmorIconBackdrop = this.armorIconBackdrop;
				final StringFrame localArmorInfoPanelIconValue = this.armorInfoPanelIconValue;
				final StringFrame localArmorInfoPanelIconLevel = this.armorInfoPanelIconLevel;
				if (anyAttacks && !constructing) {
					War3ID weaponUpgradeId = unitType.getUpgradeClassToType().get(CUpgradeClass.MELEE);
					if (weaponUpgradeId == null) {
						weaponUpgradeId = unitType.getUpgradeClassToType().get(CUpgradeClass.RANGED);
						if (weaponUpgradeId == null) {
							weaponUpgradeId = unitType.getUpgradeClassToType().get(CUpgradeClass.ARTILLERY);
						}
					}
					final boolean weaponUpgradeLevelVisible = weaponUpgradeId != null;
					final InfoPanelIconBackdrops damageBackdrops = weaponUpgradeLevelVisible ? this.damageBackdrops
							: this.damageBackdropsNeutral;
					final CUnitAttack attackOne = simulationUnit.getCurrentAttacks().get(0);
					this.attack1Icon.setVisible(true);// attackOne.isShowUI());
					this.attack1IconBackdrop.setTexture(damageBackdrops.getTexture(attackOne.getAttackType()));
					String attackOneDmgText = attackOne.getMinDamageDisplay() + " - " + attackOne.getMaxDamageDisplay();
					final int attackOneTemporaryDamageBonus = attackOne.getTotalTemporaryDamageBonus();
					if (attackOneTemporaryDamageBonus != 0) {
						attackOneDmgText += (attackOneTemporaryDamageBonus > 0 ? "|cFF00FF00 +" : "|cFFFF0000 ")
								+ attackOneTemporaryDamageBonus + "";
					}
					this.rootFrame.setText(this.attack1InfoPanelIconValue, attackOneDmgText);
					this.attack1InfoPanelIconLevel.setVisible(weaponUpgradeLevelVisible);
					if (weaponUpgradeLevelVisible) {
						this.rootFrame.setText(this.attack1InfoPanelIconLevel,
								Integer.toString(
										this.war3MapViewer.simulation.getPlayer(simulationUnit.getPlayerIndex())
												.getTechtreeUnlocked(weaponUpgradeId)));
					}
					if (simulationUnit.getCurrentAttacks().size() > 1) {
						final CUnitAttack attackTwo = simulationUnit.getCurrentAttacks().get(1);
						this.attack2Icon.setVisible(attackTwo.isShowUI());
						this.attack2IconBackdrop.setTexture(damageBackdrops.getTexture(attackTwo.getAttackType()));
						String attackTwoDmgText = attackTwo.getMinDamageDisplay() + " - "
								+ attackTwo.getMaxDamageDisplay();
						final int attackTwoTemporaryDamageBonus = attackTwo.getTotalTemporaryDamageBonus();
						if (attackTwoTemporaryDamageBonus != 0) {
							attackTwoDmgText += (attackTwoTemporaryDamageBonus > 0 ? "|cFF00FF00 +" : "|cFFFF0000 ")
									+ attackTwoTemporaryDamageBonus + "";
						}
						this.rootFrame.setText(this.attack2InfoPanelIconValue, attackTwoDmgText);
						this.attack2InfoPanelIconLevel.setVisible(weaponUpgradeLevelVisible);
						if (weaponUpgradeLevelVisible) {
							this.rootFrame.setText(this.attack2InfoPanelIconLevel,
									Integer.toString(
											this.war3MapViewer.simulation.getPlayer(simulationUnit.getPlayerIndex())
													.getTechtreeUnlocked(weaponUpgradeId)));
						}
					}
					else {
						this.attack2Icon.setVisible(false);
					}

					this.smashArmorIconWrapper.addSetPoint(
							new SetPoint(FramePoint.TOPLEFT, this.simpleInfoPanelUnitDetail, FramePoint.TOPLEFT,
									GameUI.convertX(this.uiViewport, 0f), GameUI.convertY(this.uiViewport, -0.0705f)));
					this.smashArmorIconWrapper.positionBounds(this.rootFrame, this.uiViewport);
					this.armorIcon.positionBounds(this.rootFrame, this.uiViewport);
				}
				else {
					this.attack1Icon.setVisible(false);
					this.attack2Icon.setVisible(false);

					this.smashArmorIconWrapper.addSetPoint(
							new SetPoint(FramePoint.TOPLEFT, this.simpleInfoPanelUnitDetail, FramePoint.TOPLEFT,
									GameUI.convertX(this.uiViewport, 0f), GameUI.convertY(this.uiViewport, -0.040f)));
					this.smashArmorIconWrapper.positionBounds(this.rootFrame, this.uiViewport);
					this.armorIcon.positionBounds(this.rootFrame, this.uiViewport);
				}

				final CAbilityHero heroData = simulationUnit.getHeroData();
				final boolean hero = heroData != null;
				this.heroInfoPanel.setVisible(hero);
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
					this.primaryAttributeIcon.setTexture(iconKey, this.rootFrame);

					this.rootFrame.setText(this.strengthValue, heroData.getStrength().getDisplayText());
					this.rootFrame.setText(this.agilityValue, heroData.getAgility().getDisplayText());
					this.rootFrame.setText(this.intelligenceValue, heroData.getIntelligence().getDisplayText());
					final String infopanelLevelClass = this.rootFrame.getTemplates()
							.getDecoratedString("INFOPANEL_LEVEL_CLASS").replace("%u", "%d"); // :(
					final int heroLevel = heroData.getHeroLevel();
					this.simpleClassValue.setVisible(true);
					this.rootFrame.setText(this.simpleClassValue,
							String.format(infopanelLevelClass, heroLevel, unitTypeName));
					this.rootFrame.setText(this.simpleNameValue, heroData.getProperName());
					this.simpleHeroLevelBar.setVisible(true);
					final CGameplayConstants gameplayConstants = this.war3MapViewer.simulation.getGameplayConstants();
					this.simpleHeroLevelBar
							.setValue((heroData.getXp() - gameplayConstants.getNeedHeroXPSum(heroLevel - 1))
									/ (float) gameplayConstants.getNeedHeroXP(heroLevel));
				}
				else {
					this.simpleClassValue.setVisible(!simulationUnit.isBuilding());
					this.rootFrame.setText(this.simpleNameValue, unitTypeName);
					String classText = null;
					for (final CUnitClassification classification : simulationUnit.getClassifications()) {
						if (classification.getDisplayName() != null) {
							classText = classification.getDisplayName();
						}
					}
					if (classText != null) {
						this.rootFrame.setText(this.simpleClassValue, classText);
					}
					else {
						this.rootFrame.setText(this.simpleClassValue, "");
					}
					this.simpleHeroLevelBar.setVisible(false);
				}

				localArmorIcon.setVisible(!constructing);
				this.simpleBuildTimeIndicator.setVisible(constructing);
				this.simpleBuildingBuildTimeIndicator.setVisible(false);
				if (constructing) {
					War3ID constructingTypeId = simulationUnit.getTypeId();
					if (simulationUnit.isUpgrading()) {
						constructingTypeId = simulationUnit.getUpgradeIdType();
					}

					this.rootFrame.setText(this.simpleBuildingActionLabel,
							this.rootFrame.getTemplates().getDecoratedString("CONSTRUCTING"));
					this.queueIconFrames[0].setVisible(true);
					final UnitIconUI constructingUnitUI = this.war3MapViewer.getAbilityDataUI()
							.getUnitUI(constructingTypeId);
					this.queueIconFrames[0].setTexture(constructingUnitUI.getIcon());
					this.queueIconFrames[0].setToolTip(constructingUnitUI.getToolTip());
					this.queueIconFrames[0].setUberTip(constructingUnitUI.getUberTip());
					if ((simulationUnit.getWorker() != null) && !simulationUnit.isConstructionConsumesWorker()
							&& !simulationUnit.isConstructingPaused()) {
						this.selectWorkerInsideFrame.setVisible(true);
						this.selectWorkerInsideFrame.setTexture(this.war3MapViewer.getAbilityDataUI()
								.getUnitUI(simulationUnit.getWorker().getTypeId()).getIcon());
					}
					else {
						this.selectWorkerInsideFrame.setVisible(false);
					}
				}
				else {
					this.rootFrame.setText(this.simpleBuildingActionLabel, "");
					this.selectWorkerInsideFrame.setVisible(false);
				}
				final War3ID armorUpgradeId = unitType.getUpgradeClassToType().get(CUpgradeClass.ARMOR);
				final boolean armorUpgradeLevelVisible = armorUpgradeId != null;
				final Texture defenseTexture = (armorUpgradeLevelVisible ? this.defenseBackdrops
						: this.defenseBackdropsNeutral).getTexture(simulationUnit.getDefenseType());
				if (defenseTexture == null) {
					throw new RuntimeException(simulationUnit.getDefenseType() + " can't find texture!");
				}
				localArmorIconBackdrop.setTexture(defenseTexture);

				String defenseDisplayString;
				if (simulationUnit.isInvulnerable()) {
					defenseDisplayString = this.rootFrame.getTemplates().getDecoratedString("INVULNERABLE");
				}
				else {
					defenseDisplayString = Integer.toString(simulationUnit.getCurrentDefenseDisplay());
					final float temporaryDefenseBonus = simulationUnit.getTotalTemporaryDefenseBonus();
					if (temporaryDefenseBonus != 0) {
						if (temporaryDefenseBonus > 0) {
							defenseDisplayString += "|cFF00FF00 +" + String.format("%.1f", temporaryDefenseBonus) + "";
						}
						else {
							defenseDisplayString += "|cFFFF0000 " + String.format("%.1f", temporaryDefenseBonus) + "";
						}
					}
				}
				this.rootFrame.setText(localArmorInfoPanelIconValue, defenseDisplayString);
				localArmorInfoPanelIconLevel.setVisible(armorUpgradeLevelVisible);
				if (armorUpgradeLevelVisible) {
					this.rootFrame.setText(localArmorInfoPanelIconLevel, Integer.toString(this.war3MapViewer.simulation
							.getPlayer(simulationUnit.getPlayerIndex()).getTechtreeUnlocked(armorUpgradeId)));
				}
			}
		}
		CAbilityInventory inventory = simulationUnit.getInventoryData();
		boolean inventoryEnabled = simulationUnit.getPlayerIndex() == this.war3MapViewer.getLocalPlayerIndex();
		if (inventory == null) {
			final CAbilityNeutralBuilding neutralBuildingData = simulationUnit.getNeutralBuildingData();
			if (neutralBuildingData != null) {
				final CUnit selectedPlayerUnit = neutralBuildingData
						.getSelectedPlayerUnit(this.war3MapViewer.getLocalPlayerIndex());
				if (selectedPlayerUnit != null) {
					inventory = selectedPlayerUnit.getInventoryData();
					inventoryEnabled = false;
				}
			}
		}
		this.inventoryCover.setVisible(inventory == null);
		this.activationReceiverPreviewCallback.setup(this.war3MapViewer.simulation.getUnitData(),
				this.war3MapViewer.simulation.getUpgradeData(), this.rootFrame.getTemplates());
		if (inventory != null) {
			this.inventoryBarFrame.setVisible(true);
			int index = 0;
			for (int i = 0; i < INVENTORY_HEIGHT; i++) {
				for (int j = 0; j < INVENTORY_WIDTH; j++) {
					final CommandCardIcon inventoryIcon = this.inventoryIcons[i][j];
					final CItem item = inventory.getItemInSlot(index);
					if (item != null) {
						final ItemUI itemUI = this.war3MapViewer.getAbilityDataUI().getItemUI(item.getTypeId());
						final IconUI iconUI = itemUI.getIconUI();
						final CItemType itemType = item.getItemType();
						// TODO: below we set menu=false, this is bad, item should be based on item abil
						final boolean activelyUsed = itemType.isActivelyUsed() && inventoryEnabled;
						final boolean pawnable = item.isPawnable();
						final String uberTip = iconUI.getUberTip();
						this.recycleStringBuilder.setLength(0);
						if (pawnable) {
							this.recycleStringBuilder
									.append(this.rootFrame.getTemplates().getDecoratedString("ITEM_PAWN_TOOLTIP"));
							this.recycleStringBuilder.append("|n");
						}
						if (activelyUsed) {
							this.recycleStringBuilder
									.append(this.rootFrame.getTemplates().getDecoratedString("ITEM_USE_TOOLTIP"));
							this.recycleStringBuilder.append("|n");
						}
						inventory.checkCanUse(this.war3MapViewer.simulation, this.selectedUnit.getSimulationUnit(),
								OrderIds.itemuse00 + index, this.activationReceiverPreviewCallback.reset());
						this.recycleStringBuilder.append(uberTip);
						inventoryIcon.setCommandButtonData(
								inventoryEnabled ? iconUI.getIcon() : iconUI.getIconDisabled(), 0,
								activelyUsed ? OrderIds.itemuse00 + index : 0, index + 1, activelyUsed, false, false,
								itemUI.getName(), this.recycleStringBuilder.toString(), '\0',
								(int) StrictMath.ceil(itemType.getGoldCost()
										* this.war3MapViewer.simulation.getGameplayConstants().getPawnItemRate()),
								itemType.getLumberCost(), 0, 0, false,
								this.activationReceiverPreviewCallback.getCooldownRemaining(),
								this.activationReceiverPreviewCallback.getCooldownMax(),
								item.getCharges() > 0 ? item.getCharges() : -1);
					}
					else {
						if (index >= inventory.getItemCapacity()) {
							inventoryIcon.setCommandButtonData(this.consoleInventoryNoCapacityTexture, 0, 0, 0, false,
									false, false, null, null, '\0', 0, 0, 0, 0, false, 0, 0, -1);
						}
						else {
							if (this.draggingItem != null) {
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
		updateBuildTimeIndicators();
	}

	private void clearCommandCard() {
		for (int j = 0; j < COMMAND_CARD_HEIGHT; j++) {
			for (int i = 0; i < COMMAND_CARD_WIDTH; i++) {
				this.commandCard[j][i].clear();
			}
		}
		for (int i = 0; i < BUFF_DISPLAY_MAX; i++) {
			this.buffBarIcons[i].clear();
		}
		this.currentBuffBarIconIndex = 0;
	}

	@Override
	public void commandButton(final int buttonPositionX, final int buttonPositionY, final Texture icon,
			final int abilityHandleId, final int orderId, final int autoCastId, final boolean active,
			final boolean autoCastActive, final boolean menuButton, final String tip, final String uberTip,
			final char hotkey, final int goldCost, final int lumberCost, final int foodCost, final int manaCost,
			final float cooldownRemaining, final float cooldownMax, final int numberOverlay) {
		if ((buttonPositionX == -11) || (buttonPositionY == -11)) {
			// NOTE some guys said they liked to do this as a hack to hide icons or whatever
			return;
		}
		int x = Math.max(0, Math.min(COMMAND_CARD_WIDTH - 1, buttonPositionX));
		int y = Math.max(0, Math.min(COMMAND_CARD_HEIGHT - 1, buttonPositionY));
		while ((x >= 0) && (y >= 0) && this.commandCard[y][x].isVisible()) {
			x--;
			if ((x < 0) && (y != 0)) {
				x = COMMAND_CARD_WIDTH - 1;
				y--;
			}
		}
		if ((y < 0) || (x < 0)) {
			if (y < 0) {
				y = 0;
			}
			if (x < 0) {
				x = 0;
			}
			while ((x < COMMAND_CARD_WIDTH) && (y < COMMAND_CARD_HEIGHT) && this.commandCard[y][x].isVisible()) {
				x++;
				if (x >= COMMAND_CARD_WIDTH) {
					x = 0;
					y++;
				}
			}
		}
		if ((x < COMMAND_CARD_WIDTH) && (x >= 0) && (y < COMMAND_CARD_HEIGHT) && (y >= 0)) {
			this.commandCard[y][x].setCommandButtonData(icon, abilityHandleId, orderId, autoCastId, active,
					autoCastActive, menuButton, tip, uberTip, hotkey, goldCost, lumberCost, foodCost, manaCost,
					this.selectedUnit.getSimulationUnit().getMana() < manaCost, cooldownRemaining, cooldownMax,
					numberOverlay);
		}
	}

	@Override
	public void buff(final Texture icon, final int level, final String tip, final String uberTip) {
		if ((this.selectedUnit == null) || !this.selectedUnit.getSimulationUnit().isBuilding()) {
			if (this.currentBuffBarIconIndex < this.buffBarIcons.length) {
				this.buffBarIcons[this.currentBuffBarIconIndex++].set(icon, tip, uberTip);
			}
		}
	}

	@Override
	public void timedLifeBar(final int level, final String toolTip, final float durationRemaining,
			final float durationMax) {
		this.simpleProgressIndicatorDurationRemaining = durationRemaining;
		this.simpleProgressIndicatorDurationMax = durationMax;
		this.simpleProgressIndicator.setVisible(true);
		this.simpleProgressIndicator.setValue(durationRemaining / durationMax);
		this.simpleClassValue.setVisible(true);
		this.rootFrame.setText(this.simpleClassValue, toolTip);
	}

	@Override
	public void resize(final int width, final int height) {
		this.cameraManager.resize(setupWorldFrameViewport(width, height));
		positionPortrait();
		if (this.rootFrame.isAutoPosition()) {
			this.rootFrame.positionBounds(this.rootFrame, this.uiViewport);
		}
	}

	private Rectangle setupWorldFrameViewport(final int width, final int height) {
		this.tempRect.x = 0;
		this.tempRect.width = width;
		final float topHeight = 0.02666f * height;
		final float bottomHeight = 0.21333f * height;
		this.tempRect.y = (int) bottomHeight;
		this.tempRect.height = height - (int) (topHeight + bottomHeight);
		return this.tempRect;
	}

	public void positionPortrait() {
		if (this.cinematicPanel.isVisible()) {
			this.portrait.portraitScene.show = false;
		}
		else {
			this.portrait.portraitScene.show = true;
			this.projectionTemp1.x = 422 * this.widthRatioCorrection;
			this.projectionTemp1.y = 57 * this.heightRatioCorrection;
			this.projectionTemp2.x = (422 + 167) * this.widthRatioCorrection;
			this.projectionTemp2.y = (57 + 170) * this.heightRatioCorrection;
			this.uiViewport.project(this.projectionTemp1);
			this.uiViewport.project(this.projectionTemp2);
		}

		this.tempRect.x = this.projectionTemp1.x + this.uiViewport.getScreenX();
		this.tempRect.y = this.projectionTemp1.y + this.uiViewport.getScreenY();
		this.tempRect.width = this.projectionTemp2.x - this.projectionTemp1.x;
		this.tempRect.height = this.projectionTemp2.y - this.projectionTemp1.y;
		this.portrait.portraitScene.camera.viewport(this.tempRect);
	}

	private static final class InfoPanelIconBackdrops {
		private final Texture[] damageBackdropTextures;

		public InfoPanelIconBackdrops(final CodeKeyType[] attackTypes, final GameUI gameUI, final String prefix,
				final String suffix) {
			this.damageBackdropTextures = new Texture[attackTypes.length];
			for (int index = 0; index < attackTypes.length; index++) {
				final CodeKeyType attackType = attackTypes[index];
				String skinLookupKey = "InfoPanelIcon" + prefix + attackType.getCodeKey() + suffix;
				if (!gameUI.hasSkinField(skinLookupKey) && (attackType == CAttackType.SPELLS)) {
					skinLookupKey = "InfoPanelIcon" + prefix + CAttackType.MAGIC.getCodeKey() + suffix;
				}
				final Texture suffixTexture = gameUI.loadTexture(gameUI.getSkinField(skinLookupKey));
				if (suffixTexture != null) {
					this.damageBackdropTextures[index] = suffixTexture;
				}
				else {
					skinLookupKey = "InfoPanelIcon" + prefix + attackType.getCodeKey();
					if (!gameUI.hasSkinField(skinLookupKey) && (attackType == CAttackType.SPELLS)) {
						skinLookupKey = "InfoPanelIcon" + prefix + CAttackType.MAGIC.getCodeKey();
					}
					this.damageBackdropTextures[index] = gameUI.loadTexture(gameUI.getSkinField(skinLookupKey));
				}
			}
		}

		public Texture getTexture(final CodeKeyType attackType) {
			if (attackType != null) {
				final int ordinal = attackType.ordinal();
				if ((ordinal >= 0) && (ordinal < this.damageBackdropTextures.length)) {
					return this.damageBackdropTextures[ordinal];
				}
			}
			return this.damageBackdropTextures[0];
		}
	}

	@Override
	public void lifeChanged() {
		if (this.selectedUnit == null) {
			return;
		}
		if (this.selectedUnit.getSimulationUnit().isDead()) {
			removeSubGroupHighlightSelectedUnitFromSelection();
		}
		else {
			final float lifeRatioRemaining = this.selectedUnit.getSimulationUnit().getLife()
					/ this.selectedUnit.getSimulationUnit().getMaxLife();
			this.rootFrame.setText(this.unitLifeText,
					FastNumberFormat.formatWholeNumber(this.selectedUnit.getSimulationUnit().getLife()) + " / "
							+ FastNumberFormat.formatWholeNumber(this.selectedUnit.getSimulationUnit().getMaxLife()));
			this.unitLifeText.setColor(new Color(Math.min(1.0f, 2.0f - (lifeRatioRemaining * 2)),
					Math.min(1.0f, lifeRatioRemaining * 2), 0, 1.0f));
		}
	}

	@Override
	public void hideStateChanged() {
		if (this.selectedUnit == null) {
			return;
		}
		if (this.selectedUnit.getSimulationUnit().isHidden() || !this.selectedUnit.getSimulationUnit()
				.isVisible(this.war3MapViewer.simulation, this.war3MapViewer.getLocalPlayerIndex())) {
			removeSubGroupHighlightSelectedUnitFromSelection();
		}
	}

	private void removeSubGroupHighlightSelectedUnitFromSelection() {
		final RenderUnit preferredSelectionReplacement = this.selectedUnit.getPreferredSelectionReplacement();
		final List<RenderWidget> newSelection;
		newSelection = new ArrayList<>(this.selectedUnits);
		newSelection.remove(this.selectedUnit);
		if (preferredSelectionReplacement != null) {
			newSelection.add(preferredSelectionReplacement);
		}
		selectWidgets(newSelection);
		this.war3MapViewer.doSelectUnit(newSelection);

		// clear active commands
		this.activeCommandUnit = null;
		this.activeCommand = null;
		this.activeCommandOrderId = -1;
		if (this.draggingItem != null) {
			setDraggingItem(null);
		}
	}

	@Override
	public void manaChanged() {
		final int maximumMana = this.selectedUnit.getSimulationUnit().getMaximumMana();
		if (maximumMana > 0) {
			this.rootFrame.setText(this.unitManaText,
					FastNumberFormat.formatWholeNumber(this.selectedUnit.getSimulationUnit().getMana()) + " / "
							+ maximumMana);
		}
		else {
			this.rootFrame.setText(this.unitManaText, "");
		}
	}

	@Override
	public void goldChanged() {
		this.rootFrame.setText(this.resourceBarGoldText, Integer.toString(this.localPlayer.getGold()));
	}

	@Override
	public void lumberChanged() {
		this.rootFrame.setText(this.resourceBarLumberText, Integer.toString(this.localPlayer.getLumber()));
	}

	@Override
	public void foodChanged() {
		final int foodCap = this.localPlayer.getFoodCap();
		if (foodCap == 0) {
			this.rootFrame.setText(this.resourceBarSupplyText, Integer.toString(this.localPlayer.getFoodUsed()));
			this.resourceBarSupplyText.setColor(Color.WHITE);
		}
		else {
			this.rootFrame.setText(this.resourceBarSupplyText, this.localPlayer.getFoodUsed() + "/" + foodCap);
			this.resourceBarSupplyText.setColor(this.localPlayer.getFoodUsed() > foodCap ? Color.RED : Color.WHITE);
		}
	}

	@Override
	public void upkeepChanged() {
		this.rootFrame.setText(this.resourceBarUpkeepText, "Upkeep NYI");
		this.resourceBarUpkeepText.setColor(Color.CYAN);
	}

	@Override
	public void heroDeath() {
		if (this.selectedUnit != null) {
			if (this.selectedUnit.getSimulationUnit().getUnitType().isRevivesHeroes()) {
				reloadSelectedUnitUI(this.selectedUnit);
			}
		}
	}

	@Override
	public void heroTokensChanged() {
		// TODO Auto-generated method stub
	}

	@Override
	public void ordersChanged() {
		reloadSelectedUnitUI(this.selectedUnit);
		if (this.mouseOverUIFrame instanceof ClickableActionFrame) {
			loadTooltip((ClickableActionFrame) this.mouseOverUIFrame);
		}
	}

	@Override
	public void heroStatsChanged() {
		reloadSelectedUnitUI(this.selectedUnit);
	}

	@Override
	public void attacksChanged() {
		reloadSelectedUnitUI(this.selectedUnit);
	}

	@Override
	public void abilitiesChanged() {
		reloadSelectedUnitUI(this.selectedUnit);
	}

	@Override
	public void inventoryChanged() {
		reloadSelectedUnitUI(this.selectedUnit);
	}

	@Override
	public void queueChanged() {
		reloadSelectedUnitUI(this.selectedUnit);
	}

	private void clearAndRepopulateCommandCard() {
		clearCommandCard();
		final AbilityDataUI abilityDataUI = this.war3MapViewer.getAbilityDataUI();
		final int menuOrderId = getSubMenuOrderId();
		if ((this.activeCommand != null) && (this.draggingItem == null)) {
			final IconUI cancelUI = abilityDataUI.getCancelUI();
			commandButton(cancelUI.getButtonPositionX(), cancelUI.getButtonPositionY(), cancelUI.getIcon(), 0,
					menuOrderId, 0, false, false, true, cancelUI.getToolTip(), cancelUI.getUberTip(),
					cancelUI.getHotkey(), 0, 0, 0, 0, 0, 0, -1);
		}
		else {
			if (menuOrderId != 0) {
				final int exitOrderId = this.subMenuOrderIdStack.size() > 1
						? this.subMenuOrderIdStack.get(this.subMenuOrderIdStack.size() - 2)
						: 0;
				final IconUI cancelUI = abilityDataUI.getCancelUI();
				commandButton(cancelUI.getButtonPositionX(), cancelUI.getButtonPositionY(), cancelUI.getIcon(), 0,
						exitOrderId, 0, false, false, true, cancelUI.getToolTip(), cancelUI.getUberTip(),
						cancelUI.getHotkey(), 0, 0, 0, 0, 0, 0, -1);
			}
			this.selectedUnit.populateCommandCard(this.war3MapViewer.simulation, this.rootFrame, this, abilityDataUI,
					menuOrderId, this.selectedUnits.size() > 1, this.war3MapViewer.getLocalPlayerIndex());
		}
	}

	private int getSubMenuOrderId() {
		return this.subMenuOrderIdStack.isEmpty() ? 0
				: this.subMenuOrderIdStack.get(this.subMenuOrderIdStack.size() - 1);
	}

	public RenderUnit getSelectedUnit() {
		return this.selectedUnit;
	}

	@Override
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
		if (keycode == Input.Keys.ESCAPE) {
			this.unitOrderListener.issueGuiPlayerEvent(JassGameEventsWar3.EVENT_PLAYER_END_CINEMATIC.getEventId());
			return true;
		}
		if (!this.userControlEnabled) {
			return false;
		}
		if (keycode == Input.Keys.TAB) {
			if (this.selectedUnits.size() > 1) {
				advanceSelectedSubGroup();
				this.war3MapViewer.getUiSounds().getSound("SubGroupSelectionChange").play(this.uiScene.audioContext, 0,
						0, 0);
				return true;
			}
		}
		final String keyString = Input.Keys.toString(keycode);
		final char c = keyString.length() == 1 ? keyString.charAt(0) : ' ';
		for (int j = 0; j < COMMAND_CARD_HEIGHT; j++) {
			for (int i = 0; i < COMMAND_CARD_WIDTH; i++) {
				boolean match = false;
				switch (WarsmashConstants.INPUT_HOTKEY_MODE) {
				case 0:
					if (this.commandCard[j][i].checkHotkey(c, keycode)) {
						match = true;
					}
					break;
				case 1:

					if (keycode == this.commandCardGridHotkeys[j][i]) {
						match = true;
					}
					break;
				}
				if (match) {
					this.commandCard[j][i].onClick(Input.Buttons.LEFT);
					this.war3MapViewer.getUiSounds().getSound("InterfaceClick").play(this.uiScene.audioContext, 0, 0,
							0);
					return true;
				}
			}
		}
		return this.cameraManager.keyDown(keycode);
	}

	@Override
	public boolean keyUp(final int keycode) {
		if (!this.userControlEnabled) {
			return false;
		}
		return this.cameraManager.keyUp(keycode);
	}

	@Override
	public boolean scrolled(final float amountX, final float amountY) {
		if (!this.userControlEnabled) {
			return false;
		}
		this.cameraManager.scrolled((int) amountY);
		return false;
	}

	@Override
	public boolean touchDown(final int screenX, final int screenY, final float worldScreenY, final int button) {
		if (!this.userControlEnabled) {
			return false;
		}
		this.allowDrag = false;
		if (button == Input.Buttons.FORWARD) {
			if (this.selectedUnits.size() > 1) {
				advanceSelectedSubGroup();
				this.war3MapViewer.getUiSounds().getSound("SubGroupSelectionChange").play(this.uiScene.audioContext, 0,
						0, 0);
			}
			return false;
		}
		else if (button == Input.Buttons.BACK) {
			if (this.selectedUnits.size() > 1) {
				advanceSelectedSubGroupReverse();
				this.war3MapViewer.getUiSounds().getSound("SubGroupSelectionChange").play(this.uiScene.audioContext, 0,
						0, 0);
			}
			return false;
		}
		screenCoordsVector.set(screenX, screenY);
		this.uiViewport.unproject(screenCoordsVector);
		if (this.meleeUIMinimap.containsMouse(screenCoordsVector.x, screenCoordsVector.y)) {
			final Vector2 worldPoint = this.meleeUIMinimap.getWorldPointFromScreen(screenCoordsVector.x,
					screenCoordsVector.y);
			this.cameraManager.target.x = worldPoint.x;
			this.cameraManager.target.y = worldPoint.y;
			return true;
		}
		final UIFrame clickedUIFrame = this.rootFrame.touchDown(screenCoordsVector.x, screenCoordsVector.y, button);
		if (clickedUIFrame == null) {
			// try to interact with world
			if (this.activeCommand != null) {
				if (button == Input.Buttons.RIGHT) {
					this.activeCommandUnit = null;
					this.activeCommand = null;
					this.activeCommandOrderId = -1;
					if (this.draggingItem != null) {
						setDraggingItem(null);
					}
					clearAndRepopulateCommandCard();
				}
				else if (button == Input.Buttons.LEFT) {
					final boolean shiftDown = isShiftDown();
					this.activeCommandUnitTargetFilter.reset();
					final RenderWidget rayPickUnit = this.war3MapViewer.rayPickUnit(screenX, worldScreenY,
							this.activeCommandUnitTargetFilter);
					if (rayPickUnit != null) {
						useActiveCommandOnUnit(shiftDown, rayPickUnit);
					}
					else {
						this.war3MapViewer.getClickLocation(clickLocationTemp, screenX, (int) worldScreenY,
								this.activeCommandUnit.getSimulationUnit().isMovementOnWaterAllowed(), true);
						clickLocationTemp2.set(clickLocationTemp.x, clickLocationTemp.y);

						if (this.draggingItem != null) {
							this.war3MapViewer.showConfirmation(clickLocationTemp, 0, 1, 0);

							this.unitOrderListener.issueDropItemAtPointOrder(
									this.activeCommandUnit.getSimulationUnit().getHandleId(),
									this.activeCommand.getHandleId(), this.activeCommandOrderId,
									this.draggingItem.getHandleId(), clickLocationTemp2.x, clickLocationTemp2.y,
									shiftDown);
							if (getSelectedUnit().soundset.yes
									.playUnitResponse(this.war3MapViewer.worldScene.audioContext, getSelectedUnit())) {
								portraitTalk(getSelectedUnit().soundset.yes);
							}
							this.activeCommandUnit = null;
							this.activeCommand = null;
							this.activeCommandOrderId = -1;
							setDraggingItem(null);
							clearAndRepopulateCommandCard();
						}
						else {
							final ExternStringMsgTargetCheckReceiver<AbilityPointTarget> pointTargetReceiver = ExternStringMsgTargetCheckReceiver
									.getInstance();
							pointTargetReceiver.reset();
							this.activeCommand.checkCanTarget(this.war3MapViewer.simulation,
									this.activeCommandUnit.getSimulationUnit(), this.activeCommandOrderId,
									clickLocationTemp2, pointTargetReceiver);
							final Vector2 target = pointTargetReceiver.getTarget();
							if (target != null) {
								if ((this.activeCommand instanceof CAbilityAttack)
										&& (this.activeCommandOrderId == OrderIds.attack)) {
									this.war3MapViewer.showConfirmation(clickLocationTemp, 1, 0, 0);
								}
								else {
									this.war3MapViewer.showConfirmation(clickLocationTemp, 0, 1, 0);
								}
								this.unitOrderListener.issuePointOrder(
										this.activeCommandUnit.getSimulationUnit().getHandleId(),
										this.activeCommand.getHandleId(), this.activeCommandOrderId,
										clickLocationTemp2.x, clickLocationTemp2.y, shiftDown);
								if (this.selectedUnits.size() > 1) {
									for (final RenderUnit otherSelectedUnit : this.selectedUnits) {
										if (otherSelectedUnit != this.activeCommandUnit) {
											CAbility abilityToUse = null;
											AbilityPointTarget targetToUse = null;
											for (final CAbility ability : otherSelectedUnit.getSimulationUnit()
													.getAbilities()) {
												final PointAbilityTargetCheckReceiver receiver = PointAbilityTargetCheckReceiver.INSTANCE
														.reset();
												ability.checkCanTarget(this.war3MapViewer.simulation,
														otherSelectedUnit.getSimulationUnit(),
														this.activeCommandOrderId, clickLocationTemp2, receiver);
												if (receiver.getTarget() != null) {
													abilityToUse = ability;
													targetToUse = receiver.getTarget();
												}
											}
											if (abilityToUse != null) {
												this.unitOrderListener.issuePointOrder(
														otherSelectedUnit.getSimulationUnit().getHandleId(),
														abilityToUse.getHandleId(), this.activeCommandOrderId,
														targetToUse.getX(), targetToUse.getY(), shiftDown);
											}
										}
									}
								}
								if (getSelectedUnit().soundset.yes.playUnitResponse(
										this.war3MapViewer.worldScene.audioContext, getSelectedUnit())) {
									portraitTalk(getSelectedUnit().soundset.yes);
								}
								this.selectedSoundCount = 0;
								if (this.activeCommand instanceof AbstractCAbilityBuild) {
									this.war3MapViewer.getUiSounds().getSound("PlaceBuildingDefault")
											.play(this.uiScene.audioContext, 0, 0, 0);
								}
								else if (this.activeCommand instanceof CAbilityRally) {
									this.war3MapViewer.getUiSounds().getSound("RallyPointPlace")
											.play(this.uiScene.audioContext, 0, 0, 0);
								}
								if (!shiftDown) {
									this.subMenuOrderIdStack.clear();
									this.activeCommandUnit = null;
									this.activeCommand = null;
									this.activeCommandOrderId = -1;
									clearAndRepopulateCommandCard();
								}

							}
							else {
								if ((this.activeCommandUnitTargetFilter.lastFailureMessage != null)
										&& !this.activeCommandUnitTargetFilter.lastFailureMessage.isEmpty()) {
									showInterfaceError(this.activeCommandUnit.getSimulationUnit().getPlayerIndex(),
											this.activeCommandUnitTargetFilter.lastFailureMessage);
								}
								else {
									final String externStringKey = pointTargetReceiver.getExternStringKey();
									if ((externStringKey != null) && !externStringKey.isEmpty()) {
										showInterfaceError(this.activeCommandUnit.getSimulationUnit().getPlayerIndex(),
												externStringKey);
									}
									else {
										showInterfaceError(this.activeCommandUnit.getSimulationUnit().getPlayerIndex(),
												CommandStringErrorKeys.MUST_TARGET_A_UNIT_WITH_THIS_ACTION);
									}
								}
							}
						}

					}
				}
			}
			else {
				if (button == Input.Buttons.RIGHT) {
					if ((getSelectedUnit() != null) && (getSelectedUnit().getSimulationUnit()
							.getPlayerIndex() == this.war3MapViewer.getLocalPlayerIndex())) {
						RenderWidget rayPickUnit = this.war3MapViewer.rayPickUnit(screenX, worldScreenY,
								this.anyClickableUnitFilter);
						if (rayPickUnit == null) {
							rayPickUnit = this.war3MapViewer.rayPickUnit(screenX, worldScreenY,
									this.anyTargetableUnitFilter);
						}
						if (rayPickUnit != null) {
							boolean ordered = false;
							boolean rallied = false;
							boolean attacked = false;
							for (final RenderUnit unit : this.selectedUnits) {
								CAbility abilityToUse = null;
								CWidget targetToUse = null;
								for (final CAbility ability : unit.getSimulationUnit().getAbilities()) {
									ability.checkCanTarget(this.war3MapViewer.simulation, unit.getSimulationUnit(),
											OrderIds.smart, rayPickUnit.getSimulationWidget(),
											CWidgetAbilityTargetCheckReceiver.INSTANCE);
									final CWidget targetWidget = CWidgetAbilityTargetCheckReceiver.INSTANCE.getTarget();
									if (targetWidget != null) {
										abilityToUse = ability;
										targetToUse = targetWidget;
									}
								}
								if (abilityToUse != null) {
									this.unitOrderListener.issueTargetOrder(unit.getSimulationUnit().getHandleId(),
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
								if (yesSound.playUnitResponse(this.war3MapViewer.worldScene.audioContext,
										getSelectedUnit())) {
									portraitTalk(yesSound);
								}
								if (rallied) {
									this.war3MapViewer.getUiSounds().getSound("RallyPointPlace")
											.play(this.uiScene.audioContext, 0, 0, 0);
								}
								this.selectedSoundCount = 0;
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
					if (getHoveredFrame(this.rootFrame, screenCoordsVector.x, screenCoordsVector.y, this.includeFrames,
							this.ignoreFrames) != null) {
						return false;
					}

					this.war3MapViewer.getClickLocation(this.lastMouseClickLocation, screenX, (int) worldScreenY, true,
							true);
					this.lastMouseDragStart.set(this.lastMouseClickLocation);
					this.lastMouseDragCameraTargetStart.set(this.cameraManager.target);
					this.allowDrag = true;
					this.draggingMouseButton = button;
				}
			}
		}
		else {
			if (clickedUIFrame instanceof ClickableFrame) {
				this.mouseDownUIFrame = (ClickableFrame) clickedUIFrame;
				this.mouseDownUIFrame.mouseDown(this.rootFrame, this.uiViewport);
			}
		}
		return false;
	}

	private void advanceSelectedSubGroup() {
		boolean foundSubSelection = false;
		for (final RenderUnit unit : this.selectedUnits) {
			if (foundSubSelection) {
				if (!unit.groupsWith(this.selectedUnit)) {
					selectUnit(unit);
					return;
				}
			}
			else if (unit == this.selectedUnit) {
				foundSubSelection = true;
			}
		}
		if (!this.selectedUnits.isEmpty()) {
			selectUnit(this.selectedUnits.get(0));
		}
	}

	private void advanceSelectedSubGroupReverse() {
		boolean foundSubSelection = false;
		for (int i = this.selectedUnits.size() - 1; i >= 0; i--) {
			final RenderUnit unit = this.selectedUnits.get(i);
			if (foundSubSelection) {
				if (!unit.groupsWith(this.selectedUnit)) {
					selectUnit(unit);
					return;
				}
			}
			else if (unit == this.selectedUnit) {
				foundSubSelection = true;
			}
		}
		if (!this.selectedUnits.isEmpty()) {
			selectUnit(this.selectedUnits.get(this.selectedUnits.size() - 1));
		}
	}

	private void useActiveCommandOnUnit(final boolean shiftDown, final RenderWidget rayPickUnit) {
		if (this.draggingItem != null) {
			this.unitOrderListener.issueDropItemAtTargetOrder(this.activeCommandUnit.getSimulationUnit().getHandleId(),
					this.activeCommand.getHandleId(), this.activeCommandOrderId, this.draggingItem.getHandleId(),
					rayPickUnit.getSimulationWidget().getHandleId(), shiftDown);
			setDraggingItem(null);
		}
		else {
			this.unitOrderListener.issueTargetOrder(this.activeCommandUnit.getSimulationUnit().getHandleId(),
					this.activeCommand.getHandleId(), this.activeCommandOrderId,
					rayPickUnit.getSimulationWidget().getHandleId(), shiftDown);
			if (this.selectedUnits.size() > 1) {
				for (final RenderUnit otherSelectedUnit : this.selectedUnits) {
					if (otherSelectedUnit != this.activeCommandUnit) {
						CAbility abilityToUse = null;
						CWidget targetToUse = null;
						for (final CAbility ability : otherSelectedUnit.getSimulationUnit().getAbilities()) {
							final CWidgetAbilityTargetCheckReceiver receiver = CWidgetAbilityTargetCheckReceiver.INSTANCE
									.reset();
							ability.checkCanTarget(this.war3MapViewer.simulation, otherSelectedUnit.getSimulationUnit(),
									this.activeCommandOrderId, rayPickUnit.getSimulationWidget(), receiver);
							if (receiver.getTarget() != null) {
								abilityToUse = ability;
								targetToUse = receiver.getTarget();
							}
						}
						if (abilityToUse != null) {
							this.unitOrderListener.issueTargetOrder(otherSelectedUnit.getSimulationUnit().getHandleId(),
									abilityToUse.getHandleId(), this.activeCommandOrderId, targetToUse.getHandleId(),
									shiftDown);
						}
					}
				}
			}
		}
		final UnitSound yesSound = this.activeCommand instanceof CAbilityAttack ? getSelectedUnit().soundset.yesAttack
				: getSelectedUnit().soundset.yes;
		if (yesSound.playUnitResponse(this.war3MapViewer.worldScene.audioContext, getSelectedUnit())) {
			portraitTalk(yesSound);
		}
		this.selectedSoundCount = 0;
		if (this.activeCommand instanceof CAbilityRally) {
			this.war3MapViewer.getUiSounds().getSound("RallyPointPlace").play(this.uiScene.audioContext, 0, 0, 0);
		}
		if (!shiftDown) {
			this.subMenuOrderIdStack.clear();
			this.activeCommandUnit = null;
			this.activeCommand = null;
			this.activeCommandOrderId = -1;
			clearAndRepopulateCommandCard();
		}
	}

	private void rightClickMove(final int screenX, final float worldScreenY) {
		this.war3MapViewer.getClickLocation(clickLocationTemp, screenX, (int) worldScreenY,
				(getSelectedUnit() != null) && getSelectedUnit().getSimulationUnit().isMovementOnWaterAllowed(), true);
		this.war3MapViewer.showConfirmation(clickLocationTemp, 0, 1, 0);
		clickLocationTemp2.set(clickLocationTemp.x, clickLocationTemp.y);

		boolean ordered = false;
		boolean rallied = false;
		for (final RenderUnit unit : this.selectedUnits) {
			if (unit.getSimulationUnit().getPlayerIndex() == this.war3MapViewer.getLocalPlayerIndex()) {
				for (final CAbility ability : unit.getSimulationUnit().getAbilities()) {
					ability.checkCanUse(this.war3MapViewer.simulation, unit.getSimulationUnit(), OrderIds.smart,
							BooleanAbilityActivationReceiver.INSTANCE);
					if (BooleanAbilityActivationReceiver.INSTANCE.isOk()) {
						ability.checkCanTarget(this.war3MapViewer.simulation, unit.getSimulationUnit(), OrderIds.smart,
								clickLocationTemp2, PointAbilityTargetCheckReceiver.INSTANCE);
						final Vector2 target = PointAbilityTargetCheckReceiver.INSTANCE.getTarget();
						if (target != null) {
							this.unitOrderListener.issuePointOrder(unit.getSimulationUnit().getHandleId(),
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
			if (getSelectedUnit().soundset.yes.playUnitResponse(this.war3MapViewer.worldScene.audioContext,
					getSelectedUnit())) {
				portraitTalk(getSelectedUnit().soundset.yes);
			}
			if (rallied) {
				this.war3MapViewer.getUiSounds().getSound("RallyPointPlace").play(this.uiScene.audioContext, 0, 0, 0);
			}
			this.selectedSoundCount = 0;
		}
	}

	private void selectWidgets(final List<RenderWidget> selectedUnits) {
		if ((selectedUnits.size() == 1) && (selectedUnits.get(0) instanceof RenderItem)) {
			final RenderItem selectedItem = (RenderItem) selectedUnits.get(0);
			selectItem(selectedItem);
			return;
		}
		if ((selectedUnits.size() == 1) && (selectedUnits.get(0) instanceof RenderDestructable)) {
			final RenderDestructable selectedItem = (RenderDestructable) selectedUnits.get(0);
			selectDestructable(selectedItem);
			return;
		}
		final List<RenderUnit> units = new ArrayList<>();
		Collections.sort(selectedUnits, new Comparator<RenderWidget>() {
			@Override
			public int compare(final RenderWidget widget1, final RenderWidget widget2) {
				final CUnitType unitType1 = ((RenderUnit) widget2).getSimulationUnit().getUnitType();
				final CUnitType unitType2 = ((RenderUnit) widget1).getSimulationUnit().getUnitType();
				final int prioSort = unitType1.getPriority() - unitType2.getPriority();
				if (prioSort == 0) {
					final int levelSort = unitType1.getLevel() - unitType2.getLevel();
					if (levelSort == 0) {
						return unitType1.getTypeId().getValue() - unitType2.getTypeId().getValue();
					}
					else {
						return levelSort;
					}
				}
				else {
					return prioSort;
				}
			}
		});
		for (final RenderWidget widget : selectedUnits) {
			if (widget instanceof RenderUnit) {
				units.add((RenderUnit) widget);
			}
		}
		selectUnits(units);
	}

	private void selectItem(RenderItem selectedItem) {
		selectUnit(null);
		this.portrait.setSelectedItem(selectedItem);
		this.simpleInfoPanelItemDetail.setVisible(true);
		final War3ID typeId = selectedItem.getSimulationItem().getTypeId();
		final ItemUI itemUI = this.war3MapViewer.getAbilityDataUI().getItemUI(typeId);
		this.rootFrame.setText(this.simpleItemNameValue, itemUI.getName());
		this.rootFrame.setText(this.simpleItemDescriptionValue, itemUI.getDescription());
	}

	private void selectDestructable(RenderDestructable selectedItem) {
		selectUnit(null);
		this.portrait.setSelectedDestructable(selectedItem);
		this.simpleInfoPanelDestructableDetail.setVisible(true);
		final String name = selectedItem.getSimulationDestructable().getDestType().getName();
		this.rootFrame.setText(this.simpleDestructableNameValue, name);
	}

	private void selectUnits(final List<RenderUnit> selectedUnits) {
		final List<RenderUnit> prevSelectedUnits = this.selectedUnits;
		this.selectedUnits = selectedUnits;
		if (!selectedUnits.isEmpty()) {
			final RenderUnit unit = selectedUnits.get(0);
			boolean selectionChanged = (unit != this.selectedUnit)
					|| (prevSelectedUnits.size() != selectedUnits.size());
			for (int i = 0; (i < prevSelectedUnits.size()) && (i < selectedUnits.size()); i++) {
				if (prevSelectedUnits.get(i) != selectedUnits.get(i)) {
					selectionChanged = true;
				}
			}
			boolean playedNewSound = false;
			if (selectionChanged) {
				this.selectedSoundCount = 0;
			}
			UnitSound USAudio = null;
			if ((unit.getSimulationUnit().getPlayerIndex() == this.war3MapViewer.getLocalPlayerIndex())
					|| (unit.getSimulationUnit().getUnitType().getRace() == CUnitRace.CRITTERS)
					|| ((unit.getSimulationUnit().getUnitType().getRace() == CUnitRace.OTHER)
							&& (unit.getSimulationUnit().getPlayerIndex() == (WarsmashConstants.MAX_PLAYERS - 1)))) {
				if (unit.soundset != null) {
					UnitSound ackSoundToPlay = unit.soundset.what;
					int soundIndex;
					final int pissedSoundCount = unit.soundset.pissed.getSoundCount();
					if (unit.getSimulationUnit().isConstructing()) {
						ackSoundToPlay = this.war3MapViewer.getUiSounds()
								.getSound(this.rootFrame.getSkinField("ConstructingBuilding"));
						soundIndex = (int) (Math.random() * ackSoundToPlay.getSoundCount());
					}
					else {
						if ((this.selectedSoundCount >= 3) && (pissedSoundCount > 0)) {
							soundIndex = this.selectedSoundCount - 3;
							ackSoundToPlay = unit.soundset.pissed;
						}
						else {
							soundIndex = (int) (Math.random() * ackSoundToPlay.getSoundCount());
						}
					}
					if ((ackSoundToPlay != null) && ackSoundToPlay
							.playUnitResponse(this.war3MapViewer.worldScene.audioContext, unit, soundIndex)) {
						this.selectedSoundCount++;
						if ((this.selectedSoundCount - 3) >= pissedSoundCount) {
							this.selectedSoundCount = 0;
						}
						USAudio = ackSoundToPlay;
						playedNewSound = true;
					}
				}
			}
			else {
				this.war3MapViewer.getUiSounds().getSound("InterfaceClick").play(this.uiScene.audioContext, 0, 0, 0);
			}
			if (selectionChanged) {
				for (final MultiSelectUnitStateListener listener : this.multiSelectUnitStateListeners) {
					listener.dispose();
				}
				this.multiSelectUnitStateListeners.clear();
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
						this.multiSelectUnitStateListeners.add(multiSelectUnitStateListener);
					}
				}
			}
			if (playedNewSound) {
				portraitTalk(USAudio);
			}
		}
		else {
			selectUnit(null);
		}
	}

	@Override
	public boolean touchUp(final int screenX, final int screenY, final float worldScreenY, final int button) {
		if (button == Input.Buttons.FORWARD) {
			return false;
		}
		else if (button == Input.Buttons.BACK) {
			return false;
		}
		if (!this.userControlEnabled) {
			return false;
		}
		this.currentlyDraggingPointer = -1;
		screenCoordsVector.set(screenX, screenY);
		this.uiViewport.unproject(screenCoordsVector);
		final UIFrame clickedUIFrame = this.rootFrame.touchUp(screenCoordsVector.x, screenCoordsVector.y, button);
		if (this.mouseDownUIFrame != null) {
			if (clickedUIFrame == this.mouseDownUIFrame) {
				this.mouseDownUIFrame.onClick(button);
				final String soundKey = this.mouseDownUIFrame.getSoundKey();
				if (soundKey != null) {
					this.war3MapViewer.getUiSounds().getSound(soundKey).play(this.uiScene.audioContext, 0, 0, 0);
				}
			}
			this.mouseDownUIFrame.mouseUp(this.rootFrame, this.uiViewport);
		}
		else {
			if (this.draggingMouseButton == Input.Buttons.LEFT) {
				if (!this.dragSelectPreviewUnits.isEmpty()) {
					if (this.allowDrag) {
						final List<RenderWidget> selectedWidgets = new ArrayList<>();
						boolean foundGoal = false;
						for (final RenderUnit unit : this.dragSelectPreviewUnits) {
							if ((unit.getSimulationUnit().getPlayerIndex() == this.war3MapViewer.getLocalPlayerIndex())
									&& !unit.getSimulationUnit().isBuilding()) {
								foundGoal = true;
								selectedWidgets.add(unit);
							}
						}
						if (!foundGoal) {
							selectedWidgets.addAll(this.dragSelectPreviewUnits);
						}
						final boolean shiftDown = isShiftDown();
						if (shiftDown) {
							for (final RenderUnit unit : this.selectedUnits) {
								if (!selectedWidgets.contains(unit)) {
									selectedWidgets.add(unit);
								}
							}
						}

						this.war3MapViewer.clearUnitMouseOverHighlight();

						this.war3MapViewer.doSelectUnit(selectedWidgets);
						selectWidgets(selectedWidgets);
					}
					this.dragSelectPreviewUnits.clear();
				}
				else {
					if (this.allowDrag) {
						if (button == Input.Buttons.LEFT) {
							updateMouseOverUnit(screenX, worldScreenY);
							if ((this.mouseOverUnit != null) && isUnitSelectable(this.mouseOverUnit)) {
								final long currentMillis = TimeUtils.millis();
								final List<RenderWidget> unitList = new ArrayList<>();
								final boolean shiftDown = isShiftDown();
								final boolean controlDown = isControlDown()
										|| (((currentMillis - this.lastUnitClickTime) < 500)
												&& (this.mouseOverUnit == this.lastClickUnit));
								if (shiftDown) {
									unitList.addAll(this.selectedUnits);
								}
								if ((this.mouseOverUnit instanceof RenderUnit) && controlDown) {
									processSelectNearbyUnits(unitList, shiftDown, (RenderUnit) this.mouseOverUnit);
								}
								else {
									processClickSelect(unitList, shiftDown, this.mouseOverUnit);
								}
								this.war3MapViewer.doSelectUnit(unitList);
								selectWidgets(unitList);
								this.lastUnitClickTime = currentMillis;
								this.lastClickUnit = this.mouseOverUnit;
							}
						}
					}
				}
			}
		}
		this.mouseDownUIFrame = null;
		this.draggingMouseButton = -1;
		this.allowDrag = false;
		return false;
	}

	private void processSelectNearbyUnits(final List<RenderWidget> unitList, final boolean shiftDown,
			final RenderUnit mouseOverUnit) {
		this.war3MapViewer.simulation.getWorldCollision().enumUnitsInRect(
				new Rectangle(this.mouseOverUnit.getX() - 1024, this.mouseOverUnit.getY() - 1024, 2048, 2048),
				new CUnitEnumFunction() {
					@Override
					public boolean call(final CUnit unit) {
						if (unit.getUnitType() == mouseOverUnit.getSimulationUnit().getUnitType()) {
							final RenderUnit renderPeer = MeleeUI.this.war3MapViewer.getRenderPeer(unit);
							processClickSelect(unitList, shiftDown, renderPeer);
						}
						return false;
					}
				});
	}

	private void processClickSelect(final List<RenderWidget> unitList, final boolean shiftDown,
			final RenderWidget mouseOverUnit) {
		if (shiftDown) {
			if (this.selectedUnits.contains(mouseOverUnit)) {
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

	@Override
	public boolean touchDragged(final int screenX, final int screenY, final float worldScreenY, final int pointer) {
		if (!this.userControlEnabled) {
			return false;
		}
		screenCoordsVector.set(screenX, screenY);
		this.uiViewport.unproject(screenCoordsVector);

		if (this.meleeUIMinimap.containsMouse(screenCoordsVector.x, screenCoordsVector.y)) {
			final Vector2 worldPoint = this.meleeUIMinimap.getWorldPointFromScreen(screenCoordsVector.x,
					screenCoordsVector.y);
			this.cameraManager.target.x = worldPoint.x;
			this.cameraManager.target.y = worldPoint.y;
		}
		else {
			if (this.allowDrag) {
				if (null != this.mouseOverUnit) {
					this.war3MapViewer.clearUnitMouseOverHighlight();
					this.dragSelectPreviewUnits.clear();
					this.mouseOverUnit = null;
				}

				this.war3MapViewer.getClickLocationOnZPlane(clickLocationTemp, screenX, (int) worldScreenY,
						this.lastMouseDragStart.z);
				this.currentlyDraggingPointer = pointer;
				if (this.draggingMouseButton == Input.Buttons.MIDDLE) {
					this.cameraManager.target.set(clickLocationTemp.sub(this.lastMouseDragStart).scl(-1)
							.add(this.lastMouseDragCameraTargetStart));
				}
				else if (this.draggingMouseButton == Input.Buttons.LEFT) {
					// update mouseover
				}
				this.lastMouseClickLocation.set(clickLocationTemp);
			}
			else {
				if (this.mouseDownUIFrame != null) {
					this.mouseDownUIFrame.mouseDragged(this.rootFrame, this.uiViewport, screenCoordsVector.x,
							screenCoordsVector.y);
				}
			}
		}
		return false;
	}

	@Override
	public boolean mouseMoved(final int screenX, final int screenY, final float worldScreenY) {
		screenCoordsVector.set(screenX, screenY);
		this.uiViewport.unproject(screenCoordsVector);
		final UIFrame mousedUIFrame = this.rootFrame.getFrameChildUnderMouse(screenCoordsVector.x,
				screenCoordsVector.y);
		if (mousedUIFrame != this.mouseOverUIFrame) {
			if (this.mouseOverUIFrame != null) {
				this.mouseOverUIFrame.mouseExit(this.rootFrame, this.uiViewport);
			}
			if (mousedUIFrame instanceof ClickableFrame) {
				this.mouseOverUIFrame = (ClickableFrame) mousedUIFrame;
				if (this.mouseOverUIFrame != null) {
					this.mouseOverUIFrame.mouseEnter(this.rootFrame, this.uiViewport);
				}
				if (mousedUIFrame instanceof ClickableActionFrame) {
					loadTooltip((ClickableActionFrame) mousedUIFrame);
				}
			}
			else {
				this.mouseOverUIFrame = null;
				this.tooltipFrame.setVisible(false);
			}
		}
		final UIFrame hover = getHoveredFrame(this.rootFrame, screenCoordsVector.x, screenCoordsVector.y,
				this.includeFrames, this.ignoreFrames);
		if (hover == null) {
			updateMouseOverUnit(screenX, worldScreenY);
		}
		else {
			this.war3MapViewer.clearUnitMouseOverHighlight();
			this.mouseOverUnit = null;
		}
		return false;
	}

	private void updateMouseOverUnit(final int screenX, final float worldScreenY) {
		final RenderWidget newMouseOverUnit;
		if (this.userControlEnabled) {
			newMouseOverUnit = this.war3MapViewer.rayPickUnit(screenX, worldScreenY, this.anyClickableUnitFilter);
		}
		else {
			newMouseOverUnit = null;
		}
		if (newMouseOverUnit != this.mouseOverUnit) {
			this.war3MapViewer.clearUnitMouseOverHighlight();
			this.dragSelectPreviewUnits.clear();
			if (newMouseOverUnit != null) {
				this.war3MapViewer.showUnitMouseOverHighlight(newMouseOverUnit);
			}
			this.mouseOverUnit = newMouseOverUnit;
		}
	}

	private void loadTooltip(final ClickableActionFrame mousedUIFrame) {
		final int goldCost = mousedUIFrame.getToolTipGoldCost();
		final int lumberCost = mousedUIFrame.getToolTipLumberCost();
		final int foodCost = mousedUIFrame.getToolTipFoodCost();
		final int manaCost = mousedUIFrame.getToolTipManaCost();
		final String toolTip = mousedUIFrame.getToolTip();
		final String uberTip = mousedUIFrame.getUberTip();
		if ((toolTip == null) || (uberTip == null)) {
			this.tooltipFrame.setVisible(false);
		}
		else {
			this.rootFrame.setText(this.tooltipUberTipText, uberTip);
			int resourceIndex = 0;
			if (goldCost != 0) {
				this.tooltipResourceFrames[resourceIndex].setVisible(true);
				this.tooltipResourceIconFrames[resourceIndex].setTexture("ToolTipGoldIcon", this.rootFrame);
				this.rootFrame.setText(this.tooltipResourceTextFrames[resourceIndex], Integer.toString(goldCost));
				resourceIndex++;
			}
			if (lumberCost != 0) {
				this.tooltipResourceFrames[resourceIndex].setVisible(true);
				this.tooltipResourceIconFrames[resourceIndex].setTexture("ToolTipLumberIcon", this.rootFrame);
				this.rootFrame.setText(this.tooltipResourceTextFrames[resourceIndex], Integer.toString(lumberCost));
				resourceIndex++;
			}
			if (foodCost != 0) {
				this.tooltipResourceFrames[resourceIndex].setVisible(true);
				this.tooltipResourceIconFrames[resourceIndex].setTexture("ToolTipSupplyIcon", this.rootFrame);
				this.rootFrame.setText(this.tooltipResourceTextFrames[resourceIndex], Integer.toString(foodCost));
				resourceIndex++;
			}
			if (manaCost != 0) {
				this.tooltipResourceFrames[resourceIndex].setVisible(true);
				this.tooltipResourceIconFrames[resourceIndex].setTexture("ToolTipManaIcon", this.rootFrame);
				this.rootFrame.setText(this.tooltipResourceTextFrames[resourceIndex], Integer.toString(manaCost));
				resourceIndex++;
			}
			for (int i = resourceIndex; i < this.tooltipResourceFrames.length; i++) {
				this.tooltipResourceFrames[i].setVisible(false);
			}
			float resourcesHeight;
			if (resourceIndex != 0) {
				this.tooltipUberTipText.addSetPoint(this.uberTipWithResourcesSetPoint);
				resourcesHeight = 0.014f;
			}
			else {
				this.tooltipUberTipText.addSetPoint(this.uberTipNoResourcesSetPoint);
				resourcesHeight = 0.004f;
			}
			this.rootFrame.setText(this.tooltipText, toolTip);
			final float predictedViewportHeight = this.tooltipText.getPredictedViewportHeight()
					+ GameUI.convertY(this.uiViewport, resourcesHeight)
					+ this.tooltipUberTipText.getPredictedViewportHeight() + GameUI.convertY(this.uiViewport, 0.003f);
			this.tooltipFrame.setHeight(predictedViewportHeight);
			this.tooltipFrame.positionBounds(this.rootFrame, this.uiViewport);
			this.tooltipFrame.setVisible(true);
		}
	}

	public float getHeightRatioCorrection() {
		return this.heightRatioCorrection;
	}

	@Override
	public void queueIconClicked(final int index) {
		final CUnit simulationUnit = this.selectedUnit.getSimulationUnit();
		if (simulationUnit.isConstructingOrUpgrading()) {
			switch (index) {
			case 0:
				for (final CAbility ability : simulationUnit.getAbilities()) {
					ability.checkCanUse(this.war3MapViewer.simulation, simulationUnit, OrderIds.cancel,
							BooleanAbilityActivationReceiver.INSTANCE);
					if (BooleanAbilityActivationReceiver.INSTANCE.isOk()) {

						final BooleanAbilityTargetCheckReceiver<Void> targetCheckReceiver = BooleanAbilityTargetCheckReceiver
								.<Void>getInstance().reset();
						ability.checkCanTargetNoTarget(this.war3MapViewer.simulation, simulationUnit, OrderIds.cancel,
								targetCheckReceiver);
						if (targetCheckReceiver.isTargetable()) {
							this.unitOrderListener.issueImmediateOrder(simulationUnit.getHandleId(),
									ability.getHandleId(), OrderIds.cancel, false);
						}
					}
				}
				break;
			case 1:
				final List<RenderWidget> unitList = Arrays
						.asList(this.war3MapViewer.getRenderPeer(this.selectedUnit.getSimulationUnit().getWorker()));
				this.war3MapViewer.doSelectUnit(unitList);
				selectWidgets(unitList);
				break;
			}
		}
		else {
			this.unitOrderListener.unitCancelTrainingItem(simulationUnit.getHandleId(), index);
		}
	}

	@Override
	public void dispose() {
		if (this.rootFrame != null) {
			this.rootFrame.dispose();
		}
	}

	private class ItemCommandCardCommandListener implements CommandCardCommandListener {
		@Override
		public void onClick(final int abilityHandleId, final int orderId, final boolean rightClick) {
			final RenderUnit selectedUnit2 = MeleeUI.this.selectedUnit;
			final CUnit simulationUnit = selectedUnit2.getSimulationUnit();
			final CAbilityInventory inventoryData = simulationUnit.getInventoryData();
			if (inventoryData == null) {
				return;
			}
			if (rightClick) {
				final int slot = orderId - 1;
				final CItem itemInSlot = inventoryData.getItemInSlot(slot);
				if (MeleeUI.this.draggingItem != null) {
					final CUnit activeCmdSimUnit = MeleeUI.this.activeCommandUnit.getSimulationUnit();
					MeleeUI.this.unitOrderListener.issueTargetOrder(activeCmdSimUnit.getHandleId(),
							activeCmdSimUnit.getInventoryData().getHandleId(), OrderIds.itemdrag00 + slot,
							MeleeUI.this.draggingItem.getHandleId(), false);
					setDraggingItem(null);
					MeleeUI.this.activeCommand = null;
					MeleeUI.this.activeCommandUnit = null;
				}
				else {
					if (itemInSlot != null) {
						setDraggingItem(itemInSlot);
						MeleeUI.this.activeCommand = inventoryData;
						MeleeUI.this.activeCommandUnit = selectedUnit2;
						MeleeUI.this.activeCommandOrderId = OrderIds.dropitem;
					}
				}
			}
			else {
				final CSimulation game = MeleeUI.this.war3MapViewer.simulation;
				final ExternStringMsgAbilityActivationReceiver receiver = ExternStringMsgAbilityActivationReceiver.INSTANCE;
				receiver.reset();
				inventoryData.checkCanUse(game, simulationUnit, orderId, receiver);
				if (receiver.isUseOk()) {
					final BooleanAbilityTargetCheckReceiver<Void> targetReceiver = BooleanAbilityTargetCheckReceiver
							.getInstance();
					targetReceiver.reset();
					inventoryData.checkCanTargetNoTarget(game, simulationUnit, orderId, targetReceiver);
					if (targetReceiver.isTargetable()) {
						MeleeUI.this.unitOrderListener.issueImmediateOrder(simulationUnit.getHandleId(),
								inventoryData.getHandleId(), orderId, isShiftDown());
					}
					else {
						MeleeUI.this.activeCommand = inventoryData;
						MeleeUI.this.activeCommandOrderId = orderId;
						MeleeUI.this.activeCommandUnit = selectedUnit2;
						clearAndRepopulateCommandCard();
					}
				}
				else {
					final String externStringKey = receiver.getExternStringKey();
					if ((externStringKey != null) && !externStringKey.isEmpty()) {
						showInterfaceError(simulationUnit.getPlayerIndex(), externStringKey);
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
		private boolean disposed = false;

		public MultiSelectUnitStateListener(final RenderUnit sourceUnit, final int index) {
			this.sourceUnit = sourceUnit;
			this.index = index;
		}

		public void dispose() {
			this.sourceUnit.getSimulationUnit().removeStateListener(this);
			this.disposed = true;
		}

		@Override
		public void lifeChanged() {
			if (this.disposed) {
				return;
			}
			if (this.sourceUnit.getSimulationUnit().isDead()) {
				removeSourceUnitFromSelection();
			}
			else {
				MeleeUI.this.selectedUnitFrames[this.index]
						.setLifeRatioRemaining(this.sourceUnit.getSimulationUnit().getLife()
								/ this.sourceUnit.getSimulationUnit().getMaximumLife());
			}
		}

		@Override
		public void hideStateChanged() {
			if (this.disposed) {
				return;
			}
			if (this.sourceUnit.getSimulationUnit().isHidden()
					|| !this.sourceUnit.getSimulationUnit().isVisible(MeleeUI.this.war3MapViewer.simulation,
							MeleeUI.this.war3MapViewer.getLocalPlayerIndex())) {
				removeSourceUnitFromSelection();
			}
		}

		private void removeSourceUnitFromSelection() {
			MeleeUI.this.selectedUnits.remove(this.sourceUnit);
			MeleeUI.this.war3MapViewer.doUnselectUnit(this.sourceUnit);
			MeleeUI.this.multiSelectUnitStateListeners.remove(this.index);
			for (int i = this.index; i < MeleeUI.this.multiSelectUnitStateListeners.size(); i++) {
				MeleeUI.this.multiSelectUnitStateListeners.get(i).index--;
			}
			dispose();
			reloadSelectedUnitUI(MeleeUI.this.selectedUnit);
		}

		@Override
		public void manaChanged() {
			if (this.disposed) {
				return;
			}
			MeleeUI.this.selectedUnitFrames[this.index]
					.setManaRatioRemaining(this.sourceUnit.getSimulationUnit().getMana()
							/ this.sourceUnit.getSimulationUnit().getMaximumMana());
		}

		@Override
		public void ordersChanged() {
			if (this.disposed) {
				return;
			}

		}

		@Override
		public void queueChanged() {
			if (this.disposed) {
				return;
			}

		}

		@Override
		public void rallyPointChanged() {
			if (this.disposed) {
				return;
			}

		}

		@Override
		public void waypointsChanged() {
			if (this.disposed) {
				return;
			}

		}

		@Override
		public void heroStatsChanged() {
			if (this.disposed) {
				return;
			}

		}

		@Override
		public void inventoryChanged() {
			if (this.disposed) {
				return;
			}

		}

		@Override
		public void attacksChanged() {
			if (this.disposed) {
				return;
			}
		}

		@Override
		public void abilitiesChanged() {
			if (this.disposed) {
				return;
			}
		}

	}

	@Override
	public GameCameraManager getCameraManager() {
		return this.cameraManager;
	}

	@Override
	public Music playMusic(final String musicField, final boolean random, int index) {
		return playMusicEx(musicField, random, index, 0, -1);
	}

	@Override
	public Music setMapMusic(String musicField, boolean random, int index) {
		return this.musicPlayer.setDefaultMusic(musicField, random, index);
	}

	@Override
	public void playMapMusic() {
		this.musicPlayer.playDefaultMusic();
	}

	@Override
	public Music playMusicEx(String musicField, boolean random, int index, int fromMSecs, int fadeInMSecs) {
		return this.musicPlayer.playMusicEx(musicField, random, index, fromMSecs, fadeInMSecs);
	}

	@Override
	public void stopMusic(boolean fadeOut) {
		this.musicPlayer.stopMusic();
	}

	@Override
	public void resumeMusic() {
		this.musicPlayer.resumeMusic();
	}

	@Override
	public void setMusicVolume(int volume) {
		this.musicPlayer.setVolume(volume);
	}

	@Override
	public void setMusicPlayPosition(int millisecs) {
		this.musicPlayer.setMusicPosition(millisecs);
	}

	@Override
	public void gameClosed() {
		this.musicPlayer.stopMusic();
	}

	@Override
	public Scene getUiScene() {
		return this.uiScene;
	}

	@Override
	public CTimerDialog createTimerDialog(final CTimer timer) {
		final UIFrame timerDialog = this.rootFrame.createFrame("TimerDialog", this.rootFrame, 0, 0);
		final StringFrame valueFrame = (StringFrame) this.rootFrame.getFrameByName("TimeDialogValue", 0);
		final StringFrame titleFrame = (StringFrame) this.rootFrame.getFrameByName("TimerDialogTitle", 0);
		return new CTimerDialog(timer, timerDialog, valueFrame, titleFrame);
	}

	@Override
	public void displayTimedText(final float x, final float y, final float duration, final String message) {
		showGameMessage(message, duration); // TODO x y
	}

	@Override
	public void clearTextMessages() {
		for (final GameMessage gameMessage : this.gameMessages) {
			this.rootFrame.remove(gameMessage.stringFrame);
		}
		this.gameMessages.clear();
	}

	@Override
	public CScriptDialog createScriptDialog(final GlobalScope globalScope) {
		final SimpleFrame scriptDialog = (SimpleFrame) this.rootFrame.createFrame("ScriptDialog", this.rootFrame, 0, 0);
		scriptDialog.addAnchor(new AnchorDefinition(FramePoint.TOP, 0, GameUI.convertY(this.uiViewport, -0.05f)));
		scriptDialog.setVisible(false);
		final StringFrame scriptDialogTextFrame = (StringFrame) this.rootFrame.getFrameByName("ScriptDialogText", 0);
		scriptDialog.positionBounds(this.rootFrame, this.uiViewport);
		return new CScriptDialog(globalScope, scriptDialog, scriptDialogTextFrame);
	}

	@Override
	public CScriptDialogButton createScriptDialogButton(final CScriptDialog scriptDialog, final String text,
			final char hotkey) {
		// TODO use hotkey
		final GlueTextButtonFrame scriptDialogButton = (GlueTextButtonFrame) this.rootFrame
				.createFrame("ScriptDialogButton", scriptDialog.getScriptDialogFrame(), 0, 0);
		scriptDialogButton.setHeight(GameUI.convertY(this.uiViewport, 0.03f));
		final StringFrame scriptDialogTextFrame = (StringFrame) this.rootFrame.getFrameByName("ScriptDialogButtonText",
				0);
		this.rootFrame.setText(scriptDialogTextFrame, text);
		scriptDialogButton.addSetPoint(new SetPoint(FramePoint.TOP, scriptDialog.getLastAddedComponent(),
				FramePoint.BOTTOM, 0, GameUI.convertY(this.uiViewport, -0.005f)));
		final CScriptDialogButton newButton = new CScriptDialogButton(scriptDialogButton, scriptDialogTextFrame);
		scriptDialog.addButton(this.rootFrame, this.uiViewport, newButton);
		return newButton;
	}

	@Override
	public void destroyDialog(final CScriptDialog dialog) {
		this.rootFrame.remove(dialog.getScriptDialogFrame());
	}

	@Override
	public void clearDialog(final CScriptDialog dialog) {
		destroyDialog(dialog);
		final SimpleFrame scriptDialog = (SimpleFrame) this.rootFrame.createFrame("ScriptDialog", this.rootFrame, 0, 0);
		scriptDialog.addAnchor(new AnchorDefinition(FramePoint.TOP, 0, GameUI.convertY(this.uiViewport, -0.05f)));
		scriptDialog.setVisible(false);
		final StringFrame scriptDialogTextFrame = (StringFrame) this.rootFrame.getFrameByName("ScriptDialogText", 0);
		scriptDialog.positionBounds(this.rootFrame, this.uiViewport);
		dialog.reset(scriptDialog, scriptDialogTextFrame);
	}

	@Override
	public void removedUnit(final CUnit whichUnit) {
		final RenderUnit renderUnit = this.war3MapViewer.getRenderPeer(whichUnit);
		if ((this.selectedUnits != null) && this.selectedUnits.contains(renderUnit)) {
			final List<RenderUnit> newSelectedUnits = new ArrayList<>(this.selectedUnits);
			newSelectedUnits.remove(renderUnit);
			selectUnits(newSelectedUnits);
		}
		else if (this.selectedUnit == renderUnit) {
			selectUnit(null);
		}
	}

	@Override
	public void removedItem(final CItem whichItem) {
		// TODO unselect an item if it exists
	}

	private final class ClickablePortrait extends AbstractClickableActionFrame implements ClickableFrame {
		public ClickablePortrait(final String name, final UIFrame parent) {
			super(name, parent);
		}

		private boolean mouseDown = false;

		@Override
		public void mouseEnter(final GameUI gameUI, final Viewport uiViewport) {
		}

		@Override
		public void mouseExit(final GameUI gameUI, final Viewport uiViewport) {
		}

		@Override
		public void mouseDragged(final GameUI rootFrame, final Viewport uiViewport, final float x, final float y) {
		}

		@Override
		public void mouseDown(final GameUI gameUI, final Viewport uiViewport) {
			if ((getCameraManager().getTargetControllerUnit() == null) && (MeleeUI.this.selectedUnit != null)) {
				getCameraManager().setTargetController(MeleeUI.this.selectedUnit, 0, 0, false);
			}
			this.mouseDown = true;
		}

		@Override
		public void mouseUp(final GameUI gameUI, final Viewport uiViewport) {
			getCameraManager().setTargetController(null, 0, 0, false);
			this.mouseDown = true;
		}

		@Override
		public void onClick(final int button) {
		}

		@Override
		protected void innerPositionBounds(final GameUI gameUI, final Viewport viewport) {
		}

		@Override
		protected void internalRender(final SpriteBatch batch, final BitmapFont baseFont,
				final GlyphLayout glyphLayout) {
		}

		@Override
		public String getSoundKey() {
			return SOUND_KEY_NONE;
		}
	}

	@Override
	public void onHide() {
		this.timeIndicator.setVisible(false);
		this.cursorFrame.setVisible(false);
		this.showing = false;
	}

	@Override
	public void onShow() {
		this.timeIndicator.setVisible(true);
		this.cursorFrame.setVisible(true);
		this.showing = true;
	}

	@Override
	public void showInterface(boolean show, float fadeDuration) {
		updateInterfaceVisibility(show);
	}

	private void updateInterfaceVisibility(boolean show) {
		this.consoleUI.setVisible(show);
		this.resourceBar.setVisible(show);
		this.timeIndicator.setVisible(show);
		this.meleeUIMinimap.setVisible(show);
		this.smashSimpleInfoPanel.setVisible(show);
		this.smashCommandButtons.setVisible(show);
		this.upperButtonBar.setVisible(show);
		this.inventoryCover.setVisible(show);
		this.inventoryBarFrame.setVisible(show);
		this.inventoryTitleFrame.setVisible(show);
		this.cinematicPanel.setVisible(!show);

		positionPortrait();
	}

	@Override
	public void setCinematicScene(int portraitUnitId, CPlayerColor color, String speakerTitle, String text,
			float sceneDuration, float voiceoverDuration) {
		final RenderUnitType unitTypeData = this.war3MapViewer.getUnitTypeData(new War3ID(portraitUnitId));
		if (this.cinematicPanel.isVisible()) {
			this.rootFrame.setText(this.cinematicSpeakerText, speakerTitle);
			this.rootFrame.setText(this.cinematicDialogueText, text);
			this.cinematicPortrait.setCinematicTalkingHead(
					unitTypeData == null ? null : unitTypeData.getPortraitModel(), color.getHandleId(),
					unitTypeData.getRequiredAnimationNames(), sceneDuration);
			this.cinematicPortrait.talk(null, voiceoverDuration);
		}
		else {
			if (unitTypeData != null) {
				this.portrait.setCinematicTalkingHead(unitTypeData.getPortraitModel(), color.getHandleId(),
						unitTypeData.getRequiredAnimationNames(), sceneDuration, null, voiceoverDuration);
			}
			if (this.subtitleDisplayOverride || true) {
				showGameMessage("|Cffffcc00" + speakerTitle + "|r: " + text, sceneDuration);
			}
		}
	}

	@Override
	public void endCinematicScene() {
		this.portrait.faceLockTime = 0;
		selectUnit(this.selectedUnit);
	}

	@Override
	public void forceCinematicSubtitles(boolean value) {
		this.subtitleDisplayOverride = value;
	}

	@Override
	public void enableUserControl(boolean value) {
		this.userControlEnabled = value;
		this.cursorFrame.setVisible(value);
		if (!value) {
			this.allowDrag = false;
			this.subMenuOrderIdStack.clear();
			this.activeCommandUnit = null;
			this.activeCommand = null;
			this.activeCommandOrderId = -1;
			this.mouseDownUIFrame = null;
			this.war3MapViewer.clearUnitMouseOverHighlight();
			this.dragSelectPreviewUnits.clear();
			this.mouseOverUnit = null;
		}
	}
}
