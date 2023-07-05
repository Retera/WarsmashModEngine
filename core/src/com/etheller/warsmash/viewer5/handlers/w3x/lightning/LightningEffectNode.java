package com.etheller.warsmash.viewer5.handlers.w3x.lightning;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.etheller.warsmash.viewer5.*;

public class LightningEffectNode extends BatchedInstance {
	protected LightningEffectNode friend;
	protected boolean showing;
	protected boolean source;
	protected float textureAnimationPosition;

	public LightningEffectNode(LightningEffectModel model) {
		super(model);
	}

	public void setFriend(LightningEffectNode friend) {
		this.friend = friend;
	}

	public boolean isShowing() {
		return showing;
	}

	public void setShowing(boolean showing) {
		this.showing = showing;
	}

	public boolean isSource() {
		return source;
	}

	public LightningEffectNode getSource() {
		if (!this.source) {
			return friend;
		}
		return this;
	}

	public void setSource(boolean source) {
		this.source = source;
	}

	@Override
	public void updateAnimations(float dt) {
		if (this.showing && !this.friend.showing && this.friend.source) {
			this.friend.updateAnimations(dt);
		}
		else {
			final LightningEffectModel model = (LightningEffectModel) this.model;
			float textureCoordinateSpeed = 3.5f;
			textureAnimationPosition += dt * model.getTexCoordScale() * textureCoordinateSpeed;
			textureAnimationPosition = (((textureAnimationPosition) % 1.0f) + 1.0f) % 1.0f;
		}
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
