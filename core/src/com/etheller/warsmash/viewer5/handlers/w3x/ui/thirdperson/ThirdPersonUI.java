package com.etheller.warsmash.viewer5.handlers.w3x.ui.thirdperson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.etheller.warsmash.parsers.dbc.DbcParser;
import com.etheller.warsmash.parsers.dbc.decoders.DbcDecoderSoundEntries;
import com.etheller.warsmash.parsers.fdf.GameUI;
import com.etheller.warsmash.parsers.fdf.datamodel.FramePoint;
import com.etheller.warsmash.parsers.fdf.frames.SimpleFrame;
import com.etheller.warsmash.parsers.fdf.frames.SimpleStatusBarFrame;
import com.etheller.warsmash.parsers.fdf.frames.SpriteFrame;
import com.etheller.warsmash.parsers.fdf.frames.StringFrame;
import com.etheller.warsmash.parsers.fdf.frames.UIFrame;
import com.etheller.warsmash.units.DataTable;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.util.WorldEditStrings;
import com.etheller.warsmash.viewer5.Scene;
import com.etheller.warsmash.viewer5.handlers.w3x.War3MapViewer;
import com.etheller.warsmash.viewer5.handlers.w3x.camera.ThirdPersonCameraManager;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.RenderUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.RenderWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidgetFilterFunction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.thirdperson.CAbilityPlayerPawn;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.thirdperson.CBehaviorPlayerPawn;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CAllianceType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayerUnitOrderListener;
import com.etheller.warsmash.viewer5.handlers.w3x.ui.WarsmashToggleableUI;
import com.etheller.warsmash.viewer5.handlers.w3x.ui.command.ClickableActionFrame;
import com.etheller.warsmash.viewer5.handlers.w3x.ui.command.ClickableFrame;
import com.etheller.warsmash.viewer5.handlers.w3x.ui.sound.KeyedSounds;
import com.hiveworkshop.rms.util.BinaryReader;

public class ThirdPersonUI implements WarsmashToggleableUI {
	private static final boolean ALL_PLAYERS = false;
	private static final Vector2 screenCoordsVector = new Vector2();
	private ThirdPersonCameraManager cameraManager;
	private final War3MapViewer war3MapViewer;
	private final Scene uiScene;
	private final Viewport uiViewport;
	private final Scene portraitScene;
	private final Rectangle tempRect = new Rectangle();
	private int lastX;
	private int lastY;
	private int touchDownX;
	private int touchDownY;
	private int button;
	private boolean showing = false;
	private GameUI rootFrame;
	private SpriteFrame cursorFrame;
	private boolean touchDown;
//	private final ModelInstance skyModelInstance;
	private SimpleFrame mainMenuBar;
	private SimpleFrame worldFrame;
	private SimpleFrame uiParent;
	private SimpleStatusBarFrame mainMenuExpBar;
	private StringFrame mainMenuExpBarText;
	private UIFrame castingBarFrame;
	private ClickableFrame mouseDownUIFrame;
	private ClickableFrame mouseOverUIFrame;
	private UIFrame tooltipFrame;
	private StringFrame tooltipFrame1;
	private CUnit pawnUnit;
	private CAbilityPlayerPawn abilityPlayerPawn;
	private final CPlayerUnitOrderListener uiOrderListener;
	private final War3ID pawnId;
	private RenderWidget mouseOverUnit;
	private RenderWidget targetUnit;
	private final boolean userControlEnabled = true;
	private final AnyClickableUnitFilter anyClickableUnitFilter;
	private final AnyTargetableUnitFilter anyTargetableUnitFilter;
	private KeyedSounds uiSounds;

	public ThirdPersonUI(final War3MapViewer war3MapViewer, final Scene uiScene, final Viewport uiViewport,
			final Scene portraitScene, final CPlayerUnitOrderListener uiOrderListener, final War3ID pawnId) {
		this.war3MapViewer = war3MapViewer;
		this.uiScene = uiScene;
		this.uiViewport = uiViewport;
		this.portraitScene = portraitScene;
		this.uiOrderListener = uiOrderListener;
		this.pawnId = pawnId;
		this.anyClickableUnitFilter = new AnyClickableUnitFilter();
		this.anyTargetableUnitFilter = new AnyTargetableUnitFilter();

//		final MdxModel skyModel = war3MapViewer
//				.loadModelMdx("environment\\sky\\lordaeronsummersky\\lordaeronsummersky.mdx");
//		this.skyModelInstance = skyModel.addInstance();
////		this.skyModelInstance.setParent(pawnComplexInstance.getAttachment(0));
//		this.skyModelInstance.setScene(war3MapViewer.worldScene);
//		this.skyModelInstance.uniformScale(10);
//		this.skyModelInstance.setLocation(0, 0, 0);
//		((MdxComplexInstance) this.skyModelInstance).setSequence(0);

	}

