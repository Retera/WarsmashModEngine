package com.etheller.warsmash.viewer5.handlers.blp;

import java.util.ArrayList;

import com.etheller.warsmash.viewer5.HandlerResource;
import com.etheller.warsmash.viewer5.ModelViewer;
import com.etheller.warsmash.viewer5.handlers.ResourceHandler;
import com.etheller.warsmash.viewer5.handlers.ResourceHandlerConstructionParams;

public class DdsHandler extends ResourceHandler {

	public DdsHandler() {
		this.extensions = new ArrayList<>();
		this.extensions.add(new String[] { ".dds", "arrayBuffer" });
	}

	@Override
	public boolean load(final ModelViewer modelViewer) {
		return true;
	}

	@Override
	public HandlerResource<?> construct(final ResourceHandlerConstructionParams params) {
		return new DdsTexture(params.getViewer(), params.getHandler(), params.getExtension(), params.getPathSolver(),
				params.getFetchUrl());
	}

}
