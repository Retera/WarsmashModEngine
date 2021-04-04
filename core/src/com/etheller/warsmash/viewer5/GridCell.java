package com.etheller.warsmash.viewer5;

import java.util.ArrayList;
import java.util.List;

import com.etheller.warsmash.util.RenderMathUtils;

public class GridCell {
	public final float left;
	public final float right;
	public final float bottom;
	public final float top;
	public int plane;
	final List<ModelInstance> instances;
	public final boolean visible;

	public GridCell(final float left, final float right, final float bottom, final float top) {
		this.left = left;
		this.right = right;
		this.bottom = bottom;
		this.top = top;
		this.plane = -1;
		this.instances = new ArrayList<ModelInstance>();
		this.visible = false;
	}

	public void add(final ModelInstance instance) {
		this.instances.add(instance);
	}

	public void remove(final ModelInstance instance) {
		this.instances.remove(instance);
	}

	public void clear() {
		this.instances.clear();
	}

	public boolean isVisible(final Camera camera) {
		this.plane = RenderMathUtils.testCell(camera.planes, this.left, this.right, this.bottom, this.top, this.plane);

		return this.plane == -1;
	}
}
