package com.hiveworkshop.blizzard.casc.io;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.hiveworkshop.blizzard.casc.ConfigurationFile;
import com.hiveworkshop.blizzard.casc.info.Info;
import com.hiveworkshop.blizzard.casc.nio.MalformedCASCStructureException;
import com.hiveworkshop.blizzard.casc.storage.Storage;
import com.hiveworkshop.blizzard.casc.vfs.VirtualFileSystem;
import com.hiveworkshop.blizzard.casc.vfs.VirtualFileSystem.PathResult;

/**
 * A convenient access to locally stored Warcraft III data files. Intended for
 * use with CASC versions of Warcraft III including classic and Reforged.
 */
public class WarcraftIIICASC implements AutoCloseable {
	/**
	 * File system view for accessing files from file paths.
	 */
	public class FileSystem {
		/**
		 * Private constructor, currently not used.
		 */
		private FileSystem() {

		}

		/**
		 * Enumerate all file paths contained in this file system.
		 * <p>
		 * This operation might be quite slow.
		 *
		 * @return A list containing all file paths contained in this file system.
		 * @throws IOException In an exception occurs when resolving files.
		 */
		public List<String> enumerateFiles() throws IOException {
			final List<PathResult> pathResults = vfs.getAllFiles();
			final ArrayList<String> filePathStrings = new ArrayList<String>(pathResults.size());

			for (final PathResult pathResult : pathResults) {
				filePathStrings.add(pathResult.getPath());
			}

			return filePathStrings;
		}

		/**
		 * Test if the specified file path is a file.
		 *
		 * @param filePath Path of file to test.
		 * @return True if path represents a file, otherwise false.
		 * @throws IOException In an exception occurs when resolving files.
		 */
		public boolean isFile(final String filePath) throws IOException {
			final byte[][] pathFragments = VirtualFileSystem.convertFilePath(filePath);
			try {
				final PathResult resolveResult = vfs.resolvePath(pathFragments);
				return resolveResult.isFile();
			} catch (final FileNotFoundException e) {
				return false;
			}
		}

		/**
		 * Test if the specified file path is available from local storage.
		 *
		 * @param filePath Path of file to test.
		 * @return True if path represents a file inside local storage, otherwise false.
		 * @throws IOException In an exception occurs when resolving files.
		 */
		public boolean isFileAvailable(final String filePath) throws IOException {
			final byte[][] pathFragments = VirtualFileSystem.convertFilePath(filePath);
			final PathResult resolveResult = vfs.resolvePath(pathFragments);
			return resolveResult.existsInStorage();
		}

		/**
		 * Test if the specified file path is a nested file system.
		 * <p>
		 * If true a file system can be resolved from the file path which files can be
		 * resolved from more efficiently than from higher up file systems.
		 * <p>
		 * Support for this feature is not yet implemented. Please resolve everything
		 * from the root.
		 *
		 * @param filePath Path of file to test.
		 * @return True if file is a nested file system, otherwise false.
		 * @throws IOException In an exception occurs when resolving files.
		 */
		public boolean isNestedFileSystem(final String filePath) throws IOException {
			final byte[][] pathFragments = VirtualFileSystem.convertFilePath(filePath);
			try {
				final PathResult resolveResult = vfs.resolvePath(pathFragments);
				return resolveResult.isTVFS();
			} catch (final FileNotFoundException e) {
				return false;
			}
		}

		/**
		 * Fully read the file at the specified file path into memory.
		 *
		 * @param filePath File path of file to read.
		 * @return Buffer containing file data.
		 * @throws IOException If an error occurs when reading the file.
		 */
		public ByteBuffer readFileData(final String filePath) throws IOException {
			final byte[][] pathFragments = VirtualFileSystem.convertFilePath(filePath);
			final PathResult resolveResult = vfs.resolvePath(pathFragments);

			if (!resolveResult.isFile()) {
				throw new FileNotFoundException("the specified file path does not resolve to a file");
			} else if (!resolveResult.existsInStorage()) {
				throw new FileNotFoundException("the specified file is not in local storage");
			}

			final ByteBuffer fileBuffer = resolveResult.readFile(null);
			fileBuffer.flip();
			return fileBuffer;
		}
	}

	/**
	 * Name of the CASC data folder used by Warcraft III.
	 */
	private static final String WC3_DATA_FOLDER_NAME = "Data";

	/**
	 * Warcraft III build information.
	 */
	private final Info buildInfo;

	/**
	 * Detected active build information record.
	 */
	private final int activeInfoRecord;

	/**
	 * Warcraft III build configuration.
	 */
	private final ConfigurationFile buildConfiguration;

