package com.etheller.warsmash.viewer5.handlers.w3x.ui.mapsetup;

import java.io.File;

public class CurrentNetGameMapLookupFile implements CurrentNetGameMapLookup {
	private final File file;

	public CurrentNetGameMapLookupFile(File file) {
		this.file = file;
	}

	public File getFile() {
		return this.file;
	}
}
