package com.etheller.warsmash.parsers.wmo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

import com.badlogic.gdx.utils.IntIntMap;
import com.etheller.warsmash.datasources.SourcedData;
import com.etheller.warsmash.util.FlagUtils;
import com.etheller.warsmash.viewer5.ModelViewer;
import com.etheller.warsmash.viewer5.PathSolver;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxHandler;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxModel;
import com.hiveworkshop.rms.parsers.mdlx.MdlxBone;
import com.hiveworkshop.rms.parsers.mdlx.MdlxCollisionGeometry;
import com.hiveworkshop.rms.parsers.mdlx.MdlxExtent;
import com.hiveworkshop.rms.parsers.mdlx.MdlxGeoset;
import com.hiveworkshop.rms.parsers.mdlx.MdlxLayer;
import com.hiveworkshop.rms.parsers.mdlx.MdlxLayer.FilterMode;
import com.hiveworkshop.rms.parsers.mdlx.MdlxMaterial;
import com.hiveworkshop.rms.parsers.mdlx.MdlxModel;
import com.hiveworkshop.rms.parsers.mdlx.MdlxSequence;
import com.hiveworkshop.rms.parsers.mdlx.MdlxTexture;
import com.hiveworkshop.rms.parsers.mdlx.MdlxTexture.WrapMode;

public class WmoPortingModel extends MdxModel {

	public WmoPortingModel(final MdxHandler handler, final ModelViewer viewer, final String extension,
			final PathSolver pathSolver, final String fetchUrl) {
		super(handler, viewer, extension, pathSolver, fetchUrl);
	}

	@Override
	public void load(final Object bufferOrParser) throws IOException {
		final WorldModelObject parser = new WorldModelObject(((SourcedData) bufferOrParser).read());
		super.load(createPortedModel(this.fetchUrl, parser));
	}

	private static FilterMode convert(final com.etheller.warsmash.parsers.wmo.WmoMaterial.FilterMode filterMode) {
		switch (filterMode) {
		case ADD:
		case NO_ALPHA_ADD:
			return FilterMode.ADDITIVE;
		case ALPHA:
			return FilterMode.BLEND;
		case ALPHA_KEY:
			return FilterMode.TRANSPARENT;
		case BLEND_ADD:
		case INV_SRC_ALPHA_ADD:
			return FilterMode.ADDALPHA;
		case OPAQUE:
		case SRC_ALPHA_OPAQUE:
		case INV_SRC_ALPHA_OPAQUE:
		case SCREEN:
		case CONSTANT_ALPHA:
			return FilterMode.NONE;
		case MOD:
		case MOD_ADD:
			return FilterMode.MODULATE;
		case MOD2X:
			return FilterMode.MODULATE2X;
		}
		return null;
	}

