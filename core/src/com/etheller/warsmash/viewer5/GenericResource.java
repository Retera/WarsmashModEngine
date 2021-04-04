package com.etheller.warsmash.viewer5;

import java.io.InputStream;

import com.etheller.warsmash.common.LoadGenericCallback;

public final class GenericResource extends Resource {

	public Object data; // TODO this likely won't work, just brainstorming until I get to the part of
						// using the data
	private final LoadGenericCallback callback;

	public GenericResource(final ModelViewer viewer, final String extension, final PathSolver pathSolver,
			final String fetchUrl, final LoadGenericCallback callback) {
		super(viewer, extension, pathSolver, fetchUrl);
		this.callback = callback;
	}

	@Override
	protected void lateLoad() {

	}

	@Override
	protected void load(final InputStream src, final Object options) {
		this.data = this.callback.call(src);

	}

	@Override
	protected void error(final Exception e) {
		e.printStackTrace();
	}

}
