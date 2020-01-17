package com.etheller.warsmash.viewer5.handlers.mdx;

import java.util.ArrayList;
import java.util.List;

public abstract class GenericGroup {
	public final List<Integer> objects;

	public abstract void render(MdxComplexInstance instance);

	public GenericGroup() {
		this.objects = new ArrayList<>(); // TODO IntArrayList
	}

}
