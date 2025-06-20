package com.etheller.warsmash.viewer5;

import java.util.ArrayList;

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
public class WorldScene extends Scene {

	public Grid grid;
	private final InstanceProcessor instanceProcessor = new InstanceProcessor();

	public WorldScene(final ModelViewer viewer, final SceneLightManager lightManager) {
		super(viewer, lightManager);
		this.grid = new Grid(-100000, -100000, 200000, 200000, 200000, 200000);
	}

	@Override
	public void instanceMoved(final ModelInstance instance, final float x, final float y) {
		this.grid.moved(instance, x, y);
	}

	@Override
	protected void innerRemove(final ModelInstance instance) {
		this.grid.remove(instance);
	}

	@Override
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

	@Override
	protected void innerUpdate(final float dt, final int frame) {
		this.visibleCells = 0;
		this.visibleInstances = 0;

		// Update and collect all of the visible instances.
		this.instanceProcessor.reset(dt, frame);
		for (final GridCell cell : this.grid.cells) {
			if (cell.isVisible(this.camera)) {
				this.visibleCells += 1;

				for (final ModelInstance instance : new ArrayList<>(cell.instances)) {
					this.instanceProcessor.call(instance);
				}
			}
		}
	}

	private final class InstanceProcessor implements ModelInstanceCallback {
		private float dt;
		private int frame;

		public InstanceProcessor reset(final float dt, final int frame) {
			this.dt = dt;
			this.frame = frame;
			return this;
		}

		@Override
		public void call(final ModelInstance instance) {
//			final ModelInstance instance = cell.instances.get(i);
			if (instance.rendered && (instance.cullFrame < this.frame) && instance.isVisible(WorldScene.this.camera)) {
				instance.cullFrame = this.frame;

				if (instance.updateFrame < this.frame) {
					instance.update(this.dt, WorldScene.this);
					if (!instance.rendered) {
						// it became hidden while it updated
						return;
					}
				}

				if (instance.isBatched()) {
					if (WorldScene.this.currentBatchedInstance < WorldScene.this.batchedInstances.size()) {
						WorldScene.this.batchedInstances.set(WorldScene.this.currentBatchedInstance++, instance);
					}
					else {
						WorldScene.this.batchedInstances.add(instance);
						WorldScene.this.currentBatchedInstance++;
					}
				}
				else {
					if (WorldScene.this.currentInstance < WorldScene.this.instances.size()) {
						WorldScene.this.instances.set(WorldScene.this.currentInstance++, instance);
					}
					else {
						WorldScene.this.instances.add(instance);
						WorldScene.this.currentInstance++;
					}
				}

				WorldScene.this.visibleInstances += 1;
				for (int i = 0, l = instance.childrenInstances.size(); i < l; i++) {
					call(instance.childrenInstances.get(i));
				}
			}
		}
	}
}
