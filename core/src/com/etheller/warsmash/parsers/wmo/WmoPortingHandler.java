package com.etheller.warsmash.parsers.wmo;

import java.util.ArrayList;

import com.etheller.warsmash.viewer5.HandlerResource;
import com.etheller.warsmash.viewer5.ModelViewer;
import com.etheller.warsmash.viewer5.handlers.ModelHandler;
import com.etheller.warsmash.viewer5.handlers.ResourceHandlerConstructionParams;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxHandler;
import com.etheller.warsmash.viewer5.handlers.w3x.environment.WdtLiquidType;

public class WmoPortingHandler extends ModelHandler {
	private final MdxHandler mdxHandler;
	// used as pass-thru for the currently constructing thing
	private WdtLiquidType currentLiquidType;

	public WmoPortingHandler(final MdxHandler mdxHandler) {
		this.mdxHandler = mdxHandler;
		this.extensions = new ArrayList<>();
		this.extensions.add(new String[] { ".wmo", "arrayBuffer" });
		this.load = true;
	}

	@Override
	public boolean load(final ModelViewer viewer) {
		return true;
	}

	@Override
	public HandlerResource<?> construct(final ResourceHandlerConstructionParams params) {
		return new WmoPortingModel2(this, params.getViewer(), params.getExtension(), params.getPathSolver(),
				params.getFetchUrl(), this.currentLiquidType);
	}

	public MdxHandler getMdxHandler() {
		return this.mdxHandler;
	}

	public void setCurrentLiquidType(final WdtLiquidType liquidType) {
		this.currentLiquidType = liquidType;
	}

	public WdtLiquidType getCurrentLiquidType() {
		return this.currentLiquidType;
	}
}
