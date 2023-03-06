package com.etheller.warsmash.viewer5.handlers.w3x.ui.thirdperson;

import java.io.IOException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.etheller.warsmash.parsers.fdf.GameUI;
import com.etheller.warsmash.parsers.fdf.datamodel.FramePoint;
import com.etheller.warsmash.parsers.fdf.frames.SpriteFrame;
import com.etheller.warsmash.util.ImageUtils;
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
	private int button;
	private boolean showing = false;
	private GameUI rootFrame;
	private SpriteFrame cursorFrame;
	private boolean touchDown;
//	private final ModelInstance skyModelInstance;
	private Texture uiPortraitTexture;
	private Texture uiMinimapTexture;
	private Texture endCapTexture;

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

		this.rootFrame = new GameUI(this.war3MapViewer.dataSource, GameUI.loadSkin(this.war3MapViewer.dataSource, 0),
				this.uiViewport, this.uiScene, this.war3MapViewer, 0, this.war3MapViewer.getAllObjectData().getWts());

		try {
			this.rootFrame.loadTOCFile("Interface\\FrameXML\\FrameXML.toc");
		}
		catch (final IOException e) {
			throw new IllegalStateException(e);
		}

		this.cursorFrame = (SpriteFrame) this.rootFrame.createFrameByType("SPRITE", "SmashTPCursorFrame",
				this.rootFrame, "", 0);
		this.rootFrame.setSpriteFrameModel(this.cursorFrame, "Interface\\Cursor\\Cursor.mdx");
		this.cursorFrame.setSequence("Point");
		this.cursorFrame.setZDepth(1.0f);

		this.uiPortraitTexture = ImageUtils.getAnyExtensionTexture(this.war3MapViewer.dataSource,
				"Interface\\CharacterFrame\\UI-Player-Portrait.blp");
		this.uiMinimapTexture = ImageUtils.getAnyExtensionTexture(this.war3MapViewer.dataSource,
				"Interface\\Minimap\\UI-Minimap-Border.blp");

		this.endCapTexture = ImageUtils.getAnyExtensionTexture(this.war3MapViewer.dataSource,
				"Interface\\MainMenuBar\\UI-MainMenuBar-EndCap-Dwarf.blp");
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
		batch.draw(this.uiMinimapTexture, worldWidth - (this.uiMinimapTexture.getWidth() * 2),
				worldHeight - (this.uiMinimapTexture.getHeight() * 2), this.uiMinimapTexture.getWidth() * 2,
				this.uiMinimapTexture.getHeight() * 2);
		batch.draw(this.uiPortraitTexture, 0, worldHeight - (this.uiPortraitTexture.getHeight() * 2),
				this.uiPortraitTexture.getWidth() * 2, this.uiPortraitTexture.getHeight() * 2);

		batch.draw(this.endCapTexture, 0, 0, this.endCapTexture.getWidth() * 2, this.endCapTexture.getHeight() * 2);
		batch.draw(this.endCapTexture, worldWidth - this.endCapTexture.getWidth(), 0, this.endCapTexture.getWidth() * 2,
				this.endCapTexture.getHeight() * 2, 0, 0, this.endCapTexture.getWidth(), this.endCapTexture.getHeight(),
				true, false);
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
		this.lastX = screenX;
		this.lastY = screenY;
		this.button = button;
		this.touchDown = true;
		this.cameraManager.setTouchDown(true);
		return false;
	}

	@Override
	public boolean touchUp(final int screenX, final int screenY, final float worldScreenY, final int button) {
		this.touchDown = false;
		this.cameraManager.setTouchDown(false);
		return false;
	}

	@Override
	public boolean touchDragged(final int screenX, final int screenY, final float worldScreenY, final int pointer) {
		final int newX = screenX;
		final int newY = screenY;
		final int dx = newX - this.lastX;
		final int dy = newY - this.lastY;
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
		return false;
	}

	@Override
	public boolean mouseMoved(final int screenX, final int screenY, final float worldScreenY) {
		this.lastX = screenX;
		this.lastY = screenY;
		return false;
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
		this.myFogModifier.setEnabled(false);
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
