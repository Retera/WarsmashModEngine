package com.hiveworkshop.blizzard.casc.vfs;

import java.util.List;

/**
 * TVFS file containing file system path nodes.
 */
public class TVFSFile {
	private final byte version;
	private final int flags;
	private final int encodingKeySize;
	private final int patchKeySize;
	private final int maximumPathDepth;
	
	private final PathNode[] rootNodes;

	public TVFSFile(final byte version, final int flags, final int encodingKeySize, final int patchKeySize, final int maximumPathDepth,
			final  List<PathNode> rootNodeList) {
		this.version = version;
		this.flags = flags;
		this.encodingKeySize = encodingKeySize;
		this.patchKeySize = patchKeySize;
		this.maximumPathDepth = maximumPathDepth;
		this.rootNodes = rootNodeList.toArray(new PathNode[0]);
	}

	public int getEncodingKeySize() {
		return encodingKeySize;
	}

	public int getFlags() {
		return flags;
	}

	public int getMaximumPathDepth() {
		return maximumPathDepth;
	}

	public int getPatchKeySize() {
		return patchKeySize;
	}

	public PathNode getRootNode(final int index) {
		return rootNodes[index];
	}
	
	public int getRootNodeCount() {
		return rootNodes.length;
	}
	
	public byte getVersion() {
		return version;
	}
}
