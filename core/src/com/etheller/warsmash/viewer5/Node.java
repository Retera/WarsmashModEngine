package com.etheller.warsmash.viewer5;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.etheller.warsmash.util.Descriptor;
import com.etheller.warsmash.util.RenderMathUtils;

public abstract class Node extends GenericNode {
	protected static final Vector3 locationHeap = new Vector3();
	protected static final Quaternion rotationHeap = new Quaternion();
	protected static final Vector3 scalingHeap = new Vector3();

	public Node() {
		this.pivot = new Vector3();
		this.localLocation = new Vector3();
		this.localRotation = new Quaternion();
		this.localScale = new Vector3(1, 1, 1);
		this.worldLocation = new Vector3();
		this.worldRotation = new Quaternion();
		this.worldScale = new Vector3(1, 1, 1);
		this.inverseWorldLocation = new Vector3();
		this.inverseWorldRotation = new Quaternion();
		this.inverseWorldScale = new Vector3();
		this.localMatrix = new Matrix4();
//		this.localMatrix.val[0] = 1;
//		this.localMatrix.val[5] = 1;
//		this.localMatrix.val[10] = 1;
//		this.localMatrix.val[15] = 1;
		this.worldMatrix = new Matrix4();
		this.parent = null;
		this.children = new ArrayList<>();
		this.dontInheritTranslation = false;
		this.dontInheritRotation = false;
		this.dontInheritScaling = false;

		this.visible = true;
		this.wasDirty = false;
		this.dirty = true;
	}

	public Node setPivot(final float[] pivot) {
		this.pivot.set(pivot);
		this.dirty = true;
		return this;
	}

	public Node setLocation(final float x, final float y, final float z) {
		this.localLocation.set(x, y, z);
		this.dirty = true;
		return this;
	}

	public Node setLocation(final float[] location) {
		this.localLocation.set(location);
		this.dirty = true;
		return this;
	}

	public Node setLocation(final Vector3 location) {
		this.localLocation.set(location);
		this.dirty = true;
		return this;
	}

	public Node setRotation(final float[] rotation) {
		this.localRotation.set(rotation[0], rotation[1], rotation[2], rotation[3]);
		this.dirty = true;
		return this;
	}

	public Node setScale(final float[] varying) {
		this.localScale.set(varying);
		this.dirty = true;
		return this;
	}

	public Node setUniformScale(final float uniform) {
		this.localScale.set(uniform, uniform, uniform);
		this.dirty = true;
		return this;
	}

	public Node setTransformation(final Vector3 location, final Quaternion rotation, final Vector3 scale) {
		// TODO for performance, Ghostwolf did a direct field write on everything here.
		// I'm hoping we can get Java's JIT to just figure it out and do it on its own
		this.localLocation.set(location);
		this.localRotation.set(rotation);
		this.localScale.set(scale);
		this.dirty = true;
		return this;
	}

	public Node resetTransformation() {
		this.pivot.set(Vector3.Zero);
		this.localLocation.set(Vector3.Zero);
		this.localRotation.set(RenderMathUtils.QUAT_DEFAULT);
		this.localScale.set(RenderMathUtils.VEC3_ONE);

		this.dirty = true;
		return this;
	}

	public Node movePivot(final float[] offset) {
		this.pivot.add(offset[0], offset[1], offset[2]);

		this.dirty = true;

		return this;
	}

	public Node move(final float[] offset) {
		this.localLocation.add(offset[0], offset[1], offset[2]);

		this.dirty = true;

		return this;
	}

	public Node moveTo(final float[] offset) {
		this.localLocation.set(offset[0], offset[1], offset[2]);

		this.dirty = true;

		return this;
	}

	public Node rotate(final Quaternion rotation) {
		RenderMathUtils.mul(this.localRotation, this.localRotation, rotation);

		this.dirty = true;

		return this;
	}

	public Node setLocalRotation(final Quaternion rotation) {
		this.localRotation.set(rotation);

		this.dirty = true;

		return this;
	}

	public Node rotateLocal(final Quaternion rotation) {
		RenderMathUtils.mul(this.localRotation, rotation, this.localRotation);

		this.dirty = true;

		return this;
	}

	public Node scale(final float[] scale) {
		this.localScale.x *= scale[0];
		this.localScale.y *= scale[1];
		this.localScale.z *= scale[2];

		this.dirty = true;

		return this;
	}

	public Node uniformScale(final float scale) {
		this.localScale.x *= scale;
		this.localScale.y *= scale;
		this.localScale.z *= scale;

		this.dirty = true;

		return this;
	}

	public Node setParent(final GenericNode parent) {
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
		final GenericNode parent = this.parent;

		this.wasDirty = this.dirty;

		if (parent != null) {
			dirty = dirty || parent.wasDirty;
		}

		this.wasDirty = dirty;

		if (dirty) {
			this.dirty = false;

			if (parent != null) {
				Vector3 computedLocation;
				Quaternion computedRotation;
				Vector3 computedScaling;

				final Vector3 parentPivot = parent.pivot;

				computedLocation = locationHeap;
				computedLocation.x = this.localLocation.x + parentPivot.x;
				computedLocation.y = this.localLocation.y + parentPivot.y;
				computedLocation.z = this.localLocation.z + parentPivot.z;

				if (this.dontInheritRotation) {
					computedRotation = rotationHeap;

					computedRotation.set(this.localRotation);
					computedRotation.mul(parent.inverseWorldRotation);
				}
				else {
					computedRotation = this.localRotation;
				}

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

				RenderMathUtils.fromRotationTranslationScale(computedRotation, computedLocation, computedScaling,
						this.localMatrix);

				RenderMathUtils.mul(this.worldMatrix, parent.worldMatrix, this.localMatrix);

				RenderMathUtils.mul(this.worldRotation, parent.worldRotation, computedRotation);
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
		this.worldLocation.x = this.worldMatrix.val[Matrix4.M03];
		this.worldLocation.y = this.worldMatrix.val[Matrix4.M13];
		this.worldLocation.z = this.worldMatrix.val[Matrix4.M23];

		// Inverse world location
		this.inverseWorldLocation.x = -this.worldLocation.x;
		this.inverseWorldLocation.y = -this.worldLocation.y;
		this.inverseWorldLocation.z = -this.worldLocation.z;

	}

	@Override
	public void update(final float dt, final Scene scene) {
		if (this.dirty || ((this.parent != null) && this.parent.wasDirty)) {
			this.dirty = true; // in case this node isn't dirty, but the parent was
			this.wasDirty = true;
			this.recalculateTransformation();
		}
		else {
			this.wasDirty = false;
		}


		this.updateObject(dt, scene);
		this.updateChildren(dt, scene);
	}

	protected abstract void updateObject(float dt, Scene scene);

	private void updateChildren(final float dt, final Scene scene) {
		final int childrenSize = this.children.size();
		for (int i = 0; i < childrenSize; i++) {
			this.children.get(i).update(dt, scene);
		}
	}

	public static <NODE extends SkeletalNode> Object[] createSkeletalNodes(final int count,
			final Descriptor<NODE> nodeDescriptor) {
		final List<NODE> nodes = new ArrayList<>();
		final List<Matrix4> worldMatrices = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			final NODE node = nodeDescriptor.create();
			nodes.add(node);
			worldMatrices.add(node.worldMatrix);
		}
		final Object[] data = { nodes, worldMatrices };
		return data;
	}
}
