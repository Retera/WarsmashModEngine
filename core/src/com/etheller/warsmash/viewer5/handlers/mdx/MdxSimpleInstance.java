package com.etheller.warsmash.viewer5.handlers.mdx;

import com.badlogic.gdx.math.Matrix4;
import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.BatchedInstance;
import com.etheller.warsmash.viewer5.Model;
import com.etheller.warsmash.viewer5.PathSolver;
import com.etheller.warsmash.viewer5.RenderBatch;
import com.etheller.warsmash.viewer5.Scene;
import com.etheller.warsmash.viewer5.Texture;
import com.etheller.warsmash.viewer5.TextureMapper;

public class MdxSimpleInstance extends BatchedInstance {
	public Texture[] replaceableTextures = new Texture[WarsmashConstants.REPLACEABLE_TEXTURE_LIMIT];

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
	public void renderOpaque(final Matrix4 mvp) {
	}

	@Override
	public void renderTranslucent() {
	}

	@Override
	protected void updateLights(final Scene scene2) {
	}

	@Override
	protected void removeLights(final Scene scene2) {
	}

	@Override
	public void load() {
	}

	@Override
	public RenderBatch getBatch(final TextureMapper textureMapper) {
		return new MdxRenderBatch(this.scene, this.model, textureMapper);
	}

	@Override
	public void setReplaceableTexture(final int replaceableTextureId, final String replaceableTextureFile) {
		this.replaceableTextures[replaceableTextureId] = (Texture) this.model.viewer.load(replaceableTextureFile,
				PathSolver.DEFAULT, null);
	}

	@Override
	public void setReplaceableTextureHD(final int replaceableTextureId, final String replaceableTextureFile) {
		throw new UnsupportedOperationException();
	}
}
