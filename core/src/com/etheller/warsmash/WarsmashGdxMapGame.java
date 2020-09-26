package com.etheller.warsmash;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.imageio.ImageIO;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
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
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
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
import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.Camera;
import com.etheller.warsmash.viewer5.CanvasProvider;
import com.etheller.warsmash.viewer5.Scene;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxComplexInstance;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxModel;
import com.etheller.warsmash.viewer5.handlers.mdx.ReplaceableIds;
import com.etheller.warsmash.viewer5.handlers.tga.TgaFile;
import com.etheller.warsmash.viewer5.handlers.w3x.UnitSound;
import com.etheller.warsmash.viewer5.handlers.w3x.War3MapViewer;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.RenderUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.ui.MeleeUI;

public class WarsmashGdxMapGame extends ApplicationAdapter implements CanvasProvider, InputProcessor {
	private static final double HORIZONTAL_ANGLE_INCREMENT = Math.PI / 60;

	private static final Vector3 clickLocationTemp = new Vector3();
	private static final Vector2 clickLocationTemp2 = new Vector2();
	private DataSource codebase;
	private War3MapViewer viewer;
	private GameCameraManager cameraManager;
	private final Rectangle tempRect = new Rectangle();

	// libGDX stuff
	private OrthographicCamera uiCamera;
	private BitmapFont font;
	private BitmapFont font20;
	private SpriteBatch batch;
	private ExtendViewport uiViewport;
	private GlyphLayout glyphLayout;

	private Texture consoleUITexture;
	private int selectedSoundCount = 0;

	private Rectangle minimap;
	private Rectangle minimapFilledArea;

	private final Texture[] teamColors = new Texture[WarsmashConstants.MAX_PLAYERS];
	private Texture solidGreenTexture;

	private ShapeRenderer shapeRenderer;
	private boolean showTalentTree;

	private final List<Message> messages = new LinkedList<>();
	private MdxModel timeIndicator;
	private final DataTable warsmashIni;

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
		this.cameraManager = new GameCameraManager(cameraPresets);

		this.cameraManager.setupCamera(this.viewer.worldScene);

		System.out.println("Loaded");
		Gdx.gl30.glClearColor(0.0f, 0.0f, 0.0f, 1); // TODO remove white background
		Gdx.gl30.glEnable(GL30.GL_SCISSOR_TEST);

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
		if (this.viewer.dataSource.has("war3mapMap.tga")) {
			try {
				this.minimapTexture = ImageUtils.getTextureNoColorCorrection(TgaFile.readTGA("war3mapMap.tga",
						this.viewer.dataSource.getResourceAsStream("war3mapMap.tga")));
			}
			catch (final IOException e) {
				System.err.println("Could not load minimap TGA file");
				e.printStackTrace();
			}
		}
		else if (this.viewer.dataSource.has("war3mapMap.blp")) {
			try {
				this.minimapTexture = ImageUtils
						.getTexture(ImageIO.read(this.viewer.dataSource.getResourceAsStream("war3mapMap.blp")));
			}
			catch (final IOException e) {
				System.err.println("Could not load minimap BLP file");
				e.printStackTrace();
			}
		}

		for (int i = 0; i < this.teamColors.length; i++) {
			this.teamColors[i] = ImageUtils.getBLPTexture(this.viewer.dataSource,
					"ReplaceableTextures\\" + ReplaceableIds.getPathString(1) + ReplaceableIds.getIdString(i) + ".blp");
		}

		this.solidGreenTexture = ImageUtils.getBLPTexture(this.viewer.dataSource,
				"ReplaceableTextures\\TeamColor\\TeamColor06.blp");

		Gdx.input.setInputProcessor(this);

//		final Music music = Gdx.audio
//				.newMusic(new DataSourceFileHandle(this.viewer.dataSource, "Sound\\Music\\mp3Music\\Undead2.mp3"));
//		music.setVolume(0.2f);
//		music.setLooping(true);
//		music.play();

		this.minimap = new Rectangle(18.75f, 13.75f, 278.75f, 276.25f);
		final float worldWidth = (this.viewer.terrain.columns - 1);
		final float worldHeight = this.viewer.terrain.rows - 1;
		final float worldSize = Math.max(worldWidth, worldHeight);
		final float minimapFilledWidth = (worldWidth / worldSize) * this.minimap.width;
		final float minimapFilledHeight = (worldHeight / worldSize) * this.minimap.height;

