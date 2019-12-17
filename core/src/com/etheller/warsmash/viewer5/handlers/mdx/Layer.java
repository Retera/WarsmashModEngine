package com.etheller.warsmash.viewer5.handlers.mdx;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.etheller.warsmash.parsers.mdlx.Layer.FilterMode;

public class Layer {
	public MdxModel model;
	public com.etheller.warsmash.parsers.mdlx.Layer layer;
	public int layerId;
	public int priorityPlane;

	public int filterMode;
	public int textureId;
	public int coordId;
	public float alpha;

	public int index = -666;

	public Layer(final MdxModel model, final com.etheller.warsmash.parsers.mdlx.Layer layer, final int layerId,
			final int priorityPlane) {
		super(model, layer);
		this.model = model;
		this.layer = layer;
		this.layerId = layerId;
		this.priorityPlane = priorityPlane;

		final FilterMode filterMode2 = layer.getFilterMode();
		this.filterMode = filterMode2.ordinal();
		this.textureId = layer.getTextureId();
		// this.coo
		this.index
	}

	public void bind(final ShaderProgram shader) {
		// TODO Auto-generated method stub

	}

}
