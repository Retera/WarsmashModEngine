package com.etheller.warsmash.viewer5.handlers.mdx;

import com.etheller.warsmash.parsers.mdlx.AnimationMap;
import com.etheller.warsmash.util.RenderMathUtils;

public class TextureAnimation extends AnimatedObject {

	public TextureAnimation(final MdxModel model,
			final com.etheller.warsmash.parsers.mdlx.TextureAnimation textureAnimation) {
		super(model, textureAnimation);
	}

	public int getTranslation(final float[] out, final MdxComplexInstance instance) {
		return this.getVectorValue(out, AnimationMap.KTAT.getWar3id(), instance, RenderMathUtils.FLOAT_VEC3_ZERO);
	}

	public int getRotation(final float[] out, final MdxComplexInstance instance) {
		return this.getVectorValue(out, AnimationMap.KTAR.getWar3id(), instance, RenderMathUtils.FLOAT_QUAT_DEFAULT);
	}

	public int getScale(final float[] out, final MdxComplexInstance instance) {
		return this.getVectorValue(out, AnimationMap.KTAS.getWar3id(), instance, RenderMathUtils.FLOAT_VEC3_ONE);
	}

	public boolean isTranslationVariant(final int sequence) {
		return this.isVariant(AnimationMap.KTAT.getWar3id(), sequence);
	}

	public boolean isRotationVariant(final int sequence) {
		return this.isVariant(AnimationMap.KTAR.getWar3id(), sequence);
	}

	public boolean isScaleVariant(final int sequence) {
		return this.isVariant(AnimationMap.KTAS.getWar3id(), sequence);
	}

}
