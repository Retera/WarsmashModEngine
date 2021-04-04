package com.etheller.warsmash.viewer5;

import java.io.InputStream;

public abstract class Resource {
	public final ModelViewer viewer;
	public final String extension;
	public final String fetchUrl;
	public boolean ok;
	public boolean loaded;
	public final PathSolver pathSolver;
	public final Object solverParams = null;

	public Resource(final ModelViewer viewer, final String extension, final PathSolver pathSolver,
			final String fetchUrl) {
		this.viewer = viewer;
		this.extension = extension;
		this.pathSolver = pathSolver;
		this.fetchUrl = fetchUrl;
		this.ok = false;
		this.loaded = false;
	}

	public void loadData(final InputStream src, final Object options) {
		this.loaded = true;

		try {
			this.load(src, options);
			this.ok = true;
			this.lateLoad();
		}
		catch (final Exception e) {
			this.error(e);
		}
	}

	public boolean detach() {
		return this.viewer.unload(this);
	}

	protected abstract void lateLoad();

	protected abstract void load(InputStream src, Object options);

	protected abstract void error(Exception e);
}
