package com.etheller.warsmash.viewer5.handlers.mdx;

import java.util.List;

public class Material {
	public final MdxModel model;
	public final String shader;
	public final List<Layer> layers;

	public Material(final MdxModel model, final String shader, final List<Layer> layers) {
		this.model = model;
		this.shader = shader;
		this.layers = layers;
	}

}
