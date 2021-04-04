package com.hiveworkshop.blizzard.casc.vfs;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import com.hiveworkshop.ReteraCASCUtils;
import com.hiveworkshop.blizzard.casc.Key;
import com.hiveworkshop.blizzard.casc.nio.MalformedCASCStructureException;
import com.hiveworkshop.blizzard.casc.storage.BankStream;
import com.hiveworkshop.blizzard.casc.storage.Storage;

/**
 * High level file system API using TVFS directories to extract files from a
 * store.
 */
public final class VirtualFileSystem {
	/**
	 * A result of a file path lookup operation in a TVFS file system.
	 * <p>
	 * Can be used to fetch the data of a file.
	 */
	public final class PathResult {
		private final PathNode node;
		private final byte[][] pathFragments;

		/**
		 * Internal constructor for path results.
		 *
		 * @param node          Resolved node.
		 * @param pathFragments Path of resolved node.
		 */
		private PathResult(final PathNode node, final byte[][] pathFragments) {
			this.node = node;
			this.pathFragments = pathFragments;
		}

		/**
		 * Returns true if this file completely exists in storage.
		 * <p>
		 * The virtual file system structure lists all files, even ones that may not be
		 * in storage. Only files that are in storage can have their file buffer read.
		 * <p>
		 * If this result is not a file then it exists in storage as it has no storage
		 * footprint.
		 *
		 * @return True if the file exists in storage.
		 */
		public boolean existsInStorage() {
			boolean exists = true;

			if (isFile()) {
				final FileNode fileNode = (FileNode) node;
				final int fileReferenceCount = fileNode.getFileReferenceCount();
				for (int fileReferenceIndex = 0; fileReferenceIndex < fileReferenceCount; fileReferenceIndex += 1) {
					final StorageReference fileReference = fileNode.getFileReference(fileReferenceIndex);
					exists = exists && storage.hasBanks(fileReference.getEncodingKey());
				}
			}

			return exists;
		}

		/**
		 * Get the size of the file in bytes.
		 * <p>
		 * If this result is not a file a value of 0 is returned.
		 *
		 * @return File size in bytes.
		 */
		public long getFileSize() {
			long size = 0L;

			if (isFile()) {
				final FileNode fileNode = (FileNode) node;
				final int fileReferenceCount = fileNode.getFileReferenceCount();
				for (int fileReferenceIndex = 0; fileReferenceIndex < fileReferenceCount; fileReferenceIndex += 1) {
					final StorageReference fileReference = fileNode.getFileReference(fileReferenceIndex);
					size = Math.max(size, fileReference.getOffset() + fileReference.getSize());
				}
			}

			return size;
		}

		public String getPath() throws CharacterCodingException {
			return convertPathFragments(pathFragments);
		}

		public byte[][] getPathFragments() {
			return pathFragments;
		}

		public boolean isFile() {
			return node instanceof FileNode;
		}

		/**
		 * Returns if this path result represents a TVFS file node used by this file
		 * system.
		 * <p>
		 * Such nodes logically act as folders in the file path but also contain file
		 * data used by this file system. Such behaviour may be incompatible with
		 * standard file systems which do not support both a folder and file at the same
		 * path.
		 * <p>
		 * Results that are not files cannot be a TVFS file.
		 *
		 * @return If this node is a TVFS file used by this file system.
		 */
		public boolean isTVFS() {
			if (!isFile()) {
				return false;
			}

			final FileNode fileNode = (FileNode) node;
			final StorageReference fileReference = fileNode.getFileReference(0);
			return tvfsStorageReferences.containsKey(fileReference.getEncodingKey());
		}

