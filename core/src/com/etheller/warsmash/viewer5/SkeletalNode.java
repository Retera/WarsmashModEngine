package com.etheller.warsmash.viewer5;

import java.util.ArrayList;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.etheller.warsmash.util.RenderMathUtils;

public abstract class SkeletalNode extends GenericNode {
	protected static final Vector3 cameraRayHeap = new Vector3();
	protected static final Vector3 billboardAxisHeap = new Vector3();
	protected static final Quaternion rotationHeap = new Quaternion();
	protected static final Quaternion rotationHeap2 = new Quaternion();
	protected static final Vector3 scalingHeap = new Vector3();
	protected static final Vector3 blendLocationHeap = new Vector3();
	protected static final Vector3 blendHeap = new Vector3();
	protected static final Vector3 blendScaleHeap = new Vector3();

	public UpdatableObject object;

	public boolean billboarded;
	public boolean billboardedX;
	public boolean billboardedY;
	public boolean billboardedZ;

	public Vector3 localBlendLocation;
	public Quaternion localBlendRotation;
	public Vector3 localBlendScale;

	public SkeletalNode() {
		this.pivot = new Vector3();
		this.localLocation = new Vector3();
		this.localRotation = new Quaternion(0, 0, 0, 1);
		this.localScale = new Vector3(1, 1, 1);
		this.worldLocation = new Vector3();
		this.worldRotation = new Quaternion();
		this.worldScale = new Vector3(1, 1, 1);
		this.inverseWorldLocation = new Vector3();
		this.inverseWorldRotation = new Quaternion();
		this.inverseWorldScale = new Vector3();
		this.localMatrix = new Matrix4();
		this.localBlendLocation = new Vector3();
		this.localBlendRotation = new Quaternion(0, 0, 0, 1);
		this.localBlendScale = new Vector3(1, 1, 1);
		this.worldMatrix = new Matrix4();
		this.dontInheritTranslation = false;
		this.dontInheritRotation = false;
		this.dontInheritScaling = false;
		this.children = new ArrayList<>();

		this.visible = true;
		this.wasDirty = false;

		/**
		 * The object associated with this node, if there is any.
		 *
		 * @member {?}
		 */
		this.object = null;

		this.localRotation.w = 1;

		this.localScale.set(1, 1, 1);

		this.localMatrix.val[0] = 1;
		this.localMatrix.val[5] = 1;
		this.localMatrix.val[10] = 1;
		this.localMatrix.val[15] = 1;

		this.dirty = true;

		this.billboarded = false;
		this.billboardedX = false;
		this.billboardedY = false;
		this.billboardedZ = false;
	}

	public void recalculateTransformation(final Scene scene, final float blendTimeRatio) {
		final float inverseBlendRatio = 1 - blendTimeRatio;
		final Quaternion computedRotation;
		Vector3 computedScaling;
		Vector3 computedLocation;

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
			if (!Float.isNaN(blendTimeRatio) && (blendTimeRatio > 0)) {
				blendScaleHeap.set(this.localScale).scl(inverseBlendRatio)
						.add(blendHeap.set(this.localBlendScale).scl(blendTimeRatio));
				computedScaling = blendScaleHeap;
			}
			else {
				computedScaling = this.localScale;
			}

			final Vector3 parentScale = this.parent.worldScale;
			this.worldScale.x = parentScale.x * this.localScale.x;
			this.worldScale.y = parentScale.y * this.localScale.y;
			this.worldScale.z = parentScale.z * this.localScale.z;
		}

