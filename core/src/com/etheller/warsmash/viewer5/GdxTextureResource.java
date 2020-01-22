package com.etheller.warsmash.viewer5;

import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.etheller.warsmash.viewer5.handlers.ResourceHandler;

public abstract class GdxTextureResource extends Texture {
	private com.badlogic.gdx.graphics.Texture gdxTexture;

	public GdxTextureResource(final ModelViewer viewer, final ResourceHandler handler, final String extension,
			final PathSolver pathSolver, final String fetchUrl) {
		super(viewer, extension, pathSolver, fetchUrl, handler);
	}

	public void setGdxTexture(final com.badlogic.gdx.graphics.Texture gdxTexture) {
		this.gdxTexture = gdxTexture;
	}

	@Override
	protected void error(final Exception e) {
		e.printStackTrace();
	}

	@Override
	public void bind(final int unit) {
		this.viewer.webGL.bindTexture(this, unit);
	}

	@Override
	public void internalBind() {
		this.gdxTexture.bind();
	}

	@Override
	public int getWidth() {
		return this.gdxTexture.getWidth();
	}

	@Override
	public int getHeight() {
		return this.gdxTexture.getHeight();
	}

	@Override
	public int getGlTarget() {
		return this.gdxTexture.glTarget;
	}

	@Override
	public int getGlHandle() {
		return this.gdxTexture.getTextureObjectHandle();
	}

	@Override
	public void setWrapS(final boolean wrapS) {
		this.gdxTexture.setWrap(wrapS ? TextureWrap.Repeat : TextureWrap.ClampToEdge, this.gdxTexture.getVWrap());
	}

	@Override
	public void setWrapT(final boolean wrapT) {
		this.gdxTexture.setWrap(this.gdxTexture.getUWrap(), wrapT ? TextureWrap.Repeat : TextureWrap.ClampToEdge);
	}

}
