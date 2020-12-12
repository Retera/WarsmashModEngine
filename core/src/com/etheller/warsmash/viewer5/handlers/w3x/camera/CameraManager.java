package com.etheller.warsmash.viewer5.handlers.w3x.camera;

import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.etheller.warsmash.viewer5.Camera;
import com.etheller.warsmash.viewer5.CanvasProvider;
import com.etheller.warsmash.viewer5.Scene;

public abstract class CameraManager {
	private static final double HORIZONTAL_ANGLE_INCREMENT = Math.PI / 60;
	protected final float[] cameraPositionTemp = new float[3];
	protected final float[] cameraTargetTemp = new float[3];
	protected CanvasProvider canvas;
	public Camera camera;
	protected float moveSpeed;
	protected float rotationSpeed;
	protected float zoomFactor;
	public float horizontalAngle;
	public float verticalAngle;
	public float distance;
	protected Vector3 position;
	public Vector3 target;
	protected Vector3 worldUp;
	protected Vector3 vecHeap;
	protected Quaternion quatHeap;
	protected Quaternion quatHeap2;

	public CameraManager() {
	}

	// An orbit camera setup example.
	// Left mouse button controls the orbit itself.
	// The right mouse button allows to move the camera and the point it's looking
	// at on the XY plane.
	// Scrolling zooms in and out.
	public void setupCamera(final Scene scene) {
		this.canvas = scene.viewer.canvas;
		this.camera = scene.camera;
		this.moveSpeed = 2;
		this.rotationSpeed = (float) HORIZONTAL_ANGLE_INCREMENT;
		this.zoomFactor = 0.1f;
		this.horizontalAngle = 0;// (float) (Math.PI / 2);
		this.verticalAngle = (float) Math.toRadians(34);
		this.distance = 1650;
		this.position = new Vector3();
		this.target = new Vector3(0, 0, 0);
		this.worldUp = new Vector3(0, 0, 1);
		this.vecHeap = new Vector3();
		this.quatHeap = new Quaternion();
		this.quatHeap2 = new Quaternion();

		updateCamera();

//		cameraUpdate();
	}

	public abstract void updateCamera();

//	private void cameraUpdate() {
//
//	}
}