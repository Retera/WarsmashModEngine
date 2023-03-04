package com.etheller.warsmash.viewer5.handlers.w3x.ui.thirdperson;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.etheller.warsmash.viewer5.ModelInstance;
import com.etheller.warsmash.viewer5.Scene;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxComplexInstance;
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
	private final PlayerPawn playerPawn;
	private final ThirdPersonCameraManager cameraManager;
	private final War3MapViewer war3MapViewer;
	private CFogModifier myFogModifier;
	private final Scene uiScene;
	private final Scene portraitScene;
	private final Rectangle tempRect = new Rectangle();
	private int lastX;
	private int lastY;
	private int button;
	private boolean showing = false;

	public ThirdPersonUI(final War3MapViewer war3MapViewer, final Scene uiScene, final Scene portraitScene,
			final String pawnModel) {
		this.war3MapViewer = war3MapViewer;
		this.uiScene = uiScene;
		this.portraitScene = portraitScene;
		final MdxModel pawnMdx = war3MapViewer.loadModelMdx(pawnModel);
		final ModelInstance pawnModelInstance = pawnMdx.addInstance();
		pawnModelInstance.setScene(war3MapViewer.worldScene);

		final MdxComplexInstance pawnComplexInstance = (MdxComplexInstance) pawnModelInstance;
		pawnComplexInstance.setBlendTime(150);
		final UnitAnimationListenerImpl animationProcessor = new UnitAnimationListenerImpl(pawnComplexInstance, 3, 4);

		final String texture = "ReplaceableTextures\\Shadows\\ShadowFlyer.blp";
		final SplatMover unitShadowSplatDynamicIngame = war3MapViewer.terrain.addUnitShadowSplat(texture, -32, -32, 32,
				32, 3, 1.0f, false);
		this.playerPawn = new PlayerPawn(pawnModelInstance, animationProcessor, unitShadowSplatDynamicIngame,
				pawnComplexInstance);
		this.cameraManager = new ThirdPersonCameraManager(this.playerPawn);
		this.cameraManager.setupCamera(this.war3MapViewer.worldScene);
	}

	@Override
	public void main() {
		final CPlayer localPlayer = this.war3MapViewer.simulation.getPlayer(this.war3MapViewer.getLocalPlayerIndex());
		this.myFogModifier = new CFogModifier(CFogState.VISIBLE, this.war3MapViewer.terrain.getEntireMap());
		localPlayer.addFogModifer(this.myFogModifier);
	}

	@Override
	public void update(final float deltaTime) {
		this.playerPawn.update(this.war3MapViewer);

		if (this.showing) {
			this.cameraManager.updateCamera();
		}
	}

	@Override
	public void render(final SpriteBatch batch, final GlyphLayout glyphLayout) {

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
		this.cameraManager.setTouchDown(true);
		return false;
	}

	@Override
	public boolean touchUp(final int screenX, final int screenY, final float worldScreenY, final int button) {
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

	}

	@Override
	public void onShow() {
		this.myFogModifier.setEnabled(true);
		this.war3MapViewer.removeScene(this.portraitScene);
//		this.war3MapViewer.removeScene(this.uiScene);
		this.showing = true;
	}

}
