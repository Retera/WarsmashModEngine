package com.etheller.warsmash;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.etheller.warsmash.datasources.DataSource;
import com.etheller.warsmash.units.DataTable;
import com.etheller.warsmash.util.DataSourceFileHandle;
import com.etheller.warsmash.viewer5.Camera;
import com.etheller.warsmash.viewer5.CanvasProvider;
import com.etheller.warsmash.viewer5.ModelViewer;
import com.etheller.warsmash.viewer5.PathSolver;
import com.etheller.warsmash.viewer5.Scene;
import com.etheller.warsmash.viewer5.SolvedPath;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxComplexInstance;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxHandler;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxModel;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxViewer;
import com.etheller.warsmash.viewer5.handlers.mdx.Sequence;
import com.etheller.warsmash.viewer5.handlers.mdx.SequenceLoopMode;

public class WarsmashGdxGame extends ApplicationAdapter implements CanvasProvider {
	private static final boolean SPIN = false;
	private static final boolean ADVANCE_ANIMS = true;
	private DataSource codebase;
	private ModelViewer viewer;
	private MdxModel model;
	private CameraManager cameraManager;
	public static int VAO;
	private final Rectangle tempRect = new Rectangle();

	private BitmapFont font;
	private SpriteBatch batch;
	private final DataTable warsmashIni;

	public WarsmashGdxGame(final DataTable warsmashIni) {
		this.warsmashIni = warsmashIni;
	}

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

		this.codebase = WarsmashGdxMapScreen.parseDataSources(this.warsmashIni);
		this.viewer = new MdxViewer(this.codebase, this, new Vector3(0.3f, 0.3f, -0.25f));

		this.viewer.addHandler(new MdxHandler());
		this.viewer.enableAudio();

		final Scene scene = this.viewer.addSimpleScene();
		scene.enableAudio();

		this.cameraManager = new CameraManager();
		this.cameraManager.setupCamera(scene);

//		final String musicPath = "Sound\\Music\\mp3Music\\Mainscreen.mp3";
//		final Music music = Gdx.audio.newMusic(new DataSourceFileHandle(this.viewer.dataSource, musicPath));
////		music.setVolume(0.2f);
//		music.setLooping(true);
//		music.play();

		this.mainModel = (MdxModel) this.viewer.load("Doodads\\Cinematic\\ArthasIllidanFight\\ArthasIllidanFight.mdx",
//		this.mainModel = (MdxModel) this.viewer.load("UI\\Glues\\SinglePlayer\\NightElf_Exp\\NightElf_Exp.mdx",
//		this.mainModel = (MdxModel) this.viewer.load("Abilities\\Spells\\Orc\\FeralSpirit\\feralspirittarget.mdx",
				new PathSolver() {
					@Override
					public SolvedPath solve(final String src, final Object solverParams) {
						return new SolvedPath(src, src.substring(src.lastIndexOf('.')), true);
					}
				}, null);

//		final EventObjectEmitterObject evt = this.mainModel.getEventObjects().get(1);
//		for (final Sequence seq : this.mainModel.getSequences()) {
//			System.out.println(seq.getName() + ": " + Arrays.toString(seq.getInterval()));
//		}
//		System.out.println(Arrays.toString(evt.keyFrames));
//		System.out.println(evt.name);

		this.mainInstance = (MdxComplexInstance) this.mainModel.addInstance(0);

		this.mainInstance.setScene(scene);
//
		final int animIndex = 1;
		this.modelCamera = this.mainModel.cameras.get(animIndex);
		this.mainInstance.setSequence(animIndex);
//
//		this.mainInstance.setSequenceLoopMode(SequenceLoopMode.LOOP_TO_NEXT_ANIMATION);

//		acolytesHarvestingSceneJoke2(scene);

//		singleModelScene(scene, "Buildings\\Undead\\Necropolis\\Necropolis.mdx", "birth");
//		singleModelScene(scene, "Units\\Orc\\KotoBeast\\KotoBeast.mdx", "spell slam");
//		singleModelScene(scene, "Doodads\\Cinematic\\ArthasIllidanFight\\ArthasIllidanFight.mdx", "Stand");
//		this.modelCamera = this.mainModel.cameras.get(0);

		System.out.println("Loaded");
		Gdx.gl30.glClearColor(0.5f, 0.5f, 0.5f, 1); // TODO remove white background

