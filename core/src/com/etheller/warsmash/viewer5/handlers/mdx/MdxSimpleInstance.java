package com.etheller.warsmash.viewer5.handlers.mdx;

import com.etheller.warsmash.viewer5.Model;
import com.etheller.warsmash.viewer5.ModelInstance;

public class MdxSimpleInstance extends ModelInstance {

	public MdxSimpleInstance(final Model model) {
		super(model);
	}

	@Override
	public boolean isBatched() {
		return true;
	}

	@Override
	public void updateAnimations(final float dt) {
	}

	@Override
	public void clearEmittedObjects() {
	}

	@Override
	public void renderOpaque() {
	}

	@Override
	public void renderTranslucent() {
	}

	@Override
	public void load() {
	}

}
