package com.etheller.warsmash.datasources;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class FolderDataSource implements DataSource {

	private final Path folderPath;
	private final Set<String> listfile;

	public FolderDataSource(final Path folderPath) {
		this.folderPath = folderPath;
		this.listfile = new HashSet<>();
		try {
			Files.walk(folderPath).filter(Files::isRegularFile).forEach(new Consumer<Path>() {
				@Override
				public void accept(final Path t) {
					FolderDataSource.this.listfile.add(folderPath.relativize(t).toString());
				}
			});
		}
		catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public InputStream getResourceAsStream(final String filepath) throws IOException {
		if (!has(filepath)) {
			return null;
		}
		return Files.newInputStream(this.folderPath.resolve(filepath), StandardOpenOption.READ);
	}

	@Override
	public File getFile(final String filepath) throws IOException {
		if (!has(filepath)) {
			return null;
		}
		return new File(this.folderPath.toString() + File.separatorChar + filepath);
	}

	@Override
	public boolean has(final String filepath) {
		if ("".equals(filepath)) {
			return false; // special case for folder data source, dont do this
		}
		return Files.exists(this.folderPath.resolve(filepath));
	}

	@Override
	public Collection<String> getListfile() {
		return this.listfile;
	}

	@Override
	public void close() {
	}

}
