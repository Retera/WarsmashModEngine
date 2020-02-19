package com.etheller.warsmash.viewer5.handlers.mdx;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.Matrix4;

public abstract class GenericGroup {
	public final List<Integer> objects;

	public abstract void render(MdxComplexInstance instance, Matrix4 mvp);

	public GenericGroup() {
		this.objects = new ArrayList<>(); // TODO IntArrayList
	}

}
