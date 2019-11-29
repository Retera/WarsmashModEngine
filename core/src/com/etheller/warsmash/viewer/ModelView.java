package com.etheller.warsmash.viewer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public abstract class ModelView {
	protected final Model model;
	protected final HashSet<Object> instanceSet;
	protected final HashMap<Scene, SceneData> sceneData;
	protected final int renderedInstances;
	protected final int renderedParticles;
	protected final int renderedBuckets;
	protected final int renderedCalls;

	public ModelView(final Model model) {
		this.model = model;

		this.instanceSet = new HashSet<>();
		this.sceneData = new HashMap<>();

		this.renderedInstances = 0;
		this.renderedParticles = 0;
		this.renderedBuckets = 0;
		this.renderedCalls = 0;
	}

	public abstract Object getShallowCopy();

	public abstract void applyShallowCopy(final Object view);

	@Override
	public abstract boolean equals(Object view);

	@Override
	public abstract int hashCode();

	// public boo
	public void addSceneData(final ModelInstance instance, final Scene scene) {
		if (this.model.ok && (scene != null)) {
			SceneData data = this.sceneData.get(scene);

			if (data == null) {
				data = this.createSceneData(scene);

				this.sceneData.put(scene, data);
			}

		}
	}

	private SceneData createSceneData(final Scene scene) {
		return new SceneData(scene, this);
	}

	public static final class SceneData {
		public final Scene scene;
		public final ModelView modelView;
		public final int baseIndex = 0;
		public final List<ModelInstance> instances = new ArrayList<>();
		public final List<Bucket> buckets = new ArrayList<>();
		public final int usedBuckets = 0;

		public SceneData(final Scene scene, final ModelView modelView) {
			this.scene = scene;
			this.modelView = modelView;
		}

	}
}
