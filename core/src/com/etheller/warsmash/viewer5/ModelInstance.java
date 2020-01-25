package com.etheller.warsmash.viewer5;

import com.etheller.warsmash.util.RenderMathUtils;
import com.etheller.warsmash.util.Vector4;

public abstract class ModelInstance extends Node {

	public int left;
	public int right;
	public int bottom;
	public int top;
	public int plane;
	public float depth;
	public int updateFrame;
	public int cullFrame;
	public Model<?> model;
	public TextureMapper textureMapper;
	public boolean paused;
	public boolean rendered;

	public Scene scene;

	public ModelInstance(final Model model) {
		this.scene = null;
		this.left = -1;
		this.right = -1;
		this.bottom = -1;
		this.top = -1;
		this.plane = -1;
		this.depth = 0;
		this.updateFrame = 0;
		this.cullFrame = 0;
		this.model = model;
		this.textureMapper = model.viewer.baseTextureMapper(this);
		this.paused = false;
		this.rendered = true;
	}

	public void setTexture(final int index, final Texture texture) {
		this.textureMapper = this.model.viewer.changeTextureMapper(this, index, texture);
	}

	public void show() {
		this.rendered = true;
	}

	public void hide() {
		this.rendered = false;
	}

	public boolean shown() {
		return this.rendered;
	}

	public boolean hidden() {
		return !this.rendered;
	}

	public boolean detach() {
		if (this.scene != null) {
			return this.scene.removeInstance(this);
		}

		return false;
	}

	public abstract void updateAnimations(float dt);

	public abstract void clearEmittedObjects();

	@Override
	protected final void updateObject(final float dt, final Scene scene) {
		if (this.updateFrame < this.model.viewer.frame) {
			if (this.rendered && !this.paused) {
				this.updateAnimations(dt);
			}
		}

		this.updateFrame = this.model.viewer.frame;
	}

	public boolean setScene(final Scene scene) {
		return scene.addInstance(this);
	}

	@Override
	public void recalculateTransformation() {
		super.recalculateTransformation();

		if (this.scene != null) {
			this.scene.grid.moved(this);
		}
	}

	public boolean isVisible(final Camera camera) {
		if (true) {
			return true;
		}
		final float x = this.worldLocation.x;
		final float y = this.worldLocation.y;
		final float z = this.worldLocation.z;
		final Bounds bounds = this.model.bounds;
		final Vector4[] planes = camera.planes;

		this.plane = RenderMathUtils.testSphere(planes, x + bounds.x, y + bounds.y, z, bounds.r, this.plane);

		if (this.plane == -1) {
			this.depth = RenderMathUtils.distanceToPlane3(planes[4], x, y, z);

			return true;
		}

		return false;
	}

	public boolean isBatched() {
		return false;
	}

	public abstract void renderOpaque();

	public abstract void renderTranslucent();

	public abstract void load();

	protected abstract RenderBatch getBatch(TextureMapper textureMapper2);

	public abstract void setReplaceableTexture(int replaceableTextureId, String replaceableTextureFile);
}
