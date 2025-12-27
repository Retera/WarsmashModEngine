package com.etheller.warsmash.viewer5.handlers.mdx;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.badlogic.gdx.graphics.GL20;
import com.etheller.warsmash.util.RenderMathUtils;
import com.hiveworkshop.rms.parsers.mdlx.MdlxGeoset;

public class SetupGeosets {

	public static void setupGeosets(final MdxModel model, final List<MdlxGeoset> geosets, final boolean bigNodeSpace) {
		if (geosets.size() > 0) {
			final GL20 gl = model.viewer.gl;
			int positionBytes = 0;
			int normalBytes = 0;
			int uvBytes = 0;
			int tangentBytes = 0;
			int vertexLightingColorBytes = 0;
			int skinBytes = 0;
			int faceBytes = 0;
			final SkinningType[] batchTypes = new SkinningType[geosets.size()];

			final int extendedBatchStride = bigNodeSpace ? 36 : 9;
			final int normalBatchStride = bigNodeSpace ? 20 : 5;
			final int openGLSkinType = bigNodeSpace ? GL20.GL_UNSIGNED_INT : GL20.GL_UNSIGNED_BYTE;
			final int normalBatchBoneCountOffsetBytes = bigNodeSpace ? 16 : 4;
			final int extendedBatchBoneCountOffsetBytes = bigNodeSpace ? 32 : 8;

			for (int i = 0, l = geosets.size(); i < l; i++) {
				final MdlxGeoset geoset = geosets.get(i);

				if ((geoset.lod == 0) || (geoset.lod == -1)) {
					final int vertices = geoset.getVertices().length / 3;

					positionBytes += vertices * 12;
					normalBytes += vertices * 12;
					uvBytes += geoset.getUvSets().length * vertices * 8;

					if (geoset.tangents != null) {
						tangentBytes += vertices * 16;
					}

					if ((geoset.skin != null) && model.reforged) {
						skinBytes += vertices * 8;

						batchTypes[i] = SkinningType.Skin;
					}
					else {
						long biggestGroup = 0;

						for (final long group : geoset.getMatrixGroups()) {
							if (group > biggestGroup) {
								biggestGroup = group;
							}
						}

						if (biggestGroup > 4) {
							skinBytes += vertices * extendedBatchStride;

							batchTypes[i] = SkinningType.ExtendedVertexGroups;
						}
						else {
							skinBytes += vertices * normalBatchStride;

							batchTypes[i] = SkinningType.VertexGroups;
						}
					}
					if (geoset.isWmo()) {
						vertexLightingColorBytes += vertices * 12;
						batchTypes[i] = SkinningType.Wmo;
					}

					faceBytes += geoset.getFaces().length * 2;
				}
			}

			int positionOffset = 0;
			int normalOffset = positionOffset + positionBytes;
			int uvOffset = normalOffset + normalBytes;
			int tangentOffset = uvOffset + uvBytes;
			int skinOffset = tangentOffset + tangentBytes;
			int vertexLightingColorsOffset = skinOffset + skinBytes;
			int faceOffset = 0;

			model.arrayBuffer = gl.glGenBuffer();
			gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, model.arrayBuffer);
			gl.glBufferData(GL20.GL_ARRAY_BUFFER, vertexLightingColorsOffset + vertexLightingColorBytes, null,
					GL20.GL_STATIC_DRAW);

			model.elementBuffer = gl.glGenBuffer();
			gl.glBindBuffer(GL20.GL_ELEMENT_ARRAY_BUFFER, model.elementBuffer);
			gl.glBufferData(GL20.GL_ELEMENT_ARRAY_BUFFER, faceBytes, null, GL20.GL_STATIC_DRAW);

			for (int i = 0, l = geosets.size(); i < l; i++) {
				final MdlxGeoset geoset = geosets.get(i);

				final SkinningType batchType = batchTypes[i];
				if ((geoset.lod == 0) || (geoset.lod == -1)) {
					final float[] positions = geoset.getVertices();
					final float[] normals = geoset.getNormals();
					final float[][] uvSets = geoset.getUvSets();
					final float[] tangents = geoset.getTangents();
					final float[] vertexLightingColors = geoset.getVertexLightingColors();
					final int[] faces = geoset.getFaces();
					int[] skin = null;
					final int vertices = geoset.getVertices().length / 3;

					int maxBones;
					int skinStride;
					int boneCountOffsetBytes;
					if (batchType == SkinningType.ExtendedVertexGroups) {
						maxBones = 8;
						skinStride = extendedBatchStride;
						boneCountOffsetBytes = extendedBatchBoneCountOffsetBytes;
					}
					else {
						maxBones = 4;
						skinStride = normalBatchStride;
						boneCountOffsetBytes = normalBatchBoneCountOffsetBytes;
					}

					if (batchType == SkinningType.Skin) {
						skin = new int[geoset.skin.length];
						for (int j = 0; j < geoset.skin.length; j++) {
							skin[j] = geoset.skin[j];
						}
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

						skin = new int[vertices * (maxBones + 1)];

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
									skin[offset + j] = (int) (matrixGroup[j] + 1); // 1 is added to diffrentiate
																					// between matrix 0, and no matrix.
								}

								skin[offset + maxBones] = bones;
							}
						}
					}

