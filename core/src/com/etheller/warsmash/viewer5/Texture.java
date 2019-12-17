package com.etheller.warsmash.viewer5;

import com.etheller.warsmash.viewer5.handlers.ResourceHandler;

public abstract class Texture extends Resource<ResourceHandler> {
	private com.badlogic.gdx.graphics.Texture gdxTexture;

	public Texture(final ModelViewer viewer, final ResourceHandler handler, final String extension,
			final PathSolver pathSolver, final String fetchUrl) {
		super(viewer, handler, extension, pathSolver, fetchUrl);
	}

	public void setGdxTexture(final com.badlogic.gdx.graphics.Texture gdxTexture) {
		this.gdxTexture = gdxTexture;
	}

	@Override
	protected void error(final Exception e) {
		throw new RuntimeException(e);
	}

	public void bind(final int unit) {
		this.viewer.webGL.bindTexture(this, unit);
	}

	public void internalBind() {
		this.gdxTexture.bind();
	}

	public int getWidth() {
		return this.gdxTexture.getWidth();
	}

	public int getHeight() {
		return this.gdxTexture.getHeight();
	}

	public int getGlTarget() {
		return this.gdxTexture.glTarget;
	}

}