	@Override
	public void main() {
		final List<CUnit> pawnUnits = new ArrayList<>();
		if (ALL_PLAYERS) {
			for (int i = 0; i < WarsmashConstants.MAX_PLAYERS; i++) {
				final float[] startLocation = this.war3MapViewer.simulation.getPlayer(i).getStartLocation();
				pawnUnits.add(this.war3MapViewer.simulation.createUnitSimple(this.pawnId, i, startLocation[0],
						startLocation[1], 0));
			}

			this.pawnUnit = pawnUnits.get(this.war3MapViewer.getLocalPlayerIndex());
		}
		else {
			final float[] startLocation = this.war3MapViewer.simulation
					.getPlayer(this.war3MapViewer.getLocalPlayerIndex()).getStartLocation();
			pawnUnits.add(this.war3MapViewer.simulation.createUnitSimple(this.pawnId,
					this.war3MapViewer.getLocalPlayerIndex(), startLocation[0], startLocation[1], 0));
//			this.war3MapViewer.simulation.createUnitSimple(War3ID.fromString("hwtw"),
//			this.war3MapViewer.getLocalPlayerIndex(), startLocation[0], startLocation[1], 0);
//			this.war3MapViewer.simulation.createUnitSimple(War3ID.fromString("hpea"),
//					this.war3MapViewer.getLocalPlayerIndex(), startLocation[0], startLocation[1], 0);
//			this.war3MapViewer.simulation.createUnitSimple(War3ID.fromString("hpea"),
//					this.war3MapViewer.getLocalPlayerIndex(), startLocation[0], startLocation[1], 0);
//			this.war3MapViewer.simulation.createUnitSimple(War3ID.fromString("hpea"),
//			this.war3MapViewer.getLocalPlayerIndex(), startLocation[0], startLocation[1], 0);
			this.war3MapViewer.simulation.createUnitSimple(War3ID.fromString("nqb1"), 1, startLocation[0] + 2000,
					startLocation[1], 0);
			this.war3MapViewer.simulation.createUnitSimple(War3ID.fromString("nqb1"), 1, startLocation[0] + 4000,
					startLocation[1], 0);
			this.war3MapViewer.simulation.createUnitSimple(War3ID.fromString("nqb1"), 1, startLocation[0] + 6000,
					startLocation[1], 0);
			this.war3MapViewer.simulation.createUnitSimple(War3ID.fromString("nqb1"), 1, startLocation[0] + 8000,
					startLocation[1], 0);
			this.war3MapViewer.simulation.createUnitSimple(War3ID.fromString("hpea"), 0, startLocation[0],
					startLocation[1], 0);
			this.war3MapViewer.simulation.getPlayer(0).addGold(9999);
			this.war3MapViewer.simulation.getPlayer(0).addLumber(9999);
			if (false) {
				this.war3MapViewer.simulation.createUnitSimple(War3ID.fromString("Hart"),
						this.war3MapViewer.getLocalPlayerIndex(), startLocation[0], startLocation[1], 0);
				this.war3MapViewer.simulation.createUnitSimple(War3ID.fromString("H0sh"),
						this.war3MapViewer.getLocalPlayerIndex(), startLocation[0], startLocation[1], 0);
				this.war3MapViewer.simulation.createUnitSimple(War3ID.fromString("ocat"),
						this.war3MapViewer.getLocalPlayerIndex(), startLocation[0], startLocation[1], 0);
				this.war3MapViewer.simulation.createUnitSimple(War3ID.fromString("ocat"),
						this.war3MapViewer.getLocalPlayerIndex(), startLocation[0], startLocation[1], 0);
				for (int i = 0; i < 15; i++) {
					this.war3MapViewer.simulation.createUnitSimple(War3ID.fromString("ocat"),
							this.war3MapViewer.getLocalPlayerIndex(), startLocation[0], startLocation[1], 0);
				}
//			this.war3MapViewer.simulation.getPlayer(0).addGold(99999);
//			this.war3MapViewer.simulation.getPlayer(0).addLumber(99999);
			}

			// for (int i = 0; i < 6; i++) {
//				for (int k = 0; k < 7; k++) {
//					this.war3MapViewer.simulation.createItem(War3ID.fromString("tkno"), i * 32, k * 32);
//				}
//			}
//			for (int i = 0; i < 6; i++) {
//				for (int k = 0; k < 7; k++) {
//					this.war3MapViewer.simulation.createItem(War3ID.fromString("gold"), i * 32, k * 32);
//					this.war3MapViewer.simulation.createItem(War3ID.fromString("lmbr"), i * 32, k * 32);
//				}
//			}

			this.pawnUnit = pawnUnits.get(0);
		}
		this.abilityPlayerPawn = this.pawnUnit.getFirstAbilityOfType(CAbilityPlayerPawn.class);

		final RenderUnit pawnRenderUnit = this.war3MapViewer.getRenderPeer(this.pawnUnit);

		this.cameraManager = new ThirdPersonCameraManager(pawnRenderUnit, this.abilityPlayerPawn, this.war3MapViewer);
		this.cameraManager.setupCamera(this.war3MapViewer.worldScene);

		final CPlayer localPlayer = this.war3MapViewer.simulation.getPlayer(this.war3MapViewer.getLocalPlayerIndex());

		final WorldEditStrings worldEditStrings = new WorldEditStrings(this.war3MapViewer.mapMpq);
		final DataTable uiSoundsTable = new DataTable(worldEditStrings);
		try {
			DbcParser.parse(new BinaryReader(this.war3MapViewer.mapMpq.read("DBFilesClient\\SoundEntries.dbc")),
					new DbcDecoderSoundEntries(), uiSoundsTable);
		}
		catch (final IOException e1) {
			e1.printStackTrace();
		}

		this.uiSounds = new KeyedSounds(uiSoundsTable, this.war3MapViewer.mapMpq);
		this.rootFrame = new GameUI(this.war3MapViewer.mapMpq, GameUI.loadSkin(this.war3MapViewer.mapMpq, 0),
				this.uiViewport, this.uiScene, this.war3MapViewer, 0, this.war3MapViewer.getAllObjectData().getWts(),
				this.uiSounds);

		try {
			this.rootFrame.bindPawnUnit(this.pawnUnit, this.abilityPlayerPawn, this.war3MapViewer.getAbilityDataUI(),
					this.uiOrderListener);
			this.rootFrame.loadTOCFile("Interface\\FrameXML\\FrameXML.toc");
		}
		catch (final IOException e) {
			throw new IllegalStateException(e);
		}

		final UIFrame mainMenuBarFixed = this.rootFrame.getFrameByName("MainMenuBar", 0);
		mainMenuBarFixed.setVisible(true);

		this.tooltipFrame = this.rootFrame.createFrame("GameTooltip", this.rootFrame, 0, 0);
//		this.uiParent.add(this.tooltipFrame);
		this.tooltipFrame1 = (StringFrame) this.rootFrame.getFrameByName("$parentTextLeft1", 0);

		this.cursorFrame = (SpriteFrame) this.rootFrame.createFrameByType("SPRITE", "SmashTPCursorFrame",
				this.rootFrame, "", 0);
		this.rootFrame.setSpriteFrameModel(this.cursorFrame, "Interface\\Cursor\\Cursor.mdx");
		this.cursorFrame.setSequence("Point");
		this.cursorFrame.setZDepth(1.0f);
		this.cursorFrame.setVisible(false);

		this.rootFrame.positionBounds(this.rootFrame, this.uiViewport);
		this.rootFrame.positionBounds(this.rootFrame, this.uiViewport);
	}

