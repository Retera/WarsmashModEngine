package com.etheller.warsmash.viewer5.handlers.w3x.camera;

public class CameraRates {
	public final float aoa;
	public final float fov;
	public final float rotation;
	public final float distance;
	public final float forward;
	public final float strafe;

	public CameraRates(final float aoa, final float fov, final float rotation, final float distance,
			final float forward, final float strafe) {
		this.aoa = aoa;
		this.fov = fov;
		this.rotation = rotation;
		this.distance = distance;
		this.forward = forward;
		this.strafe = strafe;
	}

	public static final CameraRates INFINITY = new CameraRates(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY,
			Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY);
}
