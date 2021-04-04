package com.hiveworkshop.blizzard.casc.vfs;

import java.util.List;

/**
 * A file system node containing a logical file.
 */
public class FileNode extends PathNode {
	private final StorageReference[] references;
	
	protected FileNode(final List<byte[]> pathFragments, final List<StorageReference> references) {
		super(pathFragments);
		this.references = references.toArray(new StorageReference[0]);
	}

	public int getFileReferenceCount() {
		return references.length;
	}
	
	public StorageReference getFileReference(final int index) {
		return references[index];
	}

}
