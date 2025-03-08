package com.etheller.warsmash;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.etheller.warsmash.datasources.CascDataSourceDescriptor;
import com.etheller.warsmash.datasources.CompoundDataSource;
import com.etheller.warsmash.datasources.CompoundDataSourceDescriptor;
import com.etheller.warsmash.datasources.DataSource;
import com.etheller.warsmash.datasources.DataSourceDescriptor;
import com.etheller.warsmash.datasources.FolderDataSourceDescriptor;
import com.etheller.warsmash.datasources.MpqDataSourceDescriptor;
import com.etheller.warsmash.datasources.SubdirDataSource;
import com.etheller.warsmash.parsers.fdf.GameUI;
import com.etheller.warsmash.parsers.jass.Jass2;
import com.etheller.warsmash.parsers.jass.Jass2.CommonEnvironment;
import com.etheller.warsmash.parsers.jass.Jass2.RootFrameListener;
import com.etheller.warsmash.units.DataTable;
import com.etheller.warsmash.units.Element;
import com.etheller.warsmash.util.ImageUtils;
import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.util.WarsmashUtils;
import com.etheller.warsmash.viewer5.Model;
import com.etheller.warsmash.viewer5.ModelInstance;
import com.etheller.warsmash.viewer5.ModelViewer;
import com.etheller.warsmash.viewer5.PathSolver;
import com.etheller.warsmash.viewer5.RenderBatch;
import com.etheller.warsmash.viewer5.Scene;
import com.etheller.warsmash.viewer5.TextureMapper;
import com.etheller.warsmash.viewer5.handlers.ModelHandler;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxModel;
import com.etheller.warsmash.viewer5.handlers.w3x.War3MapViewer;
import com.etheller.warsmash.viewer5.handlers.w3x.camera.CameraPreset;
import com.etheller.warsmash.viewer5.handlers.w3x.camera.CameraRates;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayerUnitOrderListener;
import com.etheller.warsmash.viewer5.handlers.w3x.ui.MeleeUI;
import com.etheller.warsmash.viewer5.handlers.w3x.ui.WarsmashUI;
import com.etheller.warsmash.viewer5.handlers.w3x.ui.toggle.MeleeToggleUI;

public class WarsmashGdxMapScreen implements InputProcessor, Screen {
	public static final boolean ENABLE_AUDIO = true;
	private final War3MapViewer viewer;
	private final Rectangle tempRect = new Rectangle();

	// libGDX stuff
	private OrthographicCamera uiCamera;
	private SpriteBatch batch;
	private ExtendViewport uiViewport;
	private GlyphLayout glyphLayout;

	private Texture solidGreenTexture;

	private ShapeRenderer shapeRenderer;

	private MdxModel timeIndicator;

	private Scene uiScene;
	private WarsmashUI meleeUI;

	private Music currentMusic;
	private final WarsmashGdxMultiScreenGame screenManager;
	private final WarsmashGdxMenuScreen menuScreen;
	private final CPlayerUnitOrderListener uiOrderListener;
	private CommonEnvironment commonEnv;

