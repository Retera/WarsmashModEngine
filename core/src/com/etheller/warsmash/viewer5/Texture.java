package com.etheller.warsmash.viewer5;

import com.etheller.warsmash.viewer5.handlers.ResourceHandler;

public abstract class Texture extends HandlerResource<ResourceHandler> implements ViewerTextureRenderable {

	public Texture(final ModelViewer viewer, final String extension, final PathSolver pathSolver, final String fetchUrl,
			final ResourceHandler handler) {
		super(viewer, extension, pathSolver, fetchUrl, handler);
	}

	public abstract void bind(final int unit);

	public abstract void internalBind();

	public abstract int getWidth();

	public abstract int getHeight();

	@Override
	public abstract int getGlTarget();

	@Override
	public abstract int getGlHandle();

	public abstract void setWrapS(final boolean wrapS);

	public abstract void setWrapT(final boolean wrapT);

}
