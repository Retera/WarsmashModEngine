package com.etheller.warsmash.viewer5;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.etheller.warsmash.viewer5.gl.Extensions;
import com.etheller.warsmash.viewer5.gl.WebGL;
import com.etheller.warsmash.viewer5.handlers.w3x.DynamicShadowManager;

/**
 * A scene.
 *
 * Every scene has its own list of model instances, and its own camera and
 * viewport.
 *
 * In addition, in Ghostwolf's original code every scene may have its own
 * AudioContext if enableAudio() is called. If audo is enabled, the
 * AudioContext's listener's location will be updated automatically. Note that
 * due to browser policies, this may be done only after user interaction with
 * the web page.
 *
 * In "Warsmash", we are starting from an attempt to replicate Ghostwolf, but
 * audio is always on in LibGDX generally. So we will probably simplify or skip
 * over those behaviors other than a boolean on/off toggle for audio.
 */
public abstract class Scene {

	public final ModelViewer viewer;
	public int visibleCells;
	public int visibleInstances;
	public final Camera camera;
	public int updatedParticles;
	public boolean audioEnabled;
	public AudioContext audioContext;

	public final List<ModelInstance> instances;
	public int currentInstance;
	public final List<ModelInstance> batchedInstances;
	public int currentBatchedInstance;
	public final EmittedObjectUpdater emitterObjectUpdater;
	public final Map<TextureMapper, RenderBatch> batches;
	public final Comparator<ModelInstance> instanceDepthComparator;
	public DynamicShadowManager shadowManager;
	/**
	 * Similar to WebGL's own `alpha` parameter.
	 *
	 * If false, the scene will be cleared before rendering, meaning that scenes
	 * behind it won't be visible through it.
	 *
	 * If true, alpha works as usual.
	 */
	public boolean alpha = false;
	public Color backgroundColor = Color.BLACK;
	private final SceneLightManager lightManager;
	public FogSettings fogSettings = new FogSettings();
	public boolean show = true;

	public Scene(final ModelViewer viewer, final SceneLightManager lightManager) {
		final CanvasProvider canvas = viewer.canvas;
		this.viewer = viewer;
		this.camera = new Camera();

		this.updatedParticles = 0;

		this.audioEnabled = false;
		this.audioContext = null;

		// Use the whole canvas, and standard perspective projection values.
		this.camera.viewport(new Rectangle(0, 0, canvas.getWidth(), canvas.getHeight()));
		this.camera.perspective((float) (Math.PI / 4), canvas.getWidth() / canvas.getHeight(), 8, 10000);

		this.instances = new ArrayList<>();
		this.currentInstance = 0;

		this.batchedInstances = new ArrayList<>();
		this.currentBatchedInstance = 0;

		this.emitterObjectUpdater = new EmittedObjectUpdater();

		this.batches = new HashMap<>();
		this.instanceDepthComparator = new InstanceDepthComparator();
		this.visibleCells = 0;
		this.visibleInstances = 0;

		this.lightManager = lightManager;
	}

	public boolean enableAudio() {
		if (this.audioContext == null) {
			this.audioContext = Extensions.audio.createContext(this instanceof WorldScene);
		}
		if (!this.audioContext.isRunning()) {
			this.audioContext.resume();
		}
		this.audioEnabled = this.audioContext.isRunning();
		return this.audioEnabled;
	}

	public void disableAudio() {
		if (this.audioContext != null) {
			this.audioContext.suspend();
		}
		this.audioEnabled = false;
	}

	public boolean addInstance(final ModelInstance instance) {
		if (instance.scene != this) {
			if (instance.scene != null) {
				instance.scene.removeInstance(instance);
			}

			instance.scene = this;

			// Only allow instances that are actually ok to be added the scene.
			if (instance.model.ok) {
				// predict x and y of model
				float x, y;
				if (instance.dirty) {
					// TODO this is an incorrect, predicted location for dirty case
					if ((instance.parent != null) && !instance.dontInheritTranslation) {
						x = instance.parent.localLocation.x + instance.localLocation.x;
						y = instance.parent.localLocation.y + instance.localLocation.y;
					}
					else {
						x = instance.localLocation.x;
						y = instance.localLocation.y;
					}
				}
				else {
					x = instance.worldLocation.x;
					y = instance.worldLocation.y;
				}
				instanceMoved(instance, x, y);
				return true;
			}
		}

		return false;
	}

	public abstract void instanceMoved(ModelInstance instance, float x, float y);

	public boolean removeInstance(final ModelInstance instance) {
		if (instance.scene == this) {
			instance.removeLights(this);
			for (int i = 0, l = instance.childrenInstances.size(); i < l; i++) {
				instance.childrenInstances.get(i).detach();
			}
			innerRemove(instance);

			instance.scene = null;
			this.instances.remove(instance);

			return true;
		}
		return false;
	}

	protected abstract void innerRemove(ModelInstance instance);

	public abstract void clear();

	public boolean detach() {
		if (this.viewer != null) {
			return this.viewer.removeScene(this);
		}
		return false;
	}

	public void addToBatch(final ModelInstance instance) {
		final TextureMapper textureMapper = instance.textureMapper;
		RenderBatch batch = this.batches.get(textureMapper);

		if (batch == null) {
			batch = instance.getBatch(textureMapper);

			this.batches.put(textureMapper, batch);
		}

		batch.add(instance);
	}

