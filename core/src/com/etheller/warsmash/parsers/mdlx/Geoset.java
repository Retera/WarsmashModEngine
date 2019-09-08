package com.etheller.warsmash.parsers.mdlx;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.etheller.warsmash.util.MdlUtils;
import com.etheller.warsmash.util.ParseUtils;
import com.etheller.warsmash.util.War3ID;
import com.google.common.io.LittleEndianDataInputStream;
import com.google.common.io.LittleEndianDataOutputStream;

public class Geoset implements MdlxBlock, Chunk {
	private static final War3ID VRTX = War3ID.fromString("VRTX");
	private static final War3ID NRMS = War3ID.fromString("NRMS");
	private static final War3ID PTYP = War3ID.fromString("PTYP");
	private static final War3ID PCNT = War3ID.fromString("PCNT");
	private static final War3ID PVTX = War3ID.fromString("PVTX");
	private static final War3ID GNDX = War3ID.fromString("GNDX");
	private static final War3ID MTGC = War3ID.fromString("MTGC");
	private static final War3ID MATS = War3ID.fromString("MATS");
	private static final War3ID UVAS = War3ID.fromString("UVAS");
	private static final War3ID UVBS = War3ID.fromString("UVBS");
	private float[] vertices;
	private float[] normals;
	private long[] faceTypeGroups; // unsigned int[]
	private long[] faceGroups; // unsigned int[]
	private int[] faces; // unsigned short[]
	private short[] vertexGroups; // unsigned byte[]
	private long[] matrixGroups; // unsigned int[]
	private long[] matrixIndices; // unsigned int[]
	private long materialId = 0;
	private long selectionGroup = 0;
	private long selectionFlags = 0;
	private final Extent extent = new Extent();
	private Extent[] sequenceExtents;
	private float[][] uvSets;

	@Override
	public void readMdx(final LittleEndianDataInputStream stream) throws IOException {
		final long mySize = ParseUtils.readUInt32(stream);
		final int vrtx = stream.readInt(); // skip VRTX
		this.vertices = ParseUtils.readFloatArray(stream, (int) (ParseUtils.readUInt32(stream) * 3));
		final int nrms = stream.readInt(); // skip NRMS
		this.normals = ParseUtils.readFloatArray(stream, (int) (ParseUtils.readUInt32(stream) * 3));
		final int ptyp = stream.readInt(); // skip PTYP
		this.faceTypeGroups = ParseUtils.readUInt32Array(stream, (int) ParseUtils.readUInt32(stream));
		stream.readInt(); // skip PCNT
		this.faceGroups = ParseUtils.readUInt32Array(stream, (int) ParseUtils.readUInt32(stream));
		stream.readInt(); // skip PVTX
		this.faces = ParseUtils.readUInt16Array(stream, (int) ParseUtils.readUInt32(stream));
		stream.readInt(); // skip GNDX
		this.vertexGroups = ParseUtils.readUInt8Array(stream, (int) ParseUtils.readUInt32(stream));
		stream.readInt(); // skip MTGC
		this.matrixGroups = ParseUtils.readUInt32Array(stream, (int) ParseUtils.readUInt32(stream));
		stream.readInt(); // skip MATS
		this.matrixIndices = ParseUtils.readUInt32Array(stream, (int) ParseUtils.readUInt32(stream));
		this.materialId = ParseUtils.readUInt32(stream);
		this.selectionGroup = ParseUtils.readUInt32(stream);
		this.selectionFlags = ParseUtils.readUInt32(stream);
		this.extent.readMdx(stream);

		final long numExtents = ParseUtils.readUInt32(stream);
		this.sequenceExtents = new Extent[(int) numExtents];
		for (int i = 0; i < numExtents; i++) {
			final Extent extent = new Extent();
			extent.readMdx(stream);
			this.sequenceExtents[i] = extent;
		}

		stream.readInt(); // skip UVAS

		final long numUVLayers = ParseUtils.readUInt32(stream);
		this.uvSets = new float[(int) numUVLayers][];
		for (int i = 0; i < numUVLayers; i++) {
			stream.readInt(); // skip UVBS
			this.uvSets[i] = ParseUtils.readFloatArray(stream, (int) (ParseUtils.readUInt32(stream) * 2));
		}
	}

