package com.etheller.warsmash.viewer;

import com.badlogic.gdx.graphics.GL20;
import com.etheller.warsmash.viewer.ModelView.SceneData;

public class Bucket {
	private final ModelView modelView;
	private final Model model;
	private final int count;

	public Bucket(final ModelView modelView) {
		final Model model = modelView.model;
		final GL20 gl = model.getViewer().gl;

		this.modelView = modelView;
		this.model = model;
		this.count = 0;

//		this.instanceIdBuffer =
	}

	public int fill(final SceneData data, final int baseInstance, final Scene scene) {
		// Make believe the bucket is now filled with data for all instances.
		// This is because if a non-specific bucket implementation is supplied,
		// instancing isn't used, so batching is irrelevant.
		return data.instances.size();
	}
}
