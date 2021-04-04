package com.etheller.warsmash.viewer5.handlers;

import com.etheller.warsmash.viewer5.ModelViewer;
import com.etheller.warsmash.viewer5.PathSolver;

public class ResourceHandlerConstructionParams {
	public final ModelViewer viewer;
	public final ResourceHandler handler;
	public final String extension;
	public final PathSolver pathSolver;
	public final String fetchUrl;

	public ResourceHandlerConstructionParams(final ModelViewer viewer, final ResourceHandler handler,
			final String extension, final PathSolver pathSolver, final String fetchUrl) {
		this.viewer = viewer;
		this.handler = handler;
		this.extension = extension;
		this.pathSolver = pathSolver;
		this.fetchUrl = fetchUrl;
	}

	public ModelViewer getViewer() {
		return this.viewer;
	}

	public ResourceHandler getHandler() {
		return this.handler;
	}

	public String getExtension() {
		return this.extension;
	}

	public PathSolver getPathSolver() {
		return this.pathSolver;
	}

	public String getFetchUrl() {
		return this.fetchUrl;
	}

}
