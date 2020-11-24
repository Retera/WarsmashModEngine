package com.etheller.warsmash.viewer5.handlers.mdx;

import com.badlogic.gdx.math.Vector3;
import com.etheller.warsmash.datasources.DataSource;
import com.etheller.warsmash.viewer5.CanvasProvider;
import com.etheller.warsmash.viewer5.ModelViewer;
import com.etheller.warsmash.viewer5.SceneLightManager;
import com.etheller.warsmash.viewer5.handlers.w3x.W3xScenePortraitLightManager;

public class MdxViewer extends ModelViewer {

	public MdxViewer(final DataSource dataSource, final CanvasProvider canvas) {
		super(dataSource, canvas);
	}

	@Override
	public SceneLightManager createLightManager(final boolean simple) {
		return new W3xScenePortraitLightManager(this, new Vector3(0.3f, 0.3f, -0.25f));
	}

}
