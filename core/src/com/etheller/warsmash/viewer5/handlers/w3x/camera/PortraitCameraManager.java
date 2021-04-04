package com.etheller.warsmash.viewer5.handlers.w3x.camera;

import com.etheller.warsmash.viewer5.handlers.mdx.MdxComplexInstance;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxModel;

public final class PortraitCameraManager extends CameraManager {
	public com.etheller.warsmash.viewer5.handlers.mdx.Camera modelCamera;
	protected MdxComplexInstance modelInstance;

	@Override
	public void updateCamera() {
		this.quatHeap.idt();
		this.quatHeap.setFromAxisRad(0, 0, 1, this.horizontalAngle);
		this.quatHeap2.idt();
		this.quatHeap2.setFromAxisRad(1, 0, 0, this.verticalAngle);
		this.quatHeap.mul(this.quatHeap2);

		this.position.set(0, 0, 1);
		this.quatHeap.transform(this.position);
		this.position.scl(this.distance);
		this.position = this.position.add(this.target);
		if (this.modelCamera != null) {
			this.modelCamera.getPositionTranslation(this.cameraPositionTemp, this.modelInstance.sequence,
					this.modelInstance.frame, this.modelInstance.counter);
			this.modelCamera.getTargetTranslation(this.cameraTargetTemp, this.modelInstance.sequence,
					this.modelInstance.frame, this.modelInstance.counter);

			this.position.set(this.modelCamera.position);
			this.target.set(this.modelCamera.targetPosition);

			this.position.add(this.cameraPositionTemp[0], this.cameraPositionTemp[1], this.cameraPositionTemp[2]);
			this.target.add(this.cameraTargetTemp[0], this.cameraTargetTemp[1], this.cameraTargetTemp[2]);
			this.camera.perspective(this.modelCamera.fieldOfView * 0.75f, this.camera.getAspect(),
					this.modelCamera.nearClippingPlane, this.modelCamera.farClippingPlane);
		}
		else {
			this.camera.perspective(70, this.camera.getAspect(), 100, 5000);
		}

		this.camera.moveToAndFace(this.position, this.target, this.worldUp);
	}

	public void setModelInstance(final MdxComplexInstance modelInstance, final MdxModel portraitModel) {
		this.modelInstance = modelInstance;
		if (modelInstance == null) {
			this.modelCamera = null;
		}
		else if ((portraitModel != null) && (portraitModel.getCameras().size() > 0)) {
			this.modelCamera = portraitModel.getCameras().get(0);
		}
	}
}