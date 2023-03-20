package com.etheller.warsmash.viewer5.handlers.w3x.camera;

import com.badlogic.gdx.math.Rectangle;
import com.etheller.warsmash.viewer5.Scene;
import com.etheller.warsmash.viewer5.handlers.w3x.War3MapViewer;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.RenderUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.thirdperson.CAbilityPlayerPawn;

public final class ThirdPersonCameraManager extends CameraManager {
	private final float fov = 70;
	private final float nearZ = 50;
	private final float farZ = 5000;
	private boolean touchDown;
	private final War3MapViewer war3MapViewer;
	private final RenderUnit pawnUnit;
	private final CAbilityPlayerPawn abilityPlayerPawn;

	public ThirdPersonCameraManager(final RenderUnit pawnUnit, final CAbilityPlayerPawn abilityPlayerPawn,
			final War3MapViewer war3MapViewer) {
		this.pawnUnit = pawnUnit;
		this.abilityPlayerPawn = abilityPlayerPawn;
		this.war3MapViewer = war3MapViewer;
	}

	@Override
	public void setupCamera(final Scene scene) {
		super.setupCamera(scene);
		this.distance = 300;
	}

	@Override
	public void updateCamera() {
		final CameraPanControls cameraPanControls = this.abilityPlayerPawn.getBehaviorPlayerPawn()
				.getCameraPanControls();
		if ((cameraPanControls.isAnyArrowPressed()) && !this.touchDown) {
			float destAngle = (float) Math.toRadians(this.pawnUnit.getFacing());
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
		this.target.set(this.pawnUnit.getX(), this.pawnUnit.getY(), this.pawnUnit.getZ());
		this.target.z += this.abilityPlayerPawn.getBehaviorPlayerPawn().getHeight();
		this.position = this.position.add(this.target);
		this.war3MapViewer.rayTest(this.target, this.position, this.position);

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