package com.etheller.warsmash.util;

import java.io.IOException;
import java.io.InputStream;

import com.badlogic.gdx.files.FileHandle;
import com.etheller.warsmash.datasources.DataSource;

public class DataSourceFileHandle extends FileHandle {
	private final DataSource dataSource;

	public DataSourceFileHandle(final DataSource dataSource, final String path) {
		super(path);
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
}
