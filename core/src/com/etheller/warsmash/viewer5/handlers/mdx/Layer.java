package com.etheller.warsmash.viewer5.handlers.mdx;

import com.etheller.warsmash.parsers.mdlx.Layer.FilterMode;

public class Layer {
	public Model model;
	public com.etheller.warsmash.parsers.mdlx.Layer layer;
	public int layerId;
	public int priorityPlane;

	public int filterMode;
	public int textureId;
	public int coordId;
	public float alpha;

	public Layer(final Model model, final com.etheller.warsmash.parsers.mdlx.Layer layer, final int layerId,
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
	}

}
