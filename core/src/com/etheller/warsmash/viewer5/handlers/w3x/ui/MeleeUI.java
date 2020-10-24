package com.etheller.warsmash.viewer5.handlers.w3x.ui;

import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.etheller.warsmash.datasources.DataSource;
import com.etheller.warsmash.parsers.fdf.GameUI;
import com.etheller.warsmash.parsers.fdf.datamodel.AnchorDefinition;
import com.etheller.warsmash.parsers.fdf.datamodel.FramePoint;
import com.etheller.warsmash.parsers.fdf.datamodel.TextJustify;
import com.etheller.warsmash.parsers.fdf.frames.SetPoint;
import com.etheller.warsmash.parsers.fdf.frames.SpriteFrame;
import com.etheller.warsmash.parsers.fdf.frames.StringFrame;
import com.etheller.warsmash.parsers.fdf.frames.TextureFrame;
import com.etheller.warsmash.parsers.fdf.frames.UIFrame;
import com.etheller.warsmash.parsers.jass.Jass2.RootFrameListener;
import com.etheller.warsmash.util.FastNumberFormat;
import com.etheller.warsmash.util.ImageUtils;
import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.Scene;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxComplexInstance;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxModel;
import com.etheller.warsmash.viewer5.handlers.mdx.ReplaceableIds;
import com.etheller.warsmash.viewer5.handlers.mdx.SequenceLoopMode;
import com.etheller.warsmash.viewer5.handlers.tga.TgaFile;
import com.etheller.warsmash.viewer5.handlers.w3x.SequenceUtils;
import com.etheller.warsmash.viewer5.handlers.w3x.UnitSound;
import com.etheller.warsmash.viewer5.handlers.w3x.War3MapViewer;
import com.etheller.warsmash.viewer5.handlers.w3x.camera.CameraPreset;
import com.etheller.warsmash.viewer5.handlers.w3x.camera.CameraRates;
import com.etheller.warsmash.viewer5.handlers.w3x.camera.GameCameraManager;
import com.etheller.warsmash.viewer5.handlers.w3x.camera.PortraitCameraManager;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.RenderUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.commandbuttons.CommandButtonListener;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitClassification;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitFilterFunction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitStateListener;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityAttack;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityView;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CAttackType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CDefenseType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CodeKeyType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttack;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayerUnitOrderListener;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.BooleanAbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.BooleanAbilityTargetCheckReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.CWidgetAbilityTargetCheckReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.PointAbilityTargetCheckReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.StringMsgAbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.ui.command.CommandCardCommandListener;

public class MeleeUI implements CUnitStateListener, CommandButtonListener, CommandCardCommandListener {
	private static final int COMMAND_CARD_WIDTH = 4;
	private static final int COMMAND_CARD_HEIGHT = 3;

	private static final Vector2 screenCoordsVector = new Vector2();
	private static final Vector3 clickLocationTemp = new Vector3();
	private static final Vector2 clickLocationTemp2 = new Vector2();
	private final DataSource dataSource;
	private final Viewport uiViewport;
	private final FreeTypeFontGenerator fontGenerator;
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
	private UIFrame simpleInfoPanelUnitDetail;
	private StringFrame simpleNameValue;
	private StringFrame simpleClassValue;
	private StringFrame simpleBuildingActionLabel;
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

	private final CommandCardIcon[][] commandCard = new CommandCardIcon[COMMAND_CARD_HEIGHT][COMMAND_CARD_WIDTH];

	private RenderUnit selectedUnit;

	// TODO remove this & replace with FDF
	private final Texture activeButtonTexture;
	private UIFrame inventoryCover;
	private SpriteFrame cursorFrame;
	private MeleeUIMinimap meleeUIMinimap;
	private final CPlayerUnitOrderListener unitOrderListener;
	private StringFrame errorMessageFrame;

	private CAbilityView activeCommand;
	private int activeCommandOrderId;
	private RenderUnit activeCommandUnit;

	private int selectedSoundCount = 0;
	private final ActiveCommandUnitTargetFilter activeCommandUnitTargetFilter;