		/**
		 * Fully read this file into the specified destination buffer. If no buffer is
		 * specified a new one will be allocated.
		 * <p>
		 * The specified buffer must have at least getFileSize bytes remaining.
		 *
		 * @param destBuffer Buffer to be written to.
		 * @return Buffer that was written to.
		 * @throws IOException      If an error occurs during reading.
		 * @throws OutOfMemoryError If no buffer is specified and the file is too big
		 *                          for a single buffer.
		 */
		public ByteBuffer readFile(ByteBuffer destBuffer) throws IOException {
			if (!isFile()) {
				throw new FileNotFoundException("result is not a file");
			}

			final long fileSize = getFileSize();
			if (fileSize > Integer.MAX_VALUE) {
				throw new OutOfMemoryError("file too big to process");
			}

			if (destBuffer == null) {
				destBuffer = ByteBuffer.allocate((int) fileSize);
			} else if (destBuffer.remaining() < fileSize) {
				throw new BufferOverflowException();
			}

			final ByteBuffer fileBuffer = destBuffer.slice();

			final FileNode fileNode = (FileNode) node;
			final int fileReferenceCount = fileNode.getFileReferenceCount();
			for (int fileReferenceIndex = 0; fileReferenceIndex < fileReferenceCount; fileReferenceIndex += 1) {
				final StorageReference fileReference = fileNode.getFileReference(fileReferenceIndex);

				final long logicalSize = fileReference.getSize();
				if (logicalSize != fileReference.getActualSize()) {
					throw new MalformedCASCStructureException("inconsistent size");
				}
				final long logicalOffset = fileReference.getOffset();

				final BankStream bankStream = storage.getBanks(fileReference.getEncodingKey());
				// TODO test if compressed and logical sizes match stored sizes.

				fileBuffer.limit((int) (logicalOffset + logicalSize));
				fileBuffer.position((int) logicalOffset);
				while (bankStream.hasNextBank()) {
					bankStream.getBank(fileBuffer);
				}
			}

			destBuffer.position(destBuffer.position() + (int) fileSize);
			return destBuffer;
		}
	}

	/**
	 * VFS storage reference key prefix.
	 */
	public static final String CONFIGURATION_KEY_PREFIX = "vfs-";

	/**
	 * Root VFS storage reference.
	 */
	public static final String ROOT_KEY = "root";

	/**
	 * Character encoding used internally by file paths.
	 */
	public static final Charset PATH_ENCODING = Charset.forName("UTF8");

	/**
	 * Path separator used by path strings.
	 */
	public static final String PATH_SEPERATOR = "\\";

	/**
	 * Compares the path fragments of a node with a section of file path fragments.
	 * This is useful for performing a binary search on a node's children.
	 * <p>
	 * A return value of 0 does not mean that the node is in the path fragments.
	 * Only that if it were, it would be this node. This is because the children of
	 * a node all have unique first fragment sequences so only the first fragment is
	 * tested.
	 *
	 * @param pathFragments  Path fragments of a file path.
	 * @param fragmentIndex  Index of fragment where to start comparing at.
	 * @param fragmentOffset Offset into fragment to start comparing at.
	 * @param node           Node which is being compared.
	 * @return Similar to standard comparator value (see above).
	 */
	private static int compareNodePathFragments(final byte[][] pathFragments, final int fragmentIndex,
			final int fragmentOffset, final PathNode node) {
		final int nodeFragmentCount = node.getPathFragmentCount();
		if (nodeFragmentCount == 0) {
			// nodes without fragments have no path fragment presence so always match
			return 0;
		}

		final byte[] nodeFragment = node.getFragment(0);
		final byte[] fragment = pathFragments[fragmentIndex];
		if ((nodeFragment.length == 0) && ((fragment.length - fragmentOffset) > 0)) {
			// node with termination fragment are always before all other child nodes
			return 1;
		}
		return ReteraCASCUtils.arraysCompareUnsigned(fragment, fragmentOffset,
				Math.min(fragmentOffset + nodeFragment.length, fragment.length), nodeFragment, 0, nodeFragment.length);
	}

