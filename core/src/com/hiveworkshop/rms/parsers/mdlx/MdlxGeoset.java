package com.hiveworkshop.rms.parsers.mdlx;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.etheller.warsmash.util.War3ID;
import com.hiveworkshop.rms.parsers.mdlx.mdl.MdlTokenInputStream;
import com.hiveworkshop.rms.parsers.mdlx.mdl.MdlTokenOutputStream;
import com.hiveworkshop.rms.parsers.mdlx.mdl.MdlUtils;
import com.hiveworkshop.rms.util.BinaryReader;
import com.hiveworkshop.rms.util.BinaryWriter;

public class MdlxGeoset implements MdlxBlock, MdlxChunk {
	private static final War3ID VRTX = War3ID.fromString("VRTX");
	private static final War3ID NRMS = War3ID.fromString("NRMS");
	private static final War3ID PTYP = War3ID.fromString("PTYP");
	private static final War3ID PCNT = War3ID.fromString("PCNT");
	private static final War3ID PVTX = War3ID.fromString("PVTX");
	private static final War3ID GNDX = War3ID.fromString("GNDX");
	private static final War3ID MTGC = War3ID.fromString("MTGC");
	private static final War3ID MATS = War3ID.fromString("MATS");
	private static final War3ID TANG = War3ID.fromString("TANG");
	private static final War3ID SKIN = War3ID.fromString("SKIN");
	private static final War3ID UVAS = War3ID.fromString("UVAS");
	private static final War3ID UVBS = War3ID.fromString("UVBS");

	public float[] vertices;
	public float[] normals;
	public long[] faceTypeGroups; // unsigned int[]
	public long[] faceGroups; // unsigned int[]
	public int[] faces; // unsigned short[]
	public short[] vertexGroups; // unsigned byte[]
	public long[] matrixGroups; // unsigned int[]
	public long[] matrixIndices; // unsigned int[]
	public long materialId = 0;
	public long selectionGroup = 0;
	public long selectionFlags = 0;
	/**
	 * @since 900
	 */
	public int lod = 0;
	/**
	 * @since 900
	 */
	public String lodName = "";
	public MdlxExtent extent = new MdlxExtent();
	public List<MdlxExtent> sequenceExtents = new ArrayList<>();
	/**
	 * @since 900
	 */
	public float[] tangents;
	/**
	 * @since 900
	 */
	public short[] skin;
	public float[][] uvSets;

	@Override
	public void readMdx(final BinaryReader reader, final int version) {
		final long size = reader.readUInt32();

		reader.readInt32(); // skip VRTX
		this.vertices = reader.readFloat32Array(reader.readInt32() * 3);
		reader.readInt32(); // skip NRMS
		this.normals = reader.readFloat32Array(reader.readInt32() * 3);
		reader.readInt32(); // skip PTYP
		this.faceTypeGroups = reader.readUInt32Array(reader.readInt32());
		reader.readInt32(); // skip PCNT
		this.faceGroups = reader.readUInt32Array(reader.readInt32());
		reader.readInt32(); // skip PVTX
		this.faces = reader.readUInt16Array(reader.readInt32());
		reader.readInt32(); // skip GNDX
		this.vertexGroups = reader.readUInt8Array(reader.readInt32());
		reader.readInt32(); // skip MTGC
		this.matrixGroups = reader.readUInt32Array(reader.readInt32());
		reader.readInt32(); // skip MATS
		this.matrixIndices = reader.readUInt32Array(reader.readInt32());
		this.materialId = reader.readUInt32();
		this.selectionGroup = reader.readUInt32();
		this.selectionFlags = reader.readUInt32();

		if (version > 800) {
			this.lod = reader.readInt32();
			this.lodName = reader.read(80);
		}

		this.extent.readMdx(reader);

		final long numExtents = reader.readUInt32();

		for (int i = 0; i < numExtents; i++) {
			final MdlxExtent extent = new MdlxExtent();
			extent.readMdx(reader);
			this.sequenceExtents.add(extent);
		}

		int id = reader.readTag(); // TANG or SKIN or UVAS

		if ((version > 800) && (id != UVAS.getValue())) {
			if (id == TANG.getValue()) {
				this.tangents = reader.readFloat32Array(reader.readInt32() * 4);

				id = reader.readTag(); // SKIN or UVAS
			}

			if (id == SKIN.getValue()) {
				this.skin = reader.readUInt8Array(reader.readInt32());

				id = reader.readInt32(); // UVAS
			}
		}

		final long numUVLayers = reader.readUInt32();
		this.uvSets = new float[(int) numUVLayers][];
		for (int i = 0; i < numUVLayers; i++) {
			reader.readInt32(); // skip UVBS
			this.uvSets[i] = reader.readFloat32Array(reader.readInt32() * 2);
		}
	}

