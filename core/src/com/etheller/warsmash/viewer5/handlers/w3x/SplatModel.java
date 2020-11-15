package com.etheller.warsmash.viewer5.handlers.w3x;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.etheller.warsmash.util.RenderMathUtils;
import com.etheller.warsmash.viewer5.Texture;

/**
 * TODO this is copied from RivSoft stuff.
 * https://github.com/d07RiV/wc3data/blob/3435e9728663825d892693318d0a0bb823dfad8c/src/mdx/viewer/handlers/w3x/splatmodel.js
 *
 * Shouldn't this just be a geomtry shader that takes X/Y/Texture as input and
 * renders a splat, so that we can simply change the X/Y attribute values and
 * move around the unit selection circles without memory allocations? For now I
 * plan to simply port the RivSoft stuff, and come back later.
 */
public class SplatModel {
	private static final int MAX_VERTICES = 65000;
	private final Texture texture;
	private final List<Batch> batches;
	public final float[] color;
	private final List<float[]> locations;
	private final List<SplatMover> splatInstances;
	private final boolean unshaded;

	public SplatModel(final GL30 gl, final Texture texture, final List<float[]> locations, final float[] centerOffset,
			final List<Consumer<SplatMover>> unitMapping, final boolean unshaded) {
		this.texture = texture;
		this.unshaded = unshaded;
		this.batches = new ArrayList<>();
		this.color = new float[] { 1, 1, 1, 1 };

		this.locations = locations;
		if (unitMapping != null) {
			this.splatInstances = new ArrayList<>();
			for (int i = 0; i < unitMapping.size(); i++) {
				this.splatInstances.add(new SplatMover(this));
			}
		}
		else {
			this.splatInstances = null;
		}
		loadBatches(gl, centerOffset);
		if (unitMapping != null) {
			if (this.splatInstances.size() != unitMapping.size()) {
				throw new IllegalStateException();
			}
			for (int i = 0; i < this.splatInstances.size(); i++) {
				unitMapping.get(i).accept(this.splatInstances.get(i));
			}
		}
	}

	public void compact(final GL30 gl, final float[] centerOffset) {
		// delete all the batches
		for (final Batch b : this.batches) {
			// Vertices
			gl.glDeleteBuffer(b.vertexBuffer);

			// Faces.
			gl.glDeleteBuffer(b.faceBuffer);
		}
		this.batches.clear();

		loadBatches(gl, centerOffset);
	}