	public static MdlxModel createPortedModel(final String fetchUrl, final WorldModelObject parser) {
		final MdlxModel portedModel = new MdlxModel();

		portedModel.name = fetchUrl;
		portedModel.blendTime = 0;

		final List<ModelObjectGroup> groups = parser.getGroups();
		final MdlxExtent extent = new MdlxExtent();
		final float[] min = extent.getMin();
		final float[] max = extent.getMax();
		for (int i = 0; i < 3; i++) {
			min[i] = Float.MAX_VALUE;
			max[i] = -Float.MAX_VALUE;
		}
		portedModel.extent = extent;
		for (final ModelObjectGroup group : groups) {
			final float[] vertices = group.getVertices();
			for (int i = 0; i < vertices.length; i += 3) {
				for (int j = 0; j < 3; j++) {
					final float value = vertices[i + j];
					if (value < min[j]) {
						min[j] = value;
					}
					if (value > max[j]) {
						max[j] = value;
					}
				}
			}
		}

		// Sequences
		final MdlxSequence stand = new MdlxSequence();
		stand.name = "Stand";
		stand.extent = extent;
		stand.interval[0] = 300;
		stand.interval[1] = 1300;
		stand.flags |= 0x1; // nonlooping or something
		portedModel.sequences.add(stand);

		// Texture animations
//		for (final MdlxTextureAnimation textureAnimation : parser.getTextureAnimations()) {
//			this.textureAnimations.add(new TextureAnimation(this, textureAnimation));
//		}
		for (final String name : parser.getHeaders().getTextureFileNames()) {
			final MdlxTexture texture = new MdlxTexture();
			texture.path = name;
			texture.replaceableId = 0;
			texture.wrapMode = WrapMode.WRAP_BOTH;
			portedModel.textures.add(texture);
		}

		// Materials
		final int layerId = 0;
		for (final WmoMaterial material : parser.getHeaders().getMaterials()) {
			final MdlxLayer portedLayer = new MdlxLayer();
			portedLayer.filterMode = convert(material.getFilterMode());
			portedLayer.alpha = 1.0f;

			final long diffuseNameIndex = material.getDiffuseNameIndex();
			final String textureName = parser.getHeaders().getTextureFileNamesOffsetLookup().get(diffuseNameIndex);
			final int textureId = parser.getHeaders().getTextureFileNames().indexOf(textureName);
			final MdlxTexture usedTexture = portedModel.textures.get(textureId);
			if (FlagUtils.hasFlag(material.getFlags(), WmoMaterial.Flags.ClampSAddress)) {
				if (FlagUtils.hasFlag(material.getFlags(), WmoMaterial.Flags.ClampTAddress)) {
					usedTexture.setWrapMode(WrapMode.WRAP_NONE);
				}
				else {
					usedTexture.setWrapMode(WrapMode.WRAP_HEIGHT);
				}
			}
			else if (FlagUtils.hasFlag(material.getFlags(), WmoMaterial.Flags.ClampTAddress)) {
				usedTexture.setWrapMode(WrapMode.WRAP_WIDTH);
			}

			if (FlagUtils.hasFlag(material.getFlags(), WmoMaterial.Flags.SelfIlluminatedDayNight)
					|| FlagUtils.hasFlag(material.getFlags(), WmoMaterial.Flags.Unlit)) {
				portedLayer.flags |= MdlxLayer.Flags.UNSHADED;
			}

			if (FlagUtils.hasFlag(material.getFlags(), WmoMaterial.Flags.Unfogged)) {
				portedLayer.flags |= MdlxLayer.Flags.UNFOGGED;
			}

			if (FlagUtils.hasFlag(material.getFlags(), WmoMaterial.Flags.Unlit)) {
				portedLayer.flags |= MdlxLayer.Flags.UNLIT;
			}

			if (FlagUtils.hasFlag(material.getFlags(), WmoMaterial.Flags.Unculled)) {
				portedLayer.flags |= MdlxLayer.Flags.TWO_SIDED;
			}
			portedLayer.textureId = textureId;

			// TODO "ExteriorLit" pretty important!

			final MdlxMaterial portedMaterial = new MdlxMaterial();
			portedMaterial.layers.add(portedLayer);
			portedModel.materials.add(portedMaterial);
		}

		final IntIntMap textureToMdlxMat = new IntIntMap();
		for (int groupIndex = 0; groupIndex < parser.getHeaders().getnGroups(); groupIndex++) {
			final ModelObjectGroup group = parser.getGroups().get(groupIndex);
			for (final GroupBatch groupBatch : group.getBatches()) {
				final byte materialId = groupBatch.getMaterialId();
				if (materialId == -1) {
					final byte texture = groupBatch.getTexture();

					final int mdlxMatIdx = textureToMdlxMat.get(texture, -1);
					if (mdlxMatIdx == -1) {
						final MdlxLayer portedLayer = new MdlxLayer();
						portedLayer.alpha = 1.0f;
						portedLayer.filterMode = FilterMode.TRANSPARENT;
						portedLayer.textureId = texture;
						final MdlxMaterial portedMaterial = new MdlxMaterial();
						portedMaterial.layers.add(portedLayer);
						textureToMdlxMat.put(texture, portedModel.materials.size());
						portedModel.materials.add(portedMaterial);
					}
				}
			}
		}

		for (int groupIndex = 0; groupIndex < parser.getHeaders().getnGroups(); groupIndex++) {
			final WmoGroupInfo groupInfo = parser.getHeaders().getGroupInfos().get(groupIndex);
			final ModelObjectGroup group = parser.getGroups().get(groupIndex);

			final int[] groupVertexIndices = group.getVertexIndices();
			for (final GroupBatch groupBatch : group.getBatches()) {
				final MdlxGeoset portedGeoset = new MdlxGeoset();
				final short[][] boundingBox = groupBatch.getBoundingBox();
				for (int i = 0; i < 3; i++) {
					portedGeoset.extent.min[i] = boundingBox[0][i];
					portedGeoset.extent.max[i] = boundingBox[1][i];
				}

				final int usedVertexCount = ((groupBatch.getMaxIndex() - groupBatch.getMinIndex()) + 1);

				portedGeoset.vertices = new float[usedVertexCount * 3];
				System.arraycopy(group.getVertices(), groupBatch.getMinIndex() * 3, portedGeoset.vertices, 0,
						usedVertexCount * 3);
				portedGeoset.normals = new float[usedVertexCount * 3];
				System.arraycopy(group.getNormals(), groupBatch.getMinIndex() * 3, portedGeoset.normals, 0,
						usedVertexCount * 3);
				portedGeoset.faces = new int[groupBatch.getCount()];
				System.arraycopy(groupVertexIndices, (int) groupBatch.getStartIndex(), portedGeoset.faces, 0,
						groupBatch.getCount());
				for (int i = 0; i < portedGeoset.faces.length; i += 3) {
					portedGeoset.faces[i] -= groupBatch.getMinIndex();
					portedGeoset.faces[i + 1] -= groupBatch.getMinIndex();
					portedGeoset.faces[i + 2] -= groupBatch.getMinIndex();
				}
				portedGeoset.faceGroups = new long[portedGeoset.faces.length / 3];
				portedGeoset.faceTypeGroups = new long[] { 4 };
				portedGeoset.uvSets = new float[1][usedVertexCount * 2];
				System.arraycopy(group.getTextureVertices(), groupBatch.getMinIndex() * 2, portedGeoset.uvSets[0], 0,
						usedVertexCount * 2);
				portedGeoset.vertexGroups = new short[usedVertexCount];
				portedGeoset.matrixGroups = new long[] { 1 };
				portedGeoset.matrixIndices = new long[] { 0 };
				if (groupBatch.getMaterialId() == -1) {
					final int materialId = textureToMdlxMat.get(groupBatch.getTexture(), -1);
					if (materialId == -1) {
						throw new IllegalStateException("bad mat id");
					}
					portedGeoset.materialId = materialId;
				}
				else {
					portedGeoset.materialId = groupBatch.getMaterialId() & 0xFF;
				}
				portedModel.geosets.add(portedGeoset);
			}

			// NOTE: for now, instead of building BSP we are being very dumb, creating
			// corresponding
			// "CollisionGeometry" in our engine (TODO should be BSP instead later)
			final int[] bspFaceIndices = group.getBspFaceIndices();
			final int[] bspVertexIndices = new int[bspFaceIndices.length * 3];
			int maxEL = 0;
			int minErr = Integer.MAX_VALUE;
			for (int i = 0; i < bspFaceIndices.length; i++) {
				for (int vertIdInFace = 0; vertIdInFace < 3; vertIdInFace++) {
					final int expectedLookup = (bspFaceIndices[i] * 3) + vertIdInFace;
					if (expectedLookup < groupVertexIndices.length) {
						bspVertexIndices[(i * 3) + vertIdInFace] = groupVertexIndices[expectedLookup];
					}
					else {
						minErr = Math.min(minErr, expectedLookup);
					}
					maxEL = Math.max(expectedLookup, maxEL);
				}
			}
			System.out.println(maxEL + ";" + minErr);
			for (final GroupBSPNode bspNode : group.getBspNodes()) {
				final long faceStart = bspNode.getFaceStart();
				final int numFaces = bspNode.getNumFaces();
				final int[] collisionGeometryVertexIndices = new int[numFaces * 3];
				int minVertexIndex = Integer.MAX_VALUE;
				int maxVertexIndex = 0;
				for (long triangleIndex = faceStart; triangleIndex < (faceStart + numFaces); triangleIndex++) {
					for (int vertIdInFace = 0; vertIdInFace < 3; vertIdInFace++) {
						final int bspVertexIndex = bspVertexIndices[(int) ((triangleIndex * 3) + vertIdInFace)];
						maxVertexIndex = Math.max(maxVertexIndex, bspVertexIndex);
						minVertexIndex = Math.min(minVertexIndex, bspVertexIndex);
						collisionGeometryVertexIndices[((int) (triangleIndex - faceStart) * 3)
								+ vertIdInFace] = bspVertexIndex;
					}
				}
				if (minVertexIndex == Integer.MAX_VALUE) {
					continue;
				}
				final float[] collisionGeometryVertices = new float[((maxVertexIndex - minVertexIndex) + 1) * 3];
				System.arraycopy(group.getVertices(), minVertexIndex * 3, collisionGeometryVertices, 0,
						((maxVertexIndex - minVertexIndex) + 1) * 3);

				final float[] collisionGeometryNormals = new float[((maxVertexIndex - minVertexIndex) + 1) * 3];
				System.arraycopy(group.getNormals(), minVertexIndex * 3, collisionGeometryNormals, 0,
						((maxVertexIndex - minVertexIndex) + 1) * 3);

				for (int vertexIndexId = 0; vertexIndexId < collisionGeometryVertexIndices.length; vertexIndexId++) {
					collisionGeometryVertexIndices[vertexIndexId] -= minVertexIndex;
				}

				final MdlxCollisionGeometry mdlxCollisionGeometry = new MdlxCollisionGeometry();
				mdlxCollisionGeometry.vertices = collisionGeometryVertices;
				mdlxCollisionGeometry.normals = collisionGeometryNormals;
				mdlxCollisionGeometry.faces = collisionGeometryVertexIndices;
				portedModel.collisionGeometries.add(mdlxCollisionGeometry);
			}
		}

		final MdlxBone bone = new MdlxBone();
		bone.name = "Root";
		bone.objectId = 0;
		bone.geosetId = -1;
		bone.geosetAnimationId = -1;

		portedModel.bones.add(bone);

		portedModel.pivotPoints.add(new float[3]);

		final File dst = new File("/tmp/dumbwmo/" + fetchUrl + ".mdx");
		dst.getParentFile().mkdirs();
		try (FileOutputStream fos = new FileOutputStream(dst)) {
			final ByteBuffer saveMdx = portedModel.saveMdx();
			fos.write(saveMdx.array());
		}
		catch (final Exception e) {
			e.printStackTrace();
		}

		return portedModel;
	}
}
