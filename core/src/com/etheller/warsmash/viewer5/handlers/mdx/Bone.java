package com.etheller.warsmash.viewer5.handlers.mdx;

public class Bone extends GenericObject {

	private final GeosetAnimation geosetAnimation;

	public Bone(final MdxModel model, final com.etheller.warsmash.parsers.mdlx.Bone bone, final int index) {
		super(model, bone, index);

		this.geosetAnimation = model.getGeosetAnimations().get(bone.getGeosetAnimationId());
	}

	@Override
	public int getVisibility(final float[] out, final MdxComplexInstance instance) {
		if (this.geosetAnimation != null) {
			return this.geosetAnimation.getAlpha(out, instance);
		}

		out[0] = 1;

		return -1;
	}

}
