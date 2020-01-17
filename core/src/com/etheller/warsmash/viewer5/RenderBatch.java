package com.etheller.warsmash.viewer5;

import java.util.ArrayList;
import java.util.List;

/**
 * A render batch.
 */
public abstract class RenderBatch {
	public Scene scene;
	public Model<?> model;
	public TextureMapper textureMapper;
	public List<ModelInstance> instances = new ArrayList<>();
	public int count = 0;

	public abstract void render();

	public RenderBatch(final Scene scene, final Model<?> model, final TextureMapper textureMapper) {
		this.scene = scene;
		this.model = model;
		this.textureMapper = textureMapper;
	}

	public void clear() {
		this.count = 0;
	}

	public void add(final ModelInstance instance) {
		if (this.count == this.instances.size()) {
			this.instances.add(instance);
		}
		else if (this.count > this.instances.size()) {
			throw new IllegalStateException("count > size");
		}
		else {
			this.instances.set(this.count, instance);
		}
		this.count++;
	}
}