	/**
	 * Convert a path string into path fragments for resolution in the VFS.
	 *
	 * @param filePath Path string to convert.
	 * @return Path fragments.
	 * @throws CharacterCodingException If the path string cannot be encoded into
	 *                                  fragments.
	 */
	public static byte[][] convertFilePath(final String filePath) throws CharacterCodingException {
		final String[] fragmentStrings = filePath.toLowerCase(Locale.ROOT).split("\\" + PATH_SEPERATOR);
		final byte[][] pathFragments = new byte[fragmentStrings.length][]; 

		final CharsetEncoder encoder = PATH_ENCODING.newEncoder();
		encoder.onMalformedInput(CodingErrorAction.REPORT);
		encoder.onUnmappableCharacter(CodingErrorAction.REPORT);
		for (int index = 0; index < fragmentStrings.length; index += 1) {
			final ByteBuffer fragmentBuffer = encoder.encode(CharBuffer.wrap(fragmentStrings[index]));
			if (fragmentBuffer.hasArray() && (fragmentBuffer.limit() == fragmentBuffer.capacity())
					&& (fragmentBuffer.position() == 0)) {
				// can use underlying array
				pathFragments[index] = fragmentBuffer.array();
			} else {
				// copy into array
				final byte[] pathFragment = new byte[fragmentBuffer.remaining()];
				fragmentBuffer.get(pathFragment);
				pathFragments[index] = pathFragment;
			}
		}

		return pathFragments;
	}

	/**
	 * Convert path fragments used internally by VFS into a path string.
	 *
	 * @param pathFragments Path fragments to convert.
	 * @return Path string.
	 * @throws CharacterCodingException If the path fragments cannot be decoded into
	 *                                  a valid String.
	 */
	public static String convertPathFragments(final byte[][] pathFragments) throws CharacterCodingException {
		final String[] fragmentStrings = new String[pathFragments.length];

		final CharsetDecoder decoder = PATH_ENCODING.newDecoder();
		decoder.onMalformedInput(CodingErrorAction.REPORT);
		decoder.onUnmappableCharacter(CodingErrorAction.REPORT);

		for (int index = 0; index < fragmentStrings.length; index += 1) {
			fragmentStrings[index] = decoder.decode(ByteBuffer.wrap(pathFragments[index])).toString();
		}

		return String.join(PATH_SEPERATOR, fragmentStrings);
	}

	/**
	 * Test the path fragments of a node form a section of file path fragments.
	 *
	 * @param pathFragments  Path fragments of a file path.
	 * @param fragmentIndex  Index of fragment where to start testing at.
	 * @param fragmentOffset Offset into fragment to start testing at.
	 * @param node           Node which is being tested.
	 * @return True if the node is contained in the path fragments, otherwise false.
	 */
	private static boolean equalNodePathFragments(final byte[][] pathFragments, final int fragmentIndex,
			int fragmentOffset, final PathNode node) {
		final int nodeFragmentCount = node.getPathFragmentCount();
		if (nodeFragmentCount == 0) {
			// nodes without fragments have no path fragment presence so always match
			return true;
		}

		if ((nodeFragmentCount == 1) && (node.getFragment(0).length == 0)) {
			// node with termination fragment
			return fragmentOffset == pathFragments[fragmentIndex].length;
		} else if (pathFragments.length < (fragmentIndex + nodeFragmentCount)) {
			// fragment too short
			return false;
		}

		boolean result = true;
		int nodeFragmentIndex = 0;
		while (result && (nodeFragmentIndex < nodeFragmentCount)) {
			final byte[] fragment = pathFragments[fragmentIndex + nodeFragmentIndex];
			final byte[] nodeFragment = node.getFragment(nodeFragmentIndex);
			result = result && ReteraCASCUtils.arraysEquals(fragment, fragmentOffset,
					Math.min(fragmentOffset + nodeFragment.length, fragment.length), nodeFragment, 0,
					nodeFragment.length);
			fragmentOffset = 0;
			nodeFragmentIndex += 1;
		}

		return result;
	}

	/**
	 * Local CASC storage. Used to retrieve file data.
	 */
	private final Storage storage;

	/**
	 * Decoder used to load TVFS files in the TVFS tree.
	 */
	private final TVFSDecoder decoder = new TVFSDecoder();

	/**
	 * TVFS file containing the root directory for the file system.
	 */
	private final TVFSFile tvfsRoot;

	/**
	 * TVFS file cache. Holds all loaded TVFS files for this file system. This
	 * allows the TVFS files to be loaded lazily which could potentially reduce
	 * loading times and memory usage when only some branches of the TVFS file tree
	 * are accessed.
	 */
	private final TreeMap<Key, TVFSFile> tvfsCache = new TreeMap<>();