					final boolean unselectable = geoset.getSelectionFlags() == 4;
					final Geoset vGeoset = new Geoset(model, model.getGeosets().size(), positionOffset, normalOffset,
							uvOffset, tangentOffset, skinOffset, vertexLightingColorsOffset, faceOffset, vertices,
							faces.length, openGLSkinType, skinStride, boneCountOffsetBytes, unselectable, geoset,
							(int) geoset.getFaceTypeGroups()[0]);

					model.getGeosets().add(vGeoset);

					final Material material = model.materials.get((int) geoset.getMaterialId());
					final boolean isHd = "Shader_HD_DefaultUnit".equals(material.shader);

					if (isHd) {
						model.batches.add(new Batch(model.batches.size(), vGeoset, material, batchType));
					}
					else {
						for (final Layer layer : model.getMaterials().get((int) geoset.getMaterialId()).layers) {
							model.batches.add(new Batch(model.batches.size(), vGeoset, layer, batchType));
						}
					}

					// Positions.
					gl.glBufferSubData(GL20.GL_ARRAY_BUFFER, positionOffset, positions.length,
							RenderMathUtils.wrap(positions));
					positionOffset += positions.length * 4;

					// Normals.
					gl.glBufferSubData(GL20.GL_ARRAY_BUFFER, normalOffset, normals.length,
							RenderMathUtils.wrap(normals));
					normalOffset += normals.length * 4;

					// Texture coordinates.
					for (final float[] uvSet : uvSets) {
						gl.glBufferSubData(GL20.GL_ARRAY_BUFFER, uvOffset, uvSet.length, RenderMathUtils.wrap(uvSet));
						uvOffset += uvSet.length * 4;
					}

					if (tangents != null) {
						gl.glBufferSubData(GL20.GL_ARRAY_BUFFER, tangentOffset, tangents.length,
								RenderMathUtils.wrap(tangents));
						tangentOffset += tangents.length * 4;
					}

					if (geoset.isWmo()) {
						gl.glBufferSubData(GL20.GL_ARRAY_BUFFER, vertexLightingColorsOffset,
								vertexLightingColors.length, RenderMathUtils.wrap(vertexLightingColors));
						vertexLightingColorsOffset += vertexLightingColors.length * 4;
					}
					// Skin.
					gl.glBufferSubData(GL20.GL_ARRAY_BUFFER, skinOffset, skin.length,
							bigNodeSpace ? RenderMathUtils.wrap(skin) : RenderMathUtils.wrapAsBytes(skin));
					skinOffset += skin.length * (bigNodeSpace ? 4 : 1);

					// Faces.
					gl.glBufferSubData(GL20.GL_ELEMENT_ARRAY_BUFFER, faceOffset, faces.length,
							RenderMathUtils.wrapFaces(faces));
					faceOffset += faces.length * 2;
				}

			}
		}
	}

}
