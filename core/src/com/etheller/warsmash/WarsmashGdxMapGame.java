package com.etheller.warsmash;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.Arrays;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.etheller.warsmash.datasources.CompoundDataSourceDescriptor;
import com.etheller.warsmash.datasources.DataSource;
import com.etheller.warsmash.datasources.DataSourceDescriptor;
import com.etheller.warsmash.datasources.FolderDataSourceDescriptor;
import com.etheller.warsmash.viewer5.Camera;
import com.etheller.warsmash.viewer5.CanvasProvider;
import com.etheller.warsmash.viewer5.Scene;
import com.etheller.warsmash.viewer5.handlers.w3x.War3MapViewer;

public class WarsmashGdxMapGame extends ApplicationAdapter implements CanvasProvider, InputProcessor {
	private DataSource codebase;
	private War3MapViewer viewer;
	private CameraManager cameraManager;
	private final Rectangle tempRect = new Rectangle();

	private BitmapFont font;
	private SpriteBatch batch;

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

//		final FolderDataSourceDescriptor war3mpq = new FolderDataSourceDescriptor("E:\\Backups\\Warcraft\\Data\\127");
		final FolderDataSourceDescriptor war3mpq = new FolderDataSourceDescriptor(
				"D:\\NEEDS_ORGANIZING\\MPQBuild\\War3.mpq\\war3.mpq");
		final FolderDataSourceDescriptor war3xLocalmpq = new FolderDataSourceDescriptor(
				"D:\\NEEDS_ORGANIZING\\MPQBuild\\War3xLocal.mpq\\enus-war3local.mpq");
		final FolderDataSourceDescriptor testingFolder = new FolderDataSourceDescriptor(
				"D:\\NEEDS_ORGANIZING\\MPQBuild\\Test");
		final FolderDataSourceDescriptor currentFolder = new FolderDataSourceDescriptor(".");
		this.codebase = new CompoundDataSourceDescriptor(
				Arrays.<DataSourceDescriptor>asList(war3mpq, war3xLocalmpq, testingFolder, currentFolder))
						.createDataSource();
		this.viewer = new War3MapViewer(this.codebase, this);

		try {
			this.viewer.loadMap("ReforgedGeorgeVacation.w3x");
		}
		catch (final IOException e) {
			throw new RuntimeException(e);
		}

		this.cameraManager = new CameraManager();
		this.cameraManager.setupCamera(this.viewer.worldScene);

		System.out.println("Loaded");
		Gdx.gl30.glClearColor(0.5f, 0.5f, 0.5f, 1); // TODO remove white background

		this.font = new BitmapFont();
		this.batch = new SpriteBatch();

		Gdx.input.setInputProcessor(this);
	}

	@Override
	public void render() {
		Gdx.gl30.glBindVertexArray(WarsmashGdxGame.VAO);
		this.cameraManager.target.add(this.cameraVelocity.x, this.cameraVelocity.y, 0);
		this.cameraManager.updateCamera();
		this.viewer.updateAndRender();

//		gl.glDrawElements(GL20.GL_TRIANGLES, this.elements, GL20.GL_UNSIGNED_SHORT, this.faceOffset);

//		this.batch.begin();
//		this.font.draw(this.batch, Integer.toString(Gdx.graphics.getFramesPerSecond()), 0, 0);
//		this.batch.end();
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
		this.tempRect.width = width;
		this.tempRect.height = height;
		this.cameraManager.camera.viewport(this.tempRect);
	}

	class CameraManager {
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
			this.verticalAngle = (float) (Math.PI / 4);
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

			this.camera.moveToAndFace(this.position, this.target, this.worldUp);
		}

//	private void cameraUpdate() {
//
//	}
	}

	private final float cameraSpeed = 10.0f;
	private final Vector2 cameraVelocity = new Vector2();

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
