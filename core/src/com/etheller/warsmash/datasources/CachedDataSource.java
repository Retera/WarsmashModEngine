package com.etheller.warsmash.datasources;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Collection;

public class CachedDataSource implements DataSource {
	private final DataSource delegate;

	public CachedDataSource(final DataSource delegate) {
		this.delegate = delegate;
	}

	@Override
	public InputStream getResourceAsStream(final String filepath) throws IOException {
		return null;
	}

	@Override
	public File getFile(final String filepath) throws IOException {
		return null;
	}

	@Override
	public File getDirectory(final String filepath) throws IOException {
		return null;
	}

	@Override
	public ByteBuffer read(final String path) throws IOException {
		return null;
	}

	@Override
	public boolean has(final String filepath) {
		return false;
	}

	@Override
	public Collection<String> getListfile() {
		return null;
	}

	@Override
	public void close() throws IOException {

	}

}
