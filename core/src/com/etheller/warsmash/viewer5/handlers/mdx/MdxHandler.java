package com.etheller.warsmash.viewer5.handlers.mdx;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.etheller.warsmash.viewer5.HandlerResource;
import com.etheller.warsmash.viewer5.ModelViewer;
import com.etheller.warsmash.viewer5.handlers.ModelHandler;
import com.etheller.warsmash.viewer5.handlers.ResourceHandlerConstructionParams;
import com.etheller.warsmash.viewer5.handlers.blp.BlpHandler;
import com.etheller.warsmash.viewer5.handlers.blp.DdsHandler;
import com.etheller.warsmash.viewer5.handlers.tga.TgaHandler;

public class MdxHandler extends ModelHandler {
	public final Shaders shaders = new Shaders();

	public static enum ShaderEnvironmentType {
		MENU, GAME
	};

	public static ShaderEnvironmentType CURRENT_SHADER_TYPE;

	public MdxHandler() {
		this.extensions = new ArrayList<>();
		this.extensions.add(new String[] { ".mdx", "arrayBuffer" });
		this.extensions.add(new String[] { ".mdl", "text" });
		this.load = true;
	}

	@Override
	public boolean load(final ModelViewer viewer) {
		viewer.addHandler(new BlpHandler());
		viewer.addHandler(new DdsHandler());
		viewer.addHandler(new TgaHandler());

		this.shaders.complex = viewer.webGL.createShaderProgram(MdxShaders.vsComplex("", false), MdxShaders.fsComplex);
		this.shaders.extended = viewer.webGL
				.createShaderProgram(MdxShaders.vsComplex("#define EXTENDED_BONES\r\n", false), MdxShaders.fsComplex);
		this.shaders.complexSkin = viewer.webGL.createShaderProgram(MdxShaders.vsComplex("", true),
				MdxShaders.fsComplex);
		this.shaders.particles = viewer.webGL.createShaderProgram(MdxShaders.vsParticles(), MdxShaders.fsParticles);
		// Shaders.simple = viewer.webGL.createShaderProgram(MdxShaders.vsSimple,
		// MdxShaders.fsSimple);
		this.shaders.hd = viewer.webGL.createShaderProgram(MdxShaders.vsHd, MdxShaders.fsHd());
		// TODO HD reforged

		// If a shader failed to compile, don't allow the handler to be registered, and
		// send an error instead.
		return this.shaders.complex.isCompiled() && this.shaders.extended.isCompiled()
				&& this.shaders.particles.isCompiled()
		/* && Shaders.simple.isCompiled() && Shaders.hd.isCompiled() */;
	}

	@Override
	public HandlerResource<?> construct(final ResourceHandlerConstructionParams params) {
		return new MdxModel((MdxHandler) params.getHandler(), params.getViewer(), params.getExtension(),
				params.getPathSolver(), params.getFetchUrl());
	}

	public static final class Shaders {
		private Shaders() {

		}

		public ShaderProgram complex;
		public ShaderProgram complexSkin;
		public ShaderProgram extended;
		public ShaderProgram simple;
		public ShaderProgram particles;
		public ShaderProgram hd;
	}
}