	@Override
	public void update(final float deltaTime) {
		if (this.showing) {
			this.cameraManager.updateCamera();
		}

		final int baseMouseX = Gdx.input.getX();
		final int mouseX = baseMouseX;
		final int baseMouseY = Gdx.input.getY();
		final int mouseY = baseMouseY;

		screenCoordsVector.set(mouseX, mouseY);
		this.uiViewport.unproject(screenCoordsVector);
		this.cursorFrame.setFramePointX(FramePoint.LEFT, screenCoordsVector.x);
		this.cursorFrame.setFramePointY(FramePoint.BOTTOM, screenCoordsVector.y);

		if (this.showing) {
			this.cursorFrame.setVisible(!this.touchDown);
		}
	}

	@Override
	public void render(final SpriteBatch batch, final GlyphLayout glyphLayout) {
		this.rootFrame.render(batch, this.rootFrame.getFont20(), glyphLayout);
		final float worldWidth = ((ExtendViewport) this.uiViewport).getMinWorldWidth();
		final float worldHeight = ((ExtendViewport) this.uiViewport).getMinWorldHeight();
	}

	@Override
	public void dispose() {

	}

	@Override
	public void resize(final int width, final int height) {
		this.cameraManager.resize(setupWorldFrameViewport(width, height));
	}