	public void update(final float dt) {
		this.camera.update();

		if (this.audioEnabled) {
			final float x = this.camera.location.x;
			final float y = this.camera.location.y;
			final float z = this.camera.location.z;
			final float forwardX = this.camera.directionY.x;
			final float forwardY = this.camera.directionY.y;
			final float forwardZ = this.camera.directionY.z;
			final float upX = this.camera.directionZ.x;
			final float upY = this.camera.directionZ.y;
			final float upZ = this.camera.directionZ.z;
			final AudioContext.Listener listener = this.audioContext.listener;

			listener.setPosition(x, y, z);
			listener.setOrientation(forwardX, forwardY, forwardZ, upX, upY, upZ);
		}

		final int frame = this.viewer.frame;

		this.currentInstance = 0;
		this.currentBatchedInstance = 0;

		innerUpdate(dt, frame);

		for (int i = this.batchedInstances.size() - 1; i >= this.currentBatchedInstance; i--) {
			this.batchedInstances.remove(i);
		}

		for (int i = this.instances.size() - 1; i >= this.currentInstance; i--) {
			this.instances.remove(i);
		}
		Collections.sort(this.instances, this.instanceDepthComparator);

		this.emitterObjectUpdater.update(dt);
		this.updatedParticles = this.emitterObjectUpdater.objects.size();

	}

	protected abstract void innerUpdate(float dt, int frame);

	public void startFrame() {
		final GL20 gl = this.viewer.gl;
		final Rectangle viewport = this.camera.rect;

		// Set the viewport
		gl.glViewport((int) viewport.x, (int) viewport.y, (int) viewport.width, (int) viewport.height);

		// Allow to render only in the viewport
		gl.glScissor((int) viewport.x, (int) viewport.y, (int) viewport.width, (int) viewport.height);

		// If this scene doesn't want alpha, clear it.
		gl.glDepthMask(true);
		if (!this.alpha) {
			gl.glClearColor(this.backgroundColor.r, this.backgroundColor.g, this.backgroundColor.b,
					this.backgroundColor.a);
			gl.glClear(GL20.GL_DEPTH_BUFFER_BIT | GL20.GL_COLOR_BUFFER_BIT);
		}
		else {
			Gdx.gl30.glClear(GL30.GL_DEPTH_BUFFER_BIT);
		}
		this.lightManager.update();
	}

	public void renderOpaque() {
		if (!this.show) {
			return;
		}
		final Rectangle viewport = this.camera.rect;
		this.viewer.gl.glViewport((int) viewport.x, (int) viewport.y, (int) viewport.width, (int) viewport.height);

		// Clear all of the batches.
		for (final RenderBatch batch : this.batches.values()) {
			batch.clear();
		}

		// Add all of the batched instances to batches.
		for (final ModelInstance instance : this.batchedInstances) {
			addToBatch(instance);
		}

		// Render all of the batches.
		for (final RenderBatch batch : this.batches.values()) {
			batch.renderOpaque();
		}

		// Render all of the opaque things of non-batched instances.
		for (final ModelInstance instance : this.instances) {
			instance.renderOpaque(this.camera.viewProjectionMatrix);
		}
	}

	public void renderOpaque(final DynamicShadowManager dynamicShadowManager, final WebGL webGL) {
		if (!this.show) {
			return;
		}
		final Matrix4 depthMatrix = dynamicShadowManager.prepareShadowMatrix();
		dynamicShadowManager.beginShadowMap(webGL);
		Gdx.gl30.glDepthMask(true);
		Gdx.gl30.glClear(GL20.GL_DEPTH_BUFFER_BIT | GL20.GL_COLOR_BUFFER_BIT);
		Gdx.gl30.glDisable(GL30.GL_SCISSOR_TEST);

		// Render all of the opaque things of non-batched instances.
//		for (final ModelInstance instance : this.instances) {
//			instance.renderOpaque(depthMatrix);
//		}

		dynamicShadowManager.endShadowMap();
		final Rectangle viewport = this.camera.rect;
		this.viewer.gl.glViewport((int) viewport.x, (int) viewport.y, (int) viewport.width, (int) viewport.height);
		Gdx.gl30.glEnable(GL30.GL_SCISSOR_TEST);
	}

	/**
	 * Renders all translucent things in this scene. Automatically applies the
	 * camera's viewport.
	 */
	public void renderTranslucent() {
		if (!this.show) {
			return;
		}
		final Rectangle viewport = this.camera.rect;

		this.viewer.gl.glViewport((int) viewport.x, (int) viewport.y, (int) viewport.width, (int) viewport.height);

		// Render all of the batches.
		for (final RenderBatch batch : this.batches.values()) {
			batch.renderTranslucent();
		}

		for (final ModelInstance instance : this.instances) {
			instance.renderTranslucent();
		}

	}

	public void clearEmitterObjects() {
		for (final EmittedObject object : this.emitterObjectUpdater.objects) {
			object.health = 0;
		}
	}

	public void addLight(final SceneLightInstance lightInstance) {
		this.lightManager.add(lightInstance);
	}

	public void removeLight(final SceneLightInstance lightInstance) {
		this.lightManager.remove(lightInstance);
	}

	private static final class InstanceDepthComparator implements Comparator<ModelInstance> {
		@Override
		public int compare(final ModelInstance o1, final ModelInstance o2) {
			return Float.compare(o2.depth, o1.depth);
		}
	}

	public SceneLightManager getLightManager() {
		return this.lightManager;
	}
}
