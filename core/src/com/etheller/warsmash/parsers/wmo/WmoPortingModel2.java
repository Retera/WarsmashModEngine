package com.etheller.warsmash.parsers.wmo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.IntIntMap;
import com.badlogic.gdx.utils.LongMap;
import com.etheller.warsmash.datasources.SourcedData;
import com.etheller.warsmash.util.FlagUtils;
import com.etheller.warsmash.viewer5.ModelInstance;
import com.etheller.warsmash.viewer5.ModelViewer;
import com.etheller.warsmash.viewer5.PathSolver;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxModel;
import com.hiveworkshop.rms.parsers.mdlx.AnimationMap;
import com.hiveworkshop.rms.parsers.mdlx.InterpolationType;
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
import com.hiveworkshop.rms.parsers.mdlx.timeline.MdlxUInt32Timeline;

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
			final GroupModelLoader groupModelLoader = portedModelsData[i];
			this.portedModels[i] = new GroupModel(mdxModel, groupModelLoader.extentCenter, groupModelLoader.flags,
					groupModelLoader.doodadReferences, groupModelLoader.animatedLiquid);
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
		// Liquid cells claimed across all groups of this WMO. Groups share one coordinate space and
		// their MLIQ grids overlap (the real client hides overlaps via portal/group visibility, which
		// we don't do); without dedup the same lava tile is drawn by 2+ group instances -> Z-fighting
		// and flickery patches. First group to claim a global cell wins.
		final Set<Long> claimedLiquidCells = new HashSet<>();
		// One shared liquid surface height for the whole WMO (median of the per-tile heights). Liquid
		// is level, so flattening all groups to this removes the step-seams between groups that store
		// slightly different base heights. See addLiquidGeosets.
		final float wmoLiquidSurfaceHeight = computeLiquidSurfaceHeight(groups);
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

				if (false) {
					if (!FlagUtils.hasFlag(group.getFlags(), WmoGroupInfo.Flags.IsExterior)
							|| FlagUtils.hasFlag(group.getFlags(), WmoGroupInfo.Flags.IsInterior)) {
						portedLayer.flags |= MdlxLayer.Flags.WARSMASH_ONLY_NOT_EXTERIOR_LIT;
					}
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

			int[] groupVertexIndices = group.getVertexIndices();
			final int[] stupidestFaces = new int[group.getPolygons().size() * 3];
			for (int i = 0, k = 0; i < group.getPolygons().size(); i++) {
				stupidestFaces[(i * 3) + 0] = k++;
				stupidestFaces[(i * 3) + 1] = k++;
				stupidestFaces[(i * 3) + 2] = k++;
			}
			groupVertexIndices = stupidestFaces;
			final int[] triangleStripIndices = group.getTriangleStripIndices();
			for (final GroupGxBatch gxBatch : group.getIntBatch()) {
				for (int groupBatchIndex = gxBatch.getBatchStart(); groupBatchIndex < (gxBatch.getBatchStart()
						+ gxBatch.getBatchCount()); groupBatchIndex++) {
					final GroupBatch groupBatch = group.getBatches().get(groupBatchIndex);

					final boolean triangleStrip = (triangleStripIndices != null) || (groupBatch.getFlags() != 0);
					if (triangleStrip) {
						continue;
					}
					final MdlxGeoset portedGeoset = new MdlxGeoset();
					portedGeoset.wmo = true;
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

					// Allocate only the vertices THIS batch actually references, not the whole
					// gxBatch window. The faces below are sequential (groupVertexIndices is the
					// identity list "stupidestFaces"), so this batch's triangles reference exactly
					// the contiguous vertex run [startIndex, startIndex+count). Using the wider
					// gxBatch.getVertStart()/getVertCount() made every batch in a window duplicate
					// that window's entire vertex range (e.g. 84 batches x 16383 verts), ballooning
					// a 19k-vertex group into ~1.4M verts (~63MB) -- enough that the largest WMOs
					// (e.g. Blackrock Mountain) silently failed to allocate GL buffers and vanished.
					final int minIndex = (int) groupBatch.getStartIndex();
					final int usedVertexCount = groupBatch.getCount();

					portedGeoset.vertices = new float[usedVertexCount * 3];
					System.arraycopy(group.getVertices(), minIndex * 3, portedGeoset.vertices, 0, usedVertexCount * 3);
					for (int i = 0; i < portedGeoset.vertices.length; i += 3) {
						portedGeoset.vertices[i + 0] -= extentCenter.x;
						portedGeoset.vertices[i + 1] -= extentCenter.y;
						portedGeoset.vertices[i + 2] -= extentCenter.z;
					}
					portedGeoset.vertexLightingColors = new float[usedVertexCount * 3];
					final int[] vertexColors = group.getVertexColors();
					for (int i = 0; i < usedVertexCount; i++) {
						final int vertexIndex = minIndex + i;
						final int colorInt = vertexColors[vertexIndex];
						portedGeoset.vertexLightingColors[(i * 3) + 0] = ((colorInt >> 16) & 0xFF) / 255f;
						portedGeoset.vertexLightingColors[(i * 3) + 1] = ((colorInt >> 8) & 0xFF) / 255f;
						portedGeoset.vertexLightingColors[(i * 3) + 2] = ((colorInt >> 0) & 0xFF) / 255f;
					}
					portedGeoset.normals = new float[usedVertexCount * 3];
					System.arraycopy(group.getNormals(), minIndex * 3, portedGeoset.normals, 0, usedVertexCount * 3);
					portedGeoset.faces = new int[groupBatch.getCount()];
					System.arraycopy(triangleStrip ? triangleStripIndices : groupVertexIndices,
							(int) groupBatch.getStartIndex(), portedGeoset.faces, 0, groupBatch.getCount());
					for (int i = 0; i < portedGeoset.faces.length; i++) {
						portedGeoset.faces[i] -= minIndex;
					}
					portedGeoset.faceGroups = new long[portedGeoset.faces.length / 3];
					portedGeoset.faceTypeGroups = new long[] {
							triangleStrip ? GL20.GL_TRIANGLE_STRIP : GL20.GL_TRIANGLES };
					portedGeoset.uvSets = new float[1][usedVertexCount * 2];
					System.arraycopy(group.getTextureVertices(), minIndex * 2, portedGeoset.uvSets[0], 0,
							usedVertexCount * 2);
					portedGeoset.vertexGroups = new short[usedVertexCount];
					portedGeoset.matrixGroups = new long[] { 1 };
					portedGeoset.matrixIndices = new long[] { 0 };
					if (groupBatch.getMaterialId() == -1) {
//						final int materialId = textureToMdlxMat.get(, -1);
//						if (materialId == -1) {
//							throw new IllegalStateException("bad mat id");
//						}
						portedGeoset.materialId = groupBatch.getTexture() & 0xFF;
					}
					else {
						portedGeoset.materialId = groupBatch.getMaterialId() & 0xFF;
					}
					portedModel.geosets.add(portedGeoset);
				}
			}
			for (final GroupGxBatch gxBatch : group.getExtBatch()) {
				for (int groupBatchIndex = gxBatch.getBatchStart(); groupBatchIndex < (gxBatch.getBatchStart()
						+ gxBatch.getBatchCount()); groupBatchIndex++) {
					final GroupBatch groupBatch = group.getBatches().get(groupBatchIndex);

					final boolean triangleStrip = (triangleStripIndices != null) || (groupBatch.getFlags() != 0);
					if (triangleStrip) {
						continue;
					}
					final MdlxGeoset portedGeoset = new MdlxGeoset();
					portedGeoset.wmo = true;
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

					// Allocate only the vertices THIS batch actually references, not the whole
					// gxBatch window. The faces below are sequential (groupVertexIndices is the
					// identity list "stupidestFaces"), so this batch's triangles reference exactly
					// the contiguous vertex run [startIndex, startIndex+count). Using the wider
					// gxBatch.getVertStart()/getVertCount() made every batch in a window duplicate
					// that window's entire vertex range (e.g. 84 batches x 16383 verts), ballooning
					// a 19k-vertex group into ~1.4M verts (~63MB) -- enough that the largest WMOs
					// (e.g. Blackrock Mountain) silently failed to allocate GL buffers and vanished.
					final int minIndex = (int) groupBatch.getStartIndex();
					final int usedVertexCount = groupBatch.getCount();

					portedGeoset.vertices = new float[usedVertexCount * 3];
					System.arraycopy(group.getVertices(), minIndex * 3, portedGeoset.vertices, 0, usedVertexCount * 3);
					for (int i = 0; i < portedGeoset.vertices.length; i += 3) {
						portedGeoset.vertices[i + 0] -= extentCenter.x;
						portedGeoset.vertices[i + 1] -= extentCenter.y;
						portedGeoset.vertices[i + 2] -= extentCenter.z;
					}
					portedGeoset.vertexLightingColors = new float[usedVertexCount * 3];
					final int[] vertexColors = group.getVertexColors();
					for (int i = 0; i < usedVertexCount; i++) {
						final int vertexIndex = minIndex + i;
						final int colorInt = vertexColors[vertexIndex];
						portedGeoset.vertexLightingColors[(i * 3) + 0] = ((colorInt >> 16) & 0xFF) / 255f;
						portedGeoset.vertexLightingColors[(i * 3) + 1] = ((colorInt >> 8) & 0xFF) / 255f;
						portedGeoset.vertexLightingColors[(i * 3) + 2] = ((colorInt >> 0) & 0xFF) / 255f;
					}
					portedGeoset.normals = new float[usedVertexCount * 3];
					System.arraycopy(group.getNormals(), minIndex * 3, portedGeoset.normals, 0, usedVertexCount * 3);
					portedGeoset.faces = new int[groupBatch.getCount()];
					System.arraycopy(triangleStrip ? triangleStripIndices : groupVertexIndices,
							(int) groupBatch.getStartIndex(), portedGeoset.faces, 0, groupBatch.getCount());
					for (int i = 0; i < portedGeoset.faces.length; i++) {
						portedGeoset.faces[i] -= minIndex;
					}
					portedGeoset.faceGroups = new long[portedGeoset.faces.length / 3];
					portedGeoset.faceTypeGroups = new long[] {
							triangleStrip ? GL20.GL_TRIANGLE_STRIP : GL20.GL_TRIANGLES };
					portedGeoset.uvSets = new float[1][usedVertexCount * 2];
					System.arraycopy(group.getTextureVertices(), minIndex * 2, portedGeoset.uvSets[0], 0,
							usedVertexCount * 2);
					portedGeoset.vertexGroups = new short[usedVertexCount];
					portedGeoset.matrixGroups = new long[] { 1 };
					portedGeoset.matrixIndices = new long[] { 0 };
					if (groupBatch.getMaterialId() == -1) {
//						final int materialId = textureToMdlxMat.get(, -1);
//						if (materialId == -1) {
//							throw new IllegalStateException("bad mat id");
//						}
						portedGeoset.materialId = groupBatch.getTexture() & 0xFF;
					}
					else {
						portedGeoset.materialId = groupBatch.getMaterialId() & 0xFF;
					}
					portedModel.geosets.add(portedGeoset);
				}
			}

			// Liquids (MLIQ): bake each liquid surface as an extra geoset in this group's
			// model so it shares the group's transform, culling, batching, and unload
			// lifecycle (no separate per-frame system to leak).
			final boolean animatedLiquid = addLiquidGeosets(portedModel, group, extentCenter, parser,
					claimedLiquidCells, wmoLiquidSurfaceHeight);

			// NOTE: for now, instead of building BSP we are being very dumb, creating
			// corresponding
			// "CollisionGeometry" in our engine (TODO should be BSP instead later)
			final int[] bspFaceIndices = group.getBspFaceIndices();
			int[] bspVertexIndices = new int[bspFaceIndices.length * 3];
			int maxEL = 0;
			int minErr = Integer.MAX_VALUE;
			int usedIdx = 0;
			for (int i = 0; i < bspFaceIndices.length; i++) {
				final int faceIndex = bspFaceIndices[i];
				final GroupPolygon groupPolygon = group.getPolygons().get(faceIndex);
				if (groupPolygon.isCollidable()) {
					for (int vertIdInFace = 0; vertIdInFace < 3; vertIdInFace++) {

						final int expectedLookup = (faceIndex * 3) + vertIdInFace;
						if (expectedLookup < groupVertexIndices.length) {
							bspVertexIndices[((usedIdx) * 3) + vertIdInFace] = groupVertexIndices[expectedLookup];
						}
						else {
							minErr = Math.min(minErr, expectedLookup);
						}
						maxEL = Math.max(expectedLookup, maxEL);
					}
					usedIdx++;
				}
			}
			if (false) {
				final int[] usedBspVertexIndices = new int[usedIdx * 3];
				System.arraycopy(bspVertexIndices, 0, usedBspVertexIndices, 0, usedIdx * 3);
				bspVertexIndices = usedBspVertexIndices;
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
					loadLight(parser, portedModel, extentCenter, lightReference);
				}
			}

			if (false) { // debug: dump each ported group as an .mdx under /tmp/dumbwmo
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

			portedModels[groupIndex] = new GroupModelLoader(portedModel, extentCenter, group.getFlags(),
					group.getDoodadReferences(), animatedLiquid);
		}

		return portedModels;
	}

	/** MLIQ liquid-grid tile spacing in WMO-local units (verified from corner alignment). */
	private static final float LIQUID_TILE_SIZE = 100.0f / 24.0f; // ~4.16667
	/**
	 * Lava flipbook frames. 0.5.3 ships XTextures\lava\lava.1.blp .. lava.30.blp (the
	 * BURNINGSTEPPSLAVA02 the Blackrock build's liquid material references is absent). We cycle these
	 * as an animated texture via a global sequence + a stepped KMTF (texture-id) timeline on the lava
	 * layer, so it flows like the terrain water without depending on the model playing a sequence.
	 */
	private static final int LAVA_FRAME_COUNT = 30;
	private static final int LAVA_FRAME_MS = 80; // ~12.5 fps
	/** Small upward nudge so the flat liquid surface doesn't Z-fight the basin floor at the shoreline. */
	private static final float LIQUID_SURFACE_LIFT = 0.5f;

	/**
	 * Bakes each MLIQ liquid surface of a WMO group into the group's model as a flat textured
	 * geoset. The liquid texture/shading is taken from the group's own liquid material (MLIQ
	 * materialId -> MOMT); a lava material (texture path contains "lava") becomes an unshaded,
	 * opaque, glowing surface using {@link #LAVA_TEXTURE}, anything else a translucent surface
	 * using the WMO's own texture. Geometry is built in the same centered local space as the
	 * group geometry (vertices minus the group's extentCenter), so the group instance transform
	 * places it correctly.
	 */
	private static boolean addLiquidGeosets(final MdlxModel portedModel, final ModelObjectGroup group,
			final Vector3 extentCenter, final WorldModelObject parser, final Set<Long> claimedLiquidCells,
			final float wmoSurfaceHeight) {
		final List<GroupLiquid> liquids = group.getLiquids();
		if (liquids.isEmpty()) {
			return false;
		}
		boolean animated = false;
		final List<WmoMaterial> mats = parser.getHeaders().getMaterials();
		for (final GroupLiquid liquid : liquids) {
			final int[] vc = liquid.getVertexCount(); // [rows(i=Y), cols(j=X)]
			final int[] tc = liquid.getTileCount();
			if ((vc[0] <= 0) || (vc[1] <= 0) || (tc[0] <= 0) || (tc[1] <= 0)) {
				continue;
			}
			final float[] corner = liquid.getCorner();

			// Liquid texture / shading from the WMO's own liquid material.
			String texturePath = null;
			final int matId = liquid.getMaterialId();
			if ((matId >= 0) && (matId < mats.size())) {
				final long dni = mats.get(matId).getDiffuseNameIndex();
				texturePath = parser.getHeaders().getTextureFileNamesOffsetLookup().get(dni);
			}
			final boolean lava = (texturePath != null) && texturePath.toLowerCase().contains("lava");
			if (!lava && (texturePath == null)) {
				continue; // nothing meaningful to draw
			}

			final int rows = vc[0]; // i -> Y
			final int cols = vc[1]; // j -> X

			// Faces over every tile in the grid. (We render all tiles: the per-tile MLIQ byte's low
			// bits are a liquid-type/flow field, NOT a reliable "no liquid" flag for these v14 WMOs --
			// skipping by it left missing strips, so we draw the whole grid and let the height-snap
			// flatten the un-set vertices.) All groups share the WMO coordinate space and corners are
			// TS-aligned, so each tile maps to a global cell (gx,gy); skip cells another group already
			// claimed to avoid overlapping (Z-fighting) surfaces.
			final int cgx = Math.round(corner[0] / LIQUID_TILE_SIZE);
			final int cgy = Math.round(corner[1] / LIQUID_TILE_SIZE);
			final int[] faceTmp = new int[tc[0] * tc[1] * 6];
			int fc = 0;
			for (int i = 0; i < tc[0]; i++) {
				for (int j = 0; j < tc[1]; j++) {
					final long cellKey = ((long) (cgx - j) << 32) ^ ((cgy + i) & 0xFFFFFFFFL);
					if (!claimedLiquidCells.add(cellKey)) {
						continue; // already drawn by another group -> avoid Z-fight/flicker
					}
					final int v00 = (i * cols) + j;
					final int v10 = ((i + 1) * cols) + j;
					final int v01 = (i * cols) + (j + 1);
					final int v11 = ((i + 1) * cols) + (j + 1);
					faceTmp[fc++] = v00;
					faceTmp[fc++] = v10;
					faceTmp[fc++] = v01;
					faceTmp[fc++] = v10;
					faceTmp[fc++] = v11;
					faceTmp[fc++] = v01;
				}
			}
			if (fc == 0) {
				continue; // fully hidden liquid
			}

			// Texture(s) + material/layer.
			final MdlxLayer layer = new MdlxLayer();
			layer.flags |= MdlxLayer.Flags.TWO_SIDED;
			if (lava) {
				// Add the 30 lava frames; the layer's texture id is animated through them by a
				// stepped KMTF timeline bound to a fresh global sequence, so it cycles continuously
				// (independent of model playback, paused only when the instance is off-screen).
				final int baseTextureId = portedModel.textures.size();
				for (int f = 1; f <= LAVA_FRAME_COUNT; f++) {
					final MdlxTexture frame = new MdlxTexture();
					frame.path = "XTextures\\lava\\lava." + f + ".blp";
					frame.replaceableId = 0;
					frame.wrapMode = WrapMode.WRAP_BOTH;
					portedModel.textures.add(frame);
				}
				layer.textureId = baseTextureId; // static fallback = frame 1
				layer.filterMode = FilterMode.NONE;
				layer.alpha = 1.0f;
				layer.flags |= MdlxLayer.Flags.UNSHADED; // glow, ignore scene lighting
				layer.flags |= MdlxLayer.Flags.UNFOGGED;

				final int globalSequenceId = portedModel.globalSequences.size();
				portedModel.globalSequences.add((long) (LAVA_FRAME_COUNT * LAVA_FRAME_MS));
				final MdlxUInt32Timeline kmtf = new MdlxUInt32Timeline();
				kmtf.name = AnimationMap.KMTF.getWar3id();
				kmtf.interpolationType = InterpolationType.DONT_INTERP; // stepped (no blend between frames)
				kmtf.globalSequenceId = globalSequenceId;
				kmtf.frames = new long[LAVA_FRAME_COUNT];
				kmtf.values = new long[LAVA_FRAME_COUNT][];
				for (int f = 0; f < LAVA_FRAME_COUNT; f++) {
					kmtf.frames[f] = (long) f * LAVA_FRAME_MS;
					kmtf.values[f] = new long[] { baseTextureId + f };
				}
				layer.timelines.add(kmtf);
				animated = true;
			}
			else {
				final MdlxTexture texture = new MdlxTexture();
				texture.path = texturePath;
				texture.replaceableId = 0;
				texture.wrapMode = WrapMode.WRAP_BOTH;
				layer.textureId = portedModel.textures.size();
				portedModel.textures.add(texture);
				layer.filterMode = FilterMode.BLEND;
				layer.alpha = 0.65f;
			}
			final MdlxMaterial material = new MdlxMaterial();
			material.layers.add(layer);
			final int materialId = portedModel.materials.size();
			portedModel.materials.add(material);

			// Geoset: indexed grid (vertex (i,j) at local (corner.x - j*TS, corner.y + i*TS, height)).
			final int vCount = rows * cols;
			final MdlxGeoset geoset = new MdlxGeoset();
			geoset.wmo = true;
			geoset.materialId = materialId;
			geoset.vertices = new float[vCount * 3];
			geoset.normals = new float[vCount * 3];
			geoset.uvSets = new float[1][vCount * 2];
			geoset.vertexLightingColors = new float[vCount * 3];
			geoset.vertexGroups = new short[vCount];
			geoset.matrixGroups = new long[] { 1 };
			geoset.matrixIndices = new long[] { 0 };
			// A liquid surface is physically level, so flatten the whole WMO's liquid to one shared
			// height (in centered local space). This removes the visible step-seams where groups with
			// different per-group base heights (corner.z) meet, removes the per-group precision
			// mismatch at boundaries, and makes the data's stray/unset vertex heights irrelevant.
			final float surfaceZ = (wmoSurfaceHeight + LIQUID_SURFACE_LIFT) - extentCenter.z;
			for (int i = 0; i < rows; i++) {
				for (int j = 0; j < cols; j++) {
					final int vi = (i * cols) + j;
					geoset.vertices[(vi * 3) + 0] = (corner[0] - (j * LIQUID_TILE_SIZE)) - extentCenter.x;
					geoset.vertices[(vi * 3) + 1] = (corner[1] + (i * LIQUID_TILE_SIZE)) - extentCenter.y;
					geoset.vertices[(vi * 3) + 2] = surfaceZ;
					geoset.normals[(vi * 3) + 2] = 1.0f; // flat, facing up
					// Global tile-grid UVs so the texture pattern flows continuously across groups.
					geoset.uvSets[0][(vi * 2) + 0] = (cgx - j) / 5.0f;
					geoset.uvSets[0][(vi * 2) + 1] = (cgy + i) / 5.0f;
					geoset.vertexLightingColors[(vi * 3) + 0] = 1.0f;
					geoset.vertexLightingColors[(vi * 3) + 1] = 1.0f;
					geoset.vertexLightingColors[(vi * 3) + 2] = 1.0f;
				}
			}
			geoset.faces = new int[fc];
			System.arraycopy(faceTmp, 0, geoset.faces, 0, fc);
			geoset.faceGroups = new long[fc / 3];
			geoset.faceTypeGroups = new long[] { GL20.GL_TRIANGLES };

			// Geoset extent from the referenced (rendered) vertices.
			final float[] gmin = { Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE };
			final float[] gmax = { -Float.MAX_VALUE, -Float.MAX_VALUE, -Float.MAX_VALUE };
			for (int k = 0; k < fc; k++) {
				final int vi = geoset.faces[k];
				for (int c = 0; c < 3; c++) {
					final float v = geoset.vertices[(vi * 3) + c];
					if (v < gmin[c]) {
						gmin[c] = v;
					}
					if (v > gmax[c]) {
						gmax[c] = v;
					}
				}
			}
			for (int c = 0; c < 3; c++) {
				geoset.extent.min[c] = gmin[c];
				geoset.extent.max[c] = gmax[c];
			}
			portedModel.geosets.add(geoset);
		}
		return animated;
	}

	/**
	 * One representative liquid surface height for the whole WMO: the median of all MLIQ per-tile
	 * vertex heights (ignoring unset 0 fillers). Liquid is level, so every group flattens to this,
	 * eliminating the step-seams between groups whose stored base heights differ slightly.
	 */
	private static float computeLiquidSurfaceHeight(final List<ModelObjectGroup> groups) {
		final List<Float> heights = new ArrayList<>();
		for (final ModelObjectGroup g : groups) {
			for (final GroupLiquid lq : g.getLiquids()) {
				for (final GroupLiquid.Vertex[] row : lq.getLiquidVertices()) {
					for (final GroupLiquid.Vertex v : row) {
						if (Math.abs(v.getHeight()) > 0.001f) { // skip unset (0) filler vertices
							heights.add(v.getHeight());
						}
					}
				}
			}
		}
		if (heights.isEmpty()) {
			for (final ModelObjectGroup g : groups) {
				for (final GroupLiquid lq : g.getLiquids()) {
					heights.add(lq.getCorner()[2]);
				}
			}
		}
		if (heights.isEmpty()) {
			return 0f;
		}
		Collections.sort(heights);
		return heights.get(heights.size() / 2);
	}

	private static void loadLight(final WorldModelObject parser, final MdlxModel portedModel,
			final Vector3 extentCenter, final int lightReference) {
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
			return;
		}
		final short[] bgraColor = wmoLight.getColor();

		light.color[0] = bgraColor[2] / 255f;
		light.color[1] = bgraColor[1] / 255f;
		light.color[2] = bgraColor[0] / 255f;
		light.intensity = wmoLight.getIntensity();
		light.attenuation[0] = wmoLight.getAttenStart();
		light.attenuation[1] = wmoLight.getAttenEnd();

		final float[] wmoLightPosition = wmoLight.getPosition();
		portedModel.pivotPoints.add(new float[] { wmoLightPosition[0] - extentCenter.x,
				wmoLightPosition[1] - extentCenter.y, wmoLightPosition[2] - extentCenter.z });
		portedModel.lights.add(light);
	}

	public static final class GroupModel {
		private final MdxModel model;
		private final Vector3 extentCenter;
		private final int flags;
		private final int[] doodadReferences;
		private final boolean animatedLiquid;

		private GroupModel(final MdxModel model, final Vector3 extentCenter, final int flags,
				final int[] doodadReferences, final boolean animatedLiquid) {
			this.model = model;
			this.extentCenter = extentCenter;
			this.flags = flags;
			this.doodadReferences = doodadReferences;
			this.animatedLiquid = animatedLiquid;
		}

		/** True if this group has an animated (flipbook) liquid surface that needs the instance to
		 * play a looping sequence so its global-sequence texture animation advances. */
		public boolean hasAnimatedLiquid() {
			return this.animatedLiquid;
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

		public int[] getDoodadReferences() {
			return this.doodadReferences;
		}
	}

	private static final class GroupModelLoader {
		private final MdlxModel model;
		private final Vector3 extentCenter;
		private final int flags;
		private final int[] doodadReferences;
		private final boolean animatedLiquid;

		public GroupModelLoader(final MdlxModel model, final Vector3 extentCenter, final int flags,
				final int[] doodadReferences, final boolean animatedLiquid) {
			this.model = model;
			this.extentCenter = extentCenter;
			this.flags = flags;
			this.doodadReferences = doodadReferences;
			this.animatedLiquid = animatedLiquid;
		}
	}

}
