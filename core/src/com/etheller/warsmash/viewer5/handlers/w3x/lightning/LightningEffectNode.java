package com.etheller.warsmash.viewer5.handlers.w3x.lightning;

import com.badlogic.gdx.math.Matrix4;
import com.etheller.warsmash.viewer5.*;

public class LightningEffectNode extends BatchedInstance {
	protected LightningEffectNode friend;
	protected boolean showing;

	public LightningEffectNode(LightningEffectModel model) {
		super(model);
	}

	public void setFriend(LightningEffectNode friend) {
		this.friend = friend;
	}

	public void setShowing(boolean showing) {
		this.showing = showing;
	}

	public boolean isShowing() {
		return showing;
	}

	public LightningEffectNode getSource() {
		if(this.paused) {
			// target is a "paused" node
			return friend;
		}
		return this;
	}

	@Override
	public void updateAnimations(float dt) {

	}

	@Override
	public void clearEmittedObjects() {

	}

	@Override
	protected void updateLights(Scene scene2) {
	}

	@Override
	public void renderOpaque(Matrix4 mvp) {
	}

	@Override
	public void renderTranslucent() {

	}

	@Override
	public void load() {

	}

	@Override
	protected RenderBatch getBatch(TextureMapper textureMapper) {
		return new LightningEffectBatch(this.scene, this.model, textureMapper);
	}

	@Override
	public void setReplaceableTexture(int replaceableTextureId, String replaceableTextureFile) {
		throw new UnsupportedOperationException("NOT API");
	}

	@Override
	public void setReplaceableTextureHD(int replaceableTextureId, String replaceableTextureFile) {
		throw new UnsupportedOperationException("NOT API");
	}

	@Override
	protected void removeLights(Scene scene2) {

	}
}
