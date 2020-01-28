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

	public SplatModel(final GL30 gl, final Texture texture, final List<float[]> locations, final float[] centerOffset,
			final List<Consumer<SplatMover>> unitMapping) {
		this.texture = texture;
		this.batches = new ArrayList<>();
		this.color = new float[] { 1, 1, 1, 1 };

		final List<float[]> vertices = new ArrayList<>();
		final List<float[]> uvs = new ArrayList<>();
		final List<int[]> indices = new ArrayList<>();
		final List<SplatMover> batchRenderUnits = new ArrayList<>();
		final int instances = locations.size();
		for (int idx = 0; idx < instances; ++idx) {
			final Consumer<SplatMover> unit = ((unitMapping != null) && (idx < unitMapping.size()))
					? unitMapping.get(idx)
					: null;
			final float[] locs = locations.get(idx);
			final float x0 = locs[0];
			final float y0 = locs[1];
			final float x1 = locs[2];
			final float y1 = locs[3];
			final float zoffs = locs[4];

			final int ix0 = (int) Math.floor((x0 - centerOffset[0]) / 128.0);
			final int ix1 = (int) Math.ceil((x1 - centerOffset[0]) / 128.0);
			final int iy0 = (int) Math.floor((y0 - centerOffset[1]) / 128.0);
			final int iy1 = (int) Math.ceil((y1 - centerOffset[1]) / 128.0);

			final float newVerts = ((iy1 - iy0) + 1) * ((ix1 - ix0) + 1);
			if (newVerts > MAX_VERTICES) {
				continue;
			}

			int start = vertices.size();
			final SplatMover splatMover = unit == null ? null : new SplatMover(start * 3 * 4);
			final int step = (ix1 - ix0) + 1;
			if ((start + newVerts) > MAX_VERTICES) {
				this.addBatch(gl, vertices, uvs, indices, batchRenderUnits);
				vertices.clear();
				uvs.clear();
				indices.clear();
				batchRenderUnits.clear();
				start = 0;
			}

			for (int iy = iy0; iy <= iy1; ++iy) {
				final float y = (iy * 128.0f) + centerOffset[1];
				for (int ix = ix0; ix <= ix1; ++ix) {
					final float x = (ix * 128.0f) + centerOffset[0];
					final float[] vertex = new float[] { x, y, zoffs };
					vertices.add(vertex);
					uvs.add(new float[] { (x - x0) / (x1 - x0), 1.0f - ((y - y0) / (y1 - y0)) });
					if (splatMover != null) {
						splatMover.vertices.add(vertex);
					}
				}
			}
			for (int i = 0; i < (iy1 - iy0); ++i) {
				for (int j = 0; j < (ix1 - ix0); ++j) {
					final int i0 = start + (i * step) + j;
					indices.add(new int[] { i0, i0 + 1, i0 + step, i0 + 1, i0 + step + 1, i0 + step });
				}
			}
			if (unit != null) {
				unit.accept(splatMover);
				batchRenderUnits.add(splatMover);
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
		}
	}

	public void render(final GL30 gl, final ShaderProgram shader) {
		// Texture

		gl.glActiveTexture(GL30.GL_TEXTURE1);
		gl.glBindTexture(GL30.GL_TEXTURE_2D, this.texture.getGlHandle());
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
		private int vertexBuffer;
		private final int startOffset;
		private final List<float[]> vertices = new ArrayList<>();

		private SplatMover(final int i) {
			this.startOffset = i;
		}

		public void move(final float deltaX, final float deltaY) {
			for (final float[] vertex : this.vertices) {
				vertex[0] += deltaX;
				vertex[1] += deltaY;
			}

			final GL30 gl = Gdx.gl30;
			gl.glBindBuffer(GL30.GL_ARRAY_BUFFER, this.vertexBuffer);
			gl.glBufferSubData(GL30.GL_ARRAY_BUFFER, this.startOffset, 4 * 3 * this.vertices.size(),
					RenderMathUtils.wrap(this.vertices));
		}
	}
}
