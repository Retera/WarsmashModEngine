package com.hiveworkshop.blizzard.casc.vfs;

import java.util.List;

/**
 * Prefix nodes generate a path prefix for other nodes.
 */
public class PrefixNode extends PathNode {
	/**
	 * Array of child node that this node forms a prefix of.
	 */
	private final PathNode[] nodes;

	protected PrefixNode(final List<byte[]> pathFragments, final List<PathNode> nodes) {
		super(pathFragments);
		this.nodes = nodes.toArray(new PathNode[0]);
	}
	
	public int getNodeCount() {
		return nodes.length;
	}
	
	public PathNode getNode(final int index) {
		return nodes[index];
	}
}
