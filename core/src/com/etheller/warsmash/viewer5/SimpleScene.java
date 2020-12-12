package com.etheller.warsmash.viewer5;

import java.util.ArrayList;
import java.util.List;

public class SimpleScene extends Scene {
	private final List<ModelInstance> allInstances = new ArrayList<>();

	public SimpleScene(final ModelViewer viewer, final SceneLightManager lightManager) {
		super(viewer, lightManager);
		this.visibleCells = 1;
		this.visibleInstances = 0;
	}

	@Override
	public void instanceMoved(final ModelInstance instance, final float x, final float y) {
		if (instance.left == -1) {
			instance.left = 0;
			this.allInstances.add(instance);
		}
	}

	@Override
	protected void innerRemove(final ModelInstance instance) {
		this.allInstances.remove(instance);
		instance.left = -1;
	}

	@Override
	public void clear() {
		for (final ModelInstance instance : this.allInstances) {
			instance.scene = null;
		}
		this.allInstances.clear();
	}

	@Override
	protected void innerUpdate(final float dt, final int frame) {

		// Update and collect all of the visible instances.
		for (final ModelInstance instance : new ArrayList<>(this.allInstances)) {
			// Below: current SimpleScene is not checking instance visibility.
			// It's meant to be simple. Low number of models. Render everything,
			// dont check visible. Then I had to add a call to isVisible() because it
			// assigns depth, which is crazy.
			instance.isVisible(this.camera);
			if (instance.rendered && (instance.cullFrame < frame)) {
				instance.cullFrame = frame;

				if (instance.updateFrame < frame) {
					instance.update(dt, this);
					if (!instance.rendered) {
						// it became hidden while it updated
						continue;
					}
				}

				if (instance.isBatched()) {
					if (this.currentBatchedInstance < this.batchedInstances.size()) {
						this.batchedInstances.set(this.currentBatchedInstance++, instance);
					}
					else {
						this.batchedInstances.add(instance);
						this.currentBatchedInstance++;
					}
				}
				else {
					if (this.currentInstance < this.instances.size()) {
						this.instances.set(this.currentInstance++, instance);
					}
					else {
						this.instances.add(instance);
						this.currentInstance++;
					}
				}

			}
		}
	}

}
