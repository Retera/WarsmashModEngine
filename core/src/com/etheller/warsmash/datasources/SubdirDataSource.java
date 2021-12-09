package com.etheller.warsmash.datasources;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SubdirDataSource implements DataSource {
	private final DataSource dataSource;
	private final String subdir;

	public SubdirDataSource(final DataSource dataSource, final String subdir) {
		this.dataSource = dataSource;
		this.subdir = subdir;
	}

	@Override
	public File getFile(final String filepath) throws IOException {
		return this.dataSource.getFile(this.subdir + filepath);
	}

	@Override
	public File getDirectory(String filepath) throws IOException {
		return this.dataSource.getDirectory(this.subdir + filepath);
	}

	@Override
	public ByteBuffer read(final String path) throws IOException {
		return this.dataSource.read(this.subdir + path);
	}

	@Override
	public InputStream getResourceAsStream(final String filepath) throws IOException {
		return this.dataSource.getResourceAsStream(this.subdir + filepath);
	}

	@Override
	public boolean has(final String filepath) {
		return this.dataSource.has(this.subdir + filepath);
	}

	@Override
	public Collection<String> getListfile() {
		final List<String> results = new ArrayList<>();
		for (final String x : this.dataSource.getListfile()) {
			if (x.startsWith(this.subdir)) {
				results.add(x.substring(this.subdir.length()));
			}
		}
		return results;
	}

	@Override
	public void close() throws IOException {
		this.dataSource.close();
	}
}
