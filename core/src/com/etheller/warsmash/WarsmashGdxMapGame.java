package com.etheller.warsmash;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.etheller.warsmash.datasources.CompoundDataSourceDescriptor;
import com.etheller.warsmash.datasources.DataSource;
import com.etheller.warsmash.datasources.DataSourceDescriptor;
import com.etheller.warsmash.datasources.FolderDataSourceDescriptor;
import com.etheller.warsmash.datasources.MpqDataSourceDescriptor;
import com.etheller.warsmash.parsers.fdf.GameUI;
import com.etheller.warsmash.parsers.jass.Jass2.RootFrameListener;
import com.etheller.warsmash.units.DataTable;
import com.etheller.warsmash.units.Element;
import com.etheller.warsmash.util.DataSourceFileHandle;
import com.etheller.warsmash.util.ImageUtils;
import com.etheller.warsmash.viewer5.CanvasProvider;
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
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayerUnitOrderExecutor;
import com.etheller.warsmash.viewer5.handlers.w3x.ui.MeleeUI;
import com.etheller.warsmash.viewer5.handlers.w3x.ui.command.CommandErrorListener;

public class WarsmashGdxMapGame extends ApplicationAdapter implements CanvasProvider, InputProcessor {
	private static final boolean ENABLE_MUSIC = false;
	private DataSource codebase;
	private War3MapViewer viewer;
	private final Rectangle tempRect = new Rectangle();

	// libGDX stuff
	private OrthographicCamera uiCamera;
	private BitmapFont font;
	private BitmapFont font20;
	private SpriteBatch batch;
	private ExtendViewport uiViewport;
	private GlyphLayout glyphLayout;

	private Texture solidGreenTexture;

	private ShapeRenderer shapeRenderer;

	private MdxModel timeIndicator;
	private final DataTable warsmashIni;

	private Scene uiScene;
	private MeleeUI meleeUI;

	public WarsmashGdxMapGame(final DataTable warsmashIni) {
		this.warsmashIni = warsmashIni;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.badlogic.gdx.ApplicationAdapter#create()
	 */
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

		final Element dataSourcesConfig = this.warsmashIni.get("DataSources");
		final int dataSourcesCount = dataSourcesConfig.getFieldValue("Count");
		final List<DataSourceDescriptor> dataSourcesList = new ArrayList<>();
		for (int i = 0; i < dataSourcesCount; i++) {
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
			default:
				throw new RuntimeException("Unknown data source type: " + type);
			}
		}
		this.codebase = new CompoundDataSourceDescriptor(dataSourcesList).createDataSource();
		this.viewer = new War3MapViewer(this.codebase, this);

		this.viewer.worldScene.enableAudio();
		this.viewer.enableAudio();
		try {
			this.viewer.loadMap(this.warsmashIni.get("Map").getField("FilePath"));
		}
		catch (final IOException e) {
			throw new RuntimeException(e);
		}

		final Element cameraData = this.viewer.miscData.get("Camera");
		final Element cameraListenerData = this.viewer.miscData.get("Listener");
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

//		this.mainModel = (MdxModel) this.viewer.load("UI\\Glues\\MainMenu\\MainMenu3D_exp\\MainMenu3D_exp.mdx",

		// libGDX stuff
		final float w = Gdx.graphics.getWidth();
		final float h = Gdx.graphics.getHeight();

		final FreeTypeFontGenerator fontGenerator = new FreeTypeFontGenerator(
				new DataSourceFileHandle(this.viewer.dataSource, "fonts\\FRIZQT__.TTF"));
		final FreeTypeFontParameter fontParam = new FreeTypeFontParameter();
		fontParam.size = 32;
		this.font = fontGenerator.generateFont(fontParam);
		fontParam.size = 20;
		this.font20 = fontGenerator.generateFont(fontParam);
		this.glyphLayout = new GlyphLayout();

		// Constructs a new OrthographicCamera, using the given viewport width and
		// height
		// Height is multiplied by aspect ratio.
		this.uiCamera = new OrthographicCamera();
		this.uiViewport = new ExtendViewport(1600, 1200, this.uiCamera);
		this.uiViewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		this.uiCamera.position.set(this.uiViewport.getMinWorldWidth() / 2, this.uiViewport.getMinWorldHeight() / 2, 0);
		this.uiCamera.update();

		this.batch = new SpriteBatch();

//		this.consoleUITexture = new Texture(new DataSourceFileHandle(this.viewer.dataSource, "AlphaUi.png"));

		this.solidGreenTexture = ImageUtils.getBLPTexture(this.viewer.dataSource,
				"ReplaceableTextures\\TeamColor\\TeamColor06.blp");

		Gdx.input.setInputProcessor(this);

