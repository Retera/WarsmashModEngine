package com.etheller.warsmash.viewer5.handlers.w3x.lightning;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Matrix4;
import com.etheller.warsmash.viewer5.*;

public class LightningEffectNode extends BatchedInstance {
	protected LightningEffectNode friend;
	protected boolean showing;
	protected boolean source;
	protected float textureAnimationPosition;
	protected float[] color;
	protected float lifeSpanRemaining;

	public LightningEffectNode(LightningEffectModel model) {
		super(model);
		this.color = new float[4];
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

	public void setColor(Color color) {
		this.color[0] = color.r;
		this.color[1] = color.g;
		this.color[2] = color.b;
		this.color[3] = color.a;
	}

	public void setColor(float r, float g, float b, float a) {
		this.color[0] = r;
		this.color[1] = g;
		this.color[2] = b;
		this.color[3] = a;
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
			if(lifeSpanRemaining > 0) {
				lifeSpanRemaining -= dt;
				if(lifeSpanRemaining <= 0) {
					lifeSpanRemaining = 0;
				}
				color[3] = lifeSpanRemaining / model.getDuration();
			}
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
		final LightningEffectModel model = (LightningEffectModel) this.model;
		System.arraycopy(model.getColor(), 0, color, 0, color.length);
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

	public void setLifeSpanRemaining(float duration) {
		this.lifeSpanRemaining = duration;
	}
}