	/**
	 * Map of all TVFS files used by the TVFS file tree. Keys that are not in this
	 * map are treated as leaf files rather than a nested TVFS file.
	 */
	private final TreeMap<Key, com.hiveworkshop.blizzard.casc.StorageReference> tvfsStorageReferences = new TreeMap<>();

	/**
	 * Construct a TVFS file system from a CASC local storage and build
	 * configuration.
	 *
	 * @param storage            CASC local storage to source files from.
	 * @param buildConfiguration Build configuration of CASC archive.
	 * @throws IOException If an exception occurs when loading the file system.
	 */
	public VirtualFileSystem(final Storage storage, final Map<String, String> buildConfiguration) throws IOException {
		this.storage = storage;

		int vfsNumber = 0;
		String configurationKey;
		while (buildConfiguration
				.containsKey(configurationKey = CONFIGURATION_KEY_PREFIX + Integer.toUnsignedString(++vfsNumber))) {
			final com.hiveworkshop.blizzard.casc.StorageReference storageReference = new com.hiveworkshop.blizzard.casc.StorageReference(
					configurationKey, buildConfiguration);
			tvfsStorageReferences.put(storageReference.getEncodingKey(), storageReference);
		}

		final com.hiveworkshop.blizzard.casc.StorageReference rootReference = new com.hiveworkshop.blizzard.casc.StorageReference(
				CONFIGURATION_KEY_PREFIX + ROOT_KEY, buildConfiguration);
		final ByteBuffer rootBuffer = fetchStoredBuffer(rootReference);
		tvfsRoot = decoder.loadFile(rootBuffer);

		tvfsCache.put(rootReference.getEncodingKey(), tvfsRoot);
	}

	/**
	 * Resolves a TVFS storage reference into a data buffer from the local storage.
	 *
	 * @param storageReference TVFS storage reference.
	 * @return Data buffer containing refered content.
	 * @throws IOException If an exception occurs when fetching the data buffer.
	 */
	private ByteBuffer fetchStoredBuffer(final com.hiveworkshop.blizzard.casc.StorageReference storageReference)
			throws IOException {
		final long size = storageReference.getSize();
		if (size > Integer.MAX_VALUE) {
			throw new MalformedCASCStructureException("stored data too large to process");
		}

		final BankStream bankStream = storage.getBanks(storageReference.getEncodingKey());
		final ByteBuffer storedBuffer = ByteBuffer.allocate((int) size);
		try {
			while (bankStream.hasNextBank()) {
				bankStream.getBank(storedBuffer);
			}
		} catch (final BufferOverflowException e) {
			throw new MalformedCASCStructureException("stored data is bigger than expected");
		}

		if (storedBuffer.hasRemaining()) {
			throw new MalformedCASCStructureException("stored data is smaller than expected");
		}

		storedBuffer.rewind();
		return storedBuffer;
	}

	/**
	 * Method to get all files in the file system.
	 *
	 * @return List of file path results for every file in the file system.
	 * @throws IOException If an exception is thrown when loading a TVFS file or
	 *                     decoding path fragments into a path string.
	 */
	public List<PathResult> getAllFiles() throws IOException {
		final ArrayList<PathResult> pathStringList = new ArrayList<PathResult>();

		final int rootCount = tvfsRoot.getRootNodeCount();
		for (int rootIndex = 0; rootIndex < rootCount; rootIndex += 1) {
			final PathNode root = tvfsRoot.getRootNode(rootIndex);
			recursiveFilePathRetrieve(new byte[1][0], pathStringList, root);
		}

		return pathStringList;
	}

