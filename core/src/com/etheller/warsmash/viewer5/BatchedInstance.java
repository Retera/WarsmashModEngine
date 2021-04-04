package com.etheller.warsmash.viewer5;

/**
 * A batched model instance.
 */
public abstract class BatchedInstance extends ModelInstance {

	public BatchedInstance(final Model model) {
		super(model);
	}

	@Override
	public boolean isBatched() {
		return true;
	}
}