	private void loadBatches(final GL30 gl, final float[] centerOffset) {
		final List<float[]> vertices = new ArrayList<>();
		final List<float[]> uvs = new ArrayList<>();
		final List<int[]> indices = new ArrayList<>();
		final List<SplatMover> batchRenderUnits = new ArrayList<>();
		final int instances = this.locations.size();
		for (int idx = 0; idx < instances; ++idx) {
			final float[] locs = this.locations.get(idx);
			final float x0 = locs[0];
			final float y0 = locs[1];
			final float x1 = locs[2];
			final float y1 = locs[3];
			final float zoffs = locs[4];

			final float centerOffsetX = centerOffset[0];
			final float centerOffsetY = centerOffset[1];
			final int ix0 = (int) Math.floor((x0 - centerOffsetX) / 128.0);
			final int ix1 = (int) Math.ceil((x1 - centerOffsetX) / 128.0);
			final int iy0 = (int) Math.floor((y0 - centerOffsetY) / 128.0);
			final int iy1 = (int) Math.ceil((y1 - centerOffsetY) / 128.0);

			final int newVerts = ((iy1 - iy0) + 1) * ((ix1 - ix0) + 1);
			final int maxPossibleVerts = ((int) Math.ceil((y1 - y0) / 128.0) + 2)
					* ((int) Math.ceil((x1 - x0) / 128.0) + 2);
			final int maxPossibleFaces = ((int) Math.ceil((y1 - y0) / 128.0) + 1)
					* ((int) Math.ceil((x1 - x0) / 128.0) + 1);

			int start = vertices.size();
			final SplatMover splatMover = (this.splatInstances == null) ? null
					: this.splatInstances.get(idx).reset(start * 3 * 4, indices.size() * 6 * 2, idx);

			final int numVertsToCrate = splatMover == null ? newVerts : maxPossibleVerts;
			if (numVertsToCrate > MAX_VERTICES) {
				continue;
			}

			final int step = (ix1 - ix0) + 1;
			if ((start + numVertsToCrate) > MAX_VERTICES) {
				this.addBatch(gl, vertices, uvs, indices, batchRenderUnits);
				vertices.clear();
				uvs.clear();
				indices.clear();
				batchRenderUnits.clear();
				start = 0;
			}

			final float uvXScale = x1 - x0;
			final float uvYScale = y1 - y0;
			for (int iy = iy0; iy <= iy1; ++iy) {
				final float y = (iy * 128.0f) + centerOffsetY;
				for (int ix = ix0; ix <= ix1; ++ix) {
					final float x = (ix * 128.0f) + centerOffsetX;
					final float[] vertex = new float[] { x, y, zoffs };
					vertices.add(vertex);
					final float[] uv = new float[] { (x - x0) / uvXScale, 1.0f - ((y - y0) / uvYScale) };
					uvs.add(uv);
					if (splatMover != null) {
						splatMover.vertices.add(vertex);
						splatMover.uvs.add(uv);
					}
				}
			}
			if (splatMover != null) {
				splatMover.uvXScale = uvXScale;
				splatMover.uvYScale = uvYScale;
				splatMover.locs = locs;
				splatMover.ix0 = ix0;
				splatMover.iy0 = iy0;
				splatMover.ix1 = ix1;
				splatMover.iy1 = iy1;

				final float y = (iy1 * 128.0f) + centerOffsetY;
				final float x = (ix1 * 128.0f) + centerOffsetX;
				while (splatMover.vertices.size() < maxPossibleVerts) {
					final float[] vertex = new float[] { x, y, zoffs };
					vertices.add(vertex);
					final float[] uv = new float[] { (x - x0) / uvXScale, 1.0f - ((y - y0) / uvYScale) };
					uvs.add(uv);
					splatMover.vertices.add(vertex);
					splatMover.uvs.add(uv);
				}
			}
			for (int i = 0; i < (iy1 - iy0); ++i) {
				for (int j = 0; j < (ix1 - ix0); ++j) {
					final int i0 = start + (i * step) + j;
					final int[] indexArray = new int[] { i0, i0 + 1, i0 + step, i0 + 1, i0 + step + 1, i0 + step };
					indices.add(indexArray);
					if (splatMover != null) {
						splatMover.indices.add(indexArray);
					}
				}
			}
			if (this.splatInstances != null) {
				batchRenderUnits.add(splatMover);

				while (splatMover.indices.size() < maxPossibleFaces) {
					final int i0 = start;
					final int[] indexArray = new int[] { i0, i0, i0, i0, i0, i0 };
					indices.add(indexArray);
					splatMover.indices.add(indexArray);
				}
			}

		}
		if (indices.size() > 0) {
			this.addBatch(gl, vertices, uvs, indices, batchRenderUnits);
		}
	}

	private void addBatch(final GL30 gl, final List<float[]> vertices, final List<float[]> uvs,
			final List<int[]> indices, final List<SplatMover> batchRenderUnits) {
		final int uvsOffset = vertices.size() * 3 * 4;

		final int vertexBuffer = gl.glGenBuffer();
		gl.glBindBuffer(GL30.GL_ARRAY_BUFFER, vertexBuffer);
		gl.glBufferData(GL30.GL_ARRAY_BUFFER, uvsOffset + (uvs.size() * 4 * 2), null, GL30.GL_STATIC_DRAW);
		gl.glBufferSubData(GL30.GL_ARRAY_BUFFER, 0, vertices.size() * 4 * 5, RenderMathUtils.wrap(vertices));
		gl.glBufferSubData(GL30.GL_ARRAY_BUFFER, uvsOffset, uvs.size() * 4 * 2, RenderMathUtils.wrap(uvs));

		final int faceBuffer = gl.glGenBuffer();
		gl.glBindBuffer(GL30.GL_ELEMENT_ARRAY_BUFFER, faceBuffer);
		gl.glBufferData(GL30.GL_ELEMENT_ARRAY_BUFFER, indices.size() * 6 * 2, RenderMathUtils.wrapFaces(indices),
				GL30.GL_STATIC_DRAW);

		this.batches.add(new Batch(uvsOffset, vertexBuffer, faceBuffer, indices.size() * 6));
		for (final SplatMover mover : batchRenderUnits) {
			mover.vertexBuffer = vertexBuffer;
			mover.uvsOffset = uvsOffset;
			mover.faceBuffer = faceBuffer;
		}
	}

