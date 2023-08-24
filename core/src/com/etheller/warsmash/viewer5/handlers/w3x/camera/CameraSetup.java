package com.etheller.warsmash.viewer5.handlers.w3x.camera;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CCameraField;

public class CameraSetup {
	private float aoa;
	private float fov;
	private float rotation;
	private float roll;
	private float distance;
	private float farZ;
	private float nearZ;
	private float height;

	public CameraSetup(final float aoa, final float fov, final float rotation, final float roll, final float distance,
			final float farZ, final float nearZ, final float height) {
		this.aoa = aoa;
		this.fov = fov;
		this.rotation = rotation;
		this.roll = roll;
		this.distance = distance;
		this.farZ = farZ;
		this.nearZ = nearZ;
		this.height = height;
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

	public float getRoll() {
		return this.roll;
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

	public void setAoa(final float aoa) {
		this.aoa = aoa;
	}

	public void setFov(final float fov) {
		this.fov = fov;
	}

	public void setRotation(final float rotation) {
		this.rotation = rotation;
	}

	public void setRoll(final float roll) {
		this.roll = roll;
	}

	public void setDistance(final float distance) {
		this.distance = distance;
	}

	public void setFarZ(final float farZ) {
		this.farZ = farZ;
	}

	public void setNearZ(final float nearZ) {
		this.nearZ = nearZ;
	}

	public void setHeight(final float height) {
		this.height = height;
	}

	public void setField(final CCameraField field, final float value) {
		switch (field) {
		case ANGLE_OF_ATTACK:
			this.aoa = value;
			break;
		case FIELD_OF_VIEW:
			this.fov = value;
			break;
		case TARGET_DISTANCE:
			this.distance = value;
			break;
		case FARZ:
			this.farZ = value;
			break;
		case NEARZ:
			this.nearZ = value;
			break;
		case ROTATION:
			this.rotation = value;
			break;
		case ROLL:
			this.roll = value;
			break;
		case ZOFFSET:
			this.height = value;
			break;
		case LOCAL_PITCH:
		case LOCAL_ROLL:
		case LOCAL_YAW:
		default:
			break;
		}
	}

	public float getField(final CCameraField field) {
		switch (field) {
		case ANGLE_OF_ATTACK:
			return this.aoa;
		case FIELD_OF_VIEW:
			return this.fov;
		case TARGET_DISTANCE:
			return this.distance;
		case FARZ:
			return this.farZ;
		case NEARZ:
			return this.nearZ;
		case ROTATION:
			return this.rotation;
		case ROLL:
			return this.roll;
		case ZOFFSET:
			return this.height;
		case LOCAL_PITCH:
		case LOCAL_ROLL:
		case LOCAL_YAW:
		default:
			return 0.0f; // TODO ?
		}
	}

}