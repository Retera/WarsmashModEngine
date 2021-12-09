package com.etheller.warsmash.datasources;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Collection;

public interface DataSource {
	/**
	 * Efficiently return a stream instance that will read the data source file's
	 * contents directly from the data source. For example, this will read a file
	 * within an MPQ or CASC storage without extracting it.
	 *
	 * @param filepath
	 * @return
	 * @throws IOException
	 */
	InputStream getResourceAsStream(String filepath) throws IOException;

	/**
	 * Inefficiently copy a file from the data source onto the Hard Drive of the
	 * computer, and then return a java File instance pointed at the file.
	 *
	 * @param filepath
	 * @return
	 * @throws IOException
	 */
	File getFile(String filepath) throws IOException;

	/**
	 * Returns a directory from a FolderDataSource, otherwise returns null
	 *
	 * @param filepath
	 * @return
	 * @throws IOException
	 */
	File getDirectory(String filepath) throws IOException;

	ByteBuffer read(String path) throws IOException;

	/**
	 * Returns true if the data source contains a valid entry for a particular file.
	 * Some data sources (MPQs) may contain files for which this returns true, even
	 * though they cannot list the file in their Listfile.
	 *
	 * @param filepath
	 * @return
	 */
	boolean has(String filepath);

	/**
	 * @return a list of data source contents, or null if no list is provided
	 */
	Collection<String> getListfile();

	void close() throws IOException;
}