		this.minimapFilledArea = new Rectangle(this.minimap.x + ((this.minimap.width - minimapFilledWidth) / 2),
				this.minimap.y + ((this.minimap.height - minimapFilledHeight) / 2), minimapFilledWidth,
				minimapFilledHeight);

		if (this.viewer.startLocations[0] != null) {
			this.cameraManager.target.x = this.viewer.startLocations[0].x;
			this.cameraManager.target.y = this.viewer.startLocations[0].y;
		}

		this.shapeRenderer = new ShapeRenderer();
		this.talentTreeWindow = new Rectangle(100, 300, 1400, 800);

//		Jass2.loadJUI(this.codebase, this.uiViewport, fontGenerator, this.uiScene, this.viewer,
//				new RootFrameListener() {
//					@Override
//					public void onCreate(final GameUI rootFrame) {
//						WarsmashGdxMapGame.this.gameUI = rootFrame;
//					}
//				}, "Scripts\\common.jui", "Scripts\\melee.jui");

		this.meleeUI = new MeleeUI(this.codebase, this.uiViewport, fontGenerator, this.uiScene, this.viewer,
				new RootFrameListener() {
					@Override
					public void onCreate(final GameUI rootFrame) {
					}
				});
		this.meleeUI.main();
		fontGenerator.dispose();

		updateUIScene();

		this.meleeUI.resize();
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
		this.cameraManager.target.add(this.cameraVelocity.x * deltaTime, this.cameraVelocity.y * deltaTime, 0);
		this.cameraManager.target.z = (Math.max(
				this.viewer.terrain.getGroundHeight(this.cameraManager.target.x, this.cameraManager.target.y),
				this.viewer.terrain.getWaterHeight(this.cameraManager.target.x, this.cameraManager.target.y)) - 256)
				+ this.cameraManager.presets[this.cameraManager.currentPreset].height + 256;
		this.cameraManager.updateCamera();
		this.meleeUI.updatePortrait();
		this.viewer.updateAndRender();

//		gl.glDrawElements(GL20.GL_TRIANGLES, this.elements, GL20.GL_UNSIGNED_SHORT, this.faceOffset);

//		this.batch.begin();
//		this.font.draw(this.batch, Integer.toString(Gdx.graphics.getFramesPerSecond()), 0, 0);
//		this.batch.end();

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

		this.batch.draw(this.minimapTexture, this.minimap.x, this.minimap.y, this.minimap.width, this.minimap.height);

		final Rectangle playableMapArea = this.viewer.terrain.getPlayableMapArea();
		for (final RenderUnit unit : this.viewer.units) {
			if (unit.playerIndex >= WarsmashConstants.MAX_PLAYERS) {
				System.err.println(unit.row.getName() + " at ( " + unit.location[0] + ", " + unit.location[1] + " )"
						+ " with " + unit.playerIndex);
				unit.playerIndex -= 12;
			}
			final Texture minimapIcon = this.teamColors[unit.playerIndex];
			this.batch.draw(minimapIcon,
					this.minimapFilledArea.x
							+ (((unit.location[0] - playableMapArea.getX()) / (playableMapArea.getWidth()))
									* this.minimapFilledArea.width),
					this.minimapFilledArea.y
							+ (((unit.location[1] - playableMapArea.getY()) / (playableMapArea.getHeight()))
									* this.minimapFilledArea.height),
					4, 4);
		}
		this.batch.end();

		if (this.showTalentTree) {
			this.shapeRenderer.setProjectionMatrix(this.uiCamera.combined);

			this.shapeRenderer.setColor(Color.BLACK);
			this.shapeRenderer.begin(ShapeType.Filled);
			this.shapeRenderer.rect(this.talentTreeWindow.x, this.talentTreeWindow.y, this.talentTreeWindow.width,
					this.talentTreeWindow.height);
			this.shapeRenderer.end();

			this.shapeRenderer.setColor(Color.YELLOW);
			this.shapeRenderer.begin(ShapeType.Line);
			this.shapeRenderer.rect(100, 300, 1400, 800);
			this.shapeRenderer.end();

			this.batch.begin();

			this.font.setColor(Color.YELLOW);
			final String title = "Mage Talent Tree";
			this.glyphLayout.setText(this.font, title);
			this.font.draw(this.batch, title,
					this.talentTreeWindow.x + ((this.talentTreeWindow.width - this.glyphLayout.width) / 2),
					(this.talentTreeWindow.y + this.talentTreeWindow.height) - 45);

			this.batch.end();
		}

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
		this.tempRect.x = 0;
		this.tempRect.width = width;
		final float topHeight = 0.02666f * height;
		final float bottomHeight = 0.21333f * height;
		this.tempRect.y = (int) bottomHeight;
		this.tempRect.height = height - (int) (topHeight + bottomHeight);
		this.cameraManager.camera.viewport(this.tempRect);
		final float portraitTestWidth = (100 / 640f) * width;
		final float portraitTestHeight = (100 / 480f) * height;

