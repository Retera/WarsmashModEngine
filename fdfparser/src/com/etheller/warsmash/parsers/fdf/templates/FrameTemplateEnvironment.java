package com.etheller.warsmash.parsers.fdf.templates;

import java.util.HashMap;
import java.util.Map;

public class FrameTemplateEnvironment {
	private final Map<String, String> idToDecoratedString = new HashMap<>();

	public void addDecoratedString(final String id, final String value) {
		this.idToDecoratedString.put(id, value);
	}

	public String getDecoratedString(final String id) {
		final String decoratedString = this.idToDecoratedString.get(id);
		if (decoratedString != null) {
			return decoratedString;
		}
		return id;
	}
}
