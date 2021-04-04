package com.etheller.warsmash.viewer5.handlers.mdx;

public class Batch implements GenericIndexed {
	public int index;
	public Geoset geoset;
	public Layer layer;
	public boolean isExtended;

	public Batch(final int index, final Geoset geoset, final Layer layer, final boolean isExtended) {
		this.index = index;
		this.geoset = geoset;
		this.layer = layer;
		this.isExtended = isExtended;
	}

	@Override
	public int getIndex() {
		return this.index;
	}

}