	public MeleeUI(final DataSource dataSource, final Viewport uiViewport, final FreeTypeFontGenerator fontGenerator,
			final Scene uiScene, final Scene portraitScene, final CameraPreset[] cameraPresets,
			final CameraRates cameraRates, final War3MapViewer war3MapViewer, final RootFrameListener rootFrameListener,
			final CPlayerUnitOrderListener unitOrderListener) {
		this.dataSource = dataSource;
		this.uiViewport = uiViewport;
		this.fontGenerator = fontGenerator;
		this.uiScene = uiScene;
		this.portraitScene = portraitScene;
		this.war3MapViewer = war3MapViewer;
		this.rootFrameListener = rootFrameListener;
		this.unitOrderListener = unitOrderListener;

		this.cameraManager = new GameCameraManager(cameraPresets, cameraRates);

		this.cameraManager.setupCamera(war3MapViewer.worldScene);
		if (this.war3MapViewer.startLocations[0] != null) {
			this.cameraManager.target.x = this.war3MapViewer.startLocations[0].x;
			this.cameraManager.target.y = this.war3MapViewer.startLocations[0].y;
		}

		this.activeButtonTexture = ImageUtils.getBLPTexture(war3MapViewer.mapMpq,
				"UI\\Widgets\\Console\\Human\\CommandButton\\human-activebutton.blp");
		this.activeCommandUnitTargetFilter = new ActiveCommandUnitTargetFilter();

	}

