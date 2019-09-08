package com.etheller.warsmash.viewer;

public abstract class Model {
	private ModelView modelView;

	public boolean ok;

	public abstract Viewer getViewer();
}
