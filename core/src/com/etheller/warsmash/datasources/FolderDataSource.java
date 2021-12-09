package com.etheller.warsmash.datasources;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
	public InputStream getResourceAsStream(String filepath) throws IOException {
		filepath = fixFilepath(filepath);
		if (!has(filepath)) {
			return null;
		}
		return Files.newInputStream(this.folderPath.resolve(filepath), StandardOpenOption.READ);
	}

	@Override
	public File getFile(String filepath) throws IOException {
		filepath = fixFilepath(filepath);
		if (!has(filepath)) {
			return null;
		}
		return new File(this.folderPath.toString() + File.separatorChar + filepath);
	}

	@Override
	public File getDirectory(String filepath) throws IOException {
		filepath = fixFilepath(filepath);
		File file = new File(this.folderPath.toString() + File.separatorChar + filepath);
		if(!file.exists() || !file.isDirectory()) {
			return null;
		}
		return file;
	}

	@Override
	public ByteBuffer read(String path) throws IOException {
		path = fixFilepath(path);
		if (!has(path)) {
			return null;
		}
		return ByteBuffer.wrap(Files.readAllBytes(Paths.get(this.folderPath.toString(), path)));
	}

	@Override
	public boolean has(String filepath) {
		filepath = fixFilepath(filepath);
		if ("".equals(filepath)) {
			return false; // special case for folder data source, dont do this
		}
		final Path resolvedPath = this.folderPath.resolve(filepath);
		return Files.exists(resolvedPath) && !Files.isDirectory(resolvedPath);
	}

	@Override
	public Collection<String> getListfile() {
		return this.listfile;
	}

	@Override
	public void close() {
	}

	private static String fixFilepath(final String filepath) {
		return filepath.replace('\\', File.separatorChar).replace('/', File.separatorChar).replace(':',
				File.separatorChar);
	}
}