	public void render(final GL30 gl, final ShaderProgram shader) {
		// Texture

		gl.glActiveTexture(GL30.GL_TEXTURE1);
		gl.glBindTexture(GL30.GL_TEXTURE_2D, this.texture.getGlHandle());
		shader.setUniformi("u_show_lighting", this.unshaded ? 0 : 1);
		shader.setUniform4fv("u_color", this.color, 0, 4);

		for (final Batch b : this.batches) {
			// Vertices
			gl.glBindBuffer(GL30.GL_ARRAY_BUFFER, b.vertexBuffer);
			shader.setVertexAttribute("a_position", 3, GL30.GL_FLOAT, false, 12, 0);
			shader.setVertexAttribute("a_uv", 2, GL30.GL_FLOAT, false, 8, b.uvsOffset);

			// Faces.
			gl.glBindBuffer(GL30.GL_ELEMENT_ARRAY_BUFFER, b.faceBuffer);

			// Draw
			gl.glDrawElements(GL30.GL_TRIANGLES, b.elements, GL30.GL_UNSIGNED_SHORT, 0);
		}

	}

	public SplatMover add(final float x, final float y, final float w, final float h, final float zDepthUpward,
			final float[] centerOffset) {
		this.locations.add(new float[] { x, y, w, h, zDepthUpward });
		final SplatMover splatMover;
		if (this.splatInstances != null) {
			splatMover = new SplatMover(this);
			this.splatInstances.add(splatMover);
		}
		else {
			splatMover = null;
		}
		compact(Gdx.gl30, centerOffset);
		return splatMover;
	}

	private static final class Batch {
		private final int uvsOffset;
		private final int vertexBuffer;
		private final int faceBuffer;
		private final int elements;

		public Batch(final int uvsOffset, final int vertexBuffer, final int faceBuffer, final int elements) {
			this.uvsOffset = uvsOffset;
			this.vertexBuffer = vertexBuffer;
			this.faceBuffer = faceBuffer;
			this.elements = elements;
		}
	}

	public static final class SplatMover {
		public int faceBuffer;
		public int uvsOffset;
		public int iy1;
		public int ix1;
		public int iy0;
		public int ix0;
		public float[] locs;
		public float uvYScale;
		public float uvXScale;
		private int vertexBuffer;
		private int startOffset;
		private int start;
		private final List<float[]> vertices = new ArrayList<>();
		private final List<float[]> uvs = new ArrayList<>();
		private final List<int[]> indices = new ArrayList<>();
		private int indicesStartOffset;
		private int index;
		private final SplatModel splatModel;

		private SplatMover(final SplatModel splatModel) {
			this.splatModel = splatModel;
		}

		private SplatMover reset(final int i, final int indicesStartOffset, final int index) {
			this.startOffset = i;
			this.indicesStartOffset = indicesStartOffset;
			this.start = i / 12;
			this.index = index;
			this.vertices.clear();
			this.uvs.clear();
			this.indices.clear();
			return this;
		}

