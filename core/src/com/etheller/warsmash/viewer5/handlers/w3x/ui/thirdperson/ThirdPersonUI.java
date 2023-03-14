package com.etheller.warsmash.viewer5.handlers.w3x.ui.thirdperson;

import java.io.IOException;

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
import com.etheller.warsmash.util.WorldEditStrings;
import com.etheller.warsmash.viewer5.ModelInstance;
import com.etheller.warsmash.viewer5.Scene;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxCharacterInstance;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxModel;
import com.etheller.warsmash.viewer5.handlers.w3x.SplatModel.SplatMover;
import com.etheller.warsmash.viewer5.handlers.w3x.War3MapViewer;
import com.etheller.warsmash.viewer5.handlers.w3x.camera.ThirdPersonCameraManager;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.RenderWidget.UnitAnimationListenerImpl;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CFogModifier;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CFogState;
import com.etheller.warsmash.viewer5.handlers.w3x.ui.WarsmashToggleableUI;
import com.etheller.warsmash.viewer5.handlers.w3x.ui.command.ClickableActionFrame;
import com.etheller.warsmash.viewer5.handlers.w3x.ui.command.ClickableFrame;
import com.etheller.warsmash.viewer5.handlers.w3x.ui.sound.KeyedSounds;
import com.hiveworkshop.rms.util.BinaryReader;

public class ThirdPersonUI implements WarsmashToggleableUI {
	private static final Vector2 screenCoordsVector = new Vector2();
	private final PlayerPawn playerPawn;
	private final ThirdPersonCameraManager cameraManager;
	private final War3MapViewer war3MapViewer;
	private CFogModifier myFogModifier;
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

	public ThirdPersonUI(final War3MapViewer war3MapViewer, final Scene uiScene, final Viewport uiViewport,
			final Scene portraitScene, final String pawnModel) {
		this.war3MapViewer = war3MapViewer;
		this.uiScene = uiScene;
		this.uiViewport = uiViewport;
		this.portraitScene = portraitScene;
		final MdxModel pawnMdx = war3MapViewer.loadModelMdx(pawnModel);
		final ModelInstance pawnModelInstance = pawnMdx.addInstance(2);
		pawnModelInstance.setScene(war3MapViewer.worldScene);

		final MdxCharacterInstance pawnComplexInstance = (MdxCharacterInstance) pawnModelInstance;
		pawnComplexInstance.setBlendTime(150);
		final UnitAnimationListenerImpl animationProcessor = new UnitAnimationListenerImpl(pawnComplexInstance, 3, 4);

		final String texture = "ReplaceableTextures\\Shadows\\ShadowFlyer.blp";
		final SplatMover unitShadowSplatDynamicIngame = war3MapViewer.terrain.addUnitShadowSplat(texture, -16, -16, 16,
				16, 3, 1.0f, false);
		this.playerPawn = new PlayerPawn(pawnModelInstance, animationProcessor, unitShadowSplatDynamicIngame,
				pawnComplexInstance);
		this.cameraManager = new ThirdPersonCameraManager(this.playerPawn, war3MapViewer);
		this.cameraManager.setupCamera(this.war3MapViewer.worldScene);

//		final MdxModel skyModel = war3MapViewer
//				.loadModelMdx("environment\\sky\\lordaeronsummersky\\lordaeronsummersky.mdx");
//		this.skyModelInstance = skyModel.addInstance();
////		this.skyModelInstance.setParent(pawnComplexInstance.getAttachment(0));
//		this.skyModelInstance.setScene(war3MapViewer.worldScene);
//		this.skyModelInstance.uniformScale(10);
//		this.skyModelInstance.setLocation(0, 0, 0);
//		((MdxComplexInstance) this.skyModelInstance).setSequence(0);

	}

	public PlayerPawn getPlayerPawn() {
		return this.playerPawn;
	}

