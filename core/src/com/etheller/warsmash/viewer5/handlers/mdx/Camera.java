package com.etheller.warsmash.viewer5.handlers.mdx;

import com.etheller.warsmash.parsers.mdlx.AnimationMap;

public class Camera extends AnimatedObject {

	private final String name;
	private final float[] position;
	private final float fieldOfView;
	private final float farClippingPlane;
	private final float nearClippingPlane;
	private final float[] targetPosition;

	public Camera(final MdxModel model, final com.etheller.warsmash.parsers.mdlx.Camera camera) {
		super(model, camera);

		this.name = camera.getName();
		this.position = camera.getPosition();
		this.fieldOfView = camera.getFieldOfView();
		this.farClippingPlane = camera.getFarClippingPlane();
		this.nearClippingPlane = camera.getNearClippingPlane();
		this.targetPosition = camera.getTargetPosition();
	}

	public int getPositionTranslation(final float[] out, final MdxComplexInstance instance) {
		return this.getVectorValue(out, AnimationMap.KCTR.getWar3id(), instance, this.position);
	}

	public int getTargetTranslation(final float[] out, final MdxComplexInstance instance) {
		return this.getVectorValue(out, AnimationMap.KTTR.getWar3id(), instance, this.targetPosition);
	}

	public int getRotation(final float[] out, final MdxComplexInstance instance) {
		return this.getScalarValue(out, AnimationMap.KCRL.getWar3id(), instance, 0);
	}

}
