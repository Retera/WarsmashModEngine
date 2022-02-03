package com.etheller.warsmash.viewer5.handlers.mdx;

public class Batch implements GenericIndexed {
	public int index;
	public Geoset geoset;
	public Layer layer;
	public Material material;
	public final SkinningType skinningType;
	public final boolean hd;

	public Batch(final int index, final Geoset geoset, final Layer layer, final SkinningType skinningType) {
		this.index = index;
		this.geoset = geoset;
		this.layer = layer;
		this.material = null;
		this.skinningType = skinningType;
		this.hd = false;
	}

	public Batch(final int index, final Geoset geoset, final Material material, final SkinningType skinningType) {
		this.index = index;
		this.geoset = geoset;
		this.material = material;
		this.layer = material.layers.get(0);
		this.skinningType = skinningType;
		this.hd = true;
	}

	@Override
	public int getIndex() {
		return this.index;
	}

}
