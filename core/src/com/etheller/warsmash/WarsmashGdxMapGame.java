package com.etheller.warsmash;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.Arrays;
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
import com.etheller.warsmash.util.DataSourceFileHandle;
import com.etheller.warsmash.util.ImageUtils;
import com.etheller.warsmash.viewer5.Camera;
import com.etheller.warsmash.viewer5.CanvasProvider;
import com.etheller.warsmash.viewer5.Scene;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxComplexInstance;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxModel;
import com.etheller.warsmash.viewer5.handlers.tga.TgaFile;
import com.etheller.warsmash.viewer5.handlers.w3x.StandSequence;
import com.etheller.warsmash.viewer5.handlers.w3x.Unit;
import com.etheller.warsmash.viewer5.handlers.w3x.War3MapViewer;

public class WarsmashGdxMapGame extends ApplicationAdapter implements CanvasProvider, InputProcessor {
	private static final Vector3 clickLocationTemp = new Vector3();
	private DataSource codebase;
	private War3MapViewer viewer;
	private CameraManager cameraManager;
	private final Rectangle tempRect = new Rectangle();

	private CameraManager portraitCameraManager;
	private MdxComplexInstance portraitInstance;
	private final float[] cameraPositionTemp = new float[3];
	private final float[] cameraTargetTemp = new float[3];

	// libGDX stuff
	private OrthographicCamera uiCamera;
	private BitmapFont font;
	private SpriteBatch batch;
	private Viewport uiViewport;
	private GlyphLayout glyphLayout;

	private Texture consoleUITexture;
	private final Vector2 projectionTemp1 = new Vector2();
	private final Vector2 projectionTemp2 = new Vector2();

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

		final FolderDataSourceDescriptor war3mpq = new FolderDataSourceDescriptor("E:\\Backups\\Warcraft\\Data\\127");
//		final FolderDataSourceDescriptor war3mpq = new FolderDataSourceDescriptor(
//				"D:\\NEEDS_ORGANIZING\\MPQBuild\\War3.mpq\\war3.mpq");
//		final FolderDataSourceDescriptor war3xLocalmpq = new FolderDataSourceDescriptor(
//				"D:\\NEEDS_ORGANIZING\\MPQBuild\\War3xLocal.mpq\\enus-war3local.mpq");
		final FolderDataSourceDescriptor testingFolder = new FolderDataSourceDescriptor(
				"D:\\NEEDS_ORGANIZING\\MPQBuild\\Test");
		final FolderDataSourceDescriptor currentFolder = new FolderDataSourceDescriptor(".");
		this.codebase = new CompoundDataSourceDescriptor(
				Arrays.<DataSourceDescriptor>asList(war3mpq, /* war3xLocalmpq, */ testingFolder, currentFolder))
						.createDataSource();
		this.viewer = new War3MapViewer(this.codebase, this);

		try {
			this.viewer.loadMap("Farm.w3x");
		}
		catch (final IOException e) {
			throw new RuntimeException(e);
		}
		this.viewer.worldScene.enableAudio();
		this.viewer.enableAudio();

		this.cameraManager = new CameraManager();
		this.cameraManager.setupCamera(this.viewer.worldScene);

		System.out.println("Loaded");
		Gdx.gl30.glClearColor(0.0f, 0.0f, 0.0f, 1); // TODO remove white background
		Gdx.gl30.glEnable(GL30.GL_SCISSOR_TEST);

		this.portraitScene = this.viewer.addScene();
		this.portraitCameraManager = new CameraManager();
		this.portraitCameraManager.setupCamera(this.portraitScene);

//		this.mainModel = (MdxModel) this.viewer.load("UI\\Glues\\MainMenu\\MainMenu3D_exp\\MainMenu3D_exp.mdx",

		this.portraitScene.camera.viewport(new Rectangle(100, 0, 6400, 48));

		// libGDX stuff
		final float w = Gdx.graphics.getWidth();
		final float h = Gdx.graphics.getHeight();

		final FreeTypeFontGenerator fontGenerator = new FreeTypeFontGenerator(
				new DataSourceFileHandle(this.viewer.dataSource, "fonts\\FRIZQT__.TTF"));
		final FreeTypeFontParameter fontParam = new FreeTypeFontParameter();
		fontParam.size = 32;
		this.font = fontGenerator.generateFont(fontParam);
		fontGenerator.dispose();
		this.glyphLayout = new GlyphLayout();

		// Constructs a new OrthographicCamera, using the given viewport width and
		// height
		// Height is multiplied by aspect ratio.
		this.uiCamera = new OrthographicCamera();
		this.uiViewport = new FitViewport(1600, 1200, this.uiCamera);
		this.uiViewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		this.uiCamera.position.set(this.uiCamera.viewportWidth / 2f, this.uiCamera.viewportHeight / 2f, 0);
		this.uiCamera.update();

