package com.etheller.warsmash.viewer5.handlers.w3x.ui;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
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
import com.etheller.warsmash.parsers.fdf.frames.SpriteFrame;
import com.etheller.warsmash.parsers.fdf.frames.StringFrame;
import com.etheller.warsmash.parsers.fdf.frames.TextureFrame;
import com.etheller.warsmash.parsers.fdf.frames.UIFrame;
import com.etheller.warsmash.parsers.jass.Jass2.RootFrameListener;
import com.etheller.warsmash.units.Element;
import com.etheller.warsmash.units.manager.MutableObjectData;
import com.etheller.warsmash.util.FastNumberFormat;
import com.etheller.warsmash.util.ImageUtils;
import com.etheller.warsmash.util.RenderMathUtils;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.util.WarsmashConstants;
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
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.RenderUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.RenderWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.ability.AbilityDataUI;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.ability.IconUI;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.ability.ItemUI;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.commandbuttons.CommandButtonListener;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CDestructable;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CGameplayConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CItem;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CItemType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CPlayerStateListener;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit.QueueItemType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitClassification;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitStateListener;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidgetFilterFunction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityAttack;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityGeneric;
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
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.combat.CAbilityColdArrows;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.GenericNoIconAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.GenericSingleIconActiveAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.hero.CAbilityHero;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.hero.CPrimaryAttribute;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.inventory.CAbilityInventory;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.queue.CAbilityQueue;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.queue.CAbilityRally;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CAttackType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CDefenseType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CodeKeyType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttack;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttackMissileSplash;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.COrder;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.pathing.CBuildingPathingType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayerUnitOrderListener;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CRace;
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
import com.etheller.warsmash.viewer5.handlers.w3x.ui.command.QueueIconListener;
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
	private long lastErrorMessageExpireTime;
	private long lastErrorMessageFadeTime;

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
	private UIFrame smashSimpleInfoPanel;
	private SimpleFrame smashAttack1IconWrapper;
	private SimpleFrame smashAttack2IconWrapper;
	private SimpleFrame smashArmorIconWrapper;
	private final RallyPositioningVisitor rallyPositioningVisitor;
	private final CPlayer localPlayer;
	private MeleeUIAbilityActivationReceiver meleeUIAbilityActivationReceiver;
	private MdxModel waypointModel;
	private final List<MdxComplexInstance> waypointModelInstances = new ArrayList<>();
	private List<RenderUnit> selectedUnits;
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
		final CRace race = this.localPlayer.getRace();
		final String racialSkinKey;
		int racialCommandIndex;
		if (race == null) {
			racialSkinKey = "Human";
			racialCommandIndex = 0;
		}
		else {
			switch (race) {
			case HUMAN:
				racialSkinKey = "Human";
				racialCommandIndex = 0;
				break;
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
			default:
				racialSkinKey = "Human";
				racialCommandIndex = 0;
				break;
			}
		}
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
		this.damageBackdrops = new InfoPanelIconBackdrops(CAttackType.values(), this.rootFrame, "Damage", "Neutral");
		this.defenseBackdrops = new InfoPanelIconBackdrops(CDefenseType.values(), this.rootFrame, "Armor", "Neutral");

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

		final UIFrame upperButtonBar = this.rootFrame.createSimpleFrame("UpperButtonBarFrame", this.consoleUI, 0);
		upperButtonBar.addSetPoint(new SetPoint(FramePoint.TOPLEFT, this.consoleUI, FramePoint.TOPLEFT, 0, 0));

		this.questsButton = (SimpleButtonFrame) this.rootFrame.getFrameByName("UpperButtonBarQuestsButton", 0);
		this.questsButton.setEnabled(false);
		this.menuButton = (SimpleButtonFrame) this.rootFrame.getFrameByName("UpperButtonBarMenuButton", 0);
		this.alliesButton = (SimpleButtonFrame) this.rootFrame.getFrameByName("UpperButtonBarAlliesButton", 0);
		this.alliesButton.setEnabled(false);
		this.chatButton = (SimpleButtonFrame) this.rootFrame.getFrameByName("UpperButtonBarChatButton", 0);
		this.chatButton.setEnabled(false);

		final UIFrame escMenuBackdrop = this.rootFrame.createFrame("EscMenuBackdrop", this.rootFrame, 0, 0);
		escMenuBackdrop.setVisible(false);
		escMenuBackdrop.addAnchor(new AnchorDefinition(FramePoint.TOP, 0, GameUI.convertY(this.uiViewport, -0.05f)));
		final UIFrame escMenuMainPanel = this.rootFrame.createFrame("EscMenuMainPanel", this.rootFrame, 0, 0);
		escMenuMainPanel.setVisible(false);
		escMenuMainPanel.addAnchor(new AnchorDefinition(FramePoint.TOP, 0, GameUI.convertY(this.uiViewport, -0.05f)));

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
				escMenuInnerMainPanel.setVisible(true);
				updateEscMenuCurrentPanel(escMenuBackdrop, escMenuMainPanel, escMenuInnerMainPanel);
			}
		});
		returnButton.setOnClick(new Runnable() {
			@Override
			public void run() {
				escMenuBackdrop.setVisible(false);
				escMenuMainPanel.setVisible(false);
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
		positionPortrait();
		this.unitPortrait = this.rootFrame.createSimpleFrame("UnitPortrait", this.consoleUI, 0);
		this.unitLifeText = (StringFrame) this.rootFrame.getFrameByName("UnitPortraitHitPointText", 0);
		this.unitManaText = (StringFrame) this.rootFrame.getFrameByName("UnitPortraitManaPointText", 0);

		final float infoPanelUnitDetailWidth = GameUI.convertY(this.uiViewport, 0.180f);
		final float infoPanelUnitDetailHeight = GameUI.convertY(this.uiViewport, 0.112f);
		this.smashSimpleInfoPanel = this.rootFrame.createSimpleFrame("SmashSimpleInfoPanel", this.rootFrame, 0);
		this.smashSimpleInfoPanel
				.addAnchor(new AnchorDefinition(FramePoint.BOTTOM, 0, GameUI.convertY(this.uiViewport, 0.0f)));
		this.smashSimpleInfoPanel.setWidth(infoPanelUnitDetailWidth);
		this.smashSimpleInfoPanel.setHeight(infoPanelUnitDetailHeight);

		// Create Simple Info Unit Detail
		this.simpleInfoPanelUnitDetail = this.rootFrame.createSimpleFrame("SimpleInfoPanelUnitDetail",
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
		final TextureFrame simpleBuildQueueBackdrop = (TextureFrame) this.rootFrame
				.getFrameByName("SimpleBuildQueueBackdrop", 0);
		simpleBuildQueueBackdrop.setWidth(infoPanelUnitDetailWidth);
		simpleBuildQueueBackdrop.setHeight(infoPanelUnitDetailWidth * 0.5f);

		this.queueIconFrames[0] = new QueueIcon("SmashBuildQueueIcon0", this.smashSimpleInfoPanel, this, 0);
		final TextureFrame queueIconFrameBackdrop0 = new TextureFrame("SmashBuildQueueIcon0Backdrop",
				this.queueIconFrames[0], false, new Vector4Definition(0, 1, 0, 1));
		queueIconFrameBackdrop0
				.addSetPoint(new SetPoint(FramePoint.CENTER, this.queueIconFrames[0], FramePoint.CENTER, 0, 0));
		this.queueIconFrames[0].set(queueIconFrameBackdrop0);
		this.queueIconFrames[0]
				.addSetPoint(new SetPoint(FramePoint.CENTER, this.smashSimpleInfoPanel, FramePoint.BOTTOMLEFT,
						(infoPanelUnitDetailWidth * (15 + 19f)) / 256, (infoPanelUnitDetailWidth * (66 + 19f)) / 256));
		final float frontQueueIconWidth = (infoPanelUnitDetailWidth * 38) / 256;
		this.queueIconFrames[0].setWidth(frontQueueIconWidth);
		this.queueIconFrames[0].setHeight(frontQueueIconWidth);
		queueIconFrameBackdrop0.setWidth(frontQueueIconWidth);
		queueIconFrameBackdrop0.setHeight(frontQueueIconWidth);
		this.rootFrame.add(this.queueIconFrames[0]);

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
			this.rootFrame.add(this.queueIconFrames[i]);
		}
		this.selectWorkerInsideFrame = new QueueIcon("SmashBuildQueueWorkerIcon", this.smashSimpleInfoPanel, this, 1);
		final TextureFrame selectWorkerInsideIconFrameBackdrop = new TextureFrame("SmashBuildQueueWorkerIconBackdrop",
				this.queueIconFrames[0], false, new Vector4Definition(0, 1, 0, 1));
		this.selectWorkerInsideFrame.set(selectWorkerInsideIconFrameBackdrop);
		selectWorkerInsideIconFrameBackdrop
				.addSetPoint(new SetPoint(FramePoint.CENTER, this.selectWorkerInsideFrame, FramePoint.CENTER, 0, 0));
		this.selectWorkerInsideFrame
				.addSetPoint(new SetPoint(FramePoint.TOPLEFT, this.queueIconFrames[1], FramePoint.TOPLEFT, 0, 0));
		this.selectWorkerInsideFrame.setWidth(frontQueueIconWidth);
		this.selectWorkerInsideFrame.setHeight(frontQueueIconWidth);
		selectWorkerInsideIconFrameBackdrop.setWidth(frontQueueIconWidth);
		selectWorkerInsideIconFrameBackdrop.setHeight(frontQueueIconWidth);
		this.rootFrame.add(this.selectWorkerInsideFrame);

		this.smashAttack1IconWrapper = (SimpleFrame) this.rootFrame.createSimpleFrame("SmashSimpleInfoPanelIconDamage",
				this.simpleInfoPanelUnitDetail, 0);
		this.smashAttack1IconWrapper.addSetPoint(new SetPoint(FramePoint.TOPLEFT, this.simpleInfoPanelUnitDetail,
				FramePoint.TOPLEFT, 0, GameUI.convertY(this.uiViewport, -0.032f)));
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
						GameUI.convertX(this.uiViewport, 0.1f), GameUI.convertY(this.uiViewport, -0.03125f)));
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
				FramePoint.TOPLEFT, GameUI.convertX(this.uiViewport, 0f), GameUI.convertY(this.uiViewport, -0.0625f)));
		this.smashArmorIconWrapper.setWidth(GameUI.convertX(this.uiViewport, 0.1f));
		this.smashArmorIconWrapper.setHeight(GameUI.convertY(this.uiViewport, 0.030125f));
		this.armorIcon = this.rootFrame.createSimpleFrame("SimpleInfoPanelIconArmor", this.smashArmorIconWrapper, 0);
		this.armorIconBackdrop = (TextureFrame) this.rootFrame.getFrameByName("InfoPanelIconBackdrop", 0);
		this.armorInfoPanelIconValue = (StringFrame) this.rootFrame.getFrameByName("InfoPanelIconValue", 0);
		this.armorInfoPanelIconLevel = (StringFrame) this.rootFrame.getFrameByName("InfoPanelIconLevel", 0);

		this.smashHeroInfoPanelWrapper = (SimpleFrame) this.rootFrame.createSimpleFrame("SmashSimpleInfoPanelIconHero",
				this.simpleInfoPanelUnitDetail, 0);
		this.smashHeroInfoPanelWrapper.addSetPoint(new SetPoint(FramePoint.TOPLEFT, this.simpleInfoPanelUnitDetail,
				FramePoint.TOPLEFT, GameUI.convertX(this.uiViewport, 0.1f), GameUI.convertY(this.uiViewport, -0.029f)));
		this.smashHeroInfoPanelWrapper.setWidth(GameUI.convertX(this.uiViewport, 0.1f));
		this.smashHeroInfoPanelWrapper.setHeight(GameUI.convertY(this.uiViewport, 0.0625f));
		this.heroInfoPanel = this.rootFrame.createSimpleFrame("SimpleInfoPanelIconHero", this.smashHeroInfoPanelWrapper,
				0);
		this.primaryAttributeIcon = (TextureFrame) this.rootFrame.getFrameByName("InfoPanelIconHeroIcon", 0);
		this.strengthValue = (StringFrame) this.rootFrame.getFrameByName("InfoPanelIconHeroStrengthValue", 0);
		this.agilityValue = (StringFrame) this.rootFrame.getFrameByName("InfoPanelIconHeroAgilityValue", 0);
		this.intelligenceValue = (StringFrame) this.rootFrame.getFrameByName("InfoPanelIconHeroIntellectValue", 0);

		this.inventoryBarFrame = (SimpleFrame) this.rootFrame.createSimpleFrame("SmashSimpleInventoryBar",
				this.rootFrame, 0);
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
		for (int j = 0; j < INVENTORY_HEIGHT; j++) {
			for (int i = 0; i < INVENTORY_WIDTH; i++) {
				final CommandCardIcon commandCardIcon = new CommandCardIcon(
						"SmashInventoryButton_" + commandButtonIndex, this.inventoryBarFrame,
						this.itemCommandCardCommandListener);
				this.inventoryBarFrame.add(commandCardIcon);
				final TextureFrame iconFrame = new TextureFrame(
						"SmashInventoryButton_" + (commandButtonIndex) + "_Icon", this.rootFrame, false, null);
				final SpriteFrame cooldownFrame = (SpriteFrame) this.rootFrame.createFrameByType("SPRITE",
						"SmashInventoryButton_" + (commandButtonIndex) + "_Cooldown", this.rootFrame, "", 0);
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
				commandCardIcon.set(iconFrame, null, cooldownFrame, null);
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
		this.inventoryTitleFrame.setWidth(GameUI.convertX(this.uiViewport, 0.071f));
		this.inventoryTitleFrame.setHeight(GameUI.convertX(this.uiViewport, 0.01125f));
		this.inventoryTitleFrame.setFontShadowColor(new Color(0f, 0f, 0f, 0.9f));
		this.inventoryTitleFrame.setFontShadowOffsetX(GameUI.convertX(this.uiViewport, 0.001f));
		this.inventoryTitleFrame.setFontShadowOffsetY(GameUI.convertY(this.uiViewport, -0.001f));
		this.consoleInventoryNoCapacityTexture = ImageUtils.getAnyExtensionTexture(this.dataSource,
				this.rootFrame.getSkinField("ConsoleInventoryNoCapacity"));

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

		commandButtonIndex = 0;
		for (int j = 0; j < COMMAND_CARD_HEIGHT; j++) {
			for (int i = 0; i < COMMAND_CARD_WIDTH; i++) {
				final CommandCardIcon commandCardIcon = new CommandCardIcon("SmashCommandButton_" + commandButtonIndex,
						this.rootFrame, this);
				this.rootFrame.add(commandCardIcon);
				final TextureFrame iconFrame = new TextureFrame("SmashCommandButton_" + (commandButtonIndex) + "_Icon",
						this.rootFrame, false, null);
				final FilterModeTextureFrame activeHighlightFrame = new FilterModeTextureFrame(
						"SmashCommandButton_" + (commandButtonIndex) + "_ActiveHighlight", this.rootFrame, true, null);
				activeHighlightFrame.setFilterMode(FilterMode.ADDALPHA);
				final SpriteFrame cooldownFrame = (SpriteFrame) this.rootFrame.createFrameByType("SPRITE",
						"SmashCommandButton_" + (commandButtonIndex) + "_Cooldown", this.rootFrame, "", 0);
				final SpriteFrame autocastFrame = (SpriteFrame) this.rootFrame.createFrameByType("SPRITE",
						"SmashCommandButton_" + (commandButtonIndex) + "_Autocast", this.rootFrame, "", 0);
				commandCardIcon.addAnchor(new AnchorDefinition(FramePoint.BOTTOMLEFT,
						GameUI.convertX(this.uiViewport, 0.6175f + (0.0434f * i)),
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
				cooldownFrame.addSetPoint(new SetPoint(FramePoint.CENTER, commandCardIcon, FramePoint.CENTER, 0, 0));
				this.rootFrame.setSpriteFrameModel(cooldownFrame, this.rootFrame.getSkinField("CommandButtonCooldown"));
				cooldownFrame.setWidth(GameUI.convertX(this.uiViewport, DEFAULT_COMMAND_CARD_ICON_WIDTH));
				cooldownFrame.setHeight(GameUI.convertY(this.uiViewport, DEFAULT_COMMAND_CARD_ICON_WIDTH));
				autocastFrame.addSetPoint(new SetPoint(FramePoint.CENTER, commandCardIcon, FramePoint.CENTER, 0, 0));
				this.rootFrame.setSpriteFrameModel(autocastFrame, this.rootFrame.getSkinField("CommandButtonAutocast"));
				autocastFrame.setWidth(GameUI.convertX(this.uiViewport, DEFAULT_COMMAND_CARD_ICON_WIDTH));
				autocastFrame.setHeight(GameUI.convertY(this.uiViewport, DEFAULT_COMMAND_CARD_ICON_WIDTH));
				commandCardIcon.set(iconFrame, activeHighlightFrame, cooldownFrame, autocastFrame);
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
//		this.tooltipFrame = this.rootFrame.createFrameByType("BACKDROP", "SmashToolTipBackdrop", this.rootFrame, "", 0);

		this.cursorFrame = (SpriteFrame) this.rootFrame.createFrameByType("SPRITE", "SmashCursorFrame", this.rootFrame,
				"", 0);
		this.rootFrame.setSpriteFrameModel(this.cursorFrame, this.rootFrame.getSkinField("Cursor"));
		this.cursorFrame.setSequence("Normal");
		this.cursorFrame.setZDepth(-1.0f);
		Gdx.input.setCursorCatched(true);

		this.meleeUIMinimap = createMinimap(this.war3MapViewer);

		this.meleeUIAbilityActivationReceiver = new MeleeUIAbilityActivationReceiver(
				new AbilityActivationErrorHandler(this.rootFrame.getErrorString("NoGold"),
						this.war3MapViewer.getUiSounds().getSound(this.rootFrame.getSkinField("NoGoldSound"))),
				new AbilityActivationErrorHandler(this.rootFrame.getErrorString("NoLumber"),
						this.war3MapViewer.getUiSounds().getSound(this.rootFrame.getSkinField("NoLumberSound"))),
				new AbilityActivationErrorHandler(this.rootFrame.getErrorString("NoFood"),
						this.war3MapViewer.getUiSounds().getSound(this.rootFrame.getSkinField("NoFoodSound"))),
				new AbilityActivationErrorHandler("", this.war3MapViewer.getUiSounds().getSound("InterfaceError")));

		final MdxModel rallyModel = (MdxModel) this.war3MapViewer.load(
				War3MapViewer.mdx(this.rootFrame.getSkinField("RallyIndicatorDst")), this.war3MapViewer.mapPathSolver,
				this.war3MapViewer.solverParams);
		this.rallyPointInstance = (MdxComplexInstance) rallyModel.addInstance();
		this.rallyPointInstance.rotate(RenderUnit.tempQuat.setFromAxis(RenderMathUtils.VEC3_UNIT_Z,
				this.war3MapViewer.simulation.getGameplayConstants().getBuildingAngle()));
		this.rallyPointInstance.setSequenceLoopMode(SequenceLoopMode.ALWAYS_LOOP);
		SequenceUtils.randomStandSequence(this.rallyPointInstance);
		this.rallyPointInstance.hide();
		this.waypointModel = (MdxModel) this.war3MapViewer.load(
				War3MapViewer.mdx(this.rootFrame.getSkinField("WaypointIndicator")), this.war3MapViewer.mapPathSolver,
				this.war3MapViewer.solverParams);

		final FreeTypeFontParameter fontParam = new FreeTypeFontParameter();
		fontParam.size = (int) GameUI.convertY(this.uiViewport, 0.012f);
		this.textTagFont = this.rootFrame.getFontGenerator().generateFont(fontParam);

		this.rootFrame.positionBounds(this.rootFrame, this.uiViewport);

		selectUnit(null);

	}

	private void updateEscMenuCurrentPanel(final UIFrame escMenuBackdrop, final UIFrame escMenuMainPanel,
			final UIFrame escMenuInnerMainPanel) {
		escMenuMainPanel.setHeight(escMenuInnerMainPanel.getAssignedHeight());
		escMenuMainPanel.setWidth(escMenuInnerMainPanel.getAssignedWidth());
		escMenuBackdrop.setHeight(escMenuInnerMainPanel.getAssignedHeight());
		escMenuBackdrop.setWidth(escMenuInnerMainPanel.getAssignedWidth());
		escMenuMainPanel.positionBounds(MeleeUI.this.rootFrame, MeleeUI.this.uiViewport);
		escMenuBackdrop.positionBounds(MeleeUI.this.rootFrame, MeleeUI.this.uiViewport);
	}

	@Override
	public void onClick(final int abilityHandleId, final int orderId, final boolean rightClick) {
		// TODO not O(N)
		if (this.selectedUnit == null) {
			return;
		}
		if (orderId == 0) {
			return;
		}
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
					this.unitOrderListener.issueImmediateOrder(this.selectedUnit.getSimulationUnit().getHandleId(),
							abilityHandleId, orderId, isShiftDown());
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
	public void showCommandError(final String message) {
		this.rootFrame.setText(this.errorMessageFrame, message);
		this.errorMessageFrame.setVisible(true);
		final long millis = TimeUtils.millis();
		this.lastErrorMessageExpireTime = millis + WORLD_FRAME_MESSAGE_EXPIRE_MILLIS;
		this.lastErrorMessageFadeTime = millis + WORLD_FRAME_MESSAGE_FADEOUT_MILLIS;
		this.errorMessageFrame.setAlpha(1.0f);
	}

	@Override
	public void showCantPlaceError() {
		showCommandError(this.rootFrame.getErrorString("Cantplace"));
		this.war3MapViewer.getUiSounds().getSound(this.rootFrame.getSkinField("CantPlaceSound"))
				.play(this.uiScene.audioContext, 0, 0, 0);
	}

	@Override
	public void showNoFoodError() {
		showCommandError(this.rootFrame.getErrorString("NoFood"));
		this.war3MapViewer.getUiSounds().getSound(this.rootFrame.getSkinField("NoFoodSound"))
				.play(this.uiScene.audioContext, 0, 0, 0);
	}

	@Override
	public void showInventoryFullError() {
		showCommandError(this.rootFrame.getErrorString("InventoryFull"));
		this.war3MapViewer.getUiSounds().getSound(this.rootFrame.getSkinField("InventoryFullSound"))
				.play(this.uiScene.audioContext, 0, 0, 0);
	}

	public void update(final float deltaTime) {
		this.portrait.update();

		final int baseMouseX = Gdx.input.getX();
		int mouseX = baseMouseX;
		final int baseMouseY = Gdx.input.getY();
		int mouseY = baseMouseY;
		final int minX = this.uiViewport.getScreenX();
		final int maxX = minX + this.uiViewport.getScreenWidth();
		final int minY = this.uiViewport.getScreenY();
		final int maxY = minY + this.uiViewport.getScreenHeight();
		final boolean left = mouseX <= (minX + 3);
		final boolean right = mouseX >= (maxX - 3);
		final boolean up = mouseY <= (minY + 3);
		final boolean down = mouseY >= (maxY - 3);
		this.cameraManager.applyVelocity(deltaTime, up, down, left, right);

		mouseX = Math.max(minX, Math.min(maxX, mouseX));
		mouseY = Math.max(minY, Math.min(maxY, mouseY));
		if (Gdx.input.isCursorCatched()) {
			Gdx.input.setCursorPosition(mouseX, mouseY);
		}

		screenCoordsVector.set(mouseX, mouseY);
		this.uiViewport.unproject(screenCoordsVector);
		this.cursorFrame.setFramePointX(FramePoint.LEFT, screenCoordsVector.x);
		this.cursorFrame.setFramePointY(FramePoint.BOTTOM, screenCoordsVector.y);

		if (this.activeCommand != null) {
			if (this.draggingItem != null) {
				this.cursorFrame.setSequence("HoldItem");
			}
			else {
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
					this.cursorFrame.setSequence("Scroll Down Left");
				}
				else if (right) {
					this.cursorFrame.setSequence("Scroll Down Right");
				}
				else {
					this.cursorFrame.setSequence("Scroll Down");
				}
			}
			else if (up) {
				if (left) {
					this.cursorFrame.setSequence("Scroll Up Left");
				}
				else if (right) {
					this.cursorFrame.setSequence("Scroll Up Right");
				}
				else {
					this.cursorFrame.setSequence("Scroll Up");
				}
			}
			else if (left) {
				this.cursorFrame.setSequence("Scroll Left");
			}
			else if (right) {
				this.cursorFrame.setSequence("Scroll Right");
			}
			else {
				this.cursorFrame.setSequence("Normal");
			}
		}
		if (this.selectedUnit != null) {
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

		final float groundHeight = Math.max(
				this.war3MapViewer.terrain.getGroundHeight(this.cameraManager.target.x, this.cameraManager.target.y),
				this.war3MapViewer.terrain.getWaterHeight(this.cameraManager.target.x, this.cameraManager.target.y));
		this.cameraManager.updateTargetZ(groundHeight);
		this.cameraManager.updateCamera();
		final long currentMillis = TimeUtils.millis();
		if (currentMillis > this.lastErrorMessageExpireTime) {
			this.errorMessageFrame.setVisible(false);
		}
		else if (currentMillis > this.lastErrorMessageFadeTime) {
			final float fadeAlpha = (this.lastErrorMessageExpireTime - currentMillis)
					/ (float) WORLD_FRAME_MESSAGE_FADE_DURATION;
			this.errorMessageFrame.setAlpha(fadeAlpha);
		}
	}

	public void render(final SpriteBatch batch, final GlyphLayout glyphLayout) {
		final BitmapFont font = this.rootFrame.getFont();
		font.setColor(Color.YELLOW);
		final String fpsString = "FPS: " + Gdx.graphics.getFramesPerSecond();
		glyphLayout.setText(font, fpsString);
		font.draw(batch, fpsString, (this.uiViewport.getMinWorldWidth() - glyphLayout.width) / 2,
				1100 * this.heightRatioCorrection);
		this.rootFrame.render(batch, this.rootFrame.getFont20(), glyphLayout);
		if (this.selectedUnit != null) {
			this.rootFrame.getFont20().setColor(Color.WHITE);

		}

		this.meleeUIMinimap.render(batch, this.war3MapViewer.units);
		this.timeIndicator.setFrameByRatio(this.war3MapViewer.simulation.getGameTimeOfDay()
				/ this.war3MapViewer.simulation.getGameplayConstants().getGameDayHours());
		for (final TextTag textTag : this.war3MapViewer.textTags) {
			this.war3MapViewer.worldScene.camera.worldToScreen(screenCoordsVector, textTag.getPosition());
			if (this.war3MapViewer.worldScene.camera.rect.contains(screenCoordsVector.x,
					(Gdx.graphics.getHeight() - screenCoordsVector.y) + textTag.getScreenCoordsZHeight())) {
				final Vector2 unprojected = this.uiViewport.unproject(screenCoordsVector);
				final float remainingLife = textTag.getRemainingLife();
				final float alpha = (remainingLife > 1.0f ? 1.0f : remainingLife);
				this.textTagFont.setColor(textTag.getColor().r, textTag.getColor().g, textTag.getColor().b,
						textTag.getColor().a * alpha);
				glyphLayout.setText(this.textTagFont, textTag.getText());
				this.textTagFont.draw(batch, textTag.getText(), unprojected.x - (glyphLayout.width / 2),
						(unprojected.y - (glyphLayout.height / 2)) + textTag.getScreenCoordsZHeight());
			}
		}
	}

	public void portraitTalk() {
		this.portrait.talk();
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
				for (final CUnitAttack attack : MeleeUI.this.activeCommandUnit.getSimulationUnit().getAttacks()) {
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
		public Void accept(final CAbilityGeneric ability) {
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
			if (MeleeUI.this.cursorModelInstance != null) {
				MeleeUI.this.cursorModelInstance.detach();
				MeleeUI.this.cursorModelInstance = null;
				MeleeUI.this.cursorFrame.setVisible(true);
			}
			MeleeUI.this.cursorFrame.setSequence("Target");
		}

		private void handleBuildCursor(final AbstractCAbilityBuild ability) {
			boolean justLoaded = false;
			final War3MapViewer viewer = MeleeUI.this.war3MapViewer;
			if (MeleeUI.this.cursorModelInstance == null) {
				final MutableObjectData unitData = viewer.getAllObjectData().getUnits();
				final War3ID buildingTypeId = new War3ID(MeleeUI.this.activeCommandOrderId);
				MeleeUI.this.cursorBuildingUnitType = viewer.simulation.getUnitData().getUnitType(buildingTypeId);
				final String unitModelPath = viewer.getUnitModelPath(unitData.get(buildingTypeId));
				final MdxModel model = (MdxModel) viewer.load(unitModelPath, viewer.mapPathSolver, viewer.solverParams);
				MeleeUI.this.cursorModelInstance = (MdxComplexInstance) model.addInstance();
//				MeleeUI.this.cursorModelInstance.setVertexColor(new float[] { 1, 1, 1, 0.5f });
				final int playerColorIndex = viewer.simulation
						.getPlayer(MeleeUI.this.activeCommandUnit.getSimulationUnit().getPlayerIndex()).getColor();
				MeleeUI.this.cursorModelInstance.setTeamColor(playerColorIndex);
				MeleeUI.this.cursorModelInstance.rotate(RenderUnit.tempQuat.setFromAxis(RenderMathUtils.VEC3_UNIT_Z,
						viewer.simulation.getGameplayConstants().getBuildingAngle()));
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
							new ArrayList<>(), viewer.terrain.centerOffset, new ArrayList<>(), true, false, true);
					MeleeUI.this.cursorModelUnderneathPathingRedGreenSplatModel.color[3] = 0.20f;
				}
			}
			viewer.getClickLocation(clickLocationTemp, this.baseMouseX, Gdx.graphics.getHeight() - this.baseMouseY);
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
				if (blockAll) {
					for (int i = 0; i < MeleeUI.this.cursorModelUnderneathPathingRedGreenPixmap.getWidth(); i++) {
						for (int j = 0; j < MeleeUI.this.cursorModelUnderneathPathingRedGreenPixmap.getHeight(); j++) {
							MeleeUI.this.cursorModelUnderneathPathingRedGreenPixmap.drawPixel(i,
									MeleeUI.this.cursorModelUnderneathPathingRedGreenPixmap.getHeight() - 1 - j,
									Color.rgba8888(1, 0, 0, 1.0f));
						}
					}
				}
				else {
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
			viewer.getClickLocation(clickLocationTemp, this.baseMouseX, Gdx.graphics.getHeight() - this.baseMouseY);
			if (MeleeUI.this.placementCursor == null) {
				MeleeUI.this.placementCursor = viewer.terrain.addUberSplat(
						MeleeUI.this.rootFrame.getSkinField("PlacementCursor"), clickLocationTemp.x,
						clickLocationTemp.y, 10, radius, true, true, true);
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
				if (attachment.getName().startsWith("sprite")) {
					index = i;
					break;
				}
			}
			if (index == -1) {
				for (int i = 0; i < model.attachments.size(); i++) {
					final Attachment attachment = model.attachments.get(i);
					if (attachment.getName().startsWith("overhead ref")) {
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
		@Override
		public boolean call(final CWidget unit) {
			final BooleanAbilityTargetCheckReceiver<CWidget> targetReceiver = BooleanAbilityTargetCheckReceiver
					.<CWidget>getInstance();
			MeleeUI.this.activeCommand.checkCanTarget(MeleeUI.this.war3MapViewer.simulation,
					MeleeUI.this.activeCommandUnit.getSimulationUnit(), MeleeUI.this.activeCommandOrderId, unit,
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

		public Portrait(final War3MapViewer war3MapViewer, final Scene portraitScene) {
			this.portraitScene = portraitScene;
			this.portraitCameraManager = new PortraitCameraManager();
			this.portraitCameraManager.setupCamera(this.portraitScene);
			this.portraitScene.camera.viewport(new Rectangle(100, 0, 6400, 48));
		}

		public void update() {
			this.portraitCameraManager.updateCamera();
			if ((this.modelInstance != null)
					&& (this.modelInstance.sequenceEnded || (this.modelInstance.sequence == -1))) {
				this.recycleSet.clear();
				this.recycleSet.addAll(this.unit.getSecondaryAnimationTags());
				SequenceUtils.randomSequence(this.modelInstance, PrimaryTag.PORTRAIT, this.recycleSet, true);
			}
		}

		public void talk() {
			this.recycleSet.clear();
			this.recycleSet.addAll(this.unit.getSecondaryAnimationTags());
			this.recycleSet.add(SecondaryTag.TALK);
			SequenceUtils.randomSequence(this.modelInstance, PrimaryTag.PORTRAIT, this.recycleSet, true);
		}

		public void setSelectedUnit(final RenderUnit unit) {
			this.unit = unit;
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
					this.modelInstance.setSequenceLoopMode(SequenceLoopMode.NEVER_LOOP);
					this.modelInstance.setScene(this.portraitScene);
					this.modelInstance.setVertexColor(unit.instance.vertexColor);
					this.modelInstance.setTeamColor(unit.playerIndex);
				}
			}
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
							inventoryIcon.setCommandButtonData(null, 0, 0, index + 1, true, false, false, null, null, 0,
									0, 0);
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
		if (rallyPoint != null) {
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
		for (; (orderIndex < this.waypointModelInstances.size()) || (iterator.hasNext()); orderIndex++) {
			final MdxComplexInstance waypointModelInstance = getOrCreateWaypointIndicator(orderIndex);
			if (iterator.hasNext()) {
				final COrder order = iterator.next();
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
		final CUnit simulationUnit = unit.getSimulationUnit();
		this.rootFrame.setText(this.unitLifeText,
				FastNumberFormat.formatWholeNumber(simulationUnit.getLife()) + " / " + simulationUnit.getMaximumLife());
		final int maximumMana = simulationUnit.getMaximumMana();
		if (maximumMana > 0) {
			this.rootFrame.setText(this.unitManaText,
					FastNumberFormat.formatWholeNumber(simulationUnit.getMana()) + " / " + maximumMana);
		}
		else {
			this.rootFrame.setText(this.unitManaText, "");
		}
		repositionRallyPoint(simulationUnit);
		repositionWaypointFlags(simulationUnit);
		if ((simulationUnit.getBuildQueue()[0] != null)
				&& (simulationUnit.getPlayerIndex() == this.war3MapViewer.getLocalPlayerIndex())) {
			for (int i = 0; i < this.queueIconFrames.length; i++) {
				final QueueItemType queueItemType = simulationUnit.getBuildQueueTypes()[i];
				if (queueItemType == null) {
					this.queueIconFrames[i].setVisible(false);
				}
				else {
					this.queueIconFrames[i].setVisible(true);
					switch (queueItemType) {
					case RESEARCH:
						final IconUI upgradeUI = this.war3MapViewer.getAbilityDataUI()
								.getUpgradeUI(simulationUnit.getBuildQueue()[i], 0);
						this.queueIconFrames[i].setTexture(upgradeUI.getIcon());
						this.queueIconFrames[i].setToolTip(upgradeUI.getToolTip());
						this.queueIconFrames[i].setUberTip(upgradeUI.getUberTip());
						break;
					case UNIT:
					default:
						final IconUI unitUI = this.war3MapViewer.getAbilityDataUI()
								.getUnitUI(simulationUnit.getBuildQueue()[i]);
						this.queueIconFrames[i].setTexture(unitUI.getIcon());
						this.queueIconFrames[i].setToolTip(unitUI.getToolTip());
						this.queueIconFrames[i].setUberTip(unitUI.getUberTip());
						break;
					}
				}
			}
			this.simpleInfoPanelBuildingDetail.setVisible(true);
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
			else {
				this.rootFrame.setText(this.simpleBuildingBuildingActionLabel,
						this.rootFrame.getTemplates().getDecoratedString("RESEARCHING"));
			}
			this.attack1Icon.setVisible(false);
			this.attack2Icon.setVisible(false);
			this.armorIcon.setVisible(false);
			this.heroInfoPanel.setVisible(false);
			this.selectWorkerInsideFrame.setVisible(false);
		}
		else {
			for (final QueueIcon queueIconFrame : this.queueIconFrames) {
				queueIconFrame.setVisible(false);
			}
			this.simpleInfoPanelBuildingDetail.setVisible(false);
			this.simpleInfoPanelUnitDetail.setVisible(true);
			final String unitTypeName = simulationUnit.getUnitType().getName();

			final boolean anyAttacks = simulationUnit.getAttacks().size() > 0;
			final boolean constructing = simulationUnit.isConstructing();
			final UIFrame localArmorIcon = this.armorIcon;
			final TextureFrame localArmorIconBackdrop = this.armorIconBackdrop;
			final StringFrame localArmorInfoPanelIconValue = this.armorInfoPanelIconValue;
			if (anyAttacks && !constructing) {
				final CUnitAttack attackOne = simulationUnit.getAttacks().get(0);
				this.attack1Icon.setVisible(attackOne.isShowUI());
				this.attack1IconBackdrop.setTexture(this.damageBackdrops.getTexture(attackOne.getAttackType()));
				String attackOneDmgText = attackOne.getMinDamageDisplay() + " - " + attackOne.getMaxDamageDisplay();
				final int attackOneTemporaryDamageBonus = attackOne.getTemporaryDamageBonus();
				if (attackOneTemporaryDamageBonus != 0) {
					attackOneDmgText += (attackOneTemporaryDamageBonus > 0 ? "|cFF00FF00 (+" : "|cFFFF0000 (+")
							+ attackOneTemporaryDamageBonus + ")";
				}
				this.rootFrame.setText(this.attack1InfoPanelIconValue, attackOneDmgText);
				if (simulationUnit.getAttacks().size() > 1) {
					final CUnitAttack attackTwo = simulationUnit.getAttacks().get(1);
					this.attack2Icon.setVisible(attackTwo.isShowUI());
					this.attack2IconBackdrop.setTexture(this.damageBackdrops.getTexture(attackTwo.getAttackType()));
					String attackTwoDmgText = attackTwo.getMinDamage() + " - " + attackTwo.getMaxDamage();
					final int attackTwoTemporaryDamageBonus = attackTwo.getTemporaryDamageBonus();
					if (attackTwoTemporaryDamageBonus != 0) {
						attackTwoDmgText += (attackTwoTemporaryDamageBonus > 0 ? "|cFF00FF00 (+" : "|cFFFF0000 (+")
								+ attackTwoTemporaryDamageBonus + ")";
					}
					this.rootFrame.setText(this.attack2InfoPanelIconValue, attackTwoDmgText);
				}
				else {
					this.attack2Icon.setVisible(false);
				}

				this.smashArmorIconWrapper.addSetPoint(
						new SetPoint(FramePoint.TOPLEFT, this.simpleInfoPanelUnitDetail, FramePoint.TOPLEFT,
								GameUI.convertX(this.uiViewport, 0f), GameUI.convertY(this.uiViewport, -0.0625f)));
				this.smashArmorIconWrapper.positionBounds(this.rootFrame, this.uiViewport);
				this.armorIcon.positionBounds(this.rootFrame, this.uiViewport);
			}
			else {
				this.attack1Icon.setVisible(false);
				this.attack2Icon.setVisible(false);

				this.smashArmorIconWrapper.addSetPoint(
						new SetPoint(FramePoint.TOPLEFT, this.simpleInfoPanelUnitDetail, FramePoint.TOPLEFT,
								GameUI.convertX(this.uiViewport, 0f), GameUI.convertY(this.uiViewport, -0.032f)));
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
				this.rootFrame.setText(this.simpleClassValue,
						String.format(infopanelLevelClass, heroLevel, unitTypeName));
				this.rootFrame.setText(this.simpleNameValue, heroData.getProperName());
				this.simpleHeroLevelBar.setVisible(true);
				final CGameplayConstants gameplayConstants = this.war3MapViewer.simulation.getGameplayConstants();
				this.simpleHeroLevelBar.setValue((heroData.getXp() - gameplayConstants.getNeedHeroXPSum(heroLevel - 1))
						/ (float) gameplayConstants.getNeedHeroXP(heroLevel));
			}
			else {
				this.rootFrame.setText(this.simpleNameValue, unitTypeName);
				String classText = null;
				for (final CUnitClassification classification : simulationUnit.getClassifications()) {
					if ((classification == CUnitClassification.MECHANICAL)
							&& simulationUnit.getUnitType().isBuilding()) {
						// buildings dont display MECHANICAL
						continue;
					}
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
			final CAbilityInventory inventory = simulationUnit.getInventoryData();
			this.inventoryCover.setVisible(inventory == null);
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
							final boolean activelyUsed = itemType.isActivelyUsed();
							final boolean pawnable = itemType.isPawnable();
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
							this.recycleStringBuilder.append(uberTip);
							inventoryIcon.setCommandButtonData(iconUI.getIcon(), 0,
									activelyUsed ? itemType.getCooldownGroup().getValue() : 0, index + 1, activelyUsed,
									false, false, itemUI.getName(), this.recycleStringBuilder.toString(),
									itemType.getGoldCost(), itemType.getLumberCost(), 0);
						}
						else {
							if (index >= inventory.getItemCapacity()) {
								inventoryIcon.setCommandButtonData(this.consoleInventoryNoCapacityTexture, 0, 0, 0,
										false, false, false, null, null, 0, 0, 0);
							}
							else {
								if (this.draggingItem != null) {
									inventoryIcon.setCommandButtonData(null, 0, 0, index + 1, true, false, false, null,
											null, 0, 0, 0);
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

			localArmorIcon.setVisible(!constructing);
			this.simpleBuildTimeIndicator.setVisible(constructing);
			this.simpleBuildingBuildTimeIndicator.setVisible(false);
			if (constructing) {
				this.rootFrame.setText(this.simpleBuildingActionLabel,
						this.rootFrame.getTemplates().getDecoratedString("CONSTRUCTING"));
				this.queueIconFrames[0].setVisible(true);
				this.queueIconFrames[0].setTexture(
						this.war3MapViewer.getAbilityDataUI().getUnitUI(simulationUnit.getTypeId()).getIcon());

				if (simulationUnit.getWorkerInside() != null) {
					this.selectWorkerInsideFrame.setVisible(true);
					this.selectWorkerInsideFrame.setTexture(this.war3MapViewer.getAbilityDataUI()
							.getUnitUI(simulationUnit.getWorkerInside().getTypeId()).getIcon());
				}
				else {
					this.selectWorkerInsideFrame.setVisible(false);
				}
			}
			else {
				this.rootFrame.setText(this.simpleBuildingActionLabel, "");
				this.selectWorkerInsideFrame.setVisible(false);
			}
			final Texture defenseTexture = this.defenseBackdrops
					.getTexture(simulationUnit.getUnitType().getDefenseType());
			if (defenseTexture == null) {
				throw new RuntimeException(simulationUnit.getUnitType().getDefenseType() + " can't find texture!");
			}
			localArmorIconBackdrop.setTexture(defenseTexture);

			String defenseDisplayString = Integer.toString(simulationUnit.getCurrentDefenseDisplay());
			final int temporaryDefenseBonus = simulationUnit.getTemporaryDefenseBonus();
			if (temporaryDefenseBonus != 0) {
				if (temporaryDefenseBonus > 0) {
					defenseDisplayString += "|cFF00FF00 (+" + temporaryDefenseBonus + ")";
				}
				else {
					defenseDisplayString += "|cFFFF0000 (+" + temporaryDefenseBonus + ")";
				}
			}
			this.rootFrame.setText(localArmorInfoPanelIconValue, defenseDisplayString);
		}
		clearAndRepopulateCommandCard();
	}

	private void clearCommandCard() {
		for (int j = 0; j < COMMAND_CARD_HEIGHT; j++) {
			for (int i = 0; i < COMMAND_CARD_WIDTH; i++) {
				this.commandCard[j][i].clear();
			}
		}
	}

	@Override
	public void commandButton(final int buttonPositionX, final int buttonPositionY, final Texture icon,
			final int abilityHandleId, final int orderId, final int autoCastId, final boolean active,
			final boolean autoCastActive, final boolean menuButton, final String tip, final String uberTip,
			final int goldCost, final int lumberCost, final int foodCost) {
		int x = Math.max(0, Math.min(COMMAND_CARD_WIDTH - 1, buttonPositionX));
		int y = Math.max(0, Math.min(COMMAND_CARD_HEIGHT - 1, buttonPositionY));
		while ((x < COMMAND_CARD_WIDTH) && (y < COMMAND_CARD_HEIGHT) && this.commandCard[y][x].isVisible()) {
			x++;
			if (x >= COMMAND_CARD_WIDTH) {
				x = 0;
				y++;
			}
		}
		if ((x < COMMAND_CARD_WIDTH) && (y < COMMAND_CARD_HEIGHT)) {
			this.commandCard[y][x].setCommandButtonData(icon, abilityHandleId, orderId, autoCastId, active,
					autoCastActive, menuButton, tip, uberTip, goldCost, lumberCost, foodCost);
		}
	}

	public void resize(final Rectangle viewport) {
		this.cameraManager.resize(viewport);
		positionPortrait();
	}

	public void positionPortrait() {
		this.projectionTemp1.x = 422 * this.widthRatioCorrection;
		this.projectionTemp1.y = 57 * this.heightRatioCorrection;
		this.projectionTemp2.x = (422 + 167) * this.widthRatioCorrection;
		this.projectionTemp2.y = (57 + 170) * this.heightRatioCorrection;
		this.uiViewport.project(this.projectionTemp1);
		this.uiViewport.project(this.projectionTemp2);

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
				final Texture suffixTexture = gameUI.loadTexture(gameUI.getSkinField(skinLookupKey));
				if (suffixTexture != null) {
					this.damageBackdropTextures[index] = suffixTexture;
				}
				else {
					skinLookupKey = "InfoPanelIcon" + prefix + attackType.getCodeKey();
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

		private static String getSuffix(final CAttackType attackType) {
			switch (attackType) {
			case CHAOS:
				return "Chaos";
			case HERO:
				return "Hero";
			case MAGIC:
				return "Magic";
			case NORMAL:
				return "Normal";
			case PIERCE:
				return "Pierce";
			case SIEGE:
				return "Siege";
			case SPELLS:
				return "Magic";
			case UNKNOWN:
				return "Unknown";
			default:
				throw new IllegalArgumentException("Unknown attack type: " + attackType);
			}

		}
	}

	@Override
	public void lifeChanged() {
		if (this.selectedUnit.getSimulationUnit().isDead()) {
			selectUnit(null);
		}
		else {
			this.rootFrame.setText(this.unitLifeText,
					FastNumberFormat.formatWholeNumber(this.selectedUnit.getSimulationUnit().getLife()) + " / "
							+ this.selectedUnit.getSimulationUnit().getMaximumLife());
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
	public void inventoryChanged() {
		reloadSelectedUnitUI(this.selectedUnit);
	}

	@Override
	public void queueChanged() {
		reloadSelectedUnitUI(this.selectedUnit);
	}

	private void clearAndRepopulateCommandCard() {
		clearCommandCard();
		if (this.selectedUnit.getSimulationUnit().getPlayerIndex() == this.war3MapViewer.getLocalPlayerIndex()) {
			final AbilityDataUI abilityDataUI = this.war3MapViewer.getAbilityDataUI();
			final int menuOrderId = getSubMenuOrderId();
			if ((this.activeCommand != null) && (this.draggingItem == null)) {
				final IconUI cancelUI = abilityDataUI.getCancelUI();
				this.commandButton(cancelUI.getButtonPositionX(), cancelUI.getButtonPositionY(), cancelUI.getIcon(), 0,
						menuOrderId, 0, false, false, true, cancelUI.getToolTip(), cancelUI.getUberTip(), 0, 0, 0);
			}
			else {
				if (menuOrderId != 0) {
					final int exitOrderId = this.subMenuOrderIdStack.size() > 1
							? this.subMenuOrderIdStack.get(this.subMenuOrderIdStack.size() - 2)
							: 0;
					final IconUI cancelUI = abilityDataUI.getCancelUI();
					this.commandButton(cancelUI.getButtonPositionX(), cancelUI.getButtonPositionY(), cancelUI.getIcon(),
							0, exitOrderId, 0, false, false, true, cancelUI.getToolTip(), cancelUI.getUberTip(), 0, 0,
							0);
				}
				this.selectedUnit.populateCommandCard(this.war3MapViewer.simulation, this.rootFrame, this,
						abilityDataUI, menuOrderId);
			}
		}
	}

	private int getSubMenuOrderId() {
		return this.subMenuOrderIdStack.isEmpty() ? 0
				: this.subMenuOrderIdStack.get(this.subMenuOrderIdStack.size() - 1);
	}

	public RenderUnit getSelectedUnit() {
		return this.selectedUnit;
	}

	public boolean keyDown(final int keycode) {
		return this.cameraManager.keyDown(keycode);
	}

	public boolean keyUp(final int keycode) {
		return this.cameraManager.keyUp(keycode);
	}

	public void scrolled(final int amount) {
		this.cameraManager.scrolled(amount);
	}

	public boolean touchDown(final int screenX, final int screenY, final float worldScreenY, final int button) {
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
				else {
					final RenderWidget rayPickUnit = this.war3MapViewer.rayPickUnit(screenX, worldScreenY,
							this.activeCommandUnitTargetFilter);
					final boolean shiftDown = isShiftDown();
					if (rayPickUnit != null) {
						this.unitOrderListener.issueTargetOrder(
								this.activeCommandUnit.getSimulationUnit().getHandleId(),
								this.activeCommand.getHandleId(), this.activeCommandOrderId,
								rayPickUnit.getSimulationWidget().getHandleId(), shiftDown);
						final UnitSound yesSound = (this.activeCommand instanceof CAbilityAttack)
								? getSelectedUnit().soundset.yesAttack
								: getSelectedUnit().soundset.yes;
						if (yesSound.playUnitResponse(this.war3MapViewer.worldScene.audioContext, getSelectedUnit())) {
							portraitTalk();
						}
						this.selectedSoundCount = 0;
						if (this.activeCommand instanceof CAbilityRally) {
							this.war3MapViewer.getUiSounds().getSound("RallyPointPlace").play(this.uiScene.audioContext,
									0, 0, 0);
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
						this.war3MapViewer.getClickLocation(clickLocationTemp, screenX, (int) worldScreenY);
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
								portraitTalk();
							}
							this.activeCommandUnit = null;
							this.activeCommand = null;
							this.activeCommandOrderId = -1;
							setDraggingItem(null);
							clearAndRepopulateCommandCard();
						}
						else {
							this.activeCommand.checkCanTarget(this.war3MapViewer.simulation,
									this.activeCommandUnit.getSimulationUnit(), this.activeCommandOrderId,
									clickLocationTemp2, PointAbilityTargetCheckReceiver.INSTANCE);
							final Vector2 target = PointAbilityTargetCheckReceiver.INSTANCE.getTarget();
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
								if (getSelectedUnit().soundset.yes.playUnitResponse(
										this.war3MapViewer.worldScene.audioContext, getSelectedUnit())) {
									portraitTalk();
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
						}

					}
				}
			}
			else {
				if (button == Input.Buttons.RIGHT) {
					if ((getSelectedUnit() != null) && (getSelectedUnit().getSimulationUnit()
							.getPlayerIndex() == this.war3MapViewer.getLocalPlayerIndex())) {
						final RenderWidget rayPickUnit = this.war3MapViewer.rayPickUnit(screenX, worldScreenY);
						if ((rayPickUnit != null) && !rayPickUnit.getSimulationWidget().isDead()) {
							boolean ordered = false;
							boolean rallied = false;
							boolean attacked = false;
							for (final RenderUnit unit : this.selectedUnits) {
								for (final CAbility ability : unit.getSimulationUnit().getAbilities()) {
									ability.checkCanTarget(this.war3MapViewer.simulation, unit.getSimulationUnit(),
											OrderIds.smart, rayPickUnit.getSimulationWidget(),
											CWidgetAbilityTargetCheckReceiver.INSTANCE);
									final CWidget targetWidget = CWidgetAbilityTargetCheckReceiver.INSTANCE.getTarget();
									if (targetWidget != null) {
										this.unitOrderListener.issueTargetOrder(unit.getSimulationUnit().getHandleId(),
												ability.getHandleId(), OrderIds.smart, targetWidget.getHandleId(),
												isShiftDown());
										rallied |= ability instanceof CAbilityRally;
										attacked |= ability instanceof CAbilityAttack;
										ordered = true;
									}
								}

							}
							if (ordered) {
								final UnitSound yesSound = attacked ? getSelectedUnit().soundset.yesAttack
										: getSelectedUnit().soundset.yes;
								if (yesSound.playUnitResponse(this.war3MapViewer.worldScene.audioContext,
										getSelectedUnit())) {
									portraitTalk();
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
					final List<RenderWidget> selectedUnits = this.war3MapViewer.selectUnit(screenX, worldScreenY,
							false);
					if (!selectedUnits.isEmpty()) {
						selectWidgets(selectedUnits);
					}
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

	private void rightClickMove(final int screenX, final float worldScreenY) {
		this.war3MapViewer.getClickLocation(clickLocationTemp, screenX, (int) worldScreenY);
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
				portraitTalk();
			}
			if (rallied) {
				this.war3MapViewer.getUiSounds().getSound("RallyPointPlace").play(this.uiScene.audioContext, 0, 0, 0);
			}
			this.selectedSoundCount = 0;
		}
	}

	private void selectWidgets(final List<RenderWidget> selectedUnits) {
		final List<RenderUnit> units = new ArrayList<>();
		for (final RenderWidget widget : selectedUnits) {
			if (widget instanceof RenderUnit) {
				units.add((RenderUnit) widget);
			}
		}
		selectUnits(units);
	}

	private void selectUnits(final List<RenderUnit> selectedUnits) {
		this.selectedUnits = selectedUnits;
		if (!selectedUnits.isEmpty()) {
			final RenderUnit unit = selectedUnits.get(0);
			final boolean selectionChanged = getSelectedUnit() != unit;
			boolean playedNewSound = false;
			if (selectionChanged) {
				this.selectedSoundCount = 0;
			}
			if (unit.getSimulationUnit().getPlayerIndex() == this.war3MapViewer.getLocalPlayerIndex()) {
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
						playedNewSound = true;
					}
				}
			}
			else {
				this.war3MapViewer.getUiSounds().getSound("InterfaceClick").play(this.uiScene.audioContext, 0, 0, 0);
			}
			if (selectionChanged) {
				selectUnit(unit);
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
		screenCoordsVector.set(screenX, screenY);
		this.uiViewport.unproject(screenCoordsVector);
		final UIFrame clickedUIFrame = this.rootFrame.touchUp(screenCoordsVector.x, screenCoordsVector.y, button);
		if (this.mouseDownUIFrame != null) {
			if (clickedUIFrame == this.mouseDownUIFrame) {
				this.mouseDownUIFrame.onClick(button);
				if (this.mouseDownUIFrame instanceof ClickableActionFrame) {
					this.war3MapViewer.getUiSounds().getSound("InterfaceClick").play(this.uiScene.audioContext, 0, 0,
							0);
				}
				else {
					this.war3MapViewer.getUiSounds().getSound("MenuButtonClick").play(this.uiScene.audioContext, 0, 0,
							0);
				}
			}
			this.mouseDownUIFrame.mouseUp(this.rootFrame, this.uiViewport);
		}
		this.mouseDownUIFrame = null;
		return false;
	}

	private static boolean isShiftDown() {
		return Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT);
	}

	public boolean touchDragged(final int screenX, final int screenY, final float worldScreenY, final int pointer) {
		screenCoordsVector.set(screenX, screenY);
		this.uiViewport.unproject(screenCoordsVector);

		if (this.meleeUIMinimap.containsMouse(screenCoordsVector.x, screenCoordsVector.y)) {
			final Vector2 worldPoint = this.meleeUIMinimap.getWorldPointFromScreen(screenCoordsVector.x,
					screenCoordsVector.y);
			this.cameraManager.target.x = worldPoint.x;
			this.cameraManager.target.y = worldPoint.y;
		}
		return false;
	}

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
		return false;
	}

	private void loadTooltip(final ClickableActionFrame mousedUIFrame) {
		final int goldCost = mousedUIFrame.getToolTipGoldCost();
		final int lumberCost = mousedUIFrame.getToolTipLumberCost();
		final int foodCost = mousedUIFrame.getToolTipFoodCost();
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
		if (simulationUnit.isConstructing()) {
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
				final List<RenderWidget> unitList = Arrays.asList(
						this.war3MapViewer.getRenderPeer(this.selectedUnit.getSimulationUnit().getWorkerInside()));
				this.war3MapViewer.doSelectUnit(unitList);
				selectWidgets(unitList);
				break;
			}
		}
		else {
			this.unitOrderListener.unitCancelTrainingItem(simulationUnit.getHandleId(), index);
		}
	}

	public void dispose() {
		if (this.rootFrame != null) {
			this.rootFrame.dispose();
		}
	}

	private class ItemCommandCardCommandListener implements CommandCardCommandListener {
		@Override
		public void onClick(final int abilityHandleId, final int orderId, final boolean rightClick) {
			if (rightClick) {
				final RenderUnit selectedUnit2 = MeleeUI.this.selectedUnit;
				final CUnit simulationUnit = selectedUnit2.getSimulationUnit();
				final CAbilityInventory inventoryData = simulationUnit.getInventoryData();
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
					}
				}
			}
		}

		@Override
		public void openMenu(final int orderId) {
			MeleeUI.this.openMenu(orderId);
		}

	}
}
