package com.etheller.warsmash.viewer5;

import java.io.InputStream;

import com.etheller.warsmash.viewer5.handlers.ResourceHandler;

public abstract class Resource<HANDLER extends ResourceHandler> {
	public final ModelViewer viewer;
	public final HANDLER handler;
	public final String extension;
	public final String fetchUrl;
	public boolean ok;
	public boolean loaded;

	public Resource(final ModelViewer viewer, final HANDLER handler, final String extension, final String fetchUrl) {
		this.viewer = viewer;
		this.handler = handler;
		this.extension = extension;
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