		if (this.billboarded) {
			computedRotation = rotationHeap;

			computedRotation.set(this.parent.inverseWorldRotation);
			if (scene != null) {
				// TODO null scene is stupid, and happens rarely
				computedRotation.mul(scene.camera.inverseRotation);
			}

			this.convertBasis(computedRotation);
		}
		else {
			computedRotation = rotationHeap.set(this.localRotation);
			if (!Float.isNaN(blendTimeRatio) && (blendTimeRatio > 0)) {
				computedRotation.slerp(this.localBlendRotation, blendTimeRatio);
			}

			if (this.billboardedX) {
				if (computedScaling == this.localScale) {
					computedScaling = scalingHeap.set(computedScaling);
				}
				// It took me many hours to deduce from playing around that this negative one
				// multiplier should be here. I suggest a lot of testing before you remove it.
				computedScaling.z *= -1;

				final Camera camera = scene.camera;
				cameraRayHeap.set(camera.billboardedVectors[6]);

				rotationHeap2.set(computedRotation);
				// Inverse that local rotation
				rotationHeap2.x = -rotationHeap2.x;
				rotationHeap2.y = -rotationHeap2.y;
				rotationHeap2.z = -rotationHeap2.z;

				rotationHeap2.mul(this.parent.inverseWorldRotation);

				rotationHeap2.transform(cameraRayHeap);

				billboardAxisHeap.set(1, 0, 0);
				final float angle = (float) Math.atan2(cameraRayHeap.z, cameraRayHeap.y);
				rotationHeap2.setFromAxisRad(billboardAxisHeap, angle);

				RenderMathUtils.mul(computedRotation, computedRotation, rotationHeap2);
			}
			else if (this.billboardedY) {
				final Camera camera = scene.camera;
				cameraRayHeap.set(camera.billboardedVectors[6]);

				rotationHeap2.set(computedRotation);
				// Inverse that local rotation
				rotationHeap2.x = -rotationHeap2.x;
				rotationHeap2.y = -rotationHeap2.y;
				rotationHeap2.z = -rotationHeap2.z;

				rotationHeap2.mul(this.parent.inverseWorldRotation);

				rotationHeap2.transform(cameraRayHeap);

				billboardAxisHeap.set(0, 1, 0);
				final float angle = (float) Math.atan2(-cameraRayHeap.z, cameraRayHeap.x);
				rotationHeap2.setFromAxisRad(billboardAxisHeap, angle);

				RenderMathUtils.mul(computedRotation, computedRotation, rotationHeap2);
			}
			else if (this.billboardedZ) {
				final Camera camera = scene.camera;
				cameraRayHeap.set(camera.billboardedVectors[6]);

				rotationHeap2.set(computedRotation);
				// Inverse that local rotation
				rotationHeap2.x = -rotationHeap2.x;
				rotationHeap2.y = -rotationHeap2.y;
				rotationHeap2.z = -rotationHeap2.z;

				rotationHeap2.mul(this.parent.inverseWorldRotation);

				rotationHeap2.transform(cameraRayHeap);

				billboardAxisHeap.set(0, 0, 1);
				final float angle = (float) Math.atan2(cameraRayHeap.y, cameraRayHeap.x);
				rotationHeap2.setFromAxisRad(billboardAxisHeap, angle);

				RenderMathUtils.mul(computedRotation, computedRotation, rotationHeap2);
			}
			else if (this.dontInheritRotation) {
				RenderMathUtils.mul(computedRotation, this.parent.inverseWorldRotation, computedRotation);
			}
		}

		if (!Float.isNaN(blendTimeRatio) && (blendTimeRatio > 0)) {
			computedLocation = blendLocationHeap.set(this.localLocation).scl(inverseBlendRatio)
					.add(blendHeap.set(this.localBlendLocation).scl(blendTimeRatio));
		}
		else {
			computedLocation = this.localLocation;
		}
		RenderMathUtils.fromRotationTranslationScaleOrigin(computedRotation, computedLocation, computedScaling,
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
		this.worldLocation.x = (this.worldMatrix.val[Matrix4.M00] * x) + (this.worldMatrix.val[Matrix4.M01] * y)
				+ (this.worldMatrix.val[Matrix4.M02] * z) + this.worldMatrix.val[Matrix4.M03];
		this.worldLocation.y = (this.worldMatrix.val[Matrix4.M10] * x) + (this.worldMatrix.val[Matrix4.M11] * y)
				+ (this.worldMatrix.val[Matrix4.M12] * z) + this.worldMatrix.val[Matrix4.M13];
		this.worldLocation.z = (this.worldMatrix.val[Matrix4.M20] * x) + (this.worldMatrix.val[Matrix4.M21] * y)
				+ (this.worldMatrix.val[Matrix4.M22] * z) + this.worldMatrix.val[Matrix4.M23];

		// Inverse world location
		this.inverseWorldLocation.x = -this.worldLocation.x;
		this.inverseWorldLocation.y = -this.worldLocation.y;
		this.inverseWorldLocation.z = -this.worldLocation.z;
	}

	public void beginBlending() {
		this.localBlendLocation.set(this.localLocation);
		this.localBlendRotation.set(this.localRotation);
		this.localBlendScale.set(this.localScale);
	}

	public void updateChildren(final float dt, final Scene scene) {
		for (int i = 0, l = this.children.size(); i < l; i++) {
			this.children.get(i).update(dt, scene);
		}
	}

	protected abstract void convertBasis(Quaternion computedRotation);
}
