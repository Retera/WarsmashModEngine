package com.etheller.warsmash.viewer;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.etheller.warsmash.util.RenderMathUtils;

public abstract class SceneNode extends ViewerNode {

	public SceneNode() {
	}

	public SceneNode setPivot(final float[] pivot) {
		this.pivot.set(pivot);
		this.dirty = true;
		return this;
	}

	public SceneNode setLocation(final float[] location) {
		this.localLocation.set(location);
		this.dirty = true;
		return this;
	}

	public SceneNode setRotation(final float[] rotation) {
		this.localRotation.set(rotation[0], rotation[1], rotation[2], rotation[3]);
		this.dirty = true;
		return this;
	}

	public SceneNode setScale(final float[] varying) {
		this.localScale.set(varying);
		this.dirty = true;
		return this;
	}

	public SceneNode setUniformScale(final float uniform) {
		this.localScale.set(uniform, uniform, uniform);
		this.dirty = true;
		return this;
	}

	public SceneNode setTransformation(final Vector3 location, final Quaternion rotation, final Vector3 scale) {
		// TODO for performance, Ghostwolf did a direct field write on everything here.
		// I'm hoping we can get Java's JIT to just figure it out and do it on its own
		this.localLocation.set(location);
		this.localRotation.set(rotation);
		this.localScale.set(scale);
		this.dirty = true;
		return this;
	}

	public SceneNode resetTransformation() {
		this.pivot.set(Vector3.Zero);
		this.localLocation.set(Vector3.Zero);
		this.localRotation.set(RenderMathUtils.QUAT_DEFAULT);
		this.localScale.set(RenderMathUtils.VEC3_ONE);

		this.dirty = true;
		return this;
	}

	public SceneNode movePivot(final float[] offset) {
		this.pivot.add(offset[0], offset[1], offset[2]);

		this.dirty = true;

		return this;
	}

	public SceneNode move(final float[] offset) {
		this.localLocation.add(offset[0], offset[1], offset[2]);

		this.dirty = true;

		return this;
	}

	public SceneNode rotate(final Quaternion rotation) {
		RenderMathUtils.mul(this.localRotation, this.localRotation, rotation);

		this.dirty = true;

		return this;
	}

	public SceneNode rotateLocal(final Quaternion rotation) {
		RenderMathUtils.mul(this.localRotation, rotation, this.localRotation);

		this.dirty = true;

		return this;
	}

	public SceneNode scale(final float[] scale) {
		this.localScale.x *= scale[0];
		this.localScale.y *= scale[1];
		this.localScale.z *= scale[2];

		this.dirty = true;

		return this;
	}

	public SceneNode uniformScale(final float scale) {
		this.localScale.x *= scale;
		this.localScale.y *= scale;
		this.localScale.z *= scale;

		this.dirty = true;

		return this;
	}

	public SceneNode setParent(final ViewerNode parent) {
		if (this.parent != null) {
			this.parent.children.remove(this);
		}

		this.parent = parent;

		if (parent != null) {
			parent.children.add(this);
		}

		this.dirty = true;

		return this;
	}

	public void recalculateTransformation() {
		boolean dirty = this.dirty;
		final ViewerNode parent = this.parent;

		this.wasDirty = this.dirty;

		if (parent != null) {
			dirty = dirty || parent.wasDirty;
		}

		this.wasDirty = dirty;

		if (dirty) {
			this.dirty = false;

			if (parent != null) {
				Vector3 computedLocation;
				Vector3 computedScaling;

				final Vector3 parentPivot = parent.pivot;

				computedLocation = locationHeap;
				computedLocation.x = this.localLocation.x + parentPivot.x;
				computedLocation.y = this.localLocation.y + parentPivot.y;
				computedLocation.z = this.localLocation.z + parentPivot.z;

				if (this.dontInheritScaling) {
					computedScaling = scalingHeap;

					final Vector3 parentInverseScale = parent.inverseWorldScale;
					computedScaling.x = parentInverseScale.x * this.localScale.x;
					computedScaling.y = parentInverseScale.y * this.localScale.y;
					computedScaling.z = parentInverseScale.z * this.localScale.z;

					this.worldScale.x = this.localScale.x;
					this.worldScale.y = this.localScale.y;
					this.worldScale.z = this.localScale.z;
				}
				else {
					computedScaling = this.localScale;

					final Vector3 parentScale = parent.worldScale;
					this.worldScale.x = parentScale.x * this.localScale.x;
					this.worldScale.y = parentScale.y * this.localScale.y;
					this.worldScale.z = parentScale.z * this.localScale.z;
				}

				RenderMathUtils.fromRotationTranslationScale(this.localRotation, computedLocation, computedScaling,
						this.localMatrix);

				RenderMathUtils.mul(this.worldMatrix, parent.worldMatrix, this.localMatrix);

				RenderMathUtils.mul(this.worldRotation, parent.worldRotation, this.localRotation);
			}
			else {
				RenderMathUtils.fromRotationTranslationScale(this.localRotation, this.localLocation, this.localScale,
						this.localMatrix);

				this.worldMatrix.set(this.localMatrix);

				this.worldRotation.set(this.localRotation);

				this.worldScale.set(this.localScale);
			}
		}

		// Inverse world rotation
		this.inverseWorldRotation.x = -this.worldRotation.x;
		this.inverseWorldRotation.y = -this.worldRotation.y;
		this.inverseWorldRotation.z = -this.worldRotation.z;
		this.inverseWorldRotation.w = this.worldRotation.w;

		// Inverse world scale
		this.inverseWorldScale.x = 1 / this.worldScale.x;
		this.inverseWorldScale.y = 1 / this.worldScale.y;
		this.inverseWorldScale.z = 1 / this.worldScale.z;

		// World location
		this.worldLocation.x = this.worldMatrix.val[Matrix4.M30];
		this.worldLocation.y = this.worldMatrix.val[Matrix4.M31];
		this.worldLocation.z = this.worldMatrix.val[Matrix4.M32];

		// Inverse world location
		this.inverseWorldLocation.x = -this.worldLocation.x;
		this.inverseWorldLocation.y = -this.worldLocation.y;
		this.inverseWorldLocation.z = -this.worldLocation.z;

	}

	@Override
	public void update(final Scene scene) {
		if (this.dirty || ((this.parent != null) && this.parent.wasDirty)) {
			this.dirty = true;
			this.wasDirty = true;
			this.recalculateTransformation();
		}
		else {
			this.wasDirty = false;
		}

		this.updateObject(scene);
		this.updateChildren(scene);
	}

	protected abstract void updateObject(Scene scene);

	protected void updateChildren(final Scene scene) {
		for (int i = 0, l = this.children.size(); i < l; i++) {
			this.children.get(i).update(scene);
		}
	}

	protected abstract void convertBasis(Quaternion computedRotation);

}
