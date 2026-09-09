package com.etheller.warsmash.viewer5.handlers.w3x.camera;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.etheller.warsmash.viewer5.Camera;
import com.etheller.warsmash.viewer5.CanvasProvider;
import com.etheller.warsmash.viewer5.Scene;
import com.etheller.warsmash.viewer5.handlers.w3x.War3MapViewer;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.RenderUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.thirdperson.CAbilityPlayerPawn;

public final class ThirdPersonCameraManager extends CameraManager {
	private final float fov = 84.4f;
	private final float nearZ = 15;
	private final float farZ = 25000;
	private boolean touchDown;
	private final War3MapViewer war3MapViewer;
	private final RenderUnit pawnUnit;
	private final CAbilityPlayerPawn abilityPlayerPawn;
	private Camera predictedCamera;
	protected Vector3 positionTest1;
	protected Vector3 positionTest2;
	protected Vector3 positionTest3;
	protected Vector3 positionTest4;

	public ThirdPersonCameraManager(final RenderUnit pawnUnit, final CAbilityPlayerPawn abilityPlayerPawn,
			final War3MapViewer war3MapViewer) {
		this.pawnUnit = pawnUnit;
		this.abilityPlayerPawn = abilityPlayerPawn;
		this.war3MapViewer = war3MapViewer;
	}

	@Override
	public void setupCamera(final Scene scene) {
		this.positionTest1 = new Vector3();
		this.positionTest2 = new Vector3();
		this.positionTest3 = new Vector3();
		this.positionTest4 = new Vector3();
		this.predictedCamera = new Camera();
		final CanvasProvider canvas2 = scene.viewer.canvas;
		this.predictedCamera.viewport(new Rectangle(0, 0, canvas2.getWidth(), canvas2.getHeight()));
		this.predictedCamera.perspective((float) (Math.PI / 4), canvas2.getWidth() / canvas2.getHeight(), 8, 10000);
		super.setupCamera(scene);
		this.distance = 300;
		// Use the whole canvas, and standard perspective projection values.

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
		this.vecHeap.set(this.position);
		this.position.scl(this.distance);
		this.target.set(this.pawnUnit.getX(), this.pawnUnit.getY(), this.pawnUnit.getZ());
		this.target.z += this.abilityPlayerPawn.getBehaviorPlayerPawn().getHeight();
		this.position = this.position.add(this.target);

		this.predictedCamera.perspective((float) Math.toRadians(this.fov), this.camera.getAspect(), this.nearZ,
				this.farZ);

		this.predictedCamera.moveToAndFace(this.position, this.target, this.worldUp);
		this.predictedCamera.update();

		this.positionTest1.set(5, 5, 0);
		this.predictedCamera.cameraToWorld(this.positionTest1, this.positionTest1);
		this.positionTest2.set(5, -5, 0);
		this.predictedCamera.cameraToWorld(this.positionTest2, this.positionTest2);
		this.positionTest3.set(-5, -5, 0);
		this.predictedCamera.cameraToWorld(this.positionTest3, this.positionTest3);
		this.positionTest4.set(-5, 5, 0);
		this.predictedCamera.cameraToWorld(this.positionTest4, this.positionTest4);
		boolean rayTestingHit = false;
		this.vecHeap.set(this.positionTest1);
		this.vecHeap.sub(this.target);
		final float hConvertRatio = this.distance / this.vecHeap.len();
		float minDistance = this.distance;
		if (this.war3MapViewer.rayTest(this.target, this.positionTest1, this.positionTest1)) {
			final float h = this.positionTest1.sub(this.target).len();
			final float converted = h * hConvertRatio;
			if (converted < minDistance) {
				minDistance = converted;
				rayTestingHit = true;
			}
		}
		if (this.war3MapViewer.rayTest(this.target, this.positionTest2, this.positionTest2)) {
			final float h = this.positionTest2.sub(this.target).len();
			final float converted = h * hConvertRatio;
			if (converted < minDistance) {
				minDistance = converted;
				rayTestingHit = true;
			}
		}
		if (this.war3MapViewer.rayTest(this.target, this.positionTest3, this.positionTest3)) {
			final float h = this.positionTest3.sub(this.target).len();
			final float converted = h * hConvertRatio;
			if (converted < minDistance) {
				minDistance = converted;
				rayTestingHit = true;
			}
		}
		if (this.war3MapViewer.rayTest(this.target, this.positionTest4, this.positionTest4)) {
			final float h = this.positionTest4.sub(this.target).len();
			final float converted = h * hConvertRatio;
			if (converted < minDistance) {
				minDistance = converted;
				rayTestingHit = true;
			}
		}
		if (rayTestingHit) {
			this.position.set(0, 0, 1);
			this.quatHeap.transform(this.position);
			this.position.nor();
			this.position.scl(minDistance);
			this.position = this.position.add(this.target);
		}
		this.war3MapViewer.rayTest(this.target, this.position, this.position);
		this.camera.perspective((float) Math.toRadians(this.fov), this.camera.getAspect(), this.nearZ, this.farZ);

		this.camera.moveToAndFace(this.position, this.target, this.worldUp);
	}

	public void resize(final Rectangle viewport) {
		this.camera.viewport(viewport);
	}

	public void setTouchDown(final boolean touchDown) {
		this.touchDown = touchDown;
	}

	public boolean isTouchDown() {
		return this.touchDown;
	}

	public float getFov() {
		return this.fov;
	}

	public float getNearZ() {
		return this.nearZ;
	}

	public float getFarZ() {
		return this.farZ;
	}

}