	@Override
	public void writeMdx(final LittleEndianDataOutputStream stream) throws IOException {
		ParseUtils.writeUInt32(stream, this.getByteLength());
		ParseUtils.writeWar3ID(stream, VRTX);
		ParseUtils.writeUInt32(stream, this.vertices.length / 3);
		ParseUtils.writeFloatArray(stream, this.vertices);
		ParseUtils.writeWar3ID(stream, NRMS);
		ParseUtils.writeUInt32(stream, this.normals.length / 3);
		ParseUtils.writeFloatArray(stream, this.normals);
		ParseUtils.writeWar3ID(stream, PTYP);
		ParseUtils.writeUInt32(stream, this.faceTypeGroups.length);
		ParseUtils.writeUInt32Array(stream, this.faceTypeGroups);
		ParseUtils.writeWar3ID(stream, PCNT);
		ParseUtils.writeUInt32(stream, this.faceGroups.length);
		ParseUtils.writeUInt32Array(stream, this.faceGroups);
		ParseUtils.writeWar3ID(stream, PVTX);
		ParseUtils.writeUInt32(stream, this.faces.length);
		ParseUtils.writeUInt16Array(stream, this.faces);
		ParseUtils.writeWar3ID(stream, GNDX);
		ParseUtils.writeUInt32(stream, this.vertexGroups.length);
		ParseUtils.writeUInt8Array(stream, this.vertexGroups);
		ParseUtils.writeWar3ID(stream, MTGC);
		ParseUtils.writeUInt32(stream, this.matrixGroups.length);
		ParseUtils.writeUInt32Array(stream, this.matrixGroups);
		ParseUtils.writeWar3ID(stream, MATS);
		ParseUtils.writeUInt32(stream, this.matrixIndices.length);
		ParseUtils.writeUInt32Array(stream, this.matrixIndices);
		ParseUtils.writeUInt32(stream, this.materialId);
		ParseUtils.writeUInt32(stream, this.selectionGroup);
		ParseUtils.writeUInt32(stream, this.selectionFlags);
		this.extent.writeMdx(stream);
		ParseUtils.writeUInt32(stream, this.sequenceExtents.length);

		for (final Extent sequenceExtent : this.sequenceExtents) {
			sequenceExtent.writeMdx(stream);
		}

		ParseUtils.writeWar3ID(stream, UVAS);
		ParseUtils.writeUInt32(stream, this.uvSets.length);
		for (final float[] uvSet : this.uvSets) {
			ParseUtils.writeWar3ID(stream, UVBS);
			ParseUtils.writeUInt32(stream, uvSet.length / 2);
			ParseUtils.writeFloatArray(stream, uvSet);
		}
	}

	@Override
	public void readMdl(final MdlTokenInputStream stream) {
		this.uvSets = new float[0][];
		final List<Extent> sequenceExtents = new ArrayList<>();
		for (final String token : stream.readBlock()) {
			switch (token) {
			case MdlUtils.TOKEN_VERTICES:
				this.vertices = stream.readVectorArray(new float[stream.readInt() * 3], 3);
				break;
			case MdlUtils.TOKEN_NORMALS:
				this.normals = stream.readVectorArray(new float[stream.readInt() * 3], 3);
				break;
			case MdlUtils.TOKEN_TVERTICES:
				this.uvSets = Arrays.copyOf(this.uvSets, this.uvSets.length + 1);
				this.uvSets[this.uvSets.length - 1] = stream.readVectorArray(new float[stream.readInt() * 2], 2);
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
					this.vertexGroups[i++] = vertexGroup.shortValue();
				}
			}
				break;
			case MdlUtils.TOKEN_FACES:
				// For now hardcoded for triangles, until I see a model with something
				// different.
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
					this.matrixIndices[i++] = index.intValue();
				}
				this.matrixGroups = new long[groups.size()];
				i = 0;
				for (final Integer group : groups) {
					this.matrixGroups[i++] = group.intValue();
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
			case MdlUtils.TOKEN_ANIM:
				final Extent extent = new Extent();

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

				sequenceExtents.add(extent);
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
			default:
				throw new RuntimeException("Unknown token in Geoset: " + token);
			}
		}
		this.sequenceExtents = sequenceExtents.toArray(new Extent[sequenceExtents.size()]);
	}

	@Override
	public void writeMdl(final MdlTokenOutputStream stream) {
		stream.startBlock(MdlUtils.TOKEN_GEOSET);

		stream.writeVectorArray(MdlUtils.TOKEN_VERTICES, this.vertices, 3);
		stream.writeVectorArray(MdlUtils.TOKEN_NORMALS, this.normals, 3);

		for (final float[] uvSet : this.uvSets) {
			stream.writeVectorArray(MdlUtils.TOKEN_TVERTICES, uvSet, 2);
		}

		stream.startBlock(MdlUtils.TOKEN_VERTEX_GROUP);
		for (int i = 0; i < this.vertexGroups.length; i++) {
			stream.writeLine(this.vertexGroups[i] + ",");
		}
		stream.endBlock();

		// For now hardcoded for triangles, until I see a model with something
		// different.
		stream.startBlock(MdlUtils.TOKEN_FACES, 1, this.faces.length);
		stream.startBlock(MdlUtils.TOKEN_TRIANGLES);
		final StringBuffer facesBuffer = new StringBuffer();
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

		for (final Extent sequenceExtent : this.sequenceExtents) {
			stream.startBlock(MdlUtils.TOKEN_ANIM);
			sequenceExtent.writeMdl(stream);
			stream.endBlock();
		}

		stream.writeAttribUInt32("MaterialID", this.materialId);
		stream.writeAttribUInt32("SelectionGroup", this.selectionGroup);
		if (this.selectionFlags == 4) {
			stream.writeFlag("Unselectable");
		}
		stream.endBlock();
	}

	@Override
	public long getByteLength() {
		long size = 120 + (this.vertices.length * 4) + (this.normals.length * 4) + (this.faceTypeGroups.length * 4)
				+ (this.faceGroups.length * 4) + (this.faces.length * 2) + this.vertexGroups.length
				+ (this.matrixGroups.length * 4) + (this.matrixIndices.length * 4) + (this.sequenceExtents.length * 28);
		for (final float[] uvSet : this.uvSets) {
			size += 8 + (uvSet.length * 4);
		}
		return size;
	}
}