		public void move(final float deltaX, final float deltaY, final float[] centerOffset) {
			this.locs[0] += deltaX;
			this.locs[2] += deltaX;
			this.locs[1] += deltaY;
			this.locs[3] += deltaY;
			final float x0 = this.locs[0];
			final float y0 = this.locs[1];
			final float x1 = this.locs[2];
			final float y1 = this.locs[3];

			final float centerOffsetX = centerOffset[0];
			final float centerOffsetY = centerOffset[1];
			final int ix0 = (int) Math.floor((x0 - centerOffsetX) / 128.0);
			final int ix1 = (int) Math.ceil((x1 - centerOffsetX) / 128.0);
			final int iy0 = (int) Math.floor((y0 - centerOffsetY) / 128.0);
			final int iy1 = (int) Math.ceil((y1 - centerOffsetY) / 128.0);

			final GL30 gl = Gdx.gl30;
			gl.glBindBuffer(GL30.GL_ARRAY_BUFFER, this.vertexBuffer);
			if ((ix0 != this.ix0) || (iy0 != this.iy0) || (ix1 != this.ix1) || (iy1 != this.iy1)) {
				// splat geometry has moved, difficult case
				final float newVerts = ((iy1 - iy0) + 1) * ((ix1 - ix0) + 1);
				if (newVerts <= this.uvs.size()) {
				}
				int vertexIndex = 0;
				float y = 0;
				float x = 0;
				for (int iy = iy0; iy <= iy1; ++iy) {
					y = (iy * 128.0f) + centerOffsetY;
					for (int ix = ix0; ix <= ix1; ++ix) {
						x = (ix * 128.0f) + centerOffsetX;
						final float[] vertexToUpdate = this.vertices.get(vertexIndex);
						vertexToUpdate[0] = x;
						vertexToUpdate[1] = y;
						final float[] uvItem = this.uvs.get(vertexIndex);
						uvItem[0] = (x - x0) / this.uvXScale;
						uvItem[1] = 1.0f - ((y - y0) / this.uvYScale);
						vertexIndex++;
					}
				}

				for (; vertexIndex < this.vertices.size(); vertexIndex++) {
					final float[] vertexToUpdate = this.vertices.get(vertexIndex);
					vertexToUpdate[0] = x;
					vertexToUpdate[1] = y;
					final float[] uvItem = this.uvs.get(vertexIndex);
					uvItem[0] = (x - x0) / this.uvXScale;
					uvItem[1] = 1.0f - ((y - y0) / this.uvYScale);
				}
				gl.glBufferSubData(GL30.GL_ARRAY_BUFFER, this.startOffset, 4 * 3 * this.vertices.size(),
						RenderMathUtils.wrap(this.vertices));

				final int step = (ix1 - ix0) + 1;
				int faceIndicesIndex = 0;
				for (int i = 0; i < (iy1 - iy0); ++i) {
					for (int j = 0; j < (ix1 - ix0); ++j) {
						final int i0 = this.start + (i * step) + j;
						final int[] indexArr = this.indices.get(faceIndicesIndex++);
						indexArr[0] = i0;
						indexArr[1] = i0 + 1;
						indexArr[2] = i0 + step;
						indexArr[3] = i0 + 1;
						indexArr[4] = i0 + step + 1;
						indexArr[5] = i0 + step;
					}
				}

				for (; faceIndicesIndex < this.indices.size(); faceIndicesIndex++) {
					final int i0 = this.start;
					final int[] indexArr = this.indices.get(faceIndicesIndex);
					for (int i = 0; i < indexArr.length; i++) {
						indexArr[i] = i0;
					}
				}
				gl.glBindBuffer(GL30.GL_ELEMENT_ARRAY_BUFFER, this.faceBuffer);
				gl.glBufferSubData(GL30.GL_ELEMENT_ARRAY_BUFFER, this.indicesStartOffset, 6 * 2 * this.indices.size(),
						RenderMathUtils.wrapFaces(this.indices));
				this.ix0 = ix0;
				this.iy0 = iy0;
				this.ix1 = ix1;
				this.iy1 = iy1;
			}
			else {
				// splat will use same geometry, easy case, just update the UVs

				int index = 0;
				for (int iy = iy0; iy <= iy1; ++iy) {
					final float y = (iy * 128.0f) + centerOffsetY;
					for (int ix = ix0; ix <= ix1; ++ix) {
						final float x = (ix * 128.0f) + centerOffsetX;
						final float[] uvItem = this.uvs.get(index++);
						uvItem[0] = (x - x0) / this.uvXScale;
						uvItem[1] = 1.0f - ((y - y0) / this.uvYScale);
					}
				}
			}
			gl.glBufferSubData(GL30.GL_ARRAY_BUFFER, this.uvsOffset + ((this.startOffset / 3) * 2),
					4 * 2 * this.uvs.size(), RenderMathUtils.wrap(this.uvs));
		}

		public void destroy(final GL30 gl, final float[] centerOffset) {
			this.splatModel.locations.remove(this.index);
			this.splatModel.splatInstances.remove(this.index);
			this.splatModel.compact(gl, centerOffset);
		}

		public void hide() {
			// does not remove the shadow, just makes it not show, so it would still be
			// using GPU resources
			final GL30 gl = Gdx.gl30;
			for (final float[] vertex : this.vertices) {
				for (int i = 0; i < vertex.length; i++) {
					vertex[i] = 0.0f;
				}
			}
			for (final int[] indices : this.indices) {
				for (int i = 0; i < indices.length; i++) {
					indices[i] = 0;
				}
			}
			for (final float[] uv : this.uvs) {
				for (int i = 0; i < uv.length; i++) {
					uv[i] = 0;
				}
			}
			gl.glBindBuffer(GL30.GL_ARRAY_BUFFER, this.vertexBuffer);
			gl.glBufferSubData(GL30.GL_ARRAY_BUFFER, this.startOffset, 4 * 3 * this.vertices.size(),
					RenderMathUtils.wrap(this.vertices));
			gl.glBindBuffer(GL30.GL_ELEMENT_ARRAY_BUFFER, this.faceBuffer);
			gl.glBufferSubData(GL30.GL_ELEMENT_ARRAY_BUFFER, this.indicesStartOffset, 6 * 2 * this.indices.size(),
					RenderMathUtils.wrapFaces(this.indices));
			gl.glBufferSubData(GL30.GL_ARRAY_BUFFER, this.uvsOffset + ((this.startOffset / 3) * 2),
					4 * 2 * this.uvs.size(), RenderMathUtils.wrap(this.uvs));
		}

		public void show(final float[] centerOffset) {
			// It tries to only update if it is located at a new position... but here we are
			// forcing it visible again by putting the position outside the map
			this.ix0 = this.ix1 = this.iy0 = this.iy1 = Integer.MIN_VALUE;
			move(0, 0, centerOffset);
		}
	}
}