	public WarsmashGdxMapScreen(final War3MapViewer mapViewer, final WarsmashGdxMultiScreenGame screenManager,
			final WarsmashGdxMenuScreen menuScreen, final CPlayerUnitOrderListener uiOrderListener) {
		this.viewer = mapViewer;
		this.screenManager = screenManager;
		this.menuScreen = menuScreen;
		this.uiOrderListener = uiOrderListener;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.badlogic.gdx.ApplicationAdapter#create()
	 */
	@Override
	public void show() {

		final ByteBuffer tempByteBuffer = ByteBuffer.allocateDirect(4);
		tempByteBuffer.order(ByteOrder.LITTLE_ENDIAN);
		final IntBuffer temp = tempByteBuffer.asIntBuffer();

		Gdx.gl30.glGenVertexArrays(1, temp);
		WarsmashGdxGame.VAO = temp.get(0);

		Gdx.gl30.glBindVertexArray(WarsmashGdxGame.VAO);

		final String renderer = Gdx.gl.glGetString(GL20.GL_RENDERER);
		System.err.println("Renderer: " + renderer);

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

		System.out.println("Loaded");
		Gdx.gl30.glClearColor(0.0f, 0.0f, 0.0f, 1); // TODO remove white background
		Gdx.gl30.glEnable(GL30.GL_SCISSOR_TEST);

		final Scene portraitScene = this.viewer.addSimpleScene();
		this.uiScene = this.viewer.addSimpleScene();
		this.uiScene.alpha = true;
		if (ENABLE_AUDIO) {
			this.uiScene.enableAudio();
		}

//		this.mainModel = (MdxModel) this.viewer.load("UI\\Glues\\MainMenu\\MainMenu3D_exp\\MainMenu3D_exp.mdx",

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

//		this.consoleUITexture = new Texture(new DataSourceFileHandle(this.viewer.dataSource, "AlphaUi.png"));

		this.solidGreenTexture = ImageUtils.getAnyExtensionTexture(this.viewer.dataSource,
				"ReplaceableTextures\\TeamColor\\TeamColor06.blp");

		Gdx.input.setInputProcessor(this);

		this.shapeRenderer = new ShapeRenderer();

		final Element cameraRatesElement = this.viewer.miscData.get("CameraRates");
		final CameraRates cameraRates = new CameraRates(cameraRatesElement.getFieldFloatValue("AOA"),
				cameraRatesElement.getFieldFloatValue("FOV"), cameraRatesElement.getFieldFloatValue("Rotation"),
				cameraRatesElement.getFieldFloatValue("Distance"), cameraRatesElement.getFieldFloatValue("Forward"),
				cameraRatesElement.getFieldFloatValue("Strafe"));
		final MeleeUI baseMeleeUI = new MeleeUI(this.viewer.mapMpq, this.uiViewport, this.uiScene, portraitScene,
				cameraPresets, cameraRates, this.viewer, new RootFrameListener() {
					@Override
					public void onCreate(final GameUI rootFrame) {
						WarsmashGdxMapScreen.this.viewer.setGameUI(rootFrame);
					}
				}, this.uiOrderListener, new Runnable() {
					@Override
					public void run() {
						WarsmashGdxMapScreen.this.menuScreen.onReturnFromGame();
						WarsmashGdxMapScreen.this.screenManager.setScreen(WarsmashGdxMapScreen.this.menuScreen);
					}
				});
		final MeleeToggleUI toggleUI = new MeleeToggleUI(baseMeleeUI, Arrays.asList(baseMeleeUI));
		this.meleeUI = toggleUI;
		this.viewer.getCommandErrorListener().setDelegate(this.meleeUI);
		final ModelInstance libgdxContentInstance = new LibGDXContentLayerModel(null, this.viewer, "",
				this.viewer.mapPathSolver, "").addInstance();
		libgdxContentInstance.setLocation(0f, 0f, -0.5f);
		libgdxContentInstance.setScene(this.uiScene);
		this.meleeUI.main();
		toggleUI.setCurrentUI(0);

		updateUIScene();

		resize(width, height);

		try {
			this.viewer.loadAfterUI();
		}
		catch (final IOException e) {
			throw new RuntimeException(e);
		}
		this.commonEnv = Jass2.loadCommon(this.viewer.mapMpq, this.uiViewport, this.uiScene, this.viewer, this.meleeUI,
				WarsmashConstants.JASS_FILE_LIST);
		this.commonEnv.main();
	}

	public static DataSource parseDataSources(final DataTable warsmashIni) {
		final Element dataSourcesConfig = warsmashIni.get("DataSources");
		final List<DataSourceDescriptor> dataSourcesList = new ArrayList<>();
		final List<String> allCascPrefixes = new ArrayList<>();
		for (int i = 0; i < dataSourcesConfig.size(); i++) {
			final String type = dataSourcesConfig.getField("Type" + (i < 10 ? "0" : "") + i);
			final String path = dataSourcesConfig.getField("Path" + (i < 10 ? "0" : "") + i);
			switch (type) {
			case "Folder": {
				dataSourcesList.add(new FolderDataSourceDescriptor(path));
				break;
			}
			case "MPQ": {
				dataSourcesList.add(new MpqDataSourceDescriptor(path));
				break;
			}
			case "CASC": {
				final String prefixes = dataSourcesConfig.getField("Prefixes" + (i < 10 ? "0" : "") + i);
				final List<String> parsedPrefixes = Arrays.asList(prefixes.split(","));
				allCascPrefixes.addAll(parsedPrefixes);
				dataSourcesList.add(new CascDataSourceDescriptor(path, parsedPrefixes));
				break;
			}
			case "":
				continue;
			default:
				throw new RuntimeException("Unknown data source type: " + type);
			}
		}
		final DataSource baseCompoundDataSource = new CompoundDataSourceDescriptor(dataSourcesList).createDataSource();

		final List<DataSource> subdirDataSourcesList = new ArrayList<>();
//		Collections.reverse(allCascPrefixes);
		for (final String prefix : allCascPrefixes) {
			subdirDataSourcesList.add(new SubdirDataSource(baseCompoundDataSource, prefix + "\\"));
		}
		subdirDataSourcesList.add(baseCompoundDataSource);
		return new CompoundDataSource(subdirDataSourcesList);
	}

	private void updateUIScene() {
		this.tempRect.x = this.uiViewport.getScreenX();
		this.tempRect.y = this.uiViewport.getScreenY();
		this.tempRect.width = this.uiViewport.getScreenWidth();
		this.tempRect.height = this.uiViewport.getScreenHeight();
		this.uiScene.camera.viewport(this.tempRect);
		final float worldWidth = this.uiViewport.getWorldWidth();
		final float worldHeight = this.uiViewport.getWorldHeight();
		final float xScale = worldWidth / this.uiViewport.getMinWorldWidth();
		final float yScale = worldHeight / this.uiViewport.getMinWorldHeight();
		final float uiSceneWidth = 0.8f * xScale;
		final float uiSceneHeight = 0.6f * yScale;
		final float uiSceneX = ((0.8f - uiSceneWidth) / 2);
		final float uiSceneY = ((0.6f - uiSceneHeight) / 2);
		this.uiScene.camera.ortho(uiSceneX, uiSceneWidth + uiSceneX, uiSceneY, uiSceneHeight + uiSceneY, -1024f, 1024);
	}

	@Override
	public void render(final float delta) {
		this.uiCamera.update();
		Gdx.gl30.glEnable(GL30.GL_SCISSOR_TEST);
		final float deltaTime = Gdx.graphics.getDeltaTime();
		Gdx.gl30.glBindVertexArray(WarsmashGdxGame.VAO);
		this.meleeUI.update(deltaTime);
		this.viewer.updateAndRender();

		Gdx.gl30.glDisable(GL30.GL_SCISSOR_TEST);

		Gdx.gl30.glDisable(GL30.GL_CULL_FACE);

		this.viewer.webGL.useShaderProgram(null);

		Gdx.gl30.glActiveTexture(GL30.GL_TEXTURE0);
	}

	private void renderLibGDXContent() {

		Gdx.gl30.glClear(GL30.GL_DEPTH_BUFFER_BIT);
		Gdx.gl30.glDisable(GL30.GL_SCISSOR_TEST);

		Gdx.gl30.glDisable(GL30.GL_CULL_FACE);

		this.viewer.webGL.useShaderProgram(null);

		Gdx.gl30.glActiveTexture(GL30.GL_TEXTURE0);

		this.uiViewport.apply();
		this.batch.setColor(1, 1, 1, 1);
		this.batch.setProjectionMatrix(this.uiCamera.combined);
		this.batch.begin();
		this.meleeUI.render(this.batch, this.glyphLayout);
		this.batch.end();

		Gdx.gl30.glEnable(GL30.GL_SCISSOR_TEST);
		Gdx.gl30.glBindVertexArray(WarsmashGdxGame.VAO);
	}

	@Override
	public void dispose() {
		this.meleeUI.dispose();
		this.batch.dispose();
		this.viewer.getGameUI().dispose();
	}

	@Override
	public void resize(final int width, final int height) {
//		super.resize(width, height);

		this.uiViewport.update(width, height);
		this.uiCamera.position.set(this.uiViewport.getMinWorldWidth() / 2, this.uiViewport.getMinWorldHeight() / 2, 0);

		this.meleeUI.resize(width, height);
		updateUIScene();

	}

	@Override
	public boolean keyDown(final int keycode) {
		if ((keycode == Input.Keys.B) && Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
			Gdx.input.setCursorCatched(!Gdx.input.isCursorCatched());
		}
		if (keycode == Input.Keys.ENTER) {
			if (Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.ALT_RIGHT)) {
				WarsmashUtils.toggleFullScreen();
				return true;
			}
		}
		this.meleeUI.keyDown(keycode);
		return true;
	}

