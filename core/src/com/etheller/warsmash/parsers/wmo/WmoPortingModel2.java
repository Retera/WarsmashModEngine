package com.etheller.warsmash.parsers.wmo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.IntIntMap;
import com.badlogic.gdx.utils.IntSet;
import com.badlogic.gdx.utils.LongMap;
import com.etheller.warsmash.datasources.SourcedData;
import com.etheller.warsmash.util.FlagUtils;
import com.etheller.warsmash.viewer5.ModelInstance;
import com.etheller.warsmash.viewer5.ModelViewer;
import com.etheller.warsmash.viewer5.PathSolver;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxModel;
import com.hiveworkshop.rms.parsers.mdlx.MdlxBone;
import com.hiveworkshop.rms.parsers.mdlx.MdlxCollisionGeometry;
import com.hiveworkshop.rms.parsers.mdlx.MdlxExtent;
import com.hiveworkshop.rms.parsers.mdlx.MdlxGeoset;
import com.hiveworkshop.rms.parsers.mdlx.MdlxLayer;
import com.hiveworkshop.rms.parsers.mdlx.MdlxLayer.FilterMode;
import com.hiveworkshop.rms.parsers.mdlx.MdlxLight;
import com.hiveworkshop.rms.parsers.mdlx.MdlxLight.Type;
import com.hiveworkshop.rms.parsers.mdlx.MdlxMaterial;
import com.hiveworkshop.rms.parsers.mdlx.MdlxModel;
import com.hiveworkshop.rms.parsers.mdlx.MdlxSequence;
import com.hiveworkshop.rms.parsers.mdlx.MdlxTexture;
import com.hiveworkshop.rms.parsers.mdlx.MdlxTexture.WrapMode;

public class WmoPortingModel2 extends com.etheller.warsmash.viewer5.Model<WmoPortingHandler> {
	private GroupModel[] portedModels;
	private List<WmoDoodadDefinition> doodadDefinitions;
	private LongMap<String> doodadFileNamesOffsetLookup;
	private List<WmoDoodadSet> doodadSets;

	public WmoPortingModel2(final WmoPortingHandler handler, final ModelViewer viewer, final String extension,
			final PathSolver pathSolver, final String fetchUrl) {
		super(handler, viewer, extension, pathSolver, fetchUrl);
	}

	@Override
	protected ModelInstance createInstance(final int type) {
		return this.portedModels[type].model.addInstance();
	}

	@Override
	protected void lateLoad() {

	}

	@Override
	protected void error(final Exception e) {
		e.printStackTrace();
	}

	public GroupModel getGroup(final int index) {
		return this.portedModels[index];
	}

	public int getGroupCount() {
		return this.portedModels.length;
	}

	public List<WmoDoodadDefinition> getDoodadDefinitions() {
		return this.doodadDefinitions;
	}

	public List<WmoDoodadSet> getDoodadSets() {
		return this.doodadSets;
	}

	public LongMap<String> getDoodadFileNamesOffsetLookup() {
		return this.doodadFileNamesOffsetLookup;
	}

