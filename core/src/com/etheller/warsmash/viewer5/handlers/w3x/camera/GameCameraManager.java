package com.etheller.warsmash.viewer5.handlers.w3x.camera;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Rectangle;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.RenderUnit;

public final class GameCameraManager extends CameraManager {
	private static final CameraRates INFINITE_CAMERA_RATES = new CameraRates(Float.POSITIVE_INFINITY,
			Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY,
			Float.POSITIVE_INFINITY);
	private final CameraPreset[] presets;
	private final CameraRates cameraRates;
	public final CameraPanControls cameraPanControls;
	private Rectangle cameraBounds;
	private int currentPreset = 0;
	private float fov;
	private RenderUnit targetControllerUnit;
	private float targetControllerXOffset;
	private float targetControllerYOffset;
	private boolean targetControllerInheritOrientation;

	public GameCameraManager(final CameraPreset[] presets, final CameraRates cameraRates) {
		this.presets = presets;
		this.cameraRates = cameraRates;
		this.cameraPanControls = new CameraPanControls();
	}

	public void setCameraBounds(final Rectangle cameraBounds) {
		this.cameraBounds = cameraBounds;
	}

	@Override
	public void updateCamera() {
		final CameraPreset cameraPreset = this.presets[this.currentPreset];
		final CameraRates cameraRate = this.cameraRates;
		updateCamera(cameraPreset, cameraRate);
	}

	private void updateCamera(final CameraPreset cameraPreset, final CameraRates cameraRate) {
		this.quatHeap2.idt();
		this.quatHeap.idt();
		final float newHorizontalAngle;
		if (this.targetControllerInheritOrientation && (this.targetControllerUnit != null)) {
			newHorizontalAngle = this.targetControllerUnit.getFacing();
		}
		else {
			newHorizontalAngle = (float) Math.toRadians(
					cameraPreset.getRotation(this.cameraPanControls.insertDown, this.cameraPanControls.deleteDown)
							- 90);
		}
		this.horizontalAngle = applyAtRate(this.horizontalAngle, newHorizontalAngle,
				(float) Math.toRadians(cameraRate.rotation * 3));
		this.quatHeap.setFromAxisRad(0, 0, 1, this.horizontalAngle);
		this.distance = applyAtRate(this.distance, Math.max(1200, cameraPreset.getDistance()), cameraRate.distance);
		this.verticalAngle = applyAtRate(this.verticalAngle,
				(float) Math.toRadians(Math.min(335, cameraPreset.getAoa()) - 270),
				(float) Math.toRadians(cameraRate.aoa));
		this.quatHeap2.setFromAxisRad(1, 0, 0, this.verticalAngle);
		this.quatHeap.mul(this.quatHeap2);

		this.position.set(0, 0, 1);
		this.quatHeap.transform(this.position);
		this.position.nor();
		this.position.scl(this.distance);
		this.position = this.position.add(this.target);
		this.fov = applyAtRate(this.fov, (float) Math.toRadians(cameraPreset.getFov() / 2),
				(float) Math.toRadians(cameraRate.fov));
		this.camera.perspective(this.fov, this.camera.getAspect(), cameraPreset.getNearZ(), cameraPreset.getFarZ());

		this.camera.moveToAndFace(this.position, this.target, this.worldUp);
	}

	private static float applyAtRate(final float oldValue, final float newValue, float rate) {
		rate *= Gdx.graphics.getDeltaTime();
		final float deltaDistance = newValue - oldValue;
		if (Math.abs(deltaDistance) < rate) {
			return newValue;
		}
		else {
			return oldValue + (Math.signum(deltaDistance) * rate);
		}
	}

	public void resize(final Rectangle viewport) {
		this.camera.viewport(viewport);
	}

	public void applyVelocity(final float deltaTime, boolean up, boolean down, boolean left, boolean right) {
		if (this.targetControllerUnit != null) {
			this.target.x = this.targetControllerUnit.getX() + this.targetControllerXOffset;
			this.target.y = this.targetControllerUnit.getY() + this.targetControllerYOffset;
		}
		else {
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
		if (this.cameraBounds != null) {
			if (this.target.x < this.cameraBounds.x) {
				this.target.x = this.cameraBounds.x;
			}
			if (this.target.y < this.cameraBounds.y) {
				this.target.y = this.cameraBounds.y;
			}
			if (this.target.x > (this.cameraBounds.x + this.cameraBounds.width)) {
				this.target.x = this.cameraBounds.x + this.cameraBounds.width;
			}
			if (this.target.y > (this.cameraBounds.y + this.cameraBounds.height)) {
				this.target.y = this.cameraBounds.y + this.cameraBounds.height;
			}
		}

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

	public Rectangle getCameraBounds() {
		return this.cameraBounds;
	}

	public void setTargetController(final RenderUnit targetControllerUnit, final float xoffset, final float yoffset,
			final boolean inheritOrientation) {
		this.targetControllerUnit = targetControllerUnit;
		this.targetControllerXOffset = xoffset;
		this.targetControllerYOffset = yoffset;
		this.targetControllerInheritOrientation = inheritOrientation;

	}
}