	private MeleeUIMinimap createMinimap(final War3MapViewer war3MapViewer) {
		final Rectangle minimapDisplayArea = new Rectangle(18.75f, 13.75f, 278.75f, 276.25f);
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
			try {
				minimapTexture = ImageUtils
						.getTexture(ImageIO.read(war3MapViewer.dataSource.getResourceAsStream("war3mapMap.blp")));
			}
			catch (final IOException e) {
				System.err.println("Could not load minimap BLP file");
				e.printStackTrace();
			}
		}
		final Texture[] teamColors = new Texture[WarsmashConstants.MAX_PLAYERS];
		for (int i = 0; i < teamColors.length; i++) {
			teamColors[i] = ImageUtils.getBLPTexture(war3MapViewer.dataSource,
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
		this.rootFrame = new GameUI(this.dataSource, GameUI.loadSkin(this.dataSource, 0), this.uiViewport,
				this.fontGenerator, this.uiScene, this.war3MapViewer);
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
		this.resourceBarGoldText.setText("500");
		this.resourceBarLumberText = (StringFrame) this.rootFrame.getFrameByName("ResourceBarLumberText", 0);
		this.resourceBarLumberText.setText("150");
		this.resourceBarSupplyText = (StringFrame) this.rootFrame.getFrameByName("ResourceBarSupplyText", 0);
		this.resourceBarSupplyText.setText("12/100");
		this.resourceBarUpkeepText = (StringFrame) this.rootFrame.getFrameByName("ResourceBarUpkeepText", 0);
		this.resourceBarUpkeepText.setText("No Upkeep");
		this.resourceBarUpkeepText.setColor(Color.GREEN);

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

		this.simpleInfoPanelUnitDetail = this.rootFrame.createSimpleFrame("SimpleInfoPanelUnitDetail", this.consoleUI,
				0);
		this.simpleInfoPanelUnitDetail
				.addAnchor(new AnchorDefinition(FramePoint.BOTTOM, 0, GameUI.convertY(this.uiViewport, 0.0f)));
		this.simpleInfoPanelUnitDetail.setWidth(GameUI.convertY(this.uiViewport, 0.180f));
		this.simpleInfoPanelUnitDetail.setHeight(GameUI.convertY(this.uiViewport, 0.105f));
		this.simpleNameValue = (StringFrame) this.rootFrame.getFrameByName("SimpleNameValue", 0);
		this.simpleClassValue = (StringFrame) this.rootFrame.getFrameByName("SimpleClassValue", 0);
		this.simpleBuildingActionLabel = (StringFrame) this.rootFrame.getFrameByName("SimpleBuildingActionLabel", 0);

		this.attack1Icon = this.rootFrame.createSimpleFrame("SimpleInfoPanelIconDamage", this.simpleInfoPanelUnitDetail,
				0);
		this.attack1Icon.addSetPoint(new SetPoint(FramePoint.TOPLEFT, this.simpleInfoPanelUnitDetail,
				FramePoint.TOPLEFT, 0, GameUI.convertY(this.uiViewport, -0.030125f)));
		this.attack1IconBackdrop = (TextureFrame) this.rootFrame.getFrameByName("InfoPanelIconBackdrop", 0);
		this.attack1InfoPanelIconValue = (StringFrame) this.rootFrame.getFrameByName("InfoPanelIconValue", 0);
		this.attack1InfoPanelIconLevel = (StringFrame) this.rootFrame.getFrameByName("InfoPanelIconLevel", 0);

		this.attack2Icon = this.rootFrame.createSimpleFrame("SimpleInfoPanelIconDamage", this.simpleInfoPanelUnitDetail,
				1);
		this.attack2Icon
				.addSetPoint(new SetPoint(FramePoint.TOPLEFT, this.simpleInfoPanelUnitDetail, FramePoint.TOPLEFT,
						GameUI.convertX(this.uiViewport, 0.1f), GameUI.convertY(this.uiViewport, -0.030125f)));
		this.attack2IconBackdrop = (TextureFrame) this.rootFrame.getFrameByName("InfoPanelIconBackdrop", 1);
		this.attack2InfoPanelIconValue = (StringFrame) this.rootFrame.getFrameByName("InfoPanelIconValue", 1);
		this.attack2InfoPanelIconLevel = (StringFrame) this.rootFrame.getFrameByName("InfoPanelIconLevel", 1);

		this.armorIcon = this.rootFrame.createSimpleFrame("SimpleInfoPanelIconArmor", this.simpleInfoPanelUnitDetail,
				1);
		this.armorIcon.addSetPoint(new SetPoint(FramePoint.TOPLEFT, this.simpleInfoPanelUnitDetail, FramePoint.TOPLEFT,
				GameUI.convertX(this.uiViewport, 0f), GameUI.convertY(this.uiViewport, -0.06025f)));
		this.armorIconBackdrop = (TextureFrame) this.rootFrame.getFrameByName("InfoPanelIconBackdrop", 0);
		this.armorInfoPanelIconValue = (StringFrame) this.rootFrame.getFrameByName("InfoPanelIconValue", 0);
		this.armorInfoPanelIconLevel = (StringFrame) this.rootFrame.getFrameByName("InfoPanelIconLevel", 0);

		this.inventoryCover = this.rootFrame.createSimpleFrame("SmashConsoleInventoryCover", this.rootFrame, 0);

		this.errorMessageFrame = this.rootFrame.createStringFrame("SmashErrorMessageFrame", this.consoleUI,
				new Color(0xFFFFCC00), TextJustify.LEFT, TextJustify.MIDDLE, 0.014f);
		this.errorMessageFrame.addAnchor(new AnchorDefinition(FramePoint.BOTTOMLEFT,
				GameUI.convertX(this.uiViewport, 0.275f), GameUI.convertY(this.uiViewport, 0.275f)));
		this.errorMessageFrame.setWidth(GameUI.convertX(this.uiViewport, 0.25f));

		int commandButtonIndex = 0;
		for (int j = 0; j < COMMAND_CARD_HEIGHT; j++) {
			for (int i = 0; i < COMMAND_CARD_WIDTH; i++) {
				final CommandCardIcon commandCardIcon = new CommandCardIcon("SmashCommandButton_" + commandButtonIndex,
						this.rootFrame, this);
				this.rootFrame.add(commandCardIcon);
				final TextureFrame iconFrame = this.rootFrame.createTextureFrame(
						"SmashCommandButton_" + (commandButtonIndex) + "_Icon", this.rootFrame, false, null);
				final SpriteFrame cooldownFrame = (SpriteFrame) this.rootFrame.createFrameByType("SPRITE",
						"SmashCommandButton_" + (commandButtonIndex) + "_Cooldown", this.rootFrame, "", 0);
				final SpriteFrame autocastFrame = (SpriteFrame) this.rootFrame.createFrameByType("SPRITE",
						"SmashCommandButton_" + (commandButtonIndex) + "_Autocast", this.rootFrame, "", 0);
				commandCardIcon.addAnchor(new AnchorDefinition(FramePoint.BOTTOMLEFT,
						GameUI.convertX(this.uiViewport, 0.6175f + (0.0434f * i)),
						GameUI.convertY(this.uiViewport, 0.095f - (0.044f * j))));
				commandCardIcon.setWidth(GameUI.convertX(this.uiViewport, 0.039f));
				commandCardIcon.setHeight(GameUI.convertY(this.uiViewport, 0.039f));
				iconFrame.addSetPoint(new SetPoint(FramePoint.CENTER, commandCardIcon, FramePoint.CENTER, 0, 0));
				iconFrame.setWidth(GameUI.convertX(this.uiViewport, 0.039f));
				iconFrame.setHeight(GameUI.convertY(this.uiViewport, 0.039f));
				iconFrame.setTexture(ImageUtils.DEFAULT_ICON_PATH, this.rootFrame);
				cooldownFrame.addSetPoint(new SetPoint(FramePoint.CENTER, commandCardIcon, FramePoint.CENTER, 0, 0));
				this.rootFrame.setSpriteFrameModel(cooldownFrame, this.rootFrame.getSkinField("CommandButtonCooldown"));
				cooldownFrame.setWidth(GameUI.convertX(this.uiViewport, 0.039f));
				cooldownFrame.setHeight(GameUI.convertY(this.uiViewport, 0.039f));
				autocastFrame.addSetPoint(new SetPoint(FramePoint.CENTER, commandCardIcon, FramePoint.CENTER, 0, 0));
				this.rootFrame.setSpriteFrameModel(autocastFrame, this.rootFrame.getSkinField("CommandButtonAutocast"));
				autocastFrame.setWidth(GameUI.convertX(this.uiViewport, 0.039f));
				autocastFrame.setHeight(GameUI.convertY(this.uiViewport, 0.039f));
				commandCardIcon.set(iconFrame, cooldownFrame, autocastFrame);
				this.commandCard[j][i] = commandCardIcon;
				commandCardIcon.setCommandButton(null);
				commandButtonIndex++;
			}
		}

		this.cursorFrame = (SpriteFrame) this.rootFrame.createFrameByType("SPRITE", "SmashCursorFrame", this.rootFrame,
				"", 0);
		this.rootFrame.setSpriteFrameModel(this.cursorFrame, this.rootFrame.getSkinField("Cursor"));
		this.cursorFrame.setSequence("Normal");
		this.cursorFrame.setZDepth(1.0f);
		Gdx.input.setCursorCatched(true);

		this.meleeUIMinimap = createMinimap(this.war3MapViewer);

		this.rootFrame.positionBounds(this.uiViewport);
		selectUnit(null);
	}

	@Override
	public void startUsingAbility(final int abilityHandleId, final int orderId) {
		// TODO not O(N)
		CAbilityView abilityToUse = null;
		for (final CAbility ability : this.selectedUnit.getSimulationUnit().getAbilities()) {
			if (ability.getHandleId() == abilityHandleId) {
				abilityToUse = ability;
				break;
			}
		}
		if (abilityToUse != null) {
			final StringMsgAbilityActivationReceiver stringMsgActivationReceiver = StringMsgAbilityActivationReceiver
					.getInstance().reset();
			abilityToUse.checkCanUse(this.war3MapViewer.simulation, this.selectedUnit.getSimulationUnit(), orderId,
					stringMsgActivationReceiver);
			if (!stringMsgActivationReceiver.isUseOk()) {
				showCommandError(stringMsgActivationReceiver.getMessage());
			}
			else {
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
				}
			}
		}
	}

