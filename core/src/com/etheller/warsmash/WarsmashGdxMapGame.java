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
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
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
import com.etheller.warsmash.viewer5.handlers.w3x.UnitSoundset.UnitAckSound;
import com.etheller.warsmash.viewer5.handlers.w3x.War3MapViewer;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.CommandCardIcon;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.RenderUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.COrder;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityStop;
import com.etheller.warsmash.viewer5.handlers.w3x.ui.MeleeUI;

public class WarsmashGdxMapGame extends ApplicationAdapter implements CanvasProvider, InputProcessor {
	private static final float HORIZONTAL_MAXIMUM = (float) Math.toRadians(56);
	private static final float HORIZONTAL_MINIMUM = (float) -Math.toRadians(56);
	private static final double HORIZONTAL_ANGLE_INCREMENT = Math.PI / 60;

	private static final Vector3 clickLocationTemp = new Vector3();
	private static final Vector2 clickLocationTemp2 = new Vector2();
	private DataSource codebase;
	private War3MapViewer viewer;
	private CameraManager cameraManager;
	private final Rectangle tempRect = new Rectangle();

	// libGDX stuff
	private OrthographicCamera uiCamera;
	private BitmapFont font;
	private BitmapFont font24;
	private BitmapFont font20;
	private SpriteBatch batch;
	private Viewport uiViewport;
	private GlyphLayout glyphLayout;

	private Texture consoleUITexture;
	private RenderUnit selectedUnit;
	private int selectedSoundCount = 0;

	private Texture activeButtonTexture;

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

		this.cameraManager = new CameraManager();

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

		this.tempRect.x = 0;
		this.tempRect.y = 0;
		this.tempRect.width = w;
		this.tempRect.height = h;
		this.uiScene.camera.viewport(this.tempRect);
		this.uiScene.camera.ortho(0, 0.8f, 0, 0.6f, 0, 1);

		final FreeTypeFontGenerator fontGenerator = new FreeTypeFontGenerator(
				new DataSourceFileHandle(this.viewer.dataSource, "fonts\\FRIZQT__.TTF"));
		final FreeTypeFontParameter fontParam = new FreeTypeFontParameter();
		fontParam.size = 32;
		this.font = fontGenerator.generateFont(fontParam);
		fontParam.size = 24;
		this.font24 = fontGenerator.generateFont(fontParam);
		fontParam.size = 20;
		this.font20 = fontGenerator.generateFont(fontParam);
		this.glyphLayout = new GlyphLayout();

		// Constructs a new OrthographicCamera, using the given viewport width and
		// height
		// Height is multiplied by aspect ratio.
		this.uiCamera = new OrthographicCamera();
		this.uiViewport = new FitViewport(1600, 1200, this.uiCamera);
		this.uiViewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		this.uiCamera.position.set(this.uiCamera.viewportWidth / 2f, this.uiCamera.viewportHeight / 2f, 0);
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

		this.activeButtonTexture = ImageUtils.getBLPTexture(this.viewer.dataSource,
				"UI\\Widgets\\Console\\Human\\CommandButton\\human-activebutton.blp");

		for (int i = 0; i < this.teamColors.length; i++) {
			this.teamColors[i] = ImageUtils.getBLPTexture(this.viewer.dataSource,
					"ReplaceableTextures\\" + ReplaceableIds.getPathString(1) + ReplaceableIds.getIdString(i) + ".blp");
		}

		this.solidGreenTexture = ImageUtils.getBLPTexture(this.viewer.dataSource,
				"ReplaceableTextures\\TeamColor\\TeamColor06.blp");

