package com.etheller.warsmash.viewer5.handlers.w3x;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.etheller.warsmash.util.RenderMathUtils;
import com.etheller.warsmash.viewer5.ViewerTextureRenderable;

/**
 * TODO this is copied from RivSoft stuff.
 * https://github.com/d07RiV/wc3data/blob/3435e9728663825d892693318d0a0bb823dfad8c/src/mdx/viewer/handlers/w3x/splatmodel.js
 *
 * Shouldn't this just be a geomtry shader that takes X/Y/Texture as input and
 * renders a splat, so that we can simply change the X/Y attribute values and
 * move around the unit selection circles without memory allocations? For now I
 * plan to simply port the RivSoft stuff, and come back later.
 */
public class SplatModel implements Comparable<SplatModel> {
	private static final int MAX_VERTICES = 65000;
	private static final float NO_ABS_HEIGHT = -257f;
	private final ViewerTextureRenderable texture;
	private final List<Batch> batches;
	public final float[] color;
	private final List<float[]> locations;
	private final List<SplatMover> splatInstances;
	private final boolean unshaded;
	private final boolean noDepthTest;
	private final boolean highPriority;

	public SplatModel(final GL30 gl, final ViewerTextureRenderable texture, final List<float[]> locations,
			final float[] centerOffset, final List<Consumer<SplatMover>> unitMapping, final boolean unshaded,
			final boolean noDepthTest, final boolean highPriority) {
		this.texture = texture;
		this.unshaded = unshaded;
		this.noDepthTest = noDepthTest;
		this.highPriority = highPriority;
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
		final List<float[]> absoluteHeights = new ArrayList<>();
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
				this.addBatch(gl, vertices, uvs, absoluteHeights, indices, batchRenderUnits);
				vertices.clear();
				uvs.clear();
				absoluteHeights.clear();
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
					final float[] absHeight = new float[] { NO_ABS_HEIGHT };
					absoluteHeights.add(absHeight);
					if (splatMover != null) {
						splatMover.vertices.add(vertex);
						splatMover.uvs.add(uv);
						splatMover.absoluteHeights.add(absHeight);
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
					final float[] absHeight = new float[] { NO_ABS_HEIGHT };
					absoluteHeights.add(absHeight);
					splatMover.vertices.add(vertex);
					splatMover.uvs.add(uv);
					splatMover.absoluteHeights.add(absHeight);
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
			this.addBatch(gl, vertices, uvs, absoluteHeights, indices, batchRenderUnits);
		}
		if (this.splatInstances != null) {
			for (final SplatMover splatMover : this.splatInstances) {
				if (splatMover.hidden) {
					splatMover.hide();
				}
			}
		}
	}

	private void addBatch(final GL30 gl, final List<float[]> vertices, final List<float[]> uvs,
			final List<float[]> absoluteHeights, final List<int[]> indices, final List<SplatMover> batchRenderUnits) {
		final int uvsOffset = vertices.size() * 3 * 4;
		final int paramsOffset = uvsOffset + (uvs.size() * 4 * 2);

		final int vertexBuffer = gl.glGenBuffer();
		gl.glBindBuffer(GL30.GL_ARRAY_BUFFER, vertexBuffer);
		gl.glBufferData(GL30.GL_ARRAY_BUFFER, uvsOffset + (uvs.size() * 4 * 2) + (absoluteHeights.size() * 4), null,
				GL30.GL_STATIC_DRAW);
		gl.glBufferSubData(GL30.GL_ARRAY_BUFFER, 0, vertices.size() * 4 * 3, RenderMathUtils.wrap(vertices));
		gl.glBufferSubData(GL30.GL_ARRAY_BUFFER, uvsOffset, uvs.size() * 4 * 2, RenderMathUtils.wrap(uvs));
		gl.glBufferSubData(GL30.GL_ARRAY_BUFFER, paramsOffset, absoluteHeights.size() * 4,
				RenderMathUtils.wrap(absoluteHeights));

		final int faceBuffer = gl.glGenBuffer();
		gl.glBindBuffer(GL30.GL_ELEMENT_ARRAY_BUFFER, faceBuffer);
		gl.glBufferData(GL30.GL_ELEMENT_ARRAY_BUFFER, indices.size() * 6 * 2, RenderMathUtils.wrapFaces(indices),
				GL30.GL_STATIC_DRAW);

		this.batches.add(new Batch(uvsOffset, vertexBuffer, faceBuffer, indices.size() * 6, paramsOffset));
		for (final SplatMover mover : batchRenderUnits) {
			mover.vertexBuffer = vertexBuffer;
			mover.uvsOffset = uvsOffset;
			mover.faceBuffer = faceBuffer;
			mover.absHeightsOffset = paramsOffset;
		}
	}