	@Override
	public boolean keyUp(final int keycode) {
		this.meleeUI.keyUp(keycode);
		return true;
	}

	@Override
	public boolean keyTyped(final char character) {
		return false;
	}

	@Override
	public boolean touchDown(final int screenX, final int screenY, final int pointer, final int button) {
		final float worldScreenY = this.viewer.canvas.getHeight() - screenY;

		if (this.meleeUI.touchDown(screenX, screenY, worldScreenY, button)) {
			return false;
		}
		return false;
	}

	@Override
	public boolean touchUp(final int screenX, final int screenY, final int pointer, final int button) {
		final float worldScreenY = this.viewer.canvas.getHeight() - screenY;

		if (this.meleeUI.touchUp(screenX, screenY, worldScreenY, button)) {
			return false;
		}
		return false;
	}

	@Override
	public boolean touchDragged(final int screenX, final int screenY, final int pointer) {
		final float worldScreenY = this.viewer.canvas.getHeight() - screenY;
		if (this.meleeUI.touchDragged(screenX, screenY, worldScreenY, pointer)) {
			return false;
		}
		return false;
	}

	@Override
	public boolean mouseMoved(final int screenX, final int screenY) {
		final float worldScreenY = this.viewer.canvas.getHeight() - screenY;
		if (this.meleeUI.mouseMoved(screenX, screenY, worldScreenY)) {
			return false;
		}
		return false;
	}