	/**
	 * Warcraft III CASC data folder path.
	 */
	private final Path dataPath;

	/**
	 * Warcraft III local storage.
	 */
	private final Storage localStorage;

	/**
	 * TVFS file system to resolve file paths.
	 */
	private final VirtualFileSystem vfs;

	/**
	 * Construct an interface to the CASC local storage used by Warcraft III. Can be
	 * used to read data files from the local storage.
	 * <p>
	 * The active build record is used for local storage details.
	 * <p>
	 * Install folder is the Warcraft III installation folder where the
	 * <code>.build.info</code> file is located. For example
	 * <code>C:\Program Files (x86)\Warcraft III</code>.
	 * <p>
	 * Memory mapped IO can be used instead of conventional channel based IO. This
	 * should improve IO performance considerably by avoiding excessive memory copy
	 * operations and system calls. However it may place considerable strain on the
	 * Java VM application virtual memory address space. As such memory mapping
	 * should only be used with large address aware VMs.
	 *
	 * @param installFolder    Warcraft III installation folder.
	 * @param useMemoryMapping If memory mapped IO should be used to read file data.
	 * @throws IOException If an exception occurs while mounting.
	 */
	public WarcraftIIICASC(final Path installFolder, final boolean useMemoryMapping) throws IOException {
		final Path infoFilePath = installFolder.resolve(Info.BUILD_INFO_FILE_NAME);
		buildInfo = new Info(ByteBuffer.wrap(Files.readAllBytes(infoFilePath)));

		final int recordCount = buildInfo.getRecordCount();
		if (recordCount < 1) {
			throw new MalformedCASCStructureException("build info contains no records");
		}

		// resolve the active record
		final int activeFiledIndex = buildInfo.getFieldIndex("Active");
		if (activeFiledIndex == -1) {
			throw new MalformedCASCStructureException("build info contains no active field");
		}
		int recordIndex = 0;
		for (; recordIndex < recordCount; recordIndex += 1) {
			if (Integer.parseInt(buildInfo.getField(recordIndex, activeFiledIndex)) == 1) {
				break;
			}
		}
		if (recordIndex == recordCount) {
			throw new MalformedCASCStructureException("build info contains no active record");
		}
		activeInfoRecord = recordIndex;

		// resolve build configuration file
		final int buildKeyFieldIndex = buildInfo.getFieldIndex("Build Key");
		if (buildKeyFieldIndex == -1) {
			throw new MalformedCASCStructureException("build info contains no build key field");
		}
		final String buildKey = buildInfo.getField(activeInfoRecord, buildKeyFieldIndex);

		// resolve data folder
		dataPath = installFolder.resolve(WC3_DATA_FOLDER_NAME);
		if (!Files.isDirectory(dataPath)) {
			throw new MalformedCASCStructureException("data folder is missing");
		}

		// resolve build configuration file
		buildConfiguration = ConfigurationFile.lookupConfigurationFile(dataPath, buildKey);

		// mounting local storage
		localStorage = new Storage(dataPath, false, useMemoryMapping);

		// mounting virtual file system
		VirtualFileSystem vfs = null;
		try {
			vfs = new VirtualFileSystem(localStorage, buildConfiguration.getConfiguration());
		} finally {
			if (vfs == null) {
				// storage must be closed to prevent resource leaks
				localStorage.close();
			}
		}
		this.vfs = vfs;
	}

	@Override
	public void close() throws IOException {
		localStorage.close();
	}

	/**
	 * Returns the active record index of the build information. This is the index
	 * of the record that is mounted.
	 *
	 * @return Active record index of build information.
	 */
	public int getActiveRecordIndex() {
		return activeInfoRecord;
	}

	/**
	 * Returns the active branch name which is currently mounted.
	 * <p>
	 * This might reflect the locale that has been cached to local storage.
	 *
	 * @return Branch name.
	 * @throws IOException If no branch information is available.
	 */
	public String getBranch() throws IOException {
		// resolve branch
		final int branchFieldIndex = buildInfo.getFieldIndex("Branch");
		if (branchFieldIndex == -1) {
			throw new MalformedCASCStructureException("build info contains no branch field");
		}
		return buildInfo.getField(activeInfoRecord, branchFieldIndex);
	}

	/**
	 * Returns the build information of the archive.
	 *
	 * @return Build information.
	 */
	public Info getBuildInfo() {
		return buildInfo;
	}

	/**
	 * Get the root file system of Warcraft III. From this all locally stored data
	 * files can be accessed.
	 *
	 * @return Root file system containing all files.
	 */
	public FileSystem getRootFileSystem() {
		return new FileSystem();
	}
}
