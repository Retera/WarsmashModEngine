package com.etheller.warsmash.parsers.fdf;

public class FDFNamedString {
	private final String name;
	private final String value;

	public FDFNamedString(final String name, final String value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return this.name;
	}

	public String getValue() {
		return this.value;
	}
}