	private Rectangle setupWorldFrameViewport(final int width, final int height) {
		this.tempRect.x = 0;
		this.tempRect.width = width;
		final float topHeight = 0;
		final float bottomHeight = 0;
		this.tempRect.y = (int) bottomHeight;
		this.tempRect.height = height - (int) (topHeight + bottomHeight);
		return this.tempRect;
	}

	@Override
	public boolean keyDown(final int keycode) {
		if (keycode == Input.Keys.SPACE) {
			this.uiOrderListener.issueImmediateOrder(this.pawnUnit.getHandleId(), this.abilityPlayerPawn.getHandleId(),
					OrderIds.pawnJumpPressed, false);
		}
		if (keycode == Input.Keys.Z) {
			CBehaviorPlayerPawn.HACKON = !CBehaviorPlayerPawn.HACKON;
		}
		if ((keycode == Input.Keys.LEFT) || (keycode == Input.Keys.A)) {
			this.uiOrderListener.issueImmediateOrder(this.pawnUnit.getHandleId(), this.abilityPlayerPawn.getHandleId(),
					OrderIds.pawnLeftPressed, false);
			return true;
		}
		else if ((keycode == Input.Keys.RIGHT) || (keycode == Input.Keys.D)) {
			this.uiOrderListener.issueImmediateOrder(this.pawnUnit.getHandleId(), this.abilityPlayerPawn.getHandleId(),
					OrderIds.pawnRightPressed, false);
			return true;
		}
		else if ((keycode == Input.Keys.DOWN) || (keycode == Input.Keys.S)) {
			this.uiOrderListener.issueImmediateOrder(this.pawnUnit.getHandleId(), this.abilityPlayerPawn.getHandleId(),
					OrderIds.pawnDownPressed, false);
			return true;
		}
		else if ((keycode == Input.Keys.UP) || (keycode == Input.Keys.W)) {
			this.uiOrderListener.issueImmediateOrder(this.pawnUnit.getHandleId(), this.abilityPlayerPawn.getHandleId(),
					OrderIds.pawnUpPressed, false);
			return true;
		}
		return false;
	}

	@Override
	public boolean keyUp(final int keycode) {
		if ((keycode == Input.Keys.LEFT) || (keycode == Input.Keys.A)) {
			this.uiOrderListener.issueImmediateOrder(this.pawnUnit.getHandleId(), this.abilityPlayerPawn.getHandleId(),
					OrderIds.pawnLeftReleased, false);
			return true;
		}
		else if ((keycode == Input.Keys.RIGHT) || (keycode == Input.Keys.D)) {
			this.uiOrderListener.issueImmediateOrder(this.pawnUnit.getHandleId(), this.abilityPlayerPawn.getHandleId(),
					OrderIds.pawnRightReleased, false);
			return true;
		}
		else if ((keycode == Input.Keys.DOWN) || (keycode == Input.Keys.S)) {
			this.uiOrderListener.issueImmediateOrder(this.pawnUnit.getHandleId(), this.abilityPlayerPawn.getHandleId(),
					OrderIds.pawnDownReleased, false);
			return true;
		}
		else if ((keycode == Input.Keys.UP) || (keycode == Input.Keys.W)) {
			this.uiOrderListener.issueImmediateOrder(this.pawnUnit.getHandleId(), this.abilityPlayerPawn.getHandleId(),
					OrderIds.pawnUpReleased, false);
			return true;
		}
		else {
			this.rootFrame.getLuaGlobals().keyUp(keycode);
		}
		return false;
	}

