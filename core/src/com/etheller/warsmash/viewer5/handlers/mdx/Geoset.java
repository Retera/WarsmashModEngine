package com.etheller.warsmash.viewer5.handlers.mdx;

import java.util.Arrays;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.etheller.warsmash.viewer5.gl.ANGLEInstancedArrays;
import com.hiveworkshop.rms.parsers.mdlx.MdlxGeoset;

public class Geoset {
	public MdxModel model;
	public int index;
	public int positionOffset;
	public int normalOffset;
	public int uvOffset;
	public final int tangentOffset;
	public int skinOffset;
	public int faceOffset;
	public int vertices;
	public int elements;
	public GeosetAnimation geosetAnimation;
	public Variants variants;
	public boolean hasAlphaAnim;
	public boolean hasColorAnim;
	public boolean hasObjectAnim;
	private final int openGLSkinType;
	private final int skinStride;
	private final int boneCountOffsetBytes;
	public final boolean unselectable;
	public final MdlxGeoset mdlxGeoset;

	public Geoset(final MdxModel model, final int index, final int positionOffset, final int normalOffset,
			final int uvOffset, final int tangentOffset, final int skinOffset, final int faceOffset, final int vertices,
			final int elements, final int openGLSkinType, final int skinStride, final int boneCountOffsetBytes,
			final boolean unselectable, final MdlxGeoset mdlxGeoset) {
		this.model = model;
		this.index = index;
		this.positionOffset = positionOffset;
		this.normalOffset = normalOffset;
		this.uvOffset = uvOffset;
		this.tangentOffset = tangentOffset;
		this.skinOffset = skinOffset;
		this.faceOffset = faceOffset;
		this.vertices = vertices;
		this.elements = elements;
		this.openGLSkinType = openGLSkinType;
		this.skinStride = skinStride;
		this.boneCountOffsetBytes = boneCountOffsetBytes;
		this.unselectable = unselectable;
		this.mdlxGeoset = mdlxGeoset;

		for (final GeosetAnimation geosetAnimation : model.getGeosetAnimations()) {
			if (geosetAnimation.geosetId == index) {
				this.geosetAnimation = geosetAnimation;
			}
		}

		final Variants variants = new Variants(model.getSequences().size());

		final GeosetAnimation geosetAnimation = this.geosetAnimation;
		boolean hasAlphaAnim = false;
		boolean hasColorAnim = false;

		if (geosetAnimation != null) {
			for (int i = 0, l = model.getSequences().size(); i < l; i++) {
				final boolean alpha = geosetAnimation.isAlphaVariant(i);
				final boolean color = geosetAnimation.isColorVariant(i);

				variants.alpha[i] = alpha;
				variants.color[i] = color;
				variants.object[i] = alpha || color;

				hasAlphaAnim = hasAlphaAnim || alpha;
				hasColorAnim = hasColorAnim || color;
			}
		}
		else {
			for (int i = 0, l = model.getSequences().size(); i < l; i++) {
				variants.alpha[i] = false;
				variants.color[i] = false;
				variants.object[i] = false;
			}
		}

		this.variants = variants;
		this.hasAlphaAnim = hasAlphaAnim;
		this.hasColorAnim = hasColorAnim;
		this.hasObjectAnim = hasAlphaAnim || hasColorAnim;
	}

	public int getAlpha(final float[] out, final int sequence, final int frame, final int counter) {
		if (this.geosetAnimation != null) {
			return this.geosetAnimation.getAlpha(out, sequence, frame, counter);
		}

		out[0] = 1;
		return -1;
	}

	public int getColor(final float[] out, final int sequence, final int frame, final int counter) {
		if (this.geosetAnimation != null) {
			return this.geosetAnimation.getAlpha(out, sequence, frame, counter);
		}

		Arrays.fill(out, 1);
		return -1;
	}

