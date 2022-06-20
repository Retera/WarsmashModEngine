package com.etheller.warsmash.viewer5.handlers.w3x.ui.mapsetup;

public class CurrentNetGameMapLookupPath implements CurrentNetGameMapLookup {
	private final String path;

	public CurrentNetGameMapLookupPath(String path) {
		this.path = path;
	}

	public String getPath() {
		return this.path;
	}
}
