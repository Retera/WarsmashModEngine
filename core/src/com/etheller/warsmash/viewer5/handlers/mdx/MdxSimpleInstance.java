package com.etheller.warsmash.viewer5.handlers.mdx;

import com.etheller.warsmash.viewer5.BatchedInstance;
import com.etheller.warsmash.viewer5.Model;
import com.etheller.warsmash.viewer5.RenderBatch;
import com.etheller.warsmash.viewer5.TextureMapper;

public class MdxSimpleInstance extends BatchedInstance {

	public MdxSimpleInstance(final Model model) {
		super(model);
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

	@Override
	public RenderBatch getBatch(final TextureMapper textureMapper) {
		return new MdxRenderBatch(this.scene, this.model, textureMapper);
	}

}