	public void showCommandError(final String message) {
		this.errorMessageFrame.setText(message);
	}

	@Override
	public void toggleAutoCastAbility(final int abilityHandleId) {

	}

	public void update(final float deltaTime) {
		this.portrait.update();

		int mouseX = Gdx.input.getX();
		int mouseY = Gdx.input.getY();
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
		Gdx.input.setCursorPosition(mouseX, mouseY);

		screenCoordsVector.set(mouseX, mouseY);
		this.uiViewport.unproject(screenCoordsVector);
		this.cursorFrame.setFramePointX(FramePoint.LEFT, screenCoordsVector.x);
		this.cursorFrame.setFramePointY(FramePoint.BOTTOM, screenCoordsVector.y);

		if (this.activeCommand != null) {
			this.cursorFrame.setSequence("Target");
		}
		else if (down) {
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
		final float groundHeight = Math.max(
				this.war3MapViewer.terrain.getGroundHeight(this.cameraManager.target.x, this.cameraManager.target.y),
				this.war3MapViewer.terrain.getWaterHeight(this.cameraManager.target.x, this.cameraManager.target.y));
		this.cameraManager.updateTargetZ(groundHeight);
		this.cameraManager.updateCamera();
	}

	public void render(final SpriteBatch batch, final BitmapFont font20, final GlyphLayout glyphLayout) {
		this.rootFrame.render(batch, font20, glyphLayout);
		if (this.selectedUnit != null) {
			font20.setColor(Color.WHITE);

		}

		this.meleeUIMinimap.render(batch, this.war3MapViewer.units);
		this.timeIndicator.setFrameByRatio(this.war3MapViewer.simulation.getGameTimeOfDay()
				/ this.war3MapViewer.simulation.getGameplayConstants().getGameDayHours());
	}

	public void portraitTalk() {
		this.portrait.talk();
	}

	private final class ActiveCommandUnitTargetFilter implements CUnitFilterFunction {
		@Override
		public boolean call(final CUnit unit) {
			final BooleanAbilityTargetCheckReceiver<CWidget> targetReceiver = BooleanAbilityTargetCheckReceiver
					.<CWidget>getInstance();
			MeleeUI.this.activeCommand.checkCanTarget(MeleeUI.this.war3MapViewer.simulation, unit,
					MeleeUI.this.activeCommandOrderId, unit, targetReceiver);
			return targetReceiver.isTargetable();
		}
	}

	private static final class Portrait {
		private MdxComplexInstance modelInstance;
		private final PortraitCameraManager portraitCameraManager;
		private final Scene portraitScene;

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
				SequenceUtils.randomPortraitSequence(this.modelInstance);
			}
		}