		this.font = new BitmapFont();
		this.batch = new SpriteBatch();
	}

	private void makeDruidSquare(final Scene scene) {
		final MdxModel model2 = (MdxModel) this.viewer.load("units\\nightelf\\druidoftheclaw\\druidoftheclaw.mdx",
				new PathSolver() {
					@Override
					public SolvedPath solve(final String src, final Object solverParams) {
						return new SolvedPath(src, src.substring(src.lastIndexOf('.')), true);
					}
				}, null);
		makePerfectSquare(scene, model2, 15);
	}

	private void singleAcolyteScene(final Scene scene) {
		final MdxModel model2 = (MdxModel) this.viewer.load("units\\undead\\acolyte\\acolyte.mdx", new PathSolver() {
			@Override
			public SolvedPath solve(final String src, final Object solverParams) {
				return new SolvedPath(src, src.substring(src.lastIndexOf('.')), true);
			}
		}, null);

		final MdxComplexInstance instance3 = (MdxComplexInstance) model2.addInstance(0);

		instance3.setScene(scene);

		int animIndex = 0;
		for (final Sequence s : model2.getSequences()) {
			if (s.getName().toLowerCase().startsWith("stand work")) {
				animIndex = model2.getSequences().indexOf(s);
			}
		}
		instance3.setSequence(animIndex);

		instance3.setSequenceLoopMode(SequenceLoopMode.ALWAYS_LOOP);
	}

	private void singleModelScene(final Scene scene, final String path, final String animName) {
		final MdxModel model2 = (MdxModel) this.viewer.load(path, new PathSolver() {
			@Override
			public SolvedPath solve(final String src, final Object solverParams) {
				return new SolvedPath(src, src.substring(src.lastIndexOf('.')), true);
			}
		}, null);

		final MdxComplexInstance instance3 = (MdxComplexInstance) model2.addInstance(0);

		instance3.setScene(scene);

		int animIndex = 0;
		for (final Sequence s : model2.getSequences()) {
			if (s.getName().toLowerCase().startsWith(animName)) {
				animIndex = model2.getSequences().indexOf(s);
				break;
			}
		}
		instance3.setSequence(animIndex);

		instance3.setSequenceLoopMode(SequenceLoopMode.ALWAYS_LOOP);
		this.mainInstance = instance3;
		this.mainModel = model2;
	}

	private void acolytesHarvestingScene(final Scene scene) {

		final MdxModel acolyteModel = (MdxModel) this.viewer.load("units\\undead\\acolyte\\acolyte.mdx",
				new PathSolver() {
					@Override
					public SolvedPath solve(final String src, final Object solverParams) {
						return new SolvedPath(src, src.substring(src.lastIndexOf('.')), true);
					}
				}, null);
		final MdxModel mineEffectModel = (MdxModel) this.viewer
				.load("abilities\\spells\\undead\\undeadmine\\undeadminecircle.mdx", new PathSolver() {
					@Override
					public SolvedPath solve(final String src, final Object solverParams) {
						return new SolvedPath(src, src.substring(src.lastIndexOf('.')), true);
					}
				}, null);
		for (int i = 0; i < 5; i++) {
			final MdxComplexInstance acolyteInstance = (MdxComplexInstance) acolyteModel.addInstance(0);

			acolyteInstance.setScene(scene);

			int animIndex = i % acolyteModel.getSequences().size();
			for (final Sequence s : acolyteModel.getSequences()) {
				if (s.getName().toLowerCase().startsWith("stand work")) {
					animIndex = acolyteModel.getSequences().indexOf(s);
				}
			}
			acolyteInstance.setSequence(animIndex);

			acolyteInstance.setSequenceLoopMode(SequenceLoopMode.ALWAYS_LOOP);

			final double angle = ((Math.PI * 2) / 5) * i;
			acolyteInstance.localLocation.x = (float) Math.cos(angle) * 256;
			acolyteInstance.localLocation.y = (float) Math.sin(angle) * 256;
			acolyteInstance.localRotation.setFromAxisRad(0, 0, 1, (float) (angle + Math.PI));

			final MdxComplexInstance effectInstance = (MdxComplexInstance) mineEffectModel.addInstance(0);

			effectInstance.setScene(scene);

			effectInstance.setSequence(1);

			effectInstance.setSequenceLoopMode(SequenceLoopMode.ALWAYS_LOOP);
			effectInstance.localLocation.x = (float) Math.cos(angle) * 256;
			effectInstance.localLocation.y = (float) Math.sin(angle) * 256;
			effectInstance.localRotation.setFromAxisRad(0, 0, 1, (float) (angle));

		}
		final MdxModel mineModel = (MdxModel) this.viewer.load("buildings\\undead\\hauntedmine\\hauntedmine.mdx",
				new PathSolver() {
					@Override
					public SolvedPath solve(final String src, final Object solverParams) {
						return new SolvedPath(src, src.substring(src.lastIndexOf('.')), true);
					}
				}, null);
		final MdxComplexInstance mineInstance = (MdxComplexInstance) mineModel.addInstance(0);

		mineInstance.setScene(scene);

		mineInstance.setSequence(2);

		mineInstance.setSequenceLoopMode(SequenceLoopMode.ALWAYS_LOOP);
	}

	private void acolytesHarvestingSceneJoke2(final Scene scene) {

		final MdxModel acolyteModel = (MdxModel) this.viewer.load("units\\undead\\acolyte\\acolyte.mdx",
				new PathSolver() {
					@Override
					public SolvedPath solve(final String src, final Object solverParams) {
						return new SolvedPath(src, src.substring(src.lastIndexOf('.')), true);
					}
				}, null);
		final MdxModel mineEffectModel = (MdxModel) this.viewer
				.load("abilities\\spells\\undead\\undeadmine\\undeadminecircle.mdx", new PathSolver() {
					@Override
					public SolvedPath solve(final String src, final Object solverParams) {
						return new SolvedPath(src, src.substring(src.lastIndexOf('.')), true);
					}
				}, null);
		for (int i = 0; i < 5; i++) {
			final MdxComplexInstance acolyteInstance = (MdxComplexInstance) acolyteModel.addInstance(0);

			acolyteInstance.setScene(scene);

			int animIndex = i % acolyteModel.getSequences().size();
			for (final Sequence s : acolyteModel.getSequences()) {
				if (s.getName().toLowerCase().startsWith("stand work")) {
					animIndex = acolyteModel.getSequences().indexOf(s);
				}
			}
			acolyteInstance.setSequence(animIndex);

			acolyteInstance.setSequenceLoopMode(SequenceLoopMode.ALWAYS_LOOP);

			final double angle = ((Math.PI * 2) / 5) * i;
			acolyteInstance.localLocation.x = (float) Math.cos(angle) * 256;
			acolyteInstance.localLocation.y = (float) Math.sin(angle) * 256;
			acolyteInstance.localRotation.setFromAxisRad(0, 0, 1, (float) (angle + Math.PI));

			final MdxComplexInstance effectInstance = (MdxComplexInstance) mineEffectModel.addInstance(0);

			effectInstance.setScene(scene);

			effectInstance.setSequence(1);

			effectInstance.setSequenceLoopMode(SequenceLoopMode.ALWAYS_LOOP);
			effectInstance.localLocation.x = (float) Math.cos(angle) * 256;
			effectInstance.localLocation.y = (float) Math.sin(angle) * 256;
			effectInstance.localRotation.setFromAxisRad(0, 0, 1, (float) (angle));

		}
		final MdxModel mineModel = (MdxModel) this.viewer.load("units\\orc\\spiritwolf\\spiritwolf.mdx",
				new PathSolver() {
					@Override
					public SolvedPath solve(final String src, final Object solverParams) {
						return new SolvedPath(src, src.substring(src.lastIndexOf('.')), true);
					}
				}, null);
		final MdxComplexInstance mineInstance = (MdxComplexInstance) mineModel.addInstance(0);

		mineInstance.setScene(scene);

		mineInstance.setSequence(0);
		mineInstance.localScale.x = 2;
		mineInstance.localScale.y = 2;
		mineInstance.localScale.z = 2;

		mineInstance.setSequenceLoopMode(SequenceLoopMode.ALWAYS_LOOP);
		final MdxModel mineModel2 = (MdxModel) this.viewer
				.load("abilities\\spells\\undead\\unsummon\\unsummontarget.mdx", new PathSolver() {
					@Override
					public SolvedPath solve(final String src, final Object solverParams) {
						return new SolvedPath(src, src.substring(src.lastIndexOf('.')), true);
					}
				}, null);
		final MdxComplexInstance mineInstance2 = (MdxComplexInstance) mineModel2.addInstance(0);

		mineInstance2.setScene(scene);

		mineInstance2.setSequence(0);

		mineInstance2.setSequenceLoopMode(SequenceLoopMode.ALWAYS_LOOP);
	}

	private void makeFourHundred(final Scene scene, final MdxModel model2) {
		for (int i = 0; i < 400; i++) {
			final MdxComplexInstance instance3 = (MdxComplexInstance) model2.addInstance(0);
			instance3.localLocation.x = (((i % 20) - 10) * 128);
			instance3.localLocation.y = (((i / 20) - 10) * 128);

			instance3.setScene(scene);

			final int animIndex = i % model2.getSequences().size();
			instance3.setSequence(animIndex);

			instance3.setSequenceLoopMode(SequenceLoopMode.ALWAYS_LOOP);
		}
	}

	private void makePerfectSquare(final Scene scene, final MdxModel model2, final int n) {
		final int n2 = n * n;
		for (int i = 0; i < n2; i++) {
			final MdxComplexInstance instance3 = (MdxComplexInstance) model2.addInstance(0);
			instance3.localLocation.x = (((i % n) - (n / 2)) * 128);
			instance3.localLocation.y = (((i / n) - (n / 2)) * 128);

			instance3.setScene(scene);

			final int animIndex = i % model2.getSequences().size();
			instance3.setSequence(animIndex);

			instance3.setSequenceLoopMode(SequenceLoopMode.ALWAYS_LOOP);
		}
	}

	public static void bindDefaultVertexArray() {
		Gdx.gl30.glBindVertexArray(VAO);
	}

	private int frame;
	private MdxComplexInstance mainInstance;
	private MdxModel mainModel;
	private com.etheller.warsmash.viewer5.handlers.mdx.Camera modelCamera;
	private final float[] cameraPositionTemp = new float[3];
	private final float[] cameraTargetTemp = new float[3];
	private boolean firstFrame = true;

	@Override
	public void render() {
		Gdx.gl30.glBindVertexArray(VAO);
		if (SPIN) {
			this.cameraManager.horizontalAngle += 0.0001;
			if (this.cameraManager.horizontalAngle > (2 * Math.PI)) {
				this.cameraManager.horizontalAngle = 0;
			}
		}
		this.modelCamera = this.mainModel.cameras.get(this.mainInstance.sequence);
		this.cameraManager.updateCamera();
		this.viewer.updateAndRender();

//		gl.glDrawElements(GL20.GL_TRIANGLES, this.elements, GL20.GL_UNSIGNED_SHORT, this.faceOffset);

//		this.batch.begin();
//		this.font.draw(this.batch, Integer.toString(Gdx.graphics.getFramesPerSecond()), 0, 0);
//		this.batch.end();

		this.frame++;
		if ((this.frame % 1000) == 0) {
			System.out.println(Integer.toString(Gdx.graphics.getFramesPerSecond()));
		}

		if (ADVANCE_ANIMS && this.mainInstance.sequenceEnded) {
			final int sequence = (this.mainInstance.sequence + 1) % this.mainModel.getSequences().size();
			this.mainInstance.setSequence(sequence);
			this.mainInstance.frame += (int) (Gdx.graphics.getRawDeltaTime() * 1000);
		}
		if (this.firstFrame) {
			final Music music = Gdx.audio.newMusic(new DataSourceFileHandle(this.viewer.dataSource,
					"Sound\\Ambient\\DoodadEffects\\FinalCinematic.mp3"));
			music.setVolume(0.2f);
			music.setLooping(true);
			music.play();
			this.firstFrame = false;
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
		private Vector3 vecHeap2;
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
			this.distance = 500;
			this.position = new Vector3();
			this.target = new Vector3(0, 0, 50);
			this.worldUp = new Vector3(0, 0, 1);
			this.vecHeap = new Vector3();
			this.vecHeap2 = new Vector3();
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
			if (WarsmashGdxGame.this.modelCamera != null) {
				WarsmashGdxGame.this.modelCamera.getPositionTranslation(WarsmashGdxGame.this.cameraPositionTemp,
						WarsmashGdxGame.this.mainInstance.sequence, WarsmashGdxGame.this.mainInstance.frame,
						WarsmashGdxGame.this.mainInstance.counter);
				WarsmashGdxGame.this.modelCamera.getTargetTranslation(WarsmashGdxGame.this.cameraTargetTemp,
						WarsmashGdxGame.this.mainInstance.sequence, WarsmashGdxGame.this.mainInstance.frame,
						WarsmashGdxGame.this.mainInstance.counter);

				this.position.set(WarsmashGdxGame.this.modelCamera.position);
				this.target.set(WarsmashGdxGame.this.modelCamera.targetPosition);
//				this.vecHeap2.set(this.target);
//				this.vecHeap2.sub(this.position);
//				this.vecHeap.set(this.vecHeap2);
//				this.vecHeap.crs(this.worldUp);
//				this.vecHeap.crs(this.vecHeap2);
//				this.vecHeap.nor();
//				this.vecHeap.scl(this.camera.rect.height / 2f);
//				this.position.add(this.vecHeap);

				this.position.add(WarsmashGdxGame.this.cameraPositionTemp[0],
						WarsmashGdxGame.this.cameraPositionTemp[1], WarsmashGdxGame.this.cameraPositionTemp[2]);
				this.target.add(WarsmashGdxGame.this.cameraTargetTemp[0], WarsmashGdxGame.this.cameraTargetTemp[1],
						WarsmashGdxGame.this.cameraTargetTemp[2]);
				this.camera.perspective(WarsmashGdxGame.this.modelCamera.fieldOfView * 0.75f,
						Gdx.graphics.getWidth() / (float) Gdx.graphics.getHeight(),
						WarsmashGdxGame.this.modelCamera.nearClippingPlane,
						WarsmashGdxGame.this.modelCamera.farClippingPlane);
			}
			else {
				this.camera.perspective(70, this.camera.getAspect(), 100, 5000);
			}

			this.camera.moveToAndFace(this.position, this.target, this.worldUp);
		}

//	private void cameraUpdate() {
//
//	}
	}

	public DataSource getCodebase() {
		return this.codebase;
	}
}