		this.uiViewport.update(width, height);
		this.uiCamera.position.set(this.uiViewport.getMinWorldWidth() / 2, this.uiViewport.getMinWorldHeight() / 2, 0);

		updateUIScene();

		this.meleeUI.resize();
	}

	public static abstract class CameraManager {
		protected final float[] cameraPositionTemp = new float[3];
		protected final float[] cameraTargetTemp = new float[3];
		protected CanvasProvider canvas;
		protected Camera camera;
		protected float moveSpeed;
		protected float rotationSpeed;
		protected float zoomFactor;
		protected float horizontalAngle;
		protected float verticalAngle;
		protected float distance;
		protected Vector3 position;
		protected Vector3 target;
		protected Vector3 worldUp;
		protected Vector3 vecHeap;
		protected Quaternion quatHeap;
		protected Quaternion quatHeap2;

		public CameraManager() {
		}

		// An orbit camera setup example.
		// Left mouse button controls the orbit itself.
		// The right mouse button allows to move the camera and the point it's looking
		// at on the XY plane.
		// Scrolling zooms in and out.
		public void setupCamera(final Scene scene) {
			this.canvas = scene.viewer.canvas;
			this.camera = scene.camera;
			this.moveSpeed = 2;
			this.rotationSpeed = (float) HORIZONTAL_ANGLE_INCREMENT;
			this.zoomFactor = 0.1f;
			this.horizontalAngle = 0;// (float) (Math.PI / 2);
			this.verticalAngle = (float) Math.toRadians(34);
			this.distance = 1650;
			this.position = new Vector3();
			this.target = new Vector3(0, 0, 0);
			this.worldUp = new Vector3(0, 0, 1);
			this.vecHeap = new Vector3();
			this.quatHeap = new Quaternion();
			this.quatHeap2 = new Quaternion();

			updateCamera();

//		cameraUpdate();
		}

		public abstract void updateCamera();

//	private void cameraUpdate() {
//
//	}
	}

	public static final class GameCameraManager extends CameraManager {
		private final CameraPreset[] presets;
		private int currentPreset = 0;

		protected boolean insertDown;
		protected boolean deleteDown;

		public GameCameraManager(final CameraPreset[] presets) {
			this.presets = presets;
		}

		@Override
		public void updateCamera() {
			this.quatHeap2.idt();
			final CameraPreset cameraPreset = this.presets[this.currentPreset];
			this.quatHeap.idt();
			this.horizontalAngle = (float) Math
					.toRadians(cameraPreset.getRotation(this.insertDown, this.deleteDown) - 90);
			this.quatHeap.setFromAxisRad(0, 0, 1, this.horizontalAngle);
			this.distance = Math.max(1200, cameraPreset.distance);
			this.verticalAngle = (float) Math.toRadians(Math.min(335, cameraPreset.aoa) - 270);
			this.quatHeap2.setFromAxisRad(1, 0, 0, this.verticalAngle);
			this.quatHeap.mul(this.quatHeap2);

			this.position.set(0, 0, 1);
			this.quatHeap.transform(this.position);
			this.position.nor();
			this.position.scl(this.distance);
			this.position = this.position.add(this.target);
			this.camera.perspective((float) Math.toRadians(cameraPreset.fov / 2), this.camera.getAspect(),
					cameraPreset.nearZ, cameraPreset.farZ);

			this.camera.moveToAndFace(this.position, this.target, this.worldUp);
		}
	}

	public static final class PortraitCameraManager extends CameraManager {
		public com.etheller.warsmash.viewer5.handlers.mdx.Camera modelCamera;
		protected MdxComplexInstance modelInstance;

		@Override
		public void updateCamera() {
			this.quatHeap.idt();
			this.quatHeap.setFromAxisRad(0, 0, 1, this.horizontalAngle);
			this.quatHeap2.idt();
			this.quatHeap2.setFromAxisRad(1, 0, 0, this.verticalAngle);
			this.quatHeap.mul(this.quatHeap2);

			this.position.set(0, 0, 1);
			this.quatHeap.transform(this.position);
			this.position.scl(this.distance);
			this.position = this.position.add(this.target);
			if (this.modelCamera != null) {
				this.modelCamera.getPositionTranslation(this.cameraPositionTemp, this.modelInstance.sequence,
						this.modelInstance.frame, this.modelInstance.counter);
				this.modelCamera.getTargetTranslation(this.cameraTargetTemp, this.modelInstance.sequence,
						this.modelInstance.frame, this.modelInstance.counter);

				this.position.set(this.modelCamera.position);
				this.target.set(this.modelCamera.targetPosition);

				this.position.add(this.cameraPositionTemp[0], this.cameraPositionTemp[1], this.cameraPositionTemp[2]);
				this.target.add(this.cameraTargetTemp[0], this.cameraTargetTemp[1], this.cameraTargetTemp[2]);
				this.camera.perspective(this.modelCamera.fieldOfView * 0.75f, this.camera.getAspect(),
						this.modelCamera.nearClippingPlane, this.modelCamera.farClippingPlane);
			}
			else {
				this.camera.perspective(70, this.camera.getAspect(), 100, 5000);
			}

			this.camera.moveToAndFace(this.position, this.target, this.worldUp);
		}

		public void setModelInstance(final MdxComplexInstance modelInstance, final MdxModel portraitModel) {
			this.modelInstance = modelInstance;
			if (modelInstance == null) {
				this.modelCamera = null;
			}
			else if ((portraitModel != null) && (portraitModel.getCameras().size() > 0)) {
				this.modelCamera = portraitModel.getCameras().get(0);
			}
		}
	}

	private final float cameraSpeed = 4096.0f; // per second
	private final Vector2 cameraVelocity = new Vector2();
	private Texture minimapTexture;
	private Rectangle talentTreeWindow;
	private Scene uiScene;
	private MeleeUI meleeUI;

	@Override
	public boolean keyDown(final int keycode) {
		if (keycode == Input.Keys.LEFT) {
			this.cameraVelocity.x = -this.cameraSpeed;
		}
		else if (keycode == Input.Keys.RIGHT) {
			this.cameraVelocity.x = this.cameraSpeed;
		}
		else if (keycode == Input.Keys.DOWN) {
			this.cameraVelocity.y = -this.cameraSpeed;
		}
		else if (keycode == Input.Keys.UP) {
			this.cameraVelocity.y = this.cameraSpeed;
		}
		else if (keycode == Input.Keys.INSERT) {
			this.cameraManager.insertDown = true;
		}
		else if (keycode == Input.Keys.FORWARD_DEL) {
			this.cameraManager.deleteDown = true;
		}
		return true;
	}

	@Override
	public boolean keyUp(final int keycode) {
		if (keycode == Input.Keys.LEFT) {
			this.cameraVelocity.x = 0;
		}
		else if (keycode == Input.Keys.RIGHT) {
			this.cameraVelocity.x = 0;
		}
		else if (keycode == Input.Keys.DOWN) {
			this.cameraVelocity.y = 0;
		}
		else if (keycode == Input.Keys.UP) {
			this.cameraVelocity.y = 0;
		}
		else if (keycode == Input.Keys.INSERT) {
			this.cameraManager.insertDown = false;
		}
		else if (keycode == Input.Keys.FORWARD_DEL) {
			this.cameraManager.deleteDown = false;
		}
		return true;
	}

	@Override
	public boolean keyTyped(final char character) {
		return false;
	}

	@Override
	public boolean touchDown(final int screenX, final int screenY, final int pointer, final int button) {
		final float worldScreenY = getHeight() - screenY;
		System.out.println(screenX + "," + screenY);

		clickLocationTemp2.x = screenX;
		clickLocationTemp2.y = screenY;
		this.uiViewport.unproject(clickLocationTemp2);

		if (this.minimapFilledArea.contains(clickLocationTemp2.x, clickLocationTemp2.y)) {
			final float clickX = (clickLocationTemp2.x - this.minimapFilledArea.x) / this.minimapFilledArea.width;
			final float clickY = (clickLocationTemp2.y - this.minimapFilledArea.y) / this.minimapFilledArea.height;
			this.cameraManager.target.x = (clickX * this.viewer.terrain.columns * 128)
					+ this.viewer.terrain.centerOffset[0];
			this.cameraManager.target.y = (clickY * this.viewer.terrain.rows * 128)
					+ this.viewer.terrain.centerOffset[1];
			return false;
		}
		if (button == Input.Buttons.RIGHT) {
			final RenderUnit rayPickUnit = this.viewer.rayPickUnit(screenX, worldScreenY);
			if (this.meleeUI.getSelectedUnit() != null) {
				if ((rayPickUnit != null) && (rayPickUnit.playerIndex != this.meleeUI.getSelectedUnit().playerIndex)
						&& !rayPickUnit.getSimulationUnit().isDead()) {
					if (this.viewer.orderSmart(rayPickUnit)) {
						if (this.meleeUI.getSelectedUnit().soundset.yesAttack.playUnitResponse(
								this.viewer.worldScene.audioContext, this.meleeUI.getSelectedUnit())) {
							this.meleeUI.portraitTalk();
						}
						this.selectedSoundCount = 0;
					}
				}
				else {
					this.viewer.getClickLocation(clickLocationTemp, screenX, (int) worldScreenY);
					System.out.println(clickLocationTemp);
					this.viewer.showConfirmation(clickLocationTemp, 0, 1, 0);
					final int x = (int) ((clickLocationTemp.x - this.viewer.terrain.centerOffset[0]) / 128);
					final int y = (int) ((clickLocationTemp.y - this.viewer.terrain.centerOffset[1]) / 128);
					System.out.println(x + "," + y);
					this.viewer.terrain.logRomp(x, y);
					if (this.viewer.orderSmart(clickLocationTemp.x, clickLocationTemp.y)) {
						if (this.meleeUI.getSelectedUnit().soundset.yes.playUnitResponse(
								this.viewer.worldScene.audioContext, this.meleeUI.getSelectedUnit())) {
							this.meleeUI.portraitTalk();
						}
						this.selectedSoundCount = 0;
					}
				}
			}
		}
		else {
			final List<RenderUnit> selectedUnits = this.viewer.selectUnit(screenX, worldScreenY, false);
			if (!selectedUnits.isEmpty()) {
				final RenderUnit unit = selectedUnits.get(0);
				final boolean selectionChanged = this.meleeUI.getSelectedUnit() != unit;
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
					if (ackSoundToPlay.playUnitResponse(this.viewer.worldScene.audioContext, unit, soundIndex)) {
						this.selectedSoundCount++;
						if ((this.selectedSoundCount - 3) >= pissedSoundCount) {
							this.selectedSoundCount = 0;
						}
						playedNewSound = true;
					}
				}
				if (selectionChanged) {
					this.meleeUI.selectUnit(unit);
				}
				if (playedNewSound) {
					this.meleeUI.portraitTalk();
				}
			}
			else {
				this.meleeUI.selectUnit(null);
			}
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
		this.cameraManager.currentPreset -= amount;
		if (this.cameraManager.currentPreset < 0) {
			this.cameraManager.currentPreset = 0;
		}
		if (this.cameraManager.currentPreset >= this.cameraManager.presets.length) {
			this.cameraManager.currentPreset = this.cameraManager.presets.length - 1;
		}
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

	private static class CameraPreset {
		private final float aoa;
		private final float fov;
		private final float rotation;
		private final float rotationInsert;
		private final float rotationDelete;
		private final float distance;
		private final float farZ;
		private final float nearZ;
		private final float height;
		private final float listenerDistance;
		private final float listenerAOA;

		public CameraPreset(final float aoa, final float fov, final float rotation, final float rotationInsert,
				final float rotationDelete, final float distance, final float farZ, final float nearZ,
				final float height, final float listenerDistance, final float listenerAOA) {
			this.aoa = aoa;
			this.fov = fov;
			this.rotation = rotation;
			this.rotationInsert = rotationInsert;
			this.rotationDelete = rotationDelete;
			this.distance = distance;
			this.farZ = farZ;
			this.nearZ = nearZ;
			this.height = height;
			this.listenerDistance = listenerDistance;
			this.listenerAOA = listenerAOA;
		}

		public float getRotation(final boolean insertDown, final boolean deleteDown) {
			if (insertDown && !deleteDown) {
				return this.rotationInsert;
			}
			if (!insertDown && deleteDown) {
				return this.rotationDelete;
			}
			return this.rotation;
		}
	}
}