		public void talk() {
			SequenceUtils.randomPortraitTalkSequence(this.modelInstance);
		}

		public void setSelectedUnit(final RenderUnit unit) {
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
					this.modelInstance.setSequenceLoopMode(SequenceLoopMode.MODEL_LOOP);
					this.modelInstance.setScene(this.portraitScene);
					this.modelInstance.setVertexColor(unit.instance.vertexColor);
					this.modelInstance.setTeamColor(unit.playerIndex);
				}
			}
		}
	}

	public void selectUnit(RenderUnit unit) {
		if ((unit != null) && unit.getSimulationUnit().isDead()) {
			unit = null;
		}
		if (this.selectedUnit != null) {
			this.selectedUnit.getSimulationUnit().removeStateListener(this);
		}
		this.portrait.setSelectedUnit(unit);
		this.selectedUnit = unit;
		for (int j = 0; j < COMMAND_CARD_HEIGHT; j++) {
			for (int i = 0; i < COMMAND_CARD_WIDTH; i++) {
				this.commandCard[j][i].setCommandButton(null);
			}
		}
		if (unit == null) {
			this.simpleNameValue.setText("");
			this.unitLifeText.setText("");
			this.unitManaText.setText("");
			this.simpleClassValue.setText("");
			this.simpleBuildingActionLabel.setText("");
			this.attack1Icon.setVisible(false);
			this.attack2Icon.setVisible(false);
			this.attack1InfoPanelIconLevel.setText("");
			this.attack2InfoPanelIconLevel.setText("");
			this.armorIcon.setVisible(false);
			this.armorInfoPanelIconLevel.setText("");
		}
		else {
			unit.getSimulationUnit().addStateListener(this);
			this.simpleNameValue.setText(unit.getSimulationUnit().getUnitType().getName());
			String classText = null;
			for (final CUnitClassification classification : unit.getSimulationUnit().getClassifications()) {
				if ((classification == CUnitClassification.MECHANICAL)
						&& unit.getSimulationUnit().getUnitType().isBuilding()) {
					// buildings dont display MECHANICAL
					continue;
				}
				if (classification.getDisplayName() != null) {
					classText = classification.getDisplayName();
				}
			}
			if (classText != null) {
				this.simpleClassValue.setText(classText);
			}
			else {
				this.simpleClassValue.setText("");
			}
			this.unitLifeText.setText(FastNumberFormat.formatWholeNumber(unit.getSimulationUnit().getLife()) + " / "
					+ unit.getSimulationUnit().getMaximumLife());
			final int maximumMana = unit.getSimulationUnit().getMaximumMana();
			if (maximumMana > 0) {
				this.unitManaText.setText(
						FastNumberFormat.formatWholeNumber(unit.getSimulationUnit().getMana()) + " / " + maximumMana);
			}
			else {
				this.unitManaText.setText("");
			}
			this.simpleBuildingActionLabel.setText("");

			final boolean anyAttacks = unit.getSimulationUnit().getUnitType().getAttacks().size() > 0;
			final UIFrame localArmorIcon = this.armorIcon;
			final TextureFrame localArmorIconBackdrop = this.armorIconBackdrop;
			final StringFrame localArmorInfoPanelIconValue = this.armorInfoPanelIconValue;
			if (anyAttacks) {
				final CUnitAttack attackOne = unit.getSimulationUnit().getUnitType().getAttacks().get(0);
				this.attack1Icon.setVisible(attackOne.isShowUI());
				this.attack1IconBackdrop.setTexture(this.damageBackdrops.getTexture(attackOne.getAttackType()));
				this.attack1InfoPanelIconValue.setText(attackOne.getMinDamage() + " - " + attackOne.getMaxDamage());
				if (unit.getSimulationUnit().getUnitType().getAttacks().size() > 1) {
					final CUnitAttack attackTwo = unit.getSimulationUnit().getUnitType().getAttacks().get(1);
					this.attack2Icon.setVisible(attackTwo.isShowUI());
					this.attack2IconBackdrop.setTexture(this.damageBackdrops.getTexture(attackTwo.getAttackType()));
					this.attack2InfoPanelIconValue.setText(attackTwo.getMinDamage() + " - " + attackTwo.getMaxDamage());
				}
				else {
					this.attack2Icon.setVisible(false);
				}

				this.armorIcon.addSetPoint(
						new SetPoint(FramePoint.TOPLEFT, this.simpleInfoPanelUnitDetail, FramePoint.TOPLEFT,
								GameUI.convertX(this.uiViewport, 0f), GameUI.convertY(this.uiViewport, -0.06025f)));
				this.armorIcon.positionBounds(this.uiViewport);
			}
			else {
				this.attack1Icon.setVisible(false);
				this.attack2Icon.setVisible(false);

				this.armorIcon.addSetPoint(new SetPoint(FramePoint.TOPLEFT, this.simpleInfoPanelUnitDetail,
						FramePoint.TOPLEFT, 0, GameUI.convertY(this.uiViewport, -0.030125f)));
				this.armorIcon.positionBounds(this.uiViewport);
			}

			localArmorIcon.setVisible(true);
			final Texture defenseTexture = this.defenseBackdrops
					.getTexture(unit.getSimulationUnit().getUnitType().getDefenseType());
			if (defenseTexture == null) {
				throw new RuntimeException(
						unit.getSimulationUnit().getUnitType().getDefenseType() + " can't find texture!");
			}
			localArmorIconBackdrop.setTexture(defenseTexture);
			localArmorInfoPanelIconValue.setText(Integer.toString(unit.getSimulationUnit().getDefense()));
			unit.populateCommandCard(this, this.war3MapViewer.getAbilityDataUI());
		}
	}

	@Override
	public void commandButton(final int buttonPositionX, final int buttonPositionY, final Texture icon,
			final int abilityHandleId, final int orderId) {
		final int x = Math.max(0, Math.min(COMMAND_CARD_WIDTH - 1, buttonPositionX));
		final int y = Math.max(0, Math.min(COMMAND_CARD_HEIGHT - 1, buttonPositionY));
		this.commandCard[y][x].setCommandButtonData(icon, abilityHandleId, orderId);
	}

	public void resize(final Rectangle viewport) {
		this.cameraManager.resize(viewport);
		positionPortrait();
	}

	public void positionPortrait() {
		this.projectionTemp1.x = 422;
		this.projectionTemp1.y = 57;
		this.projectionTemp2.x = 422 + 167;
		this.projectionTemp2.y = 57 + 170;
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
			this.unitLifeText
					.setText(FastNumberFormat.formatWholeNumber(this.selectedUnit.getSimulationUnit().getLife()) + " / "
							+ this.selectedUnit.getSimulationUnit().getMaximumLife());
		}
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
				}
				else {
					final RenderUnit rayPickUnit = this.war3MapViewer.rayPickUnit(screenX, worldScreenY,
							this.activeCommandUnitTargetFilter);
					final boolean shiftDown = isShiftDown();
					if (rayPickUnit != null) {
						if (this.unitOrderListener.issueTargetOrder(
								this.activeCommandUnit.getSimulationUnit().getHandleId(),
								this.activeCommand.getHandleId(), this.activeCommandOrderId,
								rayPickUnit.getSimulationUnit().getHandleId(), shiftDown)) {
							if (getSelectedUnit().soundset.yesAttack
									.playUnitResponse(this.war3MapViewer.worldScene.audioContext, getSelectedUnit())) {
								portraitTalk();
							}
							this.selectedSoundCount = 0;
							if (!shiftDown) {
								this.activeCommandUnit = null;
								this.activeCommand = null;
								this.activeCommandOrderId = -1;
							}
						}
					}
					else {
						this.war3MapViewer.getClickLocation(clickLocationTemp, screenX, (int) worldScreenY);
						this.activeCommand.checkCanTarget(this.war3MapViewer.simulation,
								this.activeCommandUnit.getSimulationUnit(), this.activeCommandOrderId,
								clickLocationTemp2, PointAbilityTargetCheckReceiver.INSTANCE);
						final Vector2 target = PointAbilityTargetCheckReceiver.INSTANCE.getTarget();
						if (target != null) {
							if (this.activeCommand instanceof CAbilityAttack) {
								this.war3MapViewer.showConfirmation(clickLocationTemp, 1, 0, 0);
							}
							else {
								this.war3MapViewer.showConfirmation(clickLocationTemp, 0, 1, 0);
							}
							if (this.unitOrderListener.issuePointOrder(
									this.activeCommandUnit.getSimulationUnit().getHandleId(),
									this.activeCommand.getHandleId(), this.activeCommandOrderId, clickLocationTemp2.x,
									clickLocationTemp2.y, shiftDown)) {
								if (getSelectedUnit().soundset.yes.playUnitResponse(
										this.war3MapViewer.worldScene.audioContext, getSelectedUnit())) {
									portraitTalk();
								}
								this.selectedSoundCount = 0;
								if (!shiftDown) {
									this.activeCommandUnit = null;
									this.activeCommand = null;
									this.activeCommandOrderId = -1;
								}
							}
						}
					}
				}
			}
			else {
				if (button == Input.Buttons.RIGHT) {
					final RenderUnit rayPickUnit = this.war3MapViewer.rayPickUnit(screenX, worldScreenY);
					if (getSelectedUnit() != null) {
						if ((rayPickUnit != null) && (rayPickUnit.playerIndex != getSelectedUnit().playerIndex)
								&& !rayPickUnit.getSimulationUnit().isDead()) {
							boolean ordered = false;
							for (final RenderUnit unit : this.war3MapViewer.selected) {
								for (final CAbility ability : unit.getSimulationUnit().getAbilities()) {
									ability.checkCanTarget(this.war3MapViewer.simulation, unit.getSimulationUnit(),
											OrderIds.smart, rayPickUnit.getSimulationUnit(),
											CWidgetAbilityTargetCheckReceiver.INSTANCE);
									final CWidget targetWidget = CWidgetAbilityTargetCheckReceiver.INSTANCE.getTarget();
									if (targetWidget != null) {
										this.unitOrderListener.issueTargetOrder(unit.getSimulationUnit().getHandleId(),
												ability.getHandleId(), OrderIds.smart, targetWidget.getHandleId(),
												isShiftDown());
										ordered = true;
									}
								}

							}
							if (ordered) {
								if (getSelectedUnit().soundset.yesAttack.playUnitResponse(
										this.war3MapViewer.worldScene.audioContext, getSelectedUnit())) {
									portraitTalk();
								}
								this.selectedSoundCount = 0;
							}
						}
						else {
							this.war3MapViewer.getClickLocation(clickLocationTemp, screenX, (int) worldScreenY);
							this.war3MapViewer.showConfirmation(clickLocationTemp, 0, 1, 0);
							clickLocationTemp2.set(clickLocationTemp.x, clickLocationTemp.y);

							boolean ordered = false;
							for (final RenderUnit unit : this.war3MapViewer.selected) {
								for (final CAbility ability : unit.getSimulationUnit().getAbilities()) {
									ability.checkCanUse(this.war3MapViewer.simulation, unit.getSimulationUnit(),
											OrderIds.smart, BooleanAbilityActivationReceiver.INSTANCE);
									if (BooleanAbilityActivationReceiver.INSTANCE.isOk()) {
										ability.checkCanTarget(this.war3MapViewer.simulation, unit.getSimulationUnit(),
												OrderIds.smart, clickLocationTemp2,
												PointAbilityTargetCheckReceiver.INSTANCE);
										final Vector2 target = PointAbilityTargetCheckReceiver.INSTANCE.getTarget();
										if (target != null) {
											this.unitOrderListener.issuePointOrder(
													unit.getSimulationUnit().getHandleId(), ability.getHandleId(),
													OrderIds.smart, clickLocationTemp2.x, clickLocationTemp2.y,
													isShiftDown());
											ordered = true;
										}
									}
								}

							}

							if (ordered) {
								if (getSelectedUnit().soundset.yes.playUnitResponse(
										this.war3MapViewer.worldScene.audioContext, getSelectedUnit())) {
									portraitTalk();
								}
								this.selectedSoundCount = 0;
							}
						}
					}
				}
				else {
					final List<RenderUnit> selectedUnits = this.war3MapViewer.selectUnit(screenX, worldScreenY, false);
					if (!selectedUnits.isEmpty()) {
						final RenderUnit unit = selectedUnits.get(0);
						final boolean selectionChanged = getSelectedUnit() != unit;
						boolean playedNewSound = false;
						if (selectionChanged) {
							this.selectedSoundCount = 0;
						}
						if (unit.soundset != null) {
							UnitSound ackSoundToPlay = unit.soundset.what;
							final int pissedSoundCount = unit.soundset.pissed.getSoundCount();
							int soundIndex;
							if ((this.selectedSoundCount >= 3) && (pissedSoundCount > 0)) {
								soundIndex = this.selectedSoundCount - 3;
								ackSoundToPlay = unit.soundset.pissed;
							}
							else {
								soundIndex = (int) (Math.random() * ackSoundToPlay.getSoundCount());
							}
							if (ackSoundToPlay.playUnitResponse(this.war3MapViewer.worldScene.audioContext, unit,
									soundIndex)) {
								this.selectedSoundCount++;
								if ((this.selectedSoundCount - 3) >= pissedSoundCount) {
									this.selectedSoundCount = 0;
								}
								playedNewSound = true;
							}
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
			}
		}
		return false;
	}

	private static boolean isShiftDown() {
		return Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT);
	}
}