	public void bind(final ShaderProgram shader, final int coordId) {
		// TODO use indices instead of strings for attributes
		shader.setVertexAttribute("a_position", 3, GL20.GL_FLOAT, false, 0, this.positionOffset);
		shader.setVertexAttribute("a_normal", 3, GL20.GL_FLOAT, false, 0, this.normalOffset);
		shader.setVertexAttribute("a_uv", 2, GL20.GL_FLOAT, false, 0, this.uvOffset + (coordId * this.vertices * 8));
		shader.setVertexAttribute("a_bones", 4, this.openGLSkinType, false, this.skinStride, this.skinOffset);
		shader.setVertexAttribute("a_boneNumber", 1, this.openGLSkinType, false, this.skinStride,
				this.skinOffset + this.boneCountOffsetBytes);
	}

	public void bindExtended(final ShaderProgram shader, final int coordId) {
		// TODO use indices instead of strings for attributes
		shader.setVertexAttribute("a_position", 3, GL20.GL_FLOAT, false, 0, this.positionOffset);
		shader.setVertexAttribute("a_normal", 3, GL20.GL_FLOAT, false, 0, this.normalOffset);
		shader.setVertexAttribute("a_uv", 2, GL20.GL_FLOAT, false, 0, this.uvOffset + (coordId * this.vertices * 8));
		shader.setVertexAttribute("a_bones", 4, this.openGLSkinType, false, this.skinStride, this.skinOffset);
		shader.setVertexAttribute("a_extendedBones", 4, this.openGLSkinType, false, this.skinStride,
				this.skinOffset + (this.boneCountOffsetBytes / 2));
		shader.setVertexAttribute("a_boneNumber", 1, this.openGLSkinType, false, this.skinStride,
				this.skinOffset + this.boneCountOffsetBytes);
	}

	public void render() {
		final GL20 gl = this.model.viewer.gl;

		gl.glDrawElements(GL20.GL_TRIANGLES, this.elements, GL20.GL_UNSIGNED_SHORT, this.faceOffset);
	}

	public void bindSimple(final ShaderProgram shader) {
		shader.setVertexAttribute("a_position", 3, GL20.GL_FLOAT, false, 0, this.positionOffset);
		shader.setVertexAttribute("a_uv", 2, GL20.GL_FLOAT, false, 0, this.uvOffset);
	}

	public void renderSimple(final int instances) {
		final ANGLEInstancedArrays instancedArrays = this.model.viewer.webGL.instancedArrays;
		instancedArrays.glDrawElementsInstancedANGLE(GL20.GL_TRIANGLES, this.elements, GL20.GL_UNSIGNED_SHORT,
				this.faceOffset, instances);
	}

	public void bindHd(final ShaderProgram shader, final SkinningType skinningType, final int coordId) {
		shader.setVertexAttribute("a_position", 3, GL20.GL_FLOAT, false, 0, this.positionOffset);
		shader.setVertexAttribute("a_normal", 3, GL20.GL_FLOAT, false, 0, this.normalOffset);
		shader.setVertexAttribute("a_uv", 2, GL20.GL_FLOAT, false, 0, this.uvOffset + (coordId * this.vertices * 8));

		shader.setVertexAttribute("a_tangent", 4, GL20.GL_FLOAT, false, 0, this.tangentOffset);

		// TODO ghostwolf splits here and allows HD with non-skin, or SD with skin
		shader.setVertexAttribute("a_bones", 4, GL20.GL_UNSIGNED_BYTE, false, 8, this.skinOffset);
		shader.setVertexAttribute("a_weights", 4, GL20.GL_UNSIGNED_BYTE, true, 8, this.skinOffset + 4);
	}

	private static final class Variants {
		private final boolean[] alpha;
		private final boolean[] color;
		private final boolean[] object;

		public Variants(final int size) {
			this.alpha = new boolean[size];
			this.color = new boolean[size];
			this.object = new boolean[size];
		}

	}
}
