package com.etheller.warsmash.viewer5.handlers.mdx;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.badlogic.gdx.graphics.GL20;

public class SetupGeosets {
	private static final int NORMAL_BATCH = 0;
	private static final int EXTENDED_BATCH = 1;
	private static final int REFORGED_BATCH = 2;

	public static void setupGeosets(final MdxModel model,
			final List<com.etheller.warsmash.parsers.mdlx.Geoset> geosets) {
		if (geosets.size() > 0) {
			final GL20 gl = model.viewer.gl;
			int positionBytes = 0;
			int normalBytes = 0;
			int uvBytes = 0;
			int skinBytes = 0;
			int faceBytes = 0;
			final int[] batchTypes = new int[geosets.size()];

			for (int i = 0, l = geosets.size(); i < l; i++) {
				final com.etheller.warsmash.parsers.mdlx.Geoset geoset = geosets.get(i);

				if (true /* geoset.getLod() == 0 */) {
					final int vertices = geoset.getVertices().length / 3;

					positionBytes += vertices * 12;
					normalBytes += vertices * 12;
					uvBytes += geoset.getUvSets().length * vertices * 8;

					if (false /* geoset.skin.length */) {
						skinBytes += vertices * 8;

						batchTypes[i] = REFORGED_BATCH;
					}
					else {
						long biggestGroup = 0;

						for (final long group : geoset.getMatrixGroups()) {
							if (group > biggestGroup) {
								biggestGroup = group;
							}
						}

						if (biggestGroup > 4) {
							skinBytes += vertices * 9;

							batchTypes[i] = EXTENDED_BATCH;
						}
						else {
							batchTypes[i] = NORMAL_BATCH;
						}
					}

					faceBytes += geoset.getFaces().length * 4;
				}
			}

			int positionOffset = 0;
			int normalOffset = positionOffset + positionBytes;
			int uvOffset = normalOffset + normalBytes;
			int skinOffset = uvOffset + uvBytes;
			int faceOffset = 0;

			model.arrayBuffer = gl.glGenBuffer();
			gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, model.arrayBuffer);
			gl.glBufferData(GL20.GL_ARRAY_BUFFER, skinOffset + skinBytes, null, GL20.GL_STATIC_DRAW);

			model.elementBuffer = gl.glGenBuffer();
			gl.glBindBuffer(GL20.GL_ELEMENT_ARRAY_BUFFER, model.elementBuffer);
			gl.glBufferData(GL20.GL_ELEMENT_ARRAY_BUFFER, faceBytes, null, GL20.GL_STATIC_DRAW);

			for (int i = 0, l = geosets.size(); i < l; i++) {
				final com.etheller.warsmash.parsers.mdlx.Geoset geoset = geosets.get(i);

				if (true /* geoset.lod == 0 */) {
					final float[] positions = geoset.getVertices();
					final float[] normals = geoset.getNormals();
					final float[][] uvSets = geoset.getUvSets();
					final int[] faces = geoset.getFaces();
					byte[] skin = null;
					final int vertices = geoset.getVertices().length / 3;
					final int batchType = batchTypes[i];

					if (batchType == REFORGED_BATCH) {
						// skin = geoset.skin;
					}
					else {
						final long[] matrixIndices = geoset.getMatrixIndices();
						final short[] vertexGroups = geoset.getVertexGroups();
						final List<long[]> matrixGroups = new ArrayList<>();
						int offset = 0;
						// Normally the shader supports up to 4 bones per vertex.
						// This is enough for almost every existing Warcraft 3 model.
						// That being said, there are a few models with geosets that need more, for
						// example the Water Elemental.
						// These geosets use a different shader, which support up to 8 bones per vertex.
						int maxBones = 4;
						if (batchType == EXTENDED_BATCH) {
							maxBones = 8;
						}

						skin = new byte[vertices * (maxBones + 1)];

						// Slice the matrix groups
						for (final long size : geoset.getMatrixGroups()) {
							matrixGroups.add(Arrays.copyOfRange(matrixIndices, offset, (int) (offset + size)));
							offset += size;
						}

						// Parse the skinning.
						for (int si = 0; si < vertices; si++) {
							final short vertexGroup = vertexGroups[si];
							final long[] matrixGroup = (vertexGroup >= matrixGroups.size()) ? null
									: matrixGroups.get(vertexGroup);

							offset = si * (maxBones + 1);

							// Somehow in some bad models a vertex group index refers to an invalid matrix
							// group.
							// Such models are still loaded by the game.
							if (matrixGroup != null) {
								final int bones = Math.min(matrixGroup.length, maxBones);

								for (int j = 0; j < bones; j++) {
									skin[offset + j] = (byte) (matrixGroup[j] + 1); // 1 is added to diffrentiate
																					// between matrix 0, and no matrix.
								}

								skin[offset + maxBones] = (byte) bones;
							}
						}
					}

					final Geoset vGeoset = new Geoset(model, model.getGeosets().size(), positionOffset, normalOffset,
							uvOffset, skinOffset, faceOffset, vertices, faces.length);

					model.getGeosets().add(vGeoset);

					if (batchType == REFORGED_BATCH) {
						throw new UnsupportedOperationException("NYI");
//						model.batches.add(new Reforged)
					}
					else {
						final boolean isExtended = batchType == EXTENDED_BATCH;

						for (final Layer layer : model.getMaterials().get((int) geoset.getMaterialId()).layers) {
							model.batches.add(new Batch(model.batches.size(), vGeoset, layer, isExtended));
						}
					}

					// Positions.
					gl.glBufferSubData(GL20.GL_ARRAY_BUFFER, positionOffset, positions.length, wrap(positions));
					positionOffset += positions.length * 4;

					// Normals.
					gl.glBufferSubData(GL20.GL_ARRAY_BUFFER, normalOffset, normals.length, wrap(normals));
					normalOffset += normals.length * 4;

					// Texture coordinates.
					for (final float[] uvSet : uvSets) {
						gl.glBufferSubData(GL20.GL_ARRAY_BUFFER, uvOffset, uvSet.length, wrap(uvSet));
						uvOffset += uvSet.length * 4;
					}

					// Skin.
					gl.glBufferSubData(GL20.GL_ARRAY_BUFFER, skinOffset, skin.length, wrap(skin));
					skinOffset += skin.length * 1;

					// Faces.
					gl.glBufferSubData(GL20.GL_ELEMENT_ARRAY_BUFFER, faceOffset, faces.length, wrapFaces(faces));
					faceOffset += faces.length * 4;
				}

			}
		}
	}

	private static ShortBuffer wrapFaces(final int[] faces) {
		final ShortBuffer wrapper = ByteBuffer.allocateDirect(faces.length * 2).asShortBuffer();
		for (final int face : faces) {
			wrapper.put((short) face);
		}
		wrapper.clear();
		return wrapper;
	}

	private static ByteBuffer wrap(final byte[] skin) {
		final ByteBuffer wrapper = ByteBuffer.allocateDirect(skin.length);
		wrapper.put(skin);
		wrapper.clear();
		return wrapper;
	}

	private static FloatBuffer wrap(final float[] positions) {
		final FloatBuffer wrapper = ByteBuffer.allocateDirect(positions.length * 4).asFloatBuffer();
		wrapper.put(positions);
		wrapper.clear();
		return wrapper;
	}
}
