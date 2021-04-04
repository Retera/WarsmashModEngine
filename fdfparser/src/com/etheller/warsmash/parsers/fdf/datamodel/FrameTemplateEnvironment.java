package com.etheller.warsmash.parsers.fdf.datamodel;

import java.util.HashMap;
import java.util.Map;

public class FrameTemplateEnvironment {
	private final Map<String, String> idToDecoratedString = new HashMap<>();
	private final Map<String, FrameDefinition> idToFrame = new HashMap<>();

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

	public void put(final String id, final FrameDefinition frame) {
		this.idToFrame.put(id, frame);
	}

	public FrameDefinition getFrame(final String id) {
		return this.idToFrame.get(id);
	}
}
