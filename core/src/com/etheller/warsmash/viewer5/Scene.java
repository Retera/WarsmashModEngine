package com.etheller.warsmash.viewer5;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Rectangle;

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
public class Scene {

	public final ModelViewer viewer;
	public final Camera camera;
	public Grid grid;
	public int visibleCells;
	public int visibleInstances;
	public int updatedParticles;
	public boolean audioEnabled;
	public AudioContext audioContext;

	public final List<ModelInstance> instances;
	public final int currentInstance;
	public final List<ModelInstance> batchedInstances;
	public final int currentBatchedInstance;
	public final EmittedObjectUpdater emitterObjectUpdater;
	public final Map<TextureMapper, RenderBatch> batches;
	public final Comparator<ModelInstance> instanceDepthComparator;
	/**
	 * Similar to WebGL's own `alpha` parameter.
	 *
	 * If false, the scene will be cleared before rendering, meaning that scenes
	 * behind it won't be visible through it.
	 *
	 * If true, alpha works as usual.
	 */
	public boolean alpha = false;

	public Scene(final ModelViewer viewer) {
		final CanvasProvider canvas = viewer.canvas;
		this.viewer = viewer;
		this.camera = new Camera();
		this.grid = new Grid(-100000, -100000, 200000, 200000, 200000, 200000);

		this.visibleCells = 0;
		this.visibleInstances = 0;
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
	}

	public boolean enableAudio() {
		if (this.audioContext == null) {
			this.audioContext = new AudioContext();
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
				this.grid.moved(instance);

				return true;
			}
		}

		return false;
	}

	public boolean removeInstance(final ModelInstance instance) {
		if (instance.scene == this) {
			this.grid.remove(instance);

			instance.scene = null;

			return true;
		}
		return false;
	}

	public void clear() {
		// First remove references to this scene stored in the instances.
		for (final GridCell cell : this.grid.cells) {
			for (final ModelInstance instance : cell.instances) {
				instance.scene = null;
			}
		}

		// Then remove references to the instances.
		this.grid.clear();
	}

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

			listener.setPosition(-x, -y, -z);
			listener.setOrientation(forwardX, forwardY, forwardZ, upX, upY, upZ);
		}

		final int frame = this.viewer.frame;

		int currentInstance = 0;
		int currentBatchedInstance = 0;

		this.visibleCells = 0;
		this.visibleInstances = 0;

		// Update and collect all of the visible instances.
		for (final GridCell cell : this.grid.cells) {
			if (cell.isVisible(this.camera)) {
				this.visibleCells += 1;

				for (final ModelInstance instance : new ArrayList<>(cell.instances)) {
//					final ModelInstance instance = cell.instances.get(i);
					if (instance.rendered && (instance.cullFrame < frame) && instance.isVisible(this.camera)) {
						instance.cullFrame = frame;

						if (instance.updateFrame < frame) {
							instance.update(dt, this);
						}

						if (instance.isBatched()) {
							if (currentBatchedInstance < this.batchedInstances.size()) {
								this.batchedInstances.set(currentBatchedInstance++, instance);
							}
							else {
								this.batchedInstances.add(instance);
								currentBatchedInstance++;
							}
						}
						else {
							if (currentInstance < this.instances.size()) {
								this.instances.set(currentInstance++, instance);
							}
							else {
								this.instances.add(instance);
								currentInstance++;
							}
						}

						this.visibleInstances += 1;
					}
				}
			}
		}

		for (int i = this.batchedInstances.size() - 1; i >= currentBatchedInstance; i--) {
			this.batchedInstances.remove(i);
		}

		for (int i = this.instances.size() - 1; i >= currentInstance; i--) {
			this.instances.remove(i);
		}
		Collections.sort(this.instances, this.instanceDepthComparator);

		this.emitterObjectUpdater.update(dt);
		this.updatedParticles = this.emitterObjectUpdater.objects.size();
	}

	public void startFrame() {
		final GL20 gl = this.viewer.gl;
		final Rectangle viewport = this.camera.rect;

		// Set the viewport
		gl.glViewport((int) viewport.x, (int) viewport.y, (int) viewport.width, (int) viewport.height);

		// Allow to render only in the viewport
		gl.glScissor((int) viewport.x, (int) viewport.y, (int) viewport.width, (int) viewport.height);

		// If this scene doesn't want alpha, clear it.
		if (!this.alpha) {
			gl.glDepthMask(true);
			gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		}
	}

	public void renderOpaque() {
		final Rectangle viewport = this.camera.rect;
		this.viewer.gl.glViewport((int) viewport.x, (int) viewport.y, (int) viewport.width, (int) viewport.height);

		// Clear all of the batches.
		for (final RenderBatch batch : this.batches.values()) {
			batch.clear();
		}

		// Add all of the batched instances to batches.
		for (final ModelInstance instance : this.batchedInstances) {
			this.addToBatch(instance);
		}

		// Render all of the batches.
		for (final RenderBatch batch : this.batches.values()) {
			batch.render();
		}

		// Render all of the opaque things of non-batched instances.
		for (final ModelInstance instance : this.instances) {
			instance.renderOpaque();
		}
	}

	/**
	 * Renders all translucent things in this scene. Automatically applies the
	 * camera's viewport.
	 */
	public void renderTranslucent() {
		final Rectangle viewport = this.camera.rect;

		this.viewer.gl.glViewport((int) viewport.x, (int) viewport.y, (int) viewport.width, (int) viewport.height);

		for (final ModelInstance instance : this.instances) {
			instance.renderTranslucent();
		}

	}

	public void clearEmitterObjects() {
		for (final EmittedObject object : this.emitterObjectUpdater.objects) {
			object.health = 0;
		}
	}

	private static final class InstanceDepthComparator implements Comparator<ModelInstance> {
		@Override
		public int compare(final ModelInstance o1, final ModelInstance o2) {
			return (int) Math.signum(o2.depth - o1.depth);
		}
	}
}
