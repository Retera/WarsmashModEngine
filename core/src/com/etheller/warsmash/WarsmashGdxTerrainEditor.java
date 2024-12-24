package com.etheller.warsmash;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.etheller.warsmash.units.DataTable;
import com.etheller.warsmash.units.Element;
import com.etheller.warsmash.util.ImageUtils;
import com.etheller.warsmash.viewer5.Scene;
import com.etheller.warsmash.viewer5.handlers.w3x.War3MapViewer;
import com.etheller.warsmash.viewer5.handlers.w3x.camera.CameraPreset;
import com.etheller.warsmash.viewer5.handlers.w3x.camera.CameraRates;
import com.etheller.warsmash.viewer5.handlers.w3x.camera.GameCameraManager;

public class WarsmashGdxTerrainEditor extends ApplicationAdapter implements InputProcessor {
	public static final boolean ENABLE_AUDIO = true;
	private War3MapViewer viewer;
	private final Rectangle tempRect = new Rectangle();

	// libGDX stuff
	private OrthographicCamera uiCamera;
	private SpriteBatch batch;
	private ExtendViewport uiViewport;
	private GlyphLayout glyphLayout;

	private Texture solidGreenTexture;

	private ShapeRenderer shapeRenderer;
	private Scene previewScene;
	private final List<Message> errorMessages = new LinkedList<>();
	private BitmapFont defaultFont;
	private GameCameraManager cameraManager;

	public WarsmashGdxTerrainEditor() {
	}

