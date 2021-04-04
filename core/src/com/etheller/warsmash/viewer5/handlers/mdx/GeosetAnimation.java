package com.etheller.warsmash.viewer5.handlers.mdx;

import com.hiveworkshop.rms.parsers.mdlx.AnimationMap;
import com.hiveworkshop.rms.parsers.mdlx.MdlxGeosetAnimation;

public class GeosetAnimation extends AnimatedObject {

	private final float alpha;
	private final float[] color;
	public final int geosetId;

	public GeosetAnimation(final MdxModel model, final MdlxGeosetAnimation geosetAnimation) {
		super(model, geosetAnimation);

		final float[] color = geosetAnimation.getColor();

		this.alpha = geosetAnimation.getAlpha();
		this.color = new float[] { color[2], color[1], color[0] }; // Stored as RGB, but animated colors are stored as
																	// BGR, so sizzle.
		this.geosetId = geosetAnimation.getGeosetId();

		this.addVariants(AnimationMap.KGAO.getWar3id(), "alpha");
		this.addVariants(AnimationMap.KGAC.getWar3id(), "color");
	}

	public int getAlpha(final float[] out, final int sequence, final int frame, final int counter) {
		return this.getScalarValue(out, AnimationMap.KGAO.getWar3id(), sequence, frame, counter, this.alpha);
	}

	public int getColor(final float[] out, final int sequence, final int frame, final int counter) {
		return this.getVectorValue(out, AnimationMap.KGAC.getWar3id(), sequence, frame, counter, this.color);
	}

	public boolean isAlphaVariant(final int sequence) {
		return this.isVariant(AnimationMap.KGAO.getWar3id(), sequence);
	}

	public boolean isColorVariant(final int sequence) {
		return this.isVariant(AnimationMap.KGAC.getWar3id(), sequence);
	}
}
