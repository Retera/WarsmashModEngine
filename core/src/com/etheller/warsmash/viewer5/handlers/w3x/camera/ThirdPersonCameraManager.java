package com.etheller.warsmash.viewer5.handlers.w3x.camera;

import com.badlogic.gdx.math.Rectangle;
import com.etheller.warsmash.viewer5.Scene;
import com.etheller.warsmash.viewer5.handlers.w3x.ui.thirdperson.PlayerPawn;

public final class ThirdPersonCameraManager extends CameraManager {
	private final float fov = 70;
	private final float nearZ = 100;
	private final float farZ = 5000;
	private final PlayerPawn playerPawn;
	private boolean touchDown;

	public ThirdPersonCameraManager(final PlayerPawn playerPawn) {
		this.playerPawn = playerPawn;
	}

	@Override
	public void setupCamera(final Scene scene) {
		super.setupCamera(scene);
		this.distance = 300;
	}

	@Override
	public void updateCamera() {
		final CameraPanControls cameraPanControls = this.playerPawn.getCameraPanControls();
		if ((cameraPanControls.isAnyArrowPressed()) && !this.touchDown) {
			float destAngle = (float) Math.toRadians(this.playerPawn.getFacingDegrees());
			if (destAngle > Math.PI) {
				destAngle -= Math.PI * 2;
			}
			if (destAngle < -Math.PI) {
				destAngle += Math.PI * 2;
			}
			if (this.horizontalAngle > Math.PI) {
				this.horizontalAngle -= Math.PI * 2;
			}
			if (this.horizontalAngle < -Math.PI) {
				this.horizontalAngle += Math.PI * 2;
			}
			float deltaAngle = destAngle - this.horizontalAngle;
			if (deltaAngle > Math.PI) {
				deltaAngle -= Math.PI * 2;
			}
			if (deltaAngle < -Math.PI) {
				deltaAngle += Math.PI * 2;
			}
			this.horizontalAngle += deltaAngle / 2;
		}
		this.quatHeap.idt();
		this.quatHeap.setFromAxisRad(0, 0, 1, this.horizontalAngle);
		this.quatHeap2.idt();
		this.quatHeap2.setFromAxisRad(0, 1, 0, -this.verticalAngle);
		this.quatHeap.mul(this.quatHeap2);

		this.position.set(0, 0, 1);
		this.quatHeap.transform(this.position);
		this.position.nor();
		this.position.scl(this.distance);
		this.target.set(this.playerPawn.getLocation());
		this.target.z += this.playerPawn.getHeight();
		this.position = this.position.add(this.target);
		this.camera.perspective(this.fov, this.camera.getAspect(), this.nearZ, this.farZ);

		this.camera.moveToAndFace(this.position, this.target, this.worldUp);
	}

	public void resize(final Rectangle viewport) {
		this.camera.viewport(viewport);
	}

	public void setTouchDown(final boolean touchDown) {
		this.touchDown = touchDown;
	}

}