	/**
	 * Recursive function to traverse the TVFS tree and resolve all files in the
	 * file system.
	 *
	 * @param parentPathFragments Path fragments of parent node.
	 * @param resultList          Result list.
	 * @param currentNode         The child node to process.
	 * @throws IOException If an exception occurs when processing the node.
	 */
	private void recursiveFilePathRetrieve(final byte[][] parentPathFragments, final ArrayList<PathResult> resultList,
			final PathNode currentNode) throws IOException {
		byte[][] currentPathFragments = parentPathFragments;

		// process path fragments
		final int fragmentCount = currentNode.getPathFragmentCount();
		if (fragmentCount > 0) {
			int fragmentIndex = 0;
			final byte[] fragment = currentNode.getFragment(fragmentIndex++);

			// expand path fragment array
			int basePathFragmentsIndex = currentPathFragments.length;
			if ((fragmentCount > 1) || (fragment.length > 0)) {
				// first fragment of the node gets merged with last path fragment
				basePathFragmentsIndex -= 1;
			}
			currentPathFragments = Arrays.copyOf(currentPathFragments, basePathFragmentsIndex + fragmentCount);

			// merge fragment
			final byte[] sourceFragment = currentPathFragments[basePathFragmentsIndex];
			byte[] joinedFragment = fragment;
			if (sourceFragment != null) {
				joinedFragment = sourceFragment;
				if (fragment.length != 0) {
					final int joinOffset = sourceFragment.length;
					joinedFragment = Arrays.copyOf(sourceFragment, joinOffset + fragment.length);
					System.arraycopy(fragment, 0, joinedFragment, joinOffset, fragment.length);
				}
			}

			// append path fragments
			currentPathFragments[basePathFragmentsIndex] = joinedFragment;
			for (; fragmentIndex < fragmentCount; fragmentIndex += 1) {
				currentPathFragments[basePathFragmentsIndex + fragmentIndex] = currentNode.getFragment(fragmentIndex);
			}
		}

		if (currentNode instanceof PrefixNode) {
			final PrefixNode prefixNode = (PrefixNode) currentNode;

			final int childCount = prefixNode.getNodeCount();
			for (int index = 0; index < childCount; index += 1) {
				recursiveFilePathRetrieve(currentPathFragments, resultList, prefixNode.getNode(index));
			}
		} else if (currentNode instanceof FileNode) {
			final FileNode fileNode = (FileNode) currentNode;

			final int fileReferenceCount = fileNode.getFileReferenceCount();
			if (fileReferenceCount == 1) {
				// check if nested VFS
				final Key encodingKey = fileNode.getFileReference(0).getEncodingKey();
				final TVFSFile tvfsFile = resolveTVFS(encodingKey);

				if (tvfsFile != null) {
					// file is also a folder
					final byte[][] folderPathFragments = Arrays.copyOf(currentPathFragments,
							currentPathFragments.length + 1);
					folderPathFragments[currentPathFragments.length] = new byte[0];

					final int rootCount = tvfsFile.getRootNodeCount();
					for (int rootIndex = 0; rootIndex < rootCount; rootIndex += 1) {
						final PathNode root = tvfsFile.getRootNode(rootIndex);
						recursiveFilePathRetrieve(folderPathFragments, resultList, root);
					}
				}

				resultList.add(new PathResult(currentNode, currentPathFragments));
			}
		} else {
			throw new IllegalArgumentException("unsupported node type");
		}
	}