	@Override
	public void writeMdx(final BinaryWriter writer, final int version) {
		writer.writeUInt32(getByteLength(version));
		writer.writeTag(VRTX.getValue());
		writer.writeUInt32(this.vertices.length / 3);
		writer.writeFloat32Array(this.vertices);
		writer.writeTag(NRMS.getValue());
		writer.writeUInt32(this.normals.length / 3);
		writer.writeFloat32Array(this.normals);
		writer.writeTag(PTYP.getValue());
		writer.writeUInt32(this.faceTypeGroups.length);
		writer.writeUInt32Array(this.faceTypeGroups);
		writer.writeTag(PCNT.getValue());
		writer.writeUInt32(this.faceGroups.length);
		writer.writeUInt32Array(this.faceGroups);
		writer.writeTag(PVTX.getValue());
		writer.writeUInt32(this.faces.length);
		writer.writeUInt16Array(this.faces);
		writer.writeTag(GNDX.getValue());
		writer.writeUInt32(this.vertexGroups.length);
		writer.writeUInt8Array(this.vertexGroups);
		writer.writeTag(MTGC.getValue());
		writer.writeUInt32(this.matrixGroups.length);
		writer.writeUInt32Array(this.matrixGroups);
		writer.writeTag(MATS.getValue());
		writer.writeUInt32(this.matrixIndices.length);
		writer.writeUInt32Array(this.matrixIndices);
		writer.writeUInt32(this.materialId);
		writer.writeUInt32(this.selectionGroup);
		writer.writeUInt32(this.selectionFlags);

		if (version > 800) {
			writer.writeInt32(this.lod);
			writer.writeWithNulls(this.lodName, 80);
		}

		this.extent.writeMdx(writer);
		writer.writeUInt32(this.sequenceExtents.size());

		for (final MdlxExtent sequenceExtent : this.sequenceExtents) {
			sequenceExtent.writeMdx(writer);
		}

		if (version > 800) {
			if (this.tangents != null) {
				writer.writeTag(TANG.getValue());
				writer.writeUInt32(this.tangents.length / 4);
				writer.writeFloat32Array(this.tangents);
			}

			if (this.skin != null) {
				writer.writeTag(SKIN.getValue());
				writer.writeUInt32(this.skin.length);
				writer.writeUInt8Array(this.skin);
			}
		}

		writer.writeTag(UVAS.getValue());
		writer.writeUInt32(this.uvSets.length);

		for (final float[] uvSet : this.uvSets) {
			writer.writeTag(UVBS.getValue());
			writer.writeUInt32(uvSet.length / 2);
			writer.writeFloat32Array(uvSet);
		}
	}