	@Override
	public boolean scrolled(final float amountX, final float amountY) {
		this.meleeUI.scrolled(amountX, amountY);
		return false;
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

	private class LibGDXContentLayerModelInstance extends ModelInstance {

		public LibGDXContentLayerModelInstance(final Model model) {
			super(model);
		}

		@Override
		public void updateAnimations(final float dt) {

		}

		@Override
		public void clearEmittedObjects() {

		}

		@Override
		protected void updateLights(final Scene scene2) {

		}

		@Override
		public void renderOpaque(final Matrix4 mvp) {

		}

		@Override
		public void renderTranslucent() {
			renderLibGDXContent();
		}

		@Override
		public void load() {
		}

		@Override
		protected RenderBatch getBatch(final TextureMapper textureMapper2) {
			throw new UnsupportedOperationException("NOT API");
		}

		@Override
		public void setReplaceableTexture(final int replaceableTextureId, final String replaceableTextureFile) {

		}

		@Override
		public boolean isBatched() {
			return super.isBatched();
		}

		@Override
		protected void removeLights(final Scene scene2) {
			// TODO Auto-generated method stub

		}

		@Override
		public void setReplaceableTextureHD(final int replaceableTextureId, final String replaceableTextureFile) {
			// TODO Auto-generated method stub

		}

	}

	private class LibGDXContentLayerModel extends Model {

		public LibGDXContentLayerModel(final ModelHandler handler, final ModelViewer viewer, final String extension,
				final PathSolver pathSolver, final String fetchUrl) {
			super(handler, viewer, extension, pathSolver, fetchUrl);
			this.ok = true;
		}

		@Override
		protected ModelInstance createInstance(final int type) {
			return new LibGDXContentLayerModelInstance(this);
		}

		@Override
		protected void lateLoad() {
		}

		@Override
		protected void load(final InputStream src, final Object options) {
		}

		@Override
		protected void error(final Exception e) {
		}

	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void hide() {
		this.meleeUI.gameClosed();
	}
}