	/**
	 * Recursive function to resolve a file node in a TVFS tree from path fragments
	 * representing a file system file path.
	 *
	 * @param pathFragments  Path fragments of a file path.
	 * @param fragmentIndex  Index of fragment where currently testing.
	 * @param fragmentOffset Offset into fragment where currently testing.
	 * @param node           Node which is being tested.
	 * @return Resolved file node.
	 * @throws IOException If an exception occurs when testing the node.
	 */
	private FileNode recursiveResolvePathFragments(final byte[][] pathFragments, int fragmentIndex, int fragmentOffset,
			final PathNode node) throws IOException {
		if (!equalNodePathFragments(pathFragments, fragmentIndex, fragmentOffset, node)) {
			// node not on path
			return null;
		}

		// advance fragment position
		final int nodeFragmentCount = node.getPathFragmentCount();
		if (nodeFragmentCount == 1) {
			final byte[] nodeFragment = node.getFragment(0);
			if (nodeFragment.length == 0) {
				// node with termination fragment
				fragmentIndex += 1;
				fragmentOffset = 0;
			} else {
				// node with less than a whole fragment
				fragmentOffset += nodeFragment.length;
			}

		} else if (nodeFragmentCount > 1) {
			// node which completes 1 or more fragments.
			fragmentIndex += nodeFragmentCount - 1;
			fragmentOffset = node.getFragment(nodeFragmentCount - 1).length;
		}

		// process node
		if (node instanceof PrefixNode) {
			// apply binary search to prefix node to find next node
			final PrefixNode prefixNode = (PrefixNode) node;
			final int childCount = prefixNode.getNodeCount();

			int low = 0;
			int high = childCount - 1;
			while (low <= high) {
				final int middle = (low + high) / 2;
				final PathNode searchNode = prefixNode.getNode(middle);
				final int result = compareNodePathFragments(pathFragments, fragmentIndex, fragmentOffset, searchNode);

				if (result == 0) {
					// possible match
					return recursiveResolvePathFragments(pathFragments, fragmentIndex, fragmentOffset, searchNode);
				} else if (result < 0) {
					high = middle - 1;
				} else {
					low = middle + 1;
				}
			}

		} else if (node instanceof FileNode) {
			final FileNode fileNode = (FileNode) node;

			if ((fragmentIndex == (pathFragments.length - 1))
					&& (fragmentOffset == pathFragments[pathFragments.length - 1].length)) {
				// file found
				return fileNode;
			} else if (fragmentOffset == pathFragments[fragmentIndex].length) {
				// nested TVFS file
				final int fileReferenceCount = fileNode.getFileReferenceCount();
				if (fileReferenceCount == 1) {
					// check if nested VFS
					final Key encodingKey = fileNode.getFileReference(0).getEncodingKey();
					final TVFSFile tvfsFile = resolveTVFS(encodingKey);

					if (tvfsFile != null) {
						// TVFS file to recursively resolve
						if (tvfsFile.getRootNodeCount() != 1) {
							throw new MalformedCASCStructureException("logic only defined for 1 TVFS root node");
						}

						fragmentIndex += 1;
						fragmentOffset = 0;
						return recursiveResolvePathFragments(pathFragments, fragmentIndex, fragmentOffset,
								tvfsFile.getRootNode(0));
					}
				}
			}

		} else {
			throw new IllegalArgumentException("unsupported node type");
		}

		// file not found
		return null;
	}

	/**
	 * Resolves a file from the specified path fragments representing a file system
	 * file path.
	 *
	 * @param pathFragments File path fragments.
	 * @return Path result for a file.
	 * @throws FileNotFoundException If the file does not exist in the file system.
	 * @throws IOException           If an exception occurs when resolving the path
	 *                               fragments.
	 *
	 */
	public PathResult resolvePath(final byte[][] pathFragments) throws IOException {
		if (pathFragments.length == 0) {
			throw new IllegalArgumentException("pathFragments.length must be greater than 0");
		}

		if (tvfsRoot.getRootNodeCount() != 1) {
			throw new MalformedCASCStructureException("logic only defined for 1 root node");
		}

		final FileNode result = recursiveResolvePathFragments(pathFragments, 0, 0, tvfsRoot.getRootNode(0));
		if (result == null) {
			throw new FileNotFoundException("path not in storage");
		}

		return new PathResult(result, pathFragments);
	}

	/**
	 * Resolves a TVFS file from an encoding key. The key is checked if it is a TVFS
	 * file in this file system and then resolved in local storage. The resulting
	 * file is then decoded as a TVFS file and returned. Decoded TVFS files are
	 * cached for improved performance. This method can be called concurrently.
	 *
	 * @param encodingKey Encoding key of TVFS file to resolve.
	 * @return The resolved TVFS file, or null if the encoding key is not for a TVFS
	 *         file of this file system.
	 * @throws IOException If an error occurs when resolving the TVFS file.
	 */
	private TVFSFile resolveTVFS(final Key encodingKey) throws IOException {
		TVFSFile tvfsFile = null;
		final com.hiveworkshop.blizzard.casc.StorageReference storageReference = tvfsStorageReferences.get(encodingKey);
		if (storageReference != null) {
			// is a TVFS file of this file system
			synchronized (this) {
				tvfsFile = tvfsCache.get(encodingKey);
				if (tvfsFile == null) {
					// decode TVFS from storage
					final ByteBuffer rootBuffer = fetchStoredBuffer(storageReference);
					tvfsFile = decoder.loadFile(rootBuffer);

					tvfsCache.put(storageReference.getEncodingKey(), tvfsFile);
				}
			}
		}
		return tvfsFile;
	}
}
