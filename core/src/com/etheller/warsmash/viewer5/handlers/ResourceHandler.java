package com.etheller.warsmash.viewer5.handlers;

import com.etheller.warsmash.viewer5.ModelViewer;

public abstract class ResourceHandler {
	public ResourceHandler handler;
	public boolean load;

	public abstract boolean load(ModelViewer modelViewer);
}
