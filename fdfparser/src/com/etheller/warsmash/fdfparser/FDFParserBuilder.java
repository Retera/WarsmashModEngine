package com.etheller.warsmash.fdfparser;

public interface FDFParserBuilder {
	/**
	 *
	 * @param path
	 * @return Returns null if the given file is not found.
	 */
	FDFParser build(String path);
}
