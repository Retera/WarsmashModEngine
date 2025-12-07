package com.etheller.warsmash.datasources;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class SourcedDataImpl implements SourcedData {
	private final DataSource dataSource;
	private final String path;

	public SourcedDataImpl(final DataSource dataSource, final String path) {
		this.dataSource = dataSource;
		this.path = path;
	}

	@Override
	public ByteBuffer read() {
		try {
			return this.dataSource.read(this.path);
		}
		catch (final IOException e) {
			throw new IllegalStateException("Unable to load data: " + this.path);
		}
	}

	@Override
	public InputStream getResourceAsStream() {
		try {
			return this.dataSource.getResourceAsStream(this.path);
		}
		catch (final IOException e) {
			throw new IllegalStateException("Unable to load data: " + this.path);
		}
	}
}
