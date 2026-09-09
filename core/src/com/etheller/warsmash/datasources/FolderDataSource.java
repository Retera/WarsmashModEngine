package com.etheller.warsmash.datasources;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class FolderDataSource implements DataSource {

	private final Path folderPath;
	private final Set<String> listfile;
	// Game data was authored against case-insensitive filesystems, so requests
	// arrive in arbitrary case; on case-sensitive filesystems (Linux) we resolve
	// through this lowercased index of the walk we already perform at startup.
	private final Map<String, String> caseInsensitiveIndex;

	public FolderDataSource(final Path folderPath) {
		this.folderPath = folderPath;
		this.listfile = new HashSet<>();
		this.caseInsensitiveIndex = new HashMap<>();
		try {
			Files.walk(folderPath).filter(Files::isRegularFile).forEach(new Consumer<Path>() {
				@Override
				public void accept(final Path t) {
					final String relativePath = folderPath.relativize(t).toString();
					FolderDataSource.this.listfile.add(relativePath);
					FolderDataSource.this.caseInsensitiveIndex.put(lowerKey(relativePath), relativePath);
				}
			});
		}
		catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public InputStream getResourceAsStream(String filepath) throws IOException {
		filepath = resolveCase(fixFilepath(filepath));
		if (!hasExact(filepath)) {
			return null;
		}
		return Files.newInputStream(this.folderPath.resolve(filepath), StandardOpenOption.READ);
	}

	@Override
	public File getFile(String filepath) throws IOException {
		filepath = resolveCase(fixFilepath(filepath));
		if (!hasExact(filepath)) {
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
		path = resolveCase(fixFilepath(path));
		if (!hasExact(path)) {
			return null;
		}
		return ByteBuffer.wrap(Files.readAllBytes(this.folderPath.resolve(path)));
	}

	@Override
	public boolean has(String filepath) {
		filepath = fixFilepath(filepath);
		if ("".equals(filepath)) {
			return false; // special case for folder data source, dont do this
		}
		return hasExact(resolveCase(filepath));
	}

	private boolean hasExact(final String filepath) {
		if ("".equals(filepath)) {
			return false;
		}
		final Path resolvedPath = this.folderPath.resolve(filepath);
		return Files.exists(resolvedPath) && !Files.isDirectory(resolvedPath);
	}

	private String resolveCase(final String filepath) {
		final Path resolvedPath = this.folderPath.resolve(filepath);
		if (Files.exists(resolvedPath)) {
			return filepath;
		}
		final String indexed = this.caseInsensitiveIndex.get(lowerKey(filepath));
		return indexed != null ? indexed : filepath;
	}

	private static String lowerKey(final String path) {
		return path.toLowerCase(Locale.ROOT);
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
