package com.etheller.warsmash.viewer5.handlers.w3x.camera;

public class CameraPreset {
	private final float aoa;
	private final float fov;
	private final float rotation;
	private final float rotationInsert;
	private final float rotationDelete;
	private final float distance;
	private final float farZ;
	private final float nearZ;
	private final float height;
	private final float listenerDistance;
	private final float listenerAOA;

	public CameraPreset(final float aoa, final float fov, final float rotation, final float rotationInsert,
			final float rotationDelete, final float distance, final float farZ, final float nearZ, final float height,
			final float listenerDistance, final float listenerAOA) {
		this.aoa = aoa;
		this.fov = fov;
		this.rotation = rotation;
		this.rotationInsert = rotationInsert;
		this.rotationDelete = rotationDelete;
		this.distance = distance;
		this.farZ = farZ;
		this.nearZ = nearZ;
		this.height = height;
		this.listenerDistance = listenerDistance;
		this.listenerAOA = listenerAOA;
	}

	public float getRotation(final boolean insertDown, final boolean deleteDown) {
		if (insertDown && !deleteDown) {
			return this.rotationInsert;
		}
		if (!insertDown && deleteDown) {
			return this.rotationDelete;
		}
		return this.rotation;
	}

	public float getHeight() {
		return this.height;
	}

	public float getAoa() {
		return this.aoa;
	}

	public float getFov() {
		return this.fov;
	}

	public float getRotation() {
		return this.rotation;
	}

	public float getRotationInsert() {
		return this.rotationInsert;
	}

	public float getRotationDelete() {
		return this.rotationDelete;
	}

	public float getDistance() {
		return this.distance;
	}

	public float getFarZ() {
		return this.farZ;
	}

	public float getNearZ() {
		return this.nearZ;
	}

	public float getListenerDistance() {
		return this.listenerDistance;
	}

	public float getListenerAOA() {
		return this.listenerAOA;
	}
}