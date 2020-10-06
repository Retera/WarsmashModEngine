package com.etheller.warsmash.viewer5.handlers.mdx;

import com.etheller.warsmash.datasources.DataSource;
import com.etheller.warsmash.viewer5.CanvasProvider;
import com.etheller.warsmash.viewer5.ModelViewer;
import com.etheller.warsmash.viewer5.SceneLightManager;

public class MdxViewer extends ModelViewer {

	public MdxViewer(final DataSource dataSource, final CanvasProvider canvas) {
		super(dataSource, canvas);
	}

	@Override
	public SceneLightManager createLightManager(final boolean simple) {
		// TODO Auto-generated method stub
		return null;
	}

}
