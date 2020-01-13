package com.etheller.warsmash.viewer5;

import com.etheller.warsmash.viewer5.handlers.ResourceHandler;

public abstract class HandlerResource<HANDLER extends ResourceHandler> extends Resource {
	public final HANDLER handler;

	public HandlerResource(final ModelViewer viewer, final String extension, final PathSolver pathSolver,
			final String fetchUrl, final HANDLER handler) {
		super(viewer, extension, pathSolver, fetchUrl);
		this.handler = handler;
	}

}
