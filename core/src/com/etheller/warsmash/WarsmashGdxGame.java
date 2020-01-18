package com.etheller.warsmash;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.Arrays;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.etheller.warsmash.datasources.CompoundDataSource;
import com.etheller.warsmash.datasources.DataSource;
import com.etheller.warsmash.datasources.DataSourceDescriptor;
import com.etheller.warsmash.datasources.FolderDataSourceDescriptor;
import com.etheller.warsmash.viewer5.Camera;
import com.etheller.warsmash.viewer5.CanvasProvider;
import com.etheller.warsmash.viewer5.ModelViewer;
import com.etheller.warsmash.viewer5.PathSolver;
import com.etheller.warsmash.viewer5.Scene;
import com.etheller.warsmash.viewer5.SolvedPath;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxHandler;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxModel;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxSimpleInstance;

public class WarsmashGdxGame extends ApplicationAdapter implements CanvasProvider {
	private DataSource codebase;
	private ModelViewer viewer;
	private MdxModel model;
	private CameraManager cameraManager;
	private static int VAO;

	@Override
	public void create() {

		final ByteBuffer tempByteBuffer = ByteBuffer.allocateDirect(4);
		tempByteBuffer.order(ByteOrder.LITTLE_ENDIAN);
		final IntBuffer temp = tempByteBuffer.asIntBuffer();
//
		Gdx.gl30.glGenVertexArrays(1, temp);
		VAO = temp.get(0);

		Gdx.gl30.glBindVertexArray(VAO);

		final String renderer = Gdx.gl.glGetString(GL20.GL_RENDERER);
		System.err.println("Renderer: " + renderer);

		final FolderDataSourceDescriptor war3mpq = new FolderDataSourceDescriptor(
				"D:\\NEEDS_ORGANIZING\\MPQBuild\\War3.mpq\\war3.mpq");
		final FolderDataSourceDescriptor testingFolder = new FolderDataSourceDescriptor(
				"D:\\NEEDS_ORGANIZING\\MPQBuild\\Test");
		this.codebase = new CompoundDataSource(Arrays.<DataSourceDescriptor>asList(war3mpq, testingFolder));
		this.viewer = new ModelViewer(this.codebase, this);

		this.viewer.addHandler(new MdxHandler());

		final Scene scene = this.viewer.addScene();

		this.cameraManager = new CameraManager();
		this.cameraManager.setupCamera(scene);

		this.model = (MdxModel) this.viewer.load("units\\human\\footman\\footman.mdx", new PathSolver() {
//		this.model = (MdxModel) this.viewer.load("Cube.mdx", new PathSolver() {
			@Override
			public SolvedPath solve(final String src, final Object solverParams) {
				return new SolvedPath(src, src.substring(src.lastIndexOf('.')), true);
			}
		}, null);

		final MdxSimpleInstance instance = (MdxSimpleInstance) this.model.addInstance(1);

		instance.setScene(scene);

//		instance.setSequence(1);
//
//		instance.setSequenceLoopMode(2);

		System.out.println("Loaded");
//		Gdx.gl30.glClearColor(0.5f, 0.5f, 0.5f, 1); // TODO remove white background
	}

	public static void bindDefaultVertexArray() {
		Gdx.gl30.glBindVertexArray(VAO);
	}

	@Override
	public void render() {
//		this.cameraManager.verticalAngle += 0.01;
//		if (this.cameraManager.verticalAngle >= (Math.PI)) {
//			this.cameraManager.verticalAngle = 0;
//		}
		this.cameraManager.horizontalAngle += 0.01;
		if (this.cameraManager.horizontalAngle > (2 * Math.PI)) {
			this.cameraManager.horizontalAngle = 0;
		}
		this.cameraManager.updateCamera();
		this.viewer.updateAndRender();

//		gl.glDrawElements(GL20.GL_TRIANGLES, this.elements, GL20.GL_UNSIGNED_SHORT, this.faceOffset);
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
			this.horizontalAngle = (float) (Math.PI / 2);
			this.verticalAngle = (float) (Math.PI / 4);
			this.distance = 5;
			this.position = new Vector3();
			this.target = new Vector3();
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
}