	@Override
	public void main() {
		final CPlayer localPlayer = this.war3MapViewer.simulation.getPlayer(this.war3MapViewer.getLocalPlayerIndex());
		this.myFogModifier = new CFogModifier(CFogState.VISIBLE, this.war3MapViewer.terrain.getEntireMap());
		localPlayer.addFogModifer(this.myFogModifier);

		final WorldEditStrings worldEditStrings = new WorldEditStrings(this.war3MapViewer.mapMpq);
		final DataTable uiSoundsTable = new DataTable(worldEditStrings);
		try {
			DbcParser.parse(new BinaryReader(this.war3MapViewer.mapMpq.read("DBFilesClient\\SoundEntries.dbc")),
					new DbcDecoderSoundEntries(), uiSoundsTable);
		}
		catch (final IOException e1) {
			e1.printStackTrace();
		}

		this.rootFrame = new GameUI(this.war3MapViewer.mapMpq, GameUI.loadSkin(this.war3MapViewer.mapMpq, 0),
				this.uiViewport, this.uiScene, this.war3MapViewer, 0, this.war3MapViewer.getAllObjectData().getWts(),
				new KeyedSounds(uiSoundsTable, this.war3MapViewer.mapMpq));

		try {
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
		this.playerPawn.update(this.war3MapViewer);

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
			this.playerPawn.jump();
		}
		if ((keycode == Input.Keys.LEFT) || (keycode == Input.Keys.A)) {
			this.playerPawn.getCameraPanControls().left = true;
			return true;
		}
		else if ((keycode == Input.Keys.RIGHT) || (keycode == Input.Keys.D)) {
			this.playerPawn.getCameraPanControls().right = true;
			return true;
		}
		else if ((keycode == Input.Keys.DOWN) || (keycode == Input.Keys.S)) {
			this.playerPawn.getCameraPanControls().down = true;
			return true;
		}
		else if ((keycode == Input.Keys.UP) || (keycode == Input.Keys.W)) {
			this.playerPawn.getCameraPanControls().up = true;
			return true;
		}
		return false;
	}

	@Override
	public boolean keyUp(final int keycode) {
		if ((keycode == Input.Keys.LEFT) || (keycode == Input.Keys.A)) {
			this.playerPawn.getCameraPanControls().left = false;
			return true;
		}
		else if ((keycode == Input.Keys.RIGHT) || (keycode == Input.Keys.D)) {
			this.playerPawn.getCameraPanControls().right = false;
			return true;
		}
		else if ((keycode == Input.Keys.DOWN) || (keycode == Input.Keys.S)) {
			this.playerPawn.getCameraPanControls().down = false;
			return true;
		}
		else if ((keycode == Input.Keys.UP) || (keycode == Input.Keys.W)) {
			this.playerPawn.getCameraPanControls().up = false;
			return true;
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
			this.cameraManager.setTouchDown(true);
		}
		else {
			if (clickedUIFrame instanceof ClickableFrame) {
				this.mouseDownUIFrame = (ClickableFrame) clickedUIFrame;
				this.mouseDownUIFrame.mouseDown(this.rootFrame, this.uiViewport);
			}
		}
		return false;
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
			this.cameraManager.setTouchDown(false);
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
				this.cameraManager.horizontalAngle -= Math.toRadians(dx * 0.15);
				this.cameraManager.verticalAngle -= Math.toRadians(dy * 0.15);
			}
			else if (this.button == Input.Buttons.RIGHT) {
				this.cameraManager.horizontalAngle -= Math.toRadians(dx * 0.15);
				this.cameraManager.verticalAngle -= Math.toRadians(dy * 0.15);
				this.playerPawn.setFacingDegrees((float) Math.toDegrees(this.cameraManager.horizontalAngle));
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
				this.tooltipFrame.setVisible(false);
			}
		}
		if (mousedUIFrame == null) {
		}
		return false;
	}

	private void loadTooltip(final ClickableActionFrame mousedUIFrame) {
		final String toolTip = mousedUIFrame.getToolTip();
		final String uberTip = mousedUIFrame.getUberTip();
		if ((toolTip == null) || (uberTip == null)) {
			this.tooltipFrame.setVisible(false);
		}
		else {
			this.rootFrame.setText(this.tooltipFrame1, uberTip);
		}
	}

	@Override
	public boolean scrolled(final int amount) {
		this.cameraManager.distance += amount * 10;
		return false;
	}

	@Override
	public void gameClosed() {

	}

	@Override
	public void onHide() {
		this.myFogModifier.setEnabled(true);
		this.war3MapViewer.removeScene(this.uiScene);
		this.war3MapViewer.addScene(this.portraitScene);
		this.war3MapViewer.addScene(this.uiScene);
		this.showing = false;
		this.cursorFrame.setVisible(false);
//		this.skyModelInstance.hide();

	}

	@Override
	public void onShow() {
		this.myFogModifier.setEnabled(true);
		this.war3MapViewer.removeScene(this.portraitScene);
//		this.war3MapViewer.removeScene(this.uiScene);
		this.showing = true;
		this.cursorFrame.setVisible(true);
//		this.skyModelInstance.show();
	}

}
