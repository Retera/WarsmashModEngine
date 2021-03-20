package com.etheller.warsmash.viewer5;

import java.util.ArrayList;
import java.util.List;

import com.etheller.warsmash.viewer5.handlers.ModelHandler;

public abstract class Model<HANDLER extends ModelHandler> extends HandlerResource<HANDLER> {
	public final Bounds bounds;
	public List<ModelInstance> preloadedInstances;

	public Model(final HANDLER handler, final ModelViewer viewer, final String extension, final PathSolver pathSolver,
			final String fetchUrl) {
		super(viewer, extension, pathSolver, fetchUrl, handler);
		this.bounds = new Bounds();
		this.preloadedInstances = new ArrayList<>();
	}

	protected abstract ModelInstance createInstance(int type);

	public ModelInstance addInstance() {
		return addInstance(0);
	}

	public ModelInstance addInstance(final int type) {
		final ModelInstance instance = createInstance(type);

		if (this.ok) {
			instance.load();
		}
		else {
			this.preloadedInstances.add(instance);
		}

		return instance;
	}
}