		this.shapeRenderer = new ShapeRenderer();

//		Jass2.loadJUI(this.codebase, this.uiViewport, fontGenerator, this.uiScene, this.viewer,
//				new RootFrameListener() {
//					@Override
//					public void onCreate(final GameUI rootFrame) {
//						WarsmashGdxMapGame.this.gameUI = rootFrame;
//					}
//				}, "Scripts\\common.jui", "Scripts\\melee.jui");
		final Element cameraRatesElement = this.viewer.miscData.get("CameraRates");
		final CameraRates cameraRates = new CameraRates(cameraRatesElement.getFieldFloatValue("AOA"),
				cameraRatesElement.getFieldFloatValue("FOV"), cameraRatesElement.getFieldFloatValue("Rotation"),
				cameraRatesElement.getFieldFloatValue("Distance"), cameraRatesElement.getFieldFloatValue("Forward"),
				cameraRatesElement.getFieldFloatValue("Strafe"));
		this.meleeUI = new MeleeUI(this.codebase, this.uiViewport, fontGenerator, this.uiScene, portraitScene,
				cameraPresets, cameraRates, this.viewer, new RootFrameListener() {
					@Override
					public void onCreate(final GameUI rootFrame) {
						WarsmashGdxMapGame.this.viewer.setGameUI(rootFrame);

						if (ENABLE_MUSIC) {
							final String musicField = rootFrame.getSkinField("Music_V1");
							final String[] musics = musicField.split(";");
							final String musicPath = musics[(int) (Math.random() * musics.length)];
							final Music music = Gdx.audio.newMusic(
									new DataSourceFileHandle(WarsmashGdxMapGame.this.viewer.dataSource, musicPath));
							music.setVolume(0.2f);
							music.setLooping(true);
							music.play();
						}
					}
				}, new CPlayerUnitOrderExecutor(this.viewer.simulation, new CommandErrorListener() {
					@Override
					public void showCommandError(final String message) {
						WarsmashGdxMapGame.this.meleeUI.showCommandError(message);
					}
				}));
		final ModelInstance libgdxContentInstance = new LibGDXContentLayerModel(null, this.viewer, "",
				this.viewer.mapPathSolver, "").addInstance();
		libgdxContentInstance.setScene(this.uiScene);
		this.meleeUI.main();
		fontGenerator.dispose();

		updateUIScene();

		resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		try {
			this.viewer.loadAfterUI();
		}
		catch (final IOException e) {
			throw new RuntimeException(e);
		}
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
		this.uiScene.camera.ortho(uiSceneX, uiSceneWidth + uiSceneX, uiSceneY, uiSceneHeight + uiSceneY, -1f, 1);
	}

	@Override
	public void render() {
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

		Gdx.gl30.glDisable(GL30.GL_SCISSOR_TEST);

		Gdx.gl30.glDisable(GL30.GL_CULL_FACE);

		this.viewer.webGL.useShaderProgram(null);

		Gdx.gl30.glActiveTexture(GL30.GL_TEXTURE0);

		this.uiViewport.apply();
		this.batch.setProjectionMatrix(this.uiCamera.combined);
		this.batch.begin();
		this.meleeUI.render(this.batch, this.font20, this.glyphLayout);
		this.font.setColor(Color.YELLOW);
		final String fpsString = "FPS: " + Gdx.graphics.getFramesPerSecond();
		this.glyphLayout.setText(this.font, fpsString);
		this.font.draw(this.batch, fpsString, (this.uiViewport.getMinWorldWidth() - this.glyphLayout.width) / 2, 1100);
		this.batch.end();

		Gdx.gl30.glEnable(GL30.GL_SCISSOR_TEST);
		Gdx.gl30.glBindVertexArray(WarsmashGdxGame.VAO);
	}

	@Override
	public void dispose() {
	}

	@Override
	public float getWidth() {
		return Gdx.graphics.getWidth();
	}

	@Override
	public float getHeight() {
		return Gdx.graphics.getHeight();
	}

	@Override
	public void resize(final int width, final int height) {
		super.resize(width, height);

		this.uiViewport.update(width, height);
		this.uiCamera.position.set(this.uiViewport.getMinWorldWidth() / 2, this.uiViewport.getMinWorldHeight() / 2, 0);

		this.meleeUI.resize(setupWorldFrameViewport(width, height));
		updateUIScene();

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

	@Override
	public boolean keyDown(final int keycode) {
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
		final float worldScreenY = getHeight() - screenY;

		if (this.meleeUI.touchDown(screenX, screenY, worldScreenY, button)) {
			return false;
		}
		return false;
	}

	@Override
	public boolean touchUp(final int screenX, final int screenY, final int pointer, final int button) {
		return false;
	}

	@Override
	public boolean touchDragged(final int screenX, final int screenY, final int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(final int screenX, final int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(final int amount) {
		this.meleeUI.scrolled(amount);
		return true;
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
}
