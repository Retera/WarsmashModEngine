package com.hiveworkshop.wc3.mpq;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class FileCodebase implements Codebase {
	private final File sourceDirectory;

	public FileCodebase(final File sourceDirectory) {
		this.sourceDirectory = sourceDirectory;
	}

	@Override
	public InputStream getResourceAsStream(final String filepath) {
		try {
			return new FileInputStream(new File(this.sourceDirectory.getPath() + File.separatorChar + filepath));
		}
		catch (final FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public File getFile(final String filepath) {
		return new File(this.sourceDirectory.getPath() + File.separatorChar + filepath);
	}

	@Override
	public boolean has(final String filepath) {
		return new File(this.sourceDirectory.getPath() + File.separatorChar + filepath).exists();
	}

}