	public void render(final GL30 gl, final ShaderProgram shader) {
		// Texture

		if (this.noDepthTest) {
			gl.glDisable(GL20.GL_DEPTH_TEST);
		}
		else {
			gl.glEnable(GL20.GL_DEPTH_TEST);
		}
		gl.glActiveTexture(GL30.GL_TEXTURE1);
		gl.glBindTexture(GL30.GL_TEXTURE_2D, this.texture.getGlHandle());
		shader.setUniformi("u_show_lighting", this.unshaded ? 0 : 1);
		shader.setUniform4fv("u_color", this.color, 0, 4);

		for (final Batch b : this.batches) {
			// Vertices
			gl.glBindBuffer(GL30.GL_ARRAY_BUFFER, b.vertexBuffer);
			shader.setVertexAttribute("a_position", 3, GL30.GL_FLOAT, false, 12, 0);
			shader.setVertexAttribute("a_uv", 2, GL30.GL_FLOAT, false, 8, b.uvsOffset);
			shader.setVertexAttribute("a_absoluteHeight", 1, GL30.GL_FLOAT, false, 4, b.paramsOffset);

			// Faces.
			gl.glBindBuffer(GL30.GL_ELEMENT_ARRAY_BUFFER, b.faceBuffer);

			// Draw
			gl.glDrawElements(GL30.GL_TRIANGLES, b.elements, GL30.GL_UNSIGNED_SHORT, 0);
		}

	}

	public boolean isNoDepthTest() {
		return this.noDepthTest;
	}

	public boolean isHighPriority() {
		return this.highPriority;
	}

	public SplatMover add(final float x, final float y, final float x2, final float y2, final float zDepthUpward,
			final float[] centerOffset) {
		this.locations.add(new float[] { x, y, x2, y2, zDepthUpward });
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
		private final int paramsOffset;

		public Batch(final int uvsOffset, final int vertexBuffer, final int faceBuffer, final int elements,
				final int paramsOffset) {
			this.uvsOffset = uvsOffset;
			this.vertexBuffer = vertexBuffer;
			this.faceBuffer = faceBuffer;
			this.elements = elements;
			this.paramsOffset = paramsOffset;
		}
	}

	public static final class SplatMover {
		public int absHeightsOffset;
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
		private final List<float[]> absoluteHeights = new ArrayList<>();
		private final List<int[]> indices = new ArrayList<>();
		private int indicesStartOffset;
		private int index;
		private final SplatModel splatModel;
		private boolean hidden = false;
		private boolean heightIsAbsolute = false;
		private float absoluteHeightValue = 0.0f;

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
			this.absoluteHeights.clear();
			this.indices.clear();
			return this;
		}

		public void move(final float deltaX, final float deltaY, final float[] centerOffset) {
			this.locs[0] += deltaX;
			this.locs[2] += deltaX;
			this.locs[1] += deltaY;
			this.locs[3] += deltaY;
			if (!this.hidden) {
				updateAfterMove(centerOffset);
			}
		}

		public void setLocation(final float x, final float y, final float[] centerOffset) {
			final float width = this.locs[2] - this.locs[0];
			final float height = this.locs[3] - this.locs[1];
			this.locs[0] = x - (width / 2);
			this.locs[2] = x + (width / 2);
			this.locs[1] = y - (height / 2);
			this.locs[3] = y + (height / 2);
			updateAfterMove(centerOffset);
		}

		private void updateAfterMove(final float[] centerOffset) {
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
			if (this.heightIsAbsolute) {
				updateAbsoluteHeightParams();
			}
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
			this.hidden = true;
		}

		public void show(final float[] centerOffset) {
			// It tries to only update if it is located at a new position... but here we are
			// forcing it visible again by putting the position outside the map
			this.ix0 = this.ix1 = this.iy0 = this.iy1 = Integer.MIN_VALUE;
			move(0, 0, centerOffset);
			this.hidden = false;
		}

		public void setHeightAbsolute(final boolean absolute, final float absoluteHeightValue) {
			this.absoluteHeightValue = absoluteHeightValue;
			if (absolute != this.heightIsAbsolute) {
				this.heightIsAbsolute = absolute;
				updateAbsoluteHeightParams();
			}
		}

		private void updateAbsoluteHeightParams() {
			final GL30 gl = Gdx.gl30;
			final float height = this.heightIsAbsolute ? this.absoluteHeightValue : NO_ABS_HEIGHT;
			for (final float[] absHeight : this.absoluteHeights) {
				absHeight[0] = height;
			}
			gl.glBindBuffer(GL30.GL_ARRAY_BUFFER, this.vertexBuffer);
			gl.glBufferSubData(GL30.GL_ARRAY_BUFFER, this.absHeightsOffset + (this.startOffset / 3),
					this.absoluteHeights.size() * 4, RenderMathUtils.wrap(this.absoluteHeights));
		}
	}

	@Override
	public int compareTo(final SplatModel other) {
		if (this.locations.isEmpty()) {
			if (other.locations.isEmpty()) {
				return 0;
			}
			else {
				return 1;
			}
		}
		else {
			if (other.locations.isEmpty()) {
				return -1;
			}
			else {
				return Float.compare(this.locations.get(0)[4], other.locations.get(0)[4]);
			}
		}
	}
}
