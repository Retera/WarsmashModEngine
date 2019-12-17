package com.etheller.warsmash.viewer5;

public class SolvedPath {
	public String finalSrc;
	public String extension;
	public boolean fetch;

	public SolvedPath(final String finalSrc, final String extension, final boolean fetch) {
		this.finalSrc = finalSrc;
		this.extension = extension;
		this.fetch = fetch;
	}

	public String getFinalSrc() {
		return this.finalSrc;
	}

	public String getExtension() {
		return this.extension;
	}

	public boolean isFetch() {
		return this.fetch;
	}

}
