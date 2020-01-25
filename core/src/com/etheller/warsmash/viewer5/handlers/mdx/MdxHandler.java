package com.etheller.warsmash.viewer5.handlers.mdx;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.etheller.warsmash.viewer5.HandlerResource;
import com.etheller.warsmash.viewer5.ModelViewer;
import com.etheller.warsmash.viewer5.handlers.ModelHandler;
import com.etheller.warsmash.viewer5.handlers.ResourceHandlerConstructionParams;
import com.etheller.warsmash.viewer5.handlers.blp.BlpHandler;

public class MdxHandler extends ModelHandler {

	public MdxHandler() {
		this.extensions = new ArrayList<>();
		this.extensions.add(new String[] { ".mdx", "arrayBuffer" });
		this.extensions.add(new String[] { ".mdl", "text" });
		this.load = true;
	}

	@Override
	public boolean load(final ModelViewer viewer) {
		viewer.addHandler(new BlpHandler());

		Shaders.complex = viewer.webGL.createShaderProgram(MdxShaders.vsComplexUnshaded, MdxShaders.fsComplex);
		Shaders.extended = viewer.webGL.createShaderProgram("#define EXTENDED_BONES\r\n" + MdxShaders.vsComplexUnshaded,
				MdxShaders.fsComplex);
		Shaders.particles = viewer.webGL.createShaderProgram(MdxShaders.vsParticles, MdxShaders.fsParticles);
		Shaders.simple = viewer.webGL.createShaderProgram(MdxShaders.vsSimple, MdxShaders.fsSimple);
//		Shaders.hd = viewer.webGL.createShaderProgram(MdxShaders.vsHd, MdxShaders.fsHd);
		// TODO HD reforged

		// If a shader failed to compile, don't allow the handler to be registered, and
		// send an error instead.
		return Shaders.complex.isCompiled() && Shaders.extended.isCompiled() && Shaders.particles.isCompiled()
				&& Shaders.simple.isCompiled() /* && Shaders.hd.isCompiled() */;
	}

	@Override
	public HandlerResource<?> construct(final ResourceHandlerConstructionParams params) {
		return new MdxModel((MdxHandler) params.getHandler(), params.getViewer(), params.getExtension(),
				params.getPathSolver(), params.getFetchUrl());
	}

	public static final class Shaders {
		private Shaders() {

		}

		public static ShaderProgram complex;
		public static ShaderProgram extended;
		public static ShaderProgram simple;
		public static ShaderProgram particles;
		public static ShaderProgram hd;
	}
}
