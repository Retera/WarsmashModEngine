package com.etheller.warsmash.viewer5.handlers.w3x.lightning;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.etheller.warsmash.viewer5.HandlerResource;
import com.etheller.warsmash.viewer5.ModelViewer;
import com.etheller.warsmash.viewer5.handlers.ModelHandler;
import com.etheller.warsmash.viewer5.handlers.ResourceHandlerConstructionParams;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxHandler;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxModel;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxShaders;

import java.util.ArrayList;

public class LightningEffectModelHandler extends ModelHandler {


	public LightningEffectModelHandler() {
		this.extensions = new ArrayList<>();
		this.load = true;
	}

	@Override
	public boolean load(final ModelViewer viewer) {
	 	Shaders.simple = viewer.webGL.createShaderProgram(MdxShaders.vsLightning, MdxShaders.fsLightning);
		return Shaders.simple.isCompiled();
	}

	@Override
	public HandlerResource<?> construct(final ResourceHandlerConstructionParams params) {
		return new MdxModel((MdxHandler) params.getHandler(), params.getViewer(), params.getExtension(),
				params.getPathSolver(), params.getFetchUrl());
	}

	public static final class Shaders {
		private Shaders() {
		}

		public static ShaderProgram simple;
	}
}
