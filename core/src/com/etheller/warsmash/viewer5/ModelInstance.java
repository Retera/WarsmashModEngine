package com.etheller.warsmash.viewer5;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
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

		this.updateLights(scene);

		this.updateFrame = this.model.viewer.frame;
	}

	protected abstract void updateLights(Scene scene2);

	public boolean setScene(final Scene scene) {
		return scene.addInstance(this);
	}

	@Override
	public Node move(final float[] offset) {
		final Node result = super.move(offset);
		updateSceneGridLocationInfo();
		return result;
	}

	@Override
	public Node moveTo(final float[] offset) {
		final Node result = super.moveTo(offset);
		updateSceneGridLocationInfo();
		return result;
	}

	@Override
	public Node setLocation(final float x, final float y, final float z) {
		final Node result = super.setLocation(x, y, z);
		updateSceneGridLocationInfo();
		return result;
	}

	@Override
	public Node setLocation(final float[] location) {
		final Node result = super.setLocation(location);
		updateSceneGridLocationInfo();
		return result;
	}

	@Override
	public Node setLocation(final Vector3 location) {
		final Node result = super.setLocation(location);
		updateSceneGridLocationInfo();
		return result;
	}

	private void updateSceneGridLocationInfo() {
		if (this.scene != null) {
			// can't just use world location if it moves
			float x, y;
			if (this.dirty) {
				// TODO this is an incorrect, predicted location for dirty case
				if ((this.parent != null) && !this.dontInheritTranslation) {
					x = this.parent.localLocation.x + this.localLocation.x;
					y = this.parent.localLocation.y + this.localLocation.y;
				}
				else {
					x = this.localLocation.x;
					y = this.localLocation.y;
				}
			}
			else {
				x = this.worldLocation.x;
				y = this.worldLocation.y;
			}
			this.scene.instanceMoved(this, x, y);
		}
	}

	@Override
	public void recalculateTransformation() {
		super.recalculateTransformation();

		if (this.scene != null) {
			this.scene.instanceMoved(this, this.worldLocation.x, this.worldLocation.y);
		}
	}

	public boolean isVisible(final Camera camera) {
		// can't just use world location if it moves
		float x, y, z;
		float sx, sy, sz;
		if (this.dirty) {
			// TODO this is an incorrect, predicted location for dirty case
			if ((this.parent != null) && !this.dontInheritTranslation) {
				x = this.parent.localLocation.x + this.localLocation.x;
				y = this.parent.localLocation.y + this.localLocation.y;
				z = this.parent.localLocation.z + this.localLocation.z;
				sx = this.parent.localScale.x * this.localScale.x;
				sy = this.parent.localScale.y * this.localScale.y;
				sz = this.parent.localScale.z * this.localScale.z;
			}
			else {
				x = this.localLocation.x;
				y = this.localLocation.y;
				z = this.localLocation.z;
				sx = this.localScale.x;
				sy = this.localScale.y;
				sz = this.localScale.z;
			}
		}
		else {
			x = this.worldLocation.x;
			y = this.worldLocation.y;
			z = this.worldLocation.z;
			sx = this.worldScale.x;
			sy = this.worldScale.y;
			sz = this.worldScale.z;
		}
		// Get the biggest scaling dimension.
		if (sy > sx) {
			sx = sy;
		}

		if (sz > sx) {
			sx = sz;
		}

		final Bounds bounds = this.model.bounds;
		final Vector4[] planes = camera.planes;

		this.plane = RenderMathUtils.testSphere(planes, x + bounds.x, y + bounds.y, z + bounds.z, bounds.r * sx,
				this.plane);

		if (this.plane == -1) {
			this.depth = RenderMathUtils.distanceToPlane3(planes[4], x, y, z);

			return true;
		}

		return false;
	}

	public boolean isBatched() {
		return false;
	}

	public abstract void renderOpaque(Matrix4 mvp);

	public abstract void renderTranslucent();

	public abstract void load();

	protected abstract RenderBatch getBatch(TextureMapper textureMapper2);

	public abstract void setReplaceableTexture(int replaceableTextureId, String replaceableTextureFile);

	public abstract void setReplaceableTextureHD(int replaceableTextureId, String replaceableTextureFile);

	protected abstract void removeLights(Scene scene2);
}