		positionPortrait();

		this.batch = new SpriteBatch();

		this.consoleUITexture = new Texture(new DataSourceFileHandle(this.viewer.dataSource, "AlphaUi.png"));
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

		Gdx.input.setInputProcessor(this);

//		final Music music = Gdx.audio.newMusic(new DataSourceFileHandle(this.viewer.dataSource, "undead_dance.mp3"));
//		music.setVolume(0.7f);
//		music.setLooping(true);
//		music.play();
	}

	@Override
	public void render() {
		this.uiCamera.update();
		Gdx.gl30.glEnable(GL30.GL_SCISSOR_TEST);
		final float deltaTime = Gdx.graphics.getDeltaTime();
		Gdx.gl30.glBindVertexArray(WarsmashGdxGame.VAO);
		this.cameraManager.target.add(this.cameraVelocity.x * deltaTime, this.cameraVelocity.y * deltaTime, 0);
		this.cameraManager.target.z = this.viewer.terrain.getGroundHeight(this.cameraManager.target.x,
				this.cameraManager.target.y);
		this.cameraManager.updateCamera();
		this.portraitCameraManager.updateCamera();
		this.viewer.updateAndRender();

//		gl.glDrawElements(GL20.GL_TRIANGLES, this.elements, GL20.GL_UNSIGNED_SHORT, this.faceOffset);

//		this.batch.begin();
//		this.font.draw(this.batch, Integer.toString(Gdx.graphics.getFramesPerSecond()), 0, 0);
//		this.batch.end();

		if ((this.portraitInstance != null)
				&& (this.portraitInstance.sequenceEnded || (this.portraitInstance.sequence == -1))) {
			StandSequence.randomPortraitSequence(this.portraitInstance);
		}

		Gdx.gl30.glDisable(GL30.GL_SCISSOR_TEST);

		Gdx.gl30.glDisable(GL30.GL_CULL_FACE);

		this.viewer.webGL.useShaderProgram(null);

		Gdx.gl30.glActiveTexture(GL30.GL_TEXTURE0);
		this.uiViewport.apply();
		this.batch.setProjectionMatrix(this.uiCamera.combined);
		this.batch.begin();
		this.font.setColor(Color.YELLOW);
		final String fpsString = "FPS: " + Gdx.graphics.getFramesPerSecond();
		this.glyphLayout.setText(this.font, fpsString);
		this.font.draw(this.batch, fpsString, (this.uiViewport.getWorldWidth() - this.glyphLayout.width) / 2, 1100);
		this.batch.draw(this.consoleUITexture, 0, 0, this.uiViewport.getWorldWidth(), 320);
		this.batch.draw(this.minimapTexture, 35, 7, 305, 272);
		this.batch.end();
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
		this.tempRect.y = 0;
		this.tempRect.width = width;
		this.tempRect.height = height;
		this.cameraManager.camera.viewport(this.tempRect);
		final float portraitTestWidth = (100 / 640f) * width;
		final float portraitTestHeight = (100 / 480f) * height;

		this.uiViewport.update(width, height);
		this.uiCamera.position.set(this.uiCamera.viewportWidth / 2, this.uiCamera.viewportHeight / 2, 0);

		positionPortrait();

	}

	private void positionPortrait() {
		this.projectionTemp1.x = 385;
		this.projectionTemp1.y = 0;
		this.projectionTemp2.x = 385 + 180;
		this.projectionTemp2.y = 177;
		this.uiViewport.project(this.projectionTemp1);
		this.uiViewport.project(this.projectionTemp2);

		this.tempRect.x = this.projectionTemp1.x;
		this.tempRect.y = this.projectionTemp1.y;
		this.tempRect.width = this.projectionTemp2.x - this.projectionTemp1.x;
		this.tempRect.height = this.projectionTemp2.y - this.projectionTemp1.y;
		this.portraitScene.camera.viewport(this.tempRect);
	}

	class CameraManager {
		public com.etheller.warsmash.viewer5.handlers.mdx.Camera modelCamera;
		private CanvasProvider canvas;
		private Camera camera;
		private float moveSpeed;
		private float rotationSpeed;
		private float zoomFactor;
		private float horizontalAngle;
		private float verticalAngle;
		private float distance;
		private Vector3 position;
		private Vector3 target;
		private Vector3 worldUp;
		private Vector3 vecHeap;
		private Quaternion quatHeap;
		private Quaternion quatHeap2;

		// An orbit camera setup example.
		// Left mouse button controls the orbit itself.
		// The right mouse button allows to move the camera and the point it's looking
		// at on the XY plane.
		// Scrolling zooms in and out.
		private void setupCamera(final Scene scene) {
			this.canvas = scene.viewer.canvas;
			this.camera = scene.camera;
			this.moveSpeed = 2;
			this.rotationSpeed = (float) (Math.PI / 180);
			this.zoomFactor = 0.1f;
			this.horizontalAngle = 0;// (float) (Math.PI / 2);
			this.verticalAngle = (float) (Math.PI / 5);
			this.distance = 1600;
			this.position = new Vector3();
			this.target = new Vector3(0, 0, 50);
			this.worldUp = new Vector3(0, 0, 1);
			this.vecHeap = new Vector3();
			this.quatHeap = new Quaternion();
			this.quatHeap2 = new Quaternion();

			updateCamera();

//		cameraUpdate();
		}

		private void updateCamera() {
			// Limit the vertical angle so it doesn't flip.
			// Since the camera uses a quaternion, flips don't matter to it, but this feels
			// better.
			this.verticalAngle = (float) Math.min(Math.max(0.01, this.verticalAngle), Math.PI - 0.01);

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
				this.modelCamera.getPositionTranslation(WarsmashGdxMapGame.this.cameraPositionTemp,
						WarsmashGdxMapGame.this.portraitInstance.sequence,
						WarsmashGdxMapGame.this.portraitInstance.frame,
						WarsmashGdxMapGame.this.portraitInstance.counter);
				this.modelCamera.getTargetTranslation(WarsmashGdxMapGame.this.cameraTargetTemp,
						WarsmashGdxMapGame.this.portraitInstance.sequence,
						WarsmashGdxMapGame.this.portraitInstance.frame,
						WarsmashGdxMapGame.this.portraitInstance.counter);

				this.position.set(this.modelCamera.position);
				this.target.set(this.modelCamera.targetPosition);

				this.position.add(WarsmashGdxMapGame.this.cameraPositionTemp[0],
						WarsmashGdxMapGame.this.cameraPositionTemp[1], WarsmashGdxMapGame.this.cameraPositionTemp[2]);
				this.target.add(WarsmashGdxMapGame.this.cameraTargetTemp[0],
						WarsmashGdxMapGame.this.cameraTargetTemp[1], WarsmashGdxMapGame.this.cameraTargetTemp[2]);
				this.camera.perspective(this.modelCamera.fieldOfView, this.camera.getAspect(),
						this.modelCamera.nearClippingPlane, this.modelCamera.farClippingPlane);
			}

			this.camera.moveToAndFace(this.position, this.target, this.worldUp);
		}

