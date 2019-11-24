package com.etheller.warsmash.viewer5;

public abstract class Resource {
	public ModelViewer viewer;

	public Resource(final ModelViewer viewer) {
		this.viewer = viewer;
	}

	public abstract void bind(int unit);

}
