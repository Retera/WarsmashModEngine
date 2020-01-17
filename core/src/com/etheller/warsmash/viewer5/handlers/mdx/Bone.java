package com.etheller.warsmash.viewer5.handlers.mdx;

public class Bone extends GenericObject {

	private final GeosetAnimation geosetAnimation;

	public Bone(final MdxModel model, final com.etheller.warsmash.parsers.mdlx.Bone bone, final int index) {
		super(model, bone, index);

		final int geosetAnimationId = bone.getGeosetAnimationId();
		if (geosetAnimationId != -1) {
			this.geosetAnimation = model.getGeosetAnimations().get(geosetAnimationId);
		}
		else {
			this.geosetAnimation = null;
		}
	}

	@Override
	public int getVisibility(final float[] out, final int sequence, final int frame, final int counter) {
		if (this.geosetAnimation != null) {
			return this.geosetAnimation.getAlpha(out, sequence, frame, counter);
		}

		out[0] = 1;

		return -1;
	}

}