		Gdx.input.setInputProcessor(this);

//		final Music music = Gdx.audio.newMusic(
//				new DataSourceFileHandle(this.viewer.dataSource, "Sound\\Music\\mp3Music\\War2IntroMusic.mp3"));
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
	}

	@Override
	public void render() {
		this.uiCamera.update();
		Gdx.gl30.glEnable(GL30.GL_SCISSOR_TEST);
		final float deltaTime = Gdx.graphics.getDeltaTime();
		Gdx.gl30.glBindVertexArray(WarsmashGdxGame.VAO);
		this.cameraManager.target.add(this.cameraVelocity.x * deltaTime, this.cameraVelocity.y * deltaTime, 0);
		this.cameraManager.target.z = Math.max(
				this.viewer.terrain.getGroundHeight(this.cameraManager.target.x, this.cameraManager.target.y),
				this.viewer.terrain.getWaterHeight(this.cameraManager.target.x, this.cameraManager.target.y)) - 256;
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
		this.font.draw(this.batch, fpsString, (this.uiViewport.getWorldWidth() - this.glyphLayout.width) / 2, 1100);

		this.batch.draw(this.minimapTexture, this.minimap.x, this.minimap.y, this.minimap.width, this.minimap.height);

		if (this.selectedUnit != null) {
			int messageIndex = 0;
			for (final Message message : this.messages) {
				this.font20.draw(this.batch, message.text, 100, 400 + (25 * (messageIndex++)));
			}
			this.font20.setColor(Color.WHITE);

			final COrder currentOrder = this.selectedUnit.getSimulationUnit().getCurrentOrder();
			for (final CommandCardIcon commandCardIcon : this.selectedUnit.getCommandCardIcons()) {
				this.batch.draw(commandCardIcon.getTexture(), 1235 + (86.8f * commandCardIcon.getX()),
						190 - (88 * commandCardIcon.getY()), 78f, 78f);
				if (((currentOrder != null) && (currentOrder.getOrderId() == commandCardIcon.getOrderId()))
						|| ((currentOrder == null) && (commandCardIcon.getOrderId() == CAbilityStop.ORDER_ID))) {
					final int blendDstFunc = this.batch.getBlendDstFunc();
					final int blendSrcFunc = this.batch.getBlendSrcFunc();
					this.batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
					this.batch.draw(this.activeButtonTexture, 1235 + (86.8f * commandCardIcon.getX()),
							190 - (88 * commandCardIcon.getY()), 78f, 78f);
					this.batch.setBlendFunction(blendSrcFunc, blendDstFunc);
				}
			}
		}
		for (final RenderUnit unit : this.viewer.units) {
			if (unit.playerIndex >= WarsmashConstants.MAX_PLAYERS) {
				System.err.println(unit.row.getName() + " at ( " + unit.location[0] + ", " + unit.location[1] + " )"
						+ " with " + unit.playerIndex);
				unit.playerIndex -= 12;
			}
			final Texture minimapIcon = this.teamColors[unit.playerIndex];
			this.batch.draw(minimapIcon,
					this.minimapFilledArea.x + (((unit.location[0] - this.viewer.terrain.centerOffset[0])
							/ ((this.viewer.terrain.columns - 1) * 128f)) * this.minimapFilledArea.width),
					this.minimapFilledArea.y + (((unit.location[1] - this.viewer.terrain.centerOffset[1])
							/ ((this.viewer.terrain.rows - 1) * 128f)) * this.minimapFilledArea.height),
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
		this.uiCamera.position.set(this.uiCamera.viewportWidth / 2, this.uiCamera.viewportHeight / 2, 0);

		this.tempRect.x = this.uiViewport.getScreenX();
		this.tempRect.y = this.uiViewport.getScreenY();
		this.tempRect.width = this.uiViewport.getScreenWidth();
		this.tempRect.height = this.uiViewport.getScreenHeight();
		this.uiScene.camera.viewport(this.tempRect);
		this.uiScene.camera.ortho(0f, 0.8f, 0f, 0.6f, -1f, 1);

		this.meleeUI.resize();
	}

	public static class CameraManager {
		private final float[] cameraPositionTemp = new float[3];
		private final float[] cameraTargetTemp = new float[3];
		public com.etheller.warsmash.viewer5.handlers.mdx.Camera modelCamera;
		private MdxComplexInstance modelInstance;
		private CanvasProvider canvas;
		private Camera camera;
		private float moveSpeed;
		private float rotationSpeed;
		private float zoomFactor;
		private float horizontalAngle;
		private float verticalAngle;
		private float verticalAngleAcceleration;
		private float distance;
		private Vector3 position;
		private Vector3 target;
		private Vector3 worldUp;
		private Vector3 vecHeap;
		private Quaternion quatHeap;
		private Quaternion quatHeap2;
		private boolean insertDown;
		private boolean deleteDown;

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

		public void updateCamera() {
			this.quatHeap.idt();
			this.quatHeap.setFromAxisRad(0, 0, 1, this.horizontalAngle);
			if (this.insertDown && !this.deleteDown) {
				this.horizontalAngle -= HORIZONTAL_ANGLE_INCREMENT;
				if (this.horizontalAngle < HORIZONTAL_MINIMUM) {
					this.horizontalAngle = HORIZONTAL_MINIMUM;
				}
			}
			else if (this.deleteDown && !this.insertDown) {
				this.horizontalAngle += HORIZONTAL_ANGLE_INCREMENT;
				if (this.horizontalAngle > HORIZONTAL_MAXIMUM) {
					this.horizontalAngle = HORIZONTAL_MAXIMUM;
				}
			}
			else {
				if (Math.abs(this.horizontalAngle) < HORIZONTAL_ANGLE_INCREMENT) {
					this.horizontalAngle = 0;
				}
				else {
					this.horizontalAngle -= HORIZONTAL_ANGLE_INCREMENT * Math.signum(this.horizontalAngle);
				}
			}

			this.quatHeap2.idt();
			this.verticalAngle += this.verticalAngleAcceleration;
			this.verticalAngleAcceleration *= 0.975f;
			if (this.verticalAngle > ((Math.PI / 2) - Math.toRadians(17))) {
				this.verticalAngle = (float) ((Math.PI / 2) - Math.toRadians(17));
				if (this.verticalAngleAcceleration > 0) {
					this.verticalAngleAcceleration = 0;
				}
			}
			if (this.verticalAngle < (float) Math.toRadians(34)) {
				this.verticalAngle = (float) Math.toRadians(34);
				if (this.verticalAngleAcceleration < 0) {
					this.verticalAngleAcceleration = 0;
				}
			}
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

//	private void cameraUpdate() {
//
//	}
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

		if (this.selectedUnit != null) {
			for (final CommandCardIcon commandCardIcon : this.selectedUnit.getCommandCardIcons()) {
				if (new Rectangle(1235 + (86.8f * commandCardIcon.getX()), 190 - (88 * commandCardIcon.getY()), 78f,
						78f).contains(clickLocationTemp2)) {
					if (button == Input.Buttons.RIGHT) {
						this.messages.add(new Message(Gdx.input.getCurrentEventTime(), "Right mouse click"));
					}
					else {
						this.messages.add(new Message(Gdx.input.getCurrentEventTime(), "Left mouse click"));
					}
					return true;
				}
			}
		}
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
			if ((rayPickUnit != null) && (this.selectedUnit != null)
					&& (rayPickUnit.playerIndex != this.selectedUnit.playerIndex)) {
				if (this.viewer.orderSmart(rayPickUnit)) {
					this.meleeUI.portraitTalk();
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
					this.meleeUI.portraitTalk();
					this.selectedSoundCount = 0;
				}
			}
		}
		else {
			final List<RenderUnit> selectedUnits = this.viewer.selectUnit(screenX, worldScreenY, false);
			if (!selectedUnits.isEmpty()) {
				final RenderUnit unit = selectedUnits.get(0);
				final boolean selectionChanged = this.selectedUnit != unit;
				boolean playedNewSound = false;
				if (selectionChanged) {
					this.selectedSoundCount = 0;
				}
				this.selectedUnit = unit;
				if (unit.soundset != null) {
					UnitAckSound ackSoundToPlay = unit.soundset.what;
					final int pissedSoundCount = unit.soundset.pissed.getSoundCount();
					int soundIndex;
					if ((this.selectedSoundCount >= 3) && (pissedSoundCount > 0)) {
						soundIndex = this.selectedSoundCount - 3;
						ackSoundToPlay = unit.soundset.pissed;
					}
					else {
						soundIndex = (int) (Math.random() * ackSoundToPlay.getSoundCount());
					}
					if (ackSoundToPlay.play(this.viewer.worldScene.audioContext, unit.location[0], unit.location[1],
							soundIndex)) {
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
				this.selectedUnit = null;
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
		this.cameraManager.verticalAngleAcceleration -= amount / 100.f;
		if (this.cameraManager.verticalAngleAcceleration > (Math.PI / 128)) {
			this.cameraManager.verticalAngleAcceleration = (float) (Math.PI / 128);
		}
		if (this.cameraManager.verticalAngleAcceleration < (-Math.PI / 128)) {
			this.cameraManager.verticalAngleAcceleration = -(float) (Math.PI / 128);
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
}
