package com.etheller.warsmash.viewer5.handlers.w3x.camera;

public class CustomCameraSetup extends CameraSetup {
	private float destPositionX;
	private float destPositionY;

	public CustomCameraSetup(final float aoa, final float fov, final float rotation, final float roll,
			final float distance, final float farZ, final float nearZ, final float height) {
		super(aoa, fov, rotation, roll, distance, farZ, nearZ, height);
	}

	public void setDestPositionX(final float destPositionX) {
		this.destPositionX = destPositionX;
	}

	public void setDestPositionY(final float destPositionY) {
		this.destPositionY = destPositionY;
	}

	public float getDestPositionX() {
		return this.destPositionX;
	}

	public float getDestPositionY() {
		return this.destPositionY;
	}

}
