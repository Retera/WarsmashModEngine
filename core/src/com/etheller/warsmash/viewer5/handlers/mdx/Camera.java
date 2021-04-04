package com.etheller.warsmash.viewer5.handlers.mdx;

import com.etheller.warsmash.util.RenderMathUtils;
import com.hiveworkshop.rms.parsers.mdlx.AnimationMap;
import com.hiveworkshop.rms.parsers.mdlx.MdlxCamera;

public class Camera extends AnimatedObject {

	public final String name;
	public final float[] position;
	public final float fieldOfView;
	public final float farClippingPlane;
	public final float nearClippingPlane;
	public final float[] targetPosition;

	public Camera(final MdxModel model, final MdlxCamera camera) {
		super(model, camera);

		this.name = camera.getName();
		this.position = camera.getPosition();
		this.fieldOfView = camera.getFieldOfView();
		this.farClippingPlane = camera.getFarClippingPlane();
		this.nearClippingPlane = camera.getNearClippingPlane();
		this.targetPosition = camera.getTargetPosition();
	}

	public int getPositionTranslation(final float[] out, final int sequence, final int frame, final int counter) {
		return this.getVectorValue(out, AnimationMap.KCTR.getWar3id(), sequence, frame, counter,
				RenderMathUtils.FLOAT_VEC3_ZERO);
	}

	public int getTargetTranslation(final float[] out, final int sequence, final int frame, final int counter) {
		return this.getVectorValue(out, AnimationMap.KTTR.getWar3id(), sequence, frame, counter,
				RenderMathUtils.FLOAT_VEC3_ZERO);
	}

	public int getRotation(final float[] out, final int sequence, final int frame, final int counter) {
		return this.getScalarValue(out, AnimationMap.KCRL.getWar3id(), sequence, frame, counter, 0);
	}

}