	@Override
	public void readMdl(final MdlTokenInputStream stream, final int version) {
		this.uvSets = new float[0][];

		for (final String token : stream.readBlock()) {
			// For now hardcoded for triangles, until I see a model with something
			// different.
			switch (token) {
			case MdlUtils.TOKEN_VERTICES:
				this.vertices = stream.readVectorArray(new float[stream.readInt() * 3], 3);
				break;
			case MdlUtils.TOKEN_NORMALS:
				this.normals = stream.readVectorArray(new float[stream.readInt() * 3], 3);
				break;
			case MdlUtils.TOKEN_TVERTICES: {
				this.uvSets = Arrays.copyOf(this.uvSets, this.uvSets.length + 1);
				this.uvSets[this.uvSets.length - 1] = stream.readVectorArray(new float[stream.readInt() * 2], 2);
			}
				break;
			case MdlUtils.TOKEN_VERTEX_GROUP: {
				// Vertex groups are stored in a block with no count, can't allocate the buffer
				// yet.
				final List<Short> vertexGroups = new ArrayList<>();
				for (final String vertexGroup : stream.readBlock()) {
					vertexGroups.add(Short.valueOf(vertexGroup));
				}

				this.vertexGroups = new short[vertexGroups.size()];
				int i = 0;
				for (final Short vertexGroup : vertexGroups) {
					this.vertexGroups[i++] = vertexGroup;
				}
			}
				break;
			case "Tangents": {
				final int tansCount = (int) stream.readUInt32();
				this.tangents = new float[tansCount * 4];
				stream.readVectorArray(this.tangents, 4);
			}
				break;
			case "SkinWeights": {
				final int skinCount = (int) stream.readUInt32();
				this.skin = new short[skinCount * 8];
				stream.readUInt8Array(this.skin);
			}
				break;
			case MdlUtils.TOKEN_FACES: {
				this.faceTypeGroups = new long[] { 4L };
				stream.readInt(); // number of groups
				final int count = stream.readInt();
				stream.read(); // {
				stream.read(); // Triangles
				stream.read(); // {
				this.faces = stream.readUInt16Array(new int[count]);
				this.faceGroups = new long[] { count };
				stream.read(); // }
				stream.read(); // }
			}
				break;
			case MdlUtils.TOKEN_GROUPS: {
				final List<Integer> indices = new ArrayList<>();
				final List<Integer> groups = new ArrayList<>();

				stream.readInt(); // matrices count
				stream.readInt(); // total indices

				// eslint-disable-next-line no-unused-vars
				for (final String matrix : stream.readBlock()) {
					int size = 0;

					for (final String index : stream.readBlock()) {
						indices.add(Integer.valueOf(index));
						size += 1;
					}
					groups.add(size);
				}

				this.matrixIndices = new long[indices.size()];
				int i = 0;
				for (final Integer index : indices) {
					this.matrixIndices[i++] = index;
				}
				this.matrixGroups = new long[groups.size()];
				i = 0;
				for (final Integer group : groups) {
					this.matrixGroups[i++] = group;
				}
			}
				break;
			case MdlUtils.TOKEN_MINIMUM_EXTENT:
				stream.readFloatArray(this.extent.min);
				break;
			case MdlUtils.TOKEN_MAXIMUM_EXTENT:
				stream.readFloatArray(this.extent.max);
				break;
			case MdlUtils.TOKEN_BOUNDSRADIUS:
				this.extent.boundsRadius = stream.readFloat();
				break;
			case MdlUtils.TOKEN_ANIM: {
				final MdlxExtent extent = new MdlxExtent();
				for (final String subToken : stream.readBlock()) {
					switch (subToken) {
					case MdlUtils.TOKEN_MINIMUM_EXTENT:
						stream.readFloatArray(extent.min);
						break;
					case MdlUtils.TOKEN_MAXIMUM_EXTENT:
						stream.readFloatArray(extent.max);
						break;
					case MdlUtils.TOKEN_BOUNDSRADIUS:
						extent.boundsRadius = stream.readFloat();
						break;
					}
				}
				this.sequenceExtents.add(extent);
			}
				break;
			case MdlUtils.TOKEN_MATERIAL_ID:
				this.materialId = stream.readInt();
				break;
			case MdlUtils.TOKEN_SELECTION_GROUP:
				this.selectionGroup = stream.readInt();
				break;
			case MdlUtils.TOKEN_UNSELECTABLE:
				this.selectionFlags = 4;
				break;
			case "LevelOfDetail":
				this.lod = stream.readInt();
				break;
			case "Name":
				this.lodName = stream.read();
				break;
			default:
				throw new RuntimeException("Unknown token in Geoset: " + token);
			}
		}
	}

