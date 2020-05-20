package com.etheller.warsmash.parsers.fdf.frames.base;

import java.util.ArrayList;
import java.util.List;

public class Frame {
	private String name;
	private float width;
	private float height;
	private final List<Frame> innerFrames = new ArrayList<>();

	public String getName() {
		return this.name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public float getWidth() {
		return this.width;
	}

	public float getHeight() {
		return this.height;
	}

	public void setWidth(final float width) {
		this.width = width;
	}

	public void setHeight(final float height) {
		this.height = height;
	}

	public List<Frame> getInnerFrames() {
		return this.innerFrames;
	}
}
