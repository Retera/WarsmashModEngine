package com.etheller.warsmash.viewer;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.etheller.warsmash.util.Descriptor;
import com.etheller.warsmash.util.RenderMathUtils;

public abstract class SkeletalNode extends ViewerNode {

	private final Object object;

	private final boolean billboarded = false;
	private final boolean billboardedX = false;
	private final boolean billboardedY = false;
	private final boolean billboardedZ = false;

	public SkeletalNode() {
		this.object = null;
	}

	public void recalculateTransformation(final Scene scene) {
		final Quaternion computedRotation;
		Vector3 computedScaling;

		if (this.dontInheritScaling) {
			computedScaling = scalingHeap;

			final Vector3 parentInverseScale = this.parent.inverseWorldScale;
			computedScaling.x = parentInverseScale.x * this.localScale.x;
			computedScaling.y = parentInverseScale.y * this.localScale.y;
			computedScaling.z = parentInverseScale.z * this.localScale.z;

			this.worldScale.x = this.localScale.x;
			this.worldScale.y = this.localScale.y;
			this.worldScale.z = this.localScale.z;
		}
		else {
			computedScaling = this.localScale;

			final Vector3 parentScale = this.parent.worldScale;
			this.worldScale.x = parentScale.x * this.worldScale.x;
			this.worldScale.y = parentScale.y * this.worldScale.y;
			this.worldScale.z = parentScale.z * this.worldScale.z;
		}

		if (this.billboarded) {
			computedRotation = rotationHeap;

			computedRotation.set(this.parent.inverseWorldRotation);
			computedRotation.mul(scene.camera.inverseRotation);

			this.convertBasis(computedRotation);
		}
		else {
			computedRotation = this.localRotation;
		}

		RenderMathUtils.fromRotationTranslationScaleOrigin(computedRotation, this.localLocation, computedScaling,
				this.localMatrix, this.pivot);

		RenderMathUtils.mul(this.worldMatrix, this.parent.worldMatrix, this.localMatrix);

		RenderMathUtils.mul(this.worldRotation, this.parent.worldRotation, computedRotation);

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
		final float x = this.pivot.x;
		final float y = this.pivot.y;
		final float z = this.pivot.z;
		this.worldLocation.x = (this.worldMatrix.val[Matrix4.M00] * x) + (this.worldMatrix.val[Matrix4.M10] * y)
				+ (this.worldMatrix.val[Matrix4.M20] * z) + this.worldMatrix.val[Matrix4.M30];
		this.worldLocation.y = (this.worldMatrix.val[Matrix4.M01] * x) + (this.worldMatrix.val[Matrix4.M11] * y)
				+ (this.worldMatrix.val[Matrix4.M21] * z) + this.worldMatrix.val[Matrix4.M31];
		this.worldLocation.z = (this.worldMatrix.val[Matrix4.M02] * x) + (this.worldMatrix.val[Matrix4.M12] * y)
				+ (this.worldMatrix.val[Matrix4.M22] * z) + this.worldMatrix.val[Matrix4.M32];

		// Inverse world location
		this.inverseWorldLocation.x = -this.worldLocation.x;
		this.inverseWorldLocation.y = -this.worldLocation.y;
		this.inverseWorldLocation.z = -this.worldLocation.z;
	}

	protected void updateChildren(final Scene scene) {
		for (int i = 0, l = this.children.size(); i < l; i++) {
			this.children.get(i).update(scene);
		}
	}

	protected abstract void convertBasis(Quaternion computedRotation);

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
