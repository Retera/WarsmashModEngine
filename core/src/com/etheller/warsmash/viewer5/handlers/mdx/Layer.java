package com.etheller.warsmash.viewer5.handlers.mdx;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.hiveworkshop.rms.parsers.mdlx.AnimationMap;
import com.hiveworkshop.rms.parsers.mdlx.MdlxLayer;

/**
 * An MDX layer.
 */
public class Layer extends AnimatedObject {
	public int index;
	public int priorityPlane;
	public int filterMode;
	public int textureId;
	public int coordId;
	public float alpha;
	public int unshaded;
	public int sphereEnvironmentMap;
	public int twoSided;
	public int unfogged;
	public int noDepthTest;
	public int noDepthSet;
	public int lightOmitOffset;
	public boolean depthMaskValue;
	public int blendSrc;
	public int blendDst;
	public boolean blended;
	public TextureAnimation textureAnimation;

	public Layer(final MdxModel model, final MdlxLayer layer, final int layerId, final int priorityPlane) {
		super(model, layer);

		final MdlxLayer.FilterMode filterMode = layer.getFilterMode();
		final int textureAnimationId = layer.getTextureAnimationId();
		final GL20 gl = model.viewer.gl;

		this.index = layerId;
		this.priorityPlane = priorityPlane;
		this.filterMode = filterMode.ordinal();
		this.textureId = layer.getTextureId();
		this.coordId = (int) layer.getCoordId();
		this.alpha = layer.getAlpha();

		final int flags = layer.getFlags();

		this.unshaded = flags & MdlxLayer.Flags.UNSHADED;
		this.sphereEnvironmentMap = flags & MdlxLayer.Flags.SPHERE_ENVIRONMENT_MAP;
		this.twoSided = flags & MdlxLayer.Flags.TWO_SIDED;
		this.unfogged = flags & MdlxLayer.Flags.UNFOGGED;
		this.noDepthTest = flags & MdlxLayer.Flags.NO_DEPTH_TEST;
		this.noDepthSet = flags & MdlxLayer.Flags.NO_DEPTH_SET;
		this.lightOmitOffset = ((flags & MdlxLayer.Flags.WARSMASH_ONLY_NOT_EXTERIOR_LIT) != 0) ? 1 : 0;

		this.depthMaskValue = ((filterMode == MdlxLayer.FilterMode.NONE)
				|| (filterMode == MdlxLayer.FilterMode.TRANSPARENT));

		this.blendSrc = 0;
		this.blendDst = 0;
		this.blended = (filterMode.ordinal() > 1);

		if (this.blended) {
			final int[] result = FilterMode.layerFilterMode(filterMode);
			this.blendSrc = result[0];
			this.blendDst = result[1];
		}

		if (textureAnimationId != -1) {
			final TextureAnimation textureAnimation = model.getTextureAnimations().get(textureAnimationId);

			if (textureAnimation != null) {
				this.textureAnimation = textureAnimation;
			}
		}

		this.addVariants(AnimationMap.KMTA.getWar3id(), "alpha");
		this.addVariants(AnimationMap.KMTF.getWar3id(), "textureId");
	}

	public void bind(final ShaderProgram shader) {
		final GL20 gl = this.model.viewer.gl;

		// gl.uniform1f(shader.uniforms.u_unshaded, this.unshaded);
		shader.setUniformf("u_filterMode", this.filterMode);

		if (this.blended) {
			gl.glEnable(GL20.GL_BLEND);
			gl.glBlendFunc(this.blendSrc, this.blendDst);
		}
		else {
			gl.glDisable(GL20.GL_BLEND);
		}

		if (this.twoSided != 0) {
			gl.glDisable(GL20.GL_CULL_FACE);
		}
		else {
			gl.glEnable(GL20.GL_CULL_FACE);
		}

		if (this.noDepthTest != 0) {
			gl.glDisable(GL20.GL_DEPTH_TEST);
		}
		else {
			gl.glEnable(GL20.GL_DEPTH_TEST);
		}

		if (this.noDepthSet != 0) {
			gl.glDepthMask(false);
		}
		else {
			gl.glDepthMask(this.depthMaskValue);
		}
	}

	public void bindBlended(final ShaderProgram shader) {
		final GL20 gl = this.model.viewer.gl;

		// gl.uniform1f(shader.uniforms.u_unshaded, this.unshaded);
		shader.setUniformf("u_filterMode", this.filterMode);

		gl.glEnable(GL20.GL_BLEND);
		if ((this.blendSrc == 0) && (this.blendDst == 0)) {
			gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		}
		else {
			gl.glBlendFunc(this.blendSrc, this.blendDst);
		}

		if (this.twoSided != 0) {
			gl.glDisable(GL20.GL_CULL_FACE);
		}
		else {
			gl.glEnable(GL20.GL_CULL_FACE);
		}

		if (this.noDepthTest != 0) {
			gl.glDisable(GL20.GL_DEPTH_TEST);
		}
		else {
			gl.glEnable(GL20.GL_DEPTH_TEST);
		}

		if (this.noDepthSet != 0) {
			gl.glDepthMask(false);
		}
		else {
			gl.glDepthMask(this.depthMaskValue);
		}
	}

	public int getAlpha(final float[] out, final int sequence, final int frame, final int counter) {
		return this.getScalarValue(out, AnimationMap.KMTA.getWar3id(), sequence, frame, counter, this.alpha);
	}

	public int getTextureId(final long[] out, final int sequence, final int frame, final int counter) {
		return this.getScalarValue(out, AnimationMap.KMTF.getWar3id(), sequence, frame, counter, this.textureId);
	}
}