	@Override
	public void writeMdl(final MdlTokenOutputStream stream, final int version) {
		stream.startBlock(MdlUtils.TOKEN_GEOSET);

		stream.writeVectorArray(MdlUtils.TOKEN_VERTICES, this.vertices, 3);
		stream.writeVectorArray(MdlUtils.TOKEN_NORMALS, this.normals, 3);

		for (final float[] uvSet : this.uvSets) {
			stream.writeVectorArray(MdlUtils.TOKEN_TVERTICES, uvSet, 2);
		}

		if (version <= 800) {
			stream.startBlock(MdlUtils.TOKEN_VERTEX_GROUP);
			for (final short vertexGroup : this.vertexGroups) {
				stream.writeLine(vertexGroup + ",");
			}
			stream.endBlock();
		}

		if (version > 800) {

			stream.startBlock(MdlUtils.TOKEN_VERTEX_GROUP);
			if (this.skin == null) {
				for (final short vertexGroup : this.vertexGroups) {
					stream.writeLine(vertexGroup + ",");
				}
			}
			stream.endBlock();

			if (this.tangents != null) {
				stream.startBlock("Tangents", this.tangents.length / 4);

				for (int i = 0, l = this.tangents.length; i < l; i += 4) {
					stream.writeFloatArray(Arrays.copyOfRange(this.tangents, i, i + 4));
				}

				stream.endBlock();
			}

			if (this.skin != null) {
				stream.startBlock("SkinWeights", this.skin.length / 8);

				for (int i = 0, l = this.skin.length; i < l; i += 8) {
					stream.writeShortArrayRaw(Arrays.copyOfRange(this.skin, i, i + 8));
				}

				stream.endBlock();
			}
		}

		// For now hardcoded for triangles, until I see a model with something
		// different.
		stream.startBlock(MdlUtils.TOKEN_FACES, 1, this.faces.length);
		stream.startBlock(MdlUtils.TOKEN_TRIANGLES);
		final StringBuilder facesBuffer = new StringBuilder();
		for (final int faceValue : this.faces) {
			if (facesBuffer.length() > 0) {
				facesBuffer.append(", ");
			}
			facesBuffer.append(faceValue);
		}
		stream.writeLine("{ " + facesBuffer.toString() + " },");
		stream.endBlock();
		stream.endBlock();

		stream.startBlock(MdlUtils.TOKEN_GROUPS, this.matrixGroups.length, this.matrixIndices.length);
		int index = 0;
		for (final long groupSize : this.matrixGroups) {
			stream.writeLongSubArrayAttrib(MdlUtils.TOKEN_MATRICES, this.matrixIndices, index,
					(int) (index + groupSize));
			index += groupSize;
		}
		stream.endBlock();

		this.extent.writeMdl(stream);

		for (final MdlxExtent sequenceExtent : this.sequenceExtents) {
			stream.startBlock(MdlUtils.TOKEN_ANIM);
			sequenceExtent.writeMdl(stream);
			stream.endBlock();
		}

		stream.writeAttribUInt32("MaterialID", this.materialId);
		stream.writeAttribUInt32("SelectionGroup", this.selectionGroup);
		if (this.selectionFlags == 4) {
			stream.writeFlag("Unselectable");
		}

		if (version > 800) {
			stream.writeAttrib("LevelOfDetail", this.lod);

			if (this.lodName.length() > 0) {
				stream.writeStringAttrib("Name", this.lodName);
			}
		}

		stream.endBlock();
	}