	@Override
	public boolean touchDown(final int screenX, final int screenY, final float worldScreenY, final int button) {
		this.lastX = this.touchDownX = screenX;
		this.lastY = this.touchDownY = screenY;
		this.button = button;

		screenCoordsVector.set(screenX, screenY);
		this.uiViewport.unproject(screenCoordsVector);
		final UIFrame clickedUIFrame = this.rootFrame.touchDown(screenCoordsVector.x, screenCoordsVector.y, button);
		if (clickedUIFrame == null) {
			this.touchDown = true;
			if (this.mouseOverUnit == null) {
				this.cameraManager.setTouchDown(true);
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
			if (newMouseOverUnit != null) {
				this.war3MapViewer.showUnitMouseOverHighlightThirdPerson(newMouseOverUnit);
			}
			this.mouseOverUnit = newMouseOverUnit;
			this.rootFrame.getLuaGlobals().notifyUpdateMouseOver(this.mouseOverUnit);
		}
	}

	@Override
	public boolean touchUp(final int screenX, final int screenY, final float worldScreenY, final int button) {
		screenCoordsVector.set(screenX, screenY);
		this.uiViewport.unproject(screenCoordsVector);
		final UIFrame clickedUIFrame = this.rootFrame.touchUp(screenCoordsVector.x, screenCoordsVector.y, button);
		if (this.mouseDownUIFrame != null) {
			if (clickedUIFrame == this.mouseDownUIFrame) {
				this.mouseDownUIFrame.onClick(button);
				final String soundKey = this.mouseDownUIFrame.getSoundKey();
				if (soundKey != null) {
//					this.war3MapViewer.getUiSounds().getSound(soundKey).play(this.uiScene.audioContext, 0, 0, 0);
				}
			}
			this.mouseDownUIFrame.mouseUp(this.rootFrame, this.uiViewport);
		}
		else {
			this.touchDown = false;
			if (button == Input.Buttons.LEFT) {
				updateMouseOverUnit(screenX, worldScreenY);
			}
			if (this.cameraManager.isTouchDown()) {
				this.cameraManager.setTouchDown(false);
			}
			else {
				if ((this.mouseOverUnit != null) && isUnitSelectable(this.mouseOverUnit)) {
					this.war3MapViewer.doSelectUnitThirdPerson(Arrays.asList(this.mouseOverUnit));
					final CUnit mousedUnit = this.mouseOverUnit.getSimulationWidget().visit(AbilityTargetVisitor.UNIT);
					if (mousedUnit != null) {
						final CPlayer localPlayer = this.war3MapViewer.simulation
								.getPlayer(this.war3MapViewer.getLocalPlayerIndex());
						final CPlayer mousedUnitPlayer = this.war3MapViewer.simulation
								.getPlayer(mousedUnit.getPlayerIndex());
						String soundKey = "GAMETARGETNEUTRALUNIT";
						if (localPlayer.hasAlliance(mousedUnit.getPlayerIndex(), CAllianceType.PASSIVE)) {
							if (mousedUnitPlayer.hasAlliance(this.war3MapViewer.getLocalPlayerIndex(),
									CAllianceType.HELP_REQUEST)) {
								soundKey = "GAMETARGETFRIENDLYUNIT";
							}
						}
						else {
							soundKey = "GAMETARGETHOSTILEUNIT";
						}
//						this.uiSounds.getSound(soundKey).play(this.uiScene.audioContext, 0, 0, 0);
					}
					setTarget(this.mouseOverUnit);
				}
			}
			if ((this.mouseOverUnit == null) && (this.touchDownX == screenX) && (this.touchDownY == screenY)) {
				if (this.targetUnit != null) {
//					this.uiSounds.getSound("INTERFACESOUND_LOSTTARGETUNIT").play(this.uiScene.audioContext, 0, 0, 0);
					this.war3MapViewer.deselect();
					setTarget(null);
				}
			}
			Gdx.input.setCursorPosition(this.touchDownX, this.touchDownY);
		}
		this.mouseDownUIFrame = null;
		return false;
	}

	@Override
	public boolean touchDragged(final int screenX, final int screenY, final float worldScreenY, final int pointer) {
		final int newX = screenX;
		final int newY = screenY;
		final int dx = newX - this.lastX;
		final int dy = newY - this.lastY;
		if (this.touchDown) {
			if (this.button == Input.Buttons.LEFT) {
				this.cameraManager.horizontalAngle -= Math.toRadians(dx * 0.15 * 2);
				this.cameraManager.verticalAngle -= Math.toRadians(dy * 0.15 * 2);
			}
			else if (this.button == Input.Buttons.RIGHT) {
				this.cameraManager.horizontalAngle -= Math.toRadians(dx * 0.15 * 2);
				this.cameraManager.verticalAngle -= Math.toRadians(dy * 0.15 * 2);
				this.pawnUnit.setFacing((float) Math.toDegrees(this.cameraManager.horizontalAngle));
			}
			this.lastX = newX;
			this.lastY = newY;
		}
		else if (this.mouseDownUIFrame != null) {
			screenCoordsVector.set(screenX, screenY);
			this.uiViewport.unproject(screenCoordsVector);
			this.mouseDownUIFrame.mouseDragged(this.rootFrame, this.uiViewport, screenCoordsVector.x,
					screenCoordsVector.y);
		}
		return false;
	}

	@Override
	public boolean mouseMoved(final int screenX, final int screenY, final float worldScreenY) {
		this.lastX = screenX;
		this.lastY = screenY;
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
				if (this.tooltipFrame != null) {
					this.tooltipFrame.setVisible(false);
				}
			}
		}
		if (mousedUIFrame == null) {
		}
		updateMouseOverUnit(screenX, worldScreenY);
		return false;
	}

	private void loadTooltip(final ClickableActionFrame mousedUIFrame) {
		final String toolTip = mousedUIFrame.getToolTip();
		final String uberTip = mousedUIFrame.getUberTip();
		if ((toolTip == null) || (uberTip == null)) {
			if (this.tooltipFrame != null) {
				this.tooltipFrame.setVisible(false);
			}
		}
		else {
			if (this.tooltipFrame1 != null) {
				this.rootFrame.setText(this.tooltipFrame1, uberTip);
			}
		}
	}

	public void setTarget(final RenderWidget target) {
		this.targetUnit = target;
		this.rootFrame.getLuaGlobals().notifySetTarget(this.targetUnit);

	}

	@Override
	public boolean scrolled(final float amountX, final float amountY) {
		this.cameraManager.distance += amountY * 10;
		return false;
	}

	@Override
	public void gameClosed() {

	}

	@Override
	public void onHide() {
		this.war3MapViewer.simulation.setFogEnabled(true);
		this.war3MapViewer.simulation.setFogMaskEnabled(true);
		this.war3MapViewer.removeScene(this.uiScene);
		this.war3MapViewer.removeScene(this.portraitScene);
		this.war3MapViewer.addScene(this.portraitScene);
		this.war3MapViewer.addScene(this.uiScene);
		this.showing = false;
		this.cursorFrame.setVisible(false);
//		this.skyModelInstance.hide();

	}

	@Override
	public void onShow() {
		this.war3MapViewer.simulation.setFogEnabled(false);
		this.war3MapViewer.simulation.setFogMaskEnabled(false);
		this.war3MapViewer.removeScene(this.portraitScene);
//		this.war3MapViewer.addScene(this.portraitScene);
//		this.war3MapViewer.addScene(this.uiScene);
		this.showing = true;
		this.cursorFrame.setVisible(true);
//		this.skyModelInstance.show();
	}

	private final class AnyClickableUnitFilter implements CWidgetFilterFunction {
		@Override
		public boolean call(final CWidget unit) {
			final RenderWidget renderPeer = ThirdPersonUI.this.war3MapViewer.getRenderPeer(unit);
			return !unit.isDead() && renderPeer.isSelectable(ThirdPersonUI.this.war3MapViewer.simulation,
					ThirdPersonUI.this.war3MapViewer.getLocalPlayerIndex());
		}
	}

	private final class AnyTargetableUnitFilter implements CWidgetFilterFunction {
		@Override
		public boolean call(final CWidget unit) {
			return !unit.isDead();
		}
	}

	private boolean isUnitSelectable(final RenderWidget mouseOverUnit) {
		return mouseOverUnit.isSelectable(this.war3MapViewer.simulation, this.war3MapViewer.getLocalPlayerIndex())
				&& !mouseOverUnit.getSimulationWidget().isDead();
	}
}