	@Override
	public void load(final SourcedData src, final Object options) {
		final WorldModelObject parser = new WorldModelObject(src.read());
		final GroupModelLoader[] portedModelsData = createPortedModels(this.fetchUrl, parser);

		this.doodadFileNamesOffsetLookup = parser.getHeaders().getDoodadFileNamesOffsetLookup();
		this.doodadDefinitions = parser.getHeaders().getDoodadDefinitions();
		this.doodadSets = parser.getHeaders().getDoodadSets();
		this.portedModels = new GroupModel[portedModelsData.length];
		for (int i = 0; i < portedModelsData.length; i++) {
			final MdxModel mdxModel = new MdxModel(this.handler.getMdxHandler(), this.viewer, "mdx", this.pathSolver,
					this.fetchUrl);
			this.portedModels[i] = new GroupModel(mdxModel, portedModelsData[i].extentCenter,
					portedModelsData[i].flags);
			try {
				mdxModel.load(portedModelsData[i].model);
				mdxModel.ok = true;
			}
			catch (final IOException e) {
				throw new RuntimeException(e);
			}
		}

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

	public static GroupModelLoader[] createPortedModels(final String fetchUrl, final WorldModelObject parser) {
		final GroupModelLoader[] portedModels = new GroupModelLoader[(int) parser.getHeaders().getnGroups()];
		final String namePrefix = fetchUrl.length() > 76 ? fetchUrl.substring(fetchUrl.length() - 76) : fetchUrl;

		final List<ModelObjectGroup> groups = parser.getGroups();
		final IntSet usedLightIndices = new IntSet();
		for (int groupIndex = 0; groupIndex < parser.getHeaders().getnGroups(); groupIndex++) {
			final MdlxModel portedModel = new MdlxModel();
			portedModel.name = namePrefix + Integer.toString(groupIndex);
			portedModel.blendTime = 0;

			final MdlxExtent extent = new MdlxExtent();
			final float[] min = extent.getMin();
			final float[] max = extent.getMax();
			for (int i = 0; i < 3; i++) {
				min[i] = Float.MAX_VALUE;
				max[i] = -Float.MAX_VALUE;
			}

			portedModel.extent = extent;
			final ModelObjectGroup group = groups.get(groupIndex);
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
			final Vector3 extentCenter = new Vector3((min[0] + max[0]) / 2, (min[1] + max[1]) / 2,
					(min[2] + max[2]) / 2);

			// Sequences
			final MdlxSequence stand = new MdlxSequence();
			stand.name = "Stand";
			stand.extent = extent;
			stand.interval[0] = 300;
			stand.interval[1] = 1300;
			stand.flags |= 0x1; // nonlooping or something
			portedModel.sequences.add(stand);
			extent.min[0] -= extentCenter.x;
			extent.min[1] -= extentCenter.y;
			extent.min[2] -= extentCenter.z;
			extent.max[0] -= extentCenter.x;
			extent.max[1] -= extentCenter.y;
			extent.max[2] -= extentCenter.z;

			// Texture animations
//			for (final MdlxTextureAnimation textureAnimation : parser.getTextureAnimations()) {
//				this.textureAnimations.add(new TextureAnimation(this, textureAnimation));
//			}
			// TODO only load needed textures
			for (final String name : parser.getHeaders().getTextureFileNames()) {
				final MdlxTexture texture = new MdlxTexture();
				texture.path = name;
				texture.replaceableId = 0;
				texture.wrapMode = WrapMode.WRAP_BOTH;
				portedModel.textures.add(texture);
			}

			// Materials
			final int layerId = 0;
			final IntIntMap textureToMdlxMat = new IntIntMap();
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

				if (!FlagUtils.hasFlag(group.getFlags(), WmoGroupInfo.Flags.IsExterior)
						|| FlagUtils.hasFlag(group.getFlags(), WmoGroupInfo.Flags.IsInterior)) {
					portedLayer.flags |= MdlxLayer.Flags.WARSMASH_ONLY_NOT_EXTERIOR_LIT;
				}
				portedLayer.textureId = textureId;

				// TODO "ExteriorLit" pretty important!

				final MdlxMaterial portedMaterial = new MdlxMaterial();
				portedMaterial.layers.add(portedLayer);
				textureToMdlxMat.put(textureId, portedModel.materials.size());
				portedModel.materials.add(portedMaterial);
			}
			if (false) {
				for (final GroupBatch groupBatch : group.getBatches()) {
					final byte materialId = groupBatch.getMaterialId();
					if (materialId == -1) {
						final int texture = groupBatch.getTexture() & 0xFF;

						final int mdlxMatIdx = textureToMdlxMat.get(texture, -1);
						if (mdlxMatIdx == -1) {
							final MdlxLayer portedLayer = new MdlxLayer();
							portedLayer.alpha = 1.0f;
							portedLayer.filterMode = FilterMode.TRANSPARENT;
							portedLayer.flags |= MdlxLayer.Flags.TWO_SIDED;
							portedLayer.textureId = texture;
							final MdlxMaterial portedMaterial = new MdlxMaterial();
							portedMaterial.layers.add(portedLayer);
							textureToMdlxMat.put(texture, portedModel.materials.size());
							portedModel.materials.add(portedMaterial);
						}
					}
				}
			}

			final WmoGroupInfo groupInfo = parser.getHeaders().getGroupInfos().get(groupIndex);

			final int[] groupVertexIndices = group.getVertexIndices();
			for (final GroupBatch groupBatch : group.getBatches()) {
				final MdlxGeoset portedGeoset = new MdlxGeoset();
				final short[][] boundingBox = groupBatch.getBoundingBox();
				for (int i = 0; i < 3; i++) {
					portedGeoset.extent.min[i] = boundingBox[0][i];
					portedGeoset.extent.max[i] = boundingBox[1][i];
				}
				portedGeoset.extent.min[0] -= extentCenter.x;
				portedGeoset.extent.min[1] -= extentCenter.y;
				portedGeoset.extent.min[2] -= extentCenter.z;
				portedGeoset.extent.max[0] -= extentCenter.x;
				portedGeoset.extent.max[1] -= extentCenter.y;
				portedGeoset.extent.max[2] -= extentCenter.z;

				final int usedVertexCount = ((groupBatch.getMaxIndex() - groupBatch.getMinIndex()) + 1);

				portedGeoset.vertices = new float[usedVertexCount * 3];
				System.arraycopy(group.getVertices(), groupBatch.getMinIndex() * 3, portedGeoset.vertices, 0,
						usedVertexCount * 3);
				for (int i = 0; i < portedGeoset.vertices.length; i += 3) {
					portedGeoset.vertices[i + 0] -= extentCenter.x;
					portedGeoset.vertices[i + 1] -= extentCenter.y;
					portedGeoset.vertices[i + 2] -= extentCenter.z;
				}
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
//					final int materialId = textureToMdlxMat.get(, -1);
//					if (materialId == -1) {
//						throw new IllegalStateException("bad mat id");
//					}
					portedGeoset.materialId = groupBatch.getTexture() & 0xFF;
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
				for (int i = 0; i < collisionGeometryVertices.length; i += 3) {
					collisionGeometryVertices[i + 0] -= extentCenter.x;
					collisionGeometryVertices[i + 1] -= extentCenter.y;
					collisionGeometryVertices[i + 2] -= extentCenter.z;
				}

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

			final MdlxBone bone = new MdlxBone();
			bone.name = "Root";
			bone.objectId = 0;
			bone.geosetId = -1;
			bone.geosetAnimationId = -1;

			portedModel.bones.add(bone);

			portedModel.pivotPoints
					.add(new float[] { (max[0] + min[0]) / 2, (max[1] + min[1]) / 2, (max[2] + min[2]) / 2, });

			if (group.getLightReferences() != null) {
				for (final int lightReference : group.getLightReferences()) {
					if (usedLightIndices.add(lightReference)) {
						// arbitrarily throw the light into one or the other group (we are being dumb),
						// but only load it once

						final WmoLight wmoLight = parser.getHeaders().getLights().get(lightReference);

						final MdlxLight light = new MdlxLight();
						switch (wmoLight.type) {
						case AMBIENT:
							light.type = Type.AMBIENT;
							break;
						case DIRECTIONAL:
							light.type = Type.DIRECTIONAL;
							break;
						case OMNIDIRECTIONAL:
							light.type = Type.OMNIDIRECTIONAL;
							break;
						default:
						case SPOT:
							System.err.println("Unsupported light: " + wmoLight.type);
							continue;
						}
						final short[] bgraColor = wmoLight.getColor();

						light.color[0] = bgraColor[3] / 255f;
						light.color[1] = bgraColor[2] / 255f;
						light.color[2] = bgraColor[1] / 255f;
						light.intensity = wmoLight.getIntensity();
						light.attenuation[0] = wmoLight.getAttenStart();
						light.attenuation[1] = wmoLight.getAttenEnd();

						final float[] wmoLightPosition = wmoLight.getPosition();
						portedModel.pivotPoints.add(new float[] { wmoLightPosition[0] - extentCenter.x,
								wmoLightPosition[1] - extentCenter.y, wmoLightPosition[2] - extentCenter.z });
						portedModel.lights.add(light);
					}
				}
			}

			if (true) {
				final File dst = new File("/tmp/dumbwmo/" + fetchUrl + groupIndex + ".mdx");
				dst.getParentFile().mkdirs();
				try (FileOutputStream fos = new FileOutputStream(dst)) {
					final ByteBuffer saveMdx = portedModel.saveMdx();
					fos.write(saveMdx.array());
				}
				catch (final Exception e) {
					e.printStackTrace();
				}
			}

			portedModels[groupIndex] = new GroupModelLoader(portedModel, extentCenter, group.getFlags());
		}

		return portedModels;
	}

	public static final class GroupModel {
		private final MdxModel model;
		private final Vector3 extentCenter;
		private final int flags;

		private GroupModel(final MdxModel model, final Vector3 extentCenter, final int flags) {
			this.model = model;
			this.extentCenter = extentCenter;
			this.flags = flags;
		}

		public MdxModel getModel() {
			return this.model;
		}

		public Vector3 getExtentCenter() {
			return this.extentCenter;
		}

		public int getFlags() {
			return this.flags;
		}
	}

	private static final class GroupModelLoader {
		private final MdlxModel model;
		private final Vector3 extentCenter;
		private final int flags;

		public GroupModelLoader(final MdlxModel model, final Vector3 extentCenter, final int flags) {
			this.model = model;
			this.extentCenter = extentCenter;
			this.flags = flags;
		}
	}

}