//	private void cameraUpdate() {
//
//	}
	}

	private final float cameraSpeed = 4096.0f; // per second
	private final Vector2 cameraVelocity = new Vector2();
	private Scene portraitScene;
	private Texture minimapTexture;

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
		return true;
	}

	@Override
	public boolean keyTyped(final char character) {
		return false;
	}

	@Override
	public boolean touchDown(final int screenX, final int screenY, final int pointer, final int button) {
		System.out.println(screenX + "," + screenY);
		if (button == Input.Buttons.RIGHT) {
			this.viewer.getClickLocation(clickLocationTemp, screenX, screenY);
			System.out.println(clickLocationTemp);
			this.viewer.showConfirmation(clickLocationTemp, 0, 1, 0);
			final int x = (int) ((clickLocationTemp.x - this.viewer.terrain.centerOffset[0]) / 128);
			final int y = (int) ((clickLocationTemp.y - this.viewer.terrain.centerOffset[1]) / 128);
			System.out.println(x + "," + y);
		}
		else {
			final List<Unit> selectedUnits = this.viewer.selectUnit(screenX, screenY, true);
			if (!selectedUnits.isEmpty()) {
				final Unit unit = selectedUnits.get(0);
				if (unit.soundset != null) {
					unit.soundset.what.play(this.viewer.worldScene.audioContext, unit.location[0], unit.location[1]);
				}
				final MdxModel portraitModel = unit.portraitModel;
				if (portraitModel != null) {
					if (this.portraitInstance != null) {
						this.portraitScene.removeInstance(this.portraitInstance);
					}
					this.portraitInstance = (MdxComplexInstance) portraitModel.addInstance();
					this.portraitInstance.setSequenceLoopMode(1);
					this.portraitInstance.setScene(this.portraitScene);
					if (portraitModel.getCameras().size() > 0) {
						this.portraitCameraManager.modelCamera = portraitModel.getCameras().get(0);
					}
					this.portraitInstance.setTeamColor(unit.playerIndex);
					StandSequence.randomPortraitTalkSequence(this.portraitInstance);

				}
			}
			else {
				if (this.portraitInstance != null) {
					this.portraitScene.removeInstance(this.portraitInstance);
				}
				this.portraitInstance = null;
				this.portraitCameraManager.modelCamera = null;
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
		this.cameraManager.distance += amount * 10.0;
		return true;
	}
}
