package com.etheller.warsmash.viewer5.handlers.w3x.camera;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Rectangle;

public final class GameCameraManager extends CameraManager {
	private final CameraPreset[] presets;
	private final CameraRates cameraRates;
	public final CameraPanControls cameraPanControls;
	private int currentPreset = 0;

	public GameCameraManager(final CameraPreset[] presets, final CameraRates cameraRates) {
		this.presets = presets;
		this.cameraRates = cameraRates;
		this.cameraPanControls = new CameraPanControls();
	}

	@Override
	public void updateCamera() {
		this.quatHeap2.idt();
		final CameraPreset cameraPreset = this.presets[this.currentPreset];
		this.quatHeap.idt();
		this.horizontalAngle = (float) Math.toRadians(
				cameraPreset.getRotation(this.cameraPanControls.insertDown, this.cameraPanControls.deleteDown) - 90);
		this.quatHeap.setFromAxisRad(0, 0, 1, this.horizontalAngle);
		this.distance = Math.max(1200, cameraPreset.getDistance());
		this.verticalAngle = (float) Math.toRadians(Math.min(335, cameraPreset.getAoa()) - 270);
		this.quatHeap2.setFromAxisRad(1, 0, 0, this.verticalAngle);
		this.quatHeap.mul(this.quatHeap2);

		this.position.set(0, 0, 1);
		this.quatHeap.transform(this.position);
		this.position.nor();
		this.position.scl(this.distance);
		this.position = this.position.add(this.target);
		this.camera.perspective((float) Math.toRadians(cameraPreset.getFov() / 2), this.camera.getAspect(),
				cameraPreset.getNearZ(), cameraPreset.getFarZ());

		this.camera.moveToAndFace(this.position, this.target, this.worldUp);
	}

	public void resize(final Rectangle viewport) {
		this.camera.viewport(viewport);
	}

	public void applyVelocity(final float deltaTime, boolean up, boolean down, boolean left, boolean right) {
		final float velocityX;
		final float velocityY;
		up |= this.cameraPanControls.up;
		down |= this.cameraPanControls.down;
		left |= this.cameraPanControls.left;
		right |= this.cameraPanControls.right;
		if (up) {
			if (down) {
				velocityY = 0;
			}
			else {
				velocityY = this.cameraRates.forward;
			}
		}
		else if (down) {
			velocityY = -this.cameraRates.forward;
		}
		else {
			velocityY = 0;
		}
		if (right) {
			if (left) {
				velocityX = 0;
			}
			else {
				velocityX = this.cameraRates.strafe;
			}
		}
		else if (left) {
			velocityX = -this.cameraRates.strafe;
		}
		else {
			velocityX = 0;
		}
		this.target.add(velocityX * deltaTime, velocityY * deltaTime, 0);

	}

	public void updateTargetZ(final float groundHeight) {
		this.target.z = groundHeight + this.presets[this.currentPreset].getHeight();
	}

	public void scrolled(final int amount) {
		this.currentPreset -= amount;
		if (this.currentPreset < 0) {
			this.currentPreset = 0;
		}
		if (this.currentPreset >= this.presets.length) {
			this.currentPreset = this.presets.length - 1;
		}
	}

	public boolean keyDown(final int keycode) {
		if (keycode == Input.Keys.LEFT) {
			this.cameraPanControls.left = true;
			return true;
		}
		else if (keycode == Input.Keys.RIGHT) {
			this.cameraPanControls.right = true;
			return true;
		}
		else if (keycode == Input.Keys.DOWN) {
			this.cameraPanControls.down = true;
			return true;
		}
		else if (keycode == Input.Keys.UP) {
			this.cameraPanControls.up = true;
			return true;
		}
		else if (keycode == Input.Keys.INSERT) {
			this.cameraPanControls.insertDown = true;
			return true;
		}
		else if (keycode == Input.Keys.FORWARD_DEL) {
			this.cameraPanControls.deleteDown = true;
			return true;
		}
		return false;
	}

	public boolean keyUp(final int keycode) {
		if (keycode == Input.Keys.LEFT) {
			this.cameraPanControls.left = false;
			return true;
		}
		else if (keycode == Input.Keys.RIGHT) {
			this.cameraPanControls.right = false;
			return true;
		}
		else if (keycode == Input.Keys.DOWN) {
			this.cameraPanControls.down = false;
			return true;
		}
		else if (keycode == Input.Keys.UP) {
			this.cameraPanControls.up = false;
			return true;
		}
		else if (keycode == Input.Keys.INSERT) {
			this.cameraPanControls.insertDown = false;
			return true;
		}
		else if (keycode == Input.Keys.FORWARD_DEL) {
			this.cameraPanControls.deleteDown = false;
			return true;
		}
		return false;
	}
}