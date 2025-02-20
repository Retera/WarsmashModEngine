package com.etheller.warsmash.viewer5.handlers.w3x.rendersim;

import com.badlogic.gdx.math.Vector3;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxModel;

public class RenderItemType {

	private final MdxModel model;
	private final MdxModel portraitModel;
	private final RenderShadowType renderShadowType;
	private final Vector3 tintingColor;
	private final float modelScale;

	public RenderItemType(MdxModel model, MdxModel portraitModel, RenderShadowType renderShadowType,
			Vector3 tintingColor, float modelScale) {
		this.model = model;
		this.portraitModel = portraitModel;
		this.renderShadowType = renderShadowType;
		this.tintingColor = tintingColor;
		this.modelScale = modelScale;
	}

	public MdxModel getModel() {
		return this.model;
	}

	public MdxModel getPortraitModel() {
		return this.portraitModel;
	}

	public RenderShadowType getRenderShadowType() {
		return this.renderShadowType;
	}

	public Vector3 getTintingColor() {
		return this.tintingColor;
	}

	public float getModelScale() {
		return this.modelScale;
	}

}
