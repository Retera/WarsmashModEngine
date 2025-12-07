package com.etheller.warsmash.parsers.wmo;

import java.util.ArrayList;

import com.etheller.warsmash.viewer5.HandlerResource;
import com.etheller.warsmash.viewer5.ModelViewer;
import com.etheller.warsmash.viewer5.handlers.ModelHandler;
import com.etheller.warsmash.viewer5.handlers.ResourceHandlerConstructionParams;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxHandler;

public class WmoMpqPortingHandler extends ModelHandler {
	private final MdxHandler mdxHandler;

	public WmoMpqPortingHandler(final MdxHandler mdxHandler) {
		this.mdxHandler = mdxHandler;
		this.extensions = new ArrayList<>();
		this.extensions.add(new String[] { ".mpq", "arrayBuffer" });
		this.load = true;
	}

	@Override
	public boolean load(final ModelViewer viewer) {
		return true;
	}

	@Override
	public HandlerResource<?> construct(final ResourceHandlerConstructionParams params) {
		return new WmoMpqPortingModel(this.mdxHandler, params.getViewer(), params.getExtension(),
				params.getPathSolver(), params.getFetchUrl());
	}

}