	public void loadViewer(final War3MapViewer mapViewer) {
		this.viewer = mapViewer;

		final Element cameraData = this.viewer.miscData.get("Camera");
		Element cameraListenerData = this.viewer.miscData.get("Listener");
		if (cameraListenerData == null) {
			cameraListenerData = new Element("Listener", new DataTable(null));
		}
		final CameraPreset[] cameraPresets = new CameraPreset[6];
		for (int i = 0; i < cameraPresets.length; i++) {
			cameraPresets[i] = new CameraPreset(cameraData.getFieldFloatValue("AOA", i),
					cameraData.getFieldFloatValue("FOV", i), cameraData.getFieldFloatValue("Rotation", i),
					cameraData.getFieldFloatValue("Rotation", i + cameraPresets.length),
					cameraData.getFieldFloatValue("Rotation", i + (cameraPresets.length * 2)),
					cameraData.getFieldFloatValue("Distance", i), cameraData.getFieldFloatValue("FarZ", i),
					cameraData.getFieldFloatValue("NearZ", i), cameraData.getFieldFloatValue("Height", i),
					cameraListenerData.getFieldFloatValue("ListenerDistance", i),
					cameraListenerData.getFieldFloatValue("ListenerAOA", i));
		}
		final Element cameraRatesElement = this.viewer.miscData.get("CameraRates");
		final CameraRates cameraRates = new CameraRates(cameraRatesElement.getFieldFloatValue("AOA"),
				cameraRatesElement.getFieldFloatValue("FOV"), cameraRatesElement.getFieldFloatValue("Rotation"),
				cameraRatesElement.getFieldFloatValue("Distance"), cameraRatesElement.getFieldFloatValue("Forward"),
				cameraRatesElement.getFieldFloatValue("Strafe"));
		this.solidGreenTexture = ImageUtils.getAnyExtensionTexture(this.viewer.dataSource,
				"ReplaceableTextures\\TeamColor\\TeamColor06.blp");

		this.cameraManager = new GameCameraManager(cameraPresets, cameraRates);

		this.cameraManager.setupCamera(this.viewer.worldScene);

		System.out.println("Loaded");
		this.previewScene = this.viewer.addSimpleScene();

		try {
			this.viewer.loadAfterUI();
		}
		catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void create() {
		final ByteBuffer tempByteBuffer = ByteBuffer.allocateDirect(4);
		tempByteBuffer.order(ByteOrder.LITTLE_ENDIAN);
		final IntBuffer temp = tempByteBuffer.asIntBuffer();

		Gdx.gl30.glGenVertexArrays(1, temp);
		WarsmashGdxGame.VAO = temp.get(0);

		Gdx.gl30.glBindVertexArray(WarsmashGdxGame.VAO);

		final String renderer = Gdx.gl.glGetString(GL20.GL_RENDERER);
		System.err.println("Renderer: " + renderer);

		Gdx.gl30.glClearColor(0.0f, 0.0f, 0.0f, 1); // TODO remove white background
		Gdx.gl30.glEnable(GL30.GL_SCISSOR_TEST);

		// libGDX stuff
		final int width = Gdx.graphics.getWidth();
		final int height = Gdx.graphics.getHeight();

		this.glyphLayout = new GlyphLayout();

		// Constructs a new OrthographicCamera, using the given viewport width and
		// height
		// Height is multiplied by aspect ratio.
		this.uiCamera = new OrthographicCamera();
		int aspect3By4Width;
		int aspect3By4Height;
		if (width < ((height * 4) / 3)) {
			aspect3By4Width = width;
			aspect3By4Height = (width * 3) / 4;
		}
		else {
			aspect3By4Width = (height * 4) / 3;
			aspect3By4Height = height;
		}
		this.uiViewport = new ExtendViewport(aspect3By4Width, aspect3By4Height, this.uiCamera);
		this.uiViewport.update(width, height);

		this.uiCamera.position.set(this.uiViewport.getMinWorldWidth() / 2, this.uiViewport.getMinWorldHeight() / 2, 0);
		this.uiCamera.update();

		this.batch = new SpriteBatch();
		this.defaultFont = new BitmapFont();

//		this.consoleUITexture = new Texture(new DataSourceFileHandle(this.viewer.dataSource, "AlphaUi.png"));

		Gdx.input.setInputProcessor(this);

		this.shapeRenderer = new ShapeRenderer();

		resize(width, height);
	}

	@Override
	public void render() {
		if (this.viewer == null) {
			return;
		}
		Gdx.gl30.glEnable(GL30.GL_SCISSOR_TEST);
		Gdx.gl30.glBindVertexArray(WarsmashGdxGame.VAO);
		this.cameraManager.applyVelocity(Gdx.graphics.getDeltaTime(), false, false, false, false);
		final float groundHeight = Math.max(
				this.viewer.terrain.getGroundHeight(this.cameraManager.target.x, this.cameraManager.target.y),
				this.viewer.terrain.getWaterHeight(this.cameraManager.target.x, this.cameraManager.target.y));
		this.cameraManager.updateTargetZ(groundHeight);
		this.cameraManager.updateCamera();
		this.viewer.updateAndRender();

		Gdx.gl30.glDisable(GL30.GL_SCISSOR_TEST);

		Gdx.gl30.glDisable(GL30.GL_CULL_FACE);

		this.viewer.webGL.useShaderProgram(null);

		Gdx.gl30.glActiveTexture(GL30.GL_TEXTURE0);

		renderLibGDXContent();
	}

	private void renderLibGDXContent() {

		Gdx.gl30.glDisable(GL30.GL_SCISSOR_TEST);

		Gdx.gl30.glDisable(GL30.GL_CULL_FACE);

		this.viewer.webGL.useShaderProgram(null);

		Gdx.gl30.glActiveTexture(GL30.GL_TEXTURE0);

		this.uiViewport.apply();
		this.batch.setProjectionMatrix(this.uiCamera.combined);
		this.batch.begin();
		final Iterator<Message> errorMessageIterator = this.errorMessages.iterator();
		final long nowTime = TimeUtils.millis();
		int messageIndex = 0;
		while (errorMessageIterator.hasNext()) {
			final Message errorMessage = errorMessageIterator.next();
			if ((nowTime - errorMessage.time) >= 30000) {
				errorMessageIterator.remove();
			}
			else {
				this.defaultFont.draw(this.batch, errorMessage.text, 0,
						((++messageIndex) * this.defaultFont.getLineHeight()));
			}
		}
		this.batch.end();

		Gdx.gl30.glEnable(GL30.GL_SCISSOR_TEST);
		Gdx.gl30.glBindVertexArray(WarsmashGdxGame.VAO);
	}

	@Override
	public void dispose() {
		this.batch.dispose();
	}

	@Override
	public void resize(final int width, final int height) {
//		super.resize(width, height);
		if (this.viewer == null) {
			return;
		}
		this.cameraManager.resize(setupWorldFrameViewport(width, height));
		this.previewScene.camera.viewport(this.tempRect.set(0, 0, 200, height));

		this.uiViewport.update(width, height);
		this.uiCamera.position.set(this.uiViewport.getMinWorldWidth() / 2, this.uiViewport.getMinWorldHeight() / 2, 0);

	}

	private Rectangle setupWorldFrameViewport(final int width, final int height) {
		this.tempRect.x = 0;
		this.tempRect.width = width;
		this.tempRect.y = 0;
		this.tempRect.height = height;
		return this.tempRect;
	}

	@Override
	public boolean keyDown(final int keycode) {
		if (this.viewer == null) {
			return false;
		}
		return this.cameraManager.keyDown(keycode);
	}

	@Override
	public boolean keyUp(final int keycode) {
		if (this.viewer == null) {
			return false;
		}
		return this.cameraManager.keyUp(keycode);
	}

	@Override
	public boolean keyTyped(final char character) {
		return false;
	}

	@Override
	public boolean touchDown(final int screenX, final int screenY, final int pointer, final int button) {
		if (this.viewer == null) {
			return false;
		}
		final float worldScreenY = this.viewer.canvas.getHeight() - screenY;

		return false;
	}

	@Override
	public boolean touchUp(final int screenX, final int screenY, final int pointer, final int button) {
		if (this.viewer == null) {
			return false;
		}
		final float worldScreenY = this.viewer.canvas.getHeight() - screenY;

		return false;
	}

	@Override
	public boolean touchDragged(final int screenX, final int screenY, final int pointer) {
		if (this.viewer == null) {
			return false;
		}
		final float worldScreenY = this.viewer.canvas.getHeight() - screenY;

		return false;
	}

	@Override
	public boolean mouseMoved(final int screenX, final int screenY) {
		if (this.viewer == null) {
			return false;
		}
		final float worldScreenY = this.viewer.canvas.getHeight() - screenY;

		return false;
	}

	@Override
	public boolean scrolled(final float amountX, final float amountY) {
		if (this.viewer == null) {
			return false;
		}
		this.cameraManager.scrolled((int) amountY);
		return true;
	}

	@Override
	public boolean touchCancelled(final int screenX, final int screenY, final int pointer, final int button) {
		return false;
	}

	private static class Message {
		private final float time;
		private final String text;

		public Message(final float time, final String text) {
			this.time = time;
			this.text = text;
		}
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

}
