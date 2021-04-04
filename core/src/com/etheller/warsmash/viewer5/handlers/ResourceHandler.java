package com.etheller.warsmash.viewer5.handlers;

import java.util.List;

import com.etheller.warsmash.viewer5.ModelViewer;
import com.etheller.warsmash.viewer5.HandlerResource;

public abstract class ResourceHandler {
	public ResourceHandler handler;
	public boolean load;
	public List<String[]> extensions;

	public abstract boolean load(ModelViewer modelViewer);

	public abstract HandlerResource<?> construct(ResourceHandlerConstructionParams params);
}