	@Override
	public long getByteLength(final int version) {
		long size = 120 + (this.vertices.length * 4) + (this.normals.length * 4) + (this.faceTypeGroups.length * 4)
				+ (this.faceGroups.length * 4) + (this.faces.length * 2) + this.vertexGroups.length
				+ (this.matrixGroups.length * 4) + (this.matrixIndices.length * 4) + (this.sequenceExtents.size() * 28);
		for (final float[] uvSet : this.uvSets) {
			size += 8 + (uvSet.length * 4);
		}

		if (version > 800) {
			size += 84;

			if (this.tangents != null) {
				size += 8 + (this.tangents.length * 4);
			}

			if (this.skin != null) {
				size += 8 + this.skin.length;
			}
		}

		return size;
	}

	public float[] getVertices() {
		return this.vertices;
	}

	public float[] getNormals() {
		return this.normals;
	}

	public long[] getFaceTypeGroups() {
		return this.faceTypeGroups;
	}

	public long[] getFaceGroups() {
		return this.faceGroups;
	}

	public int[] getFaces() {
		return this.faces;
	}

	public short[] getVertexGroups() {
		return this.vertexGroups;
	}

	public long[] getMatrixGroups() {
		return this.matrixGroups;
	}

	public long[] getMatrixIndices() {
		return this.matrixIndices;
	}

	public long getMaterialId() {
		return this.materialId;
	}

	public long getSelectionGroup() {
		return this.selectionGroup;
	}

	public long getSelectionFlags() {
		return this.selectionFlags;
	}

	public int getLod() {
		return this.lod;
	}

	public String getLodName() {
		return this.lodName;
	}

	public MdlxExtent getExtent() {
		return this.extent;
	}

	public List<MdlxExtent> getSequenceExtents() {
		return this.sequenceExtents;
	}

	public float[] getTangents() {
		return this.tangents;
	}

	public short[] getSkin() {
		return this.skin;
	}

	public float[][] getUvSets() {
		return this.uvSets;
	}

	public void setVertices(final float[] vertices) {
		this.vertices = vertices;
	}

	public void setNormals(final float[] normals) {
		this.normals = normals;
	}

	public void setFaceTypeGroups(final long[] faceTypeGroups) {
		this.faceTypeGroups = faceTypeGroups;
	}

	public void setFaceGroups(final long[] faceGroups) {
		this.faceGroups = faceGroups;
	}

	public void setFaces(final int[] faces) {
		this.faces = faces;
	}

	public void setVertexGroups(final short[] vertexGroups) {
		this.vertexGroups = vertexGroups;
	}

	public void setMatrixGroups(final long[] matrixGroups) {
		this.matrixGroups = matrixGroups;
	}

	public void setMatrixIndices(final long[] matrixIndices) {
		this.matrixIndices = matrixIndices;
	}

	public void setMaterialId(final long materialId) {
		this.materialId = materialId;
	}

	public void setSelectionGroup(final long selectionGroup) {
		this.selectionGroup = selectionGroup;
	}

	public void setSelectionFlags(final long selectionFlags) {
		this.selectionFlags = selectionFlags;
	}

	public void setLod(final int lod) {
		this.lod = lod;
	}

	public void setLodName(final String lodName) {
		this.lodName = lodName;
	}

	public void setExtent(final MdlxExtent extent) {
		this.extent = extent;
	}

	public void setSequenceExtents(final List<MdlxExtent> sequenceExtents) {
		this.sequenceExtents = sequenceExtents;
	}

	public void setTangents(final float[] tangents) {
		this.tangents = tangents;
	}

	public void setSkin(final short[] skin) {
		this.skin = skin;
	}

	public void setUvSets(final float[][] uvSets) {
		this.uvSets = uvSets;
	}
}
