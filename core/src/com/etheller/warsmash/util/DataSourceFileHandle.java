package com.etheller.warsmash.util;

import java.io.IOException;
import java.io.InputStream;

import com.badlogic.gdx.files.FileHandle;
import com.etheller.warsmash.datasources.DataSource;

public class DataSourceFileHandle extends FileHandle {
	private final DataSource dataSource;

	public DataSourceFileHandle(final DataSource dataSource, final String path) {
		super(fixPath(dataSource, path));
		this.dataSource = dataSource;
	}

	@Override
	public String path() {
		return file().getPath();
	}

	@Override
	public InputStream read() {
		try {
			return this.dataSource.getResourceAsStream(path());
		}
		catch (final IOException e) {
			throw new RuntimeException("Failed to load FileHandle from DataSource: " + path());
		}
	}

	private static String fixPath(final DataSource dataSource, String path) {
		if (!dataSource.has(path) && (path.toLowerCase().endsWith(".wav") || path.toLowerCase().endsWith(".mp3"))) {
			final String otherPossiblePath = path.substring(0, path.lastIndexOf('.')) + ".flac";
			if (dataSource.has(otherPossiblePath)) {
				path = otherPossiblePath;
			}
		}
		return path;
	}